/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 * This class represents temporary simple objects like
 * cannonballs 
 * smoke
 * ship wakes
 * etc
 */
public abstract class Projectile implements DrawableObj{

	/**
	 * 
	 */
	int sprite;
	boolean dead;
	protected double lifetime;
	abstract void move();
	abstract void tick();
	public abstract Vector2 getPos();
	public abstract int getSpriteIndex();
	abstract void delete();
}
