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

public class FireBall extends Sprite {
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
    private static final float TIME_ALIVE = 200f;
    private static final int WIDTH_PIXELS = 32;
    private static final int HEIGHT_PIXELS = 16;
    private boolean isFriendly;

    private Animation<TextureRegion> projectile;
    public FireBall(PlayScreen screen, float x, float y, boolean goingRight, boolean isFriendly){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        this.isFriendly = isFriendly;
        defineProjectile();
        attackEnabled = false;
        aliveTimer = TIME_ALIVE;
        stateTimer = 0;
        projectileAnimation = generateAnimation(screen.getAtlas().findRegion("projectile_animation")
        ,4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setGoingRight(goingRight);
        if(isFriendly){
            setScale(1.25f);
        }
    }

    public void update(float dt){
        aliveTimer -= dt;
        setRegion(getFrame(dt));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
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

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion texture;
        switch (currentState) {
            case ARMED:
                attackEnabled = true;
                texture = projectileAnimation.getKeyFrame(stateTimer, true);
                break;
            case IMPACT:
            default:
                attackEnabled = false;
                texture = projectileAnimation.getKeyFrame(stateTimer, true);
                break;
        }
        flipFramesIfNeeded(texture);

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return texture;
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
            speed *= 2.5f;
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
        shape.setRadius(8 / AdventureGame.PPM);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        b2body.setGravityScale(0);
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
}
