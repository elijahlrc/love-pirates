/**
 * 
 */
package com.pirates.game;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Elijah
 *
 */
interface DrawableObj {
	Vector2 getPos();
	int getSpriteIndex();
	float[] getSize();

}
