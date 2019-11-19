package com.mygdx.adventuregame.tools;

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
import com.mygdx.adventuregame.sprites.Player;


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

    private static final float PLAYER_MAX_SPEED = 1.5f;

    int touchStartX;
    int touchStartY;

    int touchEndX;
    int touchEndY;

    boolean gestureStarted = false;

    public Controller(SpriteBatch batch, PlayScreen screen){
        this.screen = screen;
        this.player = screen.getPlayer();
        TextureRegionDrawable button = new TextureRegionDrawable(new TextureRegion(screen.getAtlas().findRegion("fire_elemental_idle"),
                        0, 0, 62, 43));

        image = new Image();
        image.setSize(62 * SCALE,43* SCALE);
        image.setVisible(false);
        image.setDrawable(button);
        image.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                player.castSpell();
                buttonClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonClicked = false;
                touchDown = false;
            }
        });

        jumpButton = new Image();
        jumpButton.setSize(62 * SCALE,43 * SCALE);
        jumpButton.setVisible(false);
        jumpButton.setDrawable(button);
        jumpButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                player.jump();
                buttonClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonClicked = false;
                touchDown = false;
            }
        });

        cam = new OrthographicCamera();

        viewport = new ExtendViewport(AdventureGame.V_WIDTH, AdventureGame.V_HEIGHT, cam);
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        stage = new Stage(viewport, batch);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
//        Gdx.input.setInputProcessor(stage);

        Touchpad.TouchpadStyle touchStyle = new Touchpad.TouchpadStyle();
        padKnobTex = new Texture(Gdx.files.internal("controller_inner.png"));
        TextureRegion padKnobReg = new TextureRegion(padKnobTex);
        touchStyle.knob = new TextureRegionDrawable(padKnobReg);
        padBackTex = new Texture(Gdx.files.internal("controller_outer.png"));
        TextureRegion padBackReg = new TextureRegion(padBackTex);
        touchStyle.background = new TextureRegionDrawable(padBackReg);
        touchpadLeft = new Touchpad(5, touchStyle);
        touchpadLeft.setVisible(false);
        touchpadRight = new Touchpad(5, touchStyle);
        touchpadRight.setVisible(false);

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.toFront();
        table.padLeft(5);
        table.padBottom(10);
        table.add(touchpadLeft).expandX();
        table.add().expandX();
        table.add().expandX();
        table.add(image).size(image.getWidth(), image.getHeight());
        table.add(jumpButton).size(jumpButton.getWidth(), jumpButton.getHeight()).expandX();
//        table.add(touchpadRight).expandX();


        touchpadLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });
        stage.addActor(table);
    }

    public void update(){
    }

    public void draw(){
        stage.draw();
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }

    public Touchpad getTouchpadLeft() {
        return touchpadLeft;
    }

    public void dispose(){
        padBackTex.dispose();
        padKnobTex.dispose();
        stage.dispose();

    }

    public void enable(){
        image.setVisible(true);
        jumpButton.setVisible(true);
        touchpadRight.setVisible(true);
        touchpadLeft.setVisible(true);
    }

    public void handleInput(){
        float xVal = getTouchpadLeft().getKnobPercentX();
        if ( xVal > 0  && player.b2body.getLinearVelocity().x <= PLAYER_MAX_SPEED) {

            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if(xVal < 0 && player.b2body.getLinearVelocity().x >= -PLAYER_MAX_SPEED){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }


        if(Gdx.input.justTouched()) {
            if(!buttonClicked){
                player.attack();
            }
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            player.jump();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= PLAYER_MAX_SPEED){
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -PLAYER_MAX_SPEED){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            player.attack();
        }
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
            if(touchEndX < touchStartX -50){
                player.castSpell();
            }else if(touchEndX > touchStartX +50){
                player.jump();
            }
        gestureStarted = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        gestureStarted = true;
        touchEndX = screenX;
        touchEndY = screenY;
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