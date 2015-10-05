package com.pirates.game;

class BasicShipGen extends ShipGen{
	@Override
	Ship genShip(int level) {
		int cannons = 0;
		int buckshotcannons = 0;
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
		float meanPower = (float) (Math.log(length)*10);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
		
		float meanHp = width*length*level*4+2;
		float sdHp = .2f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
		if (rand.nextFloat()>.25) {
			float meanCannons = 2.5f*(level+length)-10;
			float sdCannons = .25f*meanCannons;
			cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 2);
		} else {
			float meanBuckcannons = 2.5f*(level+length)-10;
			float sdBuckcannons = .25f*meanBuckcannons;
			buckshotcannons = (int) Math.max(helperGauss(meanBuckcannons,sdBuckcannons), 2);
		}
		int gunners = (int) Math.max((cannons+buckshotcannons)*.75,
									  Math.min((cannons+buckshotcannons)*5,
											  (cannons+buckshotcannons)*helperGauss(2f,1f)));

		Ship aiShip;
		aiShip= ShipGenerator.genShip(rand.nextInt(mapSize),rand.nextInt(mapSize),turnRate,
				drag,power,length,width,cannons,buckshotcannons,cannons+buckshotcannons,hp,hp,false, gunners, cannons*5, sailors, maxSailors);
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
}
