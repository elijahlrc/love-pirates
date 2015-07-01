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
	private Vector2 target;
	private Ship owner;
	private Vector2 vecToTarget;
	@Override
	public void tick() {
		if (target == null){
			target = findTarget();
		}
		
	}
	private Vector2 findTarget(){
		return LovePirates.playerShip.getPos();
	}
	public AiController(Ship owner) {
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getTurn()
	 */
	@Override
	public Direction getTurn() {
		// TODO Auto-generated method stub
		vecToTarget = owner.getPos().sub(target);
		float deltaAngle = (float) (vecToTarget.angleRad()-(owner.getDir())%(2*Math.PI));
		System.out.println(deltaAngle);
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
		// TODO Auto-generated method stub
		return null;
	}


}
