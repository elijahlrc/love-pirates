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
public class Ship implements DrawableObj, Collideable{
	/**
	 * @author Elijah
	 *
	 */
	
	
	static final int NUM_SLOTS = 0;
	
	Controller controller;
	private float maxPower;
	private float turnRate;
	private int spriteIndex;
	private static BodyDef bodyDef = new BodyDef();
	private static FixtureDef fixtureDef = new FixtureDef();
	private Fixture fixture;
	private float width;
	private float length;
	private Body body;
	protected Slot[] slots;
	private int PHYSICSBUFFER = 15;
	private Vector2 offset;
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
		maxPower = maxpower;
		width = wid;
		length = len;
		slots = new Slot[NUM_SLOTS];
		bodyDef.position.set(x,y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.active = true;
		bodyDef.angularDamping = 3f;
		bodyDef.linearDamping = dragcoef;
		body = LovePirates.world.createBody(bodyDef);
		body.setAwake(true);
		PolygonShape shipshape = new PolygonShape();
		offset = new Vector2(-width/2, -length/2);
		float[] verts = {0,width/2,length/2, 0, 0, -width/2, -length/2, 0};
		shipshape.set(verts);
		
		fixtureDef.shape = shipshape;
		fixtureDef.density = 1f;
		fixtureDef.friction = .9f;
		fixtureDef.restitution = .2f;
		fixtureDef.filter.categoryBits = LovePirates.SHIP_MASK;
		fixtureDef.filter.maskBits = LovePirates.LAND_MASK | LovePirates.SHIP_MASK | LovePirates.PROJ_MASK;
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
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
		for (Slot s : slots) {
			s.fire(fireDirs);
		}
	}
	public void setPos(int x,int y){
		body.setTransform(x, y, getDir());
	}
				
	public Vector2 getPos() {
		//should be center of mass
		return body.getPosition().cpy();
	}
	public Vector2 getDrawPos() {
		
		return body.getPosition().add(offset);
	}
	public Vector2 getVel() {
		return body.getLinearVelocity().cpy();
	}
	public float getDir() {
		return body.getAngle();
	}
	public float[] getSize() {
		float[] size =  {length, width};
		return size;
	}
	@Override
	public int getSpriteIndex() {
		return spriteIndex;
	}
	@Override
	public boolean handlePreCollide(Contact contact) {
		Object b = contact.getFixtureB().getUserData();
		if (b instanceof Cannonball){
			Ship owner = ((Cannonball) b).getOwner();
			if (owner == this) {
				return false;
			}
		}
		return true;
	}
	@Override
	public void handleBeginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handlePostCollide(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		//damage?
		
	}
	public float getCannonSpeed() {
		float speedTotal = 0;
		int weaponCount = 0;
		for (Slot slot : slots) {
			if (slot.getProjSpeed()>=0) {
				weaponCount += 1;
				speedTotal += slot.getProjSpeed();
			}
		}
		if (weaponCount == 0) {
			return 0;
		}
		return speedTotal/weaponCount;
	}
	public float getCannonballLifetime() {
		float lifetimeTotal = 0;
		int weaponCount = 0;
		for (Slot slot : slots) {
			if (slot.getProjLifetime()>=0) {
				weaponCount += 1;
				lifetimeTotal += slot.getProjLifetime();
			}
		}
		if (weaponCount == 0) {
			return 0;
		}
		return lifetimeTotal/weaponCount;
	}
	
	
	
}
