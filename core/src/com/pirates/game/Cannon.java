/**
 * Simple weapon class, extends equipment, implements fire method
 */
package com.pirates.game;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;
/**
 * @author Elijah
 *
 */
public class Cannon extends Equipment {

	/**
	 * 
	 */
	private static Random rand = new Random();
	private float countdown;
	private float fireSpeed;
	public Cannon() {
		countdown = 0;
		fireSpeed = 45f;
	}

	@Override
	boolean iswepon() {
		return true;
	}
	@Override
	void fire(float dir, Vector2 offset, Ship owner) {
		if (countdown < 0) {
			Vector2 offsetVec = offset.cpy().rotateRad(owner.getDir());
			Vector2 firepos = owner.getPos().add(offsetVec);
			Vector2 vel = new Vector2(1,1);
			vel.setLength(fireSpeed);
			vel.setAngleRad((float) dir + owner.getDir());
			vel.add(owner.getVel());
			LovePirates.projectiles.add(new Cannonball(firepos,vel,owner));
			countdown += 60;
		}
	}

	@Override
	void tick() {
		if (countdown >= 0) {
			countdown -= .5 + rand.nextFloat();
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	float getProjSpeed() {
		return fireSpeed;
	}

	@Override
	float getProjLifetime() {
		
		return Cannonball.LIFETIME;
	}
	

}
