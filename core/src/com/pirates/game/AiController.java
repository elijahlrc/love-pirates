/**
 * AI's  prototype controller object
 */
package com.pirates.game;

import java.util.ArrayList;

/**
 * @author Elijah
 *
 */
public class AiController implements Controller {

	/**
	 * 
	 */
	public AiController() {
		// TODO Auto-generated constructor stub
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
