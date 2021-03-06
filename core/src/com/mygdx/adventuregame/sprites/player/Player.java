package com.mygdx.adventuregame.sprites.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Projectiles.Arrow;
import com.mygdx.adventuregame.sprites.CheckPoint;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Effects.Charge;
import com.mygdx.adventuregame.sprites.Effects.Resurrect;
import com.mygdx.adventuregame.sprites.Effects.SquarePortal;
import com.mygdx.adventuregame.sprites.FireSpell;
import com.mygdx.adventuregame.sprites.Projectiles.EarthBall;
import com.mygdx.adventuregame.sprites.Projectiles.FireBall;
import com.mygdx.adventuregame.sprites.Projectiles.GreenProjectile;
import com.mygdx.adventuregame.sprites.Projectiles.ImpSpell;
import com.mygdx.adventuregame.sprites.Projectiles.ShadeProjectile;
import com.mygdx.adventuregame.sprites.SpellBall;

public class Player extends Sprite {
    public static final float TELEPORT_DELAY = 0.75f;
    private int teleportCount = 0;
    public static final float DEFLECT_OFFSET_Y = 0.22f;
    private static final float DEFLECT_OFFSET_X_RIGHT = 0.35f;
    private static final float DEFLECT_OFFSET_X_LEFT = 0.27f;
    private int deflectType = AdventureGame.FIRE_PROJECTILE;
    private static final float INVINCIBLE_TIME = 1f;
    public static final float WALLRUN_TIME = 0.45f;
    private static final float SPELL_COOLDOWN_TIME = 0.5f;
    private static final int MAX_ARROWS = 4;
    private boolean onElevator = false;
    private CheckPoint currentCheckPoint;

    private float invisibilityTimer = 0.5f;
    private float attackCooldownTimer = -1f;
    private static final float ATTACK_COOLDOWN_TIME = 0.5f;
    public float currentSpeed = 1.3f;
    private static float MAX_RUN_SPEED = 1.1f;
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
    private boolean teleportPortalStarted = false;
    private boolean resurrectStarted = false;
    private boolean teleportResurrectStarted = false;
    private float stickRotation;
    private boolean spellBallEnabled;
    private float spellBallTimer = -1f;
    private float fireRate = -1f;
    private boolean firingSpell;

    private float spellCharge = 0;
    private float animationCenterX;
    private float animationCenterY;

    private float mana = 0;
    private static float FULL_MANA = 100;

    private boolean fireBallLaunched = false;

    private float chargingTimer = 0f;
    private boolean fullyCharged = false;
    private boolean charging = false;

    private Charge chargeEffect;
    private boolean canCastSpell = true;
    private float spellCooldownTimer = -1f;
    private int arrowCount = MAX_ARROWS;
    private boolean teleporting = false;
    private boolean hasTeleportItem = false;


    private boolean timeToDeflect = false;

    public enum State {
        FALLING, JUMPING, STANDING,
        RUNNING, HURT, ATTACKING,
        AIR_ATTACKING, FLIPPING,
        CASTING, DODGING, CROUCHING, CROUCH_WALKING,
        SHOOTING, DYING, REVIVING, PICKUP,
        WALLCLIMB, CHARGING_BOW, DOWN_ATTACK, TELEPORTING
    }

    public enum Spell {FIREBALL, SHIELD, BOW, NONE}

    private boolean canWallRun = false;
    //    private Spell equipedSpell = Spell.NONE;
    private Spell equipedSpell = Spell.BOW;
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private PlayerAnimations animations;

    public float dodgeSpeed = 2f;
    private float wallrunTimer = -1f;
    private boolean positiveXInput = false;
    private boolean negativeXInput = false;

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
    private float teleportTimer = 0;
    private static final float REVIVE_TIME = 7;
    private static final float TELEPORT_REVIVE_TIME = 7;
    private static final float DEATH_TIME = 4;
    private static final float TELEPORT_TIME = 4;
    private static final float DEATH_SPELL_TIME = 2;
    private static final float TELEPORT_SPELL_TIME = 2f;
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
    private static final float CAST_TIME = 0.15f;
    private static final float ATTACK_TIME = 0.35f;
    private static final float FLIP_TIME = 0.4f;
    private static final float MAX_VERTICAL_SPEED = 3f;
    private static final float MAX_HORIZONTAL_SPEED = 2f;

    private int health;
    private static final int FULL_HEALTH = 20;

