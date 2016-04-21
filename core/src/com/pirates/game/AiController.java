/**
 * AI's  prototype controller object
 */
package com.pirates.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
public class AiController implements Controller {

	/**
	 * 
	 */
	private static final int targetCounterReset = 1000;
	private int targetcounter;
	private Target target;
	private Ship owner;
	private Vector2 vecToTarget;
	private float projectileSpeed;
	private float projectileLifetime;
	private float projectileRange;
	private float projectileDrag;
	private float targetDeltaAngle;
	private Vector2 rayCastHitLoc;
	private Vector2 reverse;
	private CollisionAvoidanceCallback raycastCallback;
	private boolean active;
	static Random rand = new Random();
	private boolean agressive;
	float w;
	
	enum Modes {AGRESIVE, BACKINGUP}
	Modes mode = Modes.AGRESIVE;
	int backupCount = 0;
	int maxBackupCount = 100;	
	AiController(Ship owner) {
		targetcounter = 0;
		w = (LovePirates.width/LovePirates.TILESIZE)/3;
		active = false;
		agressive = false;
		target = findTarget();
		this.owner = owner;
		reverse = new Vector2(999,999);
		rayCastHitLoc = new Vector2(0,0);
		projectileSpeed = getCannonProjectileSpeed();
		projectileLifetime = getCannonProjectileLifetime();
		projectileDrag = getCannonProjectileDrag();
		projectileRange = projectileSpeed*(projectileLifetime/60)/projectileDrag;
		raycastCallback = new CollisionAvoidanceCallback(this);
		
	}
	
	

	@Override
	/**
	 * called every frame, updates raycasts, targets, and gets targetDeltaAngle and
	 * VecToTarget. In short, updates the AI's perception of the world.
	 */
	public void tick() {
		targetcounter -= 1;
		if (targetcounter <= 0){
			targetcounter = targetCounterReset;
			target = findTarget();
		}
		Vector2 numbOffset = new Vector2(0,1);
		if (LovePirates.DEBUGPRINTOUT) {
			MyUtils.DrawText(mode.toString(), false, owner.getPos(), 0);
			MyUtils.DrawText("BackupCount = "+backupCount, false, owner.getPos().add(numbOffset), 0);
		}

		rayCastHitLoc =null;
		if (target == null){//this does not know if a target is dead, so will keep attacking where player died!
			target = findTarget();
		}
		//ships ai activates when close to player and goes dormant when far away.
		active = LovePirates.mapSpriteSize > owner.getPos().sub(LovePirates.playerShip.getPos()).len();
		if (active) {
			if ((backupCount < 0) && (mode == Modes.AGRESIVE)){
				backupCount = maxBackupCount;
				mode = Modes.BACKINGUP;
			} else if ((rayCastHitLoc != null) && (mode == Modes.AGRESIVE)) {
				backupCount -= 1;
			} else if ((rayCastHitLoc == null) && (mode == Modes.AGRESIVE)){
				backupCount = maxBackupCount;			
			} else if (mode == Modes.BACKINGUP) {
				backupCount -= 1;
				if (backupCount < 0) {
					backupCount = maxBackupCount;
					mode = Modes.AGRESIVE;
				}
			}
			
			
			getVecToTargetAndAngle();
			castRays();
			if (agressive && w < owner.getPos().sub(targetPos()).len()) {
				agressive = false;
				target = findTarget();
			} else if (!agressive && w > owner.getPos().sub(LovePirates.playerShip.getPos()).len()) {
				agressive = true;
				target = findTarget();
				
			} else if (!agressive && w > owner.getPos().sub(targetPos()).len()) {
				target = findTarget();
			}
			
			
		}
	}

