package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class Explosion extends Sprite implements UpdatableSprite{
    public boolean safeToRemove = false;
    protected float stateTimer;
    protected PlayScreen screen;
    protected Animation<TextureRegion> animation;
    public Explosion(PlayScreen screen, float x, float y) {
        this.screen = screen;
        setPosition(x, y);
        initializeAnimation();

    }

    protected void initializeAnimation() {
        animation = generateAnimation(screen.getAtlas().findRegion("explosion"),
                44, 96, 96, 0.02f);
        setBounds(getX(), getY(), 96 / AdventureGame.PPM, 96 / AdventureGame.PPM);
    }


    public void update(float dt) {
        setRegion(animation.getKeyFrame(stateTimer));
        stateTimer += dt;
        if(animation.isAnimationFinished(stateTimer)){
            safeToRemove = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!safeToRemove) {
            super.draw(batch);
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


    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }
}
