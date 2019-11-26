package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.FireBall;
import com.mygdx.adventuregame.sprites.FireSpell;
import com.mygdx.adventuregame.sprites.Item;
import com.mygdx.adventuregame.sprites.Player;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA == null || fixB == null) {
            return;
        }

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case AdventureGame.ENEMY_HEAD_BIT | AdventureGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_HEAD_BIT) {
                    if (((Player) fixB.getUserData()).getCurrentState() == Player.State.FALLING) {
                        ((Enemy) fixA.getUserData()).hitOnHead();
                    }

                } else {
                    if (((Player) fixA.getUserData()).getCurrentState() == Player.State.FALLING) {
                        ((Enemy) fixB.getUserData()).hitOnHead();
                    }
                }
                break;
            case AdventureGame.ITEM_BIT | AdventureGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                        Item item = ((Item) fixB.getUserData());
                        item.pickedUp();
                        ((Player) fixA.getUserData()).pickupItem(item.getItemType());

                } else {
                    Item item = ((Item) fixA.getUserData());
                    item.pickedUp();
                    ((Player) fixB.getUserData()).pickupItem(item.getItemType());
                }
                break;
//            case AdventureGame.ENEMY_BIT | AdventureGame.PLAYER_SWORD_BIT:
//                int swordDamage;
//                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_SWORD_BIT) {
//                    if (((Player) fixA.getUserData()).isSwinging()) {
//                        swordDamage = ((Player) fixA.getUserData()).getSwordDamage();
//                            ((Enemy) fixB.getUserData()).damage(swordDamage);
//
//                    }
//
//                } else {
//                    swordDamage = ((Player) fixB.getUserData()).getSwordDamage();
//                    if (((Player) fixB.getUserData()).isSwinging()) {
//                            ((Enemy) fixA.getUserData()).damage(swordDamage);
//                    }
//
//                }
//                break;

            case AdventureGame.PLAYER_PROJECTILE_BIT | AdventureGame.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).damage(3);
                    ((FireBall) fixB.getUserData()).setToDestroy();
                } else {
                    ((Enemy) fixB.getUserData()).damage(3);
                    ((FireBall) fixA.getUserData()).setToDestroy();
                }
                break;
            case AdventureGame.PLAYER_PROJECTILE_BIT | AdventureGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_PROJECTILE_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                    ((FireBall) fixA.getUserData()).explode();
                } else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                    ((FireBall) fixB.getUserData()).explode();
                }
                break;
            case AdventureGame.ENEMY_PROJECTILE_BIT | AdventureGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_PROJECTILE_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                } else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                }
                break;
            case AdventureGame.FIRE_SPELL_BIT | AdventureGame.ENEMY_BIT:
                int damage = 0;
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_BIT) {
                    damage = ((FireSpell) fixB.getUserData()).getDamage();
                    ((Enemy) fixA.getUserData()).hitByFire();
                    ((Enemy) fixA.getUserData()).damage(damage);
                } else {
                    damage = ((FireSpell) fixA.getUserData()).getDamage();
                    ((Enemy) fixB.getUserData()).hitByFire();
                    ((Enemy) fixB.getUserData()).damage(damage);
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

        if (fixA == null || fixB == null) {
            return;
        }

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case AdventureGame.ENEMY_ATTACK_BIT | AdventureGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_ATTACK_BIT) {
                    if (((Enemy) fixA.getUserData()).attackEnabled) {
                        contact.setEnabled(false);
                        if (((Player) fixB.getUserData()).notInvincible()) {
                            ((Player) fixB.getUserData()).hurt(((Enemy) fixA.getUserData()).getDamage());
                        }

                    } else {
                        contact.setEnabled(false);
                    }
                } else {
                    if (((Enemy) fixB.getUserData()).attackEnabled) {
                        contact.setEnabled(true);
                        if (((Player) fixA.getUserData()).notInvincible()) {
                            ((Player) fixA.getUserData()).hurt(((Enemy) fixB.getUserData()).getDamage());
                        }
                    } else {
                        contact.setEnabled(false);
                    }
                }
                break;
            case AdventureGame.ENEMY_BIT | AdventureGame.PLAYER_SWORD_BIT:
                int swordDamage;
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_SWORD_BIT) {
                    if (((Player) fixA.getUserData()).isSwinging()) {
                        swordDamage = ((Player) fixA.getUserData()).getSwordDamage();
                        if (((Enemy) fixB.getUserData()).notDamagedRecently()) {
                            ((Enemy) fixB.getUserData()).damage(swordDamage);
                        }


                    }
                } else {
                    swordDamage = ((Player) fixB.getUserData()).getSwordDamage();
                    if (((Player) fixB.getUserData()).isSwinging()) {
                        if (((Enemy) fixA.getUserData()).notDamagedRecently()) {
                            ((Enemy) fixA.getUserData()).damage(swordDamage);
                        }
                    }

                }
                break;
            case AdventureGame.ENEMY_HEAD_BIT | AdventureGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    if (((Player) fixA.getUserData()).getCurrentState() == Player.State.FALLING) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                    }
                } else {
                    if (((Player) fixB.getUserData()).getCurrentState() == Player.State.FALLING) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                    }
                }
                break;
            case AdventureGame.PLATFORM_BIT | AdventureGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    if (((Player) fixA.getUserData()).b2body.getLinearVelocity().y <= 0
                            && !((Player) fixA.getUserData()).canPassFloor()) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                        ((Player) fixA.getUserData()).dropThroughFloor();
                    }
                } else {
                    if (((Player) fixB.getUserData()).b2body.getLinearVelocity().y <= 0
                            && !((Player) fixB.getUserData()).canPassFloor()) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                        ((Player) fixB.getUserData()).dropThroughFloor();
                    }
                }
                break;
            case AdventureGame.FIRE_SPELL_BIT | AdventureGame.ENEMY_BIT:
                contact.setEnabled(false);
                break;
            case AdventureGame.ENEMY_PROJECTILE_BIT | AdventureGame.PLAYER_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    if (((Player) fixA.getUserData()).notInvincible()) {
                        ((Player) fixA.getUserData()).hurt(3);
                        ((FireBall) fixB.getUserData()).setToDestroy();
                    }

                } else {
                    if (((Player) fixB.getUserData()).notInvincible()) {
                        ((Player) fixB.getUserData()).hurt(3);
                        ((FireBall) fixA.getUserData()).setToDestroy();
                    }

                }
                break;
            case AdventureGame.SPIKE_BIT | AdventureGame.PLAYER_BIT:
                contact.setEnabled(true);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    if (((Player) fixA.getUserData()).notInvincible()) {
                        ((Player) fixA.getUserData()).hurt(3);
                    }

                } else {
                    if (((Player) fixB.getUserData()).notInvincible()) {
                        ((Player) fixB.getUserData()).hurt(3);
                    }
                }
                break;
            case AdventureGame.SPIKE_BIT | AdventureGame.ENEMY_BIT:
                contact.setEnabled(true);
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_BIT) {
                    if (((Enemy) fixB.getUserData()).notDamagedRecently()) {
                        ((Enemy) fixB.getUserData()).damage(10);
                    }
                } else {
                    if (((Enemy) fixA.getUserData()).notDamagedRecently()) {
                        ((Enemy) fixA.getUserData()).damage(10);
                    }
                }
                break;
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}