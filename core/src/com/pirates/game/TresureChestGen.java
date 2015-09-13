package com.pirates.game;

import java.util.Random;

public class TresureChestGen {
	static Random rand = new Random();
	private static boolean pointfound;
	private static int size = LovePirates.map.length;
	public TresureChestGen() {
		// TODO Auto-generated constructor stub
	}
	private static boolean inSand(int x, int y, double[][] map) {
		return (map[x][y] >  LovePirates.SEALEVEL)&&(map[x][y] < LovePirates.SEALEVEL+.03);
		
	}
	private static boolean inSea(int x, int y, double[][] map) {
		return (map[x][y] <  LovePirates.SEALEVEL);
		
	}
	static TresureChest genChest(double[][] map) {
		pointfound = false;
		int[] point = {0,0};
		while (true) {
			while (!inSand(point[0], point[1], map)) {
				point[0] = rand.nextInt(size);
				point[1] = rand.nextInt(size);
			}
			int amount = (int) Math.ceil(Math.abs(rand.nextGaussian()));
			if (inSea(point[0], point[1]+1, map)) {
				return new TresureChest(point[0], point[1]+.5f, amount);
			} else if (inSea(point[0], point[1]-1, map)) {
				return new TresureChest(point[0], point[1]-.5f, amount);
			} else if (inSea(point[0]+1, point[1], map)) {
				return new TresureChest(point[0]+.5f, point[1], amount);
			} else if (inSea(point[0]-1, point[1], map)) {
				return new TresureChest(point[0]-.5f, point[1], amount);
			} else {
				point[0] = 0;
				point[1] = 0;
			}
		}
		
	}
}
