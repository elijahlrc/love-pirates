package com.pirates.game;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
class DrawableText {
	String text;
	boolean relitiveToPlayer;
	Vector2 pos;
	int lifetime;
	DrawableText(String text, boolean RelitiveToPlayer, Vector2 pos,int lifetime) {
		this.text = text;
		this.relitiveToPlayer = RelitiveToPlayer;
		this.pos = pos;
		this.lifetime = lifetime;
	}
	
}
class MyUtils {
	static BitmapFont font = LovePirates.font;
	static ArrayList<Vector2[]> lineList = new ArrayList<Vector2[]>();
	static ArrayList<DrawableText> textList = new ArrayList<DrawableText>();;
	static ArrayList<DrawableText> textRemoveList = new ArrayList<DrawableText>();;
	static int textArrayListRefreshCount = 0;
	static float transperancy = .8f;
	static int imageCount = 0;
	static Texture visuliseArray(double[][] a, boolean colorLand) {
		Pixmap toSave = new Pixmap(a.length, a.length, Pixmap.Format.RGBA8888);
		if (!colorLand){
			for (int i = 0; i<(a.length); i++) {
				for (int j = 0; j<(a.length); j++) {
					Color c = new Color();
					float r = .2f;
					float g = .4f;
					float b = .9f;
					float t = (float) Math.min(1.5*Math.pow(a[i][j],1.5-.2f), 1);

					if (a[i][j]>LovePirates.SEALEVEL) {
						b = (float) a[i][j]*.5f;
						r = (float) a[i][j]*0.65f;
						g = (float) (a[i][j]*.65f);
						t = .9f;
					}
					
					c.set(r, g, b, t);
					toSave.drawPixel(i,j,Color.rgba8888(c));
				}
			}
		} else {
			for (int i = 0; i<(a.length); i++) {
				for (int j = 0; j<(a.length); j++) {
					Color c = new Color();
					float r = (float) a[i][j];
					c.set(r, r/2, r/3,transperancy);
					toSave.drawPixel(i,j,Color.rgba8888(c));
				}
			}
		}
		String filename = "heightmap"+imageCount+".png";
		FileHandle handle = Gdx.files.local(filename);
		imageCount++;
		PixmapIO.writePNG(handle,toSave);
		Texture t = new Texture(toSave,Pixmap.Format.RGBA8888,false);
		toSave.dispose();
		return t;
	}
	//seems to break rendering when you call this. Nothing but land renders.
	static void DrawDebugLine(Vector2 start, Vector2 end) {
		Vector2[] a = {start.cpy(), end.cpy()};
		lineList.add(a);
	}
		
	
	static void renderLines() {
		LovePirates.debugShapeRenderer.begin(ShapeType.Line);
		for (Vector2[] line: lineList) {
		    Gdx.gl.glLineWidth(1);
		    LovePirates.debugShapeRenderer.setProjectionMatrix(LovePirates.camera.combined);
		    LovePirates.debugShapeRenderer.setColor(Color.RED);
		    LovePirates.debugShapeRenderer.line(line[0], line[1]);
		}
		LovePirates.debugShapeRenderer.end();
		lineList.clear();
	}
	
	/**
	 * relative text used to display things like ui that stays in the same place on the screen
	 * relative to the players ship, and is specified with a vector relative to the player ship. 
	 * Non-relative text is specified absolutely, with a vector from the origin.
	 * If the text updates every frame, use 1 as the lifetime. (lifetime is specified in frames)
	 * @param text
	 * @param relative
	 * @param pos
	 * @param lifetime
	 */
	static void DrawText(String text, Boolean relative, Vector2 pos, int lifetime) {
		textList.add(new DrawableText(text, relative, pos.cpy(), lifetime));
	}
	
	
	static void renderText(SpriteBatch batch) {
		textArrayListRefreshCount += 1;
		for (DrawableText t: textList) {
			t.lifetime -= 1;
			if (t.relitiveToPlayer == true) {
				font.draw(batch, t.text, LovePirates.playerShip.getPos().x+t.pos.x, LovePirates.playerShip.getPos().y+t.pos.y);
			} else {
				font.draw(batch, t.text, t.pos.x, t.pos.y);
			}
			if (t.lifetime <= 0) {
				textRemoveList.add(t);
			}
		}
		//remove after iterating is done
		textList.removeAll(textRemoveList);
		textRemoveList.clear();
		if (textArrayListRefreshCount%100 == 0) {
			textList.trimToSize();

		}

	}
}