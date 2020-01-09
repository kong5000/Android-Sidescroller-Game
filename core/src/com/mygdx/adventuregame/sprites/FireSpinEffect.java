package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Effects;
import com.mygdx.adventuregame.sprites.Enemy;

public class FireSpinEffect extends Effects {
    private Enemy enemy;
    public FireSpinEffect(PlayScreen screen, float x, float y, Enemy enemy) {
        super(screen, x, y);
        this.enemy = enemy;
    }

    @Override
    public void update(float dt) {
        if(enemy.setToDestroy){
            setToDestroy();
        }
        stateTimer += dt;
        setPosition(enemy.b2body.getPosition().x - getWidth() / 2 - 0.5f, enemy.b2body.getPosition().y - getHeight() / 2 - 0.5f);
        setRegion(animation.getKeyFrame(stateTimer));
    }

    @Override
    protected void initializeAnimation() {
        animation = generateAnimation(screen.getAtlas().findRegion("fire_spin"),
                61, 100, 100, 0.015f);
        setBounds(getX(), getY(), 100 / AdventureGame.PPM, 100 / AdventureGame.PPM);
        setScale(2f);
    }

    @Override
    protected Animation<TextureRegion> generateAnimation(TextureRegion textureRegion, int numberOfFrames, int widthInPixels, int heightInPixels, float secondsPerFrame) {
        TextureRegion region = screen.getAtlas().findRegion("fire_spin");
        int index = 0;
        Array<TextureRegion> frames = new Array<TextureRegion>();
            for (int j = 0; j < 8; j++) {
                for (int i = 0; i < 8; i++) {
                    index++;
                    if(index < numberOfFrames){


                    frames.add(new TextureRegion(
                            region,
                            i * 100,
                            j * 100,
                            100,
                            100
                    ));
                    }
                }
            }


        return  new Animation(secondsPerFrame, frames, Animation.PlayMode.LOOP);
    }
    @Override
    public void dispose() {
        setToDestroy();
    }
}
