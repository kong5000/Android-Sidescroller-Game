package com.mygdx.adventuregame.sprites.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.UpdatableSprite;

public abstract class Effects extends Sprite implements UpdatableSprite {
    public boolean safeToRemove = false;
    protected float stateTimer;
    protected PlayScreen screen;
    protected Animation<TextureRegion> animation;
    public Effects(PlayScreen screen, float x, float y){
        this.screen = screen;
        setPosition(x, y);
        initializeAnimation();
    }

    protected abstract void initializeAnimation();

    public void update(float dt) {
        setRegion(animation.getKeyFrame(stateTimer));
        stateTimer += dt;
        if(animation.isAnimationFinished(stateTimer)){
            safeToRemove = true;
        }
    }

    @Override
    public boolean safeToRemove() {
        return false;
    }

    @Override
    public void draw(Batch batch) {
        if (!safeToRemove) {
            super.draw(batch);
        }
    }

    public void setToDestroy(){
        safeToRemove = true;
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


}
