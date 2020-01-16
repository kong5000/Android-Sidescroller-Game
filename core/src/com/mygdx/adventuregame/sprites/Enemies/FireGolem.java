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
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.Xplosion;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.FireSpinEffect;
import com.mygdx.adventuregame.sprites.GolemFireAttack;
import com.mygdx.adventuregame.sprites.MonsterTile;
import com.mygdx.adventuregame.sprites.Projectiles.VerticalFireBall;

import java.util.ArrayList;

public class FireGolem extends Enemy {
    private static final float[] MINOTAUR_HITBOX = {
            -0.15f, 0.1f,
            -0.15f, -0.35f,
            0.15f, -0.35f,
            0.15f, 0.1f};
    private static final float[] FIRE_SHIELD_HITBOX = {
            -0.15f, 0.1f,
            -0.275f, 0f,
            -0.275f, -0.2f,
            -0.15f, -0.35f,
            0.275f, 0f,
            0.275f, -0.2f,
            0.15f, -0.35f,
            0.15f, 0.1f,
    };
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

    private static final float ATTACK_RATE = 1f;

    private static final int WIDTH_PIXELS = 66;
    private static final int HEIGHT_PIXELS = 59;

    private static final float CORPSE_EXISTS_TIME = 1.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.3f;

    private float attackTimer;

    private float deathTimer;

    private Animation<TextureRegion> chargeAnimation;
    private Animation<TextureRegion> launchBallAnimation;

    private Array<MonsterTile> monsterTiles;

    private boolean setToDie = false;

    private Fixture attackFixture;
    private Fixture fireShieldFixture;

    private float attackCycleTimer = 6;
    private boolean shieldOn = false;
    private float shieldTimer = 5;
    private float chargingTimer = 0;
    private PlayScreen screen;
    GolemFireAttack fireAttack;
    private float angleToPlayer;
    private FireSpinEffect fireSpinEffect;
    private float shieldDamageTick = 0.25f;
    private float attackCooldownTimer;
    private static final float ATTACK_COOLDOWN = 2f;
    private float playerCloseTimer = 0;
    private ArrayList<Float> projectileCoords;

    private float projectileTimer = -1;
    private static final float PROJECTILE_RATE = 1;
    private float specialAttackCooldownTimer = -1;
    private static final float SPECIAL_ATTACK_COOLDOWN = 5;
    private float specialAttackTimer = -1;
    private static final float SPECIAL_ATTACK_TIME = 5;

