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
	static int[] partition(int sum, int num, int min) {
		int[] result = new int[num];
		for (int i = 0; i < num; ++i) {
			result[i] = min;
		}
		int remaining = sum - (num * min);
		for (int j = 0; j < remaining; ++j) {
			int index = rand.nextInt(num);
			result[index] += 1;
		}
		return result;
	}

	public float v(String variableName) {
		return variableMap.get(variableName.toLowerCase());
	}
}
