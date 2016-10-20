package com.robbie.monkeywarriors.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robbie.monkeywarriors.MonkeyWarriors;

/**
 * Created by robbie on 2016/10/18.
 */
public class MainMenuScreen implements Screen {

    private Game game;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(Game game) {
        this.game = game;
        viewport = new FitViewport(MonkeyWarriors.V_WIDTH, MonkeyWarriors.V_HEIGHT,
                new OrthographicCamera());
        //viewport.apply();
        stage = new Stage(viewport, ((MonkeyWarriors) game).batch);
        // Stage should control input
        Gdx.input.setInputProcessor(stage);
        createSkin();
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        //Create buttons
        TextButton newGameButton = new TextButton("New Game", skin);
        addButtonListener(newGameButton);
        table.addActor(newGameButton);

        TextButton resumeGameButton = new TextButton("Resume Game", skin);
        addButtonListener(resumeGameButton);

        TextButton exitButton = new TextButton("Exit", skin);
        addButtonListener(exitButton);

        //Add buttons to table
        table.add(newGameButton);
        table.row();
        table.add(resumeGameButton);
        table.row();
        table.add(exitButton);

        stage.addActor(table);
    }

    public void handleInput() {

    }

    @Override
    public void render(float delta) {
        handleInput();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
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
        skin.dispose();
    }

     private void createSkin() {
         //Create a font
         BitmapFont font = new BitmapFont();
         skin = new Skin();
         skin.add("default", font);

         //Create a texture
         Pixmap pixmap = new Pixmap((int) Gdx.graphics.getWidth() / 4, (int) Gdx.graphics.getHeight() / 10, Pixmap.Format.RGB888);
         pixmap.setColor(Color.WHITE);
         pixmap.fill();
         skin.add("background", new Texture(pixmap));

         //Create a button style
         TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
         textButtonStyle.up = skin.newDrawable("background", Color.GRAY);
         textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
         textButtonStyle.checked = skin.newDrawable("background", Color.DARK_GRAY);
         textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
         textButtonStyle.font = skin.getFont("default");
         skin.add("default", textButtonStyle);
     }

     public void addButtonListener(TextButton button) {
         if (button.getText().equals("New Game")) {
             button.addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen((MonkeyWarriors) game));
                 }
             });
         }
         else if (button.getText().equals("Resume Game")) {
             button.addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen((MonkeyWarriors) game));
                 }
             });
         }
         else if (button.getText().equals("Exit")) {
             Gdx.app.exit();
         }
     }

}

