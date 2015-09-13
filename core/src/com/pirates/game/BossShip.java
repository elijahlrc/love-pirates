package com.pirates.game;

public class BossShip extends Ship {
	public BossShip(int x, int y, float turnrate, float dragcoef,
			float maxpower, float len, float wid, float hp, float maxhp) {
		super(x, y, turnrate, dragcoef, maxpower, len, wid, hp, maxhp);
		
	}
	@Override
	public void delete() {
		super.delete();
		LovePirates.nextWorld();
	}

}
