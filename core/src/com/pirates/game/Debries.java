package com.pirates.game;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Debries extends DrawableObj implements Collideable{
	private static BodyDef bodyDef = new BodyDef();
	private static FixtureDef fixtureDef = new FixtureDef();
	protected Body body;
	protected Fixture fixture;
	public boolean alive;
	float[] sizeArray;
	private int lifetime;
	boolean lootable;
	static Random rand= new Random();
	public Debries(float x, float y, boolean box) {
		if (box) {
			float size = (float) Math.abs(rand.nextGaussian()/2)+.5f;
			sizeArray = new float[] {size, size};
		} else {
			sizeArray = new float[] {(float) Math.abs(rand.nextGaussian())+.25f,(float) Math.abs(rand.nextGaussian())+.25f};
		}
		
		lifetime = (int) (rand.nextGaussian()*500+500);
		alive = true;
		lootable = false;
		bodyDef.position.set(x,y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.active = true;
		bodyDef.angularDamping = 3f;
		bodyDef.linearDamping = .8f;
		bodyDef.angle = (float) (rand.nextFloat()*2*Math.PI);
		Vector2 vel = new Vector2(1,1);
		vel.setLength((float) (rand.nextGaussian()*2+1));
		vel.setAngleRad((float) (rand.nextFloat()*2*Math.PI));
		body = LovePirates.world.createBody(bodyDef);
		body.setAwake(true);
		body.setLinearVelocity(vel);
		PolygonShape debriesShape = new PolygonShape();
		float[] verts = {0,	 			sizeArray[1]/2,
					sizeArray[0]/2,		 0, 
						0,	 			-sizeArray[1]/2, 
						-sizeArray[0]/2,	 0};
		debriesShape.set(verts);

		fixtureDef.shape = debriesShape;
		fixtureDef.density = .8f;
		fixtureDef.friction = .9f;
		fixtureDef.restitution = .2f;
		fixtureDef.filter.categoryBits = LovePirates.SHIP_MASK;
		fixtureDef.filter.maskBits = LovePirates.LAND_MASK | LovePirates.SHIP_MASK;
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
		//shapes must be disposed otherwise memory leak.
		debriesShape.dispose();
		
		
		
	}

	@Override
	Vector2 getPos() {
		return body.getPosition();
	}

	@Override
	int getSpriteIndex() {
		return 3;
	}

	@Override
	float[] getSize() {
		return sizeArray;
	}

	public float getRotation() {
		// TODO Auto-generated method stub
		return body.getAngle();
	}

	public void delete() {
		LovePirates.world.destroyBody(body);
		
	}

	@Override
	public boolean handlePreCollide(Contact contact) {
		Object b = contact.getFixtureB().getUserData();
		if (b instanceof Debries){
			return false;
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
		
	}

	void tick() {
		lifetime -= 1;
		if (lifetime <= 0) {
			alive = false;
		}
		
	}

}
