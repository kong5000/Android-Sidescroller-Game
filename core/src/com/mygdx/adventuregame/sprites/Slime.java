package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class Slime extends Enemy {
    private static final float[] SLIME_HITBOX = {-0.15f, 0.02f, -0.15f, -0.1f, 0.15f, -0.1f, 0.15f, 0.02f};

    private static final float ATTACK_RATE = 3f;
    private static final float HURT_RATE = 0.3f;
    private static final float CORPSE_TIME = 0.25f;
    private float stateTimer;
    private float attackRateTimer;
    private float hurtTimer = -1f;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private boolean setToDestroy;

    private int health = 2;


    public Slime(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("slime_move"),
                4, 34, 27, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("slime_die"),
                4, 34, 27, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("slime_attack"),
                4, 34, 27, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("slime_hurt"),
                4, 34, 27, 0.07f);
        stateTimer = 0;
        setBounds(getX(), getY(), 34 / AdventureGame.PPM, 27 / AdventureGame.PPM);
        setToDestroy = false;
        destroyed = false;
        currentState = State.WALKING;
        previousState = State.WALKING;
        attackRateTimer = ATTACK_RATE;
    }

    @Override
    public void update(float dt){
        if(health <= 0){
            setToDestroy = true;
        }
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        }else if(!destroyed){
            setPosition(b2body.getPosition().x - getWidth()/ 2, b2body.getPosition().y - getHeight() / 2);
            if(hurtTimer > 0){
                hurtTimer -=dt;
            }
        }

        setRegion(getFrame(dt));

    }
    @Override
    public void draw(Batch batch){
        if(!destroyed || stateTimer < CORPSE_TIME){
            super.draw(batch);
        }else{
            safeToRemove = true;
        }

    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case DYING:
                attackEnabled = false;
                region = deathAnimation.getKeyFrame(stateTimer);
                break;
            case ATTACKING:
                if(attackAnimation.isAnimationFinished(stateTimer)){
                    region = walkAnimation.getKeyFrame(stateTimer, true);
                    attackEnabled = false;
                }else{
                    region = attackAnimation.getKeyFrame(stateTimer);
                    attackEnabled = true;
                }
                break;
            case HURT:
                attackEnabled = false;
                region = hurtAnimation.getKeyFrame(stateTimer);
                break;
            case WALKING:
            default:
                attackEnabled = false;
                region = walkAnimation.getKeyFrame(stateTimer, true);
                break;
        }

        Vector2 vectorToPlayer = getVectorToPlayer();
        if((vectorToPlayer.x < 0) && region.isFlipX()){
            region.flip(true, false);
        }
        if((vectorToPlayer.x > 0) && !region.isFlipX()){
            region.flip(true, false);
        }

        attackRateTimer -= dt;
        if(currentState == State.ATTACKING && attackRateTimer < 0){
            attackRateTimer = ATTACK_RATE;
            stateTimer = 0; //To reset attack animation (otherwise will just use walking animation during attacks after first attack)
            if(vectorToPlayer.x < 0){
                b2body.applyLinearImpulse(new Vector2(-1.5f, 1.5f), b2body.getWorldCenter(), true);
            }else{
                b2body.applyLinearImpulse(new Vector2(1.5f, 1.5f), b2body.getWorldCenter(), true);
            }
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if(setToDestroy){
            return State.DYING;
        }
        else if(hurtTimer > 0){
            return State.HURT;
        }
        else if(Math.abs(getVectorToPlayer().x) < 100 / AdventureGame.PPM){
            return State.ATTACKING;

        }
        else{
            return State.WALKING;
        }
    }

    @Override
    public void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.PLAYER_SWORD_BIT;
        PolygonShape shape = new PolygonShape();
        shape.set(SLIME_HITBOX);
//        CircleShape shape = new CircleShape();
//        shape.setRadius(8 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);

        fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_ATTACK_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;

        fixtureDef.shape = shape;
        fixtureDef.isSensor = false;
        b2body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(
                new Vector2(-8 / AdventureGame.PPM, 1 / AdventureGame.PPM),
                new Vector2(8 / AdventureGame.PPM, 1 / AdventureGame.PPM)
        );
        fixtureDef.shape = head;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_HEAD_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;
        fixtureDef.isSensor = false;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(){
        setToDestroy = true;
    }



    @Override
    public void hurt() {
        health -=2;
        if(currentState != State.HURT){
            if(hurtTimer < 0){
                hurtTimer = HURT_RATE;
            }
            if(getVectorToPlayer().x < 0){
                b2body.applyLinearImpulse(new Vector2(1f, 1f),b2body.getWorldCenter(),true);
            }else{
                b2body.applyLinearImpulse(new Vector2(-1f, 1f),b2body.getWorldCenter(),true);
            }

        }
    }

    @Override
    public boolean notHurt() {
        return (hurtTimer < 0);
    }
}
