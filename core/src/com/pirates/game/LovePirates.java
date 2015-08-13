package com.pirates.game;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.PerformanceCounter;
public class LovePirates extends ApplicationAdapter {
	SpriteBatch batch;
	static double[][] map;
	static HandleUserInput inputProcessor;
	static TextureRegion[] textureRegions;
	static Texture tiles;
	static HashSet<Projectile> projectiles;
	static HashSet<Ship> ships;
	static HashSet<Debries> debries;
	static OrthographicCamera camera;
	static Ship playerShip;
	static int width;
	static int height;
	static int TILESIZE = 16;
	static float SEALEVEL = .73f;
	static boolean DEBUGPRINTOUT = false;
	static TextureRegion land;
	static TextureRegion sea;
	static TextureRegion grass;
	static TextureRegion ygrass;
	static TextureRegion bgrass;
	static TextureRegion sand;
	static TextureRegion dsea;
	static TextureRegion lsea;
	static Texture debug;
	static HashSet<Vector2> debugObjects;
	static int MAPSIZE = 10;
	static int COLLIDERPOOLSIZE = 6000;//maybe bigger
	static float dt;
	static World world;
	static ColliderPool colliderPool;
	static Box2DDebugRenderer debugRenderer;
	static Random rand;
	//these are used to keep track of preformance.
	static PerformanceCounter totalPrefCount;
	static PerformanceCounter renderPrefCount;
	static PerformanceCounter shipPrefCount;
	static PerformanceCounter physicsPrefCount;
	//some collision filters:
	final static short LAND_MASK = 0x0001; 
	final static short SHIP_MASK = 0x0002;
	final static short PROJ_MASK = 0x0004;
	static ShapeRenderer debugShapeRenderer;
	static BitmapFont font;
	HashSet<Projectile> projRemovalSet;
	HashSet<Ship> shipRemovalSet;
	HashSet<Debries> debriesRemovalSet;
	//static BodyDef bodyDef = new BodyDef();
	//static FixtureDef fixtureDef = new FixtureDef();
	public LovePirates(int w, int h){
		super();
		width = w;
		height = h;
	}
	@Override
	public void create() {
		Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);

		font = new BitmapFont();
		font.setScale(1/16f);
		font.setUseIntegerPositions(false);
		rand = new Random();
		shipPrefCount = new PerformanceCounter("ship");
		renderPrefCount = new PerformanceCounter("render");
		physicsPrefCount = new PerformanceCounter("physics");
		totalPrefCount = new PerformanceCounter("total");
		debugObjects = new HashSet<Vector2>();
		debugShapeRenderer = new ShapeRenderer();
		
