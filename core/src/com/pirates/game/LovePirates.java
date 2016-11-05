package com.pirates.game;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.viewport.Viewport;
/*
 * getPower() and getTurn() get called by ships to see what they should do
 */
public class LovePirates extends ApplicationAdapter {
	static SpriteBatch batch;
	static double[][] map;
	static TextureRegion[] textureRegions;
	static Texture tiles;
	static HashSet<Projectile> projectiles;
	static HashSet<Ship> ships;
	static HashSet<Debries> debries;
	static OrthographicCamera camera;
	static Viewport viewport;
	static Ship playerShip;
	static int width;
	static int height;
	static int TILESIZE = 16;
	int TILESIZEPLUSS = TILESIZE + 1;
	static float SEALEVEL = .73f;
	static float SANDWIDTH =.015f;
	static float GRASSWIDTH = .05f;
	static float YGRASSWIDTH = .8f;
	static float BGRASSWIDTH = .10f;
	static LandDecoration[][] decorationMap;

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
	static int COLLIDERPOOLSIZE = 15000;//maybe bigger?
	static float dt;
	static World world;
	static ColliderPool colliderPool;
	static Box2DDebugRenderer debugRenderer;
	static Random rand;
	//these are used to keep track of Performance.
	static PerformanceCounter totalPerfCount;
	static PerformanceCounter renderPerfCount;
	static PerformanceCounter shipPerfCount;
	static PerformanceCounter physicsPerfCount;
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
	static Texture mapTexture;
	static int mapSpriteSize = 225;
	public static Ui UI;
	static LootScreen lootScreen;
	static MainMenuOverlay mainMenu;
	private static Skin skin;
	static CStage stage;
	static Ui ui;
	static DecorateLand mapDecorator = new DecorateLand();
	
	static final int numShips = 75;
	final static float cameraScalingFactor = .5f;
	static boolean paused = false;

	//Ship factories
	//called like basicShipGen.genShip(level);
	static ShipGen basicShipGen;
	static ShipGen bossShipGen;
	static Vector2 UI_POS1;
	static Vector2 UI_POS2;
	//static InventoryUi inventoryUi;

	//static BodyDef bodyDef = new BodyDef();
	//static FixtureDef fixtureDef = new FixtureDef();
	public LovePirates(){
		super();
	}
	
