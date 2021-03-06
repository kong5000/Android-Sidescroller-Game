package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.DamageNumber;


public class Mimic extends Enemy {
    private static final float[] MIMIC_HITBOX = {
            -0.17f, 0.1f,
            -0.17f, -0.15f,
            0.15f, -0.15f,
            0.15f, 0.1f};
    private static final float[] SPEAR_HITBOX_RIGHT = {
            -0.17f, 0.1f,
            -0.17f, -0.15f,
            0.15f, -0.15f,
            0.15f, 0.1f};
    private static final float[] SPEAR_HITBOX_LEFT = {
            -0.17f, 0.1f,
            -0.17f, -0.15f,
            0.15f, -0.15f,
            0.15f, 0.1f};
    private static final float HURT_TIME = 0.3f;
    private static final float ATTACK_RATE = 1.5f;

    private static final int WIDTH_PIXELS = 49;
    private static final int HEIGHT_PIXELS = 37;

    private static final float CORPSE_EXISTS_TIME = 1.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.4f;
    public static final int ATTACK_RANGE = 50;
    public static final int ACTIVATION_RANGE = 200;
    private static final float MAX_HORIZONTAL_SPEED = 1.1f;

    private float attackTimer;


    private float deathTimer;
    private float transformTimer;

    private Animation<TextureRegion> transformAnimation;

    private boolean setToDie = false;
    private boolean hasBeenAttacked = false;

    private Fixture attackFixture;

    private TextureRegion inactiveTexture;


    public Mimic(PlayScreen screen, float x, float y) {
        //Todo Restore original getframe from previous version
        super(screen, x, y);
        inactiveTexture = new TextureRegion(screen.getAtlas().findRegion("mimic_transform"), 0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("mimic_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("mimic_die"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("mimic_attack"),
                11, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("mimic_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("mimic_idle"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        transformAnimation = generateAnimation(screen.getAtlas().findRegion("mimic_transform"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.05f);

        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);

        stateTimer = 0;
        setToDestroy = false;
        destroyed = false;
        currentState = State.IDLE;
        previousState = State.IDLE;
        attackTimer = ATTACK_RATE;
        deathTimer = 0;
        invincibilityTimer = -1f;
        flashRedTimer = -1f;
        health = 9;
        barYOffset = 0.09f;
        transformTimer = 0;
        attackDamage = 3;
    }

    @Override
    public void update(float dt) {
        if(runningRight){
            barXOffset = -0.15f;
        }else {
            barXOffset = 0f;
        }
        if (health <= 0) {
            if (!setToDie) {
                setToDie = true;
            }
        }
        if(transformTimer > 0){
            transformTimer -= dt;
        }
        if (currentState == State.DYING) {
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
            }
        }

        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            updateStateTimers(dt);
            setRegion(getFrame(dt));
            act();
        }
    }

    private void updateStateTimers(float dt) {
        if (hurtTimer > 0) {
            hurtTimer -= dt;
        }
        if (invincibilityTimer > 0) {
            invincibilityTimer -= dt;
        }
        if (flashRedTimer > 0) {
            flashRedTimer -= dt;
        }

        if (attackTimer > 0) {
            attackTimer -= dt;
        }
        if(affectedBySpellTimer > 0){
            affectedBySpellTimer -=dt;
        }
    }

