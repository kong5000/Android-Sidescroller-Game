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


    public enum State {
        FALLING, JUMPING, STANDING,
        RUNNING, HURT, ATTACKING,
        AIR_ATTACKING, FLIPPING,
        CASTING, DODGING, CROUCHING,
        SHOOTING, DYING, REVIVING
    }

    public enum Spell {FIREBALL, SHIELD, BOW}

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
    private Animation<TextureRegion> playerRevive;
    private Animation<TextureRegion> playerAttack;
    private Animation<TextureRegion> playerAttack2;
    private Animation<TextureRegion> playerAttack3;
    private Animation<TextureRegion> playerAirAttack;
    private Animation<TextureRegion> playerFlip;
    private Animation<TextureRegion> playerCast;
    private Animation<TextureRegion> playerDodge;
    private Animation<TextureRegion> playerBow;
    private Animation<TextureRegion> playerCrouch;

    private boolean canFireProjectile;
    private boolean passThroughFloor = false;
    private boolean isCrouching = false;
    private boolean canDodge = false;
    public boolean chargingSpell = false;
    public boolean runningRight;
    private boolean flipEnabled;
    private boolean arrowLaunched = false;

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

    private static final float CAST_COOLDOWN_TIME = 1f;
    private static final float ARROW_COOLDOWN_TIME = 0.8f;
    private static final float SHOOT_ARROW_TIME = 0.5f;
    public static final float SHIELD_TIME = 3.5f;
    private static final float HURT_TIME = 0.5f;
    private static final float CAST_TIME = 0.5f;
    private static final float ATTACK_TIME = 0.35f;
    private static final float FLIP_TIME = 0.4f;
    private static final float MAX_VERTICAL_SPEED = 3f;

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

    private boolean playerReset =false;


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
        playerDie = generateAnimation(textureAtlas.findRegion("player_die"), 7, 52, 39, 0.3f);
        playerRevive = generateAnimation(textureAtlas.findRegion("player_revive"), 7, 50, 37, 0.3f);

        playerAttack = generateAnimation(textureAtlas.findRegion("player_attack1"), 5, 52, 39, 0.07f);
        playerAttack2 = generateAnimation(textureAtlas.findRegion("player_attack2"), 6, 50, 37, 0.0575f);
        playerAttack3 = generateAnimation(textureAtlas.findRegion("player_attack3"), 6, 50, 37, 0.0575f);
        playerAirAttack = generateAnimation(textureAtlas.findRegion("player_air_attack1"), 4, 52, 39, 0.125f);
        playerFlip = generateAnimation(textureAtlas.findRegion("player_flip"), 4, 52, 39, 0.1f);
        playerCast = generateAnimation(textureAtlas.findRegion("player_cast"), 4, 52, 39, 0.1f);
        playerDodge = generateAnimation(textureAtlas.findRegion("player_dodge"), 5, 50, 37, 0.07f);
        playerCrouch = generateAnimation(textureAtlas.findRegion("player_crouch"), 4, 50, 37, 0.1f);
        playerBow = generateAnimation(textureAtlas.findRegion("player_bow"), 5, 50, 37, 0.1f);
        playerStand = new TextureRegion(getTexture(), 0, 0, 50, 37);

        definePlayer();
        setBounds(0, 0, 50 / AdventureGame.PPM, 37 / AdventureGame.PPM);
        setRegion(playerStand);

        magicShield = new MagicShield(screen, b2body.getPosition().x, b2body.getPosition().y, this);
        magicShield.setAlpha(0);
    }

    private void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(420 / AdventureGame.PPM, 460 / AdventureGame.PPM);
//        bodyDef.position.set(5015 / AdventureGame.PPM, 550 / AdventureGame.PPM);
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