	private void getVecToTargetAndAngle() {
		vecToTarget = targetPos().sub(owner.getPos());
		//aim to hit player
		Vector2 ownerAngle = new Vector2(1,1);
		ownerAngle.setAngleRad(owner.getDir());
		Vector2 targetVel = target.getVel().sub(owner.getVel());
		float travelTime = vecToTarget.len()/(projectileSpeed*1.5f);//1.5 is fudge factor which 
		//causes the ships to fire in front of you, but not too far in front. lower number means farther in front.
		Vector2 predictedVecToTarget = vecToTarget.cpy();
		predictedVecToTarget.add(targetVel.setLength(targetVel.len()*travelTime));
		//LovePirates.debugObjects.add(owner.getPos().add(vecToTarget));
		//LovePirates.debugObjects.add(owner.getPos().add(predictedVecToTarget));
		
		//now get ether left or right side to face target:
		targetDeltaAngle = predictedVecToTarget.angleRad(ownerAngle);

	}
	private Target findTarget() {
		Vector2 v;
		if (agressive) {
			return LovePirates.playerShip;
		} else if (targetcounter <= 0) {
			v = new Vector2(rand.nextInt(LovePirates.MAPSIZE),rand.nextInt(LovePirates.MAPSIZE));
			while (LovePirates.map[(int) v.x][(int) v.y]>LovePirates.SEALEVEL) {
				v.x = rand.nextInt(LovePirates.MAPSIZE);
				v.y = rand.nextInt(LovePirates.MAPSIZE);
			}
			return new AiTargetLoc(v);
		} else {
			return target;
		}
	}
	private Vector2 targetPos(){
		return target.getPos();
	}
	private float getCannonProjectileSpeed() {
		return owner.getCannonSpeed();
	}
	private float getCannonProjectileLifetime() {
		return owner.getCannonballLifetime();
	}
	private float getCannonProjectileDrag() {
		return owner.getCannonProjectileDrag();
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
		Vector2 leftOffset = new Vector2(owner.getSize()[1]/1.5f,0);
		Vector2 rightOffset= new Vector2(owner.getSize()[1]/1.5f,0);
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
		if (mode == Modes.BACKINGUP) {
			if (targetDeltaAngle > 0) {
				//maybe put a -.05 in flowing line to stop the ship from going left right left right
				//when it is close to aiming correctly
				if (targetDeltaAngle > Math.PI/2) {
					return Direction.LEFT;
				} else if (targetDeltaAngle < Math.PI/2) {
					return Direction.RIGHT;
				}
			} else {
				if (targetDeltaAngle > -Math.PI/2) {
					return Direction.RIGHT;
				} else if (targetDeltaAngle < -Math.PI/2) {
					return Direction.LEFT;
				}
			}
		}
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
			if (mode == Modes.AGRESIVE) {
				if (rayCastHitLoc == reverse) {
					return -1;
				}
				if (rayCastHitLoc == null) {
					if (vecToTarget.len() < projectileRange/1.5) {
						return .75f;
					} else {
						return 1;
					}
				} else {
					return .5f;
				}
			} else if (mode == Modes.BACKINGUP) {
				//if (rayCastHitLoc == reverse) { 
				return -.2f;
				//}
			}
		} else {
			return 0;
		}
		System.out.println("WARNING, AI controller power is returning 0 when it shouldn't be!");
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Controller#getFireDir()
	 */
	@Override
	public ArrayList<FiringDirection> getFireDir() {
		if (agressive) {
			ArrayList<FiringDirection> fireDirs = new ArrayList<FiringDirection>();
			if (vecToTarget.len() < projectileRange) {
				if ((targetDeltaAngle > (Math.PI/2-.05)) && (targetDeltaAngle < (Math.PI/2+.05))) {
					fireDirs.add(FiringDirection.RIGHT);
				} else if ((targetDeltaAngle < (-Math.PI/2+.05)) && (targetDeltaAngle > (-Math.PI/2-.05))) {
					fireDirs.add(FiringDirection.LEFT);
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

	@Override
	public void setOwner(Ship s) {
		this.owner = s;
		
	}


}
