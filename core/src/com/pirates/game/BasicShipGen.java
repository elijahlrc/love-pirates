package com.pirates.game;

import java.util.Arrays;

class BasicShipGen extends ShipGen{
	public BasicShipGen(String dataFile) {
		super(dataFile);
	}
    public BasicShipGen() {
        this("basic_ship_data.txt");
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
		int[] points = ShipGen.partition(100 + (level * 10), 5, 5);
		int sizePoints = points[0];
		int turnPoints = points[1];
		int sailorsPoints = points[2];
		int powerPoints = points[3];
		int hpPoints = points[4];

		int cannons = 0;
		int buckshotcannons = 0;
		
		float meanLen = (float) Math.max(v("lenMeanMin"), (v("lenMult")*Math.log(sizePoints)+v("lenAdd")));
		float sdLen = meanLen*v("lenSdFrac");
		float length = clampGauss(meanLen, sdLen, v("lenMin"), v("lenMax"));

		float turnRate = (float)Math.log(turnPoints) * clampGauss(v("turnRateMean"), v("turnRateSd"), v("turnRateMin"), v("turnRateMax"));

		int sailors = (int) Math.abs((Math.log(sailorsPoints) * v("sailorMult") * helperGauss(v("sailorMean"), v("sailorSd"))));
		int maxSailors = sailors * (int) v("sailorMaxMult");

		float meanWid = v("widthLengthRatio") * meanLen;
		float sdWid = v("widthSdRatio") * meanWid;
		float width = clampGauss(meanWid, sdWid, v("widthMin"), v("widthMax"));


		float drag = v("drag"); //constant for now, see if this is useful to change later
		float meanPower = (float) (Math.log(powerPoints) * v("powerMult"));
		float sdPower = v("powerSdRatio") * meanPower;
		float power = clampGauss(meanPower, sdPower, v("powerMin"));

		float meanHp = (float)Math.log(hpPoints) * v("hpMult") + v("hpAdd");
		float sdHp = v("hpSdRatio") * meanHp;
		float hp = clampGauss(meanHp,sdHp, v("hpMin"));

		if (rand.nextFloat() > (1 - v("cannonProb"))) {
			float meanCannons = v("cannonMult") * (level+length) + v("cannonAdd");
			float sdCannons = v("cannonSdRatio") * meanCannons;
			cannons = (int) clampGauss(meanCannons, sdCannons, v("cannonMin"));
		} else {
			float meanBuckcannons = v("buckshotMult") * (level+length) + v("buckshotAdd");
			float sdBuckcannons = v("buckshotSdRatio") * meanBuckcannons;
			buckshotcannons = (int) clampGauss(meanBuckcannons, sdBuckcannons, v("buckshotMin"));
		}
		int totalCannons = cannons+buckshotcannons;
		int gunners = (int) Math.max(totalCannons * helperGauss(v("gunnerMultMean"), v("gunnerMultSd")),
				Math.ceil(totalCannons * v("gunnerMaxRatio")));

		Ship aiShip;
		aiShip= ShipGenerator.genShip(x,y,turnRate,
				drag,power,length,width,cannons,buckshotcannons,totalCannons,hp,hp,false,
				gunners, totalCannons * (int) v("gunnersPerCannon"), sailors, maxSailors);
		aiShip.setControler(new AiController(aiShip));
		aiShip.getLoot("repair supplies", (int) Math.ceil(hp * v("repairSuppliesRatio")));
		return aiShip;
	}
}
