package com.pirates.game;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class TresureChest extends LootCrate {

	public TresureChest(float x, float y, int amount) {
		super(x, y, "treasure", amount);
		if (rand.nextFloat()>.5) {
			contents = "treasure";
		} else {
			contents = "cannons";
			quantaty = (int) Math.ceil(amount/2f);
		}
		
		body.setLinearVelocity(0, 0);
		body.setAngularVelocity(0);
		body.setType(BodyType.StaticBody);//is this wrong.
	}

}
