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
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Effects.Vortex;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.MonsterTile;

public class Executioner extends Enemy {
    private static final float[] MINOTAUR_HITBOX = {
            -0.15f, 0.08f,
            -0.15f, -0.275f,
            0.15f, -0.275f,
            0.15f, 0.08f};
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

    private static final float ATTACK_RATE = 1.75f;

    private static final int WIDTH_PIXELS = 87;
    private static final int HEIGHT_PIXELS = 59;

    private static final float CORPSE_EXISTS_TIME = 1.5f;
    private static final float INVINCIBILITY_TIME = 0.35f;
    private static final float FLASH_RED_TIME = 0.3f;
    private static final float STUN_TIME = 0.5f;
    private float attackTimer;

    private float deathTimer;
    private int damageForStun = 0;

    private Array<MonsterTile> monsterTiles;

    private boolean setToDie = false;

    private Fixture attackFixture;

    public Executioner(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("executioner_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("executioner_run_bright"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("executioner_die"),
                9, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("executioner_attack"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("executioner_attack_bright"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("executioner_hurt"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        hurtAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("executioner_hurt_bright"),
                3, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("executioner_idle"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("executioner_idle_bright"),
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
        monsterTiles = new Array<>();
        attachNearbyTiles();
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
        if (currentState == State.DYING) {
            if (deathAnimation.isAnimationFinished(stateTimer)) {
            }
            deathTimer += dt;
            if (deathTimer > CORPSE_EXISTS_TIME) {
                setToDestroy = true;
                if(!destroyed){
                    screen.getSpritesToAdd().add(new Vortex(screen, getX() +0.1f, getY() - getHeight()/2 + 0.1f));
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
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            updateStateTimers(dt);
            setRegion(getFrame(dt));
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
    }

    private void act(float dt) {
        if (currentState == State.CHASING) {
            chasePlayer();
            if (playerInAttackRange()) {
                goIntoAttackState();
//                lungeAtPlayer();
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


    @Override
    public void damage(int amount) {
        if (isAlive()) {
            damageForStun += 1;
            if(damageForStun > 3){
                hurtTimer = STUN_TIME;
                damageForStun = 0;
            }
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
        return (currentState == State.ATTACKING && stateTimer > 0.3f);
    }

    private boolean attackFramesOver() {
        if (currentState == State.ATTACKING) {
            return stateTimer > 0.5f;
        }
        return false;
    }


    private void attachNearbyTiles(){
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
}
