package com.mygdx.adventuregame.sprites.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class BlueFlame extends Effects{
    public BlueFlame(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(x -0.02f, y);
        setScale(1.3f);
    }

    @Override
    public void update(float dt) {
        setRegion(animation.getKeyFrame(stateTimer));
        stateTimer += dt;
    }

    @Override
    protected void initializeAnimation() {
        TextureRegion region = screen.getAtlas().findRegion("blue_flame");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 9; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * 9,
                        j * 24,
                        9,
                        24
                ));
            }
        }
        animation = new Animation(0.02f, frames, Animation.PlayMode.LOOP);
        setBounds(getX(), getY(), 11 / AdventureGame.PPM, 24 / AdventureGame.PPM);
        setScale(1f);
    }

}
