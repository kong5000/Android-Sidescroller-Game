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
import com.mygdx.adventuregame.items.Item;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Effects.Vortex;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.MonsterTile;
import com.mygdx.adventuregame.sprites.Projectiles.VerticalFireBall;
import com.mygdx.adventuregame.sprites.Projectiles.VerticalIce;
import com.mygdx.adventuregame.sprites.player.Player;

import java.util.ArrayList;

public class IceGolem extends Enemy {
    private static final float[] CHARGE_HITBOX = {
            -0.16f, 0.1f,
            -0.16f, -0.3f,
            0.16f, -0.3f,
            0.16f, 0.1f};
    private static final float[] MINOTAUR_HITBOX = {
            -0.18f, 0.12f,
            -0.18f, -0.25f,
            0.18f, -0.25f,
            0.18f, 0.12f};
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

    private static final float MAX_HORIZONTAL_SPEED = 1.1f;
    private static final float MAX_VERTICAL_SPEED = 3;

    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 72;
    private static final int HEIGHT_PIXELS = 57;

    private static final float CORPSE_EXISTS_TIME = 1.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.3f;
    private static final float STUN_TIME = 0.5f;
    private static final float STRONG_ATTACK_COOLDOWN = 4;

    private float attackTimer;
    private float chargeTimer;
    private float deathTimer;
    private int damageForStun = 0;

    private boolean charging = false;
    float chargeDirection = 2.2f;
    private float strongAttackTimer = -1f;
    private boolean strongAttacking = false;
    private Animation<TextureRegion> fastAttackAnimation;
    private Animation<TextureRegion> fastAttackAnimationDamaged;
    private Animation<TextureRegion> launchBallAnimation;
    private Animation<TextureRegion> jumpAnimation;

    private ArrayList<Float> projectileCoords;
    private Array<MonsterTile> monsterTiles;
    private boolean chargeStarted = false;
    private boolean setToDie = false;

    private Fixture attackFixture;
    private Fixture chargeFixture;
    private static final float CHARGE_COOLDOWN = 9;
    private float SPECIAL_COOLDOWN = 1f;
    private float specialCooldownTimer = SPECIAL_COOLDOWN;
    private float specialAttackTimer = -1;
    private static final float SPECIAL_ATTACK_TIME = 3;

    public IceGolem(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("ice_golem_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_die"),
                9, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_attack1"),
                7, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);

        attackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("ice_golem_attack1"),
                7, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);


        fastAttackAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_attack2"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);
        fastAttackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("ice_golem_attack2"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);


        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        hurtAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("ice_golem_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        hurtAnimationDamaged.setPlayMode(Animation.PlayMode.LOOP_REVERSED);


        idleAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("ice_golem_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        launchBallAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_attack2"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);
        launchBallAnimation.setPlayMode(Animation.PlayMode.LOOP);

        jumpAnimation = generateAnimation(screen.getAtlas().findRegion("ice_golem_jump"),
                2, WIDTH_PIXELS, HEIGHT_PIXELS, 0.12f);
        jumpAnimation.setPlayMode(Animation.PlayMode.LOOP);
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
        health = 75;
        barYOffset = 0.02f;
        monsterTiles = new Array<>();
        setScale(1.25f);
        projectileCoords = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            projectileCoords.add(getX() - 4f + i * 0.7f);
        }
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


        if (setToDestroy && !destroyed) {
//            screen.getSpritesToAdd().add(new FireElemental(screen, b2body.getPosition().x + 0.25f, b2body.getPosition().y + 0.1f, true));
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
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            }
            updateStateTimers(dt);
            setRegion(getFrame(dt));
            if(active){
                act(dt);
            }

            limitSpeed();
        }
    }

    private void updateStateTimers(float dt) {
        if (specialAttackTimer > 0) {
            specialAttackTimer -= dt;
        }
        if (specialCooldownTimer > 0) {
            specialCooldownTimer -= dt;
        }
        if (chargeTimer > 0) {
            chargeTimer -= dt;
        } else {
            if (playerInChargeRange()) {
                chargeTimer = CHARGE_COOLDOWN;
                charging = true;
                specialAttackTimer = SPECIAL_ATTACK_TIME;
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
                    screen.getSpritesToAdd().add(new Item(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, AdventureGame.BLUE_KEY));

                }
            }
        }
        if (currentState == State.CHASING) {
            chasePlayer();
            if (playerInLungeRange()) {
                if (strongAttackTimer <= 0) {
                    strongAttackTimer = STRONG_ATTACK_COOLDOWN;
//                    lungeAtPlayer();
                }
            }
            if (playerInAttackRange()) {
                goIntoAttackState();
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
            if (specialAttackTimer < 0) {
                endCharge();
            }
//            if(runningRight){
//                charge(chargeDirection);
//            }else {
//                charge(-chargeDirection);
//            }
            if (specialCooldownTimer < 0) {
                launchProjectiles();
                specialCooldownTimer = SPECIAL_COOLDOWN;
            }
        }
        if (attackTimer > 0) {
            attackTimer -= dt;
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
                texture = launchBallAnimation.getKeyFrame(stateTimer);
                break;
            case ATTACKING:

                texture = attackAnimation.getKeyFrame(stateTimer);

                attackEnabled = true;
                break;
            case HURT:
                attackEnabled = false;
                texture = hurtAnimation.getKeyFrame(stateTimer);
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
        charging = false;
        chargeStarted = false;
        strongAttackTimer = STRONG_ATTACK_COOLDOWN;
    }

    protected State getState() {
        if(!active) {
            return State.IDLE;
        }
        if (setToDie) {
            return State.DYING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (charging) {
            return State.CHARGING;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) < 430 / AdventureGame.PPM) {
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
            active = true;
            damageForStun += 1;
        }
        super.damage(amount);
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

    private void runRight() {
//        b2body.setLinearVelocity(1.1f, b2body.getLinearVelocity().y);
        b2body.applyLinearImpulse(new Vector2(0.175f, 0), b2body.getWorldCenter(), true);

    }

    private void runLeft() {
//        b2body.setLinearVelocity(-1.1f, b2body.getLinearVelocity().y);
        b2body.applyLinearImpulse(new Vector2(-0.175f, 0), b2body.getWorldCenter(), true);

    }

    private boolean playerInAttackRange() {
        return (Math.abs(getVectorToPlayer().x) < 35 / AdventureGame.PPM);
    }

    private boolean playerInLungeRange() {
        return (Math.abs(getVectorToPlayer().x) < 150 / AdventureGame.PPM && Math.abs(getVectorToPlayer().x) > 101 / AdventureGame.PPM);
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

    private void launchProjectiles() {
        for (float i : projectileCoords) {
            VerticalIce fireBall = new VerticalIce(screen, i, getY() + getHeight() / 2 + 2.5f);
            screen.getSpritesToAdd().add(fireBall);
        }

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
    private boolean playerInActivationRange() {
        return (Math.abs(getVectorToPlayer().len()) < 150 / AdventureGame.PPM);
    }
}
