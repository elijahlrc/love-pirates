package com.pirates.game;

public class DeadShipGen {

	public DeadShipGen() {
		// TODO Auto-generated constructor stub
	}
	static Ship genShip(Ship s) {
		float turnRate = 0;
		
		float length = s.getSize()[0];
		float width = s.getSize()[1];
		int sailors = s.sailors;
		int maxSailors = sailors*2;
			
		float drag = 1.5f;//constant for now, see if this is useful to change later
		float power = 0;
			
		float hp = s.getMaxHp()/5;
			
		//max of 5 gunners per cannon
		int cannons = 0;
		int gunners = s.gunners;
		Ship aiShip;
		aiShip = ShipGenerator.genShip((int)s.getPos().x,(int)s.getPos().y,turnRate,drag,power,
				length,width,cannons,cannons,cannons,hp,hp,false,gunners, cannons*5, sailors, maxSailors);
		aiShip.setControler(new StaticController(aiShip));
		aiShip.setSpriteIndex(7);
		LovePirates.ships.add(aiShip);
		aiShip.setWreck(true);
		aiShip.setPos(s.getPos().x, s.getPos().y);
		System.out.println(s.getDir());
		aiShip.setDir(s.getDir());
		System.out.println(aiShip.getDir());
		aiShip.setSpriteSize(length*2f, width*2f);
		return aiShip;
	}

}
