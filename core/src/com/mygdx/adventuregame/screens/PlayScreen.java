package com.mygdx.adventuregame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.scenes.Hud;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.Explosion;
import com.mygdx.adventuregame.sprites.FireBall;
import com.mygdx.adventuregame.sprites.FireElemental;
import com.mygdx.adventuregame.sprites.FireSpell;
import com.mygdx.adventuregame.sprites.Kobold;
import com.mygdx.adventuregame.sprites.Minotaur;
import com.mygdx.adventuregame.sprites.Player;
import com.mygdx.adventuregame.sprites.Slime;
import com.mygdx.adventuregame.tools.B2WorldCreator;
import com.mygdx.adventuregame.tools.Controller;
import com.mygdx.adventuregame.tools.WorldContactListener;

import java.util.concurrent.TimeUnit;

public class PlayScreen implements Screen {
    Controller controller;
    private static final float PLAYER_MAX_SPEED = 1.5f;
    private AdventureGame game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Player player;

    private TextureAtlas atlas;
    public AssetManager assetManager;


    private Array<Enemy> enemyList;

    private Array<FireBall> fireBalls;
    private FireBall fireBall;

    public Array<FireBall> projectilesToSpawn;
    public Array<Explosion> explosions;
    public Array<FireSpell> spellsToSpawn;
    public Array<FireSpell> spells;
    public Array<DamageNumber> damageNumbersToAdd;
    public Array<DamageNumber> damageNumbers;

    private Stage stage;
    private DamageNumber damageNumber;

