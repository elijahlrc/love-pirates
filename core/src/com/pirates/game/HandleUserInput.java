package com.pirates.game;

import com.badlogic.gdx.InputProcessor;
import java.util.HashSet;
class HandleUserInput implements InputProcessor {
	/**There will be lots of fields here which will
	 * allow the handle user input thing to be asked what user input is being gathered
	 * so some external thing will say
	 * if (inputProcessor.forward) {
	 * 		//go forward
	 * }
	 * Hope that makes sense
	 * Fields are below. Methods below will edit these fields.
	 */

	HashSet<Integer> keysDown;
	private static HandleUserInput instantiated= null;
	private HandleUserInput() {
		keysDown = new HashSet<Integer>();
	}
	//I'm loving this new singleton thing
	//bet i'm realize i'm over using it or doing it wrong later, lol.
	static HandleUserInput init() {
		if (instantiated == null)
			instantiated = new HandleUserInput();
		else {
			throw new RuntimeException("user input already instantiated");
		}
		return instantiated;
	}
	@Override
	public boolean keyDown(int keycode) {
		keysDown.add(keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		keysDown.remove(keycode);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
