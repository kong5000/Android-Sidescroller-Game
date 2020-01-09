package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface UpdatableSprite {
    public void update(float dt);
    public void draw(Batch batch);
    public boolean safeToRemove();
    public void dispose();
    public void setToDestroy();
    
}
