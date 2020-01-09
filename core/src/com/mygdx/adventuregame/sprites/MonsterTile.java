package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Effects.Explosion;

public class MonsterTile extends Sprite implements UpdatableSprite{
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
    private static final int HEIGHT_PIXELS = 32;
    private boolean isFriendly;

    private Animation<TextureRegion> projectile;
    public MonsterTile(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);
        defineTile();
        attackEnabled = false;
        stateTimer = 0;
        setRegion(screen.getAtlas().findRegion("monster_block"));

    }

    public void update(float dt){
        if(setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            explode();
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


    private void defineTile() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.filter.categoryBits = AdventureGame.GROUND_BIT;
            fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT
                    | AdventureGame.ENEMY_BIT
                    | AdventureGame.PLAYER_PROJECTILE_BIT
                    | AdventureGame.FIRE_SPELL_BIT;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((32f / 2f) / AdventureGame.PPM, (32f / 2f) / AdventureGame.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() /2);
    }

    public void setToDestroy(){
        setToDestroy = true;
    }


    public void explode(){
        screen.explosionsToAdd.add(new Explosion(screen, getX() - getWidth() / 2
                ,getY() - getHeight() / 2 -0.05f));
    }
    @Override
    public boolean safeToRemove() {
        return safeToRemove;
    }

    @Override
    public void dispose() {
        world.destroyBody(b2body);
    }
}
