package com.robbie.monkeywarriors.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import com.robbie.monkeywarriors.Sprites.Enemies.Bandit;
import com.robbie.monkeywarriors.Sprites.Enemies.Bat;
import com.robbie.monkeywarriors.Sprites.Enemies.Enemy;
import com.robbie.monkeywarriors.Sprites.Monkey;

import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/03.
 */
public class B2WorldCreator {

    private Monkey player;
    private Array<Bandit> soldiers;
    private Array<Bat> bats;

    private PlayScreen screen;
    private World world;
    private TiledMap map;
    private BodyDef bdef;
    private PolygonShape shape;
    private FixtureDef fdef;
    private Body body;

    public B2WorldCreator(PlayScreen screen) {
        this.screen = screen;
        world = screen.getWorld();
        map = screen.getMap();

        // Create body and fixture variables
        bdef = new BodyDef();
        shape = new PolygonShape();
        fdef = new FixtureDef();

        // Enemies array
        soldiers = new Array<Bandit>();

        createGround();
        createLava();
        createSoldiers();
        createMarkers();
        createMonkey();
        createBats();
    }

    private void createGround() {

        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/PPM,
                    (rect.getY() + rect.getHeight()/2)/PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/PPM, rect.getHeight()/2/PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = GROUND_BIT;
            body.createFixture(fdef);
        }
    }

    private void createLava() {
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/PPM, (rect.getY() + rect.getHeight()/2)/PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/PPM, rect.getHeight()/2/PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = LAVA_BIT;
            fdef.filter.maskBits = MONKEY_BIT | BAT_BIT | SOLDIER_BIT | BULLET_BIT;
            body.createFixture(fdef);
        }
    }

    private void createSoldiers() {
        soldiers = new Array<Bandit>();
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            boolean patrolling = object.getProperties().get("Patrol").equals(true);
            boolean facingRight = object.getProperties().get("Direction").equals("Right");
            Bandit bandit = new Bandit(screen, rect.getX()/PPM + (rect.getWidth()/2)/PPM, rect.getY()/PPM, facingRight, patrolling);
            soldiers.add(bandit);
        }
    }

    private void createMarkers() {
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/PPM, (rect.getY() + rect.getHeight()/2)/PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/PPM, rect.getHeight()/2/PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MARKER_BIT;
            fdef.filter.maskBits = SOLDIER_BIT;
            fdef.isSensor = true;
            body.createFixture(fdef);
        }
    }

    private void createMonkey() {
        MapObject object = map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class).get(0);
        Rectangle rect = ((RectangleMapObject)object).getRectangle();
        player = new Monkey(screen, (rect.getX() + rect.getWidth()/2)/PPM,
                (rect.getY() + rect.getHeight()/2)/PPM);
    }

    private void createBats() {
        bats = new Array<Bat>();
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            Bat bat = new Bat(screen, (rect.getX() + rect.getWidth()/2)/PPM,
                    (rect.getY() + rect.getHeight()/2)/PPM);
            bats.add(bat);
        }
    }

    public Monkey getPlayer() {
        return player;
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(soldiers);
        enemies.addAll(bats);
        return enemies;
    }

    /**
     * Returns the euclidean distance between two position vectors
     * @param p1
     * @param p2
     * @return
     */
}