package com.pirates.game;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	static SpriteBatch batch;
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
	static int MAPDEGREE = 10;
	static int MAPSIZE = (int) Math.pow(2, MAPDEGREE);
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
	static int lvl;
	static HashSet<Projectile> projRemovalSet;
	static HashSet<Ship> shipRemovalSet;
	static HashSet<Debries> debriesRemovalSet;
	static PerlinNoiseGen noiseGen;
	static Color seaTintColor = new Color();
	private static Texture mapTexture;
	static int mapSpriteSize = 250;
	public static Ui UI;
	
	static Vector2 UI_POS1 = new Vector2(20,0.5f);
	static Vector2 UI_POS2 = new Vector2(20,-.5f);
	
	
	//Ship factories
	//called like basicShipGen.genShip(level);
	static ShipGen basicShipGen = new BasicShipGen();
	static ShipGen bossShipGen = new BossShipGen();

	//static BodyDef bodyDef = new BodyDef();
	//static FixtureDef fixtureDef = new FixtureDef();
	public LovePirates(){
		super();
		
	}
	
	static void genWorld(int level) {

		lvl = level;
		shipPrefCount = new PerformanceCounter("ship");
		renderPrefCount = new PerformanceCounter("render");
		physicsPrefCount = new PerformanceCounter("physics");
		totalPrefCount = new PerformanceCounter("total");
		debugObjects = new HashSet<Vector2>();
		debugShapeRenderer = new ShapeRenderer();
		
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
		
		map = noiseGen.getFullPerlinArray(MAPDEGREE);
		mapTexture = MyUtils.visuliseArray(map, false);
		//save map
		MyUtils.visuliseArray(map,false);
		for (int i = 0; i<70; i++) {
			debries.add(TresureChestGen.genChest(map));
		}
	}
	public static void nextWorld() {
		genWorld(lvl+1);
		ships.add(playerShip);
		playerShip.bodyReInstantiate(world);
		while (map[(int) playerShip.getPos().x][(int) playerShip.getPos().y]>SEALEVEL) {
			playerShip.setPos((int) playerShip.getPos().x+10, (int) playerShip.getPos().y);
		}
		
	}
	
	@Override
	public void create() {
		Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		font = new BitmapFont();
		font.setScale(1/32f);
		font.setUseIntegerPositions(false);
		rand = new Random();
		
		
		camera = new OrthographicCamera(width/TILESIZE*.5f,height/TILESIZE*.5f);
		inputProcessor = HandleUserInput.init();
		Gdx.input.setInputProcessor(inputProcessor);
		//box2d
		Box2D.init();		
		Ui.init();
		
		//map gen init
		noiseGen =  PerlinNoiseGen.init();
		
		//list of blitable images
		textureRegions = new TextureRegion[99];
		//this next line is bad, should be part of the spritesheet and a texture region
		Texture shiptex = new Texture("ship.bmp");
		Texture cannoballtext = new Texture("cannonball.png");
		Texture debristext = new Texture("debris.png");
		Texture lootCratetext = new Texture("crate.png");
		Texture whiteCircle = new Texture("whileCircle.png");
		Texture buckshotSprite = new Texture("buckshot.png");

		debug = new Texture("debug.bmp");
		textureRegions[1] = new TextureRegion(cannoballtext, cannoballtext.getWidth(), cannoballtext.getHeight());
		textureRegions[0] = new TextureRegion(shiptex, shiptex.getWidth(), shiptex.getHeight()); //this is wrong
		textureRegions[3] = new TextureRegion(debristext, debristext.getWidth(), debristext.getHeight());
		textureRegions[4] = new TextureRegion(lootCratetext, lootCratetext.getWidth(), lootCratetext.getHeight());
		textureRegions[5] = new TextureRegion(whiteCircle, whiteCircle.getWidth(), whiteCircle.getHeight());
		textureRegions[6] = new TextureRegion(buckshotSprite, buckshotSprite.getWidth(), buckshotSprite.getHeight());

		
		tiles = new Texture("tiles2.png");
		tiles.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		land =  new TextureRegion(tiles,0,0,TILESIZE,TILESIZE);
		sea =  new TextureRegion(tiles,TILESIZE,TILESIZE,TILESIZE,TILESIZE);
		grass =  new TextureRegion(tiles,TILESIZE,0,TILESIZE,TILESIZE);
		ygrass =  new TextureRegion(tiles,2*TILESIZE,0,TILESIZE,TILESIZE);
		bgrass =  new TextureRegion(tiles,2*TILESIZE,TILESIZE,TILESIZE,TILESIZE);
		dsea =  new TextureRegion(tiles,TILESIZE,2*TILESIZE,TILESIZE,TILESIZE);
		lsea =  new TextureRegion(tiles,2*TILESIZE,2*TILESIZE,TILESIZE,TILESIZE);
		sand =  new TextureRegion(tiles,0,TILESIZE,TILESIZE,TILESIZE);
		
		
		
		genWorld(1);
		//ShipGenerator.genShip(x, y, turnRate, drag, power, length, width, cannons,buckshot, slots, hp, maxhp, boss, gunnars, maxgunners, sailors, maxsailors)
		playerShip = ShipGenerator.genShip(100, 400, 1, 1, 4, 2.5f, .75f, 4,8, 10, 5f, 20,false,10,40,12,50);
		playerShip.setControler(new PlayerController());
		while (map[(int) playerShip.getPos().x][(int) playerShip.getPos().y]>SEALEVEL) {
			playerShip.setPos((int) playerShip.getPos().x+10, (int) playerShip.getPos().y);
		}
		ships.add(playerShip);
		
		batch = new SpriteBatch();
		batch.enableBlending();
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
				} else {
					float height = (float) map[i][j];
					float darkness = (float) Math.max(Math.min(1.5*Math.pow(height,2), 1),.2);
					seaTintColor.r = darkness/2;
					seaTintColor.g = darkness/2;
					seaTintColor.b = darkness;
					seaTintColor.a = 1;
					batch.setColor(seaTintColor);
					batch.draw(lsea, i, j, 1, 1);
					batch.setColor(Color.WHITE.tmp());
				}
				/*
				*} else if (map[i][j] > SEALEVEL-.06){
				*	batch.draw(lsea, i, j, 1, 1);
				*} else if (map[i][j] > SEALEVEL-.12){
				*	batch.draw(sea, i, j, 1, 1);
				*} else {
				*	batch.draw(dsea, i, j, 1, 1);
				*}
				*/
			}
		}
		renderPrefCount.stop();
		shipPrefCount.start();
		TextureRegion t;
		
		

		for (Debries debrie : debries) {
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
		
		for (Debries debrie : debries) {
			if (debrie.alive == false) {
				debriesRemovalSet.add(debrie);
				debrie.delete();
			}
		}
		debries.removeAll(debriesRemovalSet);
		
		while (ships.size()<50) {
			Ship aiShip;
			if (rand.nextFloat() < .9) {
				aiShip= basicShipGen.genShip(lvl);
			} else {
				aiShip = bossShipGen.genShip(lvl);
			}
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
			Vector2 hpDrawPos = ship.getPos().add(ship.getSize()[1],ship.getSize()[1]);
			MyUtils.DrawText(((Integer) Math.round(ship.getHp())).toString(), false, hpDrawPos, 1);
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
		
		MyUtils.renderText(batch);
		
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
		
		
		//Draw UI/minimap
		//int xpos = (int) Math.max(0, Math.min(playerPos.x-mapSpriteSize/2,MAPSIZE-mapSpriteSize/2-1));
		//int ypos = (int) Math.max(0, Math.min(playerPos.y-mapSpriteSize/2,MAPSIZE-mapSpriteSize/2-1));
		int xpos = (int) (playerPos.x-mapSpriteSize/2);
		int ypos = (int) (playerPos.y-mapSpriteSize/2);
		batch.draw(mapTexture,
                playerPos.x-mapSpriteSize/TILESIZE+width/(TILESIZE*4f),
                playerPos.y-mapSpriteSize/TILESIZE+height/(TILESIZE*4f),
                0,0,
                mapSpriteSize,
                mapSpriteSize,
                1f/(TILESIZE),
                1f/(TILESIZE),
                0,
                xpos,
                ypos,
                mapSpriteSize,
                mapSpriteSize,
                false, true);
		/* batch.draw(debug,
				playerPos.x-mapSpriteSize/(2*TILESIZE) + width/(TILESIZE * 4f),
				playerPos.y-mapSpriteSize/(2*TILESIZE) + height/(TILESIZE * 4f),
				.5f,.5f);
				*/
		for (Ship ship : ships){
			x = ship.getPos().x;
			y = ship.getPos().y;
			if ((x>playerPos.x - mapSpriteSize/2 && x<playerPos.x + mapSpriteSize/2) &&
			    (y>playerPos.y - mapSpriteSize/2 && y<playerPos.y + mapSpriteSize/2)) {
				batch.draw(debug,
					x/TILESIZE+playerPos.x -playerPos.x/TILESIZE + width/(TILESIZE*4f)-mapSpriteSize/(2*TILESIZE),
					y/TILESIZE+playerPos.y -playerPos.y/TILESIZE + height/(TILESIZE*4f)-mapSpriteSize/(2*TILESIZE),
					.25f, .25f);
			}
		}
		String ui;
		ui = String.format("You have %d repair supplies %n" +
						   "%d gold", Math.round(playerShip.repairSupplies),playerShip.gold);
		
		MyUtils.DrawText(ui, true, UI_POS1,1);
		ui = String.format("You have %d sailors and " +
				   "%d cannonears", playerShip.sailors,playerShip.gunners);
		
		MyUtils.DrawText(ui, true, UI_POS2,1);
		
		Ui.drawShipIcon(batch,playerShip,-26,-13);
		
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
