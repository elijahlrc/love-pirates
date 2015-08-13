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
public class Ship extends DrawableObj implements Collideable{
	/**
	 * @author Elijah
	 *
	 */
	
	
	static final int NUM_SLOTS = 0;
	static final float DAMAGEFACTOR = 3;
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
	private int PHYSICSBUFFER = 25;
	private float hp;
	boolean alive;

	private float repairSupplies;
	/**This class is the super for all ships
	 * All ships have position vector "loc", velocity vector "vel", drag coefficient "dragcoef", and a "maxpower"
	 * Perhaps the following things should be in some kind of ship data structure/class, 
	 * but as they are not at the moment, they are going to be used as is:
	 * private float maxPower;
	 * private float turnRate;
	 * private float dragCoef;
	 * @param hp 
	 * 
	*/
	Ship(int x, int y, float turnrate, float dragcoef, float maxpower,float len, float wid, float hp) {
		this.hp = hp;
		alive = true;
		repairSupplies = 5f;
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
		float[] verts = {0,width/2,length/2, 0, 0, -width/2, -length/2, 0};
		shipshape.set(verts);
		
		fixtureDef.shape = shipshape;
		fixtureDef.density = 1f;
		fixtureDef.friction = .1f;
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
		if (repairSupplies > 0){
			repairSupplies -= .01;
			hp += .01;
		}
		if (hp<0) {
			alive = false;
		}
	}
	void clearTerrain() {
		
		
	}
	private void updateColidors() {
		if (controller.getActive()) {
			int x = (int) getPos().x;
			int y = (int) getPos().y;
			for (int i = -PHYSICSBUFFER; i<PHYSICSBUFFER; i++){
				for (int j= -PHYSICSBUFFER; j<PHYSICSBUFFER; j++){
					if ((x+i < LovePirates.map.length)&&(x+i >= 0)&&(y+j < LovePirates.map.length)&&( y+j >= 0)){
						if (LovePirates.map[x+i][y+j] > LovePirates.SEALEVEL) {
							//if a terrain collider is already at the point specified
							//colliderPool should handle this
							//and ignore this command.
							LovePirates.colliderPool.createTerainCollider(x+i,y+j);
						}
					}
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
		return body.getPosition().cpy();
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
		Object b = contact.getFixtureB().getUserData();
		if (b instanceof Cannonball){
			Ship owner = ((Cannonball) b).getOwner();
			if (owner != this) {
				System.out.println("HP is"+hp);
				hp-=impulse.getNormalImpulses()[0]*DAMAGEFACTOR;
			}
		} else if (b instanceof LootCrate) {
			LootCrate c = (LootCrate) b;
			getLoot(c.contents, c.quantaty);
		}
		
	}
	int openSlots() {
		int count = 0;
		for (Slot s : slots) {
			if (s == null) {
				count++;
			}
		}
		return count;
		
	}
	void getLoot(String loot,int quantity) {
		if (loot.equals("repair supplies")){
			repairSupplies += quantity*5;
		} else if (loot.equals("crew")) {
			//TODO
		} else if (loot.equals("cannons")) {
			for (int i = 0; i<quantity; i++) {
				if (openSlots() > 0){
					
				}
			}
		}
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
	public void delete() {
		LovePirates.world.destroyBody(body);
		int lootAmount = (int) (length*width) + 1;
		int debriesAmount = lootAmount*7;
		for (int i = 0 ; i <= debriesAmount; i++) {
			LovePirates.debries.add(new Debries((float) (body.getPosition().x+((Math.random()-.5)*Math.sqrt(i))),(float) (body.getPosition().y+((Math.random()-.5)*Math.sqrt(i))), false));
		}
		for (int i = 0; i < lootAmount; i++) {
			LovePirates.debries.add(new LootCrate((float) (body.getPosition().x+((Math.random()-.5)*Math.sqrt(i))),(float) (body.getPosition().y+((Math.random()-.5)*Math.sqrt(i))), "repair supplies", 1));

		}
		
		
	}
	public float getHp() {
		return hp;
	}
	
	
	
}
