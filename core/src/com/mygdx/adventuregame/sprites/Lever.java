package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;

public class Lever extends Enemy implements UpdatableSprite {
    private static final float[] LEVER_HITBOX = {
            -0.08f, 0.08f,
            -0.08f, -0.1f,
            0.15f, -0.1f,
            0.15f, 0.08f};

    private float SWITCH_ACTIVE_TIME = 0.75f;
    private boolean canBeToggled = true;

    private enum State {OFF, ON}
    ;
    private State currentState;
    private State previousState;
    private boolean isClosed;
    private World world;
    private PlayScreen screen;
    public Body b2body;
    protected boolean destroyed;

    private float stateTimer;
    private static final int WIDTH_PIXELS = 32;
    private static final int HEIGHT_PIXELS = 32;

    private TextureRegion switchOpen;
    private TextureRegion switchClosed;

    private float canBeSwitchedTimer = -1f;


    public Lever(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setScale(1.25f);
        defineHitBox();
        stateTimer = 0;
        currentState = State.OFF;
        previousState = currentState;
        isClosed = true;
        switchOpen =  new TextureRegion(screen.getAtlas().findRegion("switch_left"),0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
        switchClosed = new TextureRegion(screen.getAtlas().findRegion("switch_right"),0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
    }

    public void update(float dt) {
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            safeToRemove = true;
        }else {
            setRegion(getFrame(dt));
            if(canBeSwitchedTimer > 0){
                canBeSwitchedTimer -= dt;
            }
        }

    }

    @Override
    protected Enemy.State getState() {
        return null;
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        } else {
            safeToRemove = true;
        }
    }


    protected TextureRegion getFrame(float dt) {
        currentState = getLeverState();
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        TextureRegion texture;
        if (currentState == State.OFF) {
            texture = switchClosed;
        } else {
            texture = switchOpen;
        }
        return texture;
    }

    public State getLeverState() {
        if (isClosed) {
            return State.OFF;
        } else {
            return State.ON;
        }
    }

    @Override
    protected void orientTextureTowardsPlayer(TextureRegion texture) {

    }


    private void defineHitBox() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.PLAYER_SWORD_BIT | AdventureGame.ARROW_BIT;

        PolygonShape shape = new PolygonShape();
        shape.set(LEVER_HITBOX);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() /2);
    }

    public void setToDestroy() {
        setToDestroy = true;
    }


    @Override
    protected void defineEnemy() {

    }

    @Override
    public void hitOnHead() {

    }

    @Override
    public void damage(int amount) {

        if(canBeSwitchedTimer < 0 && canBeToggled){
            canBeToggled = false;
            if(currentState == State.OFF){
                isClosed = false;
            }else {
                isClosed = true;
            }
        }
        if(canBeSwitchedTimer < 0){
            canBeSwitchedTimer = SWITCH_ACTIVE_TIME;
        }
    }

    @Override
    public boolean notDamagedRecently() {
        return true;
    }

    public void explode() {
        screen.getExplosions().add(new Explosion(screen, getX() - getWidth() / 2
                , getY() - getHeight() / 2 - 0.05f));
    }

    @Override
    protected Shape getHitBoxShape() {
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / AdventureGame.PPM);
        return shape;
    }

    public boolean leverOn(){
        return currentState == State.ON;
    }
    public void setOneTimeSwitch(boolean state){
        canBeToggled = state;
    }

    @Override
    public void dispose() {
        world.destroyBody(b2body);
    }
}
