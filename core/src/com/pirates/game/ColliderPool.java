/**
 * 
 */
package com.pirates.game;
import java.util.ArrayDeque;
import java.util.HashSet;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
/**
 * @author Elijah
 *This class provides a method, createTerainCollider(int x, int y), for ships to create 
 *new terrain colliders (in order to stream colliders rather than loading all 2,000,000 of them
 *It creates a set number of colliders then once that number has been created, it moves an old 
 *collider when createTerainCollider is called rather than creating another one.
 */
public class ColliderPool {

	/**
	 * 
	 */
	private ArrayDeque<Body> pool;
	private int size;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Body body;
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
		fixtureDef.filter.categoryBits = LovePirates.LAND_MASK;
		fixtureDef.filter.maskBits = -1;
		//boxShape.dispose(); 
		//MEMORY LEEK??? above.
	}
	/**
	 * helper function to create unique id for each location
	 */
	private String pairToString(Integer x, Integer y) {
		String s = x.toString()+"/"+y.toString();
		return s;
	}
	/**
	 * main function does the main job of the class
	 * @param x
	 * @param y
	 */
	void createTerainCollider(int x, int y) {
		if (colliderLocations.contains(pairToString(x,y)) == false) {
			colliderLocations.add(pairToString(x,y));
			if (pool.size()<size){
				bodyDef.position.set(x,y);
				body = LovePirates.world.createBody(bodyDef);
				body.createFixture(fixtureDef);
				pool.addFirst(body);
			} else {
				//pool.addFirst(body);
				body = pool.removeLast();
				int delX = (int) body.getPosition().x;
				int delY = (int) body.getPosition().y;
				body.setTransform(x, y, 0);
				pool.addFirst(body);
				colliderLocations.remove(pairToString(delX, delY));
				//LovePirates.world.destroyBody(delBody);
				
			}
		}
	}

}
//world.destroyBody(body);