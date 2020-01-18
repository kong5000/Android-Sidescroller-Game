package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Chest;
import com.mygdx.adventuregame.sprites.Projectiles.FireBall;


public class FireElemental extends Enemy {
    private static final float ATTACK_RATE = 2f;

    private static final int WIDTH_PIXELS = 62;
    private static final int HEIGHT_PIXELS = 43;

    private static final float CORPSE_EXISTS_TIME = 1.1f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.4f;
    private static final float HURT_TIME = 0.3f;

    private float attackTimer;
    private float attackCooldown;


    private boolean canFireProjectile = true;

    private boolean specialDrop = false;


    public FireElemental(PlayScreen screen, float x, float y, boolean specialDrop) {
        super(screen, x, y);
        this.specialDrop = specialDrop;
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("fire_elemental_run"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("fire_elemental_die"),
                8, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("fire_elemental_attack"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("fire_elemental_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("fire_elemental_idle"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
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
        health = 5;
        barXOffset = -0.065f;
        barYOffset = 0.075f;

    }

    @Override
    public void update(float dt) {
        if (health <= 0) {
            setToDestroy = true;
        }
        if (setToDestroy && !destroyed) {
            if (specialDrop) {
                Chest chest = new Chest(screen, b2body.getPosition().x, b2body.getPosition().y, AdventureGame.FIRE_SPELLBOOK);
                chest.b2body.applyLinearImpulse(new Vector2(0, 3f), chest.b2body.getWorldCenter(), true);
                screen.getSpritesToAdd().add(chest);
            }
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 0.05f);
            updateStateTimers(dt);
        }
        setRegion(getFrame(dt));
        act();
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
        if (attackCooldown > 0) {
            attackCooldown -= dt;
        }
    }

    private void act() {
        if (currentState == State.ATTACKING) {
            if (attackAnimation.isAnimationFinished(stateTimer)) {
                attackTimer = -1f;
            }
        }
        if (currentState != State.ATTACKING) {
            canFireProjectile = true;
        }
        if (currentState == State.CHASING) {
            chasePlayer();
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
            if (stateTimer > 0.7f && canFireProjectile) {
                launchFireBall();
                canFireProjectile = false;
            }
        }
    }

    private void launchFireBall() {
        boolean playerToRight = getVectorToPlayer().x > 0;
        screen.projectilesToSpawn.add(new FireBall(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, playerToRight, false));
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTimer < CORPSE_EXISTS_TIME) {
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
        if (setToDestroy) {
            return State.DYING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) < 250 / AdventureGame.PPM
                && Math.abs(getVectorToPlayer().x) > 150 / AdventureGame.PPM) {
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
        b2body.setLinearVelocity(1f, 0f);
    }

    private void runLeft() {
        b2body.setLinearVelocity(-1f, 0f);
    }

    private boolean playerInAttackRange() {
        return  (Math.abs(getVectorToPlayer().x )< 180 / AdventureGame.PPM &&
                (Math.abs(getVectorToPlayer().y )< 180 / AdventureGame.PPM));

    }

    private void goIntoAttackState() {
        attackTimer = ATTACK_RATE;
    }


    private boolean attackCooldownOver() {
        return attackCooldown < 0;
    }

    @Override
    protected Shape getHitBoxShape() {
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / AdventureGame.PPM);
        return shape;
    }
}
