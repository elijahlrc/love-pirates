package com.pirates.game;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
class MyUtils {
	static int imageCount = 0;
	static void visuliseArray(double[][] a) {
		Pixmap toSave = new Pixmap(a.length, a.length, Pixmap.Format.RGB565);
		for (int i = 0; i<(a.length); i++) {
			for (int j = 0; j<(a.length); j++) {
				Color c = new Color();
				double r = a[i][j];
				if (r<.75) {
					r = 0;
				}
				c.set((float) r, (float) a[i][j]/2, (float) a[i][j]/3,1f);
				toSave.drawPixel(i,j,Color.rgba8888(c));
			}
		}
		String filename = "heightmap"+imageCount+".png";
		FileHandle handle = Gdx.files.local(filename);
		imageCount++;
		PixmapIO.writePNG(handle,toSave);
		toSave.dispose();
	}
}
