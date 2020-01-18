package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.SoundEffects;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.BlueFlame;

public class CheckPoint {
    private World world;
    public Body b2body;
    private PlayScreen screen;
    private boolean torchLit = false;
    float xPos;
    float yPos;
    private boolean active = false;
    private static final float[] CHECKPOINT_BOX = {
            -0.2f, 0.25f,
            -0.2f, -1.25f,
            0.35f, -1.25f,
            0.35f, 0.25f};
    public CheckPoint(PlayScreen screen,float x, float y){
        this.world = screen.getWorld();
        this.xPos = x;
        this.yPos = y;
        this.screen = screen;

        defineCheckPointBox();
    }

    private void defineCheckPointBox() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(xPos, yPos);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(CHECKPOINT_BOX);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.ENVIRONMENT_SENSOR_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;

        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(this);
    }
    
    public float getXPos(){
        return xPos +0.06f;
    }
    public float getYPos(){
        return yPos -1.12f;
    }

    public void playAnimation() {
        if(!torchLit){
            screen.getSoundEffects().playFlameSound();
            torchLit = true;
            screen.getSpritesToAdd().add(new BlueFlame(screen, getXPos(), getYPos() + 0.75f));
        }
    }
    public boolean activated(){
        return active;
    }
    public void setActive(){
        active = true;
    }
    public void destroy(){
        world.destroyBody(b2body);
    }
}
