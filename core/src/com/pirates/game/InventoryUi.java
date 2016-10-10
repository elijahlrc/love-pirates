package com.pirates.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Scaling;

public class InventoryUi {
	private static Color c;
	static CStage stage;
	private static Vector2 pos;
	private Table table;
	private Skin skin;
	InventoryUi (Skin skin, CStage stage) {
		this.stage = stage; this.skin = skin;
	}
	float xUiPos = LovePirates.width/5;
	float yUiPos = LovePirates.height/5;
	
	public void OpenInventory() {
		
		table = new Table(skin);
		table.setPosition(xUiPos, yUiPos);
		table.setWidth(LovePirates.width-xUiPos*2);
		table.setHeight(LovePirates.height-yUiPos*2);

		//temp placeholder texture
		Texture cannoballtext = new Texture("EquipmentIcons/cannonicon.png");
		Table inventoryList = new Table(skin);
		inventoryList.setTouchable(Touchable.childrenOnly);
		DragAndDrop lootIconDrag = new DragAndDrop();
		
		ScrollPane scroll = new ScrollPane(inventoryList, skin);
		scroll.setScrollingDisabled(true, false);
		scroll.setFadeScrollBars(false);
		

		
		for (int i = 0; i < 200; i++) {
			if (i != 0 && i%5 == 0) {
				inventoryList.row();
			}
			Table inventorySlot = new Table();
			lootIconDrag.addSource(new Source(inventorySlot){

				@Override
				public Payload dragStart(InputEvent event, float x, float y, int pointer) {
					Payload p = new Payload();
					p.setObject(inventorySlot);
					return p;
				}
				
			});
			lootIconDrag.addTarget(new DragAndDrop.Target(inventorySlot) {
				@Override
				public void drop(Source source, Payload payload, float x, float y, int pointer) {
					if (inventorySlot.getCells().size == 0) {
						inventorySlot.add((Table)payload.getObject());
					}
				}
				@Override
				public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
					return true;
				}
			});
			
			inventoryList.add(inventorySlot).width(80).height(80).pad(5);
			
			if (i < 10) {
				Image cannonIcon = new Image(cannoballtext);
				cannonIcon.setScaling(Scaling.fit);
				inventorySlot.add(cannonIcon).width(80).height(80);
			}
			
		}
		table.add(scroll);

		
		Table shipSlotTable = new Table();
		
		Image shipImage = new Image(LovePirates.textureRegions[0]);
		shipImage.setScaling(Scaling.fit);
		shipSlotTable.add(shipImage).prefWidth(300).prefHeight(800);//.prefHeight(500);
		table.add(shipSlotTable).expand();
		
		stage.addActor(table);
		table.setDebug(true,true);
	}
}
