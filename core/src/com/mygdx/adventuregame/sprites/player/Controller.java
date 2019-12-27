package com.mygdx.adventuregame.sprites.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.screens.PlayScreen;
import com.mygdx.adventuregame.sprites.FireSpell;


public class Controller implements InputProcessor {
    Viewport viewport;
    Stage stage;
    OrthographicCamera cam;

    private Touchpad touchpadLeft;
    private Touchpad touchpadRight;

    Texture padKnobTex;
    Texture padBackTex;


    private static final float SCALE = 1.2f;

    public boolean touchDown;
    private Image image;
    private Image jumpButton;

    private PlayScreen screen;

    private Player player;

    private boolean buttonClicked = false;


    private float touchDownTimer;
    private boolean hasActed = false;
    int touchStartX;
    int touchStartY;

    int touchEndX;
    int touchEndY;

    boolean gestureStarted = false;
    private TextureRegionDrawable shield;
    private TextureRegionDrawable button;
    private TextureRegionDrawable sword;
    private TextureRegionDrawable bow;
    private boolean stopSpell = true;
    private boolean attackHeldDown = false;
    private float attackHeldDownTimer = 0;

    public Controller(SpriteBatch batch, final PlayScreen screen) {
        this.screen = screen;
        this.player = screen.getPlayer();
        bow = new TextureRegionDrawable(new TextureRegion(screen.getAtlas().findRegion("bow"), 0, 0, 16, 16));
//        button = new TextureRegionDrawable(new TextureRegion(screen.getAtlas().findRegion("fire_elemental_idle"),
//                0, 0, 62, 43));
        button = new TextureRegionDrawable(new TextureRegion(screen.getAtlas().findRegion("pearl_01a"),
                0, 0, 62, 43));
        sword = new TextureRegionDrawable(new TextureRegion(screen.getAtlas().findRegion("sword_02b"),
                0, 0, 62, 43));
        shield = new TextureRegionDrawable(new TextureRegion(screen.getAtlas().findRegion("grass_shield"),
                0, 0, 100, 100));

        image = new Image();
        image.setSize(62 * SCALE, 43 * SCALE);
        image.setVisible(false);
        image.setDrawable(button);
        image.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (player.canAct()) {
                    player.castSpell();
                    if (player.getEquipedSpell() == Player.Spell.FIREBALL) {
                        player.startChargingAnimation();
                        player.setChargingSpell();
                    } else if (player.getEquipedSpell() == Player.Spell.BOW) {
                        player.chargingBow = true;
                    }
                }

                buttonClicked = true;


                stopSpell = false;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonClicked = false;
                touchDown = false;
                player.endChargingSpell();
                stopSpell = true;

                if (player.chargingBow) {
                    player.chargingBow = false;
                }
            }
        });

        Image aButtonImage = new Image(new Texture("a_button.png"));
        aButtonImage.setSize(50, 50);

        Image bButtonImage = new Image(new Texture("b_button.png"));
        bButtonImage.setSize(40, 40);

        Image spellButtonImage = new Image(new Texture("spell_button.png"));
        spellButtonImage.setSize(40, 40);

        jumpButton = new Image(new Texture("a_button.png"));
        jumpButton.setSize(50, 50);
//        jumpButton.setScale(0.85f);
        jumpButton.setVisible(true);
        jumpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (player.canMove()) {
                    player.jump();

                }
                player.jumpIsHeld = true;
                buttonClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonClicked = false;
                touchDown = false;
                player.jumpIsHeld = false;
            }
        });

        cam = new OrthographicCamera();

        viewport = new ExtendViewport(AdventureGame.V_WIDTH, AdventureGame.V_HEIGHT, cam);
        cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        stage = new Stage(viewport, batch);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
//        Gdx.input.setInputProcessor(stage);

        Touchpad.TouchpadStyle touchStyle = new Touchpad.TouchpadStyle();
        padKnobTex = new Texture(Gdx.files.internal("controller_inner_small.png"));
        TextureRegion padKnobReg = new TextureRegion(padKnobTex);

        touchStyle.knob = new TextureRegionDrawable(padKnobReg);
        padBackTex = new Texture(Gdx.files.internal("controller_outer_small.png"));
        TextureRegion padBackReg = new TextureRegion(padBackTex);
        touchStyle.background = new TextureRegionDrawable(padBackReg);
        touchpadLeft = new Touchpad(5.5f, touchStyle);
        touchpadLeft.setVisible(false);
        touchpadRight = new Touchpad(5.5f, touchStyle);
        touchpadRight.setVisible(false);


        Table buttonTable = new Table();
