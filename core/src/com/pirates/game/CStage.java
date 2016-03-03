package com.pirates.game;

import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class CStage extends Stage {
	HashSet<Integer> keysDown;
	public static Vector2 mousePosition;		// XY position of mouse
	public static boolean isClicking = false;	// is mouse being clicked?
	
	public CStage() {
		keysDown = new HashSet<Integer>();
	}
	
	@Override
	public boolean keyDown(int keyCode) {
		keysDown.add(keyCode);
		return super.keyDown(keyCode);
	}
	
	@Override
	public boolean keyUp(int keyCode) {
		keysDown.remove(keyCode);
		return super.keyUp(keyCode);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// prints out what "button" is, left is 0, right is 1, mouse scroll is 2
		MyUtils.DrawText("Mouse button: " + button, true, new Vector2(4,5), 30);
		
		// prints out where user has clicked
		// TODO: figure out why clicks are not going through
		MyUtils.DrawText("Mouse location: " + "(" + screenX + ", " + screenY + ")", true, new Vector2(4,6), 30);
		
		// gets location of mouse
		mousePosition = new Vector2(screenX, screenY);

		isClicking = true;
		
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		isClicking = false;
		return super.touchUp(screenX, screenY, pointer, button);
	}
}
