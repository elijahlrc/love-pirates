package com.pirates.game;


public class ShipGenerator {
	
	public ShipGenerator() {
	}
	//later add type of cannon two the genship function
	// cannons var represents cannnos per side
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, float width, int cannons, float hp, float maxhp) {
		float massFactor = length*width;
		int numSlots = 4*cannons;//magic numb, should have a alternet genShip method that allows you to set the total slot numb
		float turnMassFactor = (float) Math.pow(massFactor, 1.25);
		float offset = -length/2f;
		float spacing = length/(float)numSlots;
		Ship returnShip = new Ship(x,y,turnRate*turnMassFactor,drag,power*massFactor,length,width,hp, maxhp);
		
		Slot[] slots = new Slot[numSlots];
		
		for (int i=0; i<numSlots; i += 2) {	
			slots[i] = new Slot(offset+spacing*i, 0f, 1, (float) Math.PI*1.5f, FireingDirection.RIGHT, returnShip);
			slots[i+1] = new Slot((offset+spacing*(i)), 0f, 1, (float) (Math.PI*.5f), FireingDirection.LEFT, returnShip);
		}
		int middleindex = numSlots/2;
		for (int i=0; i<numSlots; i += 2){
			if (i<=cannons) {
				Cannon newgun1 = new Cannon();
				Cannon newgun2 = new Cannon();
				if (i%2 == 0) {
					slots[i/2+middleindex].setContents(newgun1);
					slots[i/2+middleindex+1].setContents(newgun2);
				} else {
					slots[i/2-middleindex].setContents(newgun1);
					slots[i/2-middleindex+1].setContents(newgun2);
				}
			}
		}
		returnShip.slots = slots;//this is pretty hacky, make setter. TODO
		return returnShip;
	}
	static Ship genShip(int x, int y, float turnRate, float drag, float power,  float length, float width, int cannons, float hp) {
		return genShip(x,y,turnRate,drag,power,length,width,cannons,hp,hp*1.5f);
	}
}
