package com.pirates.game;


public class ShipGenerator {
	
	public ShipGenerator() {
	}
	//later add type of cannon two the genship function
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, float width, int cannons, float hp) {
		float massFactor = length*width;
		float turnMassFactor = (float) Math.pow(massFactor, 1.25);
		float offset = -length/2f;
		float spacing = length/(float)cannons;
		Ship returnShip = new Ship(x,y,turnRate*turnMassFactor,drag,power*massFactor,length,width,hp);
		Slot[] slots = new Slot[2*cannons];
		
		for (int i=0; i<cannons; i++) {
			slots[i] = new Slot(offset+spacing*i, 0f, 1, (float) Math.PI*1.5f, FireingDirection.RIGHT, returnShip);
		}
		for (int i=cannons; i<2*cannons; i++) {
			slots[i] = new Slot((offset+spacing*(i-cannons)), 0f, 1, (float) (Math.PI*.5f), FireingDirection.LEFT, returnShip);
		}
		for (Slot slot : slots) {
			Cannon newgun = new Cannon();
			slot.setContents(newgun);
		}
		returnShip.slots = slots;
		return returnShip;
	}

}