//        buttonTable.bottom();
//        buttonTable.padLeft(100);
//        buttonTable.add();
//        buttonTable.add(bButtonImage).size(bButtonImage.getWidth(), bButtonImage.getHeight());
//        buttonTable.add();
//        buttonTable.row().pad(5, 5, 5, 5);
//        buttonTable.add(aButtonImage).size(aButtonImage.getWidth(), aButtonImage.getHeight());
//        buttonTable.add();
//        buttonTable.add(spellButtonImage).size(spellButtonImage.getWidth(), spellButtonImage.getHeight());
//        buttonTable.row().padBottom(5);
//        buttonTable.add();


        aButtonImage.setPosition(350,0);
        bButtonImage.setPosition(300, 10);
        spellButtonImage.setPosition(350, 60);


        spellButtonImage.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                player.beginRangedAttack();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                player.endRangedAttack();
            }
        });
        bButtonImage.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hasActed = false;
                attackHeldDown = true;
                player.swingSword();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                attackHeldDownTimer = 0;
                attackHeldDown = false;
            }
        });

        aButtonImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (player.canMove()) {
                    player.jump();
                    player.enableSpellBall();
                }
                player.jumpIsHeld = true;
                buttonClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonClicked = false;
                touchDown = false;
                player.jumpIsHeld = false;
            }
        });
        stage.addActor(aButtonImage);
        stage.addActor(bButtonImage);
        stage.addActor(spellButtonImage);

        Table table = new Table();
        table.left().bottom();
        table.setFillParent(true);
        table.toFront();
        table.padLeft(25);
        table.padBottom(10);
        table.add(touchpadLeft).expandX();
        table.add().expandX();
        table.add().expandX();
        table.add().expandX();
        table.add().expandX();
        table.add().expandX();
        table.add().expandX();
