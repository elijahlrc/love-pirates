package com.pirates.game;

public class InventorySlot {
	private Equipment e;
	public InventorySlot(Equipment equip) {
		e = equip;
	}
	public Equipment getContents() {
		return e;
	}
	public void clearContents() {
		e = null;
	}
	public boolean isEmpty() {
		return (e == null);
	}
}
