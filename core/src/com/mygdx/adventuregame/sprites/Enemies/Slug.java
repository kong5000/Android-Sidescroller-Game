package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Projectiles.GreenProjectile;

public class Slug extends Enemy {
    private static final float[] OGRE_HITBOX = {
            -0.2f, 0.12f,
            -0.2f, -0.175f,
            0.2f, -0.175f,
            0.2f, 0.12f};
    private static final float[] SWORD_HITBOX_RIGHT = {
            0.35f, -0.2f,
            0.35f, 0.1f,
            0.1f, -0.2f,
            0f, 0.15f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.35f, -0.2f,
            -0.35f, 0.1f,
            -0.1f, -0.2f,
            0f, 0.15f};
    private static final float BULLET_SPEED = 1;
    private static final float BULLET_ANGLE_INCREMENT = 35;
    private float startingAngle = 0;
    private static final float ATTACK_RATE = 2.75f;

    private static final int WIDTH_PIXELS = 79;
    private static final int HEIGHT_PIXELS = 41;

    private static final float CORPSE_EXISTS_TIME = 1.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.3f;
    private static final float MAX_HORIZONTAL_SPEED = 0.8f;
    private static final float MAX_VERTICAL_SPEED = 3;
    private static final float JUMP_COOLDOWN = 2;

    private static final String MOVE_ANIMATION_FILENAME = "slug_move";
    private static final String ATTACK_ANIMATION_FILENAME = "slug_attack1";
    private static final String IDLE_ANIMATION_FILENAME = "slug_idle";
    private static final String HURT_ANIMATION_FILENAME = "slug_hurt";
    private static final String DEATH_ANIMATION_FILENAME = "slug_die";

    private static final int MOVE_FRAME_COUNT = 5;
    private static final int ATTACK_FRAME_COUNT = 6;
    private static final int IDLE_FRAME_COUNT = 4;
    private static final int HURT_FRAME_COUNT = 3;
    private static final int DEATH_FRAME_COUNT = 6;

    private static final float MOVE_ANIMATION_FPS = 0.1f;
    private static final float ATTACK_ANIMATION_FPS = 0.1f;
    private static final float IDLE_ANIMATION_FPS = 0.07f;
    private static final float HURT_ANIMATION_FPS = 0.07f;
    private static final float DEATH_ANIMATION_FPS = 0.1f;


    private float attackTimer;
    private float jumpTimer = -1f;
    private float deathTimer;

    private boolean setToDie = false;

    private Fixture attackFixture;
    private boolean active;

    public Slug(PlayScreen screen, float x, float y) {
        super(screen, x, y);
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
        getEnemyAnimations().initHurtAnimation(
                HURT_ANIMATION_FILENAME,
                HURT_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                HURT_ANIMATION_FPS
        );
        getEnemyAnimations().initDeathAnimation(
                DEATH_ANIMATION_FILENAME,
                DEATH_FRAME_COUNT,
                WIDTH_PIXELS,
                HEIGHT_PIXELS,
                DEATH_ANIMATION_FPS
        );


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
        health = 12;
        barYOffset = 0.02f;
        setScale(1.3f);
    }

