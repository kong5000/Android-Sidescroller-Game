package com.mygdx.adventuregame.sprites;

import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;

public class EnemyDestroyedEffect extends Explosion {
    public EnemyDestroyedEffect(PlayScreen screen, float x, float y) {
        super(screen, x, y);
    }

    @Override
    protected void initializeAnimation() {
        animation = generateAnimation(screen.getAtlas().findRegion("ice_shatter"),
                44, 96, 96, 0.02f);
        setBounds(getX(), getY(), 96 / AdventureGame.PPM, 96 / AdventureGame.PPM);
    }
}
