/**
 * AI's  prototype controller object
 */
package com.pirates.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
public class AiController implements Controller {

	/**
	 * 
	 */
	private Object target;
	private Ship owner;
	private Vector2 vecToTarget;
	private float projectileSpeed;

	AiController(Ship owner, float projectileSpeed) {
		this.owner = owner;
		this.projectileSpeed = projectileSpeed;

	}
	
	@Override
	public void tick() {
		if (target == null){
			target = findTarget();
		}
		
	}
	private Object findTarget() {
		return LovePirates.playerShip;
	}
	private Vector2 dirToTarget(){
		return LovePirates.playerShip.getPos();
	}


	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getTurn()
	 */
	@Override
	public Direction getTurn() {
		// TODO Auto-generated method stub
		vecToTarget = owner.getPos().sub(dirToTarget());
		float deltaAngle = (float) (vecToTarget.angleRad()-(owner.getDir())%(2*Math.PI));
		if (deltaAngle<Math.PI) {
			return Direction.RIGHT;
		} else {
			return Direction.LEFT;
		}
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getPower()
	 */
	@Override
	public float getPower() {
		// TODO Auto-generated method stub
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getFireDir()
	 */
	@Override
	public ArrayList<FireingDirection> getFireDir() {
		Ship pShip = (Ship) target;
		vecToTarget = owner.getPos().sub(pShip.getPos());
		float deltaAngle = (float) (vecToTarget.angleRad()-(owner.getDir())%(2*Math.PI));
		if (deltaAngle<Math.PI) {
		} else {
		}
		return null;
	}


}
