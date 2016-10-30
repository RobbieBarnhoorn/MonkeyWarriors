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
    private Texture name;
    private Texture buttons;
    private Texture hoverButtons;
    private Button nameButton;
    private Button newButton;
    private Button resumeButton;
    private Button creditsButton;
    private Button exitButton;

    private final float NAME_WIDTH= 140*13;
    private final float NAME_HEIGHT= 20*13;
    private final float BUTTON_WIDTH = 80*10;
    private final float BUTTON_HEIGHT = 20*10;

    public MainMenuScreen(MonkeyWarriors game) {
        this.game = game;
        cam = new OrthographicCamera();
        cam.setToOrtho(false);
        viewport = new FitViewport(MonkeyWarriors.V_WIDTH, MonkeyWarriors.V_HEIGHT, cam);
        //initially set our gamcam to be centered correctly at the start of of map
        cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        background = new TextureRegion(new Texture("menu/background1.png"));
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
        nameButton.update(game.batch);
        newButton.update(game.batch, mouseX, mouseY, clicked, delta);
        resumeButton.update(game.batch, mouseX, mouseY, clicked, delta);
        creditsButton.update(game.batch, mouseX, mouseY, clicked, delta);
        exitButton.update(game.batch, mouseX, mouseY, clicked, delta);
        game.batch.end();
    }


    private void initButtons() {
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        name = new Texture("menu/name.png");
        buttons = new Texture("menu/buttons.png");
        hoverButtons = new Texture("menu/hover_buttons.png");

        nameButton = new Button(new TextureRegion(name, 0, 0, 140, 20),
                new TextureRegion(name, 0, 0, 140, 20),
                width - 0.25f * NAME_WIDTH, height + 2.6f * BUTTON_HEIGHT, NAME_WIDTH, NAME_HEIGHT);

        newButton = new Button(new TextureRegion(buttons, 0, 0, 80, 20),
                new TextureRegion(hoverButtons, 0, 0, 80, 20),
                width + 0.05f * BUTTON_WIDTH, height + 1.6f * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);

        resumeButton = new Button(new TextureRegion(buttons, 80, 0, 80, 20),
                new TextureRegion(hoverButtons, 80, 0, 80, 20),
                width + 0.05f * BUTTON_WIDTH, height + 0.6f * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);

        creditsButton = new Button(new TextureRegion(buttons, 160, 0, 80, 20),
                new TextureRegion(hoverButtons, 160, 0, 80, 20),
                width + 0.05f * BUTTON_WIDTH, height + -0.4f * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);

        exitButton = new Button(new TextureRegion(buttons, 240, 0, 80, 20),
                new TextureRegion(hoverButtons, 240, 0, 80, 20),
                width + 0.05f * BUTTON_WIDTH, height - 1.4f * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);


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
                    game.music.dispose();
                    game.setCurrentLevel(1);
                }
            });
        }
        else if (b.getName().equals("resumeButton")) {
            b.setActionHandler(new ActionHandler() {
                @Override
                public void handleClick() {
                    dispose();
                    game.music.dispose();
                    game.setCurrentLevel(game.getCurrentLevel());
                }
            });
        }
        else if (b.getName().equals("creditsButton")) {
            b.setActionHandler(new ActionHandler() {
                @Override
                public void handleClick() {
                    game.setScreen(new CreditsScreen(game, MainMenuScreen.this));
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
        name.dispose();
        buttons.dispose();
        hoverButtons.dispose();
        background.getTexture().dispose();
    }

}
