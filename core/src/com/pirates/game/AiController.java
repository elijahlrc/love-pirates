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
	private Vector2 rayCastHitLoc;
	private Vector2 reverse;
	private CollisionAvoidanceCallback raycastCallback;
	private boolean active;
	AiController(Ship owner) {
		active = false;
		this.owner = owner;
		reverse = new Vector2(999,999);
		rayCastHitLoc = new Vector2(0,0);
		projectileSpeed = getCannonProjectileSpeed()*.65f;
		projectileLifetime = getCannonProjectileLifetime();
		projectileRange = projectileSpeed*projectileLifetime/60;
		raycastCallback = new CollisionAvoidanceCallback(this);
	}
	
	

	@Override
	/**
	 * called every frame, updates raycasts, targets, and gets targetDeltaAngle and
	 * VecToTarget. In short, updates the AI's perception of the world.
	 */
	public void tick() {
		rayCastHitLoc =null;
		if (target == null){
			target = findTarget();
		}
		active = 64> owner.getPos().sub(targetPos()).len();
		if (active) {
			castRays();
			
			getVecToTargetAndAngle();
		}
	}
	private void getVecToTargetAndAngle() {
		vecToTarget = targetPos().sub(owner.getPos());
		//aim to hit player
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
	
	void rayCastCatcher(Vector2 CollisionLoc) {
		float dist = owner.getPos().sub(CollisionLoc).len();
		if (rayCastHitLoc != null) {
			if (dist<owner.getPos().sub(rayCastHitLoc).len()) {
				rayCastHitLoc = CollisionLoc;
			}
		} else {
			rayCastHitLoc = CollisionLoc;
		}
	}
	Vector2 getCollisionLoc() {
		Vector2 tmpVec;
		if (rayCastHitLoc != null) {
			tmpVec = rayCastHitLoc.cpy();
			rayCastHitLoc = null;
			return tmpVec;
			}
		return null;
	}
	/**
	 * this func casts rays and finds the closest 'rock', that is the
	 * nearest land which was hit by a ray. It sets the rayCastHitLoc instance var 
	 * to be equal to the location of this rock. it does this by using the ray cast
	 * callback defined in CollisionAvoidanceCallback to call the rayCastCatcher
	 * which eddits rayCastHitLoc.
	 */
	private void castRays() {
		Vector2 leftVec;
		Vector2 rightVec;
		Vector2 leftOffset = new Vector2(owner.getSize()[1],0);
		Vector2 rightOffset= new Vector2(owner.getSize()[1],0);
		leftOffset.setAngleRad((float) (owner.getDir()+Math.PI/2)).add(owner.getPos());
		rightOffset.setAngleRad((float) (owner.getDir()-Math.PI/2)).add(owner.getPos());
		Vector2 forwardRaycastPos = new Vector2(1,1);
		float rayLen = Math.max(owner.getVel().len()*2, 10);
		float ang = 0;
		if (!forwardRaycastPos.equals(owner.getPos())) {
			while  (ang < Math.PI/2.5) {
				forwardRaycastPos.setLength(rayLen);
				forwardRaycastPos.setAngleRad((float) (owner.getDir()+ang));
				forwardRaycastPos.add(leftOffset);
				if (LovePirates.DEBUGPRINTOUT){
					MyUtils.DrawDebugLine(forwardRaycastPos, leftOffset);
				}
				LovePirates.world.rayCast(raycastCallback, leftOffset, forwardRaycastPos);
				rightVec = getCollisionLoc();
				forwardRaycastPos.setLength(rayLen);
				forwardRaycastPos.setAngleRad((float) (owner.getDir()-ang));
				forwardRaycastPos.add(rightOffset);
				
				if (LovePirates.DEBUGPRINTOUT){
					MyUtils.DrawDebugLine(forwardRaycastPos, rightOffset);
				}
				LovePirates.world.rayCast(raycastCallback, rightOffset, forwardRaycastPos);
				leftVec = getCollisionLoc();
				if (leftVec == null) {
					rayCastHitLoc = rightVec;
					break;
				} else if (rightVec == null) {
					rayCastHitLoc = leftVec;
					break;
				}
				ang += Math.PI/32;
				if (ang >= Math.PI/2.5) {
					rayCastHitLoc = reverse;
				}
			}
			
		}
	}
	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getTurn()
	 */
	@Override
	public Direction getTurn() {
		// TODO Auto-generated method stub
		if (active) {
			if (rayCastHitLoc == null) {
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
						if (targetDeltaAngle > -Math.PI/2) {
							return Direction.RIGHT;
						} else if (targetDeltaAngle < -Math.PI/2) {
							return Direction.LEFT;
						}
					}
				} else {
					//aim to get closer to player
					if (targetDeltaAngle<0) {
						return Direction.LEFT;
					} else {
						return Direction.RIGHT;
					}
				}
				return null;
			} else {
				if (rayCastHitLoc != reverse){
					Vector2 myLoc = owner.getPos();
					Vector2 ownerAng = new Vector2(1,0);
					ownerAng.setAngleRad(owner.getDir());
					float angToCollision = myLoc.cpy().sub(rayCastHitLoc).angle(ownerAng);
					if (angToCollision>0){
						return Direction.RIGHT;
					} else {
						return Direction.LEFT;
					}
				} else {
					return Direction.LEFT;
				}
			}
		} else {
			return null;
		}
		
		
		
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getPower()
	 */
	@Override
	public float getPower() {
		
		if (active){
			if (rayCastHitLoc == reverse) {
				return -1;
			}
			if (rayCastHitLoc == null) {
				if (vecToTarget.len() < projectileRange/1.5) {
					return 1;
				} else {
					return .75f;
				}
			} else {
				return .5f;
			}
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getFireDir()
	 */
	@Override
	public ArrayList<FireingDirection> getFireDir() {
		if (active) {
			ArrayList<FireingDirection> fireDirs = new ArrayList<FireingDirection>();
			if (vecToTarget.len() < projectileRange) {
				if ((targetDeltaAngle > (Math.PI/2-.05)) && (targetDeltaAngle < (Math.PI/2+.05))) {
					fireDirs.add(FireingDirection.RIGHT);
				} else if ((targetDeltaAngle < (-Math.PI/2+.05)) && (targetDeltaAngle > (-Math.PI/2-.05))) {
					fireDirs.add(FireingDirection.LEFT);
				}
			}
			return fireDirs;
		} else {
			return null;
		}
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}



	@Override
	public boolean getActive() {
		return active;
	}


}