    TextureAtlas textureAtlas;

    private Fixture swordFixture;

    private PlayScreen screen;

    private float comboTimer;
    public int attackNumber = 0;

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
    public boolean downAttacking = false;

    private Sprite dialogBox;
    private TextureRegion itemDialog;

    private static float MAX_ARROW_CHARGE = 0.75f;
    private float spawnPointX;
    private float spawnPointY;

    private static final float LEVEL_1_START_X = 10.05f;
    private static final float LEVEL_1_START_Y = 5.65f;

    private PlayerBody playerBody;

    //Todo firespell blowsup box obstacles
    public Player(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("player_idle1"));
        this.world = world;
        this.screen = screen;
        this.animations = new PlayerAnimations(screen.getAtlas(), this);
        playerBody = new PlayerBody(world, this);
        mana = FULL_MANA;
        chargeEffect = new Charge(screen, getX(), getY(), this);
        //Temple Spawn
//        spawnPointX = 3.05f;
//        spawnPointY = 8f;

        //FOR DUNGEON1 and forest_castle
        spawnPointX = 4f;
        spawnPointY = 3f;

        //Temple Boss
//        spawnPointX = 15f;
//        spawnPointY =15f;

        //Boss Dungeon1
//        spawnPointX = 50f;
//        spawnPointY = 12f;


        //bossroom
//        spawnPointX = 82f;
//        spawnPointY = 7f;

//        spawnPointX = 55f;
//        spawnPointY = 9f;

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
        itemDialog = new TextureRegion(screen.getAtlas().findRegion("sword_dialog"), 0, 0, 450, 300);

