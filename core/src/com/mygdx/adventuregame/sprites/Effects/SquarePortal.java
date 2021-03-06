package com.mygdx.adventuregame.sprites.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class SquarePortal extends Effects {
    private static final int WIDTH = 52;
    public static final int HEIGHT = 52;
    public static final float TIME_PER_FRAME = 0.02f;
    public SquarePortal(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(x, y);
    }

    @Override
    protected void initializeAnimation() {
        TextureRegion region = screen.getAtlas().findRegion("square_portal");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i <7; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * WIDTH,
                        j * HEIGHT,
                        WIDTH,
                        HEIGHT
                ));
            }
        }
        animation = new Animation(TIME_PER_FRAME, frames, Animation.PlayMode.NORMAL);
        setBounds(getX(), getY(), WIDTH / AdventureGame.PPM, HEIGHT / AdventureGame.PPM);
        setScale(1.5f);
    }

}
