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
	private float fireSpeed;
	private float reloadTime;
	private static Random rand = new Random();
	public Cannon() {
		countdown = 0;
		fireSpeed = 20f;
		reloadTime = 5;
	}

	@Override
	boolean iswepon() {
		return true;
	}
	@Override
	void fire(float dir, Vector2 offset, Ship owner) {
		if (countdown <= 0) {
			Vector2 offsetVec = offset.cpy().rotateRad(owner.getDir());
			Vector2 firepos = owner.getPos().add(offsetVec);
			Vector2 vel = new Vector2(1,1);
			float exitVel = (float) (rand.nextGaussian()*2+fireSpeed);
			vel.setLength(exitVel);
			vel.setAngleRad((float) dir + owner.getDir());
			vel.add(owner.getVel());
			LovePirates.projectiles.add(new Cannonball(firepos,vel,owner));
			countdown += reloadTime;
		}
	}

	@Override
	void tick(float reloadSpeed) {
		if (countdown >= 0) {
			countdown -= (.5+rand.nextFloat())/(reloadSpeed*60f);
		}		
	}

	@Override
	float getProjSpeed() {
		return fireSpeed*.5f;
	}

	@Override
	float getProjLifetime() {
		
		return Cannonball.LIFETIME;
	}

	@Override
	float getProjDrag() {
		return Cannonball.drag;
	}
	

}
