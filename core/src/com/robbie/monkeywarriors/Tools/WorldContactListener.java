package com.robbie.monkeywarriors.Tools;

import com.badlogic.gdx.physics.box2d.*;
import com.robbie.monkeywarriors.Sprites.Enemies.Bat;
import com.robbie.monkeywarriors.Sprites.Enemies.Bullet;
import com.robbie.monkeywarriors.Sprites.Enemies.Soldier;
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
            case MARKER_BIT | SOLDIER_BIT:
                if (fixA.getFilterData().categoryBits == SOLDIER_BIT) {
                    ((Soldier) fixA.getUserData()).setWaiting();
                }
                else {
                    ((Soldier)fixB.getUserData()).setWaiting();
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
            case MONKEY_BIT | SOLDIER_BIT:
                if (fixA.getFilterData().categoryBits == MONKEY_BIT) {
                    Monkey player = (Monkey) fixA.getUserData();
                    player.kill();
                }
                else {
                    Monkey player = (Monkey) fixB.getUserData();
                    player.kill();
                }
                break;
            case MONKEY_BIT | BAT_BIT:
                if (fixA.getFilterData().categoryBits == MONKEY_BIT) {
                    Monkey player = (Monkey) fixA.getUserData();
                    player.kill();
                }
                else {
                    Monkey player = (Monkey) fixB.getUserData();
                    player.kill();
                }
                break;
            case BAT_BIT | GROUND_BIT:
                if (fixA.getFilterData().categoryBits == BAT_BIT) {
                    Bat bat = (Bat) fixA.getUserData();
                    bat.setToDestroy();
                }
                else {
                    Bat bat = (Bat) fixB.getUserData();
                    bat.setToDestroy();
                }
                break;
            case BULLET_BIT | MONKEY_BIT:
                if (fixA.getFilterData().categoryBits == MONKEY_BIT) {
                    Monkey monkey = (Monkey) fixA.getUserData();
                    monkey.kill();
                }
                else {
                    Monkey monkey = (Monkey) fixB.getUserData();
                    monkey.kill();
                }
                break;
            case BULLET_BIT | GROUND_BIT:
            case BULLET_BIT | LAVA_BIT:
                if (fixA.getFilterData().categoryBits == BULLET_BIT) {
                    Bullet bullet = (Bullet) fixA.getUserData();
                    bullet.setToDestroy();
                }
                else {
                    Bullet bullet = (Bullet) fixB.getUserData();
                    bullet.setToDestroy();
                }
                break;
            case BULLET_BIT | SOLDIER_BIT:
                if (fixA.getFilterData().categoryBits == BULLET_BIT) {
                    Bullet bullet = (Bullet) fixA.getUserData();
                    bullet.setToDestroy();
                    Soldier soldier = (Soldier)fixB.getUserData();
                    soldier.setToDestroy();
                }
                else {
                    Bullet bullet = (Bullet) fixB.getUserData();
                    bullet.setToDestroy();
                    Soldier soldier = (Soldier)fixA.getUserData();
                    soldier.setToDestroy();
                }
                break;
            case BULLET_BIT | BAT_BIT:
                if (fixA.getFilterData().categoryBits == BULLET_BIT) {
                    Bullet bullet = (Bullet) fixA.getUserData();
                    bullet.setToDestroy();
                    Bat bat = (Bat)fixB.getUserData();
                    bat.setToDestroy();
                }
                else {
                    Bullet bullet = (Bullet) fixB.getUserData();
                    bullet.setToDestroy();
                    Bat bat = (Bat)fixA.getUserData();
                    bat.setToDestroy();
                }
                break;
            case BAT_BIT | SOLDIER_BIT:
                if (fixA.getFilterData().categoryBits == BAT_BIT) {
                    Bat bat = (Bat) fixA.getUserData();
                    bat.setToDestroy();
                    Soldier soldier = (Soldier)fixB.getUserData();
                    soldier.setToDestroy();
                }
                else {
                    Bat bat = (Bat) fixB.getUserData();
                    bat.setToDestroy();
                    Soldier soldier = (Soldier)fixA.getUserData();
                    soldier.setToDestroy();
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
