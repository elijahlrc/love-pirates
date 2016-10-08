package com.pirates.game;

import java.util.Map;
import java.util.Random;

abstract class ShipGen {
	private Map<String, Float> variableMap;
	public ShipGen(String fileName) {
		variableMap = CSV.readFile(fileName);
	}

	abstract Ship genShip(int level,int x, int y);
	abstract Ship genShip(int level);
	static Random rand = new Random();
	
	static float helperGauss(float mean,float sd) {
		return (float) (rand.nextGaussian()*sd-sd+mean);
	}
	static float clampGauss(float mean, float sd, float min) {
		return (float) Math.max(min, helperGauss(mean, sd));
	}

	public float v(String variableName) {
		return variableMap.get(variableName.toLowerCase());
	}
}
