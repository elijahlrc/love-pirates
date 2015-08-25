package com.pirates.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class TresureChest extends LootCrate {

	public TresureChest(float x, float y, int amount) {
		super(x, y, "treasure", amount);
		if (rand.nextFloat()>.5) {
			contents = "treasure";
		} else {
			contents = "cannons";
			quantaty = (int) Math.ceil(amount/4f);
		}
		fixture.setDensity(.01f);
		Shape s = fixture.getShape();
		if (s.getType() == Shape.Type.Polygon) {
			PolygonShape p = (PolygonShape) s;
			p.setAsBox(1, 1, new Vector2(0,0), 0);
		}
		body.setLinearVelocity(0, 0);
		body.setAngularVelocity(0);
	}
	@Override
	public boolean handlePreCollide(Contact contact) {
		Fixture f = contact.getFixtureB();
		Object b = f.getUserData();
		if (b instanceof Debries){
			return false;
		} else if (f.getFilterData().categoryBits == LovePirates.LAND_MASK) {
			return false;
		}
		return true;
	}

}
