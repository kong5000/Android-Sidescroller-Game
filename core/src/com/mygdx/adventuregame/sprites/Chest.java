package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.mygdx.adventuregame.items.Item;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;

public class Chest extends Enemy implements UpdatableSprite {
    private enum State {CLOSED, OPEN}
    ;
    private State currentState;
    private State previousState;
    private boolean isClosed;
    private World world;
    private PlayScreen screen;
    public Body b2body;
    protected boolean destroyed;
    public boolean safeToRemove = false;
    private boolean setToDestroy = false;

    private float stateTimer;
    private static final int WIDTH_PIXELS = 49;
    private static final int HEIGHT_PIXELS = 37;

    private Animation<TextureRegion> chestOpen;
    private TextureRegion chestClosed;

    private int itemType;
    private float openedTimer;
    private float itemReleaseTimer;

    public Chest(PlayScreen screen, float x, float y, int itemType) {
        super(screen, x, y);
        this.world = screen.getWorld();
        this.screen = screen;
        this.itemType = itemType;
        setPosition(x, y);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        defineChest();
        itemReleaseTimer = 0;
        openedTimer = 0;
        stateTimer = 0;
        currentState = State.CLOSED;
        previousState = currentState;
        isClosed = true;
        chestOpen = generateAnimation(screen.getAtlas().findRegion("chest_open")
                , 4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        chestClosed = new TextureRegion(screen.getAtlas().findRegion("chest_open"),0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
    }

    public void update(float dt) {
        setRegion(getFrame(dt));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if(currentState == State.OPEN){
            openedTimer += dt;

            if(itemReleaseTimer >= 0){
                itemReleaseTimer += dt;
                if(itemReleaseTimer > 0.75f){
                    itemReleaseTimer = -1;
                    screen.getSpritesToAdd().add(new Item(screen, getX() + getWidth() / 2, getY() + getHeight() / 2, itemType));
                }
            }
        }
        if(openedTimer > 2){
            setToDestroy();

        }
        if (setToDestroy && !destroyed) {
            explode();
            world.destroyBody(b2body);
            destroyed = true;
        }
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
        currentState = getChestState();
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        TextureRegion texture;
        if (currentState == State.CLOSED) {
            texture = chestClosed;
        } else {
            texture = chestOpen.getKeyFrame(stateTimer);
        }

        return texture;
    }

    public State getChestState() {
        if (isClosed) {
            return State.CLOSED;
        } else {
            return State.OPEN;
        }
    }

    @Override
    protected Enemy.State getState() {
        return null;
    }

    @Override
    protected void orientTextureTowardsPlayer(TextureRegion texture) {

    }


    private void defineChest() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.PLAYER_SWORD_BIT;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((25f / 2f) / AdventureGame.PPM, (33f / 2f) / AdventureGame.PPM);
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
        if(currentState == State.CLOSED){
            isClosed = false;
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
}
