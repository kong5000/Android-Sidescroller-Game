package com.mygdx.adventuregame.sprites.Effects;

import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class IceShatter extends Effects{
    public IceShatter(PlayScreen screen, float x, float y) {
        super(screen, x, y);
    }

    @Override
    protected void initializeAnimation() {
        animation = generateAnimation(screen.getAtlas().findRegion("ice_shatter"),
                49, 96, 96, 0.02f);
        setBounds(getX(), getY(), 96 / AdventureGame.PPM, 96 / AdventureGame.PPM);
    }

}
