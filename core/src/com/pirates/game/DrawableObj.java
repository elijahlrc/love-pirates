/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
abstract class DrawableObj {
	abstract Vector2 getPos();
	abstract int getSpriteIndex();
	abstract float[] getSize();

}
