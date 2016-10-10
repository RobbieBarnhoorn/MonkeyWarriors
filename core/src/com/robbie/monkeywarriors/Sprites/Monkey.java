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
    public State currentFrameState;
    public State previousFrameState;
    public State previousState;

    public enum Movement {LEFT, RIGHT, UP, NONE}
    public Array<Movement> movement;

    public World world;
    public Body b2body;

    private TextureRegion standFrame;
    private TextureRegion jumpFrame;
    private TextureRegion doubleJumpFrame;
    private Animation runAnimation;

    private PlayScreen screen;
    private float stateTimer;
    private final static float moveSpeed = 1.1f;
    private final static float jumpSpeed = 2.4f;
    private boolean runningRight;
    private boolean dead;
    public boolean canDoubleJump;

    public Monkey(PlayScreen screen, float x, float y) {
        this.screen = screen;
        setPosition(x, y);
        this.world = screen.getWorld();
        currentFrameState = State.STANDING;
        previousFrameState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        dead = false;
        canDoubleJump = true;

        movement = new Array<Movement>();

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Create runAnimation animation
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(new Texture("sprites/monkey/monkey_walk.png"), i*32, 0, 32, 32));
        }
        runAnimation = new Animation(1/12f, frames);

        frames.clear();

        // Create standFrame frame
        standFrame = new TextureRegion(new Texture("sprites/monkey/monkey_stand.png"), 0, 0, 32, 32);

        jumpFrame = new TextureRegion(new Texture("sprites/monkey/monkey_walk.png"), 0, 0, 32, 32);

        defineMonkey();

        // Set initial values for the textures location, width and height
        setBounds(0, 0, 24/PPM, 24/PPM);
        setRegion(standFrame);
    }

    public void update(float dt) {
        // Get monkeys current state
        currentFrameState = getState();

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
        if (currentFrameState == State.STANDING || currentFrameState == State.RUNNING) {
            desiredVel += jumpSpeed;
            previousFrameState = currentFrameState;
            currentFrameState = State.JUMPING;
            float velChange = desiredVel - vel.y;
            float force = b2body.getMass() * velChange / dt; // f = mv/t
            b2body.applyForceToCenter(new Vector2(0, force), true);
        }
        // If he is jumping from in the air
        else if (currentFrameState == State.JUMPING || (currentFrameState == State.FALLING && canDoubleJump)) {
            //b2body.applyForceToCenter(new Vector2(0, 3.5f), true);
            desiredVel += 2.75;
            previousFrameState = currentFrameState;
            currentFrameState = State.DOUBLE_JUMPING;
            float velChange = desiredVel - vel.y;
            float force = b2body.getMass() * velChange / dt; // f = mv/t
            b2body.applyForceToCenter(new Vector2(0, force), true);
            canDoubleJump = false;
        }
    }

    public TextureRegion getFrame(float dt) {

        TextureRegion region;

        // Get keyFrame corresponding to currentFrameState
        switch(currentFrameState) {
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case JUMPING:
            case DOUBLE_JUMPING:
                region = standFrame;
                break;
            case STANDING:
            case DEAD:
            default:
                region = standFrame;
                break;
        }

        // If monkey is running left and the texture isn't facing left, flip it
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        // Else if monkey is running right and the texture isn't facing right, flip it
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentFrameState == previousFrameState ? stateTimer + dt : 0;
        //update previous state
        if (previousFrameState != currentFrameState) {
            previousState = previousFrameState;
        }
        previousFrameState = currentFrameState;
        //return our final adjusted frame
        return region;
    }

    public State getState(){
        if(dead) {
            return State.DEAD;
        }
        else if ((b2body.getLinearVelocity().y > 0 && currentFrameState == State.DOUBLE_JUMPING)
                || (b2body.getLinearVelocity().y < 0 && previousFrameState == State.DOUBLE_JUMPING))  {
                return State.DOUBLE_JUMPING;
        }
        else if ((b2body.getLinearVelocity().y > 0 && currentFrameState == State.JUMPING)
                || (b2body.getLinearVelocity().y < 0 && previousFrameState == State.JUMPING))  {
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
        fdef.filter.maskBits = GROUND_BIT | LAVA_BIT | ENEMY_BIT;
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

    public float getStateTimer() {
        return stateTimer;
    }

    public boolean isDead() {
        return dead;
    }
}
