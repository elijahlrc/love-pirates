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
	static int tileSize = 32;
	static TextureRegion land;
	static TextureRegion sea;
	static TextureRegion grass;
	static TextureRegion ygrass;
	static TextureRegion bgrass;
	static TextureRegion sand;
	static TextureRegion dsea;
	static TextureRegion lsea ;
	static int mapSize = 11;
	static float dt;
	static World world;
	public LovePirates(int w, int h){
		super();
		width = w;
		height = h;
	}
	@Override
	public void create() {
		camera = new OrthographicCamera(width*1.5f,height*1.5f);
		inputProcessor = HandleUserInput.init();
		Gdx.input.setInputProcessor(inputProcessor);
		//box2d
		Box2D.init();
		world = new World(new Vector2(0, 0f), true);
		//debug renderer for physics rendering
		Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
		
		//gamestate stuff
		ships = new HashSet<Ship>();
		projectiles = new HashSet<Projectile>();
		//sprite batch for efficent bliting
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
		map = noiseGen.getFullPerlinArray(mapSize);
		
		//save map
		MyUtils.visuliseArray(map,false);
		

		
		//					  x,y,turnrate,dragcoef,maxpower
		playerShip = new Ship((int) Math.pow(2, mapSize-1),2000,3f,.05f,1f);
		playerShip.setControler(new PlayerController());
		ships.add(playerShip);
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
		int x;
		int y;
		Vector2 playerPos = playerShip.getPos();
		System.out.println(playerPos);
		for (int i = (((int) playerPos.x)-width)/tileSize; i< ((int) (playerPos.x) +width)/tileSize; i++){
			for (int j = (((int)(playerPos.y)-height)/tileSize); j<((int) (playerPos.y)+height)/tileSize; j++){
				if (i<0 || i>=map.length || j<0 || j>=map[0].length){
					batch.draw(dsea,i*tileSize,j*tileSize);
				} else if (map[i][j] > .77) {
					batch.draw(land,i*tileSize,j*tileSize);
				} else if (map[i][j] > .76){
					batch.draw(bgrass, i*tileSize, j*tileSize);
				} else if (map[i][j] > .755){
					batch.draw(ygrass, i*tileSize, j*tileSize);
				} else if (map[i][j] > .75){
					batch.draw(grass, i*tileSize, j*tileSize);
				} else if (map[i][j] > .745){
					batch.draw(sand, i*tileSize, j*tileSize);
				} else if (map[i][j] > .74){
					batch.draw(lsea, i*tileSize, j*tileSize);
				} else if (map[i][j] > .0){
					batch.draw(dsea, i*tileSize, j*tileSize);
				}

			}
		}
		TextureRegion t;
		for (Ship ship : ships){
			ship.tick();
			x = (int) ship.getPos().x;
			y = (int) ship.getPos().y;
			index = ship.getSpriteIndex();
			t = textureRegions[index];
			float shipRotation = ship.getDir();
			batch.draw(t,x,y,
					   t.getRegionHeight()/2,t.getRegionWidth()/2, 
					   t.getRegionHeight(),t.getRegionWidth(),
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
		world.step(1/300f, 6, 2);
	}
}
