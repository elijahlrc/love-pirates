/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


/**
 * @author Elijah
 */
public class Cannonball extends Projectile implements Collideable  {	
	static final float LIFETIME = 100;
	int number;
	private float size;
	private Body body;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	CircleShape circleShape = new CircleShape();
	private Fixture fixture;
	private Ship owner;
	/**
	 * 
	 */
	Cannonball(Vector2 position, Vector2 velocity, Ship owner) {
		dead = false;
		lifetime = LIFETIME;
		size = .3f;
		this.owner = owner;
		bodyDef = new BodyDef();
		bodyDef.bullet = true;
		fixtureDef = new FixtureDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		circleShape.setRadius(size/2);
		fixtureDef.shape = circleShape;
		fixtureDef.density = .3f;
		fixtureDef.filter.categoryBits = LovePirates.PROJ_MASK;
		fixtureDef.filter.maskBits = LovePirates.SHIP_MASK;

		//boxShape.dispose(); 

		bodyDef.position.set(position);
		body = LovePirates.world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
		//the '7' in the following line determines how much randomness there is in projectile velocities
		float actualspeed = (velocity.len()*(1f+((float) Math.random()-.5f)/13));
		float actualang = (velocity.angleRad()*(1f+((float) Math.random()-.5f)/20));
		Vector2 realVel = velocity.cpy();
		realVel.setLength(actualspeed);
		realVel.setAngleRad(actualang);
		body.setLinearDamping(1.5f);
		body.setLinearVelocity(realVel);

	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#move()
	 */
	@Override
	void move() {
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#tick()
	 */
	@Override
	void tick() {
		//dt?
		lifetime -= 1;

		if (lifetime<0) {
			dead = true;
		}
		move();
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#getPos()
	 */
	@Override
	public Vector2 getPos() {
		return body.getPosition();
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#getSpriteIndex()
	 */
	@Override
	
	int getSpriteIndex() {
		return 1;
	}
	

	@Override
	public float[] getSize() {
		//return new float[2];
		return new float[] {size, size};
	}
	public Ship getOwner() {
		return owner;
	}

	@Override
	void delete() {
		LovePirates.world.destroyBody(body);
		
	}

	@Override
	public boolean handlePreCollide(Contact contact) {
		return true;
	}

	@Override
	public void handleBeginContact(Contact contact) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void handlePostCollide(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
