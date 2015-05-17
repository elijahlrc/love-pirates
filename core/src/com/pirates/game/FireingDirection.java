package com.pirates.game;
/** this enum represents the directions a ship can fire,
 * this is somewhat limited by the number of key inputs a player can easily use
 * though if the ai was cheat-y it could use more, probably wont though.
 * anyway, these are passed by controllers to ships.
 * @author Elijah
 *
 */
enum FireingDirection {
LEFT, RIGHT, FORWARD, BACKWARD
}
