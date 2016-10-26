package com.robbie.monkeywarriors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robbie.monkeywarriors.MonkeyWarriors;
import com.robbie.monkeywarriors.Scenes.Button;
import com.robbie.monkeywarriors.Tools.ActionHandler;

/**
 * Created by robbie on 2016/10/25.
 */
public class MainMenuScreen implements Screen {

    private MonkeyWarriors game;
    private OrthographicCamera cam;
    private Viewport viewport;

    private TextureRegion background;
    private Button newButton;
    private Button resumeButton;
    private Button creditsButton;
    private Button exitButton;

    private final float BUTTONWIDTH = 80*10;
    private final float BUTTONHEIGHT = 20*10;

    public MainMenuScreen(MonkeyWarriors game) {
        this.game = game;
        cam = new OrthographicCamera();
        cam.setToOrtho(false);
        viewport = new FitViewport(MonkeyWarriors.V_WIDTH, MonkeyWarriors.V_HEIGHT, cam);
        //initially set our gamcam to be centered correctly at the start of of map
        cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        initButtons(); // Initialize the buttons
    }

    @Override
    public void show() {

    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void render(float delta) {
        // Clear the game screen with black
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();// Set our batch to now draw what the Hud camera sees

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean clicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        newButton.update(game.batch, mouseX, mouseY, clicked);
        resumeButton.update(game.batch, mouseX, mouseY, clicked);
        creditsButton.update(game.batch, mouseX, mouseY, clicked);
        exitButton.update(game.batch, mouseX, mouseY, clicked);
        game.batch.end();
    }


    private void initButtons() {
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        Texture buttons = new Texture("menu/buttons.png");
        Texture hover_buttons = new Texture("menu/hover_buttons.png");

        newButton = new Button(new TextureRegion(buttons, 0, 0, 80, 20),
                new TextureRegion(hover_buttons, 0, 0, 80, 20),
                width + 0.1f*BUTTONWIDTH, height + 2.2f * BUTTONHEIGHT, BUTTONWIDTH, BUTTONHEIGHT);

        resumeButton = new Button(new TextureRegion(buttons, 80, 0, 80, 20),
                new TextureRegion(hover_buttons, 80, 0, 80, 20),
                width + 0.1f*BUTTONWIDTH, height + 1.2f * BUTTONHEIGHT, BUTTONWIDTH, BUTTONHEIGHT);

        creditsButton = new Button(new TextureRegion(buttons, 160, 0, 80, 20),
                new TextureRegion(hover_buttons, 160, 0, 80, 20),
                width + 0.1f*BUTTONWIDTH, height + 0.2f * BUTTONHEIGHT, BUTTONWIDTH, BUTTONHEIGHT);

        exitButton = new Button(new TextureRegion(buttons, 240, 0, 80, 20),
                new TextureRegion(hover_buttons, 240, 0, 80, 20),
                width + 0.1f*BUTTONWIDTH, height - 0.8f * BUTTONHEIGHT, BUTTONWIDTH, BUTTONHEIGHT);

        background = new TextureRegion(new Texture("menu/background1.png"));

        newButton.setName("newButton");
        resumeButton.setName("resumeButton");
        creditsButton.setName("creditsButton");
        exitButton.setName("exitButton");
        addActionHandler(newButton);
        addActionHandler(resumeButton);
        addActionHandler(creditsButton);
        addActionHandler(exitButton);
    }

    public void addActionHandler(Button b) {
        if (b.getName().equals("newButton")) {
            b.setActionHandler(new ActionHandler() {
                @Override
                public void handleClick() {
                    dispose();
                    game.setScreen(new PlayScreen(game));
                }
            });
        }
        else if (b.getName().equals("resumeButton")) {
            b.setActionHandler(new ActionHandler() {
                @Override
                public void handleClick() {
                    dispose();
                    game.setScreen(new PlayScreen(game));
                }
            });
        }
        else if (b.getName().equals("creditsButton")) {
            b.setActionHandler(new ActionHandler() {
                @Override
                public void handleClick() {
                    dispose();
                    Gdx.app.exit();
                }
            });
        }
        else if (b.getName().equals("exitButton")) {
            b.setActionHandler(new ActionHandler() {
                @Override
                public void handleClick() {
                    dispose();
                    Gdx.app.exit();
                }
            });
        }
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

    }

}
