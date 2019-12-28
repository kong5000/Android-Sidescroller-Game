package com.mygdx.adventuregame.sprites.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerAnimations {
    private static final float FLASH_RED_TIME = 1f;

    private TextureRegion playerStand;
    private Animation<TextureRegion> playerCrouchWalk;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerDownAttack;
    private Animation<TextureRegion> playerRunDamaged;
    private Animation<TextureRegion> playerIdle;
    private Animation<TextureRegion> playerFall;
    private Animation<TextureRegion> playerFallDamaged;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerJumpDamaged;
    private Animation<TextureRegion> playerHurt;
    private Animation<TextureRegion> playerHurtDamaged;
    private Animation<TextureRegion> playerDie;
    private Animation<TextureRegion> playerRevive;
    private Animation<TextureRegion> playerAttack;
    private Animation<TextureRegion> playerAttackDamaged;
    private Animation<TextureRegion> playerAttack2;
    private Animation<TextureRegion> playerAttack2Damaged;
    private Animation<TextureRegion> playerAttack3;
    private Animation<TextureRegion> playerAttack3Damaged;
    private Animation<TextureRegion> playerAirAttack;
    private Animation<TextureRegion> playerFlip;
    private Animation<TextureRegion> playerFlipDamaged;
    private Animation<TextureRegion> playerCast;
    private Animation<TextureRegion> playerCastDamaged;
    private Animation<TextureRegion> playerDodge;
    private Animation<TextureRegion> playerDodgeDamaged;
    private Animation<TextureRegion> playerBow;
    private Animation<TextureRegion> playerBowDamaged;
    private Animation<TextureRegion> playerCrouch;
    private Animation<TextureRegion> playerWallClimb;
    private Animation<TextureRegion> playerThrow1;
    private Animation<TextureRegion> playerChargeBow;
    private Animation<TextureRegion> playerChargeBowAir;
    private Animation<TextureRegion> playerFireBow;
    private TextureRegion playerGotItem;

    private int flashCount = 0;
    private boolean flashFrame = true;
    protected float flashRedTimer = -1f;

    private TextureAtlas textureAtlas;
    private Player player;
    public PlayerAnimations(TextureAtlas textureAtlas, Player player){
        this.textureAtlas = textureAtlas;
        this.player = player;
        initializeAnimations();
    }
    private void initializeAnimations(){
        playerDownAttack = generateAnimation(textureAtlas.findRegion("player_fall_attack"), 2, 52, 39, 0.1f);
        playerCrouchWalk = generateAnimation(textureAtlas.findRegion("player_crouch_walk"), 6, 52, 39, 0.1f);
        playerRun = generateAnimation(textureAtlas.findRegion("player_run"), 6, 52, 39, 0.1f);
        playerRun.setPlayMode(Animation.PlayMode.LOOP);
        playerRunDamaged = generateAnimation(textureAtlas.findRegion("player_run_bright"), 6, 52, 39, 0.1f);
        playerRunDamaged.setPlayMode(Animation.PlayMode.LOOP);
        playerIdle = generateAnimation(textureAtlas.findRegion("player_idle1"), 3, 52, 39, 0.2f);
        playerFall = generateAnimation(textureAtlas.findRegion("player_fall"), 2, 52, 39, 0.1f);
        playerFallDamaged = generateAnimation(textureAtlas.findRegion("player_fall_bright"), 2, 52, 39, 0.1f);
        playerJump = generateAnimation(textureAtlas.findRegion("player_jump"), 4, 52, 39, 0.1f);
        playerJumpDamaged = generateAnimation(textureAtlas.findRegion("player_jump_bright"), 4, 52, 39, 0.1f);
        playerHurt = generateAnimation(textureAtlas.findRegion("player_hurt"), 3, 52, 39, 0.1f);
        playerHurtDamaged = generateAnimation(textureAtlas.findRegion("player_hurt_bright"), 3, 52, 39, 0.1f);
        playerDie = generateAnimation(textureAtlas.findRegion("player_die"), 7, 52, 39, 0.3f);
        playerRevive = generateAnimation(textureAtlas.findRegion("player_revive"), 7, 50, 37, 0.3f);
        playerThrow1 = generateAnimation(textureAtlas.findRegion("player_throw"), 4, 52, 39, 0.07f);
        playerAttack = generateAnimation(textureAtlas.findRegion("player_attack1"), 5, 52, 39, 0.07f);
        playerAttackDamaged = generateAnimation(textureAtlas.findRegion("player_attack1_bright"), 5, 52, 39, 0.07f);
        playerAttack2 = generateAnimation(textureAtlas.findRegion("player_attack2"), 6, 50, 37, 0.0575f);
        playerAttack2Damaged = generateAnimation(textureAtlas.findRegion("player_attack2_bright"), 6, 50, 37, 0.0575f);
        playerAttack3 = generateAnimation(textureAtlas.findRegion("player_attack3"), 6, 50, 37, 0.0575f);
        playerAttack3Damaged = generateAnimation(textureAtlas.findRegion("player_attack3_bright"), 6, 50, 37, 0.0575f);
        playerAirAttack = generateAnimation(textureAtlas.findRegion("player_air_attack1"), 4, 52, 39, 0.125f);
        playerFlip = generateAnimation(textureAtlas.findRegion("player_flip"), 4, 52, 39, 0.1f);
        playerFlipDamaged = generateAnimation(textureAtlas.findRegion("player_flip_bright"), 4, 52, 39, 0.1f);
        playerCast = generateAnimation(textureAtlas.findRegion("player_cast"), 4, 52, 39, 0.1f);
        playerCast.setPlayMode(Animation.PlayMode.LOOP);
        playerCastDamaged = generateAnimation(textureAtlas.findRegion("player_cast_bright"), 4, 52, 39, 0.1f);
        playerCastDamaged.setPlayMode(Animation.PlayMode.LOOP);
        playerDodge = generateAnimation(textureAtlas.findRegion("player_dodge"), 5, 50, 37, 0.07f);
        playerDodgeDamaged = generateAnimation(textureAtlas.findRegion("player_dodge_bright"), 5, 50, 37, 0.07f);
        playerCrouch = generateAnimation(textureAtlas.findRegion("player_crouch"), 4, 50, 37, 0.1f);
        playerBow = generateAnimation(textureAtlas.findRegion("player_bow"), 5, 50, 37, 0.1f);
        playerChargeBow = generateAnimation(textureAtlas.findRegion("player_charge_bow"), 3, 50, 37, 0.1f);
        playerChargeBowAir = generateAnimation(textureAtlas.findRegion("player_charge_bow_air"), 2, 50, 37, 0.1f);
        playerFireBow = generateAnimation(textureAtlas.findRegion("player_fire_bow"), 2, 50, 37, 0.1f);
        playerBowDamaged = generateAnimation(textureAtlas.findRegion("player_bow_bright"), 5, 50, 37, 0.1f);
        playerGotItem = new TextureRegion(textureAtlas.findRegion("player_got_item"), 0, 0, 50, 37);
        playerWallClimb = generateAnimation(textureAtlas.findRegion("player_wall_climb"), 6, 52, 39, 0.06f);
        playerWallClimb.setPlayMode(Animation.PlayMode.LOOP);
    }

    public TextureRegion getFrame(float dt){
        if (flashRedTimer > 0) {
            flashRedTimer -= dt;
        }
        Player.State currentState = player.getCurrentState();
        float stateTimer = player.getStateTimer();

        TextureRegion region;
        switch (currentState) {
            case DOWN_ATTACK:
                region = playerDownAttack.getKeyFrame(stateTimer, true);
                break;
            case CROUCH_WALKING:
                region = playerCrouchWalk.getKeyFrame(stateTimer, true);
                break;
            case CHARGING_BOW:
                region = playerChargeBow.getKeyFrame(stateTimer, true);
                break;
            case WALLCLIMB:
                region = playerWallClimb.getKeyFrame(stateTimer);
                break;
            case REVIVING:
                region = playerRevive.getKeyFrame(stateTimer);
                break;
            case DYING:
                region = playerDie.getKeyFrame(stateTimer);
                break;
            case PICKUP:
                region = playerGotItem;
                break;
            case SHOOTING:
//                region = selectBrightFrameOrRegularFrame(playerBow, playerBowDamaged);
                region = playerFireBow.getKeyFrame(stateTimer);
                break;
            case CROUCHING:
                region = playerCrouch.getKeyFrame(stateTimer);
                break;
            case DODGING:
                region = selectBrightFrameOrRegularFrame(playerDodge, playerDodgeDamaged, stateTimer);
                break;
            case CASTING:
                region = selectBrightFrameOrRegularFrame(playerCast, playerCastDamaged, stateTimer);
//                region = playerChargeBow.getKeyFrame(stateTimer, true);
                break;
            case HURT:
                region = selectBrightFrameOrRegularFrame(playerHurt, playerHurtDamaged, stateTimer);
                break;
            case ATTACKING:
                if (player.attackNumber == 0) {
                    region = selectBrightFrameOrRegularFrame(playerAttack, playerAttackDamaged, stateTimer);
                } else if (player.attackNumber == 1) {
                    region = selectBrightFrameOrRegularFrame(playerAttack2, playerAttack2Damaged, stateTimer);
                } else {
                    region = selectBrightFrameOrRegularFrame(playerAttack3, playerAttack3Damaged, stateTimer);
                }

                break;
            case AIR_ATTACKING:
                region = playerAirAttack.getKeyFrame(stateTimer);
                break;
            case FLIPPING:
                region = selectBrightFrameOrRegularFrame(playerFlip, playerFlipDamaged, stateTimer);
                break;
            case JUMPING:
                region = selectBrightFrameOrRegularFrame(playerJump, playerJumpDamaged, stateTimer);
                break;
            case RUNNING:
                region = selectBrightFrameOrRegularFrame(playerRun, playerRunDamaged, stateTimer);
                break;
            case FALLING:
                region = selectBrightFrameOrRegularFrame(playerFall, playerFallDamaged, stateTimer);
                break;
            case STANDING:
            default:
                region = playerIdle.getKeyFrame(stateTimer, true);
                break;
        }

        if ((!player.runningRight) && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.runningRight && region.isFlipX()) {
            region.flip(true, false);
        }

        return region;
    }
    private TextureRegion selectBrightFrameOrRegularFrame(Animation<TextureRegion> animation, Animation<TextureRegion> brightAnimation, float stateTimer) {
        TextureRegion textureRegion;
        if (flashRedTimer > 0) {
            if (flashFrame) {
                flashCount++;
                if (flashCount > 4) {
                    flashFrame = false;
                    flashCount = 0;
                }
                textureRegion = brightAnimation.getKeyFrame(stateTimer);
            } else {
                flashCount++;
                if (flashCount > 4) {
                    flashFrame = true;
                    flashCount = 0;
                }
                textureRegion = animation.getKeyFrame(stateTimer);
            }
        } else {
            textureRegion = animation.getKeyFrame(stateTimer);
        }
        return textureRegion;
    }
    private Animation<TextureRegion> generateAnimation(
            TextureRegion textureRegion,
            int numberOfFrames,
            int widthInPixels,
            int heightInPixels,
            float secondsPerFrame) {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < numberOfFrames; i++) {
            frames.add(new TextureRegion(
                    textureRegion,
                    i * widthInPixels,
                    0,
                    widthInPixels,
                    heightInPixels
            ));
        }
        Animation<TextureRegion> animation = new Animation<TextureRegion>(secondsPerFrame, frames);
        return animation;
    }

    public void flashPlayerSprite(){
        if (flashRedTimer < 0) {
            flashRedTimer = FLASH_RED_TIME;
        }
    }

    public void pauseChargingAnimation(){
        playerChargeBow.setFrameDuration(10f);
    }
    public void restartChargingAnimation(){
        playerChargeBow.setFrameDuration(0.1f);
    }

    public boolean reviveAnimationDone(float stateTimer){
       return playerRevive.isAnimationFinished(stateTimer);
    }

    public boolean attackAnimationFinished(int attackNumber, float stateTimer){
        if(attackNumber == 1){
            return playerAttack.isAnimationFinished(stateTimer);
        }else if(attackNumber == 2){
            return playerAttack2.isAnimationFinished(stateTimer);
        }else {
            return playerAttack3.isAnimationFinished(stateTimer);
        }
    }
}
