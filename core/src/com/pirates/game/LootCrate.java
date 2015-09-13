/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * @author Elijah
 *
 */
public class LootCrate extends Debries implements Collideable {
	String contents;
	int quantaty;
	/**
	 * 
	 */
	public LootCrate(float x, float y, String type, int amount) {
		super(x,y,true);
		contents = type;
		quantaty = amount;
		lootable = true;
	}
	@Override
	void tick() {
	}
	@Override
	public void handlePostCollide(Contact contact, ContactImpulse impulse) {
		Fixture f;
		f = contact.getFixtureA();
		if (f == fixture){
			f = contact.getFixtureB();
		}
		Object b = f.getUserData();
		if (b instanceof Ship){
			alive = false;
		}
		
	}
	@Override
	int getSpriteIndex() {
		return 4;
	}
}