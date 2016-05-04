package com.pirates.game;

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
	
	private final static int BUTTON_WIDTH = 200;
	private final static int BUTTON_HEIGHT = 20;
	private final static int OFFSET = (int) (BUTTON_WIDTH / 1.5);
	
	
	public MainMenuOverlay(Skin skin, CStage stage) {
		MainMenuOverlay.skin = skin;
		MainMenuOverlay.stage = stage;
	}
	
	public static void makeMainMenu() {
		float xCenterPos = LovePirates.width / 2;
		float yCenterPos = LovePirates.height / 2;
		
		// sets up start button
		startGameBtn = new TextButton("Start Game", skin, "default");
		startGameBtn.setWidth(BUTTON_WIDTH);
		startGameBtn.setHeight(BUTTON_HEIGHT);
		startGameBtn.setPosition(xCenterPos - OFFSET, yCenterPos + OFFSET);
		startGameBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LovePirates.makeNewWorld();
			}
		});
		
		// sets up options button
		optionsBtn = new TextButton("Options", skin, "default");
		optionsBtn.setWidth(BUTTON_WIDTH);
		optionsBtn.setHeight(BUTTON_HEIGHT);
		optionsBtn.setPosition(xCenterPos - OFFSET, yCenterPos - OFFSET);
		optionsBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
			}
		});
		
		stage.addActor(startGameBtn);
	    stage.addActor(optionsBtn);
		
	}
	
}
