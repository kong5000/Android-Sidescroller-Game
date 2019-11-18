package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.screens.PlayScreen;

public abstract class Enemy extends Sprite {

    public enum State {ATTACKING, WALKING, DYING, HURT, CHASING, IDLE}

    public State currentState;
    public State previousState;

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public boolean attackEnabled;
    protected boolean destroyed;
    public boolean safeToRemove = false;

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
    public abstract void damage();
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
}