    private void act() {
        if (currentState == State.CHASING) {
            chasePlayer();
            if (playerInAttackRange()) {
                goIntoAttackState();
                lungeAtPlayer();
            }
        }
        if (currentState == State.ATTACKING) {
            if (currentFrameIsAnAttack()) {
                enableAttackHitBox();
            }
            if (attackFramesOver()) {
                disableAttackHitBox();
            }
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

    private void disableAttackHitBox() {
        if (attackFixture != null) {
            b2body.destroyFixture(attackFixture);
            attackFixture = null;
        }
    }

    private void enableAttackHitBox() {
        if (attackFixture == null)
            createAttack();
    }


    private void lungeAtPlayer() {
        if (playerIsToTheRight()) {
            jumpingAttackRight();
        } else {
            jumpingAttackLeft();
        }
    }

    private void chasePlayer() {
        if (playerIsToTheRight()) {
            runRight();
        } else {
            runLeft();
        }
    }

    protected State getState() {
        if (setToDie) {
            return State.DYING;
        }else if(!hasBeenAttacked){
            return State.IDLE;
        }
        else if(transformTimer > 0){
            return State.TRANSFORMING;
        }
        else if (hurtTimer > 0) {
            return State.HURT;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) < 180 / AdventureGame.PPM) {
            return State.CHASING;
        } else if (b2body.getLinearVelocity().x == 0) {
            hasBeenAttacked = false;
            return State.IDLE;
        } else {
            hasBeenAttacked = false;
            return State.IDLE;
        }
    }

    @Override
    public void hitOnHead() {
        damage(2);
    }


    @Override
    public void damage(int amount) {
        if(hasBeenAttacked == false){
            transformTimer = 0.3f;
        }
        hasBeenAttacked = true;
        if (invincibilityTimer < 0) {
            health -= amount;
            invincibilityTimer = INVINCIBILITY_TIME;
            hurtTimer = HURT_TIME;
        }
        if (flashRedTimer < 0) {
            flashRedTimer = FLASH_RED_TIME;
        }
        screen.getDamageNumbersToAdd().add(new DamageNumber(screen,b2body.getPosition().x - getWidth() / 2 + 0.4f
                , b2body.getPosition().y - getHeight() / 2 + 0.2f, false, amount));
        showHealthBar = true;
        b2body.applyLinearImpulse(new Vector2(0, 0.8f), b2body.getWorldCenter(), true);
    }

    @Override
    public boolean notDamagedRecently() {
        return (invincibilityTimer < 0);
    }

    private void createAttack() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_ATTACK_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;
        PolygonShape polygonShape = new PolygonShape();
        float[] hitbox = getAttackHitbox();
        polygonShape.set(hitbox);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = false;
        attackFixture = b2body.createFixture(fixtureDef);
        attackFixture.setUserData(this);
    }

    private float[] getAttackHitbox() {
        float[] hitbox;
        if (runningRight) {
            hitbox = SPEAR_HITBOX_RIGHT;
        } else {
            hitbox = SPEAR_HITBOX_LEFT;
        }
        return hitbox;
    }

    protected void orientTextureTowardsPlayer(TextureRegion region) {
        if (currentState != State.DYING && hasBeenAttacked) {
            Vector2 vectorToPlayer = getVectorToPlayer();
            runningRight = vectorToPlayer.x > 0;

            if (!runningRight && region.isFlipX()) {
                region.flip(true, false);
            }
            if (runningRight && !region.isFlipX()) {
                region.flip(true, false);
            }
        }
    }

    private boolean playerIsToTheRight() {
        return getVectorToPlayer().x > 0;
    }

    private void jumpingAttackLeft() {
        b2body.applyLinearImpulse(new Vector2(-.2f, 0), b2body.getWorldCenter(), true);
    }

    private void jumpingAttackRight() {
        b2body.applyLinearImpulse(new Vector2(.2f, 0), b2body.getWorldCenter(), true);
    }

    private void goIntoAttackState() {
        attackTimer = ATTACK_RATE;
    }

    private boolean currentFrameIsAnAttack() {
        return (currentState == State.ATTACKING && stateTimer > 0.5f);
    }

    private boolean attackFramesOver() {
        if (currentState == State.ATTACKING) {
            return stateTimer > 0.7f;
        }
        return false;
    }

    @Override
    protected Shape getHitBoxShape() {
        PolygonShape shape = new PolygonShape();
        shape.set(MIMIC_HITBOX);
        return shape;
    }
    @Override
    protected void initializeAnimations() {

    }

    @Override
    protected float getAttackRange() {
        return ATTACK_RANGE;
    }

    @Override
    protected float getActivationRange() {
        return ACTIVATION_RANGE;
    }

    @Override
    protected float getMovementSpeed() {
        return MAX_HORIZONTAL_SPEED;
    }
}
