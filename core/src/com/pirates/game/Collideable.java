package com.pirates.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;

interface Collideable {
	boolean handlePreCollide(Contact contact);
	void handleBeginContact(Contact contact);
	void handlePostCollide(Contact contact, ContactImpulse impulse);
}
