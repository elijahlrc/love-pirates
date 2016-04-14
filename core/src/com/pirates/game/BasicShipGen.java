package com.pirates.game;

class BasicShipGen extends ShipGen{
	
	Ship genShip(int level) {
		int mapSize = LovePirates.MAPSIZE;
		int x = rand.nextInt(mapSize);
		int y = rand.nextInt(mapSize);
		while (LovePirates.map[x][y]>LovePirates.SEALEVEL) {				
				x = rand.nextInt(mapSize);
				y = rand.nextInt(mapSize);

		}
		Ship aiShip = genShip(level,x,y);
		
		return aiShip;

	}
	@Override
	Ship genShip(int level,int x,int y) {
		int cannons = 0;
		int buckshotcannons = 0;
		float turnRate = (float) (Math.abs(helperGauss(1.75f,.3f)));
		
		float meanLen = (float) Math.max(2, (2*Math.log(level)+1));
		float sdLen = meanLen*.25f;
		float length = (float) Math.max(1.5f, helperGauss(meanLen,sdLen));
		
		int sailors = (int) Math.abs((length*5*helperGauss(1,.3f)));
		int maxSailors = sailors*2;
		
		float meanWid = .375f*meanLen;
		float sdWid = .2f*meanWid;
		float width = (float) Math.max(.5f, helperGauss(meanWid,sdWid));
		
		
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(length)*10);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
		
		float meanHp = width*length*level*4+5;
		float sdHp = .2f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
		if (rand.nextFloat()>.25) {
			float meanCannons = 2.5f*(level+length)-4;
			float sdCannons = .25f*meanCannons;
			cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 2);
		} else {
			float meanBuckcannons = 3*(level+length)-4;
			float sdBuckcannons = .25f*meanBuckcannons;
			buckshotcannons = (int) Math.max(helperGauss(meanBuckcannons,sdBuckcannons), 2);
		}
		int gunners = (int) Math.max((cannons+buckshotcannons)*helperGauss(2.5f, 1.5f),Math.ceil(buckshotcannons+cannons/2f));
		
		Ship aiShip;
		aiShip= ShipGenerator.genShip(x,y,turnRate,
				drag,power,length,width,cannons,buckshotcannons,cannons+buckshotcannons,hp,hp,false, gunners, (cannons+buckshotcannons)*5, sailors, maxSailors);
		aiShip.setControler(new AiController(aiShip));
		aiShip.getLoot("repair supplies", (int) Math.ceil(hp/10));
		return aiShip;
	}
}
