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


public class Kobold extends Enemy {
    private static final float[] KOBOLD_HITBOX = {
            -0.17f, 0.1f,
            -0.17f, -0.15f,
            0.15f, -0.15f,
            0.15f, 0.1f};
    private static final float[] SPEAR_HITBOX_RIGHT = {
            0.3f, -0.1f,
            0.3f, 0.00f,
            0.1f, -0.1f,
            0.1f, 0.00f};
    private static final float[] SPEAR_HITBOX_LEFT = {
            -0.3f, -0.1f,
            -0.3f, 0.00f,
            -0.1f, -0.1f,
            -0.1f, 0.00f};
    private static final float HURT_TIME = 0.3f;
    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 68;
    private static final int HEIGHT_PIXELS = 35;

    private static final float CORPSE_EXISTS_TIME = 0.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.4f;

    private float attackTimer;


    private float deathTimer;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> hurtAnimationBright;
    private Animation<TextureRegion> idleAnimation;

    private boolean setToDie = false;

    private Fixture attackFixture;



    public Kobold(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("kobold_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("kobold_die"),
                7, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("kobold_attack"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("kobold_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        hurtAnimationBright = generateAnimation(screen.getAtlas().findRegion("kobold_hurt_bright"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("kobold_idle"),
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
        health = 4;
        barYOffset = 0.09f;
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
        if (currentState == State.DYING) {
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if(!destroyed){
                    screen.getSpritesToAdd().add(new SmallExplosion(screen, getX() - getWidth()/4, getY() - getHeight() - 0.1f));
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
    public void damage(int amount) {
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
        }
    }

    private boolean playerIsToTheRight() {
        return getVectorToPlayer().x > 0;
    }

    private void runRight() {
        b2body.setLinearVelocity(1f, b2body.getLinearVelocity().y);
    }

    private void runLeft() {
        b2body.setLinearVelocity(-1f, b2body.getLinearVelocity().y);
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

}
