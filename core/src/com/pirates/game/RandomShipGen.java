package com.pirates.game;

import java.util.Random;

public class RandomShipGen {
	static Random rand = new Random();
	static float helperGauss(float mean,float sd) {
		return (float) (rand.nextGaussian()*sd-sd+mean);
	}
	static Ship GenRandomShip(int level) {
		int mapSize = LovePirates.MAPSIZE;
		float turnRate = (float) (Math.abs(rand.nextGaussian()/2)+.5f);
		
		float meanLen = (float) Math.max(2, (2*Math.log(level)+1));
		float sdLen = meanLen*.25f;
		float length = (float) Math.max(1.5f, helperGauss(meanLen,sdLen));
		
		float meanWid = .375f*meanLen;
		float sdWid = .2f*meanWid;
		float width = (float) Math.max(.5f, helperGauss(meanWid,sdWid));
		
		
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(length)*3);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
		
		float meanHp = width*length*level+2;
		float sdHp = .1f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
		
		float meanCannons = 5*(level+length)-10;
		float sdCannons = .25f*meanCannons;
		int cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 2);
		
		Ship aiShip;
		aiShip= ShipGenerator.genShip(rand.nextInt(mapSize),rand.nextInt(mapSize),turnRate,drag,power,length,width,cannons,cannons,hp,hp,false);
		aiShip.setControler(new AiController(aiShip));
		while (LovePirates.map[(int) aiShip.getPos().x][(int) aiShip.getPos().y]>LovePirates.SEALEVEL) {
			aiShip.setPos(rand.nextInt(mapSize),rand.nextInt(mapSize));
		}
		return aiShip;
		
	}
	static Ship GenRandomBossShip(int level) {
		int mapSize = LovePirates.MAPSIZE;
		float turnRate = (float) (Math.abs(rand.nextGaussian()/2)+.5f);
		
		float meanLen = (float) Math.max(4, (2*Math.log(level)+2));
		float sdLen = meanLen*.25f;
		float length = (float) Math.max(3f, helperGauss(meanLen,sdLen));
		
		float meanWid = .375f*meanLen;
		float sdWid = .2f*meanWid;
		float width = (float) Math.max(.5f, helperGauss(meanWid,sdWid));
		
		
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(length)*3);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
		
		float meanHp = width*length*level+2;
		float sdHp = .1f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
		
		float meanCannons = 5*(level+length)-10;
		float sdCannons = .25f*meanCannons;
		int cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 2);
		
		Ship aiShip;
		aiShip = ShipGenerator.genShip(rand.nextInt(mapSize),rand.nextInt(mapSize),turnRate,drag,power,length,width,cannons,cannons,hp,hp,true);
		aiShip.setControler(new AiController(aiShip));
		while (LovePirates.map[(int) aiShip.getPos().x][(int) aiShip.getPos().y]>LovePirates.SEALEVEL) {
			aiShip.setPos(rand.nextInt(mapSize),rand.nextInt(mapSize));
		}
		return aiShip;
		
	}

}
