package com.robbie.monkeywarriors.Sprites.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import com.robbie.monkeywarriors.Sprites.Monkey;
import com.robbie.monkeywarriors.Tools.B2WorldCreator;

import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/10.
 */
public class Bat extends Enemy{

    // What the bat is currently doing
    private enum State {SLEEPING, ATTACKING, DEAD};
    private State currentState;
    private State previousState;

    // Animations and animation timer
    private Texture tex;
    private float stateTimer;
    private Animation attackAnimation;
    private Animation deathAnimation;
    private TextureRegion sleepFrame;
    private boolean facingRight;

    // Variables relating to the bats vision
    private Fixture fix;
    private Vector2 p1;
    private Vector2 p2;
    private Vector2 collision;
    private Vector2 norm;
    private static float VISION_RANGE = 80/PPM;
    private boolean playerSeen;

    // Variables relating to the bats death
    private boolean setToDestroy;
    private boolean destroyed;

    private RayCastCallback callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            // If the ray hits a sensor, go through it
            if (fixture.isSensor()) {
                return -1;
            }
            // Otherwise, we collided with the thing
            collision = point.cpy();
            norm.set(normal).add(point);
            fix = fixture;
            // Return fraction, so that the ray ends here
            return fraction;
        }
    };

    public Bat(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        currentState = State.SLEEPING;
        previousState = State.SLEEPING;

        // SpriteSheet with bat sprites
        tex = new Texture("sprites/bat/bat.png");
        stateTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(tex, i*24, 0, 24, 24));
        }
        attackAnimation = new Animation(1/12f, frames);
        frames.clear();

        for (int i = 4; i < 9; i++) {
           frames.add(new TextureRegion(tex, i*24, 0, 24, 24));
        }
        deathAnimation = new Animation(1/12f, frames);
        frames.clear();

        sleepFrame = new TextureRegion(tex, 72, 0, 24, 24);

        facingRight = false;


        // Set initial values for the textures location, width and height
        setBounds(0, 0, 16/PPM, 16/PPM);
        setRegion(sleepFrame);

        p1 = new Vector2();
        p2 = new Vector2();
        collision = new Vector2();
        norm = new Vector2();
        playerSeen = false;

        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTimer += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            stateTimer = 0;
            destroyed = true;
        }
        else if (destroyed && stateTimer < deathAnimation.getAnimationDuration()) {
            setRegion(getFrame());
        }
        else if (!destroyed) {
            // p1 = bat's coordinates; p2 = player's coordinates
            p1 = new Vector2(b2body.getPosition().x, b2body.getPosition().y);
            p2 = new Vector2(screen.getPlayer().b2body.getPosition().x,
                    screen.getPlayer().b2body.getPosition().y);

            if (!playerSeen) {
                playerSeen = isPlayerVisible();
            }
            if (playerSeen) {
                attack(dt);
            }

            // Move the TextureRegion to where the b2body is
            setPosition(b2body.getPosition().x - getWidth() / 2 + 1 / PPM, b2body.getPosition().y - getHeight() / 2 + 1 / PPM);
            // Set which TextureRegion we are using
            setRegion(getFrame());

            if (currentState != previousState) {
                stateTimer = 0;
            }

            // Update previous state
            previousState = currentState;

            // Raycast to determine if the bat can see the player
            fix = null;
            world.rayCast(callback, p1, p2);
        }
    }

    /**
     * Return the next frame to be displayed
     * @return
     */
    public TextureRegion getFrame() {

        currentState = getState();
        TextureRegion region = null;

        // Get keyFrame corresponding to currentState
        switch(currentState) {
            case DEAD:
                region = deathAnimation.getKeyFrame(stateTimer);
                break;
            case ATTACKING:
                region = attackAnimation.getKeyFrame(stateTimer, true);
                break;
            case SLEEPING:
                region = sleepFrame;
                break;
        }
        // If soldier is walking left and the texture isn't facing left, flip it
        if ((b2body.getLinearVelocity().x < 0 || !facingRight) && region.isFlipX()) {
            region.flip(true, false);
            facingRight = false;
        }
        // Else if soldier is running right and the texture isn't facing right, flip it
        else if ((b2body.getLinearVelocity().x > 0 || facingRight) && !region.isFlipX()) {
            region.flip(true, false);
            facingRight = true;
        }

        //return our final adjusted frame
        return region;
    }

    public void draw(Batch batch) {
        if (!destroyed || stateTimer < deathAnimation.getAnimationDuration()) {
            super.draw(batch);
        }
    }

    /**
     * Returns the current state of the egg
     * @return
     */
    public State getState(){
        if (setToDestroy || destroyed) {
            return State.DEAD;
        }
        else if (playerSeen) {
            return State.ATTACKING;
        }
        else {
            return State.SLEEPING;
        }
    }

    /**
     * Define the enemy in Box2D
      */
    public void defineEnemy() {
            BodyDef bdef = new BodyDef();
            bdef.position.set(getX(), getY());
            bdef.type = BodyDef.BodyType.DynamicBody;
            bdef.gravityScale = 0;
            b2body = world.createBody(bdef);

            FixtureDef fdef = new FixtureDef();
            CircleShape shape = new CircleShape();
            shape.setRadius(3.5f / PPM);
            fdef.filter.categoryBits = BAT_BIT;
            fdef.filter.maskBits = MONKEY_BIT | GROUND_BIT | LAVA_BIT | SOLDIER_BIT |BULLET_BIT;
            fdef.shape = shape;
            b2body.createFixture(fdef).setUserData(this);
    }

    private boolean isPlayerVisible() {
        return fix != null && fix.getUserData() instanceof Monkey
                && p1.dst(p2) < VISION_RANGE;
    }

    private void attack(float dt) {
        // So that the vision line can be drawn to the screen
        screen.addRay(p1, collision);

        // Apply a downwards force initially, like the bat is pushing off the roof
        if (previousState != State.ATTACKING) {
            b2body.applyLinearImpulse(new Vector2(0, -0.4f), b2body.getWorldCenter(), true);
        }
        else {
            Vector2 force = new Vector2(0, 0);
            float xDiff = p2.x - p1.x;
            float yDiff = p2.y - p1.y;
            if ((xDiff < 0 && b2body.getLinearVelocity().x > -0.5)
                    || (xDiff > 0 && b2body.getLinearVelocity().x < 0.5)) {
                force.x = xDiff * 6f * dt;
            }
            if ((yDiff < 0 && b2body.getLinearVelocity().y > -0.5)
                    || (yDiff > 0 && b2body.getLinearVelocity().y < 0.5)) {
                force.y = yDiff * 6f * dt;
            }
            b2body.applyLinearImpulse(force, b2body.getWorldCenter(), true);
        }
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    public void dispose() {
        tex.dispose();
    }


}

