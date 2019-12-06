package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class HorizontalSpikeBlock extends SpikeBlock {
    private static float[] SENSOR_SHAPE_H = {
            -0.1f, 0.1f,
            -0.1f, 0f,
            0.3f, 0f,
            0.3f, 0.1f};

    public HorizontalSpikeBlock(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setTravelTime(4.75f);
    }

    @Override
    protected void move() {
        b2body.setLinearVelocity(new Vector2(-1f,  0f));
        sensorBody.setLinearVelocity(new Vector2(-1f,  0f));
    }

    @Override
    protected void moveDown() {
        b2body.setLinearVelocity(new Vector2(1f,  0));
        sensorBody.setLinearVelocity(new Vector2(1f, 0));
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.filter.maskBits =
                AdventureGame.PLAYER_BIT;
        PolygonShape shape = new PolygonShape();
        shape.set(BLOCK_SHAPE);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        b2body.setGravityScale(0f);


        bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
//        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        sensorBody = world.createBody(bodyDef);

        fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.MOVING_BLOCK_SENSOR;
        fixtureDef.filter.maskBits =
                AdventureGame.GROUND_BIT;
        shape.set(SENSOR_SHAPE_H);
        fixtureDef.shape = shape;
//        CircleShape myShape = new CircleShape();
//        myShape.setRadius(20 / AdventureGame.PPM);
        fixtureDef.shape =  shape;
        fixtureDef.isSensor = true;
        sensorBody.createFixture(fixtureDef).setUserData(this);
        sensorBody.setGravityScale(0);


        fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.SPIKE_BIT;
        fixtureDef.filter.maskBits =
                AdventureGame.PLAYER_BIT;
        shape.set(BOTTOM_SPIKES);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = false;
        b2body.createFixture(fixtureDef).setUserData(this);
        b2body.setGravityScale(0f);

    }
}
