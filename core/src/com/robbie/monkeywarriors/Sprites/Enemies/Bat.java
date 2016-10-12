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
 * Created by robbie on 2016/10/10.
 */
public class Bat extends Enemy{

    public enum State {SLEEPING, ATTACKING};
    public State currentState;
    public State previousState;

    private float stateTimer;
    private Animation attackAnimation;
    private TextureRegion sleepFrame;

    private boolean enemyInRange;

    public Bat(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        currentState = State.SLEEPING;
        previousState = State.SLEEPING;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Create a birthing animation
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(new Texture("sprites/bat/bat.png"), i*24, 0, 24, 24));
        }
        attackAnimation = new Animation(1/10f, frames);

        frames.clear();

        // Create a ready frame
        sleepFrame = new TextureRegion(new Texture("sprites/bat/bat.png"), 72, 0, 24, 24);

        // Set initial values for the textures location, width and height
        setBounds(0, 0, 16/PPM, 16/PPM);
        setRegion(sleepFrame);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth()/2 + 1/PPM, b2body.getPosition().y - getHeight()/2 + 1/PPM);
        setRegion(getFrame(dt));
        b2body.setLinearVelocity(velocity);
    }

    /**
     * Return the next frame to be displayed
     * @param dt
     * @return
     */
    public TextureRegion getFrame(float dt) {

        // Get monkeys current state
        currentState = getState();

        TextureRegion region;

        // Get keyFrame corresponding to currentState
        switch(currentState) {
            case SLEEPING:
            default:
                region = sleepFrame;
                break;
            case ATTACKING:
                region = attackAnimation.getKeyFrame(stateTimer);
                break;
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
     * Returns the current state of the egg
     * @return
     */
    public State getState(){
        if (enemyInRange) {
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
            bdef.type = BodyDef.BodyType.StaticBody;
            b2body = world.createBody(bdef);

            FixtureDef fdef = new FixtureDef();
            CircleShape shape = new CircleShape();
            shape.setRadius(3.5f / PPM);
            fdef.filter.categoryBits = ENEMY_BIT;
            fdef.filter.maskBits = MONKEY_BIT;
            fdef.shape = shape;
            b2body.createFixture(fdef).setUserData(this);
    }

}
