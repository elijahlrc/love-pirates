package com.pirates.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class Slot {
	Vector2 offset;
	int size;
	float dir;//in rad, ranging from 0 to 2pi
	FireingDirection side;
	Equipment inslot;
	Ship owner;
	/**
	 * 
	 * @param xOffset
	 * @param yOffset
	 * @param sizeOfSlot not yet implemented
	 * @param slotDir angle slot fires, in degrees
	 * @param fireSlot the key which fires this slot
	 */
	Slot(float xOffset, float yOffset, int sizeOfSlot,float slotDir, FireingDirection fireSlot,Ship ownedBy) {
		offset = new Vector2(xOffset,yOffset);
		size = sizeOfSlot;
		dir = slotDir;
		owner = ownedBy;
		side = fireSlot;
	}
	void setContents(Equipment e) {
		inslot = e;
	}
	void fire(ArrayList<FireingDirection> dirToFire) {
		inslot.tick();
		if ((dirToFire != null)&&(dirToFire.contains(side))) {
			if (inslot.iswepon()) {
				inslot.fire(dir, offset, owner);//how to handle this nicely?
			}
		}
	}
	void setEquip(Equipment e) {
		inslot = e;
	}
	public float getProjSpeed() {
		if (inslot.iswepon()) {
			return inslot.getProjSpeed();
		} else {
			return 0f;
		}
	}
	public float getProjLifetime() {
		if (inslot.iswepon()) {
			return inslot.getProjLifetime();
		} else {
			return 0f;
		}
	}
}