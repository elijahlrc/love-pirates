/**
 * 
 */
package com.pirates.game;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
/**
 * @author Elijah
 *
 */
public class Ship extends DrawableObj implements Collideable, Target{
	/**
	 * @author Elijah
	 *
	 */
	
	
	static final int NUM_SLOTS = 0;
	static final float DAMAGEFACTOR = 6;
	Controller controller;
	private float maxPower;
	private float turnRate;
	private int spriteIndex;
	private static BodyDef bodyDef = new BodyDef();
	private static FixtureDef fixtureDef = new FixtureDef();
	private Fixture fixture;
	private float width;
	private float length;
	private float spriteWidth;
	private float spriteLength;
	private Body body;
	protected Slot[] slots;
	private int PHYSICSBUFFER = 25;
	private float hp;
	private float maxhp;
	int sailors;
	private int maxSailors;
	int gunners;
	private int maxGunners;
	private float baseTurnRate;
	boolean alive;
	int gold;
	Random rand;
	float repairSupplies;
	private float reloadSpeed;
	private boolean isWreck;
	boolean boss;
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
	Ship(int x, int y, float turnrate, float dragcoef, float maxpower,float len, float wid,
			float hp, float maxhp,int gunners,int maxGunners,int sailors, int maxSailors) {
		this.hp = hp;
		this.maxhp = maxhp;
		this.gunners = gunners;
		this.sailors = sailors;
		this.maxGunners = maxGunners;
		this.maxSailors = maxSailors;
		reloadSpeed = 1;
		isWreck = false;
		alive = true;
		gold = 0;
		repairSupplies = 0f;
		baseTurnRate = turnrate;
		width = wid;
		length = len;
		spriteWidth = width;
		spriteLength = length;
		turnRate = baseTurnRate/4 + baseTurnRate*sailors/(length*10);
		maxPower = maxpower;
		slots = new Slot[NUM_SLOTS];
		boss = false;
		bodyDef.position.set(x,y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.active = true;
		bodyDef.angularDamping = 5f;
		bodyDef.linearDamping = dragcoef;
		body = LovePirates.world.createBody(bodyDef);
		body.setAwake(true);
		PolygonShape shipshape = new PolygonShape();
		float[] verts = {0,width/2,length/2, 0, 0, -width/2, -length/2, 0};
		shipshape.set(verts);
		rand = new Random();
		
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
	void bodyReInstantiate(World world) {
		setBody(world.createBody(bodyDef));
		body.setAwake(true);
		PolygonShape shipshape = new PolygonShape();
		float[] verts = {0,width/2,length/2, 0, 0, -width/2, -length/2, 0};
		shipshape.set(verts);
		rand = new Random();
		
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
		controller.setOwner(this);
	}
	void tick(){
		updateColidors();
		controller.tick();
		move();
		fire();
		if ((repairSupplies > 0)&&(hp <= maxhp)&&(isWreck == false)){
			repairSupplies -= .002;
			hp += .002;
		}
		if (hp<0) {
			alive = false;
		}
	}
	void clearTerrain() {

	}
	void getReloadSpeed() {
		reloadSpeed = ((float)findNumberOfCannons())/((float) gunners+1f);
	}
	void getTurnRate() {
		turnRate = baseTurnRate/4 + baseTurnRate*sailors/(length*10);
	}
	

	void addCrew(int number, String type){
		if (type.equals("sailors")) {
			if (sailors + number <= maxSailors) {
				sailors += number;
				getTurnRate();
			} else {
				sailors = maxSailors;
				getTurnRate();
			}
		} else if (type.equals("gunners")) {
			if (gunners + number <= maxGunners) {
				gunners += number;
				getReloadSpeed();
			} else {
				gunners = maxGunners;
				getReloadSpeed();
				
			}
		} else {
			System.out.println("WARNING, BAD VALUE PASSED TO addCrew()");
		}
	}
	float getMaxHp() {
		return maxhp;
	}
	void setHp(float h) {
		hp = h;
	}
	void removeCrew(int number, String type) {
		if (type.equals("sailors")) {
			if (sailors - number > 0) {
				sailors -= number;
				getTurnRate();
			} else {
				sailors = 0;
				getTurnRate();
			}
		} else if (type.equals("gunners")) {
			if (gunners - number > 0) {
				gunners -= number;
				getReloadSpeed();
			} else {
				gunners = 0;
				getReloadSpeed();
				
			}
		} else {
			System.out.println("WARNING, BAD VALUE PASSED TO removeCrew()");
		}
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
		ArrayList<FiringDirection> fireDirs = controller.getFireDir();
		for (Slot s : slots) {
			s.fire(fireDirs,reloadSpeed);
		}
	}
	void setPos(int x,int y){
		body.setTransform(x, y, getDir());
	}
	void setPos(float x,float y){
		body.setTransform(x, y, getDir());
	}
	void setDir(float a) {
		body.setTransform(body.getPosition(),a);
	}
	public Vector2 getPos() {
		return body.getPosition().cpy();
	}
	public Vector2 getVel() {
		return body.getLinearVelocity().cpy();
	}
	float getDir() {
		return body.getAngle();
	}
	public float[] getSize() {
		float[] size =  {length, width};
		return size;
	}
	void setSpriteSize(float len, float wid) {
		spriteLength = len;
		spriteWidth = wid;
	}
	float [] getSpriteSize() {
		float[] size =  {spriteLength, spriteWidth};
		return size;
	}
	@Override
	public int getSpriteIndex() {
		return spriteIndex;
	}
	public void setSpriteIndex(int i) {
		spriteIndex = i;
		setSpriteSize(this.length,this.width);
	}
	/*
	 * these checks stop a ship from colliding with its own cannonballs
	 * @see com.pirates.game.Collideable#handlePreCollide(com.badlogic.gdx.physics.box2d.Contact)
	 */
	@Override
	public boolean handlePreCollide(Contact contact) {
		Object b = contact.getFixtureB().getUserData();
		if (b instanceof Cannonball){
			Ship owner = ((Cannonball) b).getOwner();
			if (owner == this) {
				return false;
			}
		}
		if (b instanceof Buckshot){
			Ship owner = ((Buckshot) b).getOwner();
			if (owner == this) {
				return false;
			}
		}
		return true;
	}
	@Override
	public void handleBeginContact(Contact contact) {
	}
	@Override
	public void handlePostCollide(Contact contact, ContactImpulse impulse) {
		Object b = contact.getFixtureB().getUserData();
		Object a = contact.getFixtureA().getUserData();
		
		if (b instanceof Cannonball){
			Ship owner = ((Cannonball) b).getOwner();
			if (owner != this) {
				Float dmg = impulse.getNormalImpulses()[0]*DAMAGEFACTOR;
				MyUtils.DrawText(((Float) (Math.round(dmg*10)/10f)).toString(), false, ((Cannonball) b).getPos(), (int) ((Cannonball) b).lifetime);
				hp -= dmg;
				removeCrew((int) Math.max(rand.nextGaussian()*dmg,0),"sailors");
				removeCrew((int) Math.max(rand.nextGaussian()*dmg,0),"gunners");

			}
		} else if (b instanceof Buckshot){
				Ship owner = ((Buckshot) b).getOwner();
				if (owner != this) {
					Float dmg = impulse.getNormalImpulses()[0]*DAMAGEFACTOR/4;
					//MyUtils.DrawText(((Float) (Math.round(dmg*10)/10f)).toString(), false, ((Buckshot) b).getPos(), (int) ((Buckshot) b).lifetime);
					hp -= dmg;
					removeCrew((int) Math.max(rand.nextGaussian()*dmg,0),"sailors");
					removeCrew((int) Math.max(rand.nextGaussian()*dmg,0),"gunners");
				}

		} else if (b instanceof LootCrate) {
			LootCrate c = (LootCrate) b;
			getLoot(c.contents, c.quantaty);
		} else if (a instanceof LootCrate) {
			LootCrate c = (LootCrate) a;
			getLoot(c.contents, c.quantaty);
		} else if (b instanceof Ship) {
			if (this.controller instanceof PlayerController) {
				Ship bship = (Ship) b;
				if (bship.isWreck) {
					this.body.setLinearVelocity(0, 0);
					LootScreen.makeLootScreen(bship, this);
				}
			}
		} else if (a instanceof Ship) {
			if ( ((Ship) a).controller instanceof PlayerController) {
				Ship aship = (Ship) a;
				if (aship.isWreck) {
					aship.body.setLinearVelocity(0, 0);
					LootScreen.makeLootScreen(aship, this);
				}
			}
		}
	}

	/**
	 * finds and returns a random slot
	 * returns null if there are no open slots
	 * @return
	 */
	Slot findOpenSlot() {
		ArrayList<Slot> leftOpenSlots = new ArrayList<Slot>();
		ArrayList<Slot> rightOpenSlots = new ArrayList<Slot>();
		int slotIndex;
		for (Slot s : slots) {
			if (s.inslot == null) {
				if (s.side == FiringDirection.LEFT){
					leftOpenSlots.add(s);
				} else {
					rightOpenSlots.add(s);
				}
			}
		}
		if (leftOpenSlots.size() + rightOpenSlots.size() == 0) {
			return null;
		} else if (leftOpenSlots.size() > rightOpenSlots.size()) {
			slotIndex = rand.nextInt(leftOpenSlots.size());
			return leftOpenSlots.get(slotIndex);
		} else {
			slotIndex = rand.nextInt(rightOpenSlots.size());
			return rightOpenSlots.get(slotIndex);
		}
		
	}
	int findNumberOfCannons() {
		int slotNumber = 0;
		for (Slot s : slots) {
			if (s.inslot != null) {
				slotNumber += 1;
			}
		}
		return slotNumber;
	}
	
	
	void getLoot(String loot,Integer quantity) {
		MyUtils.DrawText("The chest contains "+quantity.toString()+" "+loot, false, getPos(), 150);
		if (loot.equals("repair supplies")){
			repairSupplies += quantity*5;
		} else if (loot.equals("cannoneers")) {
			addCrew(quantity*2,"gunners");
		} else if (loot.equals("sailors")) {
			addCrew(quantity*2,"sailors");
		} else if (loot.equals("cannons")) {
			Slot s = null;
			for (int i = 0; i<quantity; i++) {
				s = findOpenSlot();
				if (s != null) {
					s.setContents(new Cannon());
				}
			}
		} else if (loot.equals("buckshot cannons")) {
			Slot s = null;
			for (int i = 0; i<quantity; i++) {
				s = findOpenSlot();
				if (s!=null) {
					s.setContents(new BuckshotCannon());
				}
			}
		} else if (loot.equals("treasure")) {
			gold += quantity;
		} else {
			System.out.println("WARNING, getloot got an un-handled loot type:    " + loot);
		}
	}
	public float getCannonSpeed() {
		float speedTotal = 0;
		int weaponCount = 0;
		for (Slot slot : slots) {
			if (slot.getProjSpeed()>0) {
				weaponCount += 1;
				speedTotal += slot.getProjSpeed();
			}
		}
		if (weaponCount == 0) {
			return 0;
		}
		return speedTotal/weaponCount;
	}
	public float getCannonProjectileDrag() {
		float dragTotal = 0;
		int weaponCount = 0;
		for (Slot slot : slots) {
			if (slot.getProjDrag()>=0) {
				weaponCount += 1;
				dragTotal += slot.getProjDrag();
			}
		}
		if (weaponCount == 0) {
			return 1;
		}
		return dragTotal/weaponCount;
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
	public String pickLootType() {
		float choice = rand.nextFloat();
		if (choice> .85) {
			return "repair supplies";
		} else if (choice > .75) {
			return "cannons";
		} else if (choice > .625) {
			return "buckshot cannons";
		} else if (choice > .5) {
			return "sailors";
		} else if (choice > .25) {
			return "cannoneers";
		} else {
			return "treasure";
		}
	}
	public void delete() {
		
		//DeadShipGen.genShip(this);
		//old loot style
		
		int lootAmount = (int) (2*(length*width));
		int debriesAmount = lootAmount*3;
		for (int i = 0 ; i <= debriesAmount; i++) {
			LovePirates.debries.add(new Debries((float) (body.getPosition().x+((Math.random()-.5)*Math.sqrt(i))),(float) (body.getPosition().y+((Math.random()-.5)*Math.sqrt(i))), false));
		}
		if (boss && this != LovePirates.playerShip) {
			LovePirates.nextWorld();
		}
		
		for (int i = 0; i < lootAmount; i++) {
			LovePirates.debries.add(new LootCrate((float) (body.getPosition().x+((Math.random()-.5)*Math.sqrt(i))),(float) (body.getPosition().y+((Math.random()-.5)*Math.sqrt(i))), pickLootType(), 1));
		}
		
		
		
		
		
	}
	float getHp() {
		return hp;
	}
	boolean isWreck() {
		return isWreck;
	}
	void setWreck() {
		int lootAmount = (int) (2*(length*width));
		int debriesAmount = lootAmount*2;
		for (int i = 0 ; i <= debriesAmount; i++) {
			LovePirates.debries.add(new Debries((float) (body.getPosition().x+((Math.random()-.5)*Math.sqrt(i))),(float) (body.getPosition().y+((Math.random()-.5)*Math.sqrt(i))), false));
		}
		isWreck = true;
		controller = new StaticController(this);
		this.isWreck = true;
		this.setSpriteIndex(7);
		this.setSpriteSize(length*2, width*2);
	}
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}
	/*
	 * set this ships controller to controller another ship, also gives your crew to them
	 */
	public void captureShip(Ship s) {
		s.addCrew(gunners,"gunners");
		s.addCrew(sailors,"sailors");
		s.setControler(this.controller);
		
		s.alive = true;
		this.setWreck();
		
		s.hp = (float) Math.ceil(s.maxhp/10);
		s.repairSupplies += repairSupplies;
		s.gold = gold;
		s.setSpriteIndex(0);
		s.setSpriteSize(s.length, s.width);
		s.isWreck = false;
		this.setWreck();
		if (this == LovePirates.playerShip) {
			LovePirates.playerShip = s;
		}
		
	}
	float getPower() {
		
		return maxPower;
	}
	
	
	
	
}
