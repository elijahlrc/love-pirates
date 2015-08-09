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
public abstract class Projectile extends DrawableObj{

	/**
	 * 
	 */
	int sprite;
	boolean dead;
	protected double lifetime;
	abstract void move();
	abstract void tick();
	abstract Vector2 getPos();
	abstract int getSpriteIndex();
	abstract void delete();
}
