package com.robbie.monkeywarriors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robbie.monkeywarriors.Screens.CreditsScreen;
import com.robbie.monkeywarriors.Screens.MainMenuScreen;
import com.robbie.monkeywarriors.Screens.PlayScreen;

import java.io.*;

public class MonkeyWarriors extends Game {

	public SpriteBatch batch;
	public Screen currentScreen;
	public Music music;

	public static final int V_WIDTH = 16*32;
	public static final int V_HEIGHT = 9*32;
	public static final float PPM = 100;
	private final int NUM_LEVELS = 3;

    //Box2D Collision Bits
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MONKEY_BIT = 2;
    public static final short LAVA_BIT = 4;
	public static final short SOLDIER_BIT = 8;
    public static final short MARKER_BIT = 16;
	public static final short BAT_BIT = 32;
	public static final short BULLET_BIT = 64;
	public static final short FINISH_BIT = 128;

	@Override
	public void create () {
		batch = new SpriteBatch();
		music = Gdx.audio.newMusic(Gdx.files.internal("music/temple_2.mp3"));
		music.setLooping(true);
		music.setVolume(0.3f);
		music.play();
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

	public void setCurrentLevel(int level) {
		if (level <= NUM_LEVELS) {
			try {
				PrintWriter pr = new PrintWriter(new FileWriter("levels/currentLevel.txt"));
				pr.print(level);
				pr.close();
			} catch (IOException e) {
				System.out.println("Problem with currentLevel.txt");
			}
			setScreen(new PlayScreen(this, level));
		}
		else {
			music = Gdx.audio.newMusic(Gdx.files.internal("music/temple_2.mp3"));
			music.setLooping(true);
			music.setVolume(0.3f);
			music.play();
			setScreen(new MainMenuScreen(this));
		}
	}

	public int getCurrentLevel() {
        int currentLevel = -1;
		try {
            BufferedReader br = new BufferedReader(new FileReader("levels/currentLevel.txt"));
            currentLevel = Integer.parseInt(br.readLine());
            br.close();
		} catch (IOException e) {System.out.println("Problem with currentLevel.txt");}
		return currentLevel;
	}

	@Override
	public void setScreen(Screen screen) {
		currentScreen = screen;
		super.setScreen(screen);
	}

}
