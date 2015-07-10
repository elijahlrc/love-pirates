package com.pirates.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pirates.game.LovePirates;

public class DesktopLauncher {
	static int width;
	static int height;
	public static void main (String[] arg) {
		width = 900;
		height = 900;
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new LovePirates(width, height), config);
		config.width = width;
		config.height = height;
		config.foregroundFPS = 60;
		//config.fullscreen = true;
	}
}
