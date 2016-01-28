package com.pirates.game;

import java.util.HashSet;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class CStage extends Stage {
	HashSet<Integer> keysDown;
	CStage () {
		keysDown = new HashSet<Integer>();
	}
	
	public boolean keyDown(int keyCode) {
		System.out.println(keyCode);
		keysDown.add(keyCode);
		return super.keyDown(keyCode);
	}
	public boolean keyUp(int keyCode) {
		keysDown.remove(keyCode);
		return super.keyUp(keyCode);
	}
}
