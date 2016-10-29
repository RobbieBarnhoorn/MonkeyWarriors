package com.robbie.monkeywarriors.Sprites.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import com.robbie.monkeywarriors.Sprites.Monkey;
import com.robbie.monkeywarriors.Tools.B2WorldCreator;

import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/07.
 */
public class Bandit extends Enemy {

    private static final float SPEED = 0.3f;
    private static final float WAIT_TIME = 2.4f;
    private static float VISION_RANGE = 175/PPM;

    // What the soldier is currently doing
    private enum State {PATROLLING, IDLING, TURNING, DRAWING, SHOOTING}
    private State currentState;
    private State previousState;

    // Animations and animation timer
    private float stateTimer;
    private Animation idleAnimation;
    private Animation walkAnimation;
    private Animation gunAnimation;
    private Animation shootAnimation;
    private boolean facingRight;
    private boolean patrolling;
    private boolean waiting;

    // Variables relating to the bandits vision
    private Fixture fix;
    private Vector2 p1;
    private Vector2 p2;
    private Vector2 collision;
    private boolean playerSeen;
    private float seenTime;

    // Array of bullets this soldier has shot
    private Array<Bullet> bullets;

    // Variables relating to the bats death
    private boolean setToDestroy;
    private boolean destroyed;

    // The object and method that gets called when a ray hits a fixture
    private RayCastCallback callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            // If the ray hits a sensor, go through it
            if (fixture.isSensor()) {
                return -1;
            }
            // Otherwise, we collided with the thing
            collision = point.cpy();
            fix = fixture;
            // Return fraction, so that the ray ends here
            return fraction;
        }
    };

    public Bandit(PlayScreen screen, float x, float y, boolean facingRight, boolean patrolling) {
        super(screen, x, y);
        this.facingRight = facingRight;
        this.patrolling = patrolling;
        stateTimer = 0;

        Texture idleSheet = new Texture("sprites/bandit/idle.png");
        Texture walkSheet = new Texture("sprites/bandit/walk.png");
        Texture gunSheet= new Texture("sprites/bandit/gun.png");
        Texture shootSheet = new Texture("sprites/bandit/shoot.png");

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Create an idling animation
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(idleSheet, i*60, 0, 60, 45));
        }
        idleAnimation = new Animation(1/4f, frames);
        frames.clear();

        // Create a walking animation
        for (int i = 0; i < 9; i++) {
            frames.add(new TextureRegion(walkSheet, i*60, 0, 60, 45));
        }
        walkAnimation = new Animation(1/10f, frames);
        frames.clear();

        // Create a pulling-out-gun animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(gunSheet, i*60, 0, 60, 45));
        }
        gunAnimation = new Animation(1/10f, frames);
        frames.clear();

        // Create a shooting animation
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(shootSheet, i*60, 0, 60, 45));
        }
        shootAnimation = new Animation(1/10f, frames);
        frames.clear();
        waiting = false;

        // Set initial values for the soldier_textures location, width and height
        setBounds(0, 0, 36/PPM, 36/PPM);

        p1 = new Vector2();
        p2 = new Vector2();
        collision = new Vector2();
        playerSeen = false;
        seenTime = 0;
        bullets = new Array<Bullet>();

        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        else if (!destroyed) {
            currentState = getState();

            // p1 = bat's coordinates; p2 = player's coordinates
            if (facingRight) p1.set(b2body.getPosition().x + 12/PPM, b2body.getPosition().y + 5/PPM);
            else p1.set(b2body.getPosition().x - 12/PPM, b2body.getPosition().y + 5/PPM);
            p2.set(screen.getPlayer().b2body.getPosition().x,
                    screen.getPlayer().b2body.getPosition().y);

            playerSeen = isPlayerVisible();
            if (playerSeen) {
                velocity.set(0, 0);
                seenTime += dt;
                if (seenTime > gunAnimation.getAnimationDuration()) {
                    // Shoot at the enemy with a unit direction vector
                    attack((p2.sub(p1)).setLength(1));
                    currentState = State.SHOOTING;
                }
            }
            else {
                if (currentState != State.DRAWING && currentState != State.SHOOTING) {
                    seenTime = 0;
                }
                if (patrolling && waiting) {
                    if (stateTimer < WAIT_TIME) {
                        velocity.set(0, 0);
                    }
                    else {
                        facingRight = !facingRight;
                        if (facingRight) velocity.set(SPEED, 0);
                        else velocity.set(-SPEED, 0);
                        waiting = false;
                    }
                }
                else if (patrolling){
                    if (facingRight) velocity.set(SPEED, 0);
                    else velocity.set(-SPEED, 0);
                }
            }

            b2body.setLinearVelocity(velocity);
            if (facingRight) {
                setPosition(b2body.getPosition().x - getWidth()/2 + 8/PPM,
                        b2body.getPosition().y - getHeight()/2);
            }
            else {
                setPosition(b2body.getPosition().x - getWidth()/2 - 8/PPM,
                        b2body.getPosition().y - getHeight()/2);
            }
            setRegion(getFrame(dt));

            // Raycast to determine if the soldier can see the player
            fix = null;
            world.rayCast(callback, p1, p2);
            updateBullets(dt);
        }
    }

    /**
     * Returns the frame that corresponds to currentState
     * Also flips the frame depending on the soldier's direction
     * @return
     */
    public TextureRegion getFrame(float dt) {
        TextureRegion region;
        switch(currentState) {
            case IDLING:
                region = idleAnimation.getKeyFrame(stateTimer, true);
                break;
            case PATROLLING:
                region = walkAnimation.getKeyFrame(stateTimer, true);
                break;
            case DRAWING:
                region = gunAnimation.getKeyFrame(stateTimer, false);
                break;
            case SHOOTING:
                region = shootAnimation.getKeyFrame(stateTimer, true);
                break;
            default:
                region = idleAnimation.getKeyFrame(stateTimer, true);
                break;
        }
        // If soldier is walking left and the texture isn't facing left, flip it
        if (facingRight && !region.isFlipX()) {
            region.flip(true, false);
        }
        // Else if soldier is walking right and the texture isn't facing right, flip it
        else if (!facingRight && region.isFlipX()) {
            region.flip(true, false);
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;

        //return our final adjusted frame
        return region;
    }

    /**
     * Return the current State of the soldier, based on his vision and velocity
     * @return
     */
    public State getState(){
        if (playerSeen) {
            if (seenTime <= gunAnimation.getAnimationDuration()) {
                return State.DRAWING;
            }
            else {
                return State.SHOOTING;
            }
        }
        else if(Math.abs(b2body.getLinearVelocity().x) >= 0.01) {
            return State.PATROLLING;
        }
        else {
            return State.IDLING;
        }

    }

    /**
     * Define the Bandit in Box2D
     */
    public void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6/PPM, 12/PPM);
        fdef.filter.categoryBits = SOLDIER_BIT;
        fdef.filter.maskBits = GROUND_BIT | LAVA_BIT | MONKEY_BIT | MARKER_BIT | BAT_BIT | BULLET_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    /**
     * Determine if the player is visible based on the fixture
     * returned by the RayCastCallback, distance, vision angle,
     * and direction
     * @return
     */
    private boolean isPlayerVisible() {
        float distance = p1.dst(p2);
        float angle = (float)Math.atan((p1.y - p2.y)/(p1.x - p2.x));
        return fix != null && fix.getUserData() instanceof Monkey
                && distance < VISION_RANGE
                && Math.abs(angle) < Math.PI/4
                && ((p1.x < p2.x && facingRight) || (p1.x > p2.x && !facingRight));
    }

    /**
     * Shoot in the player's direction
     * @param direction
     */
    private void attack(Vector2 direction) {
        // So that the vision line can be drawn to the screen
        screen.addRay(p1, collision);
        // If this is the first shot, shoot immediately
        if (previousState != State.SHOOTING || stateTimer > 0.4) {
            Vector2 bulletSpawn = new Vector2();
            // Add some element of randomness to the shooting patterns
            float offset = -20/PPM + ((float)Math.random()*40)/PPM;
            if (facingRight) {
                bulletSpawn.set(b2body.getPosition().x + (12/PPM), b2body.getPosition().y + 5 / PPM);
            }
            else {
                bulletSpawn.set(b2body.getPosition().x - 12 / PPM, b2body.getPosition().y + 5/PPM);
            }
            bullets.add(new Bullet(screen, this, bulletSpawn, direction.add(0, offset)));
            stateTimer = 0;
        }
    }

    /**
     * Remove a bullet from the bullets Array
     * @param bullet
     */
    public void removeBullet(Bullet bullet) {
        bullets.removeValue(bullet, true);
    }

    /**
     * Update the position of all the bullets
     * @param dt
     */
    public void updateBullets(float dt) {
        for (int i = 0; i < bullets.size; i++) {
            bullets.get(i).update(dt);
        }
    }

    /**
     * Draw the soldier to the screen
     * @param batch
     */
    public void draw(Batch batch) {
        drawBullets(batch);
        if (!destroyed) {
            super.draw(batch);
        }
    }

    /**
     * Draw the bullets to the screen
     * @param batch
     */
    public void drawBullets(Batch batch) {
        for (int i = 0; i < bullets.size; i++) {
            bullets.get(i).draw(batch);
        }
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    /**
     * Make the soldier wait for a short time before patrolling again
     */
    public void setWaiting() {
        waiting = true;
        stateTimer = 0;
    }

    public void dispose() {
    }

}
