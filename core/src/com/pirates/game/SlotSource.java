package com.pirates.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;

public class SlotSource extends Source {
	private InventorySlot sourceSlot;
	public SlotSource(InventorySlotActor actor) {
		super(actor);
		this.sourceSlot = actor.getSlot(); 
	}
	@Override
	public Payload dragStart(InputEvent event, float x, float y, int pointer) {
		if (sourceSlot.isEmpty()) {
			return null;
		}
		Payload payload = new Payload();
		InventorySlot payloadSlot = new InventorySlot(sourceSlot.getContents());
		sourceSlot.clearContents();
		payload.setObject(payloadSlot);
		return payload;	
	}
	@Override
	public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, DragAndDrop.Target target) {
        InventorySlot payloadSlot = (InventorySlot) payload.getObject();
        if (target != null) {
        	InventorySlot targetSlot = ((InventorySlotActor) target.getActor()).getSlot();
        }
        
	}
}

