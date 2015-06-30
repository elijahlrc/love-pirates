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
	Vector2 target;
	Ship owner;
	@Override
	public void tick() {
		if (target == null){
			findTarget();
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
		return null;
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getPower()
	 */
	@Override
	public float getPower() {
		// TODO Auto-generated method stub
		return 0;
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
