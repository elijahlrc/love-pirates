/**
 * 
 */
package com.pirates.game;
import java.util.ArrayDeque;
import java.util.HashSet;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
/**
 * @author Elijah
 *
 */
public class ColliderPool {

	/**
	 * 
	 */
	private ArrayDeque<Body> pool;
	private int size;
	private Fixture fixture;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Body body;
	private Body delBody;
	private HashSet<String> colliderLocations;

	/**
	 * creates a pool for terrain colliders. 
	 * if size is lower than the minimum necessary terrain collidors then large problems
	 * will arise (missing colliders), so DONT DO THAT
	 * Make sure size is larger than it will ever need to be
	 * @param size
	 */
	public ColliderPool(int size) {
		this.size = size;
		
		colliderLocations = new HashSet<String>(size);
		
		pool = new ArrayDeque<Body>();
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		

		bodyDef.type = BodyDef.BodyType.StaticBody;
		PolygonShape boxShape = new PolygonShape();
		float[] verts = {0,0,0,1,1,1,1,0};
		boxShape.set(verts);
		fixtureDef.shape = boxShape;
		fixtureDef.filter.groupIndex = -2;
		//boxShape.dispose(); 
		//MEMORY LEEK??? above.
	}
	
	private String pairToString(Integer x, Integer y) {
		String s = x.toString()+"/"+y.toString();
		return s;
	}
	
	void createTerainCollider(int x, int y) {
		if (colliderLocations.contains(pairToString(x,y)) == false) {
			System.out.println("creating collider");
			colliderLocations.add(pairToString(x,y));
			bodyDef.position.set(x,y);
			body = LovePirates.world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			if (pool.size()<size){
				pool.addFirst(body);
			} else {
				pool.addFirst(body);
				delBody = pool.removeLast();
				int delX = (int) delBody.getPosition().x;
				int delY = (int) delBody.getPosition().y;
				colliderLocations.remove(pairToString(delX, delY));
				LovePirates.world.destroyBody(delBody);
				
			}
		}
	}

}
//world.destroyBody(body);