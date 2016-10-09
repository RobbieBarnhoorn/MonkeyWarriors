package com.robbie.monkeywarriors.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import com.robbie.monkeywarriors.Sprites.Enemies.Enemy;
import com.robbie.monkeywarriors.Sprites.Enemies.Soldier;
import com.robbie.monkeywarriors.Sprites.Monkey;

import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/03.
 */
public class B2WorldCreator {

    private Monkey player;
    private Array<Soldier> soldiers;

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
        soldiers = new Array<Soldier>();

        createGround();
        createLava();
        createEnemies();
        createMarkers();
        createMonkey();
    }

    private void createGround() {

        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
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
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/PPM, (rect.getY() + rect.getHeight()/2)/PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/PPM, rect.getHeight()/2/PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = LAVA_BIT;
            fdef.filter.maskBits= MONKEY_BIT;
            body.createFixture(fdef);
        }
    }

    private void createEnemies() {
        createSoldiers();
    }

    private void createSoldiers() {
        soldiers = new Array<Soldier>();
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            Soldier soldier = new Soldier(screen, rect.getX()/PPM, rect.getY()/PPM);
            boolean patrolling = object.getProperties().get("Patrol").equals(true);
            boolean facingRight = object.getProperties().get("Direction").equals("Right");
            soldier.setPatrolling(patrolling);
            if (patrolling) {
                if (facingRight)
                    soldier.velocity.x = 0.3f;
                else {
                    soldier.velocity.x = -0.3f;
                }
            }
            soldier.setFacingRight(object.getProperties().get("Direction").equals("Right"));
            soldiers.add(soldier);
        }
    }

    private void createMarkers() {

        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/PPM, (rect.getY() + rect.getHeight()/2)/PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/PPM, rect.getHeight()/2/PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MARKER_BIT;
            fdef.filter.maskBits = ENEMY_BIT;
            body.createFixture(fdef);
        }
    }

    private void createMonkey() {
        MapObject object = map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class).get(0);
        Rectangle rect = ((RectangleMapObject)object).getRectangle();
        player = new Monkey(screen, rect.getX()/PPM, rect.getY()/PPM);
    }

    public Monkey getPlayer() {
        return player;
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(soldiers);
        return enemies;
    }
}