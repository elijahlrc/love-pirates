package com.pirates.game;

import com.badlogic.gdx.math.Vector2;

public class AiTargetLoc implements Target {

	Vector2 pos;
	static Vector2 vel;
	AiTargetLoc(Vector2 target) {
		pos = target;
		vel = new Vector2(0,0);
	}
	@Override
	public Vector2 getPos() {
		return pos.cpy();
	}
	@Override
	public Vector2 getVel() {
		return vel.cpy();
		
	}

}
