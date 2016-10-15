package com.robbie.monkeywarriors.Sprites.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/07.
 */
public class Soldier extends Enemy {

    public enum State {PATROLLING, STATIONARY, ALERT, SEARCHING, SHOOTING, DEAD};
    public State currentState;
    public State previousState;

    private float stateTimer;
    private Texture tex;
    private Animation walkAnimation;
    private TextureRegion standFrame;
    protected boolean facingRight;

    public Soldier(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        currentState = State.STATIONARY;
        previousState = State.STATIONARY;

        tex = new Texture("sprites/soldier/soldier_walk.png");

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Create a walking animation
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(tex, i*100, 0, 100, 100));
        }
        walkAnimation = new Animation(1/5f, frames);

        frames.clear();

        // Create a standing frame
        standFrame = new TextureRegion(tex, 0, 0, 100, 100);

        // Set initial values for the textures location, width and height
        setBounds(0, 0, 36/PPM, 36/PPM);
        setRegion(standFrame);
    }


    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 3/PPM);
        setRegion(getFrame(dt));
        b2body.setLinearVelocity(velocity);
    }

    public TextureRegion getFrame(float dt) {

        // Get monkeys current state
        currentState = getState();

        TextureRegion region;

        // Get keyFrame corresponding to currentState
        switch(currentState) {
            case PATROLLING:
                region = walkAnimation.getKeyFrame(stateTimer, true);
                break;
            case STATIONARY:
            default:
                region = standFrame;
                break;
        }

        // If monkey is running left and the texture isn't facing left, flip it
        if ((b2body.getLinearVelocity().x < 0 || !facingRight) && !region.isFlipX()) {
            region.flip(true, false);
            facingRight = false;
        }
        // Else if monkey is running right and the texture isn't facing right, flip it
        else if ((b2body.getLinearVelocity().x > 0 || facingRight) && region.isFlipX()) {
            region.flip(true, false);
            facingRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }


    // Return the current state of the soldier
    public State getState(){
        if(b2body.getLinearVelocity().x != 0) {
            return State.PATROLLING;
        }
        //if none of these return then he must be standing
        else {
            return State.STATIONARY;
        }
    }

    // Define the enemy in box2d
    public void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / PPM);
        fdef.filter.categoryBits = SOLDIER_BIT;
        fdef.filter.maskBits = GROUND_BIT | LAVA_BIT | MONKEY_BIT | MARKER_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
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

    public void dispose() {
        tex.dispose();
    }

}
