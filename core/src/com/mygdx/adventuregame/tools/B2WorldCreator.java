package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.Screen;
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
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Chest;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.FireElemental;
import com.mygdx.adventuregame.sprites.HealthBar;
import com.mygdx.adventuregame.sprites.Kobold;
import com.mygdx.adventuregame.sprites.Mimic;
import com.mygdx.adventuregame.sprites.Minotaur;
import com.mygdx.adventuregame.sprites.MonsterTile;
import com.mygdx.adventuregame.sprites.Ogre;
import com.mygdx.adventuregame.sprites.Slime;

public class B2WorldCreator {
    private PlayScreen screen;
    private int chestCounter = 1;
    private BodyDef bodyDef = new BodyDef();
    private PolygonShape shape = new PolygonShape();
    private FixtureDef fixtureDef = new FixtureDef();
    private Body body;
    private World world;

    public B2WorldCreator(World world, TiledMap map, PlayScreen screen) {
        this.screen = screen;
        this.world = world;
//        BodyDef bodyDef = new BodyDef();
//        PolygonShape shape = new PolygonShape();
//        FixtureDef fixtureDef = new FixtureDef();
//        Body body;
        //3 is the object layer from tmx for ground

        for (MapObject object : map.getLayers().get(13).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            MonsterTile monsterTile = new MonsterTile(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.monsterTiles.add(monsterTile);
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
//        for(MapObject object : map.getLayers().get(12).getObjects().getByType(RectangleMapObject.class))
//        {
//            Rectangle rect = ((RectangleMapObject) object).getRectangle();
//            bodyDef.type = BodyDef.BodyType.StaticBody;
//            bodyDef.position.set((rect.getX() + rect.getWidth()/2) / AdventureGame.PPM,
//                    (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
//            body = world.createBody(bodyDef);
//            shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2)/ AdventureGame.PPM);
//            fixtureDef.shape = shape;
//            body.createFixture(fixtureDef);
//        }
        for (MapObject object : map.getLayers().get(12).getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / AdventureGame.PPM,
                        (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
                body = world.createBody(bodyDef);
                shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2) / AdventureGame.PPM);
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);

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
            screen.getHealthBars().add(new HealthBar(screen, 0, 0, enemy));
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
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
        }
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new FireElemental(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }

    }

    private Chest treasureMaker(float chestPosition, float y) {
        int treasureType = AdventureGame.MEDIUM_HEALTH;
        if (isChestAtLocation(chestPosition, AdventureGame.BOW_LOCATION)) {
            treasureType = AdventureGame.BOW;
        } else if (isChestAtLocation(chestPosition, AdventureGame.DOUBLE_JUMP_LOCATION)) {
            treasureType = AdventureGame.RING_OF_DOUBLE_JUMP;
        }else if (isChestAtLocation(chestPosition, AdventureGame.RING_OF_PROTECTION_LOCATION)){
            treasureType = AdventureGame.RING_OF_PROTECTION;
        }
        else if (isChestAtLocation(chestPosition, AdventureGame.SWORD_LOCATION)){
            treasureType = AdventureGame.SWORD;
        }
        else if (isChestAtLocation(chestPosition, AdventureGame.RING_OF_REGEN_LOCATION)){
            treasureType = AdventureGame.RING_OF_REGENERATION;
        }
        else if (isChestAtLocation(chestPosition, AdventureGame.SWORD_LOCATION_2)){
            treasureType = AdventureGame.SWORD;
        }
        else if (isChestAtLocation(chestPosition, AdventureGame.BOW_LOCATION_2)){
            treasureType = AdventureGame.BOW;
        }

        return new Chest(screen, chestPosition / AdventureGame.PPM, y / AdventureGame.PPM, treasureType);
    }

    private boolean isChestAtLocation(float currentChest, float location){
        return (Math.abs(currentChest - location) < 0.01f);
    }


}
