package com.robbie.monkeywarriors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robbie.monkeywarriors.Screens.MainMenuScreen;

public class MonkeyWarriors extends Game {

	public SpriteBatch batch;

	public static final int V_WIDTH = 16*32;
	public static final int V_HEIGHT = 9*32;
	public static final float PPM = 100;

    //Box2D Collision Bits
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MONKEY_BIT = 2;
    public static final short LAVA_BIT = 4;
	public static final short SOLDIER_BIT = 8;
    public static final short MARKER_BIT = 16;
	public static final short BAT_BIT = 32;
	public static final short BULLET_BIT = 64;

	@Override
	public void create () {
		batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
        super.render();
	}
	
	@Override
	public void dispose () {
        super.dispose();
		batch.dispose();
	}

}
