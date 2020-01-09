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
    public enum State {ATTACKING, WALKING, DYING, HURT, CHASING, IDLE, TRANSFORMING, CHARGING, CAST}

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
    private boolean flashFrame = true;
    protected int health;
    protected boolean showHealthBar = false;

    protected float affectedBySpellTimer = -1f;
    protected static final float SPELL_EFFECT_TIME = 1f;
    protected int attackDamage = 2;

    public float barXOffset = 0;
    public float barYOffset = 0;

    protected int experiencePoints = 10;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        attackEnabled = false;
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

    protected void defineEnemy(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.PLAYER_SWORD_BIT
                | AdventureGame.PLAYER_PROJECTILE_BIT
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

    protected TextureRegion selectBrightFrameOrRegularFrame(Animation<TextureRegion> animation, Animation<TextureRegion> brightAnimation) {
        TextureRegion textureRegion;
        if (flashRedTimer > 0) {
            if (flashFrame) {
                flashCount++;
                if (flashCount > 2) {
                    flashFrame = false;
                    flashCount = 0;
                }
                textureRegion = brightAnimation.getKeyFrame(stateTimer);
            } else {
                flashCount++;
                if (flashCount > 2) {
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

    protected void initializeTimers(){

    }

    public int getExperiencePoints(){
        return experiencePoints;
    }

    public void dispose(){
        if(b2body != null){
            world.destroyBody(b2body);
        }
    }
    public void setToDestroy(){
        setToDestroy = true;
    };
}

