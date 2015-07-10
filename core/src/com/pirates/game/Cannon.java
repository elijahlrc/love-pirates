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
	private Ship owner;
	private int countdown;
	public Cannon() {
		countdown = 0;
	}

	@Override
	boolean iswepon() {
		return true;
	}
	void setOwner(Ship owner){
		this.owner = owner;
	}

	@Override
	void fire(float dir, Vector2 offset, Ship owner) {
		if (countdown < 0) {
			Vector2 ownerpos = owner.getPos();
			Vector2 vel = new Vector2(1,1);
			vel.setLength(30f);
			vel.setAngleRad((float) dir+owner.getDir());
			LovePirates.projectiles.add(new Cannonball(ownerpos,vel,owner));
			countdown += 10;
		}
	}

	@Override
	void tick() {
		if (countdown >= 0) {
			countdown -= 1;
		}
		// TODO Auto-generated method stub
		
	}
	

}
