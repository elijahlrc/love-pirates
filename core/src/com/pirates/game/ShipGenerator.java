package com.pirates.game;


public class ShipGenerator {
	
	public ShipGenerator() {
	}
	//later add type of cannon two the genship function
	// cannons var represents cannnos per side
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, 
			float width, int cannons,int buckshotcannons, int numSlots, float hp, float maxhp, boolean boss, 
			int gunners,int maxGunners,int sailors, int maxSailors) {
		float massFactor = length*width;
		Ship returnShip;
		float turnMassFactor = (float) Math.pow(massFactor, 1.25);
		returnShip = new Ship(x,y,turnRate*turnMassFactor,drag,power*massFactor,length,width,hp, maxhp, gunners, maxGunners, sailors, maxSailors);
		if (boss) {
			returnShip.boss = true;
		}
		Slot[] slots = new Slot[numSlots*2];
		
		for (int i=0; i<numSlots; i += 1) {
			float yOffset = width/2;
			float xOffset = ((i+.5f)/numSlots)*length-length/2;
			int sizeOfSlot = 1;//set this later
			slots[2*i] = new Slot(xOffset,yOffset,sizeOfSlot,(float) (Math.PI/2f),FireingDirection.LEFT,returnShip);
			slots[2*i+1] = new Slot(xOffset,-yOffset,sizeOfSlot,(float) (3*Math.PI/2f),FireingDirection.RIGHT,returnShip);
		}
		if (cannons>numSlots*2) {
			System.out.println("too many cannons");
			System.out.println(cannons);
			System.out.println(numSlots);
		}
		returnShip.slots = slots;//this is pretty hacky, make setter. TODO

		for (int i=0; i<cannons; i += 1){
			Equipment gun;
			Slot slot = returnShip.findOpenSlot();
			if (slot != null){
				gun = new Cannon();
				slot.setContents(gun);
			}
		}
		for (int i=0; i<buckshotcannons; i += 1){
			Equipment gun;
			Slot slot = returnShip.findOpenSlot();
			if (slot != null){
				gun = new BuckshotCannon();
				slot.setContents(gun);
			}
		}
		returnShip.addCrew(1, "gunners");
		returnShip.addCrew(1, "sailors");
		return returnShip;
	}
	
	/**This constructor will create a fully crewed non boss ship*/
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  
			float length, float width, int cannons,int buckshotcannons, int numSlots, float hp, int maxSailors) {
		return genShip(x,y,turnRate,drag,power,length,width,cannons,buckshotcannons,numSlots,hp,hp, 
				cannons*5, cannons*5, maxSailors, maxSailors);

	}
	
	/**generates a non boss ship setting hp to max hp*/
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  
			float length, float width, int cannons,int buckshotcannons, int numSlots, float hp, 
			int gunners,int maxGunners,int sailors, int maxSailors) {
		return genShip(x,y,turnRate,drag,power,length,width,cannons,buckshotcannons,numSlots,hp,hp,
				gunners, maxGunners, sailors, maxSailors);
	}
	/**generates a non boss ship*/
	static Ship genShip(int x, int y, float turnRate, float drag, float power, 
			float length, float width, int cannons,int buckshotcannons, int numSlots, float hp, 
			float maxhp,int gunners,int maxGunners,int sailors, int maxSailors) {
		return genShip(x,y,turnRate,drag,power,length,width,cannons,buckshotcannons,numSlots,hp,maxhp,false, 
				gunners,maxGunners, sailors,  maxSailors);
	}

}
