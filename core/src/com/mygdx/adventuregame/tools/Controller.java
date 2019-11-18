package com.mygdx.adventuregame.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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


public class Controller {
    Viewport viewport;
    Stage stage;
    OrthographicCamera cam;

    private Touchpad touchpadLeft;
    private Touchpad touchpadRight;

    Texture padKnobTex;
    Texture padBackTex;


    private static final float SCALE = 0.65f;

    public boolean touchDown;
    private Image image;

    private PlayScreen screen;

    private Player player;

    private static final float PLAYER_MAX_SPEED = 1.5f;

    public Controller(SpriteBatch batch, PlayScreen screen){
        this.screen = screen;
        this.player = screen.getPlayer();


        cam = new OrthographicCamera();

        viewport = new ExtendViewport(AdventureGame.V_WIDTH, AdventureGame.V_HEIGHT, cam);
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

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
        table.pad(10);
        table.padBottom(10);
        table.add(touchpadLeft).expandX();
        table.add().expandX();
        table.add().expandX();
        table.add().expandX();
        table.add(touchpadRight).expandX();

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

    public Touchpad getTouchpadRight() {
        return touchpadRight;
    }

    public void dispose(){
        padBackTex.dispose();
        padKnobTex.dispose();
        stage.dispose();

    }

    public void enable(){
        Gdx.input.setInputProcessor(stage);
        touchpadRight.setVisible(true);
        touchpadLeft.setVisible(true);
    }

    public void handleInput(){
        Vector2 rightStickVector = new Vector2(getTouchpadRight().getKnobPercentX(),
                getTouchpadRight().getKnobPercentY()
        );
        if(rightStickVector.len() > 0){
        }else{

        }

        float length = rightStickVector.len();

        Vector2 leftStickVector = new Vector2(
                getTouchpadLeft().getKnobPercentX(),
                getTouchpadLeft().getKnobPercentY()
        );

        float xVal = getTouchpadLeft().getKnobPercentX();
        if ( xVal > 0  && player.b2body.getLinearVelocity().x <= PLAYER_MAX_SPEED) {

            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if(xVal < 0 && player.b2body.getLinearVelocity().x >= -PLAYER_MAX_SPEED){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

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

}
