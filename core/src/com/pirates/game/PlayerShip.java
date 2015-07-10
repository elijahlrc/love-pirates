/**
 * 
 */
package com.pirates.game;

/**
 * @author Elijah
 *
 */
public class PlayerShip extends Ship {

	/**
	 * @param x
	 * @param y
	 * @param turnrate
	 * @param dragcoef
	 * @param maxpower
	 * @param len
	 * @param wid
	 */
	static final int NUM_SLOTS = 10;
	
	public PlayerShip(int x, int y, float turnrate, float dragcoef,
			float maxpower, float len, float wid) {
		super(x, y, turnrate, dragcoef, maxpower, len, wid);
		
		slots = new Slot[2];
		
		for (int i=0; i<1; i++) {
			slots[i] = new Slot(0, 0, 1, (float) Math.PI*1.5f, FireingDirection.RIGHT, this);
		}
		for (int i=1; i<2; i++) {
			slots[i] = new Slot(0, 0, 1, (float) (Math.PI*.5f), FireingDirection.LEFT, this);
		}
		for (Slot slot : slots) {
			Cannon newgun = new Cannon();
			slot.setContents(newgun);
		}
		
	}

}
