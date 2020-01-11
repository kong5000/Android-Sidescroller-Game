package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.GreenProjectile;
import com.mygdx.adventuregame.sprites.Projectiles.ImpSpell;
import com.mygdx.adventuregame.sprites.player.Player;


public class Imp extends Enemy {
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
    private static final float HURT_TIME = 0.3f;
    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 50;
    private static final int HEIGHT_PIXELS = 50;

    private static final float CORPSE_EXISTS_TIME = 1f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.4f;
    private static final float MOVE_TIME = 0.2f;
    private static final float HORIZONTAL_MOVE_TIME = 2f;
    private static final float ATTACK_COOLDOWN_TIME = 1.5f;
    private static final float BULLET_SPEED = 1.25f;
    private boolean wasFlyingRight = false;
    private float startPosition;
    private float endPosition;
    private float attackTimer;
    private float movementTimer = 0;
    private float x_movementTimer = 0;

    private float deathTimer;
    private boolean active = false;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> hurtAnimationBright;
    private Animation<TextureRegion> idleAnimation;

    private boolean setToDie = false;

    private Fixture attackFixture;
    private float attackCooldownTimer = 0.5f;
    private boolean hasFireProjectile = false;
    private static final float MAX_SPEED = 0.9f;
    private static final float SLOW_SPEED = 0.6f;
    private float movementSpeed = MAX_SPEED;

    public Imp(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("imp_move"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("imp_die"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("imp_attack"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("imp_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation= generateAnimation(screen.getAtlas().findRegion("imp_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimationBright= generateAnimation(screen.getAtlas().findRegion("imp_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);;



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
        active = true;
        startPosition = b2body.getPosition().x + 0.1f;
        endPosition = startPosition + 1.5f;
    }

    @Override
    public void update(float dt) {
        if (!active && playerInActivationRange()) {
            active = true;
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
            b2body.setLinearVelocity(b2body.getLinearVelocity().x * 0.97f, -1.5f);
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if (!destroyed) {
                    screen.getSpritesToAdd().add(new SmallExplosion(screen, getX() - getWidth() / 2, getY() - getHeight() + 0.1f));
                }
            }
        }

        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            if(currentState != State.DYING){
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            }else {
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 +0.05f);
            }
            updateStateTimers(dt);
            setRegion(getFrame(dt));
            act(dt);
        }
    }

    private void maintainHeight(){
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
    }

    private void updateStateTimers(float dt) {
        if(attackCooldownTimer > 0){
            attackCooldownTimer -= dt;
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

    private void act(float dt) {

        if (currentState == State.CHASING) {
            if(playerInAttackRange() && attackCooldownTimer < 0){
                goIntoAttackState();
                attackCooldownTimer = ATTACK_COOLDOWN_TIME;
            }
        }

        if (currentState == State.ATTACKING) {
            if (currentFrameIsAnAttack()) {
                if(!hasFireProjectile){
                    launchProjectiles();
                    hasFireProjectile = true;
                }
            }
            if (attackFramesOver()) {
                disableAttackHitBox();
            }
            if (attackAnimation.isAnimationFinished(stateTimer)) {
                attackTimer = -1f;
                hasFireProjectile = false;
            }
        }

        if(currentState != State.DYING){
            maintainHeight();
            chasePlayerHorizontal();
        }

    }

    private void stopMovement() {
        b2body.setLinearVelocity(new Vector2(0, b2body.getLinearVelocity().y));
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
                texture = attackAnimation.getKeyFrame(stateTimer);
                attackEnabled = true;
                break;
            case HURT:
                attackEnabled = false;
                texture = selectBrightFrameOrRegularFrame(hurtAnimation, hurtAnimationBright);
                break;
            case CHASING:
                attackEnabled = false;
                texture = walkAnimation.getKeyFrame(stateTimer, true);
                break;
            case IDLE:
            default:
                attackEnabled = false;
                texture = idleAnimation.getKeyFrame(stateTimer, true);
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
        if (getCurrentPosition() <= startPosition) {
            runRight();
            wasFlyingRight = true;
        } else if (getCurrentPosition() >= endPosition) {
            runLeft();
            wasFlyingRight = false;
        }

    }

    private void resumeFlight() {
        if (wasFlyingRight) {
            runRight();
        } else {
            runLeft();
            ;
        }
    }

    private float getCurrentPosition() {
        return b2body.getPosition().x;
    }

    private State getState() {
        if (setToDie) {
            return State.DYING;
        } else if (!active) {
            return State.IDLE;
        } else if(hurtTimer > 0){
            return State.HURT;
        }else if (attackTimer > 0) {
            return State.ATTACKING;
        }else {
            return State.CHASING;
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

    private void orientTextureTowardsPlayer(TextureRegion region) {
        if (currentState != State.DYING) {

            runningRight = b2body.getLinearVelocity().x >= 0;
            if (currentState == State.ATTACKING) {
                runningRight = getVectorToPlayer().x >= 0;
            }
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
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, 1);
    }

    private void moveUp() {
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, -1);
    }

    private void runRight() {
        b2body.setLinearVelocity(movementSpeed, 0);
    }

    private void runLeft() {
        b2body.setLinearVelocity(-movementSpeed, 0);
    }

    private boolean playerInAttackRange() {
        return (Math.abs(getVectorToPlayer().x) < 50 / AdventureGame.PPM);
    }

    private boolean playerInActivationRange() {
        return (Math.abs(getVectorToPlayer().len()) < 150 / AdventureGame.PPM);
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
                        | AdventureGame.PLAYER_PROJECTILE_BIT
                        | AdventureGame.FIRE_SPELL_BIT
                        | AdventureGame.GROUND_BIT;
        Shape hitBox = getHitBoxShape();
        fixtureDef.shape = hitBox;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    private void launchProjectiles() {
        float startingAngle = getVectorToPlayer().angle();
        ImpSpell fireBall = new ImpSpell(screen, getX() + getWidth() / 2, getY() + getHeight() / 2 + 0.15f, false, false);
        screen.getSpritesToAdd().add(fireBall);

        float angle = startingAngle;
        fireBall.setRotation(angle + 180);

        float x = BULLET_SPEED * MathUtils.cos(angle * MathUtils.degreesToRadians);
        float y = BULLET_SPEED * MathUtils.sin(angle * MathUtils.degreesToRadians);
        fireBall.setVelocity(x, y);

    }
}
