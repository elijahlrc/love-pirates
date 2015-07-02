/**
 * 
 */
package com.pirates.game;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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
	
	private float dir = 0f;
	private Controller controller;
	private float maxPower;
	private float turnRate;
	private float dragCoef;
	private int spriteIndex;
	private static BodyDef bodyDef = new BodyDef();
	private static FixtureDef fixtureDef = new FixtureDef();
	private Fixture fixture;
	private float width;
	private float length;
	private Body body;
	private int PHYSICSBUFFER = 5;
	/**This class is the super for all ships
	 * All ships have position vector "loc", velocity vector "vel", drag coefficient "dragcoef", and a "maxpower"
	 * Perhaps the following things should be in some kind of ship data structure/class, 
	 * but as they are not at the moment, they are going to be used as is:
	 * private float maxPower;
	 * private float turnRate;
	 * private float dragCoef;
	 * 
	*/
	Ship(int x, int y, float turnrate, float dragcoef, float maxpower,float len, float wid) {
		turnRate = turnrate;
		dragCoef = dragcoef;
		maxPower = maxpower;
		width = wid;
		length = len;
		bodyDef.position.set(x,y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.active = true;
		bodyDef.angularDamping = 3f;
		bodyDef.linearDamping = dragcoef;
		body = LovePirates.world.createBody(bodyDef);
		body.setAwake(true);
		PolygonShape shipshape = new PolygonShape();
		float[] verts = {width/2,length,width, length/2, width/2, 0f, 0f, length/2};
		shipshape.set(verts);
		
		fixtureDef.shape = shipshape;
		fixtureDef.density = 1f;
		fixtureDef.friction = .9f;
		fixtureDef.restitution = .2f;
		fixture = body.createFixture(fixtureDef);
		
		//shapes must be disposed otherwise memory leak.
		shipshape.dispose();
	}
	/** 
	 * just calls all the moves, firing, etc that each ship does each frame, maybe should take in a dt.
	 */
	void setControler(Controller c){
		controller = c;
	}
	void tick(){
		updateColidors();
		controller.tick();
		move();
		fire();
	}
	void clearTerrain() {
		
		
	}
	private void updateColidors() {
		int x = (int) getPos().x;
		int y = (int) getPos().y;
		for (int i = -PHYSICSBUFFER; i<PHYSICSBUFFER; i++){
			for (int j= -PHYSICSBUFFER; j<PHYSICSBUFFER; j++){
				if (LovePirates.map[x+i][y+j] > LovePirates.SEALEVEL) {
					//if a terrain collider is already at the point specified
					//colliderPool should handle this
					//and ignore this command.
					LovePirates.colliderPool.createTerainCollider(x+i,y+j);
				}
			}
		}
	}
	/** move method
	 * gets move from controller
	 * makes move
	 * includes turning and changes to velocity
	 * now using BOX2D!!!!!!!!!
	 */
	void move() {
		float drag;
		float currentDir = body.getAngle();
		
		Direction turnDir = controller.getTurn();
		float power = controller.getPower();
		
		Vector2 forceV = new Vector2(1,1);
		forceV.setAngleRad(currentDir);
		forceV.setLength(power*maxPower);
		if (power > 0) {
			forceV.setAngleRad(currentDir);
			body.applyForceToCenter(forceV, true);
		} else {
			forceV.setAngleRad(currentDir+(float) Math.PI);
			body.applyForceToCenter(forceV, true);
		}
		
		if (turnDir == Direction.LEFT) {
			body.applyTorque(turnRate, true);
		} else if (turnDir == Direction.RIGHT) {
			body.applyTorque(-turnRate, true);
		}
		
		float deltaAngle = (float) ((body.getAngle()-body.getLinearVelocity().angleRad())%(Math.PI*2));
		
		drag = (float) Math.abs(Math.sin(deltaAngle));
		
		Vector2 forceD = new Vector2(1,1);
		Vector2 dir = body.getLinearVelocity();
		forceD.setAngleRad((float) (dir.angleRad()+Math.PI));
		forceD.setLength((float) (drag*Math.pow(getVel().len(),1.5)));
		
		body.applyForceToCenter(forceD, true);
		
	}
	/**
	 * Fire method iterates through slots and fires them with the appropriate side;
	 */
	void fire() {
		ArrayList<FireingDirection> fireDirs = controller.getFireDir();
	}
	public void setPos(int x,int y){
		body.setTransform(x, y, getDir());
	}
	public Vector2 getPos() {
		System.out.println(body.getPosition());
		return body.getPosition();
	}
	public Vector2 getVel() {
		return body.getLinearVelocity();
	}
	public float getDir() {
		return body.getAngle();
	}
	public float[] getSize() {
		float[] size =  {width, length};
		return size;
	}
	@Override
	public int getSpriteIndex() {
		return spriteIndex;
	}
	
	
	
}
