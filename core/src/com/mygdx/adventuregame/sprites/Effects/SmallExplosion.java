package com.mygdx.adventuregame.sprites.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Effects;

public class SmallExplosion extends Effects {

        public SmallExplosion(PlayScreen screen, float x, float y) {
            super(screen, x, y);
            setOrigin(getWidth() / 2, getHeight() / 2);
            setPosition(x, y);
            screen.getSoundEffects().playExplosionSound();
        }

        @Override
        protected void initializeAnimation() {
            TextureRegion region = screen.getAtlas().findRegion("small_explosion");
            Array<TextureRegion> frames = new Array<TextureRegion>();
            for (int j = 0; j < 6; j++) {
                for (int i = 0; i < 9; i++) {
                    frames.add(new TextureRegion(
                            region,
                            i * 100,
                            j * 100,
                            100,
                            100
                    ));
                }
            }
            animation = new Animation(0.02f, frames, Animation.PlayMode.NORMAL);
            setBounds(getX(), getY(), 100 / AdventureGame.PPM, 100 / AdventureGame.PPM);
            setScale(1f);
        }


}
