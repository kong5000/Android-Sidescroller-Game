package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class DamageNumber extends Sprite {
    public boolean safeToRemove = false;
    protected float stateTimer;
    protected PlayScreen screen;
    private float yposIncrement;
    protected Animation<TextureRegion> animation;
    private float alpha;
    private boolean isPlayerDamage;
    private float existenceTimer;
    public DamageNumber(PlayScreen screen, float x, float y, boolean isPlayerDamage, int damage) {
        this.screen = screen;
        setPosition(x, y);
        setRegion(screen.getAtlas().findRegion(Integer.toString(damage)));
        setRegion(screen.getAtlas().findRegion(Integer.toString(damage)));
        setBounds(getX(), getY(), 75 / AdventureGame.PPM, 75 / AdventureGame.PPM);
        setScale(0.11f);
        if(damage > 3){
            setScale(0.14f);
        }
        if(damage > 7){
            setScale(0.16f);
        }
        yposIncrement = 0.5f;
        alpha = 1f;
        this.isPlayerDamage = isPlayerDamage;
        existenceTimer = 0.75f;
    }

    public void update(float dt) {
        setPosition(getX(), getY() + yposIncrement / AdventureGame.PPM);
        alpha -= 0.01f;
        setAlpha(alpha);
        existenceTimer -=dt;
        if(existenceTimer < 0){
            safeToRemove = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!safeToRemove) {
            super.draw(batch);
        }
    }

    public boolean isForPlayer(){
        return isPlayerDamage;
    }
}
