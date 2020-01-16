package com.mygdx.adventuregame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameAssets {
    private AssetManager assetManager;
    public GameAssets(){
        assetManager = new AssetManager();
        assetManager.load("game_sprites.pack", TextureAtlas.class);
        assetManager.load("audio/Boss_Battle.wav", Music.class);
        assetManager.load("audio/flame.ogg", Sound.class);
        assetManager.load("audio/swish2.ogg", Sound.class);
        assetManager.load("audio/parry.ogg", Sound.class);


        assetManager.finishLoading();
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }

}
