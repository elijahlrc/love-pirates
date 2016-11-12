package com.pirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundEffects {
    private HashMap<String, Sound> soundMap;
    private float volume;
    public SoundEffects(float volume) {
        soundMap = new HashMap<String, Sound>();
        load("cannon");
        load("buckshot");
        this.volume = volume;
    }

    public void load(String name) {
        load(name, "ogg");
    }

    public void load(String name, String format) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(name + "." + format));
        soundMap.put(name, sound);
    }

    public void play(String name) {
        soundMap.get(name).play(volume);
    }

    public void play(String name, float volume) {
        soundMap.get(name).play(volume);
    }

    public void play(String name, float volume, float pitch) {
        soundMap.get(name).play(volume, pitch, 0);
    }
}
