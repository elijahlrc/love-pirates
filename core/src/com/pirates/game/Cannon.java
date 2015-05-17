/**
 * Simple weapon class, extends equipment, implements fire method
 */
package com.pirates.game;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
public class Cannon extends Equipment {

	/**
	 * 
	 */
	public Cannon() {
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean iswepon() {
		return true;
	}

	@Override
	void fire(int dir, Vector2 offset, Ship owner) {
		Vector2 ownerpos = owner.getPos();
		Vector2 vel = new Vector2();
		vel.setLength(10f);
		vel.setAngle((float) dir);
		LovePirates.projectiles.add(new Cannonball(ownerpos,vel,owner));
	}

}
