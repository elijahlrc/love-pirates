package com.pirates.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
/**
 * This listens for collisions
 * objects which implement collidable handle their own collisions.
 * see ships and cannonballs for examples
 * @author Elijah
 *
 */
public class MyContactListener implements ContactListener {
	
	public MyContactListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture f = contact.getFixtureA();
		Object o = f.getUserData();
		if (o != null){
			if (o instanceof Collideable) {
				((Collideable) o).handleBeginContact(contact);
			}
		}

	}

	@Override
	public void endContact(Contact contact) {

		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture f = contact.getFixtureA();
		Object o = f.getUserData();
		boolean collide = true;
		if (o != null){
			if (o instanceof Collideable) {
				collide = ((Collideable) o).handlePreCollide(contact);
			}
		}
		contact.setEnabled(collide);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture f = contact.getFixtureA();
		Object o = f.getUserData();
		if (o != null){
			if (o instanceof Collideable) {
				((Collideable) o).handlePostCollide(contact, impulse);
			}
		}
	}

}
