package com.mygdx.adventuregame.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;

public class Minotaur extends Enemy {
    private static final float[] MINOTAUR_HITBOX = {-0.15f, 0.1f, -0.15f, -0.35f, 0.15f, -0.35f, 0.15f, 0.1f};
    private static final float[] SWORD_HITBOX_RIGHT = {
            0.4f, -0.4f,
            0.4f, 0.1f,
            0.1f, -0.4f,
            -0.2f, 0.3f};
    private static final float[] SWORD_HITBOX_LEFT = {
            -0.4f, -0.4f,
            -0.4f, 0.1f,
            -0.1f, -0.4f,
            0.2f, 0.3f};

    private static final float ATTACK_RATE = 1.5f;
    private static final float HURT_RATE = 0.3f;
    private static final float CORPSE_TIME = 1.1f;
    private static final int WIDTH_PIXELS = 98;
    private static final int HEIGHT_PIXELS = 79;
    private float stateTimer;
    private float hurtTimer = -1f;
    private float attackTimer;
    private float damagedTimer;
    private float flashRedTimer;
    private static final float DAMAGED_TIME = 0.7f;
    private static final float FLASH_RED_TIME = 0.2f;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> attackAnimationDamaged;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> idleAnimation;
    private boolean setToDestroy;

    private int health = 6;

    private boolean runningRight;
    private Fixture attackFixture;


    public Minotaur(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        walkAnimation = generateAnimation(screen.getAtlas().findRegion("minotaur_run"),
                6, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        deathAnimation = generateAnimation(screen.getAtlas().findRegion("minotaur_die"),
                8, 96, HEIGHT_PIXELS, 0.1f);
        attackAnimation = generateAnimation(screen.getAtlas().findRegion("minotaur_attack_slow"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        attackAnimationDamaged = generateAnimation(screen.getAtlas().findRegion("minotaur_attack_slow_damaged"),
                10, WIDTH_PIXELS, HEIGHT_PIXELS, 0.1f);
        hurtAnimation = generateAnimation(screen.getAtlas().findRegion("minotaur_hurt"),
                4, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        idleAnimation = generateAnimation(screen.getAtlas().findRegion("minotaur_idle"),
                5, WIDTH_PIXELS, HEIGHT_PIXELS, 0.07f);
        setBounds(getX(), getY(), WIDTH_PIXELS / AdventureGame.PPM, HEIGHT_PIXELS / AdventureGame.PPM);

        stateTimer = 0;
        setToDestroy = false;
        destroyed = false;
        currentState = State.IDLE;
        previousState = State.IDLE;
        attackTimer = ATTACK_RATE;
        damagedTimer = -1f;
        flashRedTimer = -1f;
    }

    @Override
    public void update(float dt) {
        if (health <= 0) {
            setToDestroy = true;
        }
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        } else if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            if (hurtTimer > 0) {
                hurtTimer -= dt;
            }
            if (damagedTimer > 0) {
                damagedTimer -= dt;
            }
            if (flashRedTimer > 0) {
                flashRedTimer -= dt;
            }
        }
        setRegion(getFrame(dt));
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTimer < CORPSE_TIME) {
            super.draw(batch);
        } else {
            safeToRemove = true;
        }
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DYING:
                attackEnabled = false;
                region = deathAnimation.getKeyFrame(stateTimer);
                break;
            case ATTACKING:
                if (flashRedTimer > 0) {
                    region = attackAnimationDamaged.getKeyFrame(stateTimer);
                } else {
                    region = attackAnimation.getKeyFrame(stateTimer);
                }
                attackEnabled = true;
                break;
            case HURT:
                attackEnabled = false;
                region = hurtAnimation.getKeyFrame(stateTimer);
                break;
            case CHASING:
                attackEnabled = false;
                region = walkAnimation.getKeyFrame(stateTimer, true);
                break;
            case IDLE:
            default:
                attackEnabled = false;
                region = idleAnimation.getKeyFrame(stateTimer, true);
                break;
        }

        Vector2 vectorToPlayer = getVectorToPlayer();
        runningRight = vectorToPlayer.x > 0;

        if (!runningRight && region.isFlipX()) {
            region.flip(true, false);
        }
        if (runningRight && !region.isFlipX()) {
            region.flip(true, false);
        }

        if (currentState != State.ATTACKING) {
            attackTimer = -1;
        }
        if (currentState == State.CHASING) {
            if (vectorToPlayer.x > 0) {
                b2body.setLinearVelocity(1f, 0f);
            } else {
                b2body.setLinearVelocity(-1f, 0f);
            }
            if (Math.abs(getVectorToPlayer().x) < 100 / AdventureGame.PPM) {
                attackTimer = ATTACK_RATE;
                if (vectorToPlayer.x < 0) {
                    b2body.applyLinearImpulse(new Vector2(-.5f, 1.5f), b2body.getWorldCenter(), true);
                } else {
                    b2body.applyLinearImpulse(new Vector2(.5f, 1.5f), b2body.getWorldCenter(), true);
                }

            }
        }
        if (currentState == State.ATTACKING) {
            if (stateTimer > 0.5f) {
                if (attackFixture == null)
                    createAttack();
            }

            if (stateTimer > 0.7f) {
                if (attackFixture != null) {
                    b2body.destroyFixture(attackFixture);
                    attackFixture = null;
                }
            }
        }
        if (attackTimer > 0) {
            attackTimer -= dt;
        }


        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (setToDestroy) {
            return State.DYING;
        } else if (hurtTimer > 0) {
            return State.HURT;
        } else if (attackTimer > 0) {
            return State.ATTACKING;
        } else if (Math.abs(getVectorToPlayer().x) < 180 / AdventureGame.PPM) {
            return State.CHASING;
        } else if (b2body.getLinearVelocity().x == 0) {
            return State.IDLE;
        } else {
            return State.IDLE;
        }
    }

    @Override
    public void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = AdventureGame.GROUND_BIT | AdventureGame.PLAYER_SWORD_BIT;
        PolygonShape shape = new PolygonShape();
        shape.set(MINOTAUR_HITBOX);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        hurt();
    }


    @Override
    public void hurt() {
        if (damagedTimer < 0) {
            health -= 1;
            damagedTimer = DAMAGED_TIME;
        }
        if (flashRedTimer < 0) {
            flashRedTimer = FLASH_RED_TIME;
        }
    }

    @Override
    public boolean notHurt() {
        return (damagedTimer < 0);
    }

    private void createAttack() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = AdventureGame.ENEMY_ATTACK_BIT;
        fixtureDef.filter.maskBits = AdventureGame.PLAYER_BIT;
        PolygonShape polygonShape = new PolygonShape();
        float[] hitbox;

        if (runningRight) {
            hitbox = SWORD_HITBOX_RIGHT;
        } else {
            hitbox = SWORD_HITBOX_LEFT;
        }

        polygonShape.set(hitbox);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = false;
        attackFixture = b2body.createFixture(fixtureDef);
        attackFixture.setUserData(this);
    }
}
