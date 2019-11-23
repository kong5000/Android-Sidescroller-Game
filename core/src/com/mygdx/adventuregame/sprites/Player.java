package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

import java.util.Random;
import java.util.UUID;

public class Player extends Sprite {
    private static final float HURT_TIME = 0.5f;
    private static final float CAST_TIME = 0.5f;
    private static final float ATTACK_TIME = 0.35f;
    private static final float FLIP_TIME = 0.4f;
    private static final float MAX_VERTICLE_SPEED = 3f;
    private static final float[] SWORD_HITBOX_AIR = {
            -0.25f, -0.1f,
            -0.25f, 0.1f,
            0f, -0.2f,
            0.25f, -0.1f,
            0.25f, 0.1f,
            0, 0.2f};

    private static final float[] SWORD_HITBOX_RIGHT = {
            0f, -0.2f,
            0.25f, -0.1f,
            0.25f, 0.1f,
            0, 0.2f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.25f, -0.1f,
            -0.25f, 0.1f,
            0f, -0.2f,
            0, 0.2f};


    public enum State {FALLING, JUMPING, STANDING, RUNNING, HURT, ATTACKING, AIR_ATTACKING, FLIPPING, CASTING, DODGING}

    public enum Spell {FIREBALL, SHIELD}

    private Spell equipedSpell = Spell.FIREBALL;
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerIdle;
    private Animation<TextureRegion> playerFall;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerHurt;
    private Animation<TextureRegion> playerDie;
    private Animation<TextureRegion> playerAttack;
    private Animation<TextureRegion> playerAttack2;
    private Animation<TextureRegion> playerAttack3;
    private Animation<TextureRegion> playerAirAttack;
    private Animation<TextureRegion> playerFlip;
    private Animation<TextureRegion> playerCast;
    private Animation<TextureRegion> playerDodge;
    private Animation<TextureRegion> playerBow;

    private boolean canDodge = false;
    public boolean chargingSpell = false;
    public boolean runningRight;
    private boolean flipEnabled;
    private float stateTimer;
    private float hurtTimer;
    private float attackTimer;
    private float flipTimer;
    private float castTimer;
    private float shieldTimer;
    private float dodgeTimer = 0;
    public static final float SHIELD_TIME = 3.5f;
    private float castCooldown;
    private static final float CAST_RATE = 1f;

    private boolean canFireProjectile;
    private boolean passThroughFloor = false;

    TextureAtlas textureAtlas;

    private Fixture swordFixture;

    private int health;
    private static final int FULL_HEALTH = 20;
    public static float movementSpeed = 1f;

    private PlayScreen screen;

    private MagicShield magicShield;
    private FireSpell fireSpell;

    private float comboTimer;
    private int attackNumber = 0;
    private UUID currentAttackId;

    //Todo firespell blowsup box obstacles
    public Player(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("player_idle1"));
        this.world = world;
        this.screen = screen;

        textureAtlas = screen.getAtlas();
        currentState = State.STANDING;
        previousState = State.STANDING;
        hurtTimer = -1f;
        stateTimer = 0;
        attackTimer = ATTACK_TIME;
        flipTimer = FLIP_TIME;
        castTimer = -1f;
        flipEnabled = true;
        runningRight = true;
        health = FULL_HEALTH;
        canFireProjectile = true;
        castCooldown = -1;
        comboTimer = 0;

