package com.pirates.game;

import com.badlogic.gdx.Input;

import java.util.ArrayList;
class PlayerController implements Controller {
	CStage inputProcessor;
	PlayerController () {
		inputProcessor = LovePirates.stage;
	}
	
	@Override
	public Direction getTurn() {
		if (inputProcessor.keysDown.contains(Input.Keys.A)) {
			return Direction.LEFT;
		} else if (inputProcessor.keysDown.contains(Input.Keys.D)) {
			return Direction.RIGHT;
		}
		return null;
	}

	@Override
	public float getPower() {
		if (inputProcessor.keysDown.contains(Input.Keys.W)) {
			return 1.0f;
		} else if ( (inputProcessor.keysDown.contains(Input.Keys.A) || inputProcessor.keysDown.contains(Input.Keys.D)) && 
					inputProcessor.keysDown.contains(Input.Keys.S)) {
			return -.1f;//if player attempts to turn while pressing backwards, turn at 1/4 throttle.
		} else if (inputProcessor.keysDown.contains(Input.Keys.A) || inputProcessor.keysDown.contains(Input.Keys.D)) {
			return .75f;
		} else if (inputProcessor.keysDown.contains(Input.Keys.S)) {
			return -.5f;
		}
		return 0;
	}

	@Override
	public ArrayList<FiringDirection> getFireDir() {
		ArrayList<FiringDirection> dirs = new ArrayList<FiringDirection>();
		if (inputProcessor.keysDown.contains(Input.Keys.Q)) {
			dirs.add(FiringDirection.LEFT);
		}
		if (inputProcessor.keysDown.contains(Input.Keys.E)) {
			dirs.add(FiringDirection.RIGHT);
		}
		if (inputProcessor.keysDown.contains(Input.Keys.SPACE)) {
			dirs.add(FiringDirection.FORWARD);
		}
		
		
		if (inputProcessor.isClicking) {
			// TODO: change FiringDirection enum?
			// TODO: determine which side of the screen to fire on based on player position
			//dirs.add(inputProcessor.mousePosition);
			
		}
		
		
		
		
		
		return dirs;
	}
	
	// Listener for quitting game with ESC
	// TODO: Figure out if we should keep this in PlayerController
	public boolean sendQuit() {
		if (inputProcessor.keysDown.contains(Input.Keys.ESCAPE)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setProjectileSpeed(float projectileSpeed) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public float getProjectileSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean getActive() {
		return true;
	}

}
