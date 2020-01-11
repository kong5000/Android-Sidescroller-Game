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
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.Xplosion;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.degreesToRadians;
import static com.badlogic.gdx.math.MathUtils.sin;

public class GolemFireAttack extends Sprite implements UpdatableSprite, EnemyProjectile, BossAttack{
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
    private static final float MAX_SIZE = 4f;
    private static final float MAX_ROTATION_SPEED = 40f;
    private float damage;
    private static final float STARTING_DAMAGE = 1f;
    private static final float MAX_DAMAGE = 8f;
    private Enemy enemy;
    private float xOffset = 0.75f;
    private float angleToPlayer;

    public GolemFireAttack(PlayScreen screen, float x, float y, boolean goingRight, Enemy enemy) {
        this.world = screen.getWorld();
        this.screen = screen;
        this.enemy = enemy;


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
        if(enemy.setToDestroy){
            setToDestroy = true;
        }
        angleToPlayer = getVectorToPlayer().angle();
        setRegion(getFrame(dt));
        if(b2body != null){
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }else {
            goingRight = enemy.runningRight;
            if(enemy.runningRight){
                setPosition(enemy.getX() - enemy.getWidth() / 2 + xOffset,
                        enemy.getY() - enemy.getHeight() / 2  + 0.1f);
            }else {
                setPosition(enemy.getX() - enemy.getWidth() / 2 - 0,
                        enemy.getY() - enemy.getHeight() / 2  + 0.1f);
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
                float xVelocity = 2 * cos(angleToPlayer * degreesToRadians);
                float yVelocity = 2 * sin(angleToPlayer * degreesToRadians);
                defineProjectile();
                b2body.setLinearVelocity(new Vector2(xVelocity, yVelocity));
                launched = true;
            }
            aliveTimer -= dt;
        }
        rotation += rotationIncrement;
        setRotation(rotation);
        if ((aliveTimer < 0 || setToDestroy) && !destroyed) {
            if(b2body != null){
                world.destroyBody(b2body);
            }
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
        fixtureDef.filter.categoryBits = AdventureGame.BOSS_ATTACK_BIT;
//        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_PROJECTILE_BIT;
        fixtureDef.filter.maskBits =  AdventureGame.PLAYER_BIT
        | AdventureGame.GROUND_BIT;


        CircleShape shape = new CircleShape();
        shape.setRadius(14 / AdventureGame.PPM);

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
        screen.getSpritesToAdd().add(new Xplosion(screen, getX()
                ,getY()));
//        screen.getExplosionsToAdd().add(new Explosion(screen, b2body.getPosition().x -0.5f
//                , b2body.getPosition().y - 0.35f ));
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

    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

    protected Vector2 getVectorToPlayer() {
        Vector2 enemyPosition = new Vector2(this.getX(), this.getY() + 0.2f);
        Vector2 playerVector = new Vector2(screen.getPlayer().getX(), screen.getPlayer().getY());
        return playerVector.sub(enemyPosition);
    }

    @Override
    public void dispose() {
        if(b2body != null){
            world.destroyBody(b2body);
        }
    }

    @Override
    public void onPlayerHit() {
        setToDestroy();
        explode();
    }
}
