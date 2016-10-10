package com.robbie.monkeywarriors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robbie.monkeywarriors.Screens.PlayScreen;

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
	public static final short ENEMY_BIT = 8;
    public static final short MARKER_BIT = 16;



	@Override
	public void create () {
		batch = new SpriteBatch();
        setScreen(new PlayScreen(this));
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
