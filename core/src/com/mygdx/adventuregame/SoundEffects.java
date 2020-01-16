package com.mygdx.adventuregame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundEffects {
    private AssetManager assetManager;
    private float effectsVolume;
    private float musicVolume;

    private Sound flameSound;
    private Sound swordSound;
    private Sound parrySound;
    private Sound shadeAttackSound;
    private Sound slugAttackSound;
    private Sound ogreRoarSound;
    private Sound slashSound;
    private Sound minotaurDieSound;
    private Sound explodeSound;
    private Sound thudSound;
    private Sound bigExplosionSound;
    private Sound enemyMeleeSound;
    private Sound coinSound;
    private Sound impAttackSound;
    private Sound arrowImpactSound;
    private Sound arrowPickupSound;
    private Sound heavyStepSound;



    private Music templeMusic;
    private Music bossMusic;
    private Music currentTrack;

    public SoundEffects(AssetManager assetManager){
        this.assetManager = assetManager;
        effectsVolume = 1f;
        musicVolume = 1f;
        flameSound  = assetManager.get("audio/flame.ogg", Sound.class);
        swordSound = assetManager.get("audio/swish2.ogg", Sound.class);
        parrySound = assetManager.get("audio/parry.ogg", Sound.class);
        shadeAttackSound = assetManager.get("audio/shade_attack.ogg", Sound.class);
        slugAttackSound = assetManager.get("audio/slug_attack.ogg", Sound.class);
        ogreRoarSound = assetManager.get("audio/ogre_roar.ogg", Sound.class);
        slashSound = assetManager.get("audio/slash.ogg", Sound.class);
        minotaurDieSound = assetManager.get("audio/minotaur_die.ogg", Sound.class);
        explodeSound = assetManager.get("audio/explosion.wav", Sound.class);
        thudSound = assetManager.get("audio/thud.ogg", Sound.class);
        bigExplosionSound = assetManager.get("audio/big_explosion.wav", Sound.class);
        enemyMeleeSound = assetManager.get("audio/swish1.ogg", Sound.class);
        coinSound = assetManager.get("audio/coin.ogg", Sound.class);
        impAttackSound = assetManager.get("audio/imp_attack.ogg", Sound.class);
        arrowImpactSound = assetManager.get("audio/arrow_hit.ogg", Sound.class);
        arrowPickupSound = assetManager.get("audio/arrow_pickup.ogg", Sound.class);
        heavyStepSound = assetManager.get("audio/heavy_step.ogg", Sound.class);

        templeMusic = assetManager.get("audio/Junkyard_Drive.ogg", Music.class);
        initializeMusic(templeMusic);

        bossMusic = assetManager.get("audio/Boss_Battle.wav", Music.class);
        initializeMusic(bossMusic);


    }
    private void initializeMusic(Music music){
        music.setVolume(effectsVolume);
        music.setLooping(true);
    }
    public void setEffectsVolume(float loudness){
        effectsVolume = loudness;
    }
    public void setMusicVolume(float loudness){ musicVolume = loudness; }

    private void stopMusic(){
        if(currentTrack != null){
            currentTrack.stop();
        }
    }

    public void playTempleMusic(){
        stopMusic();
        currentTrack = templeMusic;
        templeMusic.setVolume(0.5f);
        templeMusic.play();
    }
    public void playBossMusic(){
        stopMusic();
        currentTrack = bossMusic;
        currentTrack.play();
    }
    public void playFlameSound(){
        flameSound.play(effectsVolume * 0.5f);
    }
    public void playSwordSound(){
        swordSound.play(effectsVolume * 0.5f);
    }
    public void playParrySound(){
        parrySound.play(effectsVolume *0.5f);
    }
    public void playShadeAttackSound(){
        shadeAttackSound.play(effectsVolume *1f);
    }
    public void playSlugAttackSound(){
        slugAttackSound.play(effectsVolume *0.85f);
    }
    public void playOgreRoarSound(){
        ogreRoarSound.play(effectsVolume *1f);
    }
    public void playSlashSound(){
        slashSound.play(effectsVolume *0.3f);
    }
    public void playminotaurDieSound(){ minotaurDieSound.play(effectsVolume *0.65f); }
    public void playExplosionSound(){ explodeSound.play(effectsVolume *0.25f); }
    public void playThudSound(){ thudSound.play(effectsVolume ); }
    public void playBigExplosion(){ bigExplosionSound.play(effectsVolume *0.5f); }
    public void playEnemyMeleeSound(){ enemyMeleeSound.play(effectsVolume *0.2f); }
    public void playImpAttackSound(){ impAttackSound.play(effectsVolume *0.2f); }
    public void playCoinSound(){ coinSound.play(effectsVolume *0.5f); }
    public void playArrowHitSound(){ arrowImpactSound.play(effectsVolume); }
    public void playArrowPickupSound(){ arrowPickupSound.play(effectsVolume *0.6f); }
    public void playHeavyStepSound(){ heavyStepSound.play(effectsVolume); }
}
