package com.mygdx.adventuregame.items;

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
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.UpdatableSprite;
import com.mygdx.adventuregame.tools.AnimationGenerationTool;


public class Item extends Sprite implements UpdatableSprite {
    private boolean goingRight = false;

    private enum State {CLOSED, OPEN}

    private State currentState;
    private World world;
    private PlayScreen screen;
    public Body b2body;
    protected boolean destroyed;
    public boolean safeToRemove = false;
    private boolean setToDestroy = false;

    private float dontPickupTimer = 0.35f;
    private float stateTimer;
    private static final int WIDTH_PIXELS = 16;
    private static final int HEIGHT_PIXELS = 16;

    private TextureRegion itemTexture;

    private float openedTimer;
    private int itemType;
    private Animation<TextureRegion> itemAnimation;

    public Item(PlayScreen screen, float x, float y, int itemType) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        this.itemType = itemType;
        defineItem();

        openedTimer = 0;
        stateTimer = 0;
        currentState = State.CLOSED;

        if (itemType != AdventureGame.GOLD_COIN) {
            setItemTexture(itemType);
        } else {
            itemAnimation = AnimationGenerationTool.generateAnimation(screen.getAtlas().findRegion("gold_coin"),
                    6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
            itemAnimation.setPlayMode(Animation.PlayMode.LOOP);
            setScale(0.8f);
        }

        b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);
        if (itemType == AdventureGame.ARROW) {
            setScale(0.75f);
            b2body.setGravityScale(0);
        }
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
            case AdventureGame.RING_OF_DOUBLE_JUMP:
                assetName = "ring_of_double_jump";
                break;
            case AdventureGame.RING_OF_PROTECTION:
                assetName = "ring_of_protection";
                break;
            case AdventureGame.SWORD:
                assetName = "sword";
                break;
            case AdventureGame.ARROW:
                assetName = "arrow";
                break;
            case AdventureGame.GOLD_KEY:
                assetName = "gold_key";
                break;
            case AdventureGame.RED_KEY:
                assetName = "red_key";
                break;
            case AdventureGame.BLUE_KEY:
                assetName = "blue_key";
                break;
            case AdventureGame.SILVER_KEY:
                assetName = "silver_key";
                break;
            default:
                assetName = "small_health";
                break;
        }
        itemTexture = new TextureRegion(screen.getAtlas().findRegion(assetName), 0, 0, WIDTH_PIXELS, HEIGHT_PIXELS);
        setRegion(itemTexture);
    }

    public void update(float dt) {
        if (itemType == AdventureGame.ARROW) {
            b2body.setLinearVelocity(0, 0);
        }
        stateTimer += dt;
        if (itemType != AdventureGame.GOLD_COIN) {
            setRegion(itemTexture);
        } else {
            setRegion(itemAnimation.getKeyFrame(stateTimer));
        }

        if (itemType == AdventureGame.ARROW) {
            if (goingRight) {
                setPosition(b2body.getPosition().x - getWidth() / 2 + 0.07f, b2body.getPosition().y - getHeight() / 2 + 0.16f);
            } else {
                setPosition(b2body.getPosition().x - getWidth() / 2 + 0.07f, b2body.getPosition().y - getHeight() / 2);
            }
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

        if (dontPickupTimer > 0) {
            dontPickupTimer -= dt;
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
        float radius = 8;
        if (itemType == AdventureGame.ARROW) {
            radius = 2.5f;
        }
        shape.setRadius(radius / AdventureGame.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }

    private void playSound() {
        switch (itemType) {
            case AdventureGame.BOW:

                break;
            case AdventureGame.FIRE_SPELLBOOK:

                break;
            case AdventureGame.SMALL_HEALTH:
                screen.getSoundEffects().playCoinSound();
                break;
            case AdventureGame.MEDIUM_HEALTH:
                screen.getSoundEffects().playCoinSound();

                break;
            case AdventureGame.LARGE_HEALTH:
                screen.getSoundEffects().playCoinSound();

                break;
            case AdventureGame.RING_OF_DOUBLE_JUMP:

                break;
            case AdventureGame.RING_OF_PROTECTION:

                break;
            case AdventureGame.SWORD:

                break;
            case AdventureGame.ARROW:

                break;
            case AdventureGame.GOLD_COIN:
                screen.getSoundEffects().playCoinSound();
            default:
                screen.getSoundEffects().playCoinSound();
                break;
        }
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    //    public void pickedUp(){
//        setToDestroy();
//    }
    public void pickedUp() {
        playSound();
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

    public boolean canPickup() {
        return (dontPickupTimer < 0 && !setToDestroy);
    }

    @Override
    public void dispose() {
        world.destroyBody(b2body);
    }

    public void setItemRotation(float angle) {
        setRotation(angle);
    }

    public void setGoingRight(boolean state) {
        goingRight = state;
    }
}
