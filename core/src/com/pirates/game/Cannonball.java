/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


/**
 * @author Elijah
 *NEEDS FULL REWRITE FOR BOX2D
 *TODO
 */
public class Cannonball extends Projectile {
	Body body;
	double lifetime;
	float size = .3f;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	/**
	 * @param pos
	 */
	public Cannonball(Vector2 position, Vector2 velocity, Ship owner) {
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		CircleShape boxShape = new CircleShape();
		fixtureDef.shape = boxShape;
		fixtureDef.filter.groupIndex = -2;
		
		bodyDef.position.set(position);
		
		body = LovePirates.world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		
		
		lifetime = 100;
		boxShape.dispose(); 
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#move()
	 */
	@Override
	protected void move() {
	}

	/* (non-Javadoc)
	 * @see com.pirates.game.Projectile#tick()
	 */
	@Override
	void tick() {
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
	public int getSpriteIndex() {
		//TODO no sprite exists yet :-(
		return 0;
	}

	@Override
	public float[] getSize() {
		return new float[] {size, size};
	}

}
