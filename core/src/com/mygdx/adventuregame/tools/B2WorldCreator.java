package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.FireElemental;
import com.mygdx.adventuregame.sprites.HealthBar;
import com.mygdx.adventuregame.sprites.Kobold;
import com.mygdx.adventuregame.sprites.Minotaur;
import com.mygdx.adventuregame.sprites.MonsterTile;
import com.mygdx.adventuregame.sprites.Slime;

public class B2WorldCreator {
    private PlayScreen screen;
    public B2WorldCreator(World world, TiledMap map, PlayScreen screen){
        this.screen = screen;
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef =  new FixtureDef();
        Body body;
        //3 is the object layer from tmx for ground
        for(MapObject object : map.getLayers().get(13).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            MonsterTile monsterTile  =new MonsterTile(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.monsterTiles.add(monsterTile);
        }

        for(MapObject object : map.getLayers().get(12).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth()/2) / AdventureGame.PPM,
                    (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2)/ AdventureGame.PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        for(MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth()/2) / AdventureGame.PPM,
                    (rect.getY() + rect.getHeight() / 2) / AdventureGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox((rect.getWidth() / 2) / AdventureGame.PPM, (rect.getHeight() / 2)/ AdventureGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = AdventureGame.PLATFORM_BIT;
            fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT | AdventureGame.ENEMY_BIT;
            body.createFixture(fixtureDef);
        }

        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Slime(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getEnemyList().add(enemy);
            screen.getHealthBars().add(new HealthBar(screen, 0, 0, enemy));
        }
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy =new Kobold(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getEnemyList().add(enemy);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
        }
        for(MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Minotaur(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getEnemyList().add(enemy);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
        }
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new FireElemental(screen, rect.getX() / AdventureGame.PPM, rect.getY() / AdventureGame.PPM);
            screen.getHealthBarsToAdd().add(new HealthBar(screen, 0, 0, enemy));
            screen.getEnemyList().add(enemy);
        }

    }
}
