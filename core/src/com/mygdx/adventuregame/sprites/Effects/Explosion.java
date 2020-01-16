package com.mygdx.adventuregame.sprites.Effects;

import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Effects;
import com.mygdx.adventuregame.sprites.UpdatableSprite;

public class Explosion extends Effects implements UpdatableSprite {
    public Explosion(PlayScreen screen, float x, float y) {
        super(screen, x, y);
    }

    @Override
    protected void initializeAnimation() {
        screen.getSoundEffects().playFlameSound();
        animation = generateAnimation(screen.getAtlas().findRegion("explosion"),
                44, 96, 96, 0.02f);
        setBounds(getX(), getY(), 96 / AdventureGame.PPM, 96 / AdventureGame.PPM);
    }
}
