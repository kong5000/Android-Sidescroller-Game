package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class Cage extends Sprite implements UpdatableSprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    private boolean active = false;

    protected float movementTimer = -1f;
    protected float[] BLOCK_SHAPE = {
            -0.23f, 0.15f,
            -0.23f, -0.3f,
            0.23f, -0.3f,
            0.23f, 0.15f};
    protected float[] BOTTOM_SPIKES = {
            -0.23f, 0.18f,
            -0.23f, -0.2f,
            0.23f, -0.2f,
            0.23f, 0.18f};
    private float[] SENSOR_SHAPE = {
            -0.05f, -0.35f,
            0.05f, -0.35f,
            0.05f, 0f,
            -0.05f, 0f
    };
    protected static int WIDTH_PIXELS = 48;
    protected static int HEIGHT_PIXELS = 64;
    private float direction = -1f;
    private float pauseTimer = -1f;
    private float upTimer = -1f;
    private float downTimer = 0f;
    private boolean paused = false;
    private float travelTime = 2f;
    private Lever lever;
    private boolean safeToRemove = false;
    private boolean setToDestroy = false;
    private boolean destroyed = false;

    public Cage(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        TextureRegion block = new TextureRegion(screen.getAtlas().findRegion("cage"), 0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
        setRegion(block);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        setTravelTime(4f);
    }


    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.GROUND_BIT;
        fixtureDef.filter.maskBits =
                AdventureGame.PLAYER_BIT
                | AdventureGame.GROUND_BIT;
        PolygonShape shape = new PolygonShape();
        shape.set(BLOCK_SHAPE);
        fixtureDef.shape = shape;
        fixtureDef.friction = 2f;
        fixtureDef.restitution = 0f;
//        fixtureDef.density = 10f;
        b2body.createFixture(fixtureDef).setUserData(this);
        b2body.setGravityScale(0f);


    }

    @Override
    public void update(float dt) {
        if(!active){
            if(hasBeenActivated()){
                active = true;
                b2body.setGravityScale(1);
            }
        }
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;

        } else if (!destroyed) {
            if(hasALever()){
                if(lever.leverOn()){

                }else{

                }
            }else {
            }
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        }

    }

    private void pauseMovement() {
        b2body.setLinearVelocity(new Vector2(0, 0));
    }

    protected void move(){
        b2body.setLinearVelocity(new Vector2(0,  0.5f));
    }
    protected void moveDown(){
        b2body.setLinearVelocity(new Vector2(0,  -0.5f));
    }

    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

    public void sensorOn(){
        movementTimer = travelTime;
    }
    public void setTravelTime(float time){
        travelTime = time;
    }

    public void attachLever(Lever lever){
        this.lever = lever;
    }
    private boolean hasALever(){
        return (lever != null);
    }

    @Override
    public void dispose() {
        world.destroyBody(b2body);
    }

    @Override
    public void setToDestroy() {
        setToDestroy = true;
        safeToRemove = true;
    }

    private boolean hasBeenActivated(){
        return (b2body.getLinearVelocity().len() > 0);
    }
}
