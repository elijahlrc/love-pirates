/**
 * 
 */
package com.pirates.game;
import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
/**
 * @author Elijah
 *
 */
public class Ship implements DrawableObj{
	/**
	 * @author Elijah
	 *
	 */
	public class Slot {
		Vector2 offset;
		int size;
		int dir;//in degrees, ranging from 0 to 360
		FireingDirection side;
		Equipment inslot;
		Ship owner;
		/**
		 * 
		 * @param xOffset
		 * @param yOffset
		 * @param sizeOfSlot not yet implemented
		 * @param slotDir angle slot fires, in degrees
		 * @param fireSlot the key which fires this slot
		 */
		Slot(int xOffset, int yOffset, int sizeOfSlot,int slotDir, FireingDirection fireSlot,Ship ownedBy) {
			offset = new Vector2(xOffset,yOffset);
			size = sizeOfSlot;
			dir = slotDir;
			owner = ownedBy;
		}
		void fire(FireingDirection dirToFire) {
			if (dirToFire == side) {
				if (inslot.iswepon()) {
					inslot.fire(dir, offset, owner);//how to handle this nicely?
				}
			}
		}
		void setEquip(Equipment e) {
			inslot = e;
		}
	}
	
	private Vector2 vel = new Vector2(0,1);
	private Vector2 loc;
	private float dir = 0f;
	private Controller controller;
	private float maxPower;
	private float turnRate;
	private float dragCoef;
	private int spriteIndex;
	/**This class is the super for all ships
	 * All ships have position vector "loc", velocity vector "vel", drag coefficient "dragcoef", and a "maxpower"
	 * Perhaps the following things should be in some kind of ship data structure/class, 
	 * but as they are not at the moment, they are going to be used as is:
	 * private float maxPower;
	 * private float turnRate;
	 * private float dragCoef;
	 * 
	*/
	Ship(int x, int y, float turnrate, float dragcoef, float maxpower) {
		turnRate = turnrate;
		dragCoef = dragcoef;
		maxPower = maxpower;
		loc = new Vector2(x,y);
	}
	/** 
	 * just calls all the moves, firing, etc that each ship does each frame, maybe should take in a dt.
	 */
	void setControler(Controller c){
		controller = c;
	}
	void tick(){
		move();
		fire();
	}
	/** move method
	 * gets move from controller
	 * makes move
	 * includes turning and changes to velocity
	 */
	void move() {
		
		Direction turnDir = controller.getTurn();
		float power = controller.getPower();
		Vector2 thrustVec = new Vector2(0f,1f);
		thrustVec.setAngle(dir);
		if (turnDir == Direction.LEFT) {
			dir += turnRate;
		} else if (turnDir == Direction.RIGHT) {
			dir -= turnRate;
		}
		if (power < 0) {
			thrustVec.setAngle(dir+180);
		}
		thrustVec.setLength(power*maxPower);
		vel.add(thrustVec);
		vel.setLength(vel.len()*(1-dragCoef));
		loc.add(vel);
		
	}
	/**
	 * Fire method iterates through slots and fires them with the appropriate side;
	 */
	void fire() {
		ArrayList<FireingDirection> fireDirs = controller.getFireDir();
		/*TODO*/
	}
	@Override
	public Vector2 getPos() {
		return loc;
	}
	public Vector2 getVel() {
		return vel;
	}
	public float getDir() {
		return dir;
	}
	@Override
	public int getSpriteIndex() {
		return spriteIndex;
	}
	
	
}
