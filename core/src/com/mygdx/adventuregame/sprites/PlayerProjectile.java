package com.mygdx.adventuregame.sprites;

public interface PlayerProjectile {
    int getDamage();
    void setToDestroy();
    void setToDestroyHitBox();
    void targetHit();
}
