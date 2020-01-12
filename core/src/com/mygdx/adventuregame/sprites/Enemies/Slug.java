package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.Projectiles.GreenProjectile;

public class Slug extends Enemy {
    private static final float[] OGRE_HITBOX = {
            -0.2f, 0.12f,
            -0.2f, -0.175f,
            0.2f, -0.175f,
            0.2f, 0.12f};
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

    private float attackTimer;
    private float jumpTimer = -1f;
    private float deathTimer;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> walkAnimationDamaged;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> attackAnimationDamaged;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> hurtAnimationDamaged;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> idleAnimationDamaged;

    private boolean setToDie = false;

    private Fixture attackFixture;
    private boolean active;
    public Slug(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("slug_move"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("slug_move"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("slug_die"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("slug_attack1"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("slug_attack1"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("slug_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        hurtAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("slug_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("slug_idle"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("slug_idle"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);

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
    public void update(float dt) {
        if(!active){
            if(playerInActivationRange()){
                active = true;
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
                    launchProjectiles();
                    float xOffset = 0.2f;
                    if(runningRight){
                        xOffset = -0.1f;
                    }
                    SmallExplosion explosion = new SmallExplosion(screen, getX() + xOffset, getY() - getHeight());
                    explosion.setScale(1.9f);

                    screen.getSpritesToAdd().add(explosion);
//                    screen.getSpritesToAdd().add(new SmallExplosion(screen, getX() - getWidth() / 4, getY() - getHeight() - 0.1f));
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
            if (runningRight) {
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            } else {
                setPosition(b2body.getPosition().x - getWidth() / 2 - 0.2f, b2body.getPosition().y - getHeight() / 2);
            }
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

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion texture;
        switch (currentState) {
            case DYING:
                attackEnabled = false;
                texture = deathAnimation.getKeyFrame(stateTimer);
                break;
            case ATTACKING:
                texture = selectBrightFrameOrRegularFrame(attackAnimation, attackAnimationDamaged);
                attackEnabled = true;
                break;
            case HURT:
                attackEnabled = false;
                texture = selectBrightFrameOrRegularFrame(hurtAnimation, hurtAnimationDamaged);
                break;
            case CHASING:
                attackEnabled = false;
                texture = selectBrightFrameOrRegularFrame(walkAnimation, walkAnimationDamaged);
                break;
            case IDLE:
            default:
                attackEnabled = false;
                texture = selectBrightFrameOrRegularFrame(idleAnimation, idleAnimationDamaged);
                break;
        }


        orientTextureTowardsPlayer(texture);


        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return texture;
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

    private State getState() {
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
    public void damage(int amount) {
        if (isAlive()) {
            if (invincibilityTimer < 0) {
                health -= amount;
                invincibilityTimer = INVINCIBILITY_TIME;
            }
            if (flashRedTimer < 0) {
                flashRedTimer = FLASH_RED_TIME;
            }
            screen.getDamageNumbersToAdd().add(new DamageNumber(screen, b2body.getPosition().x - getWidth() / 2 + 0.4f
                    , b2body.getPosition().y - getHeight() / 2 + 0.2f, false, amount));
            showHealthBar = true;
            b2body.applyLinearImpulse(new Vector2(0, 0.6f), b2body.getWorldCenter(), true);
        }
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

    private void orientTextureTowardsPlayer(TextureRegion region) {
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
        return (Math.abs(getVectorToPlayer().x) < 55 / AdventureGame.PPM);
    }

    private void jumpingAttackLeft() {
        b2body.applyLinearImpulse(new Vector2(-.5f, 2f), b2body.getWorldCenter(), true);
    }

    private void jumpingAttackRight() {
        b2body.applyLinearImpulse(new Vector2(.5f, 2f), b2body.getWorldCenter(), true);
    }

    private void goIntoAttackState() {
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
