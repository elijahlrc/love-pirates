package com.pirates.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table.Debug;

import java.util.ArrayList;
class PlayerController implements Controller {
	CStage inputProcessor;
	private Ship owner;
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
			// TODO: make it possible to fire when moving with keyboard
			
			// click position and player position
			// TODO: simplify these variables
			Vector2 target = inputProcessor.mousePosition.cpy();
			Vector2 playerPos = new Vector2(LovePirates.width/LovePirates.TILESIZE*LovePirates.cameraScalingFactor,
					LovePirates.height/LovePirates.TILESIZE*LovePirates.cameraScalingFactor);
			
			// direction where the projectiles should fire
			Vector2 direction = target.sub(playerPos).scl(0.5f);
			direction.y = -direction.y;		// TODO: fix this hack
			
			// make angle (in radians) positive, modulo by 2*pi, then divides by pi
			float targetAngle = (float) (LovePirates.playerShip.getDir() - direction.angleRad() + 2*Math.PI);
			targetAngle %= 2*Math.PI;
			targetAngle /= Math.PI;
			
			//MyUtils.DrawText("direction: " + direction, true, new Vector2(7,7), 20);
			//MyUtils.DrawText("target: " + target, true, new Vector2(5,5), 20);
			MyUtils.DrawText("angle: " + targetAngle, true, direction, 40);
			
			// if clicks on left/right side of ship, fire on left/right
			if (targetAngle < 1.7 && targetAngle > 1.3) {
				dirs.add(FiringDirection.LEFT);
			} else if (targetAngle < 0.7 && targetAngle > 0.3) {
				dirs.add(FiringDirection.RIGHT);
			}
		
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
	@Override
	public void setOwner(Ship s) {
		this.owner = s;
		
	}

}
