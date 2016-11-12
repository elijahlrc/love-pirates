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
public class BuckshotCannon extends Equipment {

	/**
	 * 
	 */
	private float fireSpeed;
	private float reloadTime;
	private static Random rand = new Random();
	public BuckshotCannon() {
		countdown = 0;
		fireSpeed = 30f;
		reloadTime = 2.5f;
	}

	@Override
	boolean iswepon() {
		return true;
	}
	@Override
	void fire(float dir, Vector2 offset, Ship owner) {
		if (countdown <= 0) {
			LovePirates.soundEffects.play("buckshot");
			Vector2 offsetVec = offset.cpy().rotateRad(owner.getDir());
			Vector2 firepos = owner.getPos().add(offsetVec);
			Vector2 vel = new Vector2(1,1);
			float exitVel = (float) (rand.nextGaussian()*6+fireSpeed);
			vel.setLength(exitVel);
			vel.add(owner.getVel());
			for (int i = 1; i <= 5; i++) {
				float angleoffset = (rand.nextFloat()-.5f)/5f;
				vel.setAngleRad((float) dir + owner.getDir()+angleoffset);
				LovePirates.projectiles.add(new Buckshot(firepos,vel,owner));
			}
			
			countdown += reloadTime;
		}
	}

	@Override
	void tick(float reloadSpeed) {
		if (countdown >= 0) {
			countdown -= (.5+rand.nextFloat())/(reloadSpeed*60f);
		}
		// TODO Auto-generated method stub
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
		return Buckshot.drag;
	}
	

}
