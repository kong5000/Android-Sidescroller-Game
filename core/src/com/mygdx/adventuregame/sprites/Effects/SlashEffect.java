package com.mygdx.adventuregame.sprites.Effects;

import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.UpdatableSprite;

public class SlashEffect extends Effects implements UpdatableSprite {
    public SlashEffect(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setScale(0.75f);
    }

    @Override
    protected void initializeAnimation() {
        animation = generateAnimation(screen.getAtlas().findRegion("slash_attack"),
                6, 125, 150, 0.05f);
        setBounds(getX(), getY(), 96 / AdventureGame.PPM, 96 / AdventureGame.PPM);
    }
}