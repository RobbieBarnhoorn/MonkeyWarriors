package com.robbie.monkeywarriors.Tools;

import com.badlogic.gdx.physics.box2d.*;
import com.robbie.monkeywarriors.Sprites.Enemies.Enemy;
import com.robbie.monkeywarriors.Sprites.Monkey;

import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/04.
 */
public class WorldContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // cDef tells us what collided with what
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Depending on what collided with what, handle the collision accordingly
        switch(cDef) {
            case MONKEY_BIT | LAVA_BIT:
                if (fixA.getFilterData().categoryBits == MONKEY_BIT) {
                    ((Monkey)fixA.getUserData()).kill();
                }
                else {
                    ((Monkey)fixB.getUserData()).kill();
                }
                break;
            case MARKER_BIT | ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == ENEMY_BIT) {
                    Enemy enemy = (Enemy) fixA.getUserData();
                    enemy.reverseVelocity(true, false);
                }
                else {
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case MONKEY_BIT | GROUND_BIT:
                if (fixA.getFilterData().categoryBits == MONKEY_BIT) {
                    Fixture player = fixA;
                    Fixture ground = fixB;
                    if (player.getBody().getPosition().y > ground.getBody().getPosition().y) {
                        ((Monkey)player.getUserData()).canDoubleJump = true;
                    }
                }
                else {
                    Fixture player = fixB;
                    Fixture ground = fixA;
                    if (player.getBody().getPosition().y > ground.getBody().getPosition().y) {
                        ((Monkey)player.getUserData()).canDoubleJump = true;
                    }
                }
                break;
            case MONKEY_BIT | ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MONKEY_BIT) {
                    Monkey player = (Monkey) fixA.getUserData();
                    Enemy enemy = (Enemy) fixB.getUserData();

                    player.kill();
                }
                else {
                    Monkey player = (Monkey) fixB.getUserData();
                    Enemy enemy = (Enemy) fixA.getUserData();

                    player.kill();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
