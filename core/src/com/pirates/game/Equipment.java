/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;

/** class for things which go in equipment slots
 * @author Elijah
 */
public abstract class Equipment {
	protected float countdown;

	/**
	 * @return true if equipment is a weapon
	 */
	abstract boolean iswepon();
	/**
	 * this method basically just constructs particles with the appropriate params.
	 */
	abstract void fire(float dir, Vector2 offset, Ship owner);
	abstract void tick(float reloadSpeed);
	abstract float getProjSpeed();
	abstract float getProjLifetime();
	abstract float getProjDrag();
}
