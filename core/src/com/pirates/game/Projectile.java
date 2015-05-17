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
	Vector2 pos;
	double lifetime;
	protected abstract void move();
	abstract void tick();
	abstract public Vector2 getPos();
	abstract public int getSpriteIndex();
}
