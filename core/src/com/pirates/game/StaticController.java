package com.pirates.game;

import java.util.ArrayList;

public class StaticController implements Controller {
	Ship owner;
	public StaticController(Ship owner) {
		this.owner = owner;
	}

	@Override
	public Direction getTurn() {
		return null;
	}

	@Override
	public float getPower() {
		return 0;
	}

	@Override
	public ArrayList<FireingDirection> getFireDir() {
		return null;
	}

	@Override
	public void tick() {
	}

	@Override
	public void setProjectileSpeed(float projectileSpeed) {

	}

	@Override
	public float getProjectileSpeed() {
		return 0;
	}

	@Override
	public boolean getActive() {
		return false;
	}

	@Override
	public void setOwner(Ship s) {
		this.owner = s;
		
	}

}
