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
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Enemy;


public class Satyr extends Enemy {
    private static final float[] KOBOLD_HITBOX = {
            -0.22f, 0.1f,
            -0.22f, -0.22f,
            0.08f, -0.22f,
            0.08f, 0.1f};
    private static final float[] SPEAR_HITBOX_RIGHT = {
            0.2f, -0.175f,
            0.2f, 0.175f,
            -0.1f, -0.2f,
            -0.1f, 0.2f};
    private static final float[] SPEAR_HITBOX_LEFT = {
            -0.35f, -0.175f,
            -0.35f, 0.175f,
            0.05f, -0.2f,
            0.05f, 0.2f};
    private static final float MAX_HORIZONTAL_SPEED = 1.1f;
    private static final float MAX_VERTICAL_SPEED = 3;
    private float jumpTimer = JUMP_COOLDOWN;
    private static final float JUMP_COOLDOWN = 2;
    private static final float HURT_TIME = 0.3f;
    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 68;
    private static final int HEIGHT_PIXELS = 46;

    private static final float CORPSE_EXISTS_TIME = 0.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.4f;

    private float attackTimer;


    private float deathTimer;

    private boolean setToDie = false;

    private Fixture attackFixture;
    private float idleTimer = -1f;
    private static final float IDLE_TIME = 0.5f;

    private static final String MOVE_ANIMATION_FILENAME = "satyr_run";
    private static final String ATTACK_ANIMATION_FILENAME = "satyr_attack";
    private static final String IDLE_ANIMATION_FILENAME = "satyr_idle";
    private static final String HURT_ANIMATION_FILENAME = "satyr_hurt";
    private static final String DEATH_ANIMATION_FILENAME = "satyr_die";
    private static final String JUMP_ANIMATION_FILENAME = "satyr_jump";

    private static final int MOVE_FRAME_COUNT = 6;
    private static final int ATTACK_FRAME_COUNT = 7;
    private static final int IDLE_FRAME_COUNT = 4;
    private static final int HURT_FRAME_COUNT = 3;
    private static final int DEATH_FRAME_COUNT = 8;
    private static final int JUMP_FRAME_COUNT = 28;

    private static final float MOVE_ANIMATION_FPS = 0.1f;
    private static final float ATTACK_ANIMATION_FPS = 0.1f;
    private static final float IDLE_ANIMATION_FPS = 0.09f;
    private static final float HURT_ANIMATION_FPS = 0.07f;
    private static final float DEATH_ANIMATION_FPS = 0.1f;
    private static final float JUMP_ANIMATION_FPS = 0.1f;


    public Satyr(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        initJumpAnimation(
                JUMP_ANIMATION_FILENAME,
                JUMP_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                JUMP_ANIMATION_FPS);
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
        health = 3;
        barYOffset = 0.09f;
    }

    @Override
    public void update(float dt) {
        if (!active) {
            if (playerInActivationRange()) {
                active = true;
                jumpTimer = JUMP_COOLDOWN;
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
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if (!destroyed) {
                    screen.getSpritesToAdd().add(new SmallExplosion(screen, getX() - getWidth() / 4, getY() - getHeight() - 0.1f));
                }
            }
        }

        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            setRegion(getFrame(dt));
            if (runningRight) {
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            } else {
                setPosition(b2body.getPosition().x - 0.5f, b2body.getPosition().y - getHeight() / 2);
            }
            updateStateTimers(dt);
            if(active){
                act();
            }
        }
    }

    private void updateStateTimers(float dt) {
        if (jumpTimer > 0) {
            jumpTimer -= dt;
        }
        if (idleTimer > 0) {
            idleTimer -= dt;
        }
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
            } else if (Math.abs(b2body.getLinearVelocity().x) < 0.01) {
                if (jumpTimer < 0) {
                    jump();
                    jumpTimer = JUMP_COOLDOWN;
                }
            }
            chasePlayer();

        }
        if (currentState == State.ATTACKING) {
            if (currentFrameIsAnAttack()) {
                enableAttackHitBox();
            }
            if (attackFramesOver()) {
                disableAttackHitBox();
            }
            if (attackAnimation.isAnimationFinished(stateTimer - 0.1f)) {
                idleTimer = IDLE_TIME;
                jumpTimer = JUMP_COOLDOWN / 2;
                attackTimer = -1f;
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
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (b2body.getLinearVelocity().y > 0) {
            return State.JUMPING;
        } else if (idleTimer > 0) {
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

    private void runRight() {
        b2body.applyLinearImpulse(new Vector2(0.175f, 0), b2body.getWorldCenter(), true);
    }

    private void runLeft() {
        b2body.applyLinearImpulse(new Vector2(-0.175f, 0), b2body.getWorldCenter(), true);

    }

    private boolean playerInAttackRange() {
        return (Math.abs(getVectorToPlayer().x) < 50 / AdventureGame.PPM);
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
        shape.set(KOBOLD_HITBOX);
        return shape;
    }

    private void limitSpeed() {
        if (b2body.getLinearVelocity().y > MAX_VERTICAL_SPEED) {
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED);
        }
        if (b2body.getLinearVelocity().x > MAX_HORIZONTAL_SPEED) {
            b2body.setLinearVelocity(MAX_HORIZONTAL_SPEED, b2body.getLinearVelocity().y);
        }
        if (b2body.getLinearVelocity().x < -MAX_HORIZONTAL_SPEED) {
            b2body.setLinearVelocity(-MAX_HORIZONTAL_SPEED, b2body.getLinearVelocity().y);
        }
    }

    private void jump() {
        if (runningRight) {
            b2body.applyLinearImpulse(new Vector2(1, 2.6f), b2body.getWorldCenter(), true);
        } else {
            b2body.applyLinearImpulse(new Vector2(-1, 2.6f), b2body.getWorldCenter(), true);

        }
    }

    private boolean playerInActivationRange() {
        return (Math.abs(getVectorToPlayer().len()) < 200 / AdventureGame.PPM);
    }
}
