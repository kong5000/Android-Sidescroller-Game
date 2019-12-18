package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Resurrect;
import com.mygdx.adventuregame.sprites.Effects.SmallExplosion;
import com.mygdx.adventuregame.sprites.Effects.SquarePortal;
import com.mygdx.adventuregame.sprites.Effects.Vortex;

import java.util.Random;
import java.util.UUID;

public class Player extends Sprite {
    //    private static final float[] RECTANGULAR_HITBOX = {
//            -0.05f, 0.2f,
//            -0.08f, 0.1f,
//            -0.05f, 0f,
//            0.05f, 0f,
//            0.08f, 0.1f,
//            0.05f, 0.2f};
    private static final float[] RECTANGULAR_HITBOX = {
            -0.05f, 0.25f,
            -0.05f, -0.07f,
            0.05f, -0.07f,
            0.05f, 0.25f};
    private static final float[] RECTANGULAR_HITBOX_SMALL = {
            -0.05f, 0.15f,
            -0.03f, -0.07f,
            0.03f, -0.07f,
            0.05f, 0.15f};
    private static final float[] HEAD_HITBOX = {
            -0.115f, 0.08f,
            -0.01f, 0f,
            0.01f, 0f,
            0.115f, 0.08f};
    private static final float[] WALL_HITBOX_LEFT = {
            0.11f, 0.15f,
            0.01f, 0f,
            -0.01f, 0f,
            -0.12f, 0.15f};
    private static final float[] SWORD_HITBOX_AIR = {
            -0.25f, 0f,
            -0.25f, 0.2f,
            0f, -0.1f,
            0.25f, 0f,
            0.25f, 0.2f,
            0, 0.3f};

    private static final float[] SWORD_HITBOX_RIGHT = {
            0f, -0.1f,
            0.25f, 0f,
            0.25f, 0.2f,
            0, 0.3f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.25f, 0f,
            -0.25f, 0.2f,
            0f, -0.1f,
            0, 0.3f};
    private static final float INVINCIBLE_TIME = 1f;
    public static final float WALLRUN_TIME = 0.45f;
    private boolean onElevator = false;
    private CheckPoint currentCheckPoint;

    public float currentSpeed = 1.3f;
    private static float MAX_RUN_SPEED = 1.3f;
    private static float CROUCH_SPEED = 0.8f;
    private boolean negativeYInput = false;
    private boolean positiveYInput = false;
    public boolean jumpIsHeld = false;
    public boolean chargingBow = false;
    public boolean attackDown = false;
    public boolean attackReleased = true;
    private float arrowCharge = 0;
    private boolean hitBoxUpdated = true;
    private float wallRunCooldown;
    private boolean squarePortalStarted = false;
    private boolean resurrectStarted = false;

    public enum State {
        FALLING, JUMPING, STANDING,
        RUNNING, HURT, ATTACKING,
        AIR_ATTACKING, FLIPPING,
        CASTING, DODGING, CROUCHING, CROUCH_WALKING,
        SHOOTING, DYING, REVIVING, PICKUP,
        WALLCLIMB, CHARGING_BOW, DOWN_ATTACK
    }

    public enum Spell {FIREBALL, SHIELD, BOW, NONE}

    private boolean canWallRun = false;
    //    private Spell equipedSpell = Spell.NONE;
    private Spell equipedSpell = Spell.FIREBALL;
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerCrouchWalk;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerDownAttack;
    private Animation<TextureRegion> playerRunDamaged;
    private Animation<TextureRegion> playerIdle;
    private Animation<TextureRegion> playerFall;
    private Animation<TextureRegion> playerFallDamaged;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerJumpDamaged;
    private Animation<TextureRegion> playerHurt;
    private Animation<TextureRegion> playerHurtDamaged;
    private Animation<TextureRegion> playerDie;
    private Animation<TextureRegion> playerRevive;
    private Animation<TextureRegion> playerAttack;
    private Animation<TextureRegion> playerAttackDamaged;
    private Animation<TextureRegion> playerAttack2;
    private Animation<TextureRegion> playerAttack2Damaged;
    private Animation<TextureRegion> playerAttack3;
    private Animation<TextureRegion> playerAttack3Damaged;
    private Animation<TextureRegion> playerAirAttack;
    private Animation<TextureRegion> playerFlip;
    private Animation<TextureRegion> playerFlipDamaged;
    private Animation<TextureRegion> playerCast;
    private Animation<TextureRegion> playerCastDamaged;
    private Animation<TextureRegion> playerDodge;
    private Animation<TextureRegion> playerDodgeDamaged;
    private Animation<TextureRegion> playerBow;
    private Animation<TextureRegion> playerBowDamaged;
    private Animation<TextureRegion> playerCrouch;
    private Animation<TextureRegion> playerWallClimb;
    private Animation<TextureRegion> playerThrow1;
    private Animation<TextureRegion> playerChargeBow;
    private Animation<TextureRegion> playerChargeBowAir;
    private Animation<TextureRegion> playerFireBow;
    private TextureRegion playerGotItem;

