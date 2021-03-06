package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;


public class Reaper extends Enemy {
    private static final float[] REAPER_HITBOX = {
            -0.17f, 0.1f,
            -0.17f, -0.15f,
            0.15f, -0.15f,
            0.15f, 0.1f};
    private static final float[] SPEAR_HITBOX_RIGHT = {
            -0.19f, 0.1f,
            -0.19f, -0.15f,
            0.17f, -0.15f,
            0.17f, 0.1f};
    private static final float[] SPEAR_HITBOX_LEFT = {
            -0.19f, 0.1f,
            -0.19f, -0.15f,
            0.17f, -0.15f,
            0.17f, 0.1f};
    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 50;
    private static final int HEIGHT_PIXELS = 48;

    private static final float CORPSE_EXISTS_TIME = 1.1f;
    private static final float MOVE_TIME = 0.2f;
    private static final float HORIZONTAL_MOVE_TIME = 0.35f;
    private static final float MAX_VERTICAL_SPEED = 0.65f;
    private static final float MAX_HORIZONTAL_SPEED = 0.65f;
    public static final int ATTACK_RANGE = 50;
    public static final int ACTIVATION_RANGE = 200;

    private float attackTimer;
    private float movementTimer = 0;
    private float x_movementTimer = 0;

    private float deathTimer;

    private boolean setToDie = false;

    private Fixture attackFixture;

    private static final String MOVE_ANIMATION_FILENAME = "reaper_run";
    private static final String ATTACK_ANIMATION_FILENAME = "reaper_attack";
    private static final String IDLE_ANIMATION_FILENAME = "reaper_idle";
    private static final String HURT_ANIMATION_FILENAME = "reaper_hurt";
    private static final String DEATH_ANIMATION_FILENAME = "reaper_die";

    private static final int MOVE_FRAME_COUNT = 4;
    private static final int ATTACK_FRAME_COUNT = 5;
    private static final int IDLE_FRAME_COUNT = 4;
    private static final int HURT_FRAME_COUNT = 3;
    private static final int DEATH_FRAME_COUNT = 11;

    private static final float MOVE_ANIMATION_FPS = 0.1f;
    private static final float ATTACK_ANIMATION_FPS = 0.1f;
    private static final float IDLE_ANIMATION_FPS = 0.1f;
    private static final float HURT_ANIMATION_FPS = 0.1f;
    private static final float DEATH_ANIMATION_FPS = 0.1f;


    public Reaper(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("reaper_run"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("reaper_die"),
                11, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("reaper_attack"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("reaper_idle"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);


        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);

        stateTimer = 0;
        setToDestroy = false;
        destroyed = false;
        currentState = State.IDLE;
        previousState = State.IDLE;
        attackTimer = 0;
        deathTimer = 0;
        invincibilityTimer = -1f;
        flashRedTimer = -1f;
        health = 4;
        barYOffset = 0.09f;
        b2body.setGravityScale(0);
    }

    @Override
    public void update(float dt) {
        if(!active && playerInActivationRange()){
            active = true;
        }
        if (currentState != State.DYING && active) {
            if (movementTimer > 0) {
                movementTimer -= dt;
            } else {
                movementTimer = MOVE_TIME;
                chasePlayerVertical();
            }
            if (x_movementTimer > 0) {
                x_movementTimer -= dt;
            } else {
                x_movementTimer = HORIZONTAL_MOVE_TIME;
                chasePlayerHorizontal();
            }
        }

        if (runningRight) {
            barXOffset = -0.15f;
        } else {
            barXOffset = 0f;
        }
        if (health <= 0) {
            if (!setToDie) {
                setToDie = true;
            }
        }
        if (currentState == State.DYING) {
            b2body.setLinearVelocity(b2body.getLinearVelocity().x *0.97f, b2body.getLinearVelocity().y*0.97f );
            if (deathFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if (!destroyed) {
//                    screen.getSpritesToAdd().add(new SmallExplosion(screen, getX() - getWidth() / 4, getY() - getHeight() - 0.1f));
                }
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
        if (affectedBySpellTimer > 0) {
            affectedBySpellTimer -= dt;
        }
    }

    private void act() {
        if (currentState == State.CHASING) {

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
        limitSpeed();
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

    private void chasePlayerVertical() {
        if (playerIsAbove()) {
            moveDown();
        } else if (playerIsBelow()) {
            moveUp();
        } else {

            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);

        }
    }

    private void chasePlayerHorizontal() {
        if (playerIsToTheRight()) {
            runRight();
        } else {
            runLeft();
        }
    }


    protected State getState() {
        if (setToDie) {
            return State.DYING;
        }
        else if (!active) {
            return State.IDLE;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) < 180 / AdventureGame.PPM) {
            return State.CHASING;
        } else if (b2body.getLinearVelocity().x == 0) {
            return State.IDLE;
        } else {
            return State.IDLE;
        }
    }

    @Override
    public void hitOnHead() {
        damage(2);
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
        if (currentState != State.DYING) {
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

    private void moveDown() {
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED);
    }

    private void moveUp() {
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, -MAX_VERTICAL_SPEED);
    }


    private boolean playerIsBelow() {
        float y = getVectorToPlayer().y;
        return (y < -0.2);
    }

    private boolean playerIsAbove() {
        return (getVectorToPlayer().y > -0.1);
    }

    private void jumpingAttackLeft() {
        b2body.applyLinearImpulse(new Vector2(-.2f, 0), b2body.getWorldCenter(), true);
    }

    private void jumpingAttackRight() {
        b2body.applyLinearImpulse(new Vector2(.2f, 0), b2body.getWorldCenter(), true);
    }

    private void goIntoAttackState() {
        screen.getSoundEffects().playEnemyMeleeSound();
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
        shape.set(REAPER_HITBOX);
        return shape;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits =
                AdventureGame.PLAYER_SWORD_BIT
                        | AdventureGame.ARROW_BIT
                        | AdventureGame.FIRE_SPELL_BIT;
        Shape hitBox = getHitBoxShape();
        fixtureDef.shape = hitBox;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    protected void initializeAnimations() {
        getEnemyAnimations().initMoveAnimation(
                MOVE_ANIMATION_FILENAME,
                MOVE_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                MOVE_ANIMATION_FPS
        );
        getEnemyAnimations().initAttackAnimation(
                ATTACK_ANIMATION_FILENAME,
                ATTACK_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                ATTACK_ANIMATION_FPS
        );
        getEnemyAnimations().initIdleAnimation(
                IDLE_ANIMATION_FILENAME,
                IDLE_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                IDLE_ANIMATION_FPS
        );
//        getEnemyAnimations().initHurtAnimation(
//                HURT_ANIMATION_FILENAME,
//                HURT_FRAME_COUNT,
//                WIDTH_PIXELS,
//                HEIGHT_PIXELS,
//                HURT_ANIMATION_FPS
//        );
        getEnemyAnimations().initDeathAnimation(
                DEATH_ANIMATION_FILENAME,
                DEATH_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                DEATH_ANIMATION_FPS
        );
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
    protected float getMovementSpeed() { return MAX_HORIZONTAL_SPEED; }
}