	static void genWorld(int level) {

		lvl = level;
		shipPerfCount = new PerformanceCounter("ship");
		renderPerfCount = new PerformanceCounter("render");
		physicsPerfCount = new PerformanceCounter("physics");
		totalPerfCount = new PerformanceCounter("total");
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
		for (int i = 0; i<70; i++) {
			debries.add(TresureChestGen.genChest(map));
		}
		
		decorationMap = mapDecorator.DecorateAMap(map);
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
		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		Gdx.graphics.setDisplayMode(width, height, false);

		font = new BitmapFont();
		font.setScale(1/32f);
		font.setUseIntegerPositions(false);
		UI_POS1 = new Vector2(width/(TILESIZE*4f)-10,0.5f);
		UI_POS2 = new Vector2(width/(TILESIZE*4f)-10,-.5f);
		rand = new Random();
		float vWidth = width/TILESIZE*cameraScalingFactor;
		float vHeight =height/TILESIZE*cameraScalingFactor;
		camera = new OrthographicCamera(vWidth,vHeight);
		//stage handles input and  is used to draw UI
		stage = new CStage(width, height, camera);
		Gdx.input.setInputProcessor(stage);
		//box2d
		Box2D.init();
				//map gen init
		noiseGen =  PerlinNoiseGen.init();

		basicShipGen = new BasicShipGen();
		bossShipGen = new BossShipGen();
		
		//list of blitable images
		textureRegions = new TextureRegion[99];
		//this next line is bad, should be part of the spritesheet and a texture region
		Texture shiptex = new Texture("placeholder ship.png");
		Texture deadShiptex = new Texture("placeholder ship broken.png");
		Texture cannoballtext = new Texture("cannonball.png");
		Texture debristext = new Texture("debris.png");
		Texture lootCratetext = new Texture("crate.png");
		Texture whiteCircle = new Texture("whileCircle.png");
		Texture buckshotSprite = new Texture("buckshot.png");
		Texture treeSprite = new Texture("treesprite.png");
		Texture shrubSprite = new Texture("shrub.png");
		Texture mtreeSprite = new Texture("moutantreesprite.png");
		Texture seaWeedSprite = new Texture("seaweed.png");
		Texture thickSeaWeedSprite = new Texture("thickSeaweed.png");
		Texture thinSeaWeedSprite = new Texture("thinSeaweed.png");
		Texture shipIconSprite = new Texture("shipIcon.png");
		shiptex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		seaWeedSprite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		treeSprite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shrubSprite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		mtreeSprite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		thickSeaWeedSprite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		thinSeaWeedSprite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		debug = new Texture("debug.bmp");
		
		textureRegions[1] = new TextureRegion(cannoballtext, cannoballtext.getWidth(), cannoballtext.getHeight());
		textureRegions[0] = new TextureRegion(shiptex, shiptex.getWidth(), shiptex.getHeight()); //this is wrong, these should all be in a texture region
		textureRegions[3] = new TextureRegion(debristext, debristext.getWidth(), debristext.getHeight());//but baylife
		textureRegions[4] = new TextureRegion(lootCratetext, lootCratetext.getWidth(), lootCratetext.getHeight());
		textureRegions[5] = new TextureRegion(whiteCircle, whiteCircle.getWidth(), whiteCircle.getHeight());
		textureRegions[6] = new TextureRegion(buckshotSprite, buckshotSprite.getWidth(), buckshotSprite.getHeight());
		textureRegions[7] = new TextureRegion(deadShiptex, deadShiptex.getWidth(), deadShiptex.getHeight());
		textureRegions[8] = new TextureRegion(treeSprite, treeSprite.getWidth(), treeSprite.getHeight());
		textureRegions[9] = new TextureRegion(shrubSprite, shrubSprite.getWidth(), shrubSprite.getHeight());
		textureRegions[10] = new TextureRegion(mtreeSprite, mtreeSprite.getWidth(), mtreeSprite.getHeight());
		textureRegions[12] = new TextureRegion(seaWeedSprite, seaWeedSprite.getWidth(), seaWeedSprite.getHeight());
		textureRegions[13] = new TextureRegion(thickSeaWeedSprite, thickSeaWeedSprite.getWidth(), thickSeaWeedSprite.getHeight());
		textureRegions[11] = new TextureRegion(thinSeaWeedSprite, thinSeaWeedSprite.getWidth(), thinSeaWeedSprite.getHeight());
		textureRegions[14] = new TextureRegion(shipIconSprite, shipIconSprite.getWidth(), shipIconSprite.getHeight());
		tiles = new Texture("tiles2.png");
		tiles.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		
		land =  new TextureRegion(tiles,0,0,TILESIZEPLUSS,TILESIZEPLUSS);
		sea =  new TextureRegion(tiles,TILESIZEPLUSS,TILESIZEPLUSS,TILESIZEPLUSS,TILESIZEPLUSS);
		grass =  new TextureRegion(tiles,TILESIZEPLUSS,0,TILESIZEPLUSS,TILESIZEPLUSS-1);
		ygrass =  new TextureRegion(tiles,2*TILESIZEPLUSS,0,TILESIZEPLUSS,TILESIZEPLUSS);
		bgrass =  new TextureRegion(tiles,2*TILESIZEPLUSS,TILESIZEPLUSS,TILESIZEPLUSS,TILESIZEPLUSS);
		dsea =  new TextureRegion(tiles,TILESIZEPLUSS,2*TILESIZEPLUSS,TILESIZEPLUSS,TILESIZEPLUSS);
		lsea =  new TextureRegion(tiles,2*TILESIZEPLUSS+1,2*TILESIZEPLUSS,TILESIZEPLUSS,TILESIZEPLUSS);
		sand =  new TextureRegion(tiles,0,TILESIZEPLUSS-1,TILESIZEPLUSS,TILESIZEPLUSS);
		
        ui = new Ui(skin, stage);
        FileHandle skinfile = Gdx.files.internal("uiskin.json");
		skin = new Skin(skinfile);
        
        lootScreen = new LootScreen(skin, stage);
        mainMenu = new MainMenuOverlay(skin, stage);        
        //inventoryUi = new InventoryUi(skin, stage);
        makeNewWorld();
	
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

		if (stage.keysPressedThisFrame.contains(Input.Keys.ENTER)) {
            DEBUGPRINTOUT = !DEBUGPRINTOUT;
        }

		
		totalPerfCount.tick();
		
		totalPerfCount.start();
		
		
		renderPerfCount.tick();
		shipPerfCount.tick();
		
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
		
		renderPerfCount.start();

		/*
		 * render land
		 */
		float drawSize = (float) TILESIZEPLUSS / (float) TILESIZE;
		for (int i 		= (int)((playerPos.x)-(width/20)); 	   	i < ((int) (playerPos.x) + (width/20)); i++){
			for (int j 	= (int)((playerPos.y)-(height/20)); 	j < ((int) (playerPos.y) + (height/20)); j++){
				if (i<0 || i>=map.length || j<0 || j>=map[0].length){
					batch.draw(dsea,i,j, drawSize, drawSize);
				} else if (map[i][j] > SEALEVEL+BGRASSWIDTH) {
					batch.draw(land,i,j, drawSize, drawSize);
				} else if (map[i][j] > SEALEVEL+YGRASSWIDTH){
					batch.draw(bgrass, i, j, drawSize, drawSize);
				} else if (map[i][j] > SEALEVEL+GRASSWIDTH){
					batch.draw(ygrass, i, j, drawSize, drawSize);
				} else if (map[i][j] > SEALEVEL+SANDWIDTH){
					batch.draw(grass, i, j, drawSize, drawSize);
				} else if (map[i][j] > SEALEVEL){
					batch.draw(sand, i, j, drawSize, drawSize);
				} else {
					float height = (float) map[i][j];
					float darkness = (float) Math.max(Math.min(1.5*Math.pow(height,2), 1),.2);
					seaTintColor.r = darkness/2;
					seaTintColor.g = darkness/2;
					seaTintColor.b = darkness; 
					seaTintColor.a = 1;
					batch.setColor(seaTintColor);
					batch.draw(lsea, i, j, drawSize, drawSize);
					
					batch.setColor(Color.WHITE.tmp());
				}
			}
		}
		LandDecoration l;
		for (int j 	= (int)((playerPos.y) + (height/20)); 	j > ((int) (playerPos.y) - (height/20)); j--){
			for (int i 		= (int)((playerPos.x)-(width/20)); 	   	i < ((int) (playerPos.x) + (width/20)); i++){
				if ((i<0 || i>=map.length || j<0 || j>=map[0].length)){
					continue;
				}
				l = decorationMap[i][j];
				if (l == null) {
					continue;
				}
				if (l.id == 10) {
					batch.draw(textureRegions[8], i+l.xOffset, j+l.yOffset, 2, 2);
				} else if (l.id == 11) {
					batch.draw(textureRegions[9], i+l.xOffset, j+l.yOffset, 2, 2);
				} else if (l.id == 12) {
					batch.draw(textureRegions[10], i+l.xOffset, j+l.yOffset, 2, 2);
				} else if (l.id == 30) {
					batch.draw(textureRegions[11], i, j, 1, 1);
				} else if (l.id == 31) {
					batch.draw(textureRegions[12], i, j, 1, 1);
				} else if (l.id == 32) {
					batch.draw(textureRegions[13], i, j, 1, 1);
				}
			}
		}
		
		
		
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
		renderPerfCount.stop();
		
		for (Debries debrie : debries) {
			if (debrie.alive == false) {
				debriesRemovalSet.add(debrie);
				debrie.delete();
			}
		}
		debries.removeAll(debriesRemovalSet);
		
		shipPerfCount.start();
		
		/*
		 * draw and tick ships
		 */
		
		for (Ship ship : ships){
			ship.tick();
			x = ship.getPos().x;
			y = ship.getPos().y;
			index = ship.getSpriteIndex();
			float[] size = ship.getSpriteSize();
			t = textureRegions[index];
			float shipRotation = (float) (ship.getDir()*360/(2*Math.PI));
			Vector2 hpDrawPos = ship.getPos().add(ship.getSize()[1],ship.getSize()[1]);
			MyUtils.DrawText(((Integer) Math.round(ship.getHp())).toString(), false, hpDrawPos, 1);
			batch.draw(t,x-size[0]/2,y-size[1]/2,
					   size[0]/2,size[1]/2,
					   size[0],size[1],
					   1f,1f,shipRotation,true);
		}
		if (!paused) {
			while (ships.size()<numShips) {
				Ship aiShip;
				if (rand.nextFloat() < .9) {
					aiShip= basicShipGen.genShip(lvl);
				} else {
					aiShip = bossShipGen.genShip(lvl);
				}
				ships.add(aiShip);
			}
			for (Ship ship : ships){
				if (ship.alive == false) {
					if (ship.isWreck()) {
						shipRemovalSet.add(ship);
						if (ship == LovePirates.playerShip) {
							makeNewWorld();
						}
						ship.delete();
					} else {
						ship.alive = true;
						ship.setHp(ship.getMaxHp()/7.5f);
						ship.setWreck();
					}
				}
			}
			for (Ship s:shipRemovalSet) {
				world.destroyBody(s.getBody());
			}
			ships.removeAll(shipRemovalSet);
			shipRemovalSet.clear();
			
			//if (DEBUGPRINTOUT) {
			//	MyUtils.DrawText("Player Alive = " + playerShip.alive, true, new Vector2(10,10), 0);
			//	MyUtils.DrawText("Player wrecked = " + playerShip.isWreck(), true, new Vector2(10,11), 0);
			//	MyUtils.DrawText("Player controller = " + playerShip.controller, true, new Vector2(10,12), 0);
			//}
	
			
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
	
			Ui.draw(batch);
			
			
			
			
			
			for (Projectile proj : projectiles){
				if (proj.dead == true) {
					projRemovalSet.add(proj);
					proj.delete();
				}
			}
			
			projectiles.removeAll(projRemovalSet);
			projRemovalSet.clear();
			
			
			physicsPerfCount.tick();
			physicsPerfCount.start();
			world.step(1/60f, 6, 4);
			physicsPerfCount.stop();
		}
		MyUtils.renderText(batch);
		
		shipPerfCount.stop();
		batch.end();
		stage.act();
		stage.draw();
		if (stage.keysPressedThisFrame.contains(Input.Keys.ESCAPE)) {
			if (!paused) {
				MainMenuOverlay.makeMainMenu();
			} else {
				MainMenuOverlay.kill();
			}
		}
		stage.tick();
		//this debug renderer seems to be very heavy
		//debugRenderer.render(world, camera.combined);
		totalPerfCount.stop();
		MyUtils.renderLines();
		if (DEBUGPRINTOUT) {
			if (shipPerfCount.load.latest > .06) {
				System.out.println(shipPerfCount);
	
			}
			if (renderPerfCount.load.latest > .1) {
				System.out.println(renderPerfCount);
	
			}
			if (physicsPerfCount.load.latest > .05) {
				System.out.println(physicsPerfCount);
	
			}
			if (totalPerfCount.time.latest > .00833) {
				float fps = 1/totalPerfCount.time.latest;
				System.out.println("FPS = " + fps);
				System.out.println(totalPerfCount);
	
			}
		}
	}
	
	static void makeNewWorld() {
		//restart
		System.out.println("makeNewWorld called");
		genWorld(1);

		playerShip = basicShipGen.genShip(5,300,300);
		playerShip.setControler(new PlayerController());
		while (map[(int) playerShip.getPos().x][(int) playerShip.getPos().y]>SEALEVEL) {
			playerShip.setPos((int) playerShip.getPos().x+10, (int) playerShip.getPos().y);
		}
		ships.add(playerShip);
	}
	
	@Override
	public void resize(int width, int height) {
		float vWidth = width/TILESIZE*cameraScalingFactor;
		float vHeight =height/TILESIZE*cameraScalingFactor;
	    camera.setToOrtho(false, vWidth, vHeight);
	    camera.update();
	    stage.getViewport().setScreenSize(width, height); // update the size of ViewPort
	}
	
}
