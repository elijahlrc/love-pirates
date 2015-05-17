package com.pirates.game;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LovePirates extends ApplicationAdapter {
	SpriteBatch batch;
	static double[][] map;
	static HandleUserInput inputProcessor;
	static Texture[] textures;
	static HashSet<Projectile> projectiles;
	static HashSet<Ship> ships;
	@Override
	public void create() {
		ships = new HashSet<Ship>();
		projectiles = new HashSet<Projectile>();
		batch = new SpriteBatch();
		PerlinNoiseGen noiseGen =  PerlinNoiseGen.init();
		textures = new Texture[99];
		textures[0] = new Texture("ship.bmp");
		//map = noiseGen.getFullPerlinArray(11);
		inputProcessor = HandleUserInput.init();
		Gdx.input.setInputProcessor(inputProcessor);
		//						x,y,turnrate,dragcoef,maxpower
		Ship playerShip = new Ship(30,30,3f,.05f,.5f);
		playerShip.setControler(new PlayerController());
		ships.add(playerShip);
		/*
		  for (int i = 0; i<20; i++) {
			map = noiseGen.getFullPerlinArray(10);
			MyUtils.visuliseArray(map);
		}
		for (int i = 0; i<5; i++) {
			map = noiseGen.getFullPerlinArray(13);
			MyUtils.visuliseArray(map);
		}
		*/
		
	}
	

	@Override
	public void render() {
		Gdx.gl.glClearColor(.1f, .1f, .5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		int index;
		int x;
		int y;
		
		for (Ship ship : ships){
			ship.tick();
			x = (int) ship.getPos().x;
			y = (int) ship.getPos().y;
			index = ship.getSpriteIndex();
			TextureRegion t = new TextureRegion(textures[index],textures[index].getWidth(),textures[index].getHeight());
			float shipRotation = ship.getVel().angle();
			batch.draw(t,x,y,
					   t.getRegionWidth()/2, t.getRegionHeight()/2, 
					   t.getRegionWidth(),t.getRegionHeight(),
					   1f,1f,shipRotation,true);
		}
		for (Projectile proj : projectiles){
			proj.tick();
			x = (int) proj.getPos().x;
			y = (int) proj.getPos().y;
			index = proj.getSpriteIndex();
			batch.draw(textures[index],x,y);
		}
		batch.end();
	}
}
