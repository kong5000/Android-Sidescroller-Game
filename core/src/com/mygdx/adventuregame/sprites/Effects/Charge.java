package com.mygdx.adventuregame.sprites.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.player.Player;

public class Charge extends Effects {
    private static final float CHARGE_SCALE = 1.15f;
    private static final int WIDTH = 52;
    public static final int HEIGHT = 54;
    public static final float TIME_PER_FRAME_CHARGING = 0.017f;
    public static final float TIME_PER_FRAME_CHARGED = 0.0125f;
    private Animation<TextureRegion> fullyChargedAnimation;
    private float rotation = 0;
    private BlueFlame blueFlame;
    private boolean fullyCharged = false;
    private Player player;

    public Charge(PlayScreen screen, float x, float y, Player player) {
        super(screen, x, y);
        this.player = player;
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(x, y);
        initializeFullyChargedAnimation();
    }

    @Override
    protected void initializeAnimation() {
        TextureRegion region = screen.getAtlas().findRegion("charge");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i <12; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * WIDTH,
                        j * HEIGHT,
                        WIDTH,
                        HEIGHT
                ));
            }
        }
        animation = new Animation(TIME_PER_FRAME_CHARGING, frames, Animation.PlayMode.NORMAL);
        setBounds(getX(), getY(), WIDTH / AdventureGame.PPM, HEIGHT / AdventureGame.PPM);
        setScale(CHARGE_SCALE);
    }

    private void initializeFullyChargedAnimation(){
        TextureRegion region = screen.getAtlas().findRegion("blue_flame");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 9; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * 9,
                        j * 24,
                        9,
                        24
                ));
            }
        }
       fullyChargedAnimation = new Animation(TIME_PER_FRAME_CHARGED, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float dt) {
        if(animation.isAnimationFinished(dt + 0.08f)){
            if(!fullyCharged){
                fullyCharged = true;
                setBounds(getX(), getY(), 20 / AdventureGame.PPM, 40 / AdventureGame.PPM);
                setScale(1f);
            }
            setPosition(player.getXCoord() + 0.2f, player.getYCoord() + 0.12f);
            setRegion(fullyChargedAnimation.getKeyFrame(dt));

        }else {
            setPosition(player.getXCoord(), player.getYCoord() -0.05f);
            setRegion(animation.getKeyFrame(dt));

        }
    }

    public boolean isFullyCharged() {
        return fullyCharged;
    }
    public void reset(){
        fullyCharged = false;
        setBounds(getX(), getY(), WIDTH / AdventureGame.PPM, HEIGHT / AdventureGame.PPM);

        setScale(CHARGE_SCALE);
    }
}