		camera = new OrthographicCamera(width/TILESIZE*.5f,height/TILESIZE*.5f);
		inputProcessor = HandleUserInput.init();
		Gdx.input.setInputProcessor(inputProcessor);
		//box2d
		Box2D.init();		
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new MyContactListener());
		//this number is performance sensitive and can create subtle collision bugs, beware
		//large number == fewer bugs but worse performance
		colliderPool = new ColliderPool(COLLIDERPOOLSIZE);
		projRemovalSet = new HashSet<Projectile>();
		shipRemovalSet = new HashSet<Ship>();
		debriesRemovalSet = new HashSet<Debries>();
		//debug renderer for physics rendering
		debugRenderer = new Box2DDebugRenderer();
		
		
		
		//game state stuff
		ships = new HashSet<Ship>();
		projectiles = new HashSet<Projectile>();
		debries = new HashSet<Debries>();
		//sprite batch for efficient bliting
		batch = new SpriteBatch();
		
		//map gen init
		PerlinNoiseGen noiseGen =  PerlinNoiseGen.init();
		
		//list of blitable images
		textureRegions = new TextureRegion[99];
		//this next line is bad, should be part of the spritesheet and a texture region
		Texture shiptex = new Texture("ship.bmp");
		Texture cannoballtext = new Texture("cannonball.png");
		Texture debristext = new Texture("debris.png");
		Texture lootCratetext = new Texture("crate.png");

		debug = new Texture("debug.bmp");
		textureRegions[1] = new TextureRegion(cannoballtext, cannoballtext.getWidth(), cannoballtext.getHeight());
		textureRegions[0] = new TextureRegion(shiptex, shiptex.getWidth(), shiptex.getHeight()); //this is wrong
		textureRegions[3] = new TextureRegion(debristext, debristext.getWidth(), debristext.getHeight());
		textureRegions[4] = new TextureRegion(lootCratetext, lootCratetext.getWidth(), lootCratetext.getHeight());

		
		tiles = new Texture("tiles.png");
		tiles.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		land =  new TextureRegion(tiles,0,0,TILESIZE,TILESIZE);
		sea =  new TextureRegion(tiles,TILESIZE,TILESIZE,TILESIZE,TILESIZE);
		grass =  new TextureRegion(tiles,TILESIZE,0,TILESIZE,TILESIZE);
		ygrass =  new TextureRegion(tiles,2*TILESIZE,0,TILESIZE,TILESIZE);
		bgrass =  new TextureRegion(tiles,2*TILESIZE,TILESIZE,TILESIZE,TILESIZE);
		dsea =  new TextureRegion(tiles,TILESIZE,2*TILESIZE,TILESIZE,TILESIZE);
		lsea =  new TextureRegion(tiles,2*TILESIZE,2*TILESIZE,TILESIZE,TILESIZE);
		sand =  new TextureRegion(tiles,0,TILESIZE,TILESIZE,TILESIZE);
		
		map = noiseGen.getFullPerlinArray(MAPSIZE);
		
		//save map
		MyUtils.visuliseArray(map,false);
		
		//					  x,y,turnrate,dragcoef,maxpower, length, width, cannons,hp
		playerShip = ShipGenerator.genShip((int) Math.pow(2, MAPSIZE-1),100,1f,1f,5,2,.75f,5,20);
		while (map[(int) playerShip.getPos().x][(int) playerShip.getPos().y]>SEALEVEL) {
			playerShip.setPos((int) playerShip.getPos().x+10, (int) playerShip.getPos().y);
		}
		playerShip.setControler(new PlayerController());
		ships.add(playerShip);
		for (int i = 0; i<100; i++) {
			TresureChestGen.genChest();
		}
		
		/*
		  for (int i = 0; i<20; i++) {
			map = noiseGen.getFullPerlinArray(10);
			MyUtils.visuliseArray(map);
		}
		for (int i = 0; i<5; i++) {
			map = noiseGen.getFullPerlinArray(13);o
			MyUtils.visuliseArray(map);
		}
		*/
		
	}
	

	@Override
	public void render() {
		
		renderPrefCount.tick();
		shipPrefCount.tick();
		physicsPrefCount.tick();
		totalPrefCount.tick();
		
		totalPrefCount.start();
		
		
		Gdx.gl.glClearColor(.1f, .1f, .5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		dt = Gdx.graphics.getDeltaTime();
		camera.position.set(playerShip.getPos().x,playerShip.getPos().y,0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		int index;
		float x;
		float y;
		Vector2 playerPos = playerShip.getPos();
		renderPrefCount.start();

		/*seems that the preformance issues are from the following block of code :-(
		 * where the land is rendered
		 */
		for (int i 		= (int)((playerPos.x)-(width/20)); 	   	i < ((int) (playerPos.x) + (width/20)); i++){
			for (int j 	= (int)((playerPos.y)-(height/20)); 	j < ((int) (playerPos.y) + (height/20)); j++){
				if (i<0 || i>=map.length || j<0 || j>=map[0].length){
					batch.draw(dsea,i,j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.15) {
					batch.draw(land,i,j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.1){
					batch.draw(bgrass, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.065){
					batch.draw(ygrass, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.03){
					batch.draw(grass, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL){
					batch.draw(sand, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL-.06){
					batch.draw(lsea, i, j, 1, 1);
				} else if (map[i][j] > .0){
					batch.draw(sea, i, j, 1, 1);
				}

			}
		}
		renderPrefCount.stop();
		shipPrefCount.start();
		TextureRegion t;
		

		
		for (Debries debrie : debries){
			debrie.tick();
			x = debrie.getPos().x;
			y = debrie.getPos().y;
			index = debrie.getSpriteIndex();
			float[] size = debrie.getSize();
			t = textureRegions[index];
			float debrieRot = (float) (debrie.getRotation()*360/(2*Math.PI));
			batch.draw(t,x-size[0]/2,y-size[1]/2,
					   size[0]/2,size[1]/2,
					   size[0],size[1],
					   1f,1f,debrieRot,true);
		}
		
		for (Debries debrie : debries){
			if (debrie.alive == false) {
				debriesRemovalSet.add(debrie);
				debrie.delete();
			}
		}
		debries.removeAll(debriesRemovalSet);
		
		while (ships.size()<100) {
			Ship aiShip = RandomShipGen.GenRandomShip(1);
			ships.add(aiShip);
		}
		
		for (Ship ship : ships){
			ship.tick();
			x = ship.getPos().x;
			y = ship.getPos().y;
			index = ship.getSpriteIndex();
			float[] size = ship.getSize();
			t = textureRegions[index];
			float shipRotation = (float) (ship.getDir()*360/(2*Math.PI));
			font.draw(batch, ((Float) ship.getHp()).toString(), ship.getPos().x, ship.getPos().y+3,0,3);

			batch.draw(t,x-size[0]/2,y-size[1]/2,
					   size[0]/2,size[1]/2,
					   size[0],size[1],
					   1f,1f,shipRotation,true);
		}
		
		for (Ship ship : ships){
			if (ship.alive == false) {
				shipRemovalSet.add(ship);
				ship.delete();
			}
		}
		ships.removeAll(shipRemovalSet);

		
		
		

		
		
		
		for (Vector2 debugLoc : debugObjects) {
			batch.draw(debug,debugLoc.x, debugLoc.y,.2f,.2f);
		}
		debugObjects.clear();
		for (Projectile proj : projectiles){
			proj.tick();
			x = proj.getPos().x;
			y = proj.getPos().y;
			t = textureRegions[proj.getSpriteIndex()];
			float[] size = proj.getSize();
			batch.draw(t,x,y,
					   t.getRegionWidth()/2, t.getRegionHeight()/2, 
					   size[0],size[1],
					   1f,1f,0,true);
			
		}
		batch.end();
		for (Projectile proj : projectiles){
			if (proj.dead == true) {
				projRemovalSet.add(proj);
				proj.delete();
			}
		}
		
		projectiles.removeAll(projRemovalSet);
		projRemovalSet.clear();
		
		shipPrefCount.stop();
		
		physicsPrefCount.start();
		
		
		world.step(1/60f, 6, 4);
		physicsPrefCount.stop();
		
		//this debug renderer seems to be very heavy
		//debugRenderer.render(world, camera.combined);
		totalPrefCount.stop();
		MyUtils.renderLines();
		if (DEBUGPRINTOUT) {
			if (shipPrefCount.load.latest > .06) {
				System.out.println(shipPrefCount);
	
			}
			if (renderPrefCount.load.latest > .1) {
				System.out.println(renderPrefCount);
	
			}
			if (physicsPrefCount.load.latest > .05) {
				System.out.println(physicsPrefCount);
	
			}
			if (totalPrefCount.time.latest > .00833) {
				float fps = 1/totalPrefCount.time.latest;
				System.out.println("FPS = " + fps);
				System.out.println(totalPrefCount);
	
			}
		}
	}
}
