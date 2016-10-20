package com.robbie.monkeywarriors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robbie.monkeywarriors.MonkeyWarriors;
import com.robbie.monkeywarriors.Scenes.Hud;
import com.robbie.monkeywarriors.Sprites.Enemies.Enemy;
import com.robbie.monkeywarriors.Sprites.Enemies.Soldier;
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

    // Variables for smooth panning of camera
    private final static float cameraSpeed = 0.015f;
    private final static float ispeed = 1.0f - cameraSpeed;

    private static ShapeRenderer debugRenderer = new ShapeRenderer();
    public Array<Vector2> p1Array;
    public Array<Vector2> p2Array;


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
        TmxMapLoader maploader = new TmxMapLoader();
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

        p1Array = new Array<Vector2>();
        p2Array = new Array<Vector2>();

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
        if(player.currentState != Monkey.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.movement.add(Monkey.Movement.UP);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.movement.add(Monkey.Movement.RIGHT);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.movement.add(Monkey.Movement.LEFT);
            }
        }
    }

    public void update(float dt) {

        // Clear the lines which show us vision
        p1Array.clear();
        p2Array.clear();

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

        Vector3 cameraPosition = new Vector3(gamecam.position);
        cameraPosition.scl(ispeed);
        Vector3 target = new Vector3(player.b2body.getPosition().x, player.b2body.getPosition().y - 30/PPM, 0);
        target.scl(cameraSpeed);
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

        game.batch.setProjectionMatrix(gamecam.combined);

        game.batch.begin();

        // Draw the player
        player.draw(game.batch);

        // Draw the enemies
        for (Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }

        game.batch.end();

        // Render our Box2DDebugLines
        //b2dr.render(world, gamecam.combined);

        // Render our vision lines
        /*for (int i = 0; i < p1Array.size; i++) {
            drawDebugLine(p1Array.get(i), p2Array.get(i), gamecam.combined);
        }*/

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

    public Monkey getPlayer() {
        return player;
    }

    public void addRay(Vector2 start, Vector2 end) {
        p1Array.add(start);
        p2Array.add(end);
    }

    public static void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
}