        playerRun = generateAnimation(textureAtlas.findRegion("player_run"), 6, 52, 39, 0.1f);
        playerIdle = generateAnimation(textureAtlas.findRegion("player_idle1"), 3, 52, 39, 0.2f);
        playerFall = generateAnimation(textureAtlas.findRegion("player_fall"), 2, 52, 39, 0.1f);
        playerJump = generateAnimation(textureAtlas.findRegion("player_jump"), 4, 52, 39, 0.1f);
        playerHurt = generateAnimation(textureAtlas.findRegion("player_hurt"), 3, 52, 39, 0.1f);
        playerDie = generateAnimation(textureAtlas.findRegion("player_die"), 7, 52, 39, 0.1f);
        playerAttack = generateAnimation(textureAtlas.findRegion("player_attack1"), 5, 52, 39, 0.07f);
        playerAttack2 = generateAnimation(textureAtlas.findRegion("player_attack2"), 6, 50, 37, 0.0575f);
        playerAttack3 = generateAnimation(textureAtlas.findRegion("player_attack3"), 6, 50, 37, 0.0575f);
        playerAirAttack = generateAnimation(textureAtlas.findRegion("player_air_attack1"), 4, 52, 39, 0.125f);
        playerFlip = generateAnimation(textureAtlas.findRegion("player_flip"), 4, 52, 39, 0.1f);
        playerCast = generateAnimation(textureAtlas.findRegion("player_cast"), 4, 52, 39, 0.1f);
        playerDodge = generateAnimation(textureAtlas.findRegion("player_dodge"), 5, 50, 37, 0.07f);

        definePlayer();
        playerStand = new TextureRegion(getTexture(), 0, 0, 50, 37);
        setBounds(0, 0, 50 / AdventureGame.PPM, 37 / AdventureGame.PPM);
        setRegion(playerStand);