    public FireGolem(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        this.screen = screen;
        chargeAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_charge_ball"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);

        walkAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("fire_golem_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);

        deathAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_die"),
                9, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);

        attackAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_rapid_attack"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);


        attackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("fire_golem_rapid_attack"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);


        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        hurtAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("fire_golem_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);

        idleAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("fire_golem_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        launchBallAnimation = generateAnimation(screen.getAtlas().findRegion("fire_golem_launch_ball"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);

//        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setBounds(getX(), getY(), WIDTH_PIXELS * 1.3f / AdventureGame.PPM, HEIGHT_PIXELS * 1.3f / AdventureGame.PPM);

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
        health = 45;
        barYOffset = 0.02f;
        monsterTiles = new Array<>();
        attachNearbyTiles();
//        setScale(1.25f);

        angleToPlayer = getVectorToPlayer().angle();
        projectileCoords = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            projectileCoords.add(getX() - 4f + i * 0.7f);
        }
    }

    private void startFireShield() {
        fireSpinEffect = new FireSpinEffect(screen, getX(), getY(), this);
//        createShieldAttack();
        screen.getSpritesToAdd().add(fireSpinEffect);
        launchProjectiles();
    }

    private void stopFireShield() {
        disableFireShieldHitBox();
        fireSpinEffect.setToDestroy();
        fireSpinEffect = null;
    }

    private void startFireAttack() {
        disableAttackHitBox();
        fireAttack = new GolemFireAttack(screen, getX() - getWidth() / 2, getY() - getHeight() / 2, runningRight, this);
        screen.getSpritesToAdd().add(fireAttack);
        chargingTimer = 3f;
    }


    @Override
    public void update(float dt) {

        if (attackCycleTimer > 0) {
            attackCycleTimer -= dt;
        } else {
            attackCycleTimer = 7f;
            startFireAttack();
        }

        if (shieldOn) {

            if (shieldDamageTick > 0) {
                shieldDamageTick -= dt;
            } else {
                if (inRange()) {
                    screen.getPlayer().hurt(3);
                }
                shieldDamageTick = 0.75f;
            }
        } else {
            shieldDamageTick = -1;
        }
        if (shieldTimer > 0) {
            shieldTimer -= dt;
        } else if (shieldOn) {
//            stopFireShield();
            shieldOn = false;
        }

        if (chargingTimer > 0) {
            chargingTimer -= dt;
        } else {
            if (fireAttack != null) {
                fireAttack.stopCharging();

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
                    screen.getSpritesToAdd().add(new Xplosion(screen, getX() + 0.1f, getY() - getHeight() / 2 + 0.1f));
                }
            }
        }

        if (setToDestroy && !destroyed) {
            screen.getSpritesToAdd().add(new FireElemental(screen, b2body.getPosition().x, b2body.getPosition().y, true));
//            if(fireAttack != null){
//                fireAttack.setToDestroy();
//            }
//            if(fireSpinEffect != null){
//                fireSpinEffect.setToDestroy();
//            }
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
            for (MonsterTile monsterTile : monsterTiles) {
                monsterTile.setToDestroy();
            }
        } else if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            updateStateTimers(dt);
            setRegion(getFrame(dt));
            act(dt);
        }
    }

    private void updateStateTimers(float dt) {
        if (attackCooldownTimer > 0) {
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
        if(specialAttackTimer > 0){
            specialAttackTimer -= dt;
        }
    }

    private void act(float dt) {
        if (currentState == State.CHASING) {
            chasePlayer();

        }
        if(currentState == State.IDLE){
            if (playerInAttackRange()) {
                if (attackCooldownTimer <= 0) {
                    goIntoAttackState();
                    attackCooldownTimer = ATTACK_COOLDOWN;
                    lungeAtPlayer();
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

    private void disableFireShieldHitBox() {
        if (fireShieldFixture != null) {
            b2body.destroyFixture(fireShieldFixture);
            fireShieldFixture = null;
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
        } else if (specialAttackTimer > 0) {
            return State.SPECIAL_ATTACK;
        } else if (chargingTimer > 0) {
            if (!shieldOn) {
                shieldTimer = 3;
//                startFireShield();
                launchProjectiles();
                shieldOn = true;
            }
            if (chargingTimer < 0.35) {
                return State.CAST;
            }
            return State.CHARGING;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) > 35 / AdventureGame.PPM) {
            return State.CHASING;
        } else {
            b2body.setLinearVelocity(new Vector2(0, b2body.getLinearVelocity().y));
            return State.IDLE;
        }
    }

//    @Override
//    public void defineEnemy() {
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.position.set(getX(), getY());
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        b2body = world.createBody(bodyDef);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
//        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
//                | AdventureGame.PLAYER_SWORD_BIT
//                | AdventureGame.ARROW_BIT
//                | AdventureGame.FIRE_SPELL_BIT;
//        PolygonShape shape = new PolygonShape();
//        shape.set(MINOTAUR_HITBOX);
//
//        fixtureDef.shape = shape;
//        b2body.createFixture(fixtureDef).setUserData(this);
//    }

    @Override
    public void hitByFire() {
        screen.getExplosions().add(new Explosion(screen, getX(), getY()));

    }

    @Override
    public void hitOnHead() {
        damage(2);

    }

    private boolean inRange() {
        return (getVectorToPlayer().len() < 0.45f);
    }

    protected Vector2 getVectorToPlayer() {
        Vector2 enemyPosition = new Vector2(this.getX() + 0.22f, this.getY());
        Vector2 playerVector = new Vector2(screen.getPlayer().getX(), screen.getPlayer().getY());
        return playerVector.sub(enemyPosition);
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

    private void createShieldAttack() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_ATTACK_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;
        PolygonShape polygonShape = new PolygonShape();
        float[] hitbox = FIRE_SHIELD_HITBOX;
        polygonShape.set(hitbox);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        fixtureDef.density = 500f;
        fixtureDef.restitution = 0.7f;
        fireShieldFixture = b2body.createFixture(fixtureDef);
        fireShieldFixture.setUserData(this);
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
        b2body.setLinearVelocity(1f, b2body.getLinearVelocity().y);
    }

    private void runLeft() {
        b2body.setLinearVelocity(-1f, b2body.getLinearVelocity().y);
    }

    private boolean playerInAttackRange() {
        return (Math.abs(getVectorToPlayer().x) < 100 / AdventureGame.PPM);
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
        return (currentState == State.ATTACKING && stateTimer > 0.5f);
    }

    private boolean attackFramesOver() {
        if (currentState == State.ATTACKING) {
            return stateTimer > 0.7f;
        }
        return false;
    }


    private void attachNearbyTiles() {
        for (MonsterTile monsterTile : screen.monsterTiles) {
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

    public boolean isCharging() {
        return chargingTimer > 0;
    }

    private void launchProjectiles() {
        for (float i : projectileCoords) {
            VerticalFireBall fireBall = new VerticalFireBall(screen, i, getY() + getHeight() / 2 + 2f);
            screen.getSpritesToAdd().add(fireBall);
            fireBall.setRotation(90);
            fireBall.setVelocity(0, -1.5f);
        }

    }

}
