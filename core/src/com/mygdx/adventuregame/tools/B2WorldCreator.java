package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.items.Item;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Cage;
import com.mygdx.adventuregame.sprites.CheckPoint;
import com.mygdx.adventuregame.sprites.Chest;
import com.mygdx.adventuregame.sprites.Enemies.Executioner;
import com.mygdx.adventuregame.sprites.Enemies.Ghoul;
import com.mygdx.adventuregame.sprites.Enemies.Golem;
import com.mygdx.adventuregame.sprites.Enemies.IceGolem;
import com.mygdx.adventuregame.sprites.Enemies.Imp;
import com.mygdx.adventuregame.sprites.Enemies.Reaper;
import com.mygdx.adventuregame.sprites.Enemies.RedOgre;
import com.mygdx.adventuregame.sprites.Enemies.Satyr;
import com.mygdx.adventuregame.sprites.Enemies.Shade;
import com.mygdx.adventuregame.sprites.Enemies.Slug;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.Enemies.FireElemental;
import com.mygdx.adventuregame.sprites.Enemies.FireGolem;
import com.mygdx.adventuregame.sprites.HealthBar;
import com.mygdx.adventuregame.sprites.HorizontalSpikeBlock;
import com.mygdx.adventuregame.sprites.Enemies.Kobold;
import com.mygdx.adventuregame.sprites.Enemies.Mimic;
import com.mygdx.adventuregame.sprites.Enemies.Minotaur;
import com.mygdx.adventuregame.sprites.MonsterTile;
import com.mygdx.adventuregame.sprites.Enemies.Ogre;
import com.mygdx.adventuregame.sprites.Enemies.Slime;
import com.mygdx.adventuregame.sprites.SpikeBlock;
import com.mygdx.adventuregame.sprites.Lever;

import java.util.ArrayList;

public class B2WorldCreator {
    private PlayScreen screen;
    private int chestCounter = 1;
    private BodyDef bodyDef = new BodyDef();
    private PolygonShape shape = new PolygonShape();
    private FixtureDef fixtureDef = new FixtureDef();
    private Body body;
    private World world;
    private Array<Lever> levers;
    private Array<Body> bodies;
    private Array<MonsterTile> monsterTiles;

