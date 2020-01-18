package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;

public class Ogre extends Enemy {
    private static final float[] OGRE_HITBOX = {
            -0.12f, 0.1f,
            -0.12f, -0.2f,
            0.12f, -0.2f,
            0.12f, 0.1f};
    private static final float[] SWORD_HITBOX_RIGHT = {
            0.3f, -0.2f,
            0.3f, 0.1f,
            0.1f, -0.2f,
            0f, 0.15f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.3f, -0.2f,
            -0.3f, 0.1f,
            -0.1f, -0.2f,
            0f, 0.15f};

    private static final float ATTACK_RATE = 1.1f;

    private static final int WIDTH_PIXELS = 58;
    private static final int HEIGHT_PIXELS = 42;

    private static final float CORPSE_EXISTS_TIME = 1f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.3f;
    private static final float MAX_HORIZONTAL_SPEED = 1.1f;
    private static final float MAX_VERTICAL_SPEED = 3;
    private static final float JUMP_COOLDOWN = 2;

    private float attackTimer;
    private float jumpTimer = -1f;
    private float deathTimer;


    private boolean setToDie = false;

    private Fixture attackFixture;

    private static final String MOVE_ANIMATION_FILENAME = "ogre_run";
    private static final String ATTACK_ANIMATION_FILENAME = "ogre_attack";
    private static final String IDLE_ANIMATION_FILENAME = "ogre_idle";
    private static final String HURT_ANIMATION_FILENAME = "ogre_hurt";
    private static final String DEATH_ANIMATION_FILENAME = "ogre_die";
    private static final String JUMP_ANIMATION_FILENAME = "ogre_jump";

    private static final int MOVE_FRAME_COUNT = 6;
    private static final int ATTACK_FRAME_COUNT = 7;
    private static final int IDLE_FRAME_COUNT = 4;
    private static final int HURT_FRAME_COUNT = 3;
    private static final int DEATH_FRAME_COUNT = 9;
    private static final int JUMP_FRAME_COUNT = 2;

    private static final float MOVE_ANIMATION_FPS = 0.1f;
    private static final float ATTACK_ANIMATION_FPS = 0.1f;
    private static final float IDLE_ANIMATION_FPS = 0.1f;
    private static final float HURT_ANIMATION_FPS = 0.1f;
    private static final float DEATH_ANIMATION_FPS = 0.1f;
    private static final float JUMP_ANIMATION_FPS = 0.1f;


    private boolean active;

    public Ogre(PlayScreen screen, float x, float y) {
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
        attackDamage = 3;
        health = 9;
        barYOffset = 0.02f;
        setScale(1.3f);
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
            barXOffset = -0.2f;
        } else {
            barXOffset = -0.05f;
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
        if (currentState == State.ATTACKING) {
            if (attackAnimation.isAnimationFinished(stateTimer - 0.2f)) {
                attackTimer = -1;
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
            if(active){
                act(dt);
            }

        }
    }

    private void updateStateTimers(float dt) {
        if (jumpTimer > 0) {
            jumpTimer -= dt;
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
    }

    private void act(float dt) {
        if (currentState == State.CHASING) {

            if (playerInAttackRange()) {
                goIntoAttackState();
                jumpTimer = JUMP_COOLDOWN;
//                lungeAtPlayer();
            } else {
                if (Math.abs(b2body.getLinearVelocity().x) < 0.01) {
                    if (jumpTimer < 0) {
                        jump();
                        jumpTimer = JUMP_COOLDOWN;
                    }
                }
            }
            limitSpeed();
            chasePlayer();
        }
        if (currentState == State.ATTACKING) {
            if (currentFrameIsAnAttack()) {
                enableAttackHitBox();
            }
            if (attackFramesOver()) {
                disableAttackHitBox();
            }
        }

        if (attackTimer > 0) {
            attackTimer -= dt;
        }
    }

    private void jump() {
        b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);

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
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) < 230 / AdventureGame.PPM) {
            return State.CHASING;
        } else if (b2body.getLinearVelocity().x == 0) {
            return State.IDLE;
        } else {
            return State.IDLE;
        }
    }


    @Override
    public void hitByFire() {
        screen.getExplosions().add(new Explosion(screen, getX(), getY()));

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
            hitbox = SWORD_HITBOX_RIGHT;
        } else {
            hitbox = SWORD_HITBOX_LEFT;
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
//        b2body.setLinearVelocity(1.15f, b2body.getLinearVelocity().y);
        b2body.applyLinearImpulse(new Vector2(0.175f, 0), b2body.getWorldCenter(), true);

    }

    private void runLeft() {
//        b2body.setLinearVelocity(-1.15f, b2body.getLinearVelocity().y);
        b2body.applyLinearImpulse(new Vector2(-0.175f, 0), b2body.getWorldCenter(), true);
    }

    private boolean playerInAttackRange() {
        return (Math.abs(getVectorToPlayer().x) < 70 / AdventureGame.PPM);
    }

    private void jumpingAttackLeft() {
        b2body.applyLinearImpulse(new Vector2(-.5f, 2f), b2body.getWorldCenter(), true);
    }

    private void jumpingAttackRight() {
        b2body.applyLinearImpulse(new Vector2(.5f, 2f), b2body.getWorldCenter(), true);
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
        shape.set(OGRE_HITBOX);
        return shape;
    }

    private boolean playerInActivationRange() {
        return (Math.abs(getVectorToPlayer().len()) < 200 / AdventureGame.PPM);
    }
}