//        PolygonShape shape = new PolygonShape();
//        shape.set(MINOTAUR_HITBOX);

        CircleShape shape = new CircleShape();
        shape.setRadius(11 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        b2body.createFixture(fixtureDef).setUserData(this);

    }

    public void update(float dt) {
        setPosition(getXPos(), getYPos());
        setRegion(getFrame(dt));
        if(currentState == State.REVIVING){
            if(playerRevive.isAnimationFinished(stateTimer)){
                health = FULL_HEALTH;
            }
        }
        if(currentState == State.DYING){
            deathTimer += dt;
            if(deathTimer >= DEATH_SPELL_TIME){

                magicShield.setAlpha(1);
                magicShield.setScale(magicShieldSize);
                if(magicShieldSize < 1){
                    magicShieldSize += 0.01f;
                }
            }
            if(deathTimer >= DEATH_TIME){
                magicShieldAlpha -= 0.005;
                if(magicShieldAlpha < 0){
                    magicShieldAlpha = 0;
                }
                magicShield.setAlpha(magicShieldAlpha);
                if(!playerReset){
                    resetPlayer();
                }
            }
            if(deathTimer >= REVIVE_TIME){
                reviveTimer = 2.2f;
                stateTimer = 0;
                magicShield.setAlpha(0);
                magicShieldAlpha = 1f;
                deathTimer = 0;
                playerReset = false;
            }
        }
        if (currentState == State.SHOOTING) {
            if (stateTimer >= 0.25f) {
                if (!arrowLaunched) {
                    launchFireBall();
                    arrowLaunched = true;
                }
            }
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
        if(reviveTimer > 0){
            reviveTimer -=dt;
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

        if(arrowCooldown > 0){
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
        magicShield.draw(batch);
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case REVIVING:
                region = playerRevive.getKeyFrame(stateTimer);
                break;
            case DYING:
                region = playerDie.getKeyFrame(stateTimer);
                break;
            case SHOOTING:
                region = playerBow.getKeyFrame(stateTimer);
                break;
            case CROUCHING:
                region = playerCrouch.getKeyFrame(stateTimer);
                break;
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
        if(reviveTimer > 0){
            return State.REVIVING;
        }
        if(health <= 0){
            return State.DYING;
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
            if (Math.abs(b2body.getLinearVelocity().y) > 0) {
                return State.AIR_ATTACKING;
            }
            return State.ATTACKING;

        } else if (shootingTimer > 0) {
            return State.SHOOTING;
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
        } else if (isCrouching) {
            return State.CROUCHING;
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
        screen.getDamageNumbersToAdd().add(new DamageNumber(screen, getXPos(), getYPos(), true, damage));
    }

    public void jump() {
        if (currentState == State.JUMPING || currentState == State.FALLING || currentState == State.FLIPPING || currentState == State.AIR_ATTACKING) {
            if (flipEnabled) {
                flipTimer = FLIP_TIME;
                flipEnabled = false;
                b2body.applyLinearImpulse(new Vector2(0, 6f), b2body.getWorldCenter(), true);
                if (b2body.getLinearVelocity().y > MAX_VERTICAL_SPEED) {
                    b2body.setLinearVelocity(b2body.getLinearVelocity().x, MAX_VERTICAL_SPEED);
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
            if (castCooldown < 0) {
                castTimer = CAST_TIME;
                castCooldown = CAST_COOLDOWN_TIME;
                canFireProjectile = true;
            }
        } else if(equipedSpell == Spell.SHIELD){
            if (castCooldown < 0) {
                castTimer = CAST_TIME;
                castCooldown = CAST_COOLDOWN_TIME;
                shieldTimer = SHIELD_TIME;
                magicShield.setAlpha(1);
            }
        } else if(equipedSpell == Spell.BOW){
            if (shootingTimer <= 0 && currentState != State.SHOOTING) {
                if(arrowCooldown <= 0){
                    arrowCooldown = ARROW_COOLDOWN_TIME;
                    arrowLaunched = false;
                    shootArrow();
                }

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
        switch (equipedSpell){
            case FIREBALL:
                equipedSpell = Spell.SHIELD;
                break;
            case SHIELD:
                equipedSpell = Spell.BOW;
                break;
            case BOW:
                equipedSpell = Spell.FIREBALL;
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
        passThroughFloorTimer = 0.2f;
        if (dodgeCooldown <= 0) {
            dodgeCooldown = 1.25f;
            if (b2body.getLinearVelocity().y == 0) {
                dodgeTimer = 0.35f;
                if (runningRight) {
                    b2body.applyLinearImpulse(new Vector2(2f, 0), b2body.getWorldCenter(), true);
                } else {
                    b2body.applyLinearImpulse(new Vector2(-2f, 0), b2body.getWorldCenter(), true);

                }
            }
        }
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
        if (shootingTimer <= 0) {
            shootingTimer = SHOOT_ARROW_TIME;
        }
    }

    private void resetPlayer(){
        world.destroyBody(b2body);
        definePlayer();
    }

    public boolean doneDying(){
        if(currentState == State.DYING){
            return deathTimer >= DEATH_TIME;
        }
        return  false;

    }

    public boolean canMove(){
        return(currentState != State.DYING && currentState!= State.REVIVING);
    }
}