    public B2WorldCreator(World world, TiledMap map, PlayScreen screen) {
        this.screen = screen;
        this.world = world;
        levers = new Array<>();
        bodies = new Array<>();
        monsterTiles = new Array<>();
//        BodyDef bodyDef = new BodyDef();
//        PolygonShape shape = new PolygonShape();
//        FixtureDef fixtureDef = new FixtureDef();
//        Body body;
        //3 is the object layer from tmx for ground


        for (MapObject object : map.getLayers().get(13).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            MonsterTile monsterTile = new MonsterTile(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            monsterTiles.add(monsterTile);
            screen.getSpritesToAdd().add(monsterTile);
        }

        for (MapObject object : map.getLayers().get(16).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Mimic(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(17).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Ogre(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(18).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new FireGolem(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(21).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            CheckPoint checkPoint = new CheckPoint(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM - 0.07f);
            screen.getCheckPoints().add(checkPoint);
        }

        for (MapObject object : map.getLayers().get(22).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Lever lever = new Lever(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM - 0.07f);
            screen.getSpritesToAdd().add(lever);
            levers.add(lever);

        }
        for (MapObject object : map.getLayers().get(23).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            screen.getSpritesToAdd().add(new Item(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM,AdventureGame.GOLD_COIN));
        }
        for (MapObject object : map.getLayers().get(24).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Reaper(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(25).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Ghoul(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(26).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Executioner(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(27).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Golem(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(28).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new IceGolem(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(29).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Cage cage = new Cage(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getSpritesToAdd().add(cage);
        }
        for (MapObject object : map.getLayers().get(30).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Imp(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(31).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Shade(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(32).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new RedOgre(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(33).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Satyr(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(34).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Slug(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }
        for (MapObject object : map.getLayers().get(19).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            SpikeBlock spikeBlock = new SpikeBlock(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            if (isAtLocation(rect.getX(), 2079.5f)) {
                spikeBlock.setTravelTime(8f);
                spikeBlock.attachLever(levers.get(0));
            }
            if (isAtLocation(rect.getX(), 7216f)) {
                spikeBlock.setTravelTime(8f);
                spikeBlock.attachLever(levers.get(1));
            }
            screen.getSpritesToAdd().add(spikeBlock);
        }
        for (MapObject object : map.getLayers().get(20).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            HorizontalSpikeBlock spikeBlock = new HorizontalSpikeBlock(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM - 0.07f);
//            if(isAtLocation(rect.getY(), 1040)){
//                spikeBlock.setTravelTime(4.3f);
//            }
//            if(isAtLocation(rect.getY(), 1168)){
//                spikeBlock.setTravelTime(4.3f);
//            }
            screen.getSpritesToAdd().add(spikeBlock);
        }


        for (MapObject object : map.getLayers().get(12).getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / AdventureGame.PPM,
                        (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
                body = world.createBody(bodyDef);
                shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2) / AdventureGame.PPM);
                fixtureDef.shape = shape;
                fixtureDef.restitution = 0;
                body.createFixture(fixtureDef);
                bodies.add(body);
            } else if (object instanceof PolygonMapObject) {
                PolygonShape shape = new PolygonShape();
                Polygon polygon = ((PolygonMapObject) object).getPolygon();

                bodyDef.position.set((polygon.getX() / AdventureGame.PPM),
                        polygon.getY() / AdventureGame.PPM);

                polygon.setPosition(0, 0);
                polygon.setScale(1 / AdventureGame.PPM, 1 / AdventureGame.PPM);
                float[] vertice = polygon.getTransformedVertices();
                shape.set(polygon.getTransformedVertices());
                fixtureDef.shape = shape;

                world.createBody(bodyDef).createFixture(fixtureDef);


//                shape = getPolygon((PolygonMapObject) object);
//                bodyDef.type = BodyDef.BodyType.StaticBody;
//                body = world.createBody(bodyDef);
//                fixtureDef.shape = shape;
//                body.createFixture(fixtureDef);
                bodies.add(body);
            }

        }
        for (MapObject object : map.getLayers().get(14).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / AdventureGame.PPM,
                    (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2) / AdventureGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = AdventureGame.SPIKE_BIT;
            fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT | AdventureGame.ENEMY_BIT;
            body.createFixture(fixtureDef);
        }

        for (MapObject object : map.getLayers().get(15).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            float x = rect.getX();
            float y = rect.getY();
            Chest chest = treasureMaker(x, y);
            screen.getSpritesToAdd().add(chest);
            chestCounter++;
        }


        for (MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / AdventureGame.PPM,
                    (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2) / AdventureGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = AdventureGame.PLATFORM_BIT;
            fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT | AdventureGame.ENEMY_BIT | AdventureGame.ITEM_BIT;
            body.createFixture(fixtureDef);
        }

        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Slime(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getEnemyList().add(enemy);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
        }
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Kobold(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getEnemyList().add(enemy);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
        }
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Minotaur(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getEnemyList().add(enemy);
//            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            ((Minotaur )enemy).attachNearbyTiles(monsterTiles);
        }
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new FireElemental(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM, false);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }

    }

    private Chest treasureMaker(float chestPosition, float y) {
        int treasureType = AdventureGame.MEDIUM_HEALTH;
        if (isAtLocation(chestPosition, AdventureGame.BOW_LOCATION)) {
            treasureType = AdventureGame.BOW;
        } else if (isAtLocation(chestPosition, AdventureGame.DOUBLE_JUMP_LOCATION)) {
            treasureType = AdventureGame.RING_OF_DOUBLE_JUMP;
        } else if (isAtLocation(chestPosition, AdventureGame.RING_OF_PROTECTION_LOCATION)) {
            treasureType = AdventureGame.RING_OF_PROTECTION;
        } else if (isAtLocation(chestPosition, AdventureGame.SWORD_LOCATION)) {
            treasureType = AdventureGame.SWORD;
        } else if (isAtLocation(chestPosition, AdventureGame.RING_OF_REGEN_LOCATION)) {
            treasureType = AdventureGame.RING_OF_REGENERATION;
        } else if (isAtLocation(chestPosition, AdventureGame.SWORD_LOCATION_2)) {
            treasureType = AdventureGame.SWORD;
        } else if (isAtLocation(chestPosition, AdventureGame.BOW_LOCATION_2)) {
            treasureType = AdventureGame.BOW;
        }

        return new Chest(screen, chestPosition / AdventureGame.PPM, y / AdventureGame.PPM, treasureType);
    }

    private boolean isAtLocation(float currentChest, float location) {
        return (Math.abs(currentChest - location) < 0.01f);
    }
    public void destroyBodies(){
        int i = 0;
        for(Body body : bodies){
            bodies.removeValue(body, true);
            world.destroyBody(body);
            i++;
        }
        for(Lever lever : levers){
            levers.removeValue(lever, true);
        }
        for(MonsterTile monsterTile: monsterTiles){
            monsterTiles.removeValue(monsterTile, true);
        }
    }

    public boolean tearDownComplete(){
        return bodies.isEmpty();
    }

}
