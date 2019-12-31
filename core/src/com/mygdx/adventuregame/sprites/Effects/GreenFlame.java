package com.mygdx.adventuregame.sprites.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class GreenFlame extends Effects{
    public GreenFlame(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(x, y);
        setScale(1.4f);
    }

    @Override
    protected void initializeAnimation() {
        TextureRegion region = screen.getAtlas().findRegion("green_flame");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 11; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * 52,
                        j * 54,
                        52,
                        54
                ));
            }
        }
        animation = new Animation(0.02f, frames, Animation.PlayMode.NORMAL);
        setBounds(getX(), getY(), 52 / AdventureGame.PPM, 54 / AdventureGame.PPM);
        setScale(1f);
    }

}