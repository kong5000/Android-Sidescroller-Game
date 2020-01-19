package com.mygdx.adventuregame.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class EnemyAnimations {
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private TextureAtlas textureAtlas;

    public EnemyAnimations(TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
    }

    public void initMoveAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        walkAnimation = generateAnimation(textureAtlas.findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void initAttackAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        attackAnimation = generateAnimation(textureAtlas.findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
    }

    public void initHurtAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        hurtAnimation = generateAnimation(textureAtlas.findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
    }

    public void initIdleAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        idleAnimation = generateAnimation(textureAtlas.findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void initDeathAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        deathAnimation = generateAnimation(textureAtlas.findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
    }

    public void initJumpAnimation(
            String region_name,
            int numberOfFrames,
            int width, int height,
            float secondsPerFrame
    ) {
        jumpAnimation = generateAnimation(textureAtlas.findRegion(region_name),
                numberOfFrames,
                width,
                height,
                secondsPerFrame);
        jumpAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public Animation<TextureRegion> generateAnimation(
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
    public boolean isAttackAnimationFinished(float stateTime){
        return attackAnimation.isAnimationFinished(stateTime);
    }

    public TextureRegion getDeathFrame(float stateTime){
        return deathAnimation.getKeyFrame(stateTime);
    }
    public TextureRegion getHurtFrame(float stateTime){
        return hurtAnimation.getKeyFrame(stateTime);
    }
    public TextureRegion getAttackFrame(float stateTime){
        return attackAnimation.getKeyFrame(stateTime);
    }
    public TextureRegion getIdleFrame(float stateTime){
        return idleAnimation.getKeyFrame(stateTime);
    }
    public TextureRegion getMoveFrame(float stateTime){
        return walkAnimation.getKeyFrame(stateTime);
    }
    public TextureRegion getJumpFrame(float stateTime){
        return jumpAnimation.getKeyFrame(stateTime);
    }

    public boolean isDeathAnimationFinished(float stateTime) {
        return deathAnimation.isAnimationFinished(stateTime);
    }
}
