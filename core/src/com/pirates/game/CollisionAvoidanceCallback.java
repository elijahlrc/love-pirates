/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

/**
 * @author Elijah
 *
 */
public class CollisionAvoidanceCallback implements RayCastCallback {

	/**
	 *
	 */
	Fixture f;
	float frac;
	Filter filter;
	AiController c;
	public CollisionAvoidanceCallback(AiController controller) {
		this.c = controller;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.RayCastCallback#reportRayFixture(com.badlogic.gdx.physics.box2d.Fixture, com.badlogic.gdx.math.Vector2, com.badlogic.gdx.math.Vector2, float)
	 * Called for each fixture found in the query. 
	 * You control how the ray cast proceeds by returning a float: 
	 * 		return -1: ignore this fixture and continue 
	 * 		return 0: terminate the ray cast 
	 * 		return fraction: clip the ray to this point 
	 * 		return 1: don't clip the ray and continue. 
	 * The Vector2 instances passed to the callback will be reused for future calls
	 * so make a copy of them!
	 * Copy-pasted from documentation for ease of reference.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		filter = fixture.getFilterData();
		if ((filter.categoryBits == LovePirates.LAND_MASK) || (filter.categoryBits == LovePirates.SHIP_MASK)){
			c.rayCastCatcher(point);
			return 1;
		}
		return -1;
	}

}