    public float dodgeSpeed = 2f;
    private float wallrunTimer = -1f;
    private boolean positiveXInput = false;
    private boolean negativeXInput = false;
    private static final float FLASH_RED_TIME = 1f;
    private float invincibilityTimer = -1f;
    private boolean hasDoubleJump = true;
    private boolean hasRegeneration = false;
    private boolean hasProtection = false;
    private int swordLevel = 0;
    public boolean hasBow = true;
    public boolean hasFireSpell = true;
    private boolean canFireProjectile;
    private boolean passThroughFloor = false;
    public boolean isCrouching = false;
    private boolean canDodge = false;
    public boolean chargingSpell = false;
    public boolean runningRight;
    private boolean flipEnabled;
    private boolean arrowLaunched = false;

    private static float REGEN_TIME = 7f;
    private float regenTimer;
    private float hurtBySpikeTimer;
    private float itemPickupTimer;
    private float stateTimer;
    private float hurtTimer;
    private float attackTimer;
    private float flipTimer;
    private float castTimer;
    private float shieldTimer;
    private float dodgeTimer = 0;
    private float passThroughFloorTimer = 0;
    private float shootingTimer = 0;
    private float deathTimer = 0;
    private static final float REVIVE_TIME = 7;
    private static final float DEATH_TIME = 4;
    private static final float DEATH_SPELL_TIME = 2;
    private float castCooldown;
    private float dodgeCooldown = 0;
    private float arrowCooldown = 0;

    private float reviveTimer = 0;

    public float currentMaxSpeed = 1.35f;
    private static final float CAST_COOLDOWN_TIME = 1f;
    private static float ARROW_COOLDOWN_TIME = 0.55f;
    private static final float SHOOT_ARROW_TIME = 0.2f;
    public static final float SHIELD_TIME = 3.5f;
    private static final float HURT_TIME = 0.45f;
    private static final float CAST_TIME = 0.5f;
    private static final float ATTACK_TIME = 0.35f;
    private static final float FLIP_TIME = 0.4f;
    private static final float MAX_VERTICAL_SPEED = 3f;
    private static final float MAX_HORIZONTAL_SPEED = 2f;

    private float magicShieldAlpha = 1f;
    private int health;
    private static final int FULL_HEALTH = 20;

    TextureAtlas textureAtlas;

    private Fixture swordFixture;

    private PlayScreen screen;

    private MagicShield magicShield;

    private float comboTimer;
    private int attackNumber = 0;
    private UUID currentAttackId;

    private float magicShieldSize = 0.1f;

    private boolean playerReset = false;
    private TextureRegion pickedUpItem;
    private Sprite itemSprite;

    private int xp;
    private float[] yVelocities = {0, 0, 0};
    private float[] xVelocities = {0, 0};
    private int yVelocityIndex = 0;
    private int xVelocityIndex = 0;
    private float averageXVelocity;
    private float averageYVelocity;
    private int flashCount = 0;
    private boolean flashFrame = true;
    protected float flashRedTimer;
    public boolean downAttacking = false;

    private Sprite dialogBox;
    private TextureRegion itemDialog;

    private static float MAX_ARROW_CHARGE = 0.75f;
    private float spawnPointX;
    private float spawnPointY;

    private static final float LEVEL_1_START_X = 10.05f;
    private static final float LEVEL_1_START_Y = 5.65f;



    //Todo firespell blowsup box obstacles
    public Player(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("player_idle1"));
        this.world = world;
        this.screen = screen;


//        spawnPointX = 10.05f;
//        spawnPointY = 5.65f;

        spawnPointX = 2f;
        spawnPointY = 9f;

        dialogBox = new Sprite();
        dialogBox.setBounds(getX(), getY(), 112 / AdventureGame.PPM, 75 / AdventureGame.PPM);
        xp = 0;
        itemSprite = new Sprite();
        itemSprite.setBounds(getX(), getY(), 16 / AdventureGame.PPM, 16 / AdventureGame.PPM);
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
        itemPickupTimer = 0;
        flashRedTimer = -1f;
        playerDownAttack = generateAnimation(textureAtlas.findRegion("player_fall_attack"), 2, 52, 39, 0.1f);
        playerCrouchWalk = generateAnimation(textureAtlas.findRegion("player_crouch_walk"), 6, 52, 39, 0.1f);
        playerRun = generateAnimation(textureAtlas.findRegion("player_run"), 6, 52, 39, 0.1f);
        playerRun.setPlayMode(Animation.PlayMode.LOOP);
        playerRunDamaged = generateAnimation(textureAtlas.findRegion("player_run_bright"), 6, 52, 39, 0.1f);
        playerRunDamaged.setPlayMode(Animation.PlayMode.LOOP);
        playerIdle = generateAnimation(textureAtlas.findRegion("player_idle1"), 3, 52, 39, 0.2f);
        playerFall = generateAnimation(textureAtlas.findRegion("player_fall"), 2, 52, 39, 0.1f);
        playerFallDamaged = generateAnimation(textureAtlas.findRegion("player_fall_bright"), 2, 52, 39, 0.1f);
        playerJump = generateAnimation(textureAtlas.findRegion("player_jump"), 4, 52, 39, 0.1f);
        playerJumpDamaged = generateAnimation(textureAtlas.findRegion("player_jump_bright"), 4, 52, 39, 0.1f);
        playerHurt = generateAnimation(textureAtlas.findRegion("player_hurt"), 3, 52, 39, 0.1f);
        playerHurtDamaged = generateAnimation(textureAtlas.findRegion("player_hurt_bright"), 3, 52, 39, 0.1f);
        playerDie = generateAnimation(textureAtlas.findRegion("player_die"), 7, 52, 39, 0.3f);
        playerRevive = generateAnimation(textureAtlas.findRegion("player_revive"), 7, 50, 37, 0.3f);

