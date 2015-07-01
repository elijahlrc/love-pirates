package com.pirates.game;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LovePirates extends ApplicationAdapter {
	SpriteBatch batch;
	static double[][] map;
	static HandleUserInput inputProcessor;
	static TextureRegion[] textureRegions;
	static Texture tiles;
	static HashSet<Projectile> projectiles;
	static HashSet<Ship> ships;
	static OrthographicCamera camera;
	static Ship playerShip;
	static int width;
	static int height;
	static int TILESIZE = 32;
	static float SEALEVEL = .73f;
	static TextureRegion land;
	static TextureRegion sea;
	static TextureRegion grass;
	static TextureRegion ygrass;
	static TextureRegion bgrass;
	static TextureRegion sand;
	static TextureRegion dsea;
	static TextureRegion lsea ;
	static int MAPSIZE = 10;
	static int COLLIDERPOOLSIZE = 10000;
	static float dt;
	static World world;
	static ColliderPool colliderPool;
	static Box2DDebugRenderer debugRenderer;
	//static BodyDef bodyDef = new BodyDef();
	//static FixtureDef fixtureDef = new FixtureDef();
	public LovePirates(int w, int h){
		super();
		width = w;
		height = h;
	}
	@Override
	public void create() {
		camera = new OrthographicCamera(60f,33.75f);
		inputProcessor = HandleUserInput.init();
		Gdx.input.setInputProcessor(inputProcessor);
		//box2d
		Box2D.init();
		
		world = new World(new Vector2(0, 0), true);
		
		//this number is performance sensitive and can create subtle collision bugs, beware
		//large number == fewer bugs but worse performance
		colliderPool = new ColliderPool(COLLIDERPOOLSIZE);
		
		
		//debug renderer for physics rendering
		debugRenderer = new Box2DDebugRenderer();
		
		
		
		//game state stuff
		ships = new HashSet<Ship>();
		projectiles = new HashSet<Projectile>();
		//sprite batch for efficient bliting
		batch = new SpriteBatch();
		
		//map gen init
		PerlinNoiseGen noiseGen =  PerlinNoiseGen.init();
		
		//list of blitable images
		textureRegions = new TextureRegion[99];
		//this next line is bad, should be part of the spritesheet and a texture region
		Texture shiptex = new Texture("ship.bmp");
		textureRegions[0] = new TextureRegion(shiptex, shiptex.getWidth(), shiptex.getHeight()); //this is wrong

		
		
		tiles = new Texture("tiles.png");
		land =  new TextureRegion(tiles,0,0,32,32);
		sea =  new TextureRegion(tiles,32,32,32,32);
		grass =  new TextureRegion(tiles,32,0,32,32);
		ygrass =  new TextureRegion(tiles,64,0,32,32);
		bgrass =  new TextureRegion(tiles,64,32,32,32);
		dsea =  new TextureRegion(tiles,32,64,32,32);
		lsea =  new TextureRegion(tiles,64,64,32,32);
		sand =  new TextureRegion(tiles,0,32,32,32);
		
		map = noiseGen.getFullPerlinArray(MAPSIZE);
		
		//save map
		MyUtils.visuliseArray(map,false);
		
		
		
		//					  x,y,turnrate,dragcoef,maxpower, width, length
		playerShip = new Ship((int) Math.pow(2, MAPSIZE-1),500,2f,0f,15f,1,3);
		while (map[(int) playerShip.getPos().x][(int) playerShip.getPos().y]>SEALEVEL) {
			playerShip.setPos((int) playerShip.getPos().x+10, (int) playerShip.getPos().y);
		}
		playerShip.setControler(new PlayerController());
		ships.add(playerShip);
		
		
		Ship aiShip = new Ship((int) Math.pow(2, MAPSIZE-1),500,2f,.7f,15f,1,3);
		aiShip.setControler(new AiController(aiShip));
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
		for (int i = (((int) playerPos.x)-(width/10)); i< ((int) (playerPos.x) +(width/10)); i++){
			for (int j = (((int)(playerPos.y)-height/10)); j<((int) (playerPos.y)+height/10); j++){
				if (i<0 || i>=map.length || j<0 || j>=map[0].length){
					batch.draw(dsea,i,j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.1) {
					batch.draw(land,i,j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.05){
					batch.draw(bgrass, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.03){
					batch.draw(ygrass, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL+.02){
					batch.draw(grass, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL){
					batch.draw(sand, i, j, 1, 1);
				} else if (map[i][j] > SEALEVEL-.04){
					batch.draw(lsea, i, j, 1, 1);
				} else if (map[i][j] > .0){
					batch.draw(sea, i, j, 1, 1);
				}

			}
		}
		TextureRegion t;
		//delete terrain physics in this loop
		for (Ship ship : ships){
			ship.clearTerrain();
		}
		//add terrain physics obj in this loop
		for (Ship ship : ships){
			ship.tick();
			x = ship.getPos().x;
			y = ship.getPos().y;
			index = ship.getSpriteIndex();
			float[] size = ship.getSize();
			t = textureRegions[index];
			float shipRotation = (float) (ship.getDir()*360/(2*Math.PI));
			
			batch.draw(t,x,y,
					   0,0,//t.getRegionWidth()/2, 
					   size[0],size[1],
					   1f,1f,shipRotation,true);
		}
		for (Projectile proj : projectiles){
			proj.tick();
			x = (int) proj.getPos().x;
			y = (int) proj.getPos().y;
			index = proj.getSpriteIndex();
			batch.draw(textureRegions[index],x,y);
		}
		
		
		batch.end();
		world.step(1/60f, 6, 2);
		debugRenderer.render(world, camera.combined);

	}
}
