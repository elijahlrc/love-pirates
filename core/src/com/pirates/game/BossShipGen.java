package com.pirates.game;

public class BossShipGen extends ShipGen {

	public BossShipGen() {
		super("boss_ship_data.txt");
	}
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
		float turnRate = (float) (Math.abs(rand.nextGaussian()/2)+.5f);
		
		float meanLen = (float) Math.max(2.5, (3*Math.log(level)+1));
		float sdLen = meanLen*.25f;
		float length = (float) Math.max(3f, helperGauss(meanLen,sdLen));
			
		float meanWid = .4f*meanLen;
		float sdWid = .2f*meanWid;
		float width = (float) Math.max(.5f, helperGauss(meanWid,sdWid));
			
		//basline of 10 salors per length
		int sailors = (int) Math.abs((length*10*helperGauss(1,.3f)));
		int maxSailors = sailors*2;
			
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(length)*10);
		float sdPower = .3f*meanPower;
		float power = (float) Math.max(3f, helperGauss(meanPower,sdPower));
			
		float meanHp = width*length*level*6+10;
		float sdHp = .1f*meanHp;
		float hp = (float) Math.max(helperGauss(meanHp,sdHp), 2);
			
			//max of 5 gunners per cannon
		float meanCannons = 2*(length);
		float sdCannons = .25f*meanCannons;
		int cannons = (int) Math.max(helperGauss(meanCannons,sdCannons), 3);
		int gunners = (int) Math.max(Math.min(cannons*5, cannons*helperGauss(.5f,.3f)),2);

			
		Ship aiShip;
		aiShip = ShipGenerator.genShip(x,y,turnRate,drag,power,
				length,width,cannons,cannons,cannons,hp,hp,true,gunners, cannons*5, sailors, maxSailors);
		aiShip.setControler(new AiController(aiShip));
		return aiShip;	
	}
}