    @Override
    protected TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion texture;
        selectBrightFrameOrRegularFrame();
        switch (currentState) {
            case DYING:
                attackEnabled = false;
                texture = getEnemyAnimations().getDeathFrame(stateTimer);
                break;
            case JUMPING:
                attackEnabled = false;
                texture = getEnemyAnimations().getJumpFrame(stateTimer);
                break;
            case ATTACKING:
                texture = getEnemyAnimations().getAttackFrame(stateTimer);
                attackEnabled = true;
                break;
            case HURT:
                attackEnabled = false;
                texture = getEnemyAnimations().getHurtFrame(stateTimer);
                break;
            case CHASING:
                attackEnabled = false;
                texture = getEnemyAnimations().getMoveFrame(stateTimer);
                break;
            case IDLE:
            default:
                attackEnabled = false;
                texture = getEnemyAnimations().getIdleFrame(stateTimer);
                break;
        }
        orientTextureTowardsPlayer(texture);

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return texture;
    }

    @Override
    public void update(float dt) {
        checkIfEnemyActivated();
        correctHealthBarPosition();
        checkEnemyIsAlive();
        if (currentState == State.DYING) {
            deathTimer += dt;
            startExplosionWarningFlash();
            if (timeToRemoveCorpse()) {
                setToDestroy = true;
                if (!destroyed) {
                    launchProjectiles();
                    generateDeathExplosion();
                    //Todo copy for all enemy attack hitboxes
                    disableAttackHitBox();
                }
            }
        }
        if (currentState == State.ATTACKING) {
            if (attackFinished()) {
                leaveAttackState();
            }
        }
        if (setToDestroy && !destroyed) {
            destroyHitBox();
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            updateStateTimers(dt);
            setRegion(getFrame(dt));
            if (active) {
                act(dt);
            }
            setSpritePosition();
        }
    }

    private void destroyHitBox() {
        world.destroyBody(b2body);
    }

    private void leaveAttackState() {
        attackTimer = -1;
    }

    private boolean attackFinished() {
        return attackAnimation.isAnimationFinished(stateTimer - 0.2f);
    }

    private void startExplosionWarningFlash() {
        if (deathTimer > 0.85f && flashRedTimer < 0) {
            flashRedTimer = 2;
        }
    }

    private boolean timeToRemoveCorpse() {
        return deathTimer > CORPSE_EXISTS_TIME;
    }

    private void setSpritePosition() {
        if (runningRight) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2 - 0.2f, b2body.getPosition().y - getHeight() / 2);
        }
    }

    private void generateDeathExplosion() {
        float xOffset = 0f;
        if (runningRight) {
            xOffset = -0.1f;
        }
        SmallExplosion explosion = new SmallExplosion(screen, getX() + xOffset, getY() - getHeight());
        explosion.setScale(1.9f);
        screen.getSpritesToAdd().add(explosion);
    }

    private void checkEnemyIsAlive() {
        if (health <= 0) {
            if (!setToDie) {
                setToDie = true;
            }
        }
    }

    private void correctHealthBarPosition() {
        if (runningRight) {
            barXOffset = -0.2f;
        } else {
            barXOffset = -0.05f;
        }
    }

    private void checkIfEnemyActivated() {
        if (!active) {
            if (playerInActivationRange()) {
                active = true;
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
        if (attackFixture == null){
            createAttack();
        }

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


//    @Override
//    public void damage(int amount) {
//        if (isAlive()) {
//            if (invincibilityTimer < 0) {
//                health -= amount;
//                invincibilityTimer = INVINCIBILITY_TIME;
//            }
//            if (flashRedTimer < 0) {
//                flashRedTimer = FLASH_RED_TIME;
//            }
//            screen.getDamageNumbersToAdd().add(new DamageNumber(screen, b2body.getPosition().x - getWidth() / 2 + 0.4f
//                    , b2body.getPosition().y - getHeight() / 2 + 0.2f, false, amount));
//            showHealthBar = true;
//            b2body.applyLinearImpulse(new Vector2(0, 0.6f), b2body.getWorldCenter(), true);
//        }
//    }

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
//        b2body.setLinearVelocity(1.15f, b2body.getLinearVelocity().y);
        b2body.applyLinearImpulse(new Vector2(0.175f, 0), b2body.getWorldCenter(), true);

    }

    private void runLeft() {
//        b2body.setLinearVelocity(-1.15f, b2body.getLinearVelocity().y);
        b2body.applyLinearImpulse(new Vector2(-0.175f, 0), b2body.getWorldCenter(), true);
    }

    private boolean playerInAttackRange() {
        return (getVectorToPlayer().len() < 70 / AdventureGame.PPM);
    }

    private void jumpingAttackLeft() {
        b2body.applyLinearImpulse(new Vector2(-.5f, 2f), b2body.getWorldCenter(), true);
    }

    private void jumpingAttackRight() {
        b2body.applyLinearImpulse(new Vector2(.5f, 2f), b2body.getWorldCenter(), true);
    }

    private void goIntoAttackState() {
        screen.getSoundEffects().playSlugAttackSound();
        attackTimer = ATTACK_RATE;
    }

    private boolean currentFrameIsAnAttack() {
        return (currentState == State.ATTACKING && stateTimer > 0.65f);
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

    private void launchProjectiles() {
        for (int i = 0; i < 6; i++) {
            GreenProjectile fireBall = new GreenProjectile(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, false, false);
            screen.getSpritesToAdd().add(fireBall);

            float angle = startingAngle + BULLET_ANGLE_INCREMENT * i;

            fireBall.setRotation(angle + 180);

            float x = BULLET_SPEED * MathUtils.cos(angle * MathUtils.degreesToRadians);
            float y = BULLET_SPEED * MathUtils.sin(angle * MathUtils.degreesToRadians);
            fireBall.setVelocity(x, y);
        }
    }

    private boolean playerInActivationRange() {
        return (Math.abs(getVectorToPlayer().len()) < 150 / AdventureGame.PPM);
    }
}
