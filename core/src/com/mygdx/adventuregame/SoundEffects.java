package com.mygdx.adventuregame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.adventuregame.screens.PlayScreen;

public class SoundEffects {
    private AssetManager assetManager;
    private float volume;
    private Sound flameSound;
    private Sound swordSound;
    private Sound parrySound;
    private Sound shadeAttackSound;
    private Sound slugAttackSound;
    private Sound ogreRoarSound;
    private Sound slashSound;
    private Sound minotaurDieSound;



    private Music templeMusic;
    private Music bossMusic;
    private Music currentTrack;

    public SoundEffects(AssetManager assetManager){
        this.assetManager = assetManager;
        volume = 0.5f;
        flameSound  = assetManager.get("audio/flame.ogg", Sound.class);
        swordSound = assetManager.get("audio/swish2.ogg", Sound.class);
        parrySound = assetManager.get("audio/parry.ogg", Sound.class);
        shadeAttackSound = assetManager.get("audio/shade_attack.ogg", Sound.class);
        slugAttackSound = assetManager.get("audio/slug_attack.ogg", Sound.class);
        ogreRoarSound = assetManager.get("audio/ogre_roar.ogg", Sound.class);
        slashSound = assetManager.get("audio/slash.ogg", Sound.class);
        minotaurDieSound = assetManager.get("audio/minotaur_die.ogg", Sound.class);

        templeMusic = assetManager.get("audio/Junkyard_Drive.ogg", Music.class);
        initializeMusic(templeMusic);

        bossMusic = assetManager.get("audio/Boss_Battle.wav", Music.class);
        initializeMusic(bossMusic);


    }
    private void initializeMusic(Music music){
        music.setVolume(volume);
        music.setLooping(true);
    }
    public void setVolume(float loudness){
        volume = loudness;
    }
    private void stopMusic(){
        if(currentTrack != null){
            currentTrack.stop();
        }
    }

    public void playTempleMusic(){
        stopMusic();
        currentTrack = templeMusic;
        templeMusic.play();
    }
    public void playBossMusic(){
        stopMusic();
        currentTrack = bossMusic;
        currentTrack.play();
    }
    public void playFlameSound(){
        flameSound.play(volume);
    }
    public void playSwordSound(){
        swordSound.play(volume);
    }
    public void playParrySound(){
        parrySound.play(volume *0.85f);
    }
    public void playShadeAttackSound(){
        shadeAttackSound.play(volume *0.85f);
    }
    public void playSlugAttackSound(){
        slugAttackSound.play(volume *0.85f);
    }
    public void playOgreRoarSound(){
        ogreRoarSound.play(volume *0.85f);
    }
    public void playSlashSound(){
        slashSound.play(volume *0.65f);
    }
    public void playminotaurDieSound(){ minotaurDieSound.play(volume *0.65f); }
}
