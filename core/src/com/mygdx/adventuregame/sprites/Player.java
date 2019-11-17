package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class Player extends Sprite {
    private static final float HURT_TIME = 0.5f;
    private static final float ATTACK_TIME = 0.5f;
    private static final float FLIP_TIME = 0.4f;
    private static final float MAX_VERTICLE_SPEED = 3f;
    private static final float[] SWORD_HITBOX_AIR = {
            -0.25f, -0.1f,
            -0.25f, 0.1f,
            0f, -0.2f,
            0.25f, -0.1f,
            0.25f, 0.1f,
            0, 0.2f};

    private static final float[] SWORD_HITBOX_RIGHT = {
            0f, -0.2f,
            0.25f, -0.1f,
            0.25f, 0.1f,
            0, 0.2f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.25f, -0.1f,
            -0.25f, 0.1f,
            0f, -0.2f,
            0, 0.2f};

    public enum State {FALLING, JUMPING, STANDING, RUNNING, HURT, ATTACKING, AIR_ATTACKING, FLIPING}

    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerIdle;
    private Animation<TextureRegion> playerFall;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerHurt;
    private Animation<TextureRegion> playerDie;
    private Animation<TextureRegion> playerAttack;
    private Animation<TextureRegion> playerAirAttack;
    private Animation<TextureRegion> playerFlip;

    private boolean runningRight;
    private boolean flipEnabled;
    private float stateTimer;
    private float hurtTimer;
    private float attackTimer;
    private float flipTimer;

    TextureAtlas textureAtlas;

    private Fixture swordFixture;


    public Player(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("player_idle1"));
        this.world = world;
        textureAtlas = screen.getAtlas();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        attackTimer = ATTACK_TIME;
        flipTimer = FLIP_TIME;
        flipEnabled = true;
        runningRight = true;

        playerRun = generateAnimation(textureAtlas.findRegion("player_run"), 6, 52, 39, 0.1f);
        playerIdle = generateAnimation(textureAtlas.findRegion("player_idle1"), 3, 52, 39, 0.2f);
        playerFall = generateAnimation(textureAtlas.findRegion("player_fall"), 2, 52, 39, 0.1f);
        playerJump = generateAnimation(textureAtlas.findRegion("player_jump"), 4, 52, 39, 0.1f);
        playerHurt = generateAnimation(textureAtlas.findRegion("player_hurt"), 3, 52, 39, 0.1f);
        playerDie = generateAnimation(textureAtlas.findRegion("player_die"), 7, 52, 39, 0.1f);
        playerAttack = generateAnimation(textureAtlas.findRegion("player_attack1"), 5, 52, 39, 0.1f);
        playerAirAttack = generateAnimation(textureAtlas.findRegion("player_air_attack1"), 4, 52, 39, 0.125f);
        playerFlip = generateAnimation(textureAtlas.findRegion("player_flip"), 4, 52, 39, 0.1f);

        definePlayer();
        playerStand = new TextureRegion(getTexture(), 0, 0, 50, 37);
        setBounds(0, 0, 50 / AdventureGame.PPM, 37 / AdventureGame.PPM);
        setRegion(playerStand);
    }

    private void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / AdventureGame.PPM, 62 / AdventureGame.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
        ;
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);


//        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_SWORD_BIT;
//        fixtureDef.filter.maskBits = AdventureGame.ENEMY_BIT;
//        PolygonShape polygonShape = new PolygonShape();
//        polygonShape.set(SWORD_HITBOX_AIR);
//        fixtureDef.shape = polygonShape;
//        fixtureDef.isSensor = false;
//        b2body.createFixture(fixtureDef).setUserData(this);


//        EdgeShape boot = new EdgeShape();
//        boot.set(
//                new Vector2(-8 / AdventureGame.PPM, -17 / AdventureGame.PPM),
//                new Vector2(8 / AdventureGame.PPM, -17 / AdventureGame.PPM)
//        );
//        fixtureDef.shape = boot;
//        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BOOT_BIT;
//        fixtureDef.filter.maskBits = AdventureGame.ENEMY_HEAD_BIT;
//        fixtureDef.isSensor =false;
//        b2body.createFixture(fixtureDef).setUserData(this);

    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if (hurtTimer > 0) {
            hurtTimer -= dt;
        }
        if (attackTimer > 0) {
            attackTimer -= dt;
        } else {
            if (swordFixture != null) {
                b2body.destroyFixture(swordFixture);
                swordFixture = null;
            }
        }
        if (flipTimer > 0) {
            flipTimer -= dt;
        }
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case HURT:
                region = playerHurt.getKeyFrame(stateTimer);
                break;
            case ATTACKING:
                region = playerAttack.getKeyFrame(stateTimer);
                break;
            case AIR_ATTACKING:
                region = playerAirAttack.getKeyFrame(stateTimer);
                break;
            case FLIPING:
                region = playerFlip.getKeyFrame(stateTimer);
                break;
            case JUMPING:
                region = playerJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
                region = playerFall.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = playerIdle.getKeyFrame(stateTimer, true);
                break;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (attackTimer > 0) {
            if (Math.abs(b2body.getLinearVelocity().y) > 0) {
                return State.AIR_ATTACKING;
            }
            return State.ATTACKING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (flipTimer > 0) {
            return State.FLIPING;
        } else if (b2body.getLinearVelocity().y > 0) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            flipEnabled = true;
            return State.RUNNING;
        } else {
            flipEnabled = true;
            return State.STANDING;
        }
    }

    protected Animation<TextureRegion> generateAnimation(
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

    public void hurt() {
        hurtTimer = HURT_TIME;
    }

    public void jump() {
        if (currentState == State.JUMPING || currentState == State.FALLING || currentState == State.FLIPING || currentState == State.AIR_ATTACKING) {
            if (flipEnabled) {
                flipTimer = FLIP_TIME;
                flipEnabled = false;
                b2body.applyLinearImpulse(new Vector2(0, 6f), b2body.getWorldCenter(), true);
                if (b2body.getLinearVelocity().y > MAX_VERTICLE_SPEED) {
                    b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICLE_SPEED);
                }

            }
        } else if (currentState != State.JUMPING && currentState != State.FALLING) {
            b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);

        }
    }

    public void attack() {
        if (attackTimer < 0) {
            attackTimer = ATTACK_TIME;
            if(swordFixture == null){
                createAttack();
            }
        }
        b2body.setAwake(true);
    }

    public State getCurrentState() {
        return currentState;
    }

    public boolean isSwinging() {
        if (currentState == State.ATTACKING || currentState == State.AIR_ATTACKING) {
            return true;

        }
        return false;
    }

    private void createAttack() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_SWORD_BIT;
        fixtureDef.filter.maskBits = AdventureGame.ENEMY_BIT;
        PolygonShape polygonShape = new PolygonShape();
        float[] hitbox;
        switch (currentState) {
            case JUMPING:
            case FLIPING:
            case AIR_ATTACKING:
            case FALLING:
                hitbox = SWORD_HITBOX_AIR;
                break;
            default:
                if (runningRight) {
                    hitbox = SWORD_HITBOX_RIGHT;
                } else {
                    hitbox = SWORD_HITBOX_LEFT;
                }
                break;
        }
        polygonShape.set(hitbox);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = false;
        swordFixture = b2body.createFixture(fixtureDef);
        swordFixture.setUserData(this);
    }

}
