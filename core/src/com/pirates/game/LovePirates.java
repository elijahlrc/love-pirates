package com.pirates.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LovePirates extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	double[][] map;
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		PerlinNoiseGen noiseGen =  PerlinNoiseGen.init();
		map = noiseGen.getFullPerlinArray(11);
		for (int i = 0; i<20; i++) {
			map = noiseGen.getFullPerlinArray(10);
			MyUtils.visuliseArray(map);
		}
		for (int i = 0; i<10; i++) {
			map = noiseGen.getFullPerlinArray(11);
			MyUtils.visuliseArray(map);
		}
		for (int i = 0; i<3; i++) {
			map = noiseGen.getFullPerlinArray(13);
			MyUtils.visuliseArray(map);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
