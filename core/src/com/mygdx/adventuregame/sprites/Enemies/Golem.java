
package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.BossAttack;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.Vortex;
import com.mygdx.adventuregame.sprites.MonsterTile;

public class Golem extends Enemy implements BossAttack {
    private static final float[] MINOTAUR_HITBOX = {
            -0.18f, 0.12f,
            -0.18f, -0.25f,
            0.18f, -0.25f,
            0.18f, 0.12f};
    private static final float[] SWORD_HITBOX_RIGHT = {
            0.4f, -0.4f,
            0.4f, 0.1f,
            0.1f, -0.4f,
            -0.2f, 0.3f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.4f, -0.4f,
            -0.4f, 0.1f,
            -0.1f, -0.4f,
            0.2f, 0.3f};

    private static final float[] CHARGE_HITBOX = {
            -0.16f, 0.1f,
            -0.16f, -0.35f,
            0.16f, -0.35f,
            0.16f, 0.1f};

    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 76;
    private static final int HEIGHT_PIXELS = 59;

    private static final float CORPSE_EXISTS_TIME = 1.5f;
    private static final float FLASH_RED_TIME = 0.3f;
    private static final float STUN_TIME = 0.5f;
    private static final float STRONG_ATTACK_COOLDOWN = 4;
    public static final int ATTACK_RANGE = 100;
    public static final int ACTIVATION_RANGE = 150;
    private static final float MAX_HORIZONTAL_SPEED = 1.1f;

    private float attackTimer;
    private float chargeTimer;
    private float deathTimer;
    private int damageForStun = 0;

    private boolean charging = false;
    float chargeDirection = 2.2f;
    private float strongAttackTimer = -1f;
    private boolean strongAttacking = false;
    private Animation<TextureRegion> fastAttackAnimation;


    private Array<MonsterTile> monsterTiles;
    private boolean chargeStarted = false;
    private boolean setToDie = false;

    private Fixture attackFixture;
    private Fixture chargeFixture;
    private static final float CHARGE_COOLDOWN = 8;
    private int spawnEnemyCounter = 0;

