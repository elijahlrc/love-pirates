package com.pirates.game;


public class ShipGenerator {
	
	public ShipGenerator() {
	}
	//later add type of cannon two the genship function
	// cannons var represents cannnos per side
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, float width, int cannons, int numSlots, float hp, float maxhp, boolean boss) {
		float massFactor = length*width;
		Ship returnShip;
		float turnMassFactor = (float) Math.pow(massFactor, 1.25);
		if (boss) {
			returnShip = new BossShip(x,y,turnRate*turnMassFactor,drag,power*massFactor,length,width,hp, maxhp);
		} else {
			returnShip = new Ship(x,y,turnRate*turnMassFactor,drag,power*massFactor,length,width,hp, maxhp);
		}
		Slot[] slots = new Slot[numSlots*2];
		
		for (int i=0; i<numSlots; i += 1) {
			float yOffset = width/2;
			float xOffset = (((float) i)/numSlots)*length-length/2;
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
			Equipment gun = new Cannon();
			Slot slot = returnShip.findOpenSlot();
			if (slot != null){
				slot.setContents(gun);
			}
		}
		return returnShip;
	}
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, float width, int cannons, int numSlots, float hp) {
		return genShip(x,y,turnRate,drag,power,length,width,cannons,numSlots,hp,hp);
	}
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, float width, int cannons, int numSlots, float hp, float maxhp) {
		return genShip(x,y,turnRate,drag,power,length,width,cannons,numSlots,hp,maxhp,false);
	}

}
