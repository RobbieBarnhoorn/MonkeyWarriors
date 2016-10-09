package com.robbie.monkeywarriors.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.robbie.monkeywarriors.MonkeyWarriors;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
        config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
        config.fullscreen = true;
        config.vSyncEnabled = true;
		new LwjglApplication(new MonkeyWarriors(), config);
	}
}