    public PlayScreen(AdventureGame game){
        assetManager = new AssetManager();
        assetManager.load("game_sprites.pack", TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get("game_sprites.pack", TextureAtlas.class);


        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(AdventureGame.V_WIDTH / AdventureGame.PPM, AdventureGame.V_HEIGHT / AdventureGame.PPM, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("forest_level.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / AdventureGame.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        stage = new Stage(gamePort, game.batch);

        //Creating collision bodies for the map
        //The vector2 is for gravity
        world = new World(new Vector2(0, -8), true);
        player = new Player(world, this);
        world.setContactListener(new WorldContactListener());
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map);
        enemyList = new Array<>();
        fireBalls = new Array<>();
        projectilesToSpawn = new Array<>();
        explosions = new Array<>();
        spellsToSpawn = new Array<>();
        spells = new Array<>();


        damageNumbers = new Array<>();
        damageNumbersToAdd = new Array<>();
//        enemyList.add(new Slime(this, 1.72f, 0.32f));
//        enemyList.add(new Slime(this, 2.72f, 0.32f));
//        enemyList.add(new Slime(this, 3.72f, 0.32f));
//        enemyList.add(new Minotaur(this, 2.25f, 2.32f));

//        enemyList.add(new FireElemental(this, 2.25f, 0.32f));


        controller = new Controller(game.batch, this);
        controller.enable();


    }

    public void update(float dt){
        controller.handleInput();
        controller.update();
//        handleInupt(dt);
        world.step(1/60f, 6, 2);
        player.update(dt);
        for(Enemy enemy : enemyList){
            enemy.update(dt);
        }
        for(FireBall fireBall : fireBalls){
            fireBall.update(dt);
        }
        for (FireSpell spell : spells){
            spell.update(dt);
        }
        for(DamageNumber number : damageNumbers){
            number.update(dt);
        }
        for(Explosion explosion : explosions){
            explosion.update(dt);
        }

        hud.setScore(player.getHealth());


        //Camera tracks player
        //Todo camera tracking hysteris
        // track the maximum x position of player      ------|-x-|----
        // if player goes backwards                    ------|x--|----
        // dont update camera position until it passes ------x|-------
        // set a new camera threshold                  ----|-x-|------
        gameCam.position.x = player.b2body.getPosition().x;
        float ypos = player.b2body.getPosition().y;
        if(ypos > 1f || ypos < -1f){
            gameCam.position.y = player.b2body.getPosition().y -  0.6117f / 2;
        }else{
            gameCam.position.y = 0.6117f;
        }
        gameCam.update();
        renderer.setView(gameCam);
    }

    private void handleInupt(float dt) {
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
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        //  Clear the screen
        Gdx.gl.glClearColor(1, 0 , 0 , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();

//        b2dr.render(world, gameCam.combined);
        //Set to render only what camera can see
        game.batch.setProjectionMatrix(gameCam.combined);


        game.batch.begin();




        if(enemyList.size > 0){
            for(Enemy enemy : enemyList){
//                if(enemy.isHurt()){
//                    game.batch.setShader(shader);
//                }else {
//                    game.batch.setShader(null);
//                }
                enemy.draw(game.batch);
            }
        }
        if(fireBalls.size > 0){
            for(FireBall fireBall : fireBalls){
                fireBall.draw(game.batch);
            }
        }

        for(DamageNumber number : damageNumbers){
            if(!number.isForPlayer()){
                game.batch.setShader(shader);
            }
            number.draw(game.batch);
            game.batch.setShader(null);
        }
//        game.batch.setShader(shader);
//        game.batch.setShader(null);

        player.draw(game.batch);

        for (Explosion explosion : explosions){
            explosion.draw(game.batch);
        }
        for(FireSpell spell : spells){
            spell.draw(game.batch);
        }

        game.batch.setShader(null);
        game.batch.end();





        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        stage.draw();

        hud.stage.draw();


        if(enemyList.isEmpty()){
            enemyList.add(new Slime(this, 3.72f, 2.32f));
            enemyList.add(new Slime(this, 3.2f, 2.32f));
            enemyList.add(new Slime(this, 3.3f, 2.32f));
            enemyList.add(new FireElemental(this, 3.5f, 2.32f));

            enemyList.add(new Minotaur(this, 2.25f, 2.32f));
            enemyList.add(new Kobold(this, 1.85f, 2.32f));
            enemyList.add(new Kobold(this, 2.05f, 2.32f));
            enemyList.add(new Kobold(this, 2.9f, 2.32f));


        }
        if(!projectilesToSpawn.isEmpty()){
            for(FireBall fireBall : projectilesToSpawn){
                fireBalls.add(fireBall);
                projectilesToSpawn.removeValue(fireBall, true);
            }
        }
        if(!spellsToSpawn.isEmpty()){
            for(FireSpell spell : spellsToSpawn){
                spells.add(spell);
                spellsToSpawn.removeValue(spell, true);
            }
        }
        if(!damageNumbersToAdd.isEmpty()){
            for(DamageNumber number : damageNumbersToAdd){
                damageNumbers.add(number);
                damageNumbersToAdd.removeValue(number, true);
            }
        }

        for(Enemy enemy : enemyList){
            if(enemy.safeToRemove){
                enemyList.removeValue(enemy, true);
            }
        }

        for(FireBall fireBall : fireBalls){
            if(fireBall.safeToRemove){
                fireBalls.removeValue(fireBall, true);
            }
        }
        for(FireSpell fireSpell : spells){
            if(fireSpell.safeToRemove){
                spells.removeValue(fireSpell, true);
            }
        }
        for(DamageNumber number : damageNumbers){
            if(number.safeToRemove){
                damageNumbers.removeValue(number, true);
            }
        }

        controller.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        controller.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        controller.dispose();
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public World getWorld() {
        return world;
    }
    public Player getPlayer(){
        return player;
    }

    public Array<Explosion> getExplosions(){
        return explosions;
    }
    public Array<DamageNumber> getDamageNumbersToAdd(){
        return damageNumbersToAdd;
    }

    String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "uniform mat4 u_projTrans;\n" //
            + "varying vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "\n" //
            + "void main()\n" //
            + "{\n" //
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "}\n";
    String fragmentShader = "#ifdef GL_ES\n" //
            + "#define LOWP lowp\n" //
            + "precision mediump float;\n" //
            + "#else\n" //
            + "#define LOWP \n" //
            + "#endif\n" //
            + "varying LOWP vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "uniform sampler2D u_texture;\n" //
            + "void main()\n"//
            + "{\n" //
            + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords).a;\n" //
            + "}";

    ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);

    public Array<FireSpell> getSpells(){
        return spells;
    }
}
