package com.robbie.monkeywarriors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robbie.monkeywarriors.MonkeyWarriors;
import com.robbie.monkeywarriors.Scenes.Button;
import com.robbie.monkeywarriors.Tools.ActionHandler;

/**
 * Created by robbie on 2016/10/29.
 */
public class CreditsScreen implements Screen {

    private final float BUTTON_WIDTH = 80*10;
    private final float BUTTON_HEIGHT = 20*10;

    private Screen mainMenuScreen;

    private MonkeyWarriors game;
    private OrthographicCamera cam;
    private Viewport viewport;
    private Button backButton;
    private TextureRegion background;
    private Array<String> credits;
    private BitmapFont font;
    private Texture backButtonTex = new Texture("menu/back.png");

    private float time;


    public CreditsScreen(MonkeyWarriors game, MainMenuScreen mainMenuScreen) {
        this.game = game;
        this.mainMenuScreen = mainMenuScreen;
        cam = new OrthographicCamera();
        cam.setToOrtho(false);
        viewport = new FitViewport(MonkeyWarriors.V_WIDTH, MonkeyWarriors.V_HEIGHT, cam);
        //initially set our gamcam to be centered correctly at the start of of map
        cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        background = new TextureRegion(new Texture("menu/background1.png"));
        backButton = new Button(new TextureRegion(backButtonTex, 0, 0, 80, 20),
                new TextureRegion(new Texture("menu/back_hover.png"), 0, 0, 80, 20),
                width + 1f * BUTTON_WIDTH, height - 1.4f * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        addActionHandler(backButton);

        credits = new Array<String>();
        credits.add("Programming\n.............................\nRobbie Barnhoorn");
        credits.add("Original Music\n.............................\nMitchell Cuthbertson");
        credits.add("Original Artwork\n.............................\nRobbie Barnhoorn\nLisa Titley\nMichael Carter\nMitchell Cuthbertson");
        credits.add("Level Design\n.............................\nRobbie Barnhoorn");
        credits.add("Borrowed Artwork\n.............................\nSNK/Playmore");

        font = new BitmapFont();
        font.getData().setScale(1.5f, 1.5f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        time += delta;
        handleInput();
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean clicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        backButton.update(game.batch, mouseX, mouseY, clicked);
        font.draw(game.batch, credits.get(0), 1.7f*viewport.getWorldWidth(), time*50);
        font.draw(game.batch, credits.get(1), 1.7f*viewport.getWorldWidth(), time*50 - 220);
        font.draw(game.batch, credits.get(2), 1.7f*viewport.getWorldWidth(), time*50 - 440);
        font.draw(game.batch, credits.get(3), 1.7f*viewport.getWorldWidth(), time*50 - 750);
        font.draw(game.batch, credits.get(4), 1.7f*viewport.getWorldWidth(), time*50 - 980);
        game.batch.end();
    }

    public void addActionHandler(Button b) {
        b.setActionHandler(new ActionHandler() {
            @Override
            public void handleClick() {
                dispose();
                game.setScreen(mainMenuScreen);
            }
        });
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.getTexture().dispose();
        font.dispose();
        backButtonTex.dispose();
    }
}