        playerThrow1 = generateAnimation(textureAtlas.findRegion("player_throw"), 4, 52, 39, 0.07f);
        playerAttack = generateAnimation(textureAtlas.findRegion("player_attack1"), 5, 52, 39, 0.07f);
        playerAttackDamaged = generateAnimation(textureAtlas.findRegion("player_attack1_bright"), 5, 52, 39, 0.07f);
        playerAttack2 = generateAnimation(textureAtlas.findRegion("player_attack2"), 6, 50, 37, 0.0575f);
        playerAttack2Damaged = generateAnimation(textureAtlas.findRegion("player_attack2_bright"), 6, 50, 37, 0.0575f);
        playerAttack3 = generateAnimation(textureAtlas.findRegion("player_attack3"), 6, 50, 37, 0.0575f);
        playerAttack3Damaged = generateAnimation(textureAtlas.findRegion("player_attack3_bright"), 6, 50, 37, 0.0575f);
        playerAirAttack = generateAnimation(textureAtlas.findRegion("player_air_attack1"), 4, 52, 39, 0.125f);
        playerFlip = generateAnimation(textureAtlas.findRegion("player_flip"), 4, 52, 39, 0.1f);
        playerFlipDamaged = generateAnimation(textureAtlas.findRegion("player_flip_bright"), 4, 52, 39, 0.1f);
        playerCast = generateAnimation(textureAtlas.findRegion("player_cast"), 4, 52, 39, 0.1f);
        playerCast.setPlayMode(Animation.PlayMode.LOOP);
        playerCastDamaged = generateAnimation(textureAtlas.findRegion("player_cast_bright"), 4, 52, 39, 0.1f);
        playerCastDamaged.setPlayMode(Animation.PlayMode.LOOP);
        playerDodge = generateAnimation(textureAtlas.findRegion("player_dodge"), 5, 50, 37, 0.07f);
        playerDodgeDamaged = generateAnimation(textureAtlas.findRegion("player_dodge_bright"), 5, 50, 37, 0.07f);
        playerCrouch = generateAnimation(textureAtlas.findRegion("player_crouch"), 4, 50, 37, 0.1f);
        playerBow = generateAnimation(textureAtlas.findRegion("player_bow"), 5, 50, 37, 0.1f);
        playerChargeBow = generateAnimation(textureAtlas.findRegion("player_charge_bow"), 3, 50, 37, 0.1f);
        playerChargeBowAir = generateAnimation(textureAtlas.findRegion("player_charge_bow_air"), 2, 50, 37, 0.1f);
        playerFireBow = generateAnimation(textureAtlas.findRegion("player_fire_bow"), 2, 50, 37, 0.1f);


        playerBowDamaged = generateAnimation(textureAtlas.findRegion("player_bow_bright"), 5, 50, 37, 0.1f);
        playerStand = new TextureRegion(getTexture(), 0, 0, 50, 37);
        playerGotItem = new TextureRegion(screen.getAtlas().findRegion("player_got_item"), 0, 0, 50, 37);
        playerWallClimb = generateAnimation(textureAtlas.findRegion("player_wall_climb"), 6, 52, 39, 0.06f);
        playerWallClimb.setPlayMode(Animation.PlayMode.LOOP);
        itemDialog = new TextureRegion(screen.getAtlas().findRegion("sword_dialog"), 0, 0, 450, 300);

        definePlayer();
        setBounds(0, 0, 60 / AdventureGame.PPM, 44 / AdventureGame.PPM);
//        setBounds(0, 0, 55 / AdventureGame.PPM, 41 / AdventureGame.PPM);
//        setBounds(0, 0, 50 / AdventureGame.PPM, 37 / AdventureGame.PPM);
        setRegion(playerStand);
        magicShield = new MagicShield(screen, b2body.getPosition().x, b2body.getPosition().y, this);
        magicShield.setAlpha(0);

    }

    private void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        //Starting Castle
        bodyDef.position.set(spawnPointX , spawnPointY);
//        bodyDef.position.set(spawnPointX , spawnPointY);
        //First minotaur
//        bodyDef.position.set(6400 / AdventureGame.PPM, 900 / AdventureGame.PPM);
        //Boss Area
