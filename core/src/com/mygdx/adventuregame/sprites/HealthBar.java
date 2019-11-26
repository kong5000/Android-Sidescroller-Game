package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class HealthBar extends Sprite implements UpdatableSprite{
    public boolean safeToRemove = false;
    protected float stateTimer;
    private Sprite greenBar;
    private Enemy enemy;
    public HealthBar(PlayScreen screen, float x, float y, Enemy enemy){
        setPosition(x, y);
        setRegion(screen.getAtlas().findRegion("red_bar"));
        setBounds(getX(), getY(), 20 * enemy.getHealth() / AdventureGame.PPM, 80 / AdventureGame.PPM);
        setScale(0.1f);
        greenBar = new Sprite();
        greenBar.setPosition(getX(),getY());
        greenBar.setRegion(screen.getAtlas().findRegion("bar_border"));
        greenBar.setBounds(getX(), getY(), 20 * enemy.getHealth()/ AdventureGame.PPM, 80 / AdventureGame.PPM);
        greenBar.setScale(0.1f);
        greenBar.setAlpha(0);
        setAlpha(0);
        this.enemy = enemy;
    }

    public void update(float dt){
        if(enemy.isAlive()){
            if(enemy.showHealthBar()){
                greenBar.setAlpha(1);
                setAlpha(1);
                greenBar.setPosition(enemy.b2body.getPosition().x + enemy.barXOffset, enemy.b2body.getPosition().y + enemy.barYOffset);
                setPosition(enemy.b2body.getPosition().x + enemy.barXOffset, enemy.b2body.getPosition().y + enemy.barYOffset);
                setBounds(getX(), getY(),enemy.getHealth() * 20 / AdventureGame.PPM, 80 / AdventureGame.PPM);
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

    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

}
