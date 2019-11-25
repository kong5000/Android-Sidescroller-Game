package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class MagicShield extends Explosion {
    private Player player;
    private float rotation = 1;

    public MagicShield(PlayScreen screen, float x, float y, Player player) {
        super(screen, x, y);
        this.player = player;
        rotation = 0;
        setOrigin(getWidth() / 2, getHeight() / 2);

    }

    @Override
    public void update(float dt) {
        setRegion(animation.getKeyFrame(stateTimer));
        stateTimer += dt;
        setPosition(player.b2body.getPosition().x - getWidth() / 2, player.b2body.getPosition().y - getHeight() / 2 - 0.06f);
//        rotation += 3f;
//        setRotation(rotation);
    }

    @Override
    protected void initializeAnimation() {
//        animation = generateAnimation(screen.getAtlas().findRegion("magic_shield"),
//                49, 86, 86, 0.01f);
//        setBounds(getX(), getY(), 86 / AdventureGame.PPM, 86 / AdventureGame.PPM);
//        setScale(0.4f);
//        animation.setPlayMode(Animation.PlayMode.LOOP);
        TextureRegion region = screen.getAtlas().findRegion("grass_shield");

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 10; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * 100,
                        0,
                        100,
                        100
                ));
            }
        }
        animation = new Animation(0.05f, frames, Animation.PlayMode.LOOP);
//        animation = generateAnimation(screen.getAtlas().findRegion("grass_shield"),
//                91, 100, 100, 0.01f);
        setBounds(getX(), getY(), 100 / AdventureGame.PPM, 100 / AdventureGame.PPM);
        setScale(0.6f);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    protected Animation<TextureRegion> generateAnimation(
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
}
