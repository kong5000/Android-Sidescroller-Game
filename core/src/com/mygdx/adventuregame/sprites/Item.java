package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;


public class Item extends Sprite implements UpdatableSprite {
    private enum State {CLOSED, OPEN}

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
    private static final int WIDTH_PIXELS = 16;
    private static final int HEIGHT_PIXELS = 16;

    private TextureRegion itemTexture;

    private float openedTimer;
    private int itemType;

    public Item(PlayScreen screen, float x, float y, int itemType) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        defineItem();
        this.itemType = itemType;
        openedTimer = 0;
        stateTimer = 0;
        currentState = State.CLOSED;
        previousState = currentState;
        isClosed = true;
        setItemTexture(itemType);
        b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);
    }

    private void setItemTexture(int itemType) {
        String assetName;
        switch (itemType) {
            case AdventureGame.BOW:
                assetName = "bow";
                break;
            case AdventureGame.FIRE_SPELLBOOK:
                assetName = "fire_spellbook";
                break;
            case AdventureGame.SMALL_HEALTH:
                assetName = "small_health";
                break;
            case AdventureGame.MEDIUM_HEALTH:
                assetName = "medium_health";
                break;
            case AdventureGame.LARGE_HEALTH:
                assetName = "large_health";
                break;
            default:
                assetName = "small_health";
                break;
        }
        itemTexture = new TextureRegion(screen.getAtlas().findRegion(assetName), 0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
        setRegion(itemTexture);
    }

    public void update(float dt) {
        setRegion(itemTexture);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
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


    private void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.filter.categoryBits = AdventureGame.ITEM_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.PLAYER_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(8 / AdventureGame.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    //    public void pickedUp(){
//        setToDestroy();
//    }
    public void pickedUp() {
        setToDestroy();
    }

    public void explode() {
        screen.getExplosions().add(new Explosion(screen, getX() - getWidth() / 2
                , getY() - getHeight() / 2 - 0.05f));
    }

    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

    public int getItemType() {
        return itemType;
    }
}
