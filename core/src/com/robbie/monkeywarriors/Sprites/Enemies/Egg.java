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
public class Egg extends Enemy{

    public enum State {READY, BIRTHING, GESTATING};
    public State currentState;
    public State previousState;

    private float stateTimer;
    private Animation birthAnimation;
    private TextureRegion readyFrame;
    private TextureRegion gestatingFrame;

    private boolean enemyInRange;

    public Egg(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        currentState = State.READY;
        previousState = State.READY;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Create a birthing animation
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(new Texture("sprites/egg/egg.png"), i*32, 0, 32, 32));
            frames.get(i).flip(false, true);
        }
        birthAnimation = new Animation(1/10f, frames);

        frames.clear();

        // Create a ready frame
        readyFrame = new TextureRegion(new Texture("sprites/egg/egg.png"), 0, 0, 32, 32);
        readyFrame.flip(false, true);

        // Create a ready frame
        gestatingFrame = new TextureRegion(new Texture("sprites/egg/egg.png"), 96, 0, 32, 32);
        gestatingFrame.flip(false, true);

        // Set initial values for the textures location, width and height
        setBounds(0, 0, 16/PPM, 16/PPM);
        setRegion(readyFrame);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2 + 2/PPM);
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
            case READY:
                region = readyFrame;
                break;
            case BIRTHING:
                region = birthAnimation.getKeyFrame(stateTimer);
                break;
            case GESTATING:
            default:
                region = gestatingFrame;
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
        if (enemyInRange && currentState == State.READY) {
            return State.BIRTHING;
        }
        else if (currentState == State.BIRTHING && stateTimer <= 0.4f) {
            return State.BIRTHING;
        }
        else if (currentState == State.BIRTHING && stateTimer > 0.4f) {
            return State.GESTATING;
        }
        else {
            return State.READY;
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
            shape.setRadius(4 / PPM);
            fdef.filter.categoryBits = ENEMY_BIT;
            fdef.filter.maskBits = MONKEY_BIT;
            fdef.shape = shape;
            b2body.createFixture(fdef).setUserData(this);
    }

}