//        bodyDef.position.set(10950 / AdventureGame.PPM, 900 / AdventureGame.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(RECTANGULAR_HITBOX);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT
                | AdventureGame.ITEM_BIT
                | AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.isSensor = false;
        fixtureDef.friction = 0;
        b2body.createFixture(fixtureDef).setUserData(this);
        fixtureDef = new FixtureDef();

        bodyShape = new PolygonShape();
        bodyShape.set(HEAD_HITBOX);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.WALL_RUN_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    private void createBigHitBox() {
        BodyDef bodyDef = new BodyDef();
        //Starting Castle
        bodyDef.position.set(getX() + 0.3f, getY() + 0.11f);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(RECTANGULAR_HITBOX);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT
                | AdventureGame.ITEM_BIT
                | AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.isSensor = false;
        fixtureDef.friction = 0;
        b2body.createFixture(fixtureDef).setUserData(this);


        fixtureDef = new FixtureDef();

        bodyShape = new PolygonShape();
        bodyShape.set(HEAD_HITBOX);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.WALL_RUN_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(this);

    }

    private void createSmallHitBox() {
        BodyDef bodyDef = new BodyDef();
        //Starting Castle
        bodyDef.position.set(getX() + 0.3f, getY() + 0.11f);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(RECTANGULAR_HITBOX_SMALL);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT
                | AdventureGame.ITEM_BIT
                | AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.isSensor = false;
        fixtureDef.friction = 0;
        b2body.createFixture(fixtureDef).setUserData(this);


        fixtureDef = new FixtureDef();

        bodyShape = new PolygonShape();
        bodyShape.set(HEAD_HITBOX);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.WALL_RUN_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    private void createWallRunSensor(boolean goingRight) {

    }

    public void update(float dt) {
        setPosition(getXPos(), getYPos() + 0.1f);
        setRegion(getFrame(dt));
        limitSpeed();
        updateAverageYVelocity();
        updateAverageXVelocity();

        if(currentState == State.DOWN_ATTACK){
            b2body.setLinearVelocity(0, -3.5f);
        }
        if(!isFalling()){
            downAttacking = false;
        }
        if (currentState == State.DODGING || currentState == State.CROUCHING || currentState == State.CROUCH_WALKING && !hitBoxUpdated) {
            hitBoxUpdated = true;
            changeToSmallHitBox();
        }
        if (currentState != State.DODGING && currentState != State.CROUCHING && currentState != State.CROUCH_WALKING && hitBoxUpdated) {
            hitBoxUpdated = false;
            changeToBigHitBox();
        }
        if (currentState == State.CHARGING_BOW) {
            if (arrowCharge < MAX_ARROW_CHARGE) {
                arrowCharge += dt;
            }
            if (stateTimer > MAX_ARROW_CHARGE) {
                playerChargeBow.setFrameDuration(10f);
            }
        }
        if (currentState == State.CROUCH_WALKING) {
            currentMaxSpeed = CROUCH_SPEED;
        } else {
            currentMaxSpeed = MAX_RUN_SPEED;
        }
        if (isCanWallRun()) {
            if (positiveXInput || negativeXInput) {
                b2body.setLinearVelocity(b2body.getLinearVelocity().x, 1.5f);
            }
        }
        if (currentState != State.DYING) {
            if (hasRegeneration) {
                if (regenTimer > 0) {
                    regenTimer -= dt;
                } else {
                    regenTimer = REGEN_TIME;
                    if (health < FULL_HEALTH) {
                        health += 1;
                    }
                }
            }
        }
        if (currentState == State.REVIVING) {
            if (playerRevive.isAnimationFinished(stateTimer)) {
                health = FULL_HEALTH;
            }
        }
        if (currentState == State.DYING) {
            deathTimer += dt;
            if (deathTimer >= DEATH_SPELL_TIME) {
                if(!squarePortalStarted){
                    setAlpha(0);
                    screen.getTopLayerSpritesToAdd().add(new SquarePortal(screen,getX() + 0.07f, getY() - 0.15f));
                    squarePortalStarted = true;
                }
//
//                magicShield.setAlpha(1);
//                magicShield.setScale(magicShieldSize);
//                if (magicShieldSize < 1) {
//                    magicShieldSize += 0.01f;
//                }
            }
            if (deathTimer >= DEATH_TIME) {
//                magicShieldAlpha -= 0.005;
//                if (magicShieldAlpha < 0) {
//                    magicShieldAlpha = 0;
//                }
//                magicShield.setAlpha(magicShieldAlpha);
                if (!playerReset) {
                    resetPlayer();
                    if(!resurrectStarted){
                        setAlpha(1);
                        screen.getTopLayerSpritesToAdd().add(new Resurrect(screen,b2body.getPosition().x - 0.25f, b2body.getPosition().y));
                        resurrectStarted = true;

                    }
                }
            }
            if (deathTimer >= REVIVE_TIME) {

                reviveTimer = 2.2f;
                stateTimer = 0;
                magicShield.setAlpha(0);
                magicShieldAlpha = 1f;
                deathTimer = 0;
                playerReset = false;
                resurrectStarted = false;
                squarePortalStarted = false;
            }
        }
        if (currentState == State.CHARGING_BOW) {
            if (!chargingBow) {
                bowAttack();
            }
        }
        if (currentState == State.SHOOTING) {
            if (stateTimer >= 0.1f) {
                if (!arrowLaunched) {
                    launchFireBall();
                    arrowLaunched = true;
                }
            }
        }
        if (wallRunCooldown > 0) {
            wallRunCooldown -= dt;
        }
        if (wallrunTimer > 0) {
            wallrunTimer -= dt;
        }
        if (invincibilityTimer > 0) {
            invincibilityTimer -= dt;
        }
        if (flashRedTimer > 0) {
            flashRedTimer -= dt;
        }
        if (hurtBySpikeTimer > 0) {
            hurtBySpikeTimer -= dt;
        }
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
        if (itemPickupTimer > 0) {
            itemPickupTimer -= dt;
        }
        if (reviveTimer > 0) {
            reviveTimer -= dt;
        }
        if (flipTimer > 0)
            flipTimer -= dt;
        if (castTimer > 0)
            castTimer -= dt;
        if (castCooldown > 0)
            castCooldown -= dt;
        if (shieldTimer > 0)
            shieldTimer -= dt;
        if (comboTimer > 0) {
            comboTimer -= dt;
        } else {
            attackNumber = 0;
        }
        if (shieldTimer < 0) {
            magicShield.setAlpha(0);
        }
        if (dodgeTimer > 0) {

            dodgeTimer -= dt;
        }
        if (passThroughFloorTimer > 0) {
            passThroughFloorTimer -= dt;
            passThroughFloor = true;
        } else {
            passThroughFloor = false;
        }

        if (dodgeCooldown > 0) {
            dodgeCooldown -= dt;
        }
        if (shootingTimer > 0) {
            shootingTimer -= dt;
        }

        if (arrowCooldown > 0) {
            arrowCooldown -= dt;
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
        if (itemPickupTimer > 0) {
            dialogBox.setPosition(b2body.getPosition().x - dialogBox.getWidth() / 2, b2body.getPosition().y + dialogBox.getHeight() / 2);
            itemSprite.setPosition(b2body.getPosition().x - 0.1f, b2body.getPosition().y + 0.15f);
            itemSprite.draw(batch);
            dialogBox.draw(batch);
        }
        magicShield.draw(batch);
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DOWN_ATTACK:
                region = playerDownAttack.getKeyFrame(stateTimer, true);
                break;
            case CROUCH_WALKING:
                region = playerCrouchWalk.getKeyFrame(stateTimer, true);
                break;
            case CHARGING_BOW:
                region = playerChargeBow.getKeyFrame(stateTimer, true);
                break;
            case WALLCLIMB:
                region = playerWallClimb.getKeyFrame(stateTimer);
                break;
            case REVIVING:
                region = playerRevive.getKeyFrame(stateTimer);
                break;
            case DYING:
                region = playerDie.getKeyFrame(stateTimer);
                break;
            case PICKUP:
                region = playerGotItem;
                break;
            case SHOOTING:
//                region = selectBrightFrameOrRegularFrame(playerBow, playerBowDamaged);
                region = playerFireBow.getKeyFrame(stateTimer);
                break;
            case CROUCHING:
                region = playerCrouch.getKeyFrame(stateTimer);
                break;
            case DODGING:
                region = selectBrightFrameOrRegularFrame(playerDodge, playerDodgeDamaged);
                break;
            case CASTING:
                region = selectBrightFrameOrRegularFrame(playerCast, playerCastDamaged);
//                region = playerChargeBow.getKeyFrame(stateTimer, true);
                break;
            case HURT:
                region = selectBrightFrameOrRegularFrame(playerHurt, playerHurtDamaged);
                break;
            case ATTACKING:
                if (attackNumber == 0) {
                    region = selectBrightFrameOrRegularFrame(playerAttack, playerAttackDamaged);
//                    region = playerThrow1.getKeyFrame(stateTimer);
                } else if (attackNumber == 1) {
                    region = selectBrightFrameOrRegularFrame(playerAttack2, playerAttack2Damaged);
                } else {
                    region = selectBrightFrameOrRegularFrame(playerAttack3, playerAttack3Damaged);
                }

                break;
            case AIR_ATTACKING:
                region = playerAirAttack.getKeyFrame(stateTimer);
                break;
            case FLIPPING:
                region = selectBrightFrameOrRegularFrame(playerFlip, playerFlipDamaged);
                break;
            case JUMPING:
                region = selectBrightFrameOrRegularFrame(playerJump, playerJumpDamaged);
                break;
            case RUNNING:
                region = selectBrightFrameOrRegularFrame(playerRun, playerRunDamaged);
                break;
            case FALLING:
                region = selectBrightFrameOrRegularFrame(playerFall, playerFallDamaged);
                break;
            case STANDING:
            default:
                region = playerIdle.getKeyFrame(stateTimer, true);
                break;
        }

        if ((!runningRight) && !region.isFlipX()) {
            region.flip(true, false);
        } else if (runningRight && region.isFlipX()) {
            region.flip(true, false);
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
        if(downAttacking){
            return State.DOWN_ATTACK;
        }
        if (canWallRun && wallrunTimer > 0) {
            if (positiveXInput || negativeXInput )
                return State.WALLCLIMB;
        }
        if (reviveTimer > 0) {
            return State.REVIVING;
        }
        if (health <= 0) {
            return State.DYING;
        }
        if (itemPickupTimer > 0) {
            return State.PICKUP;
        }
        if (chargingBow && arrowCooldown <= 0) {
            return State.CHARGING_BOW;
        }
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
            if (Math.abs(b2body.getLinearVelocity().y) > 0 && !onElevator && !downAttacking) {
                return State.AIR_ATTACKING;
            }

            return State.ATTACKING;

        } else if (shootingTimer > 0) {
            return State.SHOOTING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (flipTimer > 0) {
            return State.FLIPPING;
        } else if (isJumping() && !onElevator) {
            return State.JUMPING;
        }
//        else if (b2body.getLinearVelocity().y < 0) {
//            return State.FALLING;
//        }
        else if (isFalling() && !onElevator) {
            return State.FALLING;
        } else if (dodgeTimer > 0) {
            return State.DODGING;
        }
//        else if (isCrouching) {
//            if(isRunning()){
//                return State.CROUCH_WALKING;
//            }
//            return State.CROUCHING;
//        }
        else if (isRunning()) {
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
        if (invincibilityTimer < 0) {
            invincibilityTimer = INVINCIBLE_TIME;
            hurtTimer = HURT_TIME;
            if (hasProtection) {
                damage -= 1;
            }
            health -= damage;
            if (flashRedTimer < 0) {
                flashRedTimer = FLASH_RED_TIME;
            }
            endChargingSpell();
            screen.getDamageNumbersToAdd().add(new DamageNumber(screen, getXPos(), getYPos(), true, damage));
            if (currentState == State.CHARGING_BOW) {
                bowAttack();
            }
        }
    }

    public void jump() {
        if (currentState == State.JUMPING || currentState == State.FALLING || currentState == State.FLIPPING || currentState == State.AIR_ATTACKING || currentState == State.WALLCLIMB) {
            if (hasDoubleJump) {
                if (flipEnabled) {
                    flipTimer = FLIP_TIME;
                    flipEnabled = false;
                    b2body.applyLinearImpulse(new Vector2(0, 6f), b2body.getWorldCenter(), true);
                    if (b2body.getLinearVelocity().y > MAX_VERTICAL_SPEED) {
                        b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED);
                    }
                }
            }
        } else if (currentState != State.JUMPING && currentState != State.FALLING && currentState != State.WALLCLIMB && currentState != State.DODGING) {
            if (canDodge) {
                dodge();
                wallRunCooldown = 0.65f;
            } else {
                if (currentState != State.HURT || hurtBySpikeTimer > 0) {
                    b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);
                }
            }
        }
    }

    public void attack() {
        b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);

        if (attackTimer < 0) {
//            launchFireBall();
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
        if (currentState == State.DODGING) {
            return false;
        }
        if (hurtTimer < 0) {
            return true;
        } else {
            return false;
        }
    }

    public void castSpell() {
        if (equipedSpell == Spell.FIREBALL) {
            attack();
//            if (castCooldown < 0) {
//                castTimer = CAST_TIME;
//                castCooldown = CAST_COOLDOWN_TIME;
//                canFireProjectile = true;
//            }
        } else if (equipedSpell == Spell.SHIELD) {
            if (castCooldown < 0) {
                castTimer = CAST_TIME;
                castCooldown = CAST_COOLDOWN_TIME;
                shieldTimer = SHIELD_TIME;
                magicShield.setAlpha(1);
            }
        } else if (equipedSpell == Spell.BOW) {
            if (arrowCooldown <= 0)
                chargingBow = true;
//            bowAttack();
        }
    }

    public void bowAttack() {
        if (shootingTimer <= 0 && currentState != State.SHOOTING && arrowCooldown <= 0) {
            playerChargeBow.setFrameDuration(0.1f);
            shootArrow();
            arrowCooldown = ARROW_COOLDOWN_TIME;
            arrowLaunched = false;


        }
    }

    private void launchFireBall() {
        boolean ballDirectionRight;
        if (runningRight) {
            ballDirectionRight = true;
        } else {
            ballDirectionRight = false;
        }
//        screen.projectilesToSpawn.add(new FireBall(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, ballDirectionRight, true));
        screen.getSpritesToAdd().add(new Arrow(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, ballDirectionRight, true, arrowCharge));
        arrowCharge = 0;
    }

    public void switchSpell() {
        switch (equipedSpell) {
            case FIREBALL:
                if (hasBow) {
                    equipedSpell = Spell.BOW;
                }
                break;
            case BOW:
                if (hasFireSpell) {
                    equipedSpell = Spell.FIREBALL;
                }
                break;
            default:
            case NONE:
                equipedSpell = Spell.NONE;
                break;
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

    public void setInputPositiveX(boolean state) {
        positiveXInput = state;
    }

    public void setInputPositiveY(boolean state) {
        positiveYInput = state;
    }

    public void setInputNegativeY(boolean state) {
        negativeYInput = state;
    }

    public void setInputNegativeX(boolean state) {
        negativeXInput = state;
    }

    public int getSwordDamage() {
        Random random = new Random();
        int damage = random.nextInt(3 + swordLevel) + 3 + swordLevel / 2;
        if (attackNumber == 2) {
            damage = 5 + swordLevel;
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
        passThroughFloorTimer = 0.2f;
        if (dodgeCooldown <= 0) {
            dodgeCooldown = 1.25f;
            if (b2body.getLinearVelocity().y == 0) {
                dodgeTimer = 0.35f;
                if (runningRight) {
//                    b2body.applyLinearImpulse(new Vector2(2f, 0), b2body.getWorldCenter(), true);
                    b2body.setLinearVelocity(currentSpeed, b2body.getLinearVelocity().y);

                } else {
//                    b2body.applyLinearImpulse(new Vector2(-2f, 0), b2body.getWorldCenter(), true);
                    b2body.setLinearVelocity(-currentSpeed, b2body.getLinearVelocity().y);
                }
            }
        }
    }

    private void upgradeSword() {
        swordLevel++;
    }

    public void dodgeEnable(boolean state) {
        canDodge = state;
    }

    public void dropThroughFloor() {
        b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
    }

    public void setCrouching(boolean state) {
        isCrouching = state;
    }

    private float getXPos() {
        return b2body.getPosition().x - getWidth() / 2;
    }

    private float getYPos() {
        return b2body.getPosition().y - getHeight() / 2 + 0.01f;
    }

    public void shootArrow() {
        chargingBow = false;
        if (shootingTimer <= 0) {
            shootingTimer = SHOOT_ARROW_TIME;
        }
    }

    private void changeToSmallHitBox() {
        world.destroyBody(b2body);
        createSmallHitBox();
    }

    private void changeToBigHitBox() {
        world.destroyBody(b2body);
        createBigHitBox();
    }


    private void resetPlayer() {
        world.destroyBody(b2body);
        definePlayer();
    }

    public boolean doneDying() {
        if (currentState == State.DYING) {
            return deathTimer >= DEATH_TIME;
        }
        return false;

    }

    public boolean canMove() {
        return (currentState != State.DYING && currentState != State.REVIVING && currentState != State.PICKUP && currentState != State.HURT && currentState != State.CHARGING_BOW);
    }

    public void pickupItem(int itemID) {
        switch (itemID) {
            case AdventureGame.BOW:

                equipedSpell = Spell.BOW;
                hasBow = true;

                itemPickupTimer = 2f;
                break;
            case AdventureGame.FIRE_SPELLBOOK:
                equipedSpell = Spell.FIREBALL;
                hasFireSpell = true;
                itemPickupTimer = 2f;
                break;
            case AdventureGame.SMALL_HEALTH:
                if (health < FULL_HEALTH - 3) {
                    health += 3;
                } else {
                    health = FULL_HEALTH;
                }
                break;
            case AdventureGame.MEDIUM_HEALTH:
                if (health < FULL_HEALTH - 8) {
                    health += 8;
                } else {
                    health = FULL_HEALTH;
                }
                break;
            case AdventureGame.LARGE_HEALTH:
                health = FULL_HEALTH;
                break;
            case AdventureGame.RING_OF_DOUBLE_JUMP:
                hasDoubleJump = true;
                itemPickupTimer = 2f;
                break;
            case AdventureGame.RING_OF_PROTECTION:
                hasProtection = true;
                itemPickupTimer = 2f;
                break;
            case AdventureGame.SWORD:
                upgradeSword();
                itemPickupTimer = 2f;
                break;
            default:
                break;
        }
        pickedUpItem = getItemTexture(itemID);
        itemSprite.setRegion(pickedUpItem);
        dialogBox.setRegion(getItemDialog(itemID));

    }

    private TextureRegion getItemTexture(int id) {
        String assetName;
        switch (id) {
            case AdventureGame.BOW:
                assetName = "bow";
                break;
            case AdventureGame.FIRE_SPELLBOOK:
                assetName = "fire_spellbook";
                break;
            case AdventureGame.SMALL_HEALTH:
                assetName = "small_health";
                break;
            case AdventureGame.MEDIUM_HEALTH:
                assetName = "medium_health";
                break;
            case AdventureGame.LARGE_HEALTH:
                assetName = "large_health";
                break;
            case AdventureGame.RING_OF_DOUBLE_JUMP:
                assetName = "ring_of_double_jump";
                break;
            case AdventureGame.RING_OF_PROTECTION:
                assetName = "ring_of_protection";
                break;
            case AdventureGame.SWORD:
                assetName = "sword";
                break;
            default:
                assetName = "small_health";
                break;
        }
        return new TextureRegion(screen.getAtlas().findRegion(assetName), 0, 0, 16, 16);
    }

    private TextureRegion getItemDialog(int id) {
        String assetName;
        switch (id) {
            case AdventureGame.BOW:
                assetName = "bow_dialog";
                break;
            case AdventureGame.FIRE_SPELLBOOK:
                assetName = "fire_spell_dialog";
                break;
            case AdventureGame.RING_OF_DOUBLE_JUMP:
                assetName = "double_jump_dialog";
                break;
            case AdventureGame.RING_OF_PROTECTION:
                assetName = "protection_ring_dialog";
                break;
            case AdventureGame.SWORD:
                assetName = "sword_dialog";
                break;
            default:
                assetName = "sword_dialog";
                break;
        }
        return new TextureRegion(screen.getAtlas().findRegion(assetName), 0, 0, 450, 300);
    }

    public void giveXP(float experiencePoints) {
        xp += experiencePoints;
    }

    public int geXP() {
        return xp;
    }

    public boolean isFalling() {
//        if (b2body.getLinearVelocity().y < 0) {
//            return true;
//        }
        if (averageYVelocity < -0.01) {
            return true;
        }

        return false;
//        float average = (yVelocities[0] + yVelocities[1] + yVelocities[2])/ 3f;
//        yVelocities[yVelocityIndex] = b2body.getLinearVelocity().y;
//        yVelocityIndex++;
//        if(yVelocityIndex > yVelocities.length - 1){
//            yVelocityIndex = 0;
//        }
//        if(average < -0.75){
//            return true;
//        }
//        return false;
    }

    private boolean isJumping() {
        if (b2body.getLinearVelocity().y > 0) {
            return true;
        }
        if (averageYVelocity > 0.01) {
            return true;
        }

        return false;
    }

    private boolean isRunning() {
        if (Math.abs(averageXVelocity) > 0) {
            return true;
        }
        return false;
    }

    private void updateAverageYVelocity() {
        float average = (yVelocities[0] + yVelocities[1] + yVelocities[2]) / 3f;
        yVelocities[yVelocityIndex] = b2body.getLinearVelocity().y;
        yVelocityIndex++;
        if (yVelocityIndex > yVelocities.length - 1) {
            yVelocityIndex = 0;
        }
        averageYVelocity = average;
    }

    private void updateAverageXVelocity() {
        float average = (xVelocities[0] + xVelocities[1]) / 3f;
        xVelocities[xVelocityIndex] = b2body.getLinearVelocity().x;
        xVelocityIndex++;
        if (xVelocityIndex > xVelocities.length - 1) {
            xVelocityIndex = 0;
        }
        averageXVelocity = average;
    }

    public void hitBySpike() {
        hurtBySpikeTimer = 0.15f;
        hurt(2);
    }

    private void limitSpeed() {
        if (currentState != State.DODGING) {
            if (b2body.getLinearVelocity().y > MAX_VERTICAL_SPEED) {
                b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED);
            }
            if (b2body.getLinearVelocity().x > currentMaxSpeed) {
                b2body.setLinearVelocity(currentMaxSpeed, b2body.getLinearVelocity().y);
            }
            if (b2body.getLinearVelocity().x < -currentMaxSpeed) {
                b2body.setLinearVelocity(-currentMaxSpeed, b2body.getLinearVelocity().y);
            }
        } else {
            if (b2body.getLinearVelocity().x > MAX_HORIZONTAL_SPEED) {
                b2body.setLinearVelocity(MAX_HORIZONTAL_SPEED, b2body.getLinearVelocity().y);
            }
            if (b2body.getLinearVelocity().x < -MAX_HORIZONTAL_SPEED) {
                b2body.setLinearVelocity(-MAX_HORIZONTAL_SPEED, b2body.getLinearVelocity().y);
            }
            if (b2body.getLinearVelocity().y > MAX_VERTICAL_SPEED) {
                b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED);
            }
        }

        if (currentState == State.WALLCLIMB) {
            if (b2body.getLinearVelocity().y > MAX_VERTICAL_SPEED) {
                b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED - 1.5f);
            }
        }
        if (currentState == State.PICKUP || currentState == State.HURT || currentState == State.DYING) {
            b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
        }
        if (currentState == State.CASTING) {
            if (!isFalling() && !isJumping()) {
                b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            }
        }
        if (currentState == State.SHOOTING) {
            if (!isFalling() && !isJumping()) {
                b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            }
        }
        if (currentState == State.CHARGING_BOW) {
            if (!isFalling() && !isJumping()) {
                b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            }
        }
    }

    protected TextureRegion selectBrightFrameOrRegularFrame(Animation<TextureRegion> animation, Animation<TextureRegion> brightAnimation) {
        TextureRegion textureRegion;
        if (flashRedTimer > 0) {
            if (flashFrame) {
                flashCount++;
                if (flashCount > 4) {
                    flashFrame = false;
                    flashCount = 0;
                }
                textureRegion = brightAnimation.getKeyFrame(stateTimer);
            } else {
                flashCount++;
                if (flashCount > 4) {
                    flashFrame = true;
                    flashCount = 0;
                }
                textureRegion = animation.getKeyFrame(stateTimer);
            }
        } else {
            textureRegion = animation.getKeyFrame(stateTimer);
        }
        return textureRegion;
    }

    public boolean canAct() {
        return currentState != State.HURT;
    }

    public void enableWallRun() {
        if (currentState == State.FALLING || currentState == State.JUMPING || currentState == State.FLIPPING) {
            if(jumpIsHeld){
                if (wallrunTimer < 0 && wallRunCooldown <= 0) {
                    wallrunTimer = WALLRUN_TIME;
                    wallRunCooldown = 1f;
                }

                canWallRun = true;
            }

        }

    }

    public void addXMovement(float x) {
        b2body.setLinearVelocity(x, b2body.getLinearVelocity().y);
    }

    public void setOnElevator(boolean state) {
        onElevator = state;
    }

    private boolean isCanWallRun() {
        return (wallrunTimer > 0 && canWallRun);
    }

    public void disableWallRun() {
        canWallRun = false;
    }

    public void setRespawnPoint(CheckPoint checkPoint){
        currentCheckPoint = checkPoint;
        spawnPointY = checkPoint.getYPos();
        spawnPointX = checkPoint.getXPos();
    }

    public CheckPoint getCurrentCheckPoint(){
        return currentCheckPoint;
    }
}
