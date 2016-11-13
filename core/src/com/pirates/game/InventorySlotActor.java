package com.pirates.game;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class InventorySlotActor extends ImageButton {
    private InventorySlot slot;
    public InventorySlot getSlot() {
    	return slot;
    }
	public InventorySlotActor(Skin skin,InventorySlot slot) {
		super(skin);
		this.slot = slot;
	}
}
