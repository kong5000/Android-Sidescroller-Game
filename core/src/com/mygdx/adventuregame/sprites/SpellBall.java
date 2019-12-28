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

public class SpellBall extends Sprite implements UpdatableSprite, EnemyProjectile, PlayerProjectile  {
    private boolean setToDestroyHitBox = false;

    private enum State{ARMED, IMPACT}
    private static final float FULL_CHARGE = 2.55f;
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
    private static final int WIDTH_PIXELS = 100;
    private static final int HEIGHT_PIXELS = 100;
    private boolean isFriendly;
    private float rotation =0f;
    private float charge = 0f;
    private boolean goingRight;
    private static final float MAX_CHARGE = 1.2f;
    private float existTimer = 0;
    private boolean hitBoxDestroyed = false;

    private Animation<TextureRegion> projectile;
    public SpellBall(PlayScreen screen, float x, float y, boolean goingRight, boolean isFriendly, float charge){
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

        TextureRegion region = screen.getAtlas().findRegion("fire_ball");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                frames.add(new TextureRegion(
                        region,
                        i * 100,
                        0,
                        100,
                        100
                ));
            }
        }
        projectileAnimation = new Animation(0.02f, frames, Animation.PlayMode.LOOP);

        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setGoingRight(goingRight);
        setOrigin(getWidth() / 2, getHeight() / 2);
        if(charge >= MAX_CHARGE){
            setScale(0.85f);
        }else if(charge >= MAX_CHARGE / 2){
            setScale(0.5f);
        }else {
            setScale(0.25f);
        }


    }

    public void update(float dt){
        aliveTimer -= dt;
        if(!setToDestroy){
            stateTimer += dt;
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(projectileAnimation.getKeyFrame(stateTimer));
        }

        if(aliveTimer < 0 && !destroyed){
            setToDestroy();
        }

        if(setToDestroy && !destroyed) {
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
        float speed = 2f;

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
        b2body.setGravityScale(0);
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
        if(charge >= MAX_CHARGE){
            return  9;
        }else {
            return(int)( charge * 2f) + 2;
        }
    }
}


