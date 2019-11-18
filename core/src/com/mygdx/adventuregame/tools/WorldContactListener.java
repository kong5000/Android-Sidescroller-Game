package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.FireBall;
import com.mygdx.adventuregame.sprites.Player;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA == null || fixB == null){
            return;
        }

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case AdventureGame.ENEMY_HEAD_BIT | AdventureGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == AdventureGame.ENEMY_HEAD_BIT){
                    if(((Player)fixB.getUserData()).getCurrentState() == Player.State.FALLING){
                        ((Enemy)fixA.getUserData()).hitOnHead();
                    }

                }else {
                    if(((Player)fixA.getUserData()).getCurrentState() == Player.State.FALLING){
                        ((Enemy)fixB.getUserData()).hitOnHead();
                    }
                }
                break;
            case AdventureGame.ENEMY_BIT | AdventureGame.PLAYER_SWORD_BIT:
                if(fixA.getFilterData().categoryBits == AdventureGame.PLAYER_SWORD_BIT){
                    if (((Player) fixA.getUserData()).isSwinging()) {
                        if(((Enemy)fixB.getUserData()).notDamagedRecently()){
                            ((Enemy)fixB.getUserData()).damage();
                        }
                    }

                }else {
                    if (((Player) fixB.getUserData()).isSwinging()) {
                        if(((Enemy)fixA.getUserData()).notDamagedRecently()){
                            ((Enemy)fixA.getUserData()).damage();
                        }
                    }

                }
                break;
            case AdventureGame.ENEMY_PROJECTILE_BIT | AdventureGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT){
                        if(((Player)fixA.getUserData()).notInvincible()){
                            ((Player)fixA.getUserData()).hurt();
                        }
                    ((FireBall)fixB.getUserData()).setToDestroy();
                }else {
                        if(((Player)fixB.getUserData()).notInvincible()){
                            ((Player)fixB.getUserData()).hurt();
                        }
                    ((FireBall)fixA.getUserData()).setToDestroy();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA == null || fixB == null){
            return;
        }

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case AdventureGame.ENEMY_ATTACK_BIT | AdventureGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == AdventureGame.ENEMY_ATTACK_BIT){
                    if (((Enemy) fixA.getUserData()).attackEnabled) {
                        contact.setEnabled(true);
                        if(((Player)fixB.getUserData()).notInvincible()){
                            ((Player)fixB.getUserData()).hurt();
                        }

                    } else {
                        contact.setEnabled(false);
                    }
                }else {
                    if (((Enemy) fixB.getUserData()).attackEnabled) {
                        contact.setEnabled(true);
                        if(((Player)fixA.getUserData()).notInvincible()){
                            ((Player)fixA.getUserData()).hurt();
                        }
                    } else {
                        contact.setEnabled(false);
                    }
                }
                break;
            case AdventureGame.PLAYER_SWORD_BIT | AdventureGame.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == AdventureGame.ENEMY_BIT){
                        contact.setEnabled(false);
                    if (((Player) fixB.getUserData()).isSwinging()) {
                        if(((Enemy)fixA.getUserData()).notDamagedRecently()){
                            ((Enemy)fixA.getUserData()).damage();
                        }
                    }
                }else {
                        contact.setEnabled(false);
                    if (((Player) fixA.getUserData()).isSwinging()) {
                        if(((Enemy)fixB.getUserData()).notDamagedRecently()){
                            ((Enemy)fixB.getUserData()).damage();
                        }
                    }
                }
                break;
            case AdventureGame.ENEMY_HEAD_BIT | AdventureGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT){
                    if (((Player) fixA.getUserData()).getCurrentState() == Player.State.FALLING) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                    }
                }else {
                    if (((Player) fixB.getUserData()).getCurrentState() == Player.State.FALLING) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                    }
                }
                break;
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}