package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class FireSpell extends Sprite implements UpdatableSprite{
    private enum State {ARMED, IMPACT}

    private State currentState = State.ARMED;
    private State previousState = State.ARMED;
    private World world;
    private PlayScreen screen;
    public Body b2body;
    public boolean attackEnabled;
    protected boolean destroyed;
    public boolean safeToRemove = false;
    private boolean setToDestroy = false;
    private Animation<TextureRegion> projectileAnimation;
    private float aliveTimer;
    private float stateTimer;
    private static final float TIME_ALIVE = 3f;
    private static final int WIDTH_PIXELS = 100;
    private static final int HEIGHT_PIXELS = 100;
    private boolean isFriendly;
    private float size = 0.2f;
    private float rotation = 0f;
    private boolean goingRight = true;
    private boolean launched = false;
    private boolean charging = true;
    private float rotationIncrement = 0.1f;
    private Animation<TextureRegion> projectile;
    private static final float MAX_SIZE = 0.65f;
    private static final float MAX_ROTATION_SPEED = 40f;
    private float damage;
    private static final float STARTING_DAMAGE = 1f;
    private static final float MAX_DAMAGE = 8f;
    private Player player;
    private float xOffset = 0.2f;

    public FireSpell(PlayScreen screen, float x, float y, boolean goingRight, Player player) {
        this.world = screen.getWorld();
        this.screen = screen;
        this.player = player;


        this.isFriendly = isFriendly;
        attackEnabled = false;
        aliveTimer = TIME_ALIVE;
        stateTimer = 0;
        this.goingRight = goingRight;
        damage = STARTING_DAMAGE;

        TextureRegion region = screen.getAtlas().findRegion("fire_ball");

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * 100,
                        0,
                        100,
                        100
                ));
            }
        }
        projectileAnimation = new Animation(0.02f, frames, Animation.PlayMode.LOOP);

        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setOrigin(getWidth() / 2 - 0.01f, getHeight() / 2);
//        setPosition(x - getWidth() / 2, y - getHeight() / 2);
        setScale(size);

    }

    public void update(float dt) {

        setRegion(getFrame(dt));
        if(b2body != null){
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }else {
            goingRight = player.runningRight;
            if(player.runningRight){
                setPosition(player.getX() - player.getWidth() / 2 + xOffset,
                        player.getY() - player.getHeight() / 2  - 0.1f);
            }else {
                setPosition(player.getX() - player.getWidth() / 2 - xOffset,
                        player.getY() - player.getHeight() / 2  - 0.1f);
            }

        }


        if (charging) {
            if(damage < MAX_DAMAGE){
                damage += 0.0925f;
            }
            if(size < MAX_SIZE){
                size += 0.005f;
                setScale(size);
            }
            if(rotationIncrement < MAX_ROTATION_SPEED){
                rotationIncrement += 0.3f;
            }

        }
        else {
            if (launched == false) {
                defineProjectile();
                if(goingRight){
                    b2body.setLinearVelocity(new Vector2(3f, 0));
                }else{
                    b2body.setLinearVelocity(new Vector2(-3f, 0));
                }


//                if (goingRight) {
//                    b2body.setLinearVelocity(new Vector2(3f, 0));
//                } else {
//                    b2body.setLinearVelocity(new Vector2(-3f, 0));
//                }
                launched = true;
            }
            aliveTimer -= dt;
        }
        rotation -= rotationIncrement;
        setRotation(rotation);

        if ((aliveTimer < 0 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }

    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        } else {
            safeToRemove = true;
        }
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion texture;
        switch (currentState) {
            case ARMED:
                attackEnabled = true;
                texture = projectileAnimation.getKeyFrame(stateTimer, true);
                break;
            case IMPACT:
            default:
                attackEnabled = false;
                texture = projectileAnimation.getKeyFrame(stateTimer, true);
                break;
        }
        if(launched){
            flipFramesIfNeeded(texture);
        }


        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return texture;
    }

    private void flipFramesIfNeeded(TextureRegion texture) {
        if (b2body.getLinearVelocity().x < 0 && texture.isFlipX()) {
            texture.flip(true, false);
        }
        if (b2body.getLinearVelocity().x > 0 && !texture.isFlipX()) {
            texture.flip(true, false);
        }
    }

    public void setGoingRight(boolean status) {
        float speed = 1f;
        if (isFriendly) {
            speed *= 2.5f;
        }
        if (status) {
            b2body.setLinearVelocity(new Vector2(speed, 0));
        } else {
            b2body.setLinearVelocity(new Vector2(-speed, 0));
        }
    }


    private State getState() {
        if (setToDestroy) {
            return State.IMPACT;
        } else {
            return State.ARMED;
        }
    }


    private void defineProjectile() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX() +getWidth() /2, getY() + getHeight() / 2);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.FIRE_SPELL_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.ENEMY_BIT;


        CircleShape shape = new CircleShape();
        shape.setRadius(8 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        b2body.setGravityScale(0);
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    private Animation<TextureRegion> generateAnimation(
            TextureRegion textureRegion,
            int numberOfFrames,
            int widthInPixels,
            int heightInPixels,
            float secondsPerFrame) {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < numberOfFrames; i++) {
            frames.add(new TextureRegion(
                    textureRegion,
                    i * widthInPixels,
                    0,
                    widthInPixels,
                    heightInPixels
            ));
        }
        Animation<TextureRegion> animation = new Animation<TextureRegion>(secondsPerFrame, frames);
        return animation;
    }

    public void explode() {
        screen.getExplosions().add(new Explosion(screen, getX() - getWidth() / 2
                , getY() - getHeight() / 2 - 0.05f));
    }

    public void stopCharging() {
        charging = false;
    }
    public int getDamage(){
        return (int)(damage);
    }
    public boolean isFullyCharged(){
        return damage >= MAX_DAMAGE;
    }
}
