package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;

public abstract class Enemy extends Sprite implements UpdatableSprite {
    public enum State {ATTACKING, WALKING, DYING, HURT, CHASING, IDLE, TRANSFORMING, CHARGING, CAST, SPECIAL_ATTACK, SUMMON, JUMPING}

    public State currentState;
    public State previousState;

    //Todo getter and setters
    //Todo move protected variable to subclasses as private variables.
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public boolean attackEnabled;
    protected boolean destroyed;
    public boolean safeToRemove = false;

    protected float flashRedTimer;
    protected float stateTimer;
    protected float invincibilityTimer;
    protected float hurtTimer = -1f;
    protected boolean setToDestroy;
    protected boolean runningRight;
    private int flashCount = 0;
    public boolean flashFrame = false;
    protected int health;
    protected boolean showHealthBar = false;

    protected float affectedBySpellTimer = -1f;
    protected static final float SPELL_EFFECT_TIME = 1f;
    protected int attackDamage = 2;

    public float barXOffset = 0;
    public float barYOffset = 0;

    protected int experiencePoints = 10;

    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> walkAnimationDamaged;
    protected Animation<TextureRegion> deathAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> attackAnimationDamaged;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> hurtAnimationDamaged;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> idleAnimationDamaged;
    protected Animation<TextureRegion> jumpAnimation;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        attackEnabled = false;
    }

    protected void initMoveAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        walkAnimation = generateAnimation(screen.getAtlas().findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    protected void initAttackAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        attackAnimation = generateAnimation(screen.getAtlas().findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
    }

    protected void initHurtAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
    }

    protected void initIdleAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        idleAnimation = generateAnimation(screen.getAtlas().findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    protected void initDeathAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        deathAnimation = generateAnimation(screen.getAtlas().findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
    }

    protected void initJumpAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        jumpAnimation = generateAnimation(screen.getAtlas().findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
        jumpAnimation.setPlayMode(Animation.PlayMode.LOOP);
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

    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.PLAYER_SWORD_BIT
                | AdventureGame.ARROW_BIT
                | AdventureGame.FIRE_SPELL_BIT;
        Shape hitBox = getHitBoxShape();
        fixtureDef.shape = hitBox;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    public abstract void hitOnHead();

    public abstract void damage(int amount);

    public abstract boolean notDamagedRecently();

    public abstract void update(float dt);

    public boolean isDestroyed() {
        return destroyed;
    }

    protected Vector2 getVectorToPlayer() {
        Vector2 enemyPosition = new Vector2(b2body.getPosition().x, b2body.getPosition().y);
        Vector2 playerVector = new Vector2(screen.getPlayer().b2body.getPosition().x, screen.getPlayer().b2body.getPosition().y);
        return playerVector.sub(enemyPosition);
    }

    public boolean isHurt() {
        return currentState == State.HURT;
    }

    protected void selectBrightFrameOrRegularFrame() {
        if (flashRedTimer > 0) {
            if (flashFrame) {
                flashCount++;
                if (flashCount > 2) {
                    flashFrame = false;
                    flashCount = 0;
                }
            } else {
                flashCount++;
                if (flashCount > 2) {
                    flashFrame = true;
                    flashCount = 0;
                }
            }
        } else {
            flashFrame = false;
        }
    }

    protected abstract State getState();

    protected abstract void orientTextureTowardsPlayer(TextureRegion texture);

    protected TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion texture;
        selectBrightFrameOrRegularFrame();
        switch (currentState) {
            case DYING:
                attackEnabled = false;
                texture = deathAnimation.getKeyFrame(stateTimer);
                break;
            case JUMPING:
                attackEnabled = false;
                texture = jumpAnimation.getKeyFrame(stateTimer, true);
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

    public void hitByFire() {
        screen.getExplosions().add(new Explosion(screen, getX() - getWidth() / 2, getY() - getHeight() / 2));
    }

    public int getDamage() {
        return attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean showHealthBar() {
        return showHealthBar;
    }

    protected abstract Shape getHitBoxShape();

    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

    protected void initializeTimers() {

    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void dispose() {
        if (b2body != null) {
            world.destroyBody(b2body);
        }
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    ;

    public void generateArrow(boolean goingRight) {

    }
}

