package com.robbie.monkeywarriors.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/04.
 */
public class Monkey extends Sprite {

    public enum State {FALLING, JUMPING, DOUBLE_JUMPING, STANDING, RUNNING, DEAD}
    public State currentState;
    public State previousState;

    public enum Movement {LEFT, RIGHT, UP}
    public Array<Movement> movement;

    public World world;
    public Body b2body;

    private TextureRegion idleFrame;
    private Animation runAnimation;

    private float stateTimer;
    private final static float moveSpeed = 1.1f;
    private final static float jumpSpeed = 2.2f;
    private boolean runningRight;
    private boolean dead;
    public boolean canDoubleJump;

    public Monkey(PlayScreen screen, float x, float y) {
        setPosition(x, y);
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        dead = false;
        canDoubleJump = true;

        movement = new Array<Movement>();

        Array<TextureRegion> frames = new Array<TextureRegion>();

        Texture walkSheet = new Texture("sprites/monkey/monkey_walk.png");
        Texture idleSheet = new Texture("sprites/monkey/monkey_stand.png");

        // Create runAnimation animation
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(walkSheet, i*32, 0, 32, 32));
        }
        runAnimation = new Animation(1/12f, frames);

        frames.clear();

        // Create idleFrame frame
        idleFrame = new TextureRegion(idleSheet, 0, 0, 32, 32);

        defineMonkey();

        // Set initial values for the textures location, width and height
        setBounds(0, 0, 24/PPM, 24/PPM);
        setRegion(idleFrame);
    }

    public void update(float dt) {
        // Get monkeys current state
        currentState = getState();

        handleMovement(dt);

        setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2 + 2f/PPM);
        setRegion(getFrame(dt));
    }

    public void handleMovement(float dt) {
        Vector2 vel = b2body.getLinearVelocity();
        float desiredVel = 0;
        for (int i = 0; i < movement.size; i++) {
            if (movement.get(i) == Movement.LEFT) {
                desiredVel -= moveSpeed;
            }
            if (movement.get(i) == Movement.RIGHT) {
                desiredVel += moveSpeed;
            }
            if (movement.get(i) == Movement.UP) {
                jump(dt);
            }
        }
        float velChange = desiredVel - vel.x;
        float force = b2body.getMass() * velChange / dt; // f = mv/t
        b2body.applyForceToCenter(new Vector2(force, 0), true);
    }

    public void jump(float dt) {
        Vector2 vel = b2body.getLinearVelocity();
        float desiredVel = 0;
        // If he is jumping from on the ground
        if (currentState == State.STANDING || currentState == State.RUNNING) {
            desiredVel += jumpSpeed;
            previousState = currentState;
            currentState = State.JUMPING;
            float velChange = desiredVel - vel.y;
            float force = b2body.getMass() * velChange / dt; // f = mv/t
            b2body.applyForceToCenter(new Vector2(0, force), true);
        }
        // If he is jumping from in the air
        else if (currentState == State.JUMPING || (currentState == State.FALLING && canDoubleJump)) {
            //b2body.applyForceToCenter(new Vector2(0, 3.5f), true);
            desiredVel += 2.75;
            previousState = currentState;
            currentState = State.DOUBLE_JUMPING;
            float velChange = desiredVel - vel.y;
            float force = b2body.getMass() * velChange / dt; // f = mv/t
            b2body.applyForceToCenter(new Vector2(0, force), true);
            canDoubleJump = false;
        }
    }

    public TextureRegion getFrame(float dt) {

        TextureRegion region;

        // Get keyFrame corresponding to currentState
        switch(currentState) {
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case JUMPING:
            case DOUBLE_JUMPING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            case DEAD:
            default:
                region = idleFrame;
                break;
        }

        // If monkey is running left and the texture isn't facing left, flip it
        if ((b2body.getLinearVelocity().x < 0.005 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        // Else if monkey is running right and the texture isn't facing right, flip it
        else if ((b2body.getLinearVelocity().x > 0.005 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }

    public State getState(){
        if(dead) {
            return State.DEAD;
        }
        else if ((b2body.getLinearVelocity().y > 0 && currentState == State.DOUBLE_JUMPING)
                || (b2body.getLinearVelocity().y < 0 && previousState == State.DOUBLE_JUMPING))  {
                return State.DOUBLE_JUMPING;
        }
        else if ((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING)
                || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))  {
            return State.JUMPING;
        }
        else if (b2body.getLinearVelocity().y < -0.01) {
            return State.FALLING;
        }
        else if(Math.abs(b2body.getLinearVelocity().x) >= 0.01)  {
            return State.RUNNING;
        }
        else {
            return State.STANDING;
        }
    }

    // Define the monkey in box2d
    public void defineMonkey() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);
        fdef.filter.categoryBits = MONKEY_BIT;
        fdef.filter.maskBits = GROUND_BIT | LAVA_BIT | SOLDIER_BIT | BAT_BIT | BULLET_BIT;
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 1f;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void kill() {
        dead = true;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public boolean isDead() {
        return dead;
    }
}
