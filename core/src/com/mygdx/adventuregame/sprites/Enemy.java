package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.screens.PlayScreen;

public abstract class Enemy extends Sprite implements UpdatableSprite{

    public enum State {ATTACKING, WALKING, DYING, HURT, CHASING, IDLE}

    public State currentState;
    public State previousState;

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public boolean attackEnabled;
    protected boolean destroyed;
    public boolean safeToRemove = false;

    protected float flashRedTimer;
    protected float stateTimer;
    private int flashCount = 0;
    private boolean flashFrame = true;
    protected int health;
    protected boolean showHealthBar = false;

    protected float affectedBySpellTimer = -1f;
    protected static final float SPELL_EFFECT_TIME = 1f;
    protected int attackDamage = 1;

    public float barXOffset = 0;
    public float barYOffset = 0;

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

    protected abstract void defineEnemy();

    public abstract void hitOnHead();
    public abstract void damage(int amount);
    public abstract boolean notDamagedRecently();
    public abstract void update(float dt);
    public boolean isDestroyed(){
        return destroyed;
    }

    protected Vector2 getVectorToPlayer(){
        Vector2 enemyPosition = new Vector2(this.getX(), this.getY());
        Vector2 playerVector = new Vector2(screen.getPlayer().getX(), screen.getPlayer().getY());
        return playerVector.sub(enemyPosition);
    }

    public boolean isHurt(){
        return currentState == State.HURT;
    }

    protected TextureRegion selectBrightFrameOrRegularFrame(Animation<TextureRegion> animation, Animation<TextureRegion> brightAnimation){
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
        }else {
            textureRegion = animation.getKeyFrame(stateTimer);
        }
        return textureRegion;
    }

    public void hitByFire(){
//        if(affectedBySpellTimer < 0){
            screen.getExplosions().add(new Explosion(screen, getX() - getWidth() / 2, getY() - getHeight() / 2));
//
//            affectedBySpellTimer = SPELL_EFFECT_TIME;
//        }
    }
    public int getDamage(){
        return attackDamage;
    }

    public int getHealth(){ return health;}
    public boolean isAlive(){
        return health > 0;
    }

    public boolean showHealthBar(){
        return showHealthBar;
    }
}

