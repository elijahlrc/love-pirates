package com.pirates.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Ui {
	private static Color c;
	static CStage stage;
	private static Vector2 pos;
	private Skin skin;
	private static boolean centeredUi = true; //if true the Ui for cannons and turning is displayed overlaid on the ship, otherwise it appears in the bottom left
	private static int tabToggleTimer = 50;
	public Ui(Skin skin, CStage stage) {
		this.skin = skin;
		Ui.stage = stage;
		c = new Color(0, 0, .25f, 1f);
		pos = new Vector2();
	}


	//Draw UI/minimap
	static void drawMiniMap(Batch b){
		Vector2 playerPos = LovePirates.playerShip.getPos();
		float mapSizeInTiles = LovePirates.mapSpriteSize/LovePirates.TILESIZE;
		int xpos = (int) (LovePirates.width/(LovePirates.TILESIZE*4) - mapSizeInTiles);
		int ypos = (int) (LovePirates.height/(LovePirates.TILESIZE*4) - mapSizeInTiles);

		int x_off = (int) (playerPos.x-LovePirates.mapSpriteSize/2);
		int y_off = (int) (playerPos.y-LovePirates.mapSpriteSize/2);
		//int x_off = (int) (LovePirates.mapSpriteSize/LovePirates.TILESIZE+LovePirates.width/(LovePirates.TILESIZE*4f));
		//int y_off = (int)(LovePirates.mapSpriteSize/LovePirates.TILESIZE+LovePirates.height/(LovePirates.TILESIZE*4f));

		b.draw(LovePirates.mapTexture,
				playerPos.x+xpos,playerPos.y+ypos,
	            0,0,
	            LovePirates.mapSpriteSize,
	            LovePirates.mapSpriteSize,
	            1f/(LovePirates.TILESIZE),
	            1f/(LovePirates.TILESIZE),
	            0,
	            //0,0,
	            x_off,
	            y_off,
	            LovePirates.mapSpriteSize,
	            LovePirates.mapSpriteSize,
	            false, true);
		
		Vector2 shipPos;
		TextureRegion shipTexture = LovePirates.textureRegions[14];
		int shipHeight = shipTexture.getRegionHeight();
		int shipWidth = shipTexture.getRegionWidth();
		for (Ship ship : LovePirates.ships){
			shipPos = ship.getPos();
			if ((shipPos.x>playerPos.x - LovePirates.mapSpriteSize/2 && shipPos.x<playerPos.x + LovePirates.mapSpriteSize/2) &&
			    (shipPos.y>playerPos.y - LovePirates.mapSpriteSize/2 && shipPos.y<playerPos.y + LovePirates.mapSpriteSize/2)) {
				b.draw(shipTexture,
					shipPos.x/LovePirates.TILESIZE+playerPos.x -playerPos.x/LovePirates.TILESIZE + LovePirates.width/(LovePirates.TILESIZE*4f)-LovePirates.mapSpriteSize/(2*LovePirates.TILESIZE),
					shipPos.y/LovePirates.TILESIZE+playerPos.y -playerPos.y/LovePirates.TILESIZE + LovePirates.height/(LovePirates.TILESIZE*4f)-LovePirates.mapSpriteSize/(2*LovePirates.TILESIZE),
					.25f, .25f);
			}
		}
	}
	
	
	
	static void drawShipIcon(Batch b, float x_pos, float y_pos, boolean background) {
		Ship ship = LovePirates.playerShip;
		float x = ship.getPos().x+x_pos;
		float y = ship.getPos().y+y_pos;
		float x_off;
		float y_off;
		c.a = 1f;
		TextureRegion t;

		float shipRotationR = ship.getDir();
		if (background) {
			t = LovePirates.textureRegions[5];
			b.draw(t,x-1,y-1,2,2);
			int index = ship.getSpriteIndex();
			float[] size = ship.getSize();
			t = LovePirates.textureRegions[index];
			float shipRotation = (float) (shipRotationR * 180/(Math.PI));
			b.draw(t,x-size[0]/2,y-size[1]/2,
					   size[0]/2,size[1]/2,
					   size[0],size[1],
					   1f,1f,shipRotation,true);
		}
		c.g = 0;
		for (Slot slot : ship.slots) {
			t = LovePirates.textureRegions[5];
			float width = 10f/32f;
			if (slot.inslot != null && slot.inslot.iswepon()) {
				c.r = Math.min(Math.max(0, slot.inslot.countdown/5f),1);
				c.b = 1-Math.min(Math.max(0, slot.inslot.countdown/5f),1);

				x_off = (float) (slot.offset.x*Math.cos(shipRotationR)-slot.offset.y*Math.sin(shipRotationR));
				y_off = (float) (slot.offset.x*Math.sin(shipRotationR)+slot.offset.y*Math.cos(shipRotationR));
				b.setColor(c);
				b.draw(t,x+x_off-width/2f,y+y_off-width/2f,width,width);
			}
			pos.x = (float) (x+-2*Math.sin(shipRotationR));
			pos.y = (float) (y+2*Math.cos(shipRotationR));
			MyUtils.DrawText("Q", false, pos, 1);
			pos.x = (float) (x+2*Math.sin(shipRotationR));
			pos.y = (float) (y-2*Math.cos(shipRotationR));
			MyUtils.DrawText("E", false, pos, 1);
		}
		c = Color.WHITE.cpy();
		b.setColor(c);
	}
	public static void draw(SpriteBatch batch) {
		float x_pos; 
		float y_pos;
		tabToggleTimer -= 1;
		if (tabToggleTimer <0 && stage.keysDown.contains(Input.Keys.TAB)) {
			centeredUi = !centeredUi;
			tabToggleTimer = 20;
		}
		if (centeredUi) {
			
			x_pos = 0;
			y_pos = 0;
			drawShipIcon(batch,x_pos,y_pos,false);
		} else {
			x_pos = 2.5f-LovePirates.width/(LovePirates.TILESIZE*4);
			y_pos = 2-LovePirates.height/(LovePirates.TILESIZE*4);
			drawShipIcon(batch,x_pos,y_pos,true);
		}
		drawMiniMap(batch);
		
		String ui;
		ui = String.format("You have %d repair supplies %n" +
						   "%d gold", Math.round(LovePirates.playerShip.repairSupplies),LovePirates.playerShip.gold);
		
		MyUtils.DrawText(ui, true, LovePirates.UI_POS1,1);
		ui = String.format("You have %d sailors and " +
				   "%d cannonears", LovePirates.playerShip.sailors,LovePirates.playerShip.gunners);
		
		MyUtils.DrawText(ui, true, LovePirates.UI_POS2,1);
	}
}
