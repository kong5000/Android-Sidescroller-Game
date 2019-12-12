package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;

public class Arrow extends Sprite implements UpdatableSprite, EnemyProjectile  {
    private boolean setToDestroyHitBox = false;

    private enum State{ARMED, IMPACT}
    private State currentState = State.ARMED;
    private State previousState = State.ARMED;
    private World world;
    private PlayScreen screen;
    public Body b2body;
    public boolean attackEnabled;
    protected boolean destroyed;
    public boolean safeToRemove = false;
    private boolean setToDestroy = false;
    private Animation<TextureRegion> projectileAnimation;
    private float aliveTimer;
    private float stateTimer;
    private static final float TIME_ALIVE = 5f;
    private static final int WIDTH_PIXELS = 15;
    private static final int HEIGHT_PIXELS = 15;
    private boolean isFriendly;
    private float rotation =0f;
    private float charge = 0f;
    private boolean goingRight;
    private static final float MAX_CHARGE = 1.2f;
    private float existTimer = 0;
    private boolean hitBoxDestroyed = false;

    private Animation<TextureRegion> projectile;
    public Arrow(PlayScreen screen, float x, float y, boolean goingRight, boolean isFriendly, float charge){
        this.goingRight = goingRight;
        this.world = screen.getWorld();
        this.screen = screen;
        this.charge = charge;
        setPosition(x, y);
        this.isFriendly = isFriendly;
        defineProjectile();
        attackEnabled = false;
        aliveTimer = TIME_ALIVE;
        stateTimer = 0;
        setRegion(new TextureRegion(screen.getAtlas().findRegion("arrow"), 0, 0, 16, 16));
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setGoingRight(goingRight);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(0.75f);
        if(goingRight){
            rotation = -135;
        }else {
            rotation = 45;
        }

    }

    public void update(float dt){
        if(!hitBoxDestroyed){
            setRotation(rotation);
            if(goingRight){
                rotation -= 0.25;
            }else {
                rotation += 0.25;
            }

        }

        aliveTimer -= dt;
        if(!hitBoxDestroyed){
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        }
        if( setToDestroyHitBox && !hitBoxDestroyed && !destroyed) {
            hitBoxDestroyed = true;
            world.destroyBody(b2body);
        }

        if(aliveTimer < 0 && !destroyed){
            destroyed = true;
        }

        if((aliveTimer < 0 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }

    }

    @Override
    public void draw(Batch batch){
        if(!destroyed){
            super.draw(batch);
        }else{
            safeToRemove = true;
        }
    }


    private void flipFramesIfNeeded(TextureRegion texture){
        if (b2body.getLinearVelocity().x < 0 && texture.isFlipX()) {
            texture.flip(true, false);
        }
        if (b2body.getLinearVelocity().x > 0  && !texture.isFlipX()) {
            texture.flip(true, false);
        }
    }

    public void setGoingRight(boolean status){
        float speed = 1f;
        if(isFriendly){
            speed = charge *3.5f + 3f;
        }
        if(status){
            b2body.setLinearVelocity(new Vector2(speed, 0));
        }else{
            b2body.setLinearVelocity(new Vector2(-speed,0));
        }
    }


    private State getState(){
        if (setToDestroy) {
            return State.IMPACT;
        }
        else {
            return State.ARMED;
        }
    }


    private void defineProjectile() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        if(isFriendly){
            fixtureDef.filter.categoryBits = AdventureGame.PLAYER_PROJECTILE_BIT;
            fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.ENEMY_BIT;
        }else{
            fixtureDef.filter.categoryBits = AdventureGame.ENEMY_PROJECTILE_BIT;
            fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.PLAYER_BIT;
        }

        CircleShape shape = new CircleShape();
        shape.setRadius(4 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        if(charge > MAX_CHARGE){
            fixtureDef.density = 75f;
        }else {
            fixtureDef.density = charge * 5f;
        }

        b2body.createFixture(fixtureDef).setUserData(this);
        b2body.setGravityScale(0.12f);
    }

    public void setToDestroyHitBox(){
        setToDestroyHitBox = true;
    }
    public void setToDestroy(){
        setToDestroy = true;
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

    public void explode(){
        screen.getExplosions().add(new Explosion(screen, getX() - getWidth() / 2
                ,getY() - getHeight() / 2 -0.05f));
    }

    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

    public int getDamage(){
        return (int)( charge * 8.5f) + 2;
    }
}
