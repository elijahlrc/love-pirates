package com.pirates.game;

import java.util.Random;

public class DecorateLand {
	int arrayWidth;
	int arrayHeight;
	LandDecoration[][] decorationMap;
	float SEALEVEL;
	float SANDWIDTH;
	float GRASSWIDTH;
	float YGRASSWIDTH;
	float BGRASSWIDTH;
	static Random rand = new Random();
	public DecorateLand() {
	}
	static float helperGauss(float mean,float sd) {//also defined in ShipGen because im bad.
		return (float) (rand.nextGaussian()*sd-sd+mean);
	}
	LandDecoration[][] DecorateAMap(double[][] map) {
		arrayWidth = map.length;
		arrayHeight = map[0].length;
		decorationMap = new LandDecoration[arrayWidth][arrayHeight];
		SEALEVEL = LovePirates.SEALEVEL;
		SANDWIDTH =.03f;
		GRASSWIDTH = .065f;
		YGRASSWIDTH = .1f;
		BGRASSWIDTH = .15f;
		/*gonna fill array with indexes for various decorations
		provisional decorations:
		0 = nothing
		
		10-19 = trees
			10 = tree
			11 = shrub
			12 = mountanTree
		20-29 = rocks
		30-39 = seaweed
			30 = light seaweed
			31 = heavy seaweed
		*/
		for (int i = 0; i<3000; i++) {
			makeForest(map,decorationMap,80,1,rand.nextInt(arrayWidth),rand.nextInt(arrayHeight));
		}
		for (int i = 0; i<8000; i++) {
			makeSeaweed(map,decorationMap,(int) Math.abs(helperGauss(30, 10)),1,rand.nextInt(arrayWidth),rand.nextInt(arrayHeight));
		}
		return decorationMap;
	}
	void makeForest(double[][] map, LandDecoration[][] decorationMap, int size, float density, int xCenter, int yCenter) {
		float positionDelta = (float) Math.sqrt(size)/2;
		arrayWidth = map.length;
		arrayHeight = map[0].length;
		int xPos;
		int yPos;
		for (int i = 0; i<size; i++) {
			xPos = Math.min(arrayWidth-1, Math.max(0,(int) helperGauss(xCenter,positionDelta)));
			yPos = Math.min(arrayWidth-1, Math.max(0,(int) helperGauss(yCenter,positionDelta)));
			if(map[xPos][yPos] <= SEALEVEL && rand.nextFloat()<density) {//if a decoration allready placed here skip
				continue;
			}
			float xOffset = helperGauss(0,.2f);
			float yOffset = helperGauss(0,.2f);
			if (map[xPos][yPos] > SEALEVEL && map[xPos][yPos] < SEALEVEL + SANDWIDTH) {//if its on sand put a shrub most of the time
				if (rand.nextInt(2) != 0) {
					if (rand.nextInt(10) != 0) {
						decorationMap[xPos][yPos] = new LandDecoration(11,xOffset, yOffset);
					} else {
						decorationMap[xPos][yPos] = new LandDecoration(10,xOffset, yOffset);;
					}
				}
			} else if (map[xPos][yPos] > SEALEVEL + SANDWIDTH && map[xPos][yPos] < SEALEVEL + BGRASSWIDTH) {
				if (rand.nextInt(10) != 0) {
					decorationMap[xPos][yPos] = new LandDecoration(10,xOffset, yOffset);;
				} else if (rand.nextInt(3) != 0) {
					decorationMap[xPos][yPos] = new LandDecoration(11,xOffset, yOffset);;
				} else {
					decorationMap[xPos][yPos] = new LandDecoration(12,xOffset, yOffset);;
				}
			} else {
				if (rand.nextInt(10) != 0) {
					decorationMap[xPos][yPos] = new LandDecoration(12,xOffset, yOffset);;
				} else {
					decorationMap[xPos][yPos] = new LandDecoration(10,xOffset, yOffset);;
				}
			}
		}
	}
	void makeSeaweed(double[][] map, LandDecoration[][] decorationMap, int size, float density, int xCenter, int yCenter) {
		float positionDelta = (float) Math.sqrt(size)/4;
		arrayWidth = map.length;
		arrayHeight = map[0].length;
		int xPos;
		int yPos;
		for (int i = 0; i<size; i++) {
			float xOffset = helperGauss(0,.5f);
			float yOffset = helperGauss(0,.5f);
			xPos = Math.min(arrayWidth-1, Math.max(0,(int) helperGauss(xCenter,positionDelta)));
			yPos = Math.min(arrayWidth-1, Math.max(0,(int) helperGauss(yCenter,positionDelta)));
			if(!(decorationMap[xPos][yPos] == null || decorationMap[xPos][yPos].id == 30 || decorationMap[xPos][yPos].id == 31) || map[xPos][yPos] <= SEALEVEL - .2f || map[xPos][yPos] >= SEALEVEL && rand.nextFloat()<density) {//if a decoration allready placed here skip
				continue;
			}
			if (decorationMap[xPos][yPos] == null) {
				decorationMap[xPos][yPos] = new LandDecoration(30,xOffset, yOffset);
			} else if (decorationMap[xPos][yPos].id == 31) {
				decorationMap[xPos][yPos] = new LandDecoration(32, xOffset, yOffset);
			} else if (decorationMap[xPos][yPos].id == 30) {
				decorationMap[xPos][yPos] = new LandDecoration(31, xOffset, yOffset);
			}

		}
	}
	
}
