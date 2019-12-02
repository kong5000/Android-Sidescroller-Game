package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class Explosion extends Effects implements UpdatableSprite{
    public Explosion(PlayScreen screen, float x, float y) {
        super(screen, x, y);
    }

    @Override
    protected void initializeAnimation() {
        animation = generateAnimation(screen.getAtlas().findRegion("explosion"),
                44, 96, 96, 0.02f);
        setBounds(getX(), getY(), 96 / AdventureGame.PPM, 96 / AdventureGame.PPM);
    }
}
