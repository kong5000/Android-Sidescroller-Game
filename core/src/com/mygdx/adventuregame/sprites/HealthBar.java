package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class HealthBar extends Sprite {
    public boolean safeToRemove = false;
    protected float stateTimer;
    private Sprite greenBar;
    private Enemy enemy;
    public HealthBar(PlayScreen screen, float x, float y, int health, Enemy enemy){
        setPosition(x, y);
        setRegion(screen.getAtlas().findRegion("red_bar"));
        setBounds(getX(), getY(), 20 * enemy.getHealth() / AdventureGame.PPM, 100 / AdventureGame.PPM);
        setScale(0.1f);
        greenBar = new Sprite();
        greenBar.setPosition(getX(),getY());
        greenBar.setRegion(screen.getAtlas().findRegion("bar_border"));
        greenBar.setBounds(getX(), getY(), 20 * enemy.getHealth()/ AdventureGame.PPM, 100 / AdventureGame.PPM);
        greenBar.setScale(0.1f);
        greenBar.setAlpha(0);
        setAlpha(0);
        this.enemy = enemy;
    }

    public void update(float dt){
        if(enemy.isAlive()){
            if(enemy.showHealthBar){
                greenBar.setAlpha(1);
                setAlpha(1);
                greenBar.setPosition(enemy.b2body.getPosition().x, enemy.b2body.getPosition().y);
                setPosition(enemy.b2body.getPosition().x, enemy.b2body.getPosition().y);
                setBounds(getX(), getY(),enemy.getHealth() * 20 / AdventureGame.PPM, 100 / AdventureGame.PPM);
            }
        }else {
            safeToRemove = true;
        }

    }

    @Override
    public void draw(Batch batch) {
        greenBar.draw(batch);
        super.draw(batch);

    }

}