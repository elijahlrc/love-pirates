package com.pirates.game;

import java.util.ArrayList;

interface Controller {
	/** get the actions the ship should take from the ai or player inputs
	 * 	used so ship objects can be player or ai controlled.
	 * @return
	 */
	Direction getTurn();//returns a direction for the ship to turn
	/** returns a float between 0 and 1 (inclusive) for how fast the ship is going. /or some reason vector2's use floats so this is a float*/
	float getPower();
	/** get the directions a ship is firing at any given time*/
	ArrayList<FireingDirection> getFireDir();//returns a firing direction for the ship to fire
	/**
	 * called every frame before getpower, getturn etc.
	 */
	void tick();
	void setProjectileSpeed(float projectileSpeed);
	float getProjectileSpeed();
}
