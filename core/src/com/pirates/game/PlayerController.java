package com.pirates.game;

import com.badlogic.gdx.Input;

import java.util.ArrayList;
class PlayerController implements Controller {
	HandleUserInput inputProcessor;
	PlayerController () {
		inputProcessor = LovePirates.inputProcessor;
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
	public ArrayList<FireingDirection> getFireDir() {
		ArrayList<FireingDirection> dirs = new ArrayList<FireingDirection>();
		if (inputProcessor.keysDown.contains(Input.Keys.Q)) {
			dirs.add(FireingDirection.LEFT);
		}
		if (inputProcessor.keysDown.contains(Input.Keys.E)) {
			dirs.add(FireingDirection.RIGHT);
		}
		if (inputProcessor.keysDown.contains(Input.Keys.SPACE)) {
			dirs.add(FireingDirection.FORWARD);
		}
		return dirs;
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

}
