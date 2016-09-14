package com.pirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/* 
 * Displays at the beginning of a new game
 * - Start button
 * - Options button
 * 
 */
public class MainMenuOverlay {
	private static CStage stage;
	private static Skin skin;
	private static TextButton startGameBtn;
	private static TextButton optionsBtn;
	private static TextButton exitButton;
	
	private final static int BUTTON_WIDTH = 300;
	private final static int BUTTON_HEIGHT = 30;
	private final static int OFFSET = BUTTON_HEIGHT;
	private final static int XOFFSET = -BUTTON_WIDTH/2;
	
	
	public MainMenuOverlay(Skin skin, CStage stage) {
		MainMenuOverlay.skin = skin;
		MainMenuOverlay.stage = stage;
	}
	//
	public static void makeMainMenu() {
		float xCenterPos = LovePirates.width / 2;
		float yCenterPos = LovePirates.height / 2;
		LovePirates.paused = true;
		// sets up start button
		startGameBtn = new TextButton("Restart Game", skin, "default");
		startGameBtn.setWidth(BUTTON_WIDTH);
		startGameBtn.setHeight(BUTTON_HEIGHT);
		startGameBtn.setPosition(xCenterPos + XOFFSET, yCenterPos + OFFSET);
		startGameBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("click make new world");
				LovePirates.makeNewWorld();
			}
		});
		
		// sets up exit game button
		exitButton = new TextButton("Exit Game", skin, "default");
		exitButton.setWidth(BUTTON_WIDTH);
		exitButton.setHeight(BUTTON_HEIGHT);
		exitButton.setPosition(xCenterPos + XOFFSET, yCenterPos - 3 * OFFSET);
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		// sets up options button
		optionsBtn = new TextButton("Options", skin, "default");
		optionsBtn.setWidth(BUTTON_WIDTH);
		optionsBtn.setHeight(BUTTON_HEIGHT);
		optionsBtn.setPosition(xCenterPos + XOFFSET, yCenterPos - OFFSET);
		optionsBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
			}
		});
		
		stage.addActor(startGameBtn);
	    stage.addActor(optionsBtn);
	    stage.addActor(exitButton);
	}
	public static void kill() {
		stage.clear();
		LovePirates.paused = false;
		
	}
	
}