    public Golem(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        fastAttackAnimation = generateAnimation(screen.getAtlas().findRegion("golem_launch_ball"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("golem_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("golem_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);

        deathAnimation = generateAnimation(screen.getAtlas().findRegion("golem_die"),
                9, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);

        attackAnimation = generateAnimation(screen.getAtlas().findRegion("golem_rapid_attack"),
                11, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);


        attackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("golem_rapid_attack"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);


        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("golem_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.15f);
        hurtAnimation.setPlayMode(Animation.PlayMode.LOOP_REVERSED);

        hurtAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("golem_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.15f);
        hurtAnimationDamaged.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("golem_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("golem_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimationDamaged.setPlayMode(Animation.PlayMode.LOOP);


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
        health = 80;
        barYOffset = 0.02f;
        monsterTiles = new Array<>();
        setScale(1.1f);
    }

    @Override
    public void update(float dt) {
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
        if (currentState == State.ATTACKING) {
            if (!strongAttacking) {
                if (fastAttackAnimation.isAnimationFinished(stateTimer - 0.2f)) {
                    attackTimer = -1f;
                }
            } else if (strongAttacking) {
                if (attackAnimation.isAnimationFinished(stateTimer - 0.25f)) {
                    strongAttacking = false;

                }
            }
        }
        if (currentState == State.DYING) {
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if (!destroyed) {
                    screen.getSpritesToAdd().add(new Vortex(screen, getX() + 0.1f, getY() - getHeight() / 2 + 0.1f));
                }
            }
        }

        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
            for (MonsterTile monsterTile : monsterTiles) {
                monsterTile.setToDestroy();
            }
        } else if (!destroyed) {

            if (runningRight) {
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            } else {
                setPosition(b2body.getPosition().x - getWidth() / 2 - 0.10f, b2body.getPosition().y - getHeight() / 2);
            }
            updateStateTimers(dt);
            setRegion(getFrame(dt));
            act(dt);
        }
    }

    private void updateStateTimers(float dt) {
        if (chargeTimer > 0) {
            chargeTimer -= dt;
        } else {
            if (playerInChargeRange()) {
                chargeTimer = CHARGE_COOLDOWN;
                charging = true;
            }

        }
        if (strongAttackTimer > 0) {
            strongAttackTimer -= dt;
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
            chasePlayer();
            if (playerInAttackRange()) {
                if (strongAttackTimer <= 0) {
                    strongAttackTimer = STRONG_ATTACK_COOLDOWN;
                    goIntoAttackState();
                    lungeAtPlayer();
                    strongAttacking = true;
                } else {
                    goIntoAttackState();
                }

            }
        }
        if (currentState == State.ATTACKING) {
            if (currentFrameIsAnAttack()) {
                enableAttackHitBox();
            }
            if (attackFramesOver()) {
                disableAttackHitBox();
            }
        } else {
            disableAttackHitBox();
        }
        if (currentState == State.CHARGING) {
//            chargeDirection = -2.2f;
//            if (!chargeStarted) {
//                if(runningRight){
//                    chargeDirection *= -1;
//                }
//            }

            float speed = b2body.getLinearVelocity().len();
            if (b2body.getLinearVelocity().len() < 0.01f && stateTimer > 0.2f) {
                endCharge();

            }
            if (runningRight) {
                charge(chargeDirection);
            } else {
                charge(-chargeDirection);
            }
            enableChargeHitBox();
        } else {
            disableChargeHitBox();
        }
        if (attackTimer > 0) {
            attackTimer -= dt;
        }

        if (currentState != State.DYING) {
            if (spawnEnemyCounter > 2) {
//                screen.getSpritesToAdd().add(new EarthElemental(screen, b2body.getPosition().x, b2body.getPosition().y + 0.1f, runningRight));
                spawnEnemyCounter = 0;
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

    @Override
    protected TextureRegion getFrame(float dt) {
        currentState = getState();
        selectBrightFrameOrRegularFrame();
        TextureRegion texture;
        switch (currentState) {
            case DYING:
                attackEnabled = false;
                texture = deathAnimation.getKeyFrame(stateTimer);
                break;
            case CHARGING:
                texture = walkAnimation.getKeyFrame(stateTimer);
                ;
                break;
            case ATTACKING:
                if (strongAttacking) {
                    texture = attackAnimation.getKeyFrame(stateTimer);
                } else {
                    texture = fastAttackAnimation.getKeyFrame(stateTimer);
                }
                attackEnabled = true;
                break;
            case HURT:
                attackEnabled = false;
                texture = hurtAnimation.getKeyFrame(stateTimer);
                ;
                break;
            case CHASING:
                attackEnabled = false;
                texture = walkAnimation.getKeyFrame(stateTimer);
                break;
            case IDLE:
            default:
                attackEnabled = false;
                texture = idleAnimation.getKeyFrame(stateTimer);
                break;
        }
        if (currentState != State.CHARGING && currentState != State.HURT) {
//                orientTextureTowardsPlayer(texture);
//                orientTexture(texture);
            updatePlayerVector();
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

    private void disableChargeHitBox() {
        if (chargeFixture != null) {
            b2body.destroyFixture(chargeFixture);
            chargeFixture = null;
        }
    }

    private void enableAttackHitBox() {
        if (attackFixture == null)
            createAttack();
    }

    private void enableChargeHitBox() {
        if (chargeFixture == null)
            createChargeHitbox();
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

    private void charge(float direction) {
        b2body.setLinearVelocity(direction, b2body.getLinearVelocity().y);
        chargeStarted = true;
    }

    private void createChargeHitbox() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.BOSS_ATTACK_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(CHARGE_HITBOX);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = false;
        chargeFixture = b2body.createFixture(fixtureDef);
        chargeFixture.setUserData(this);
    }

    private void endCharge() {
        screen.getSpritesToAdd().add(new EarthElemental(screen, b2body.getPosition().x, b2body.getPosition().y + 0.1f, runningRight));
        disableChargeHitBox();
        charging = false;
        chargeStarted = false;
        hurtTimer = 3.5f;
        flashRedTimer = FLASH_RED_TIME;
        strongAttackTimer = STRONG_ATTACK_COOLDOWN;
        if (runningRight) {
            b2body.applyLinearImpulse(new Vector2(-.85f, 2.4f), b2body.getWorldCenter(), true);
        } else {
            b2body.applyLinearImpulse(new Vector2(.85f, 2.4f), b2body.getWorldCenter(), true);

        }

    }

    protected State getState() {
        if (setToDie) {
            return State.DYING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (charging) {
            return State.CHARGING;
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
            damageForStun += 1;
            if (damageForStun > 3) {
                hurtTimer = STUN_TIME;
                damageForStun = 0;
            }
            super.damage(amount);
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

    private void updatePlayerVector() {
        Vector2 vectorToPlayer = getVectorToPlayer();
        runningRight = vectorToPlayer.x > 0;
    }

    protected void orientTextureTowardsPlayer(TextureRegion region) {
        if (currentState != State.DYING) {

            if (!runningRight && region.isFlipX()) {
                region.flip(true, false);
            }
            if (runningRight && !region.isFlipX()) {
                region.flip(true, false);
            }
        }
    }

    private void orientTexture(TextureRegion region) {
        if (currentState != State.DYING && currentState != State.CHARGING) {
            float x = b2body.getLinearVelocity().x;

            if (x < 0 && region.isFlipX()) {
                region.flip(true, false);
            }
            if (x > 0 && !region.isFlipX()) {
                region.flip(true, false);
            }
        }
    }

    private boolean playerIsToTheRight() {
        return getVectorToPlayer().x > 0;
    }

    private boolean playerInChargeRange() {
        return (Math.abs(getVectorToPlayer().x) > 30 / AdventureGame.PPM);
    }

    private void jumpingAttackLeft() {
        b2body.applyLinearImpulse(new Vector2(-.65f, 2.1f), b2body.getWorldCenter(), true);
    }

    private void jumpingAttackRight() {
        b2body.applyLinearImpulse(new Vector2(.65f, 2.1f), b2body.getWorldCenter(), true);
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


    public void attachNearbyTiles(Array<MonsterTile> worldTiles) {
        for (MonsterTile monsterTile : worldTiles) {
            Vector2 enemyPosition = new Vector2(this.getX(), this.getY());
            Vector2 tileVector = new Vector2(monsterTile.getX(), monsterTile.getY());
            float distance = enemyPosition.sub(tileVector).len();
            if (distance < 8f) {
                monsterTiles.add(monsterTile);
            }
        }
    }

    @Override
    protected Shape getHitBoxShape() {
        PolygonShape shape = new PolygonShape();
        shape.set(MINOTAUR_HITBOX);
        return shape;
    }

    @Override
    public void onPlayerHit() {

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
