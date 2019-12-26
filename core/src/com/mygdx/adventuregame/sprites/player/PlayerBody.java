package com.mygdx.adventuregame.sprites.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;

public class PlayerBody {
    private static final float[] SWORD_HITBOX_AIR = {
            -0.25f, 0f,
            -0.25f, 0.2f,
            0f, -0.1f,
            0.25f, 0f,
            0.25f, 0.2f,
            0, 0.3f};

    private static final float[] SWORD_HITBOX_RIGHT = {
            0f, -0.1f,
            0.25f, 0f,
            0.25f, 0.2f,
            0, 0.3f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.25f, 0f,
            -0.25f, 0.2f,
            0f, -0.1f,
            0, 0.3f};
    private static final float[] HEAD_HITBOX = {
            -0.115f, 0.08f,
            -0.01f, 0f,
            0.01f, 0f,
            0.115f, 0.08f};
    private static final float[] RECTANGULAR_HITBOX = {
            -0.05f, 0.25f,
            -0.05f, -0.07f,
            0.05f, -0.07f,
            0.05f, 0.25f};
    private static final float[] RECTANGULAR_HITBOX_SMALL = {
            -0.05f, 0.15f,
            -0.03f, -0.07f,
            0.03f, -0.07f,
            0.05f, 0.15f};
    private float spawnPointX;
    private float spawnPointY;
    private World world;
    private Player player;
    public PlayerBody(World world, Player player){
        this.world = world;
        this.player = player;

    }

    public Body createBigHitBox() {
        Body b2body;
        BodyDef bodyDef = new BodyDef();
        //Starting Castle
        bodyDef.position.set(player.getX() + 0.3f, player.getY() + 0.11f);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(RECTANGULAR_HITBOX);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT
                | AdventureGame.ITEM_BIT
                | AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.isSensor = false;
        fixtureDef.friction = 0;
        b2body.createFixture(fixtureDef).setUserData(player);


        fixtureDef = new FixtureDef();

        bodyShape = new PolygonShape();
        bodyShape.set(HEAD_HITBOX);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.WALL_RUN_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(player);
        return b2body;
    }

    public Body createSmallHitBox() {
        Body b2body;
        BodyDef bodyDef = new BodyDef();
        //Starting Castle
        bodyDef.position.set(player.getX() + 0.3f, player.getY() + 0.11f);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(RECTANGULAR_HITBOX_SMALL);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT
                | AdventureGame.ITEM_BIT
                | AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.isSensor = false;
        fixtureDef.friction = 0;
        b2body.createFixture(fixtureDef).setUserData(player);

        fixtureDef = new FixtureDef();

        bodyShape = new PolygonShape();
        bodyShape.set(HEAD_HITBOX);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.WALL_RUN_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(player);
        return b2body;
    }

    public Body definePlayer() {
        Body b2body;
        BodyDef bodyDef = new BodyDef();
        //Starting Castle
        bodyDef.position.set(spawnPointX , spawnPointY);
//        bodyDef.position.set(spawnPointX , spawnPointY);
        //First minotaur
//        bodyDef.position.set(6400 / AdventureGame.PPM, 900 / AdventureGame.PPM);
        //Boss Area
//        bodyDef.position.set(10950 / AdventureGame.PPM, 900 / AdventureGame.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.set(RECTANGULAR_HITBOX);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT
                | AdventureGame.ENEMY_HEAD_BIT
                | AdventureGame.ENEMY_ATTACK_BIT
                | AdventureGame.ENEMY_PROJECTILE_BIT
                | AdventureGame.PLATFORM_BIT
                | AdventureGame.SPIKE_BIT
                | AdventureGame.ITEM_BIT
                | AdventureGame.MOVING_BLOCK_BIT;
        fixtureDef.isSensor = false;
        fixtureDef.friction = 0;
        b2body.createFixture(fixtureDef).setUserData(player);
        fixtureDef = new FixtureDef();

        bodyShape = new PolygonShape();
        bodyShape.set(HEAD_HITBOX);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = AdventureGame.WALL_RUN_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(player);
        return b2body;
    }

    public Fixture createAttack() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.PLAYER_SWORD_BIT;
        fixtureDef.filter.maskBits = AdventureGame.ENEMY_BIT;
        PolygonShape polygonShape = new PolygonShape();
        float[] hitbox;
        switch (player.getCurrentState()) {
            case JUMPING:
            case FLIPPING:
            case AIR_ATTACKING:
            case FALLING:
                hitbox = SWORD_HITBOX_AIR;
                break;
            default:
                if (player.runningRight) {
                    hitbox = SWORD_HITBOX_RIGHT;
                } else {
                    hitbox = SWORD_HITBOX_LEFT;
                }
                break;
        }
        if (player.attackNumber == 1) {
            hitbox = SWORD_HITBOX_AIR;
        }
        polygonShape.set(hitbox);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = false;
        Fixture attackFixture = player.b2body.createFixture(fixtureDef);
        attackFixture.setUserData(player);
        return attackFixture;
    }

    public void setSpawnPoint(float x,float y){
        this.spawnPointX = x;
        this.spawnPointY = y;
    }

}
