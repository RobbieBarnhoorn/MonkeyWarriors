package com.robbie.monkeywarriors.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robbie.monkeywarriors.MonkeyWarriors;

/**
 * Created by robbie on 2016/10/07.
 */
public class GameOverScreen implements Screen {

    private MonkeyWarriors game;
    private Viewport viewport;
    private Stage stage;
    private Music music;

    public GameOverScreen(MonkeyWarriors game) {
        this.game = game;
        viewport = new FitViewport(MonkeyWarriors.V_WIDTH,
                MonkeyWarriors.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, ((MonkeyWarriors) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("GAME OVER", font);
        Label playAgainLabel = new Label("Press R to persevere", font);
        Label escapeLabel = new Label("Press ESCAPE to admit defeat", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);
        table.row();
        table.add(escapeLabel).expandX().padTop(10f);

        stage.addActor(table);

        music = Gdx.audio.newMusic(Gdx.files.internal("music/game_over.mp3"));
        music.setLooping(false);
        music.setVolume(0.3f);
        music.play();
    }

    @Override
    public void show() {

    }

    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {
            game.setScreen(new PlayScreen(game));
            dispose();
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            System.exit(0);
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        stage.dispose();
        music.dispose();
    }
}
