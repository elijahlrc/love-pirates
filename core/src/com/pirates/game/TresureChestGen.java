package com.pirates.game;

import java.util.Random;

public class TresureChestGen {
	static Random rand = new Random();
	private static boolean pointfound;
	private static int size = LovePirates.map.length;
	private static double[][] map = LovePirates.map;
	public TresureChestGen() {
		// TODO Auto-generated constructor stub
	}
	private static boolean inSand(int x, int y) {
		return (map[x][y] >  LovePirates.SEALEVEL)&&(map[x][y] <= LovePirates.SEALEVEL+.03);
		
	}
	private static boolean inSea(int x, int y) {
		return (map[x][y] <=  LovePirates.SEALEVEL);
		
	}
	static void genChest() {
		pointfound = false;
		int[] point = {0,0};
		while (pointfound == false) {
			while (!inSand(point[0], point[1])) {
				point[0] = rand.nextInt(size);
				point[1] = rand.nextInt(size);
			}
			if (inSea(point[0], point[1]+1)) {
				LovePirates.debries.add(new TresureChest(point[0], point[1]+.5f, 1));
				pointfound = true;
			} else if (inSea(point[0], point[1]-1)) {
				LovePirates.debries.add(new TresureChest(point[0], point[1]-.5f, 1));
				pointfound = true;
			} else if (inSea(point[0]+1, point[1])) {
				LovePirates.debries.add(new TresureChest(point[0]+.5f, point[1], 1));
				pointfound = true;
			} else if (inSea(point[0]-1, point[1]+1)) {
				LovePirates.debries.add(new TresureChest(point[0]+.5f, point[1], 1));
				pointfound = true;
			} else {
				point[0] = 0;
				point[1] = 0;
			}
		}
		
	}
}
