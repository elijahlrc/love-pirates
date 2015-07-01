/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
public class Cannonball extends Projectile {
	Vector2 pos;
	Vector2 vel;
	double lifetime;
	float[] size = {.3f, .3f};
	/**
	 * @param pos
	 */
	public Cannonball(Vector2 position, Vector2 velocity, Ship owner) {
		pos = position;
		vel = velocity;
		lifetime = 100;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#move()
	 */
	@Override
	protected void move() {
		pos.add(vel);
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#tick()
	 */
	@Override
	void tick() {
		move();
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#getPos()
	 */
	@Override
	public Vector2 getPos() {
		return pos;
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#getSpriteIndex()
	 */
	@Override
	public int getSpriteIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float[] getSize() {
		return size;
	}

}
