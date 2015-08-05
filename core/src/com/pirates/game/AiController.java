/**
 * AI's  prototype controller object
 */
package com.pirates.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
public class AiController implements Controller {

	/**
	 * 
	 */
	private Object target;
	private Ship owner;
	private Vector2 vecToTarget;
	private float projectileSpeed;
	private float projectileLifetime;
	private float projectileRange;
	private float targetDeltaAngle;
	private CollisionAvoidanceCallback raycastCallback;
	AiController(Ship owner) {
		this.owner = owner;
		projectileSpeed = getCannonProjectileSpeed();
		projectileLifetime = getCannonProjectileLifetime();
		projectileRange = projectileSpeed*projectileLifetime/60;
		raycastCallback = new CollisionAvoidanceCallback();
	}
	
	

	@Override
	public void tick() {
		castRays();
		if (target == null){
			target = findTarget();
		}
		getVecToTargetAndAngle();
		
	}
	private void getVecToTargetAndAngle() {
		vecToTarget = targetPos().sub(owner.getPos());
		//aim to hit player
		//NOTE gota subtract owner vel somewhere
		Vector2 ownerAngle = new Vector2(1,1);
		ownerAngle.setAngleRad(owner.getDir());
		Vector2 targetVel = ((Ship) target).getVel().sub(owner.getVel());
		float travelTime = vecToTarget.len()/(projectileSpeed*1.5f);//1.5 is fudge factor which 
		//causes the ships to fire in front of you, but not too far in front. lower number means farther in front.
		Vector2 predictedVecToTarget = vecToTarget.cpy();
		predictedVecToTarget.add(targetVel.setLength(targetVel.len()*travelTime));
		LovePirates.debugObjects.add(owner.getPos().add(vecToTarget));
		LovePirates.debugObjects.add(owner.getPos().add(predictedVecToTarget));
		
		//now get ether left or right side to face target:
		targetDeltaAngle = predictedVecToTarget.angleRad(ownerAngle);
	}
	private Object findTarget() {
		return LovePirates.playerShip;
	}
	private Vector2 targetPos(){
		return LovePirates.playerShip.getPos();
	}
	private float getCannonProjectileSpeed() {
		return owner.getCannonSpeed();
	}
	private float getCannonProjectileLifetime() {
		return owner.getCannonballLifetime();
	}
	private Direction castRays() {
		System.out.println("I'm a magiciooon, i cast rays!");
		Vector2 forwardRaycastPos = new Vector2(1,1);
		forwardRaycastPos.setAngleRad(owner.getDir());
		forwardRaycastPos.setLength(owner.getVel().len()*2);//check that this is not 0.
		forwardRaycastPos.add(owner.getPos());
		System.out.println("-----");
		System.out.println(owner.getPos());
		System.out.println(forwardRaycastPos);
		if (!(forwardRaycastPos.equals(owner.getPos()))) {
			//MyUtils.DrawDebugLine(forwardRaycastPos, owner.getPos());
			LovePirates.world.rayCast(raycastCallback, owner.getPos(), forwardRaycastPos);
			
			forwardRaycastPos.setAngleRad((float) (owner.getDir()+Math.PI/8f));
			forwardRaycastPos.setLength(owner.getVel().len()*2);//check that this is not 0.
			forwardRaycastPos.add(owner.getPos());
			
			//MyUtils.DrawDebugLine(forwardRaycastPos, owner.getPos());	
			LovePirates.world.rayCast(raycastCallback, owner.getPos(), forwardRaycastPos);
			
			forwardRaycastPos.setAngleRad((float) (owner.getDir()-Math.PI/8f));
			forwardRaycastPos.setLength(owner.getVel().len()*2);//check that this is not 0.
			forwardRaycastPos.add(owner.getPos());
			//MyUtils.DrawDebugLine(forwardRaycastPos, owner.getPos());
			LovePirates.world.rayCast(raycastCallback, owner.getPos(), forwardRaycastPos);
		}
		
		return null;
	}
	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getTurn()
	 */
	@Override
	public Direction getTurn() {
		// TODO Auto-generated method stub
		
		if (vecToTarget.len() < projectileRange) {
			
			if (targetDeltaAngle > 0) {
				//maybe put a -.05 in flowing line to stop the ship from going left right left right
				//when it is close to aiming correctly
				if (targetDeltaAngle > Math.PI/2) {
					return Direction.RIGHT;
				} else if (targetDeltaAngle < Math.PI/2) {
					return Direction.LEFT;
				}
			} else {
				System.out.println("Ship to the LEFT");
				System.out.println(targetDeltaAngle);
				if (targetDeltaAngle > -Math.PI/2) {
					return Direction.RIGHT;
				} else if (targetDeltaAngle < -Math.PI/2) {
					return Direction.LEFT;
				}
			}
		} else {
			System.out.println("SHIP out of RANGE:");
			//aim to get closer to player
			if (targetDeltaAngle<0) {
				return Direction.LEFT;
			} else {
				return Direction.RIGHT;
			}
		}
		return null;
		
		
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getPower()
	 */
	@Override
	public float getPower() {
		// TODO Auto-generated method stub
		if (vecToTarget.len() < projectileRange/1.5) {
			return 1;
		} else {
			return .75f;
		}
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getFireDir()
	 */
	@Override
	public ArrayList<FireingDirection> getFireDir() {
		ArrayList<FireingDirection> fireDirs = new ArrayList<FireingDirection>();
		if (vecToTarget.len() < projectileRange) {
			System.out.println(targetDeltaAngle);
			if ((targetDeltaAngle > (Math.PI/2-.05)) && (targetDeltaAngle < (Math.PI/2+.05))) {
				fireDirs.add(FireingDirection.RIGHT);
			} else if ((targetDeltaAngle < (-Math.PI/2+.05)) && (targetDeltaAngle > (-Math.PI/2-.05))) {
				fireDirs.add(FireingDirection.LEFT);
			}
		}
		return fireDirs;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}


}
