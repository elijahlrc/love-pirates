package com.pirates.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pirates.game.LovePirates;

public class DesktopLauncher {
	static int width;
	static int height;
	public static void main (String[] arg) {
		width = 1;
		height = 1;
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
		config.vSyncEnabled = true;
		config.width = width;
		config.height = height;
		config.foregroundFPS = 60;
		config.allowSoftwareMode = true;
		config.samples = 2;
		new LwjglApplication(new LovePirates(), config);
		//config.fullscreen = true;
	}
}
