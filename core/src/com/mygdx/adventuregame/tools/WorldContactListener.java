package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.sprites.BossAttack;
import com.mygdx.adventuregame.sprites.CheckPoint;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.EnemyProjectile;
import com.mygdx.adventuregame.sprites.FireSpell;
import com.mygdx.adventuregame.items.Item;
import com.mygdx.adventuregame.sprites.PlayerProjectile;
import com.mygdx.adventuregame.sprites.player.Player;
import com.mygdx.adventuregame.sprites.SpikeBlock;

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
            case AdventureGame.PLAYER_BIT | AdventureGame.ENVIRONMENT_SENSOR_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    CheckPoint checkPoint = ((CheckPoint) fixB.getUserData());
                    Player player = ((Player) fixA.getUserData());
                    if (checkPoint != player.getCurrentCheckPoint()) {
                        checkPoint.playAnimation();
                    }
                    player.setRespawnPoint(checkPoint);

                } else {
                    CheckPoint checkPoint = ((CheckPoint) fixA.getUserData());
                    Player player = ((Player) fixB.getUserData());
                    if (checkPoint != player.getCurrentCheckPoint()) {
                        checkPoint.playAnimation();
                    }
                    player.setRespawnPoint(checkPoint);
                }
                break;
            case AdventureGame.PLAYER_BIT | AdventureGame.MOVING_BLOCK_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).setOnElevator(true);
                } else {
                    ((Player) fixB.getUserData()).setOnElevator(true);
                }
                break;
            case AdventureGame.GROUND_BIT | AdventureGame.MOVING_BLOCK_SENSOR:
                if (fixA.getFilterData().categoryBits == AdventureGame.MOVING_BLOCK_SENSOR) {
                    ((SpikeBlock) fixA.getUserData()).sensorOn();
                } else {
                    ((SpikeBlock) fixB.getUserData()).sensorOn();
                }
                break;
            case AdventureGame.WALL_RUN_BIT | AdventureGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.WALL_RUN_BIT) {
                    ((Player) fixA.getUserData()).enableWallRun();
                } else if (fixB.getFilterData().categoryBits == AdventureGame.WALL_RUN_BIT) {
                    ((Player) fixB.getUserData()).enableWallRun();
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

            case AdventureGame.ARROW_BIT | AdventureGame.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_BIT) {
                    if (((PlayerProjectile) fixB.getUserData()).canCollideWithEnemy()) {
                        int damage = ((PlayerProjectile) fixB.getUserData()).getDamage();
                        ((PlayerProjectile) fixB.getUserData()).targetHit();
                        ((Enemy) fixA.getUserData()).damage(damage);
                    }
                } else {
                    if (((PlayerProjectile) fixA.getUserData()).canCollideWithEnemy()) {
                        int damage = ((PlayerProjectile) fixA.getUserData()).getDamage();
                        ((PlayerProjectile) fixA.getUserData()).targetHit();
                        ((Enemy) fixB.getUserData()).damage(damage);
                    }
                }
                break;
            case AdventureGame.ARROW_BIT | AdventureGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ARROW_BIT) {
//                    ((PlayerProjectile) fixA.getUserData()).setToDestroyHitBox();
//                    ((PlayerProjectile) fixA.getUserData()).setToDestroy();
                    ((PlayerProjectile) fixA.getUserData()).hitGround();
                } else {
//                    ((PlayerProjectile) fixB.getUserData()).setToDestroy();
//                    ((PlayerProjectile) fixB.getUserData()).setToDestroyHitBox();
                    ((PlayerProjectile) fixB.getUserData()).hitGround();
                }
                break;
            case AdventureGame.GROUND_BIT | AdventureGame.PROJECTILE_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PROJECTILE_BIT) {
                    ((EnemyProjectile) fixA.getUserData()).explode();
                    ((EnemyProjectile) fixA.getUserData()).setToDestroy();
                } else {
                    ((EnemyProjectile) fixB.getUserData()).explode();
                    ((EnemyProjectile) fixB.getUserData()).setToDestroy();
                }
                break;

            case AdventureGame.PROJECTILE_BIT | AdventureGame.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_BIT) {
                    ((EnemyProjectile) fixB.getUserData()).setToDestroy();
                    ((Enemy) fixA.getUserData()).damage(2);
                } else {
                    ((EnemyProjectile) fixA.getUserData()).setToDestroy();
                    ((Enemy) fixB.getUserData()).damage(2);
                }
                break;
            case AdventureGame.ENEMY_PROJECTILE_BIT | AdventureGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_PROJECTILE_BIT) {
                    ((EnemyProjectile) fixA.getUserData()).setToDestroy();
                    ((EnemyProjectile) fixA.getUserData()).explode();
                } else {
                    ((EnemyProjectile) fixB.getUserData()).setToDestroy();
                    ((EnemyProjectile) fixB.getUserData()).explode();
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
            case AdventureGame.ITEM_BIT | AdventureGame.PLAYER_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    Item item = ((Item) fixB.getUserData());
                    if (item.canPickup()) {
                        item.pickedUp();
                        ((Player) fixA.getUserData()).pickupItem(item.getItemType());
                    }


                } else {
                    Item item = ((Item) fixA.getUserData());
                    if (item.canPickup()) {
                        item.pickedUp();
                        ((Player) fixB.getUserData()).pickupItem(item.getItemType());
                    }
                }
                break;
            case AdventureGame.GROUND_BIT | AdventureGame.BOSS_ATTACK_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.BOSS_ATTACK_BIT) {
                    ((BossAttack) fixA.getUserData()).onPlayerHit();
                } else if (fixB.getFilterData().categoryBits == AdventureGame.BOSS_ATTACK_BIT) {
                    ((BossAttack) fixB.getUserData()).onPlayerHit();
                }
                break;

            case AdventureGame.BOSS_ATTACK_BIT | AdventureGame.PLAYER_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).hurt(4);
                    ((Player) fixA.getUserData()).knockedBack();
                    ((BossAttack) fixB.getUserData()).onPlayerHit();
                } else if (fixA.getFilterData().categoryBits == AdventureGame.BOSS_ATTACK_BIT) {
                    ((Player) fixB.getUserData()).hurt(4);
                    ((Player) fixB.getUserData()).knockedBack();
                    ((BossAttack) fixA.getUserData()).onPlayerHit();
                }
                break;

        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case AdventureGame.WALL_RUN_BIT | AdventureGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.WALL_RUN_BIT) {
                    ((Player) fixA.getUserData()).disableWallRun();
                } else if (fixB.getFilterData().categoryBits == AdventureGame.WALL_RUN_BIT) {
                    ((Player) fixB.getUserData()).disableWallRun();
                }
                break;
            case AdventureGame.PLAYER_BIT | AdventureGame.MOVING_BLOCK_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).setOnElevator(false);
                } else {
                    ((Player) fixB.getUserData()).setOnElevator(false);
                }
                break;
        }

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
            case AdventureGame.PROJECTILE_BIT | AdventureGame.ENEMY_BIT:
                contact.setEnabled(false);
                break;
            case AdventureGame.ARROW_BIT | AdventureGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ARROW_BIT) {
                    contact.setEnabled(false);
//                    ((PlayerProjectile) fixA.getUserData()).setToDestroyHitBox();
//                    ((PlayerProjectile) fixA.getUserData()).setToDestroy();
                    if (((PlayerProjectile) fixA.getUserData()).hasHitGround()) {
                        ((Player) fixB.getUserData()).pickupItem(AdventureGame.ARROW);
                        ((PlayerProjectile) fixA.getUserData()).setToDestroy();
                    }
                    ;
                }
                if (fixB.getFilterData().categoryBits == AdventureGame.ARROW_BIT) {
                    contact.setEnabled(false);
//                    ((PlayerProjectile) fixA.getUserData()).setToDestroyHitBox();
//                    ((PlayerProjectile) fixA.getUserData()).setToDestroy();
                    if (((PlayerProjectile) fixB.getUserData()).hasHitGround()) {
                        ((Player) fixA.getUserData()).pickupItem(AdventureGame.ARROW);
                        ((PlayerProjectile) fixB.getUserData()).setToDestroy();
                    }
                    ;
                }
                break;
            case AdventureGame.ITEM_BIT | AdventureGame.PLAYER_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    Item item = ((Item) fixB.getUserData());
                    if (item.canPickup()) {
                        item.pickedUp();
                        ((Player) fixA.getUserData()).pickupItem(item.getItemType());
                    }


                } else {
                    Item item = ((Item) fixA.getUserData());
                    if (item.canPickup()) {
                        item.pickedUp();
                        ((Player) fixB.getUserData()).pickupItem(item.getItemType());
                    }
                }
                break;
            case AdventureGame.GROUND_BIT | AdventureGame.MOVING_BLOCK_SENSOR:
                if (fixA.getFilterData().categoryBits == AdventureGame.MOVING_BLOCK_SENSOR) {
                    ((SpikeBlock) fixA.getUserData()).sensorOn();
                } else {
                    ((SpikeBlock) fixB.getUserData()).sensorOn();
                }
                break;
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
                        contact.setEnabled(false);
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
                        ((EnemyProjectile) fixB.getUserData()).setToDestroy();
                        ((EnemyProjectile) fixB.getUserData()).explode();
                    }

                } else {
                    if (((Player) fixB.getUserData()).notInvincible()) {
                        ((Player) fixB.getUserData()).hurt(3);
                        ((EnemyProjectile) fixA.getUserData()).setToDestroy();
                        ((EnemyProjectile) fixA.getUserData()).explode();
                    }
                }
                break;
            case AdventureGame.ENEMY_PROJECTILE_BIT | AdventureGame.PLAYER_SWORD_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.ENEMY_PROJECTILE_BIT) {
                    ((EnemyProjectile) fixA.getUserData()).setToDestroy();
                    ((EnemyProjectile) fixA.getUserData()).explode();
                    int type =((EnemyProjectile) fixA.getUserData()).getType();
                    ((Player) fixB.getUserData()).deflectProjectile(type);

                } else {
                    ((EnemyProjectile) fixB.getUserData()).setToDestroy();
                    ((EnemyProjectile) fixB.getUserData()).explode();
                    int type =((EnemyProjectile) fixB.getUserData()).getType();
                    ((Player) fixA.getUserData()).deflectProjectile(type);
                }
                break;
            case AdventureGame.SPIKE_BIT | AdventureGame.PLAYER_BIT:
                contact.setEnabled(false);
                if (fixA.getFilterData().categoryBits == AdventureGame.PLAYER_BIT) {
                    if (((Player) fixA.getUserData()).notInvincible()) {
                        ((Player) fixA.getUserData()).hitBySpike();
                    }

                } else {
                    if (((Player) fixB.getUserData()).notInvincible()) {
                        ((Player) fixB.getUserData()).hitBySpike();
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
            case AdventureGame.ARROW_BIT | AdventureGame.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == AdventureGame.ARROW_BIT)
                    if (((PlayerProjectile) fixA.getUserData()).canCollideWithEnemy()) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                    }
                if (fixB.getFilterData().categoryBits == AdventureGame.ARROW_BIT)
                    if (((PlayerProjectile) fixB.getUserData()).canCollideWithEnemy()) {
                        contact.setEnabled(true);
                    } else {
                        contact.setEnabled(false);
                    }
                break;
            case AdventureGame.ITEM_BIT | AdventureGame.ITEM_BIT:
                contact.setEnabled(false);
                break;
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}