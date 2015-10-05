package com.pirates.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Ui {
	private static Color c;
	private static Vector2 pos;
	private Ui() {
		c = new Color();
		c.r = 0;
		c.b = 0;
		c.g = .25f;
		c.a = 1f;
		pos = new Vector2();
	}
	static void init() {
		LovePirates.UI = new Ui();
	}
	static void drawShipIcon(Batch b, Ship ship, float x_pos, float y_pos) {
		float x = LovePirates.playerShip.getPos().x+x_pos;
		float y = LovePirates.playerShip.getPos().y+y_pos;
		float x_off;
		float y_off;
		c.a = 1f;
		TextureRegion t = LovePirates.textureRegions[5];
		b.draw(t,x-1,y-1,2,2);
		int index = ship.getSpriteIndex();
		float[] size = ship.getSize();
		t = LovePirates.textureRegions[index];
		float shipRotationR = ship.getDir();
		float shipRotation = (float) (shipRotationR * 360/(2*Math.PI));
		b.draw(t,x-size[0]/2,y-size[1]/2,
				   size[0]/2,size[1]/2,
				   size[0],size[1],
				   1f,1f,shipRotation,true);
		
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
		c.a = 0;
	}
}
