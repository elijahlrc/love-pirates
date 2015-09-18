package com.pirates.game;

public class BossShip extends Ship {
	public BossShip(int x, int y, float turnrate, float dragcoef,
			float maxpower, float len, float wid, float hp, float maxhp, 
			int gunners,int maxGunners,int sailors, int maxSailors) {
		super(x, y, turnrate, dragcoef, maxpower, len, wid, hp, maxhp,
				gunners,maxGunners,sailors, maxSailors);
		
	}
	@Override
	public void delete() {
		super.delete();
		LovePirates.nextWorld();
	}

}