//        table.add(image).size(image.getWidth(), image.getHeight());
//        table.add(jumpButton).size(jumpButton.getWidth(), jumpButton.getHeight()).expandX();
//        table.add(touchpadRight).expandX();


        touchpadLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });
        stage.addActor(table);
        stage.addActor(buttonTable);
    }

    public void update(float dt) {
        player.setStickRotation(getControlStickAngle());

        if (attackHeldDown) {
            attackHeldDownTimer += dt;
        }
        if (attackHeldDownTimer > 0.175 && !hasActed) {
            if(player.getCurrentState() == Player.State.CHARGING_BOW){
                player.fireBow();
            }else {
                player.swingSword();
            }

            hasActed = true;
        }
        switch (player.getEquipedSpell()) {
            case FIREBALL:
                image.setDrawable(sword);
                image.setScale(0.65f);
                break;
            case SHIELD:
                image.setDrawable(shield);
                image.setScale(1f);
                break;
            case BOW:
                image.setDrawable(bow);
                image.setScale(0.65f);
                break;
            case NONE:
                image.setDrawable(null);
                break;
        }


        if (stopSpell) {
            for (FireSpell spell : screen.spells) {
                spell.stopCharging();
            }
        }
        if (!player.chargingSpell) {
            for (FireSpell spell : screen.spells) {
                spell.stopCharging();
            }
        }

        for (FireSpell spell : screen.spells) {
            if (spell.isFullyCharged()) {
                player.stopChargingAnimation();
            } else {
                player.startChargingAnimation();
            }
        }

    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Touchpad getTouchpadLeft() {
        return touchpadLeft;
    }

    public void dispose() {
        padBackTex.dispose();
        padKnobTex.dispose();
        stage.dispose();

    }

    public void enable() {
        image.setVisible(true);
        jumpButton.setVisible(true);
        touchpadRight.setVisible(true);
        touchpadLeft.setVisible(true);
    }

    public void handleInput() {
        float yVal = getTouchpadLeft().getKnobPercentY();
        float xVal = getTouchpadLeft().getKnobPercentX();
        if (player.canMove()) {
            if (stopSpell) {
                if (player.currentState != Player.State.DODGING) {
                    if (xVal > 0.25 && player.b2body.getLinearVelocity().x <= player.currentMaxSpeed) {
                        player.b2body.applyLinearImpulse(new Vector2(0.175f, 0), player.b2body.getWorldCenter(), true);
//                    player.b2body.setLinearVelocity(player.currentSpeed, player.b2body.getLinearVelocity().y);
                    } else if (xVal < -0.25 && player.b2body.getLinearVelocity().x >= -player.currentMaxSpeed) {
                        player.b2body.applyLinearImpulse(new Vector2(-0.175f, 0), player.b2body.getWorldCenter(), true);
//                    player.b2body.setLinearVelocity(-player.currentSpeed, player.b2body.getLinearVelocity().y);
                    } else {
                        player.b2body.setLinearVelocity(0, player.b2body.getLinearVelocity().y);
                    }
                } else {
                    if (player.runningRight) {
                        player.b2body.setLinearVelocity(player.dodgeSpeed, player.b2body.getLinearVelocity().y);

                    } else {
                        player.b2body.setLinearVelocity(-player.dodgeSpeed, player.b2body.getLinearVelocity().y);

                    }

                }

            }

            if (yVal < -0.75f) {

                player.setCrouching(true);

                player.dodgeEnable(true);
                if (player.getCurrentState() == Player.State.JUMPING) {
                    player.dodge();
                }
            } else {
                player.dodgeEnable(false);
                player.setCrouching(false);
            }


            if (yVal > -.65) {
                player.setInputPositiveY(true);
                player.setInputNegativeY(false);
            } else if (yVal <= -.65) {
                player.setInputPositiveY(false);
                player.setInputNegativeY(true);
            } else {
                player.setInputPositiveY(false);
                player.setInputNegativeY(false);
            }


            if (Gdx.input.justTouched()) {

            }


            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.jump();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= player.currentMaxSpeed) {
                player.b2body.setLinearVelocity(1.5f, player.b2body.getLinearVelocity().y);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -player.currentMaxSpeed) {
                player.b2body.setLinearVelocity(-1.5f, player.b2body.getLinearVelocity().y);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.attack();
            }
            if (xVal > 0 && player.currentState != Player.State.DODGING) {
                player.setRunningRight(true);
                player.setInputPositiveX(true);
                player.setInputNegativeX(false);
            } else if (xVal < 0 && player.currentState != Player.State.DODGING) {
                player.setRunningRight(false);
                player.setInputPositiveX(false);
                player.setInputNegativeX(true);
            } else {
                player.setInputPositiveX(false);
                player.setInputNegativeX(false);
            }
        }else if(player.canTurn()){
            if (xVal > 0 && player.currentState != Player.State.DODGING) {
                player.setRunningRight(true);
                player.setInputPositiveX(true);
                player.setInputNegativeX(false);
            } else if (xVal < 0 && player.currentState != Player.State.DODGING) {
                player.setRunningRight(false);
                player.setInputPositiveX(false);
                player.setInputNegativeX(true);
            } else {
                player.setInputPositiveX(false);
                player.setInputNegativeX(false);
            }
        }


    }

    public float getControlStickAngle(){
        Vector2 leftStickVector = new Vector2(
                getTouchpadLeft().getKnobPercentX(),
                getTouchpadLeft().getKnobPercentY()
        );
        return leftStickVector.angle();
    }
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchStartX = screenX;
        touchStartY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (touchStartX > 1200) {
            if (touchEndX < touchStartX - 200) {
                player.switchSpell();
            } else if (touchEndX > touchStartX + 200) {
                player.jump();
            }
            gestureStarted = false;
        }

        System.out.printf("END VALUE IS " + Integer.toString(touchEndX) + "\n");
        System.out.printf("START VALUE IS " + Integer.toString(touchStartX) + "\n");

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (screenX > 1200) {
            gestureStarted = true;
            touchEndX = screenX;
            touchEndY = screenY;
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
