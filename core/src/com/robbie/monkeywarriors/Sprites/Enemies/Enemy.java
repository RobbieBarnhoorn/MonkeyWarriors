package com.robbie.monkeywarriors.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.robbie.monkeywarriors.Screens.PlayScreen;

/**
 * Created by robbie on 2016/10/07.
 */
public abstract class Enemy extends Sprite {

    public enum State {PATROLLING, STATIONARY, ALERT, SEARCHING, SHOOTING, DEAD};
    public State currentState;
    public State previousState;

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    protected boolean facingRight;
    protected boolean dead;


    public Enemy(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(0, 0);
    }


    public abstract void defineEnemy();

    public abstract void update(float dt);

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public boolean isDead() {
        return dead;
    }

    public void setPatrolling(boolean patrolling) {
        if (patrolling)
            this.currentState = State.PATROLLING;
        else
            this.currentState = State.STATIONARY;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean facingRight() {
        return facingRight;
    }
}
