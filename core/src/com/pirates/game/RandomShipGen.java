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
		
		int sailors = (int) Math.abs((length*5*helperGauss(1,.3f)));
		int maxSailors = sailors*2;
		
		float meanWid = .375f*meanLen;
		float sdWid = .2f*meanWid;
		float width = (float) Math.max(.5f, helperGauss(meanWid,sdWid));
		
		
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(length)*6);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
		
		float meanHp = width*length*level+2;
		float sdHp = .1f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
		
		float meanCannons = 5*(level+length)-10;
		float sdCannons = .25f*meanCannons;
		int cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 2);
		int gunners = (int) Math.min(cannons*5, cannons*helperGauss(.5f,.15f));

		Ship aiShip;
		aiShip= ShipGenerator.genShip(rand.nextInt(mapSize),rand.nextInt(mapSize),turnRate,
				drag,power,length,width,cannons,cannons,hp,hp,false, gunners, cannons*5, sailors, maxSailors);
		aiShip.setControler(new AiController(aiShip));
		while (LovePirates.map[(int) aiShip.getPos().x][(int) aiShip.getPos().y]>LovePirates.SEALEVEL) {
			while (true) {
				aiShip.setPos(rand.nextInt(mapSize),rand.nextInt(mapSize));
				if (rand.nextFloat() < Math.pow(LovePirates.map[(int) aiShip.getPos().x][(int) aiShip.getPos().y],3)) {
					break;
				}
			}
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
		
		//basline of 10 salors per length
		int sailors = (int) Math.abs((length*10*helperGauss(1,.3f)));
		int maxSailors = sailors*2;
		
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(length)*10);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
		
		float meanHp = width*length*level+2;
		float sdHp = .1f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
		
		//max of 5 gunners per cannon
		float meanCannons = 5*(level+length)-10;
		float sdCannons = .25f*meanCannons;
		int cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 4);
		int gunners = (int) Math.max(Math.min(cannons*5, cannons*helperGauss(.5f,.3f)),2);

		
		Ship aiShip;
		aiShip = ShipGenerator.genShip(rand.nextInt(mapSize),rand.nextInt(mapSize),turnRate,drag,power,
				length,width,cannons,cannons,hp,hp,true,gunners, cannons*5, sailors, maxSailors);
		aiShip.setControler(new AiController(aiShip));
		while (LovePirates.map[(int) aiShip.getPos().x][(int) aiShip.getPos().y]>LovePirates.SEALEVEL) {
			aiShip.setPos(rand.nextInt(mapSize),rand.nextInt(mapSize));
		}
		return aiShip;
		
	}

}
