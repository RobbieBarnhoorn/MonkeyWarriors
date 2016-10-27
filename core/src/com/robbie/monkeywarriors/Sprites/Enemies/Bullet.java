package com.robbie.monkeywarriors.Sprites.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robbie.monkeywarriors.Screens.PlayScreen;
import static com.robbie.monkeywarriors.MonkeyWarriors.*;

/**
 * Created by robbie on 2016/10/16.
 */
public class Bullet extends Enemy {

    private Texture tex;
    private TextureRegion bullet;
    private Bandit bandit;

    private boolean setToDestroy;
    private boolean destroyed;

    private final static float VELOCITY = 1f;

    public Bullet(PlayScreen screen, Bandit bandit, Vector2 spawn, Vector2 dir) {
        super(screen, spawn.x, spawn.y);
        this.bandit = bandit;
        tex = new Texture("sprites/bandit/bullet.png");
        bullet = new TextureRegion(tex, 0, 0, 32, 32);
        setBounds(0, 0, 16/PPM, 16/PPM);
        setRegion(bullet);
        b2body.applyLinearImpulse(dir.scl(VELOCITY), b2body.getWorldCenter(), true);

        setToDestroy = false;
        destroyed = false;

    }

    public void update(float dt) {
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            bandit.removeBullet(this);
            destroyed = true;
        }
        else if (!destroyed) {
            // Move the TextureRegion to where the b2body is
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
    }

    public void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.gravityScale = 0;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(1.5f / PPM);
        fdef.filter.categoryBits = BULLET_BIT;
        fdef.filter.maskBits = MONKEY_BIT | GROUND_BIT | LAVA_BIT | SOLDIER_BIT | BAT_BIT;
        fdef.shape = shape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        }
    }

    public void dispose() {
        tex.dispose();
    }
}
