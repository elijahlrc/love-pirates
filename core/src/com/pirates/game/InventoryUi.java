package com.pirates.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

		
		
		Table inventoryList = new Table(skin);
		ScrollPane scroll = new ScrollPane(inventoryList, skin);
		for (int i = 0; i < 1000; i++) {
			Button UiButton = new TextButton("Text Button", skin);
			
			if (i != 0 && i%5 == 0) {
				inventoryList.row();
			}
			inventoryList.add(UiButton).width(100);
		}
		//table.center().top();
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
