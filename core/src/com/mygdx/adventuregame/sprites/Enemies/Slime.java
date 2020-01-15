package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.items.Item;

import java.util.Random;

public class Slime extends Enemy {
    private static final float[] SLIME_HITBOX = {
            -0.15f, 0.02f,
            -0.15f, -0.1f,
            0.15f, -0.1f,
            0.15f, 0.02f};
    private static final float ATTACK_RATE = 3f;
    private static final float HURT_RATE = 0.3f;
    private static final float CORPSE_TIME = 0.25f;
    private float attackRateTimer;

    private static final String MOVE_ANIMATION_FILENAME = "slime_move";
    private static final String ATTACK_ANIMATION_FILENAME = "slime_attack";
    private static final String IDLE_ANIMATION_FILENAME = "slime_idle";
    private static final String HURT_ANIMATION_FILENAME = "slime_hurt";
    private static final String DEATH_ANIMATION_FILENAME = "slime_die";

    private static final int MOVE_FRAME_COUNT = 4;
    private static final int ATTACK_FRAME_COUNT = 4;
    private static final int IDLE_FRAME_COUNT = 4;
    private static final int HURT_FRAME_COUNT = 4;
    private static final int DEATH_FRAME_COUNT = 4;

    private static final float MOVE_ANIMATION_FPS = 0.1f;
    private static final float ATTACK_ANIMATION_FPS = 0.1f;
    private static final float IDLE_ANIMATION_FPS = 0.1f;
    private static final float HURT_ANIMATION_FPS = 0.1f;
    private static final float DEATH_ANIMATION_FPS = 0.1f;

    private static final int WIDTH_PIXELS = 34;
    private static final int HEIGHT_PIXELS = 27;

    public Slime(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        initMoveAnimation(
                MOVE_ANIMATION_FILENAME,
                MOVE_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                MOVE_ANIMATION_FPS
        );
        initAttackAnimation(
                ATTACK_ANIMATION_FILENAME,
                ATTACK_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                ATTACK_ANIMATION_FPS
        );
        initIdleAnimation(
                IDLE_ANIMATION_FILENAME,
                IDLE_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                IDLE_ANIMATION_FPS
        );
        initHurtAnimation(
                HURT_ANIMATION_FILENAME,
                HURT_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                HURT_ANIMATION_FPS
        );
        initDeathAnimation(
                DEATH_ANIMATION_FILENAME,
                DEATH_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                DEATH_ANIMATION_FPS
        );

        stateTimer = 0;
        setBounds(getX(), getY(), 34 / AdventureGame.PPM, 27 / AdventureGame.PPM);
        setToDestroy = false;
        destroyed = false;
        currentState = State.WALKING;
        previousState = State.WALKING;
        attackRateTimer = ATTACK_RATE;
        attackDamage = 2;
        health = 2;
    }


    @Override
    public void update(float dt) {
        if (health <= 0) {
            setToDestroy = true;
        }
        if (setToDestroy && !destroyed) {
            screen.getPlayer().giveXP(experiencePoints);
            world.destroyBody(b2body);
            spawnLoot();
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            if (hurtTimer > 0) {
                hurtTimer -= dt;
            }
        }
        setRegion(getFrame(dt));
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTimer < CORPSE_TIME) {
            super.draw(batch);
        } else {
            safeToRemove = true;
        }

    }


    protected State getState() {
        if (setToDestroy) {
            return State.DYING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (Math.abs(getVectorToPlayer().x) < 100 / AdventureGame.PPM) {
            return State.ATTACKING;

        } else {
            return State.WALKING;
        }
    }

    @Override
    protected void orientTextureTowardsPlayer(TextureRegion texture) {

    }

    @Override
    public void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.PLAYER_SWORD_BIT
                | AdventureGame.ARROW_BIT
                | AdventureGame.FIRE_SPELL_BIT;
        PolygonShape shape = new PolygonShape();
        shape.set(SLIME_HITBOX);
//        CircleShape shape = new CircleShape();
//        shape.setRadius(8 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);

        fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_ATTACK_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;

        fixtureDef.shape = shape;
        fixtureDef.isSensor = false;
        b2body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(
                new Vector2(-8 / AdventureGame.PPM, 1 / AdventureGame.PPM),
                new Vector2(8 / AdventureGame.PPM, 1 / AdventureGame.PPM)
        );
        fixtureDef.shape = head;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_HEAD_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;
        fixtureDef.isSensor = false;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }


    @Override
    public void damage(int amount) {
        health -= amount;
        if (currentState != State.HURT) {
            if (hurtTimer < 0) {
                hurtTimer = HURT_RATE;
            }
            if (getVectorToPlayer().x < 0) {
                b2body.applyLinearImpulse(new Vector2(1f, 1f), b2body.getWorldCenter(), true);
            } else {
                b2body.applyLinearImpulse(new Vector2(-1f, 1f), b2body.getWorldCenter(), true);
            }

        }
        screen.getDamageNumbersToAdd().add(new DamageNumber(screen,b2body.getPosition().x - getWidth() / 2 + 0.4f
                , b2body.getPosition().y - getHeight() / 2 + 0.2f, false, amount));
    showHealthBar = true;
    }

    @Override
    public boolean notDamagedRecently() {
        return (hurtTimer < 0);
    }

    private void spawnLoot(){
        Random random = new Random();
        int randomDraw = random.nextInt(5);
        if(randomDraw == 3){
            screen.getSpritesToAdd().add(new Item(screen, getX(), getY(), AdventureGame.SMALL_HEALTH));
        }
        if(randomDraw == 4){
            screen.getSpritesToAdd().add(new Item(screen, getX(), getY(), AdventureGame.LARGE_HEALTH));
        }
        screen.getSpritesToAdd().add(new Item(screen, getX(), getY(), AdventureGame.SMALL_HEALTH));


    }

    @Override
    protected Shape getHitBoxShape() {
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / AdventureGame.PPM);
        return shape;
    }
}
