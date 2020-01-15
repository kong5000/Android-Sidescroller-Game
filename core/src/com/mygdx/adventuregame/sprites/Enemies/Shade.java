package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Chest;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.Projectiles.FireBall;
import com.mygdx.adventuregame.sprites.Projectiles.ShadeProjectile;


public class Shade extends Enemy {

    private static final float[] SHADE_HITBOX = {
            -0.22f, 0.1f,
            -0.22f, -0.22f,
            0.05f, -0.22f,
            0.05f, 0.1f};

    private static final float ATTACK_RATE = 3.5f;

    private static final int WIDTH_PIXELS = 59;
    private static final int HEIGHT_PIXELS = 50;

    private static final float CORPSE_EXISTS_TIME = 0.9f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.4f;
    private static final float HURT_TIME = 0.3f;

    private float attackTimer;
    private float attackCooldown;


    private boolean canFireProjectile = false;

    private boolean specialDrop = false;
    private float deathTimer;
    private boolean setToDie;

    private static final float X_OFFSET = 0.2f;

    private static final String MOVE_ANIMATION_FILENAME = "shade_move";
    private static final String ATTACK_ANIMATION_FILENAME = "shade_attack";
    private static final String IDLE_ANIMATION_FILENAME = "shade_idle";
    private static final String HURT_ANIMATION_FILENAME = "shade_hurt";
    private static final String DEATH_ANIMATION_FILENAME = "shade_die";

    private static final int MOVE_FRAME_COUNT = 4;
    private static final int ATTACK_FRAME_COUNT = 7;
    private static final int IDLE_FRAME_COUNT = 4;
    private static final int HURT_FRAME_COUNT = 3;
    private static final int DEATH_FRAME_COUNT = 10;

    private static final float MOVE_ANIMATION_FPS = 0.1f;
    private static final float ATTACK_ANIMATION_FPS = 0.1f;
    private static final float IDLE_ANIMATION_FPS = 0.09f;
    private static final float HURT_ANIMATION_FPS = 0.07f;
    private static final float DEATH_ANIMATION_FPS = 0.1f;

    public Shade(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        this.specialDrop = specialDrop;

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
        attackCooldown = -1f;
        stateTimer = 0;
        setToDestroy = false;
        destroyed = false;
        currentState = State.IDLE;
        previousState = State.IDLE;
        attackTimer = ATTACK_RATE;
        invincibilityTimer = -1f;
        flashRedTimer = -1f;
        health = 6;
        barXOffset = -0.065f;
        barYOffset = 0.075f;
        setScale(1.15f);

    }

    @Override
    public void update(float dt) {
        if (health <= 0) {
            if (!setToDie) {
                setToDie = true;
            }
        }
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            setRegion(getFrame(dt));
            float x_offset = 0;
            if (!runningRight) {
                x_offset = X_OFFSET;
            }
            setPosition(b2body.getPosition().x - getWidth() / 2 - x_offset, b2body.getPosition().y - getHeight() / 2);

            updateStateTimers(dt);

            act(dt);
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
        if (currentState == State.ATTACKING) {
            if (attackTimer > 0) {
                attackTimer -= dt;
            }
        }

        if (attackCooldown > 0) {
            attackCooldown -= dt;
        }
    }

    private void act(float dt) {
        if (currentState == State.ATTACKING) {
            if (attackAnimation.isAnimationFinished(stateTimer)) {
                attackTimer = -1f;
            }
        }
        if (currentState != State.ATTACKING) {
            canFireProjectile = true;
        }
        if (currentState == State.CHASING) {
//            chasePlayer();
        }
        if (currentState == State.IDLE) {
            if (playerInAttackRange()) {
                if (attackCooldownOver()) {
                    goIntoAttackState();
                    attackCooldown = ATTACK_RATE;
                }
            }
        }
        if (currentState == State.ATTACKING) {
            if (stateTimer > 0.5f && canFireProjectile) {
                launchFireBall();
                canFireProjectile = false;
            }
        }
        if (currentState == State.DYING) {
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if (!destroyed) {
                    float x_offset = 0;
                    if (!runningRight) {
                        x_offset = X_OFFSET;
                    }
                    screen.getSpritesToAdd().add(new SmallExplosion(screen, getX() - getWidth() / 2 + x_offset, getY() - getHeight()));
                }
            }
        }
    }

    private void launchFireBall() {
        boolean playerToRight = getVectorToPlayer().x > 0;
        screen.getSpritesToAdd().add(new ShadeProjectile(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, playerToRight, false));
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        } else {
            safeToRemove = true;
        }
    }


    private void chasePlayer() {
        if (Math.abs(getVectorToPlayer().x) < 180 / AdventureGame.PPM
                && Math.abs(getVectorToPlayer().y) < 20 / AdventureGame.PPM) {
            if (playerIsToTheRight()) {
                runRight();
            } else {
                runLeft();
            }
        }
    }

    protected State getState() {
        if (setToDie) {
            return State.DYING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
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
    public void damage(int amount) {
        if (invincibilityTimer < 0) {
            health -= amount;
            invincibilityTimer = INVINCIBILITY_TIME;
            hurtTimer = HURT_TIME;
        }
        if (flashRedTimer < 0) {
            flashRedTimer = FLASH_RED_TIME;
        }
        screen.getDamageNumbersToAdd().add(new DamageNumber(screen, b2body.getPosition().x - getWidth() / 2 + 0.4f
                , b2body.getPosition().y - getHeight() / 2 + 0.2f, false, amount));
        showHealthBar = true;
    }

    @Override
    public boolean notDamagedRecently() {
        return (invincibilityTimer < 0);
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
        } else {
            if (runningRight && !region.isFlipX()) {
                region.flip(true, false);
            }
        }
    }

    private boolean playerIsToTheRight() {
        return getVectorToPlayer().x > 0;
    }

    private void runRight() {
        b2body.setLinearVelocity(1f, 0f);
    }

    private void runLeft() {
        b2body.setLinearVelocity(-1f, 0f);
    }

    private boolean playerInAttackRange() {
        return (Math.abs(getVectorToPlayer().x) < 140 / AdventureGame.PPM);

    }

    private void goIntoAttackState() {
        stateTimer = 0;
        attackTimer = ATTACK_RATE;
    }


    private boolean attackCooldownOver() {
        return attackCooldown < 0;
    }

    @Override
    protected Shape getHitBoxShape() {
        PolygonShape shape = new PolygonShape();
        shape.set(SHADE_HITBOX);
        return shape;
    }

    private boolean currentFrameIsAnAttack() {
        return (currentState == State.ATTACKING && stateTimer > 0.5f);
    }
}
