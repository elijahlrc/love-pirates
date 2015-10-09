package com.pirates.game;

import java.util.Random;

abstract class ShipGen {
	abstract Ship genShip(int level,int x, int y);
	abstract Ship genShip(int level);
	static Random rand = new Random();
	
	static float helperGauss(float mean,float sd) {
		return (float) (rand.nextGaussian()*sd-sd+mean);
	}
}
