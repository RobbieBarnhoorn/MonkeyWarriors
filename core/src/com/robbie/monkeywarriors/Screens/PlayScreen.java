package com.robbie.monkeywarriors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robbie.monkeywarriors.MonkeyWarriors;
import com.robbie.monkeywarriors.Scenes.Hud;
import com.robbie.monkeywarriors.Sprites.Enemies.Enemy;
import com.robbie.monkeywarriors.Sprites.Monkey;
import com.robbie.monkeywarriors.Tools.B2WorldCreator;
import com.robbie.monkeywarriors.Tools.WorldContactListener;
import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/03.
 */
public class PlayScreen implements Screen {

    private MonkeyWarriors game;
    private OrthographicCamera gamecam;
    private Viewport gameport;
    private Hud hud;


    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private MapProperties mapProperties;
    private int mapWidth;
    private int mapHeight;
    private int tilePixelWidth;
    private int tilePixelHeight;
    private int mapPxWidth;
    private int mapPxHeight;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    // Sprites
    private Monkey player;

    public PlayScreen(MonkeyWarriors game) {
        this.game = game;

        // Camera that follows player throughout world
        gamecam = new OrthographicCamera();

        // FitViewport maintains virtual aspect ratio, despite screen dimensions
        gameport = new FitViewport(MonkeyWarriors.V_WIDTH/MonkeyWarriors.PPM,
                MonkeyWarriors.V_HEIGHT/MonkeyWarriors.PPM, gamecam);

        // Create the Heads Up Display for score/level timer/info
        hud = new Hud(game.batch);

        // Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("levels/level2.tmx");
        mapProperties = map.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
        mapPxWidth = mapWidth * tilePixelWidth;
        mapPxHeight = mapHeight * tilePixelHeight;
        renderer = new OrthogonalTiledMapRenderer(map, 1/MonkeyWarriors.PPM);

        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -9f), true);
        World.setVelocityThreshold(0);

        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = creator.getPlayer();

        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        // Exit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            System.exit(0);

        // Clear what he was doing last frame
        player.movement.clear();

        //control our player using immediate impulses
        if(player.currentFrameState != Monkey.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                player.movement.add(Monkey.Movement.UP);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.movement.add(Monkey.Movement.RIGHT);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.movement.add(Monkey.Movement.LEFT);
            }
        }
    }

    public void update(float dt) {

        // Handle user input
        handleInput(dt);

        // Take one step in the physics simulation
        world.step(1/60f, 6, 2);

        // Update the player
        player.update(dt);

        // Update the enemies
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
        }

        hud.update(dt);

        // Make our camera track our players position smoothly
        final float speed = 0.02f, ispeed=1.0f-speed;
        Vector3 cameraPosition = new Vector3(gamecam.position);
        cameraPosition.scl(ispeed);
        Vector3 target = new Vector3(player.b2body.getPosition().x, player.b2body.getPosition().y, 0);
        target.scl(speed);
        cameraPosition.add(target);

        // Don't allow the camera to show any of the black portion beyond the edge of the map
        if (cameraPosition.x < V_WIDTH/PPM/2) {
            cameraPosition.x = V_WIDTH/PPM/2;
        }
        else if (cameraPosition.x > mapPxWidth/PPM - V_WIDTH/PPM/2) {
            cameraPosition.x = mapPxWidth/PPM - V_WIDTH/PPM/2;
        }
        if (cameraPosition.y < V_HEIGHT/PPM/2) {
            cameraPosition.y = V_HEIGHT/PPM/2;
        }
        else if (cameraPosition.y > mapPxHeight/PPM - V_HEIGHT/PPM/2) {
            cameraPosition.y = mapPxHeight/PPM - V_HEIGHT/PPM/2;
        }
        gamecam.position.set(cameraPosition.x, cameraPosition.y, 0);

        // Update our gamecam with correct coordinates after changes
        gamecam.update();

        // Tell our renderer to draw only what our camera can see in our game world
        renderer.setView(gamecam);
    }

    @Override
    public void render(float dt) {
        // First update the game world before rendering it
        update(dt);

        // Clear the game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render our map
        renderer.render();

        // Render our Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);

        game.batch.begin();

        // Draw the player
        player.draw(game.batch);

        // Draw the enemies
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);

        game.batch.end();

        // Set our batch to now draw what the Hud camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (player.isDead()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update our game viewport
        gameport.update(width, height);
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
        //dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
    }
}
