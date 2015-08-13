package com.pirates.game;

import java.util.Random;

public class RandomShipGen {
	static Random rand = new Random();
	private RandomShipGen() {
		// TODO Auto-generated constructor stub
	}
	static Ship GenRandomShip(int level) {
		int mapSize = (int) Math.pow(2, LovePirates.MAPSIZE);
		float turnRate = (float) (Math.abs(rand.nextGaussian()/2+.5f));
		float length = (float) Math.abs(rand.nextGaussian()*level)+1;
		float width = (float) (Math.abs(length/4+rand.nextGaussian()*length/16));
		float drag = (float) Math.abs(rand.nextGaussian()/4+1);
		float power = (float) Math.abs(rand.nextGaussian()*length+level);
		int cannons = (int) Math.abs(rand.nextGaussian()*length*level)+level;
		float hp = (float) Math.abs((rand.nextGaussian()+1)*width*5+5*width);
		//					 									 x,y,turnrate,dragcoef,maxpower, length, width, cannons,hp
		Ship aiShip = ShipGenerator.genShip(rand.nextInt(mapSize),rand.nextInt(mapSize),turnRate,drag,power,length,width,cannons,hp);
		aiShip.setControler(new AiController(aiShip));
		while (LovePirates.map[(int) aiShip.getPos().x][(int) aiShip.getPos().y]>LovePirates.SEALEVEL) {
			aiShip.setPos(rand.nextInt(mapSize),rand.nextInt(mapSize));
		}
		return aiShip;
		
	}

}