        magicShield = new MagicShield(screen, b2body.getPosition().x, b2body.getPosition().y, this);
        magicShield.setAlpha(0);
    }

    private void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(50 / AdventureGame.PPM, 450 / AdventureGame.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
        ;
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        b2body.createFixture(fixtureDef).setUserData(this);


//        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_SWORD_BIT;
//        fixtureDef.filter.maskBits = AdventureGame.ENEMY_BIT;
//        PolygonShape polygonShape = new PolygonShape();
//        polygonShape.set(SWORD_HITBOX_AIR);
//        fixtureDef.shape = polygonShape;
//        fixtureDef.isSensor = false;
//        b2body.createFixture(fixtureDef).setUserData(this);


//        EdgeShape boot = new EdgeShape();
//        boot.set(
//                new Vector2(-8 / AdventureGame.PPM, -17 / AdventureGame.PPM),
//                new Vector2(8 / AdventureGame.PPM, -17 / AdventureGame.PPM)
//        );
//        fixtureDef.shape = boot;
//        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BOOT_BIT;
//        fixtureDef.filter.maskBits = AdventureGame.ENEMY_HEAD_BIT;
//        fixtureDef.isSensor =false;
//        b2body.createFixture(fixtureDef).setUserData(this);

    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if (hurtTimer > 0) {
            hurtTimer -= dt;
        }
        if (attackTimer > 0) {
            attackTimer -= dt;
        } else {
            if (swordFixture != null) {
                b2body.destroyFixture(swordFixture);
                swordFixture = null;
            }
        }
        if (flipTimer > 0) {
            flipTimer -= dt;
        }

        if (castTimer > 0) {
            castTimer -= dt;
        }
        if (castCooldown > 0) {
            castCooldown -= dt;
        }
        if (shieldTimer > 0) {
            shieldTimer -= dt;
        }
        if (comboTimer > 0) {
            comboTimer -= dt;
        } else {
            attackNumber = 0;
        }
        if (shieldTimer < 0) {
            magicShield.setAlpha(0);
        }
        if (dodgeTimer > 0) {
            passThroughFloor = true;
            dodgeTimer -= dt;
        } else {
            passThroughFloor = false;
        }

        if (currentState == State.CASTING) {
            if (stateTimer > 0.1f && canFireProjectile) {
                castFireSpell();
//                launchFireBall();
                canFireProjectile = false;
            }
        }
        magicShield.update(dt);

    }

    private void castFireSpell() {

        screen.spellsToSpawn.add(new FireSpell(screen, getX() - getWidth() / 2, getY() - getHeight() / 2, runningRight, this));
    }

    @Override
    public void draw(Batch batch) {

        super.draw(batch);
        magicShield.draw(batch);
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DODGING:
                region = playerDodge.getKeyFrame(stateTimer);
                break;
            case CASTING:
                region = playerCast.getKeyFrame(stateTimer, true);
                break;
            case HURT:
                region = playerHurt.getKeyFrame(stateTimer);
                break;
            case ATTACKING:
                if (attackNumber == 0) {
                    region = playerAttack.getKeyFrame(stateTimer);
                } else if (attackNumber == 1) {
                    region = playerAttack2.getKeyFrame(stateTimer);
                } else {
                    region = playerAttack3.getKeyFrame(stateTimer);
                }

                break;
            case AIR_ATTACKING:
                region = playerAirAttack.getKeyFrame(stateTimer);
                break;
            case FLIPPING:
                region = playerFlip.getKeyFrame(stateTimer);
                break;
            case JUMPING:
                region = playerJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
                region = playerFall.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = playerIdle.getKeyFrame(stateTimer, true);
                break;
        }

        if ((!runningRight) && !region.isFlipX()) {
            region.flip(true, false);
//            runningRight = false;
        } else if (runningRight && region.isFlipX()) {
            region.flip(true, false);
//            runningRight = true;
        }
        if (currentState == State.AIR_ATTACKING) {
            attackNumber = 0;
            comboTimer = -1;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (castTimer > 0 || chargingSpell) {
            return State.CASTING;
        } else if (attackTimer > 0) {
            if (currentState == State.ATTACKING) {
                if (attackNumber == 0) {
                    if (playerAttack.isAnimationFinished(stateTimer)) {
                        return State.RUNNING;
                    }
                }
                if (attackNumber == 1) {
                    if (playerAttack2.isAnimationFinished(stateTimer)) {
                        return State.RUNNING;
                    }
                }
                if (attackNumber == 2) {
                    if (playerAttack3.isAnimationFinished(stateTimer)) {
                        return State.RUNNING;
                    }
                }

            }
            if (Math.abs(b2body.getLinearVelocity().y) > 0) {
                return State.AIR_ATTACKING;
            }
            return State.ATTACKING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (flipTimer > 0) {
            return State.FLIPPING;
        } else if (b2body.getLinearVelocity().y > 0) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (dodgeTimer > 0) {
            return State.DODGING;
        } else if (b2body.getLinearVelocity().x != 0) {
            flipEnabled = true;
            return State.RUNNING;
        } else {
            flipEnabled = true;
            return State.STANDING;
        }
    }

    protected Animation<TextureRegion> generateAnimation(
            TextureRegion textureRegion,
            int numberOfFrames,
            int widthInPixels,
            int heightInPixels,
            float secondsPerFrame) {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < numberOfFrames; i++) {
            frames.add(new TextureRegion(
                    textureRegion,
                    i * widthInPixels,
                    0,
                    widthInPixels,
                    heightInPixels
            ));
        }
        Animation<TextureRegion> animation = new Animation<TextureRegion>(secondsPerFrame, frames);
        return animation;
    }

    public void hurt(int damage) {
        hurtTimer = HURT_TIME;
        health -= damage;
        endChargingSpell();
        screen.getDamageNumbersToAdd().add(new DamageNumber(screen, b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2, true, damage));
    }

    public void jump() {
        if (currentState == State.JUMPING || currentState == State.FALLING || currentState == State.FLIPPING || currentState == State.AIR_ATTACKING) {
            if (flipEnabled) {
                flipTimer = FLIP_TIME;
                flipEnabled = false;
                b2body.applyLinearImpulse(new Vector2(0, 6f), b2body.getWorldCenter(), true);
                if (b2body.getLinearVelocity().y > MAX_VERTICLE_SPEED) {
                    b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICLE_SPEED);
                }

            }
        } else if (currentState != State.JUMPING && currentState != State.FALLING) {
            if (canDodge) {
                dodge();
            } else {
                b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);
            }
        }
    }

    public void attack() {
        b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);

        if (attackTimer < 0) {

            attackTimer = ATTACK_TIME;
            if (swordFixture == null) {
                createAttack();
            }
            if (attackNumber == 1) {
                if (runningRight) {
                    b2body.applyLinearImpulse(new Vector2(1f, 0), b2body.getWorldCenter(), true);
                } else {
                    b2body.applyLinearImpulse(new Vector2(-1f, 0), b2body.getWorldCenter(), true);
                }
            }
            if (comboTimer > 0) {
                attackNumber++;
                if (attackNumber > 2) {
                    attackNumber = 0;
                }
            }
        }
        comboTimer = 0.9f;
        b2body.setAwake(true);
    }

    public State getCurrentState() {
        return currentState;
    }

    public boolean isSwinging() {
        if (currentState == State.ATTACKING || currentState == State.AIR_ATTACKING) {
            return true;

        }
        return false;
    }

    private void createAttack() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_SWORD_BIT;
        fixtureDef.filter.maskBits = AdventureGame.ENEMY_BIT;
        PolygonShape polygonShape = new PolygonShape();
        float[] hitbox;
        switch (currentState) {
            case JUMPING:
            case FLIPPING:
            case AIR_ATTACKING:
            case FALLING:
                hitbox = SWORD_HITBOX_AIR;
                break;
            default:
                if (runningRight) {
                    hitbox = SWORD_HITBOX_RIGHT;
                } else {
                    hitbox = SWORD_HITBOX_LEFT;
                }
                break;
        }
        if (attackNumber == 1) {
            hitbox = SWORD_HITBOX_AIR;
        }
        polygonShape.set(hitbox);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = false;
        swordFixture = b2body.createFixture(fixtureDef);
        swordFixture.setUserData(this);
        currentAttackId = UUID.randomUUID();
    }

    public int getHealth() {
        return health;
    }

    public boolean notInvincible() {
        return hurtTimer < 0;
    }

    public void castSpell() {
        if (equipedSpell == Spell.FIREBALL) {
            if (castCooldown < 0) {
                castTimer = CAST_TIME;
                castCooldown = CAST_RATE;
                canFireProjectile = true;
            }
        } else {
            if (castCooldown < 0) {
                castTimer = CAST_TIME;
                castCooldown = CAST_RATE;
                shieldTimer = SHIELD_TIME;
                magicShield.setAlpha(1);
            }
        }

    }

    private void launchFireBall() {
        boolean ballDirectionRight;
        if (runningRight) {
            ballDirectionRight = true;
        } else {
            ballDirectionRight = false;
        }
        screen.projectilesToSpawn.add(new FireBall(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, ballDirectionRight, true));
    }

    public void switchSpell() {
        if (equipedSpell == Spell.FIREBALL) {
            equipedSpell = Spell.SHIELD;
        } else {
            equipedSpell = Spell.FIREBALL;
        }
    }

    public Spell getEquipedSpell() {
        return equipedSpell;
    }

    public void setChargingSpell() {
        chargingSpell = true;
    }

    public void endChargingSpell() {
        chargingSpell = false;
    }

    public void stopChargingAnimation() {
        playerCast.setFrameDuration(4f);
    }

    public void startChargingAnimation() {
        playerCast.setFrameDuration(0.1f);
    }

    public void setRunningRight(boolean state) {
        runningRight = state;
    }

    public int getSwordDamage() {
        Random random = new Random();
        int damage = random.nextInt(2) + 2;
        if (attackNumber == 2) {
            damage = 5;
        }
        return damage;
    }

    public UUID getAttackId() {
        return currentAttackId;
    }

    public boolean canPassFloor() {
        return passThroughFloor;
    }

    public void setCanPassFloor(boolean state) {
        passThroughFloor = true;
//        passThroughFloor = state;
    }

    public void dodge() {
        if (b2body.getLinearVelocity().y == 0) {
            dodgeTimer = 0.35f;
            if (runningRight) {
                b2body.applyLinearImpulse(new Vector2(2f, 0), b2body.getWorldCenter(), true);
            } else {
                b2body.applyLinearImpulse(new Vector2(-2f, 0), b2body.getWorldCenter(), true);

            }
        }

    }

    public void dodgeEnable(boolean state) {
        canDodge = state;
    }

    public void dropThroughFloor(){
        b2body.setLinearVelocity(0,b2body.getLinearVelocity().y);
    }
}