        playerBody.setSpawnPoint(spawnPointX, spawnPointY);
        b2body = playerBody.definePlayer();
        setBounds(0, 0, 60 / AdventureGame.PPM, 44 / AdventureGame.PPM);

    }




    public boolean canTurn() {
        return currentState == State.CHARGING_BOW || currentState == State.CASTING;
    }

    public void endRangedAttack() {
//        if(spellBallTimer < 0){
//            bowAttack();
//        }
        firingSpell = false;
    }

    public void teleport() {
        teleportCount++;
        switch (teleportCount) {
            case 1:
                playerBody.setSpawnPoint(AdventureGame.DUNGEON_START_X, AdventureGame.DUNGEON_START_Y);
                break;
            case 2:
                playerBody.setSpawnPoint(AdventureGame.TEMPLE_START_X, AdventureGame.TEMPLE_START_Y);
                break;
            case 3:
                playerBody.setSpawnPoint(AdventureGame.TEMPLE_START_X, AdventureGame.TEMPLE_START_Y);
                break;
        }
        if (!teleporting) {
            teleporting = true;
        }
    }

    public void beginRangedAttack() {
        if (canMove()) {
            if (hasArrows()) {
                if (spellBallTimer < 0) {
                    fireBow();
                } else {
                    firingSpell = true;
                }
                chargingTimer = 0;
            }
        }
////        if (mana > 0) {
////            firingSpell = true;
////        }
//        if (mana > 0 && canCastSpell) {
//
//            launchSpellBall();
//        }
    }


    public boolean isFullyCharge() {
        return fullyCharged;
    }

    public void fullHealth() {
        health = FULL_HEALTH;
    }

    public void reloadArrows() {
        arrowCount = MAX_ARROWS;
    }

    public void deflectProjectile(int type) {
//        launchSpellBall();
        screen.getSoundEffects().playParrySound();
        timeToDeflect = true;
        deflectType = type;
    }

    private void launchDeflectedProjectile() {
        float xOffset = DEFLECT_OFFSET_X_LEFT;
        if (runningRight) {
            xOffset = DEFLECT_OFFSET_X_RIGHT;
        }
        switch (deflectType) {
            case AdventureGame.FIRE_PROJECTILE:
                screen.getSpritesToAdd().add(new FireBall(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
            case AdventureGame.EARTH_PROJECTILE:
                screen.getSpritesToAdd().add(new EarthBall(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
            case AdventureGame.ICE_PROJECTILE:
                screen.getSpritesToAdd().add(new FireBall(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
            case AdventureGame.IMP_PROJECTILE:
                screen.getSpritesToAdd().add(new ImpSpell(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
            case AdventureGame.SHADE_PROJECTILE:
                screen.getSpritesToAdd().add(new ShadeProjectile(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
            case AdventureGame.GREEN_PROJECTILE:
                screen.getSpritesToAdd().add(new GreenProjectile(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
            default:
                screen.getSpritesToAdd().add(new FireBall(screen, getX() + xOffset, getY() + DEFLECT_OFFSET_Y, runningRight, true));
                break;
        }
    }


    public void update(float dt) {
        if (timeToDeflect) {
            timeToDeflect = false;
            launchDeflectedProjectile();
        }
        if (chargeEffect.isFullyCharged()) {
            animations.flashPlayerSprite();
        }
        if (spellCooldownTimer > 0) {
            spellCooldownTimer -= dt;
            canCastSpell = false;
        } else {
            canCastSpell = true;
        }
        if (charging || chargeEffect.isFullyCharged()) {
            chargingTimer += dt;
        }
        if (mana < FULL_MANA && !firingSpell) {
            mana += 7 * dt;
        }
        if (currentState == State.CASTING) {

        }
        setPosition(getXPos(), getYPos() + 0.1f);
        setRegion(getFrame(dt));
        limitSpeed();
        updateAverageYVelocity();
        updateAverageXVelocity();
        if (spellBallTimer > 0) {
            spellBallTimer -= dt;
        }
        if (currentState == State.DOWN_ATTACK) {
            b2body.setLinearVelocity(0, -3.5f);
        }
        if (!isFalling()) {
            downAttacking = false;
        }

        if (currentState == State.CHARGING_BOW) {
            chargeBow(dt);
        }
        if (charging) {
            spellCharge += dt;
        }
        if (currentState == State.CASTING) {
            fireRate -= dt;
        } else {
            fireRate = -1f;
        }

        if (currentState == State.CROUCH_WALKING) {
            currentMaxSpeed = CROUCH_SPEED;
        } else {
            currentMaxSpeed = MAX_RUN_SPEED;
        }
        if (canWallRun()) {
            if (thereIsXInputOnJoystick()) {
                climbWall();
            }
        }
        if (currentState != State.DYING) {
            applyHealthRegeneration(dt);
        }
        if (currentState == State.REVIVING) {
            if (animations.reviveAnimationDone(stateTimer)) {
                health = FULL_HEALTH;
            }
        }
        if (currentState == State.DYING) {
            deathTimer += dt;
            if (timeToStartTeleport()) {
                startTeleportEffectIfNotStarted();
            }
            if (deathTimer >= DEATH_TIME) {
                if (!playerReset) {
                    resetPlayer();
                    if (!resurrectStarted) {
                        setAlpha(1);
                        screen.getTopLayerSpritesToAdd().add(new Resurrect(screen, b2body.getPosition().x - 0.25f, b2body.getPosition().y));
                        resurrectStarted = true;
                    }
                }
            }
            if (ressurectComplete()) {
                reviveTimer = 2.2f;
                stateTimer = 0;
                deathTimer = 0;
                playerReset = false;
                resurrectStarted = false;
                squarePortalStarted = false;
            }
        }

        if (currentState == State.TELEPORTING) {
            teleportTimer += dt;
            if (teleportTimer >= TELEPORT_SPELL_TIME) {
                if (!teleportPortalStarted) {
                    setAlpha(0);
                    screen.getTopLayerSpritesToAdd().add(new SquarePortal(screen, getX() + 0.07f, getY() - 0.15f));
                    teleportPortalStarted = true;
                }
            }
            if (teleportTimer >= TELEPORT_TIME) {
                if (!playerReset) {
                    resetPlayer();
                    if (!teleportResurrectStarted) {
                        screen.getTopLayerSpritesToAdd().add(new Resurrect(screen, b2body.getPosition().x - 0.25f, b2body.getPosition().y));
                        teleportResurrectStarted = true;
                        screen.changeMap();
                    }
                }
            }
            if (teleportAnimationOver()) {
                setAlpha(1);
            }
            if (teleportResurectionOver()) {
                teleportTimer = 0;
                stateTimer = 0;
                teleportResurrectStarted = false;
                playerReset = false;
                teleporting = false;
                teleportPortalStarted = false;
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
                    launchArrow();
                    arrowLaunched = true;
                }
            }
        }

        if (currentState == State.PICKUP) {
            if (itemPickupTimer < 0.2f && hasTeleportItem) {
                hasTeleportItem = false;
                itemPickupTimer = 0;
                teleport();

            }
        }
        updateTimers(dt);

        if (currentState == State.CASTING) {
            if (stateTimer > 0.1f && castCooldown > 0) {
                castFireSpell();
                canFireProjectile = false;
            }
        }
    }

    private boolean ressurectComplete() {
        return deathTimer >= REVIVE_TIME;
    }

    private boolean timeToStartTeleport() {
        return deathTimer >= DEATH_SPELL_TIME;
    }

    private void startTeleportEffectIfNotStarted() {
        if (!squarePortalStarted) {
            setAlpha(0);
            screen.getTopLayerSpritesToAdd().add(new SquarePortal(screen, getX() + 0.07f, getY() - 0.15f));
            squarePortalStarted = true;
        }
    }

    private void applyHealthRegeneration(float dt) {
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

    private void climbWall() {
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, 1.5f);
    }

    private boolean thereIsXInputOnJoystick() {
        return positiveXInput || negativeXInput;
    }

    private void chargeBow(float dt) {
        if (arrowCharge < MAX_ARROW_CHARGE) {
            arrowCharge += dt;
        }else {
            animations.pauseChargingAnimation();
        }
    }

    private void updateTimers(float dt) {
        if (attackCooldownTimer > 0) {
            attackCooldownTimer -= dt;
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
        if (reviveTimer > 0)
            reviveTimer -= dt;
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
        if (isDodging()) {
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
    }

    private boolean teleportResurectionOver() {
        return teleportTimer >= TELEPORT_REVIVE_TIME;
    }

    private boolean teleportAnimationOver() {
        return teleportTimer > TELEPORT_TIME + TELEPORT_DELAY;
    }

    private void castFireSpell() {
        screen.spellsToSpawn.add(new FireSpell(screen, getX() - getWidth() / 2, getY() - getHeight() / 2, runningRight, this));
    }

    @Override
    public void draw(Batch batch) {
        if (charging || chargeEffect.isFullyCharged()) {
            chargeEffect.update(chargingTimer);
            chargeEffect.draw(batch);
        }
        super.draw(batch);

        if (itemPickupTimer > 0) {
            dialogBox.setPosition(b2body.getPosition().x - dialogBox.getWidth() / 2, b2body.getPosition().y + dialogBox.getHeight() / 2);
            itemSprite.setPosition(b2body.getPosition().x - 0.1f, b2body.getPosition().y + 0.15f);
            itemSprite.draw(batch);
//            dialogBox.draw(batch);
        }
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region = animations.getFrame(dt);
        if (currentState == State.AIR_ATTACKING) {
            attackNumber = 0;
            comboTimer = -1;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (teleporting) {
            return State.TELEPORTING;
        }
        if (downAttacking) {
            return State.DOWN_ATTACK;
        }
        if (canWallRun && wallrunTimer > 0) {
            if (thereIsXInputOnJoystick())
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
                if (animations.attackAnimationFinished(attackNumber, stateTimer)) {
                    return State.RUNNING;
                }
            }
            if (Math.abs(averageYVelocity) > 0.01f && !onElevator && !downAttacking) {
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
        } else if (isFalling() && !onElevator) {
            return State.FALLING;
        } else if (isDodging()) {
            return State.DODGING;
        } else if (isRunning()) {
            flipEnabled = true;
            return State.RUNNING;
        } else {
            flipEnabled = true;
            return State.STANDING;
        }
    }


    public void hurt(int damage) {
        firingSpell = false;
        stopCharging();
        if (invincibilityTimer < 0) {
            invincibilityTimer = INVINCIBLE_TIME;
            hurtTimer = HURT_TIME;
            if (hasProtection) {
                damage -= 1;
            }
            health -= damage;
            animations.flashPlayerSprite();
            endChargingSpell();
            screen.getDamageNumbersToAdd().add(new DamageNumber(screen, getXPos(), getYPos(), true, damage));
            if (currentState == State.CHARGING_BOW) {
                bowAttack();
            }
        }
    }

    public void jump() {
        if (currentState == State.JUMPING || currentState == State.FALLING || currentState == State.FLIPPING || currentState == State.AIR_ATTACKING || currentState == State.WALLCLIMB || currentState == State.SHOOTING) {
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
        if (attackCooldownTimer < 0) {
            attackCooldownTimer = ATTACK_COOLDOWN_TIME;
            screen.getSoundEffects().playSwordSound();
            b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            if (attackTimer < 0) {
                attackTimer = ATTACK_TIME;
                if (swordFixture == null) {
                    swordFixture = playerBody.createAttack();
                }
                if (attackNumber == 1) {
//                    if (runningRight) {
//                        b2body.applyLinearImpulse(new Vector2(1f, 0), b2body.getWorldCenter(), true);
//                    } else {
//                        b2body.applyLinearImpulse(new Vector2(-1f, 0), b2body.getWorldCenter(), true);
//                    }
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
        } else if (equipedSpell == Spell.SHIELD) {
            if (castCooldown < 0) {
                castTimer = CAST_TIME;
                castCooldown = CAST_COOLDOWN_TIME;
                shieldTimer = SHIELD_TIME;
            }
        } else if (equipedSpell == Spell.BOW) {
            if (arrowCooldown <= 0)
                chargingBow = true;
        }
    }

    public void swingSword() {
        attack();
    }

    public void fireBow() {
        if (canMove()) {
            if (arrowCooldown <= 0)
                chargingBow = true;
        }
    }

    public void bowAttack() {
        if (hasArrows()) {
            arrowCount--;
            if (shootingTimer <= 0 && currentState != State.SHOOTING && arrowCooldown <= 0) {
                animations.restartChargingAnimation();
                shootArrow();
                arrowCooldown = ARROW_COOLDOWN_TIME - arrowCharge;
                arrowLaunched = false;
            }
        }
    }

    private boolean hasArrows() {
        return arrowCount > 0;
    }

    private void launchArrow() {
        boolean ballDirectionRight;
        if (runningRight) {
            ballDirectionRight = true;
        } else {
            ballDirectionRight = false;
        }
        screen.getSpritesToAdd().add(new Arrow(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, ballDirectionRight, true, arrowCharge));

        arrowCharge = 0;
    }

    private void launchSpellBall() {
//        screen.changeMap();
        animations.endPlayerFlash();
        if (canCastSpell) {
            if (chargeEffect.isFullyCharged()) {

            }
            castTimer = CAST_TIME;
            chargeEffect.reset();
//            screen.getSpritesToAdd().add(new SpellBall(screen, getX() +0.25f , getY() + getHeight() / 2, runningRight, true, spellCharge));
            float x_offset = 0.1f;
            if (!runningRight) {
                x_offset *= -1;
            }
            screen.getSpritesToAdd().add(new SpellBall(screen, getX() + 0.25f + x_offset, getY() + getHeight() / 2, runningRight, true, spellCharge));
            spellCharge = 0;
            spellCooldownTimer = SPELL_COOLDOWN_TIME;
            canCastSpell = false;
        }
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
//        playerCast.setFrameDuration(4f);
    }

    public void startChargingAnimation() {
//        playerCast.setFrameDuration(0.1f);
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
//        Random random = new Random();
//        int damage = random.nextInt(3 + swordLevel) + 3 + swordLevel / 2;
//        if (attackNumber == 2) {
//            damage = 5 + swordLevel;
//        }
//        return damage;
        return 5;
    }

    public boolean canPassFloor() {
        return passThroughFloor;
    }

    public void setCanPassFloor(boolean state) {
        passThroughFloor = state;
    }

    public void dodge() {
        passThroughFloorTimer = 0.2f;
        if (dodgeCooldown <= 0) {
            dodgeCooldown = 1.25f;
            if (b2body.getLinearVelocity().y == 0) {
                dodgeTimer = 0.35f;
                if (runningRight) {
                    b2body.setLinearVelocity(currentSpeed, b2body.getLinearVelocity().y);

                } else {
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
        b2body = playerBody.createSmallHitBox();
    }

    private void changeToBigHitBox() {
        world.destroyBody(b2body);
        b2body = playerBody.createBigHitBox();
    }


    private void resetPlayer() {
        world.destroyBody(b2body);
        b2body = playerBody.definePlayer();
    }

    public boolean doneDying() {
        if (currentState == State.DYING) {
            return deathTimer >= DEATH_TIME;
        }
        return false;
    }

    public boolean doneTeleporting() {
        if (currentState == State.DYING) {
            return deathTimer >= DEATH_TIME;
        } else {
            return teleportTimer >= TELEPORT_TIME;
        }
    }

    public boolean canMove() {
        return (
                currentState != State.DYING
                        && currentState != State.REVIVING
                        && currentState != State.PICKUP
                        && currentState != State.HURT
                        && currentState != State.CHARGING_BOW
                        && currentState != State.CASTING
                        && currentState != State.ATTACKING
                        && currentState != State.TELEPORTING
        );
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
            case AdventureGame.GOLD_COIN:
                break;
            case AdventureGame.ARROW:
                if (arrowCount < MAX_ARROWS) {
                    arrowCount += 1;
                }
                break;
            case AdventureGame.GOLD_KEY:
            case AdventureGame.RED_KEY:
            case AdventureGame.BLUE_KEY:
            case AdventureGame.SILVER_KEY:
                itemPickupTimer = 2f;
                hasTeleportItem = true;
                break;
            default:
                break;
        }
        pickedUpItem = getItemTexture(itemID);
        itemSprite.setRegion(pickedUpItem);
//        dialogBox.setRegion(getItemDialog(itemID));
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
            case AdventureGame.GOLD_KEY:
                assetName = "gold_key";
                break;
            case AdventureGame.RED_KEY:
                assetName = "red_key";
                break;
            case AdventureGame.BLUE_KEY:
                assetName = "blue_key";
                break;
            case AdventureGame.SILVER_KEY:
                assetName = "silver_key";
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
        if (averageYVelocity < -0.01) {
            return true;
        }
        return false;
    }

    private boolean isJumping() {
        if (b2body.getLinearVelocity().y > 0.1f) {
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

        if (currentState == State.CASTING) {
            if (onElevator) {
                b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            }
        }
        if (currentState == State.TELEPORTING) {
            b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
        }
    }

    public boolean canAct() {
        return currentState != State.HURT;
    }

    public void enableWallRun() {
        //Todo wallrun disabled
//        if (currentState == State.FALLING || currentState == State.JUMPING || currentState == State.FLIPPING) {
//            if (jumpIsHeld) {
//                if (wallrunTimer < 0 && wallRunCooldown <= 0) {
//                    wallrunTimer = WALLRUN_TIME;
//                    wallRunCooldown = 1f;
//                }
//
//                canWallRun = true;
//            }
//        }
    }

    public void addXMovement(float x) {
        b2body.setLinearVelocity(x, b2body.getLinearVelocity().y);
    }

    public void setOnElevator(boolean state) {
        onElevator = state;
    }

    private boolean canWallRun() {
        return (wallrunTimer > 0 && canWallRun);
    }

    public void disableWallRun() {
        canWallRun = false;
    }

    public void setRespawnPoint(CheckPoint checkPoint) {
        currentCheckPoint = checkPoint;
        if (!currentCheckPoint.activated()) {
            health = FULL_HEALTH;
            reloadArrows();
            currentCheckPoint.setActive();
        }
        spawnPointY = checkPoint.getYPos();
        spawnPointX = checkPoint.getXPos();
        playerBody.setSpawnPoint(spawnPointX, spawnPointY);
    }

    public CheckPoint getCurrentCheckPoint() {
        return currentCheckPoint;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public void setStickRotation(float angle) {
        stickRotation = angle;
    }

    public void enableSpellBall() {
        spellBallTimer = 5f;
    }

    public float getMana() {
        return mana;
    }

    public void startCharge() {
        charging = true;

    }

    public void stopCharging() {
        charging = false;
    }

    public boolean isMoving() {

        switch (currentState) {
            case RUNNING:
            case FALLING:
            case FLIPPING:
            case DODGING:
            case JUMPING:
                return true;
            default:
                return false;

        }
    }

    private void updateAnimationCoords() {
        animationCenterX = getX();
        animationCenterY = getY();

        if (isMoving()) {
            if (isDodging()) {
                if (runningRight) {
                    animationCenterX -= 0.05f;
                } else {
                    animationCenterX += 0.05f;
                }
            } else {
                if (runningRight) {
                    animationCenterX += 0.05f;
                } else {
                    animationCenterX -= 0.035f;
                }
            }
        }
        if (currentState == State.DODGING) {
            animationCenterY -= .13f;
        } else if (currentState == State.ATTACKING) {
            animationCenterY -= 0.05f;
            if (runningRight) {
                animationCenterX += 0.03f;
            } else {
                animationCenterX -= 0.03f;
            }
        }
    }

    private boolean isDodging() {
        return dodgeTimer > 0;
    }

    public float getXCoord() {
        updateAnimationCoords();
        return animationCenterX;
    }

    public float getYCoord() {
        updateAnimationCoords();
        return animationCenterY;
    }

    public void knockedBack() {
        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }

    public int getArrowCount() {
        return arrowCount;
    }

}
