package com.mygdx.adventuregame.tools;

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

public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map){
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef =  new FixtureDef();
        Body body;
        //3 is the object layer from tmx for ground
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class))
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
    }
}
