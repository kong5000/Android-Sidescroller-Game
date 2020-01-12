package com.mygdx.adventuregame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adventuregame.AdventureGame;
import com.mygdx.adventuregame.items.Item;
import com.mygdx.adventuregame.scenes.Hud;
import com.mygdx.adventuregame.sprites.Cage;
import com.mygdx.adventuregame.sprites.CheckPoint;
import com.mygdx.adventuregame.sprites.DamageNumber;
import com.mygdx.adventuregame.sprites.Enemies.Kobold;
import com.mygdx.adventuregame.sprites.Enemies.RedOgre;
import com.mygdx.adventuregame.sprites.Enemies.Satyr;
import com.mygdx.adventuregame.sprites.Enemy;
import com.mygdx.adventuregame.sprites.Effects.Explosion;
import com.mygdx.adventuregame.sprites.Projectiles.FireBall;

import com.mygdx.adventuregame.sprites.FireSpell;
import com.mygdx.adventuregame.sprites.HealthBar;
import com.mygdx.adventuregame.sprites.MonsterTile;
import com.mygdx.adventuregame.sprites.player.Player;
import com.mygdx.adventuregame.sprites.UpdatableSprite;
import com.mygdx.adventuregame.tools.B2WorldCreator;
import com.mygdx.adventuregame.sprites.player.Controller;
import com.mygdx.adventuregame.tools.WorldContactListener;

import java.util.Iterator;

public class PlayScreen implements Screen {
    private Sprite background;
    private Sprite backgroundFar;
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

    private Array<CheckPoint> checkPoints;
    private Array<Enemy> enemyList;
    private Array<FireBall> fireBalls;
    public Array<FireBall> projectilesToSpawn;
    public Array<Explosion> explosions;
    public Array<Explosion> explosionsToAdd;
    public Array<FireSpell> spellsToSpawn;
    public Array<FireSpell> spells;
    public Array<DamageNumber> damageNumbersToAdd;
    public Array<DamageNumber> damageNumbers;
    public Array<HealthBar> healthBarsToAdd;
    public Array<HealthBar> healthBars;
    public Array<MonsterTile> monsterTiles;

    private Array<UpdatableSprite> spritesToAdd;
    private Array<UpdatableSprite> sprites;

    private Array<UpdatableSprite> topLayerSpritesToAdd;
    private Array<UpdatableSprite> topLayerSprites;

    private Stage stage;
    private Color fadeScreenColor = Color.BLACK;
    private float fadeTickTimer = 0;
    private float fadeScreenAlpha = 0;
    private B2WorldCreator worldCreator;
    private ShapeRenderer shapeRenderer;
    private boolean tearDownComplete = true;

    public PlayScreen(AdventureGame game){
        assetManager = new AssetManager();
        assetManager.load("game_sprites.pack", TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get("game_sprites.pack", TextureAtlas.class);

        Texture bgTexture = new Texture("temple_bg.png");
        background = new Sprite(bgTexture);

//        Texture bgTexture = new Texture("BackgroundLong.png");
//        background = new Sprite(bgTexture);
//        Texture bgTextureFar = new Texture("BackgroundCloud.png");
//        backgroundFar = new Sprite(bgTextureFar);




        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(AdventureGame.V_WIDTH / AdventureGame.PPM, AdventureGame.V_HEIGHT / AdventureGame.PPM, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.textureMinFilter = Texture.TextureFilter.Nearest;


//        map = mapLoader.load("forest_castle.tmx");
//        map = mapLoader.load("forest_castle_1.tmx", params);
        map = mapLoader.load("dungeon_1.tmx", params);
//        map = mapLoader.load("temple.tmx", params);



        Iterator<TiledMapTileSet> iter = map.getTileSets().iterator();
        while(iter.hasNext())
        {
            Iterator<TiledMapTile> iterTile = iter.next().iterator();

            while(iterTile.hasNext())
            {
                iterTile.next().getTextureRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        }

        renderer = new OrthogonalTiledMapRenderer(map, 1 / AdventureGame.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        stage = new Stage(gamePort, game.batch);

        //Creating collision bodies for the map
        //The vector2 is for gravity
        world = new World(new Vector2(0, -8), true);
        player = new Player(world, this);
        world.setContactListener(new WorldContactListener());
        b2dr = new Box2DDebugRenderer();

        enemyList = new Array<>();
        fireBalls = new Array<>();
        projectilesToSpawn = new Array<>();
        explosions = new Array<>();
        explosionsToAdd = new Array<>();
        spellsToSpawn = new Array<>();
        spells = new Array<>();

        checkPoints = new Array<>();

        damageNumbers = new Array<>();
        damageNumbersToAdd = new Array<>();

        healthBars = new Array<>();
        healthBarsToAdd = new Array<>();

        monsterTiles = new Array<>();

        controller = new Controller(game.batch, this);
        controller.enable();

        sprites = new Array<>();
        spritesToAdd = new Array<>();

        topLayerSprites = new Array<>();
        topLayerSpritesToAdd = new Array<>();

        worldCreator = new B2WorldCreator(world, map, this);

//        sprites.add(new Item(this, 5f, 5f, 1));

        shapeRenderer = new ShapeRenderer();


        spritesToAdd.add(new Item(this, 2.5f, 8f,AdventureGame.GOLD_COIN));
//        enemyList.add(new RedOgre(this, 4, 5.2f));
//        enemyList.add(new Slug(this, 4, 7f));
//        enemyList.add(new Slug(this, 5, 7f));
//        checkPoints.add(new CheckPoint(this, 5, 5));

    }

    public void update(float dt){
        if(!tearDownComplete){
            for(CheckPoint checkPoint : checkPoints){
                checkPoint.destroy();
                checkPoints.removeValue(checkPoint, true);
            }
            worldCreator.destroyBodies();
            if(worldCreator.tearDownComplete()){
                tearDownComplete = true;
            }
            if(tearDownComplete){
                worldCreator = new B2WorldCreator(world, map, this);
            }
        }
        controller.handleInput();
        controller.update(dt);
        world.step(1/60f, 6, 2);
        player.update(dt);
        background.setPosition(player.b2body.getPosition().x * -10, player.b2body.getPosition().y * - 5);
        if(backgroundFar != null){
            backgroundFar.setPosition(player.b2body.getPosition().x * -5, player.b2body.getPosition().y * - 5);
        }
        for(UpdatableSprite sprite : sprites){
            sprite.update(dt);
        }
        for(UpdatableSprite sprite : topLayerSprites){
            sprite.update(dt);
        }
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
        for(HealthBar bar : healthBars){
            bar.update(dt);
        }
        for(Explosion explosion : explosions){
            explosion.update(dt);
        }
        for(MonsterTile monsterTile : monsterTiles){
            monsterTile.update(dt);
        }

        hud.setScore(player.getHealth());
        hud.setExperience((int)player.getMana());


        //Camera tracks player
        //Todo camera tracking hysteris
        // track the maximum x position of player      ------|-x-|----
        // if player goes backwards                    ------|x--|----
        // dont update camera position until it passes ------x|-------
        // set a new camera threshold                  ----|-x-|------

//        gameCam.position.x = player.b2body.getPosition().x;
        //        gameCam.position.y = player.b2body.getPosition().y + 0.22f;

        gameCam.position.x = Math.round(player.b2body.getPosition().x * 575f) / 575f;
        gameCam.position.y = Math.round(player.b2body.getPosition().y * 575f) /575f + 0.4f;
//        float ypos = player.b2body.getPosition().y + 0.25f;
//        if(ypos > 5){
//            gameCam.position.y = ypos;
//        }else{
//            gameCam.position.y = 4.5f;
//        }
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
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            player.setCanPassFloor(true);
        }else {
            player.setCanPassFloor(false);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        //  Clear the screen
        Gdx.gl.glClearColor(0, 0, 0 , 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        if(backgroundFar != null){
            backgroundFar.draw(game.batch);
        }
        background.draw(game.batch);

        game.batch.end();
        renderer.render();

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        if(enemyList.size > 0){
            for(Enemy enemy : enemyList){
                enemy.draw(game.batch);
            }
        }
        if(fireBalls.size > 0){
            for(FireBall fireBall : fireBalls){
                fireBall.draw(game.batch);
            }
        }
        for(UpdatableSprite sprite : sprites){
            sprite.draw(game.batch);
        }
        for(DamageNumber number : damageNumbers){
            if(!number.isForPlayer()){
                game.batch.setShader(shader);
            }
            number.draw(game.batch);
            game.batch.setShader(null);
        }
        for(HealthBar bar : healthBars){
            bar.draw(game.batch);
        }

        for (Explosion explosion : explosions){
            explosion.draw(game.batch);
        }
        for(FireSpell spell : spells){
            spell.draw(game.batch);
        }
        for(MonsterTile monsterTile : monsterTiles){
            monsterTile.draw(game.batch);
        }


        game.batch.end();

        if(player.getCurrentState() == Player.State.DYING ){
            fadeTickTimer += delta;
            if(fadeTickTimer < 10000){
                fadeTickTimer = 0;
                if(!player.doneDying()){
                    if(fadeScreenAlpha <= 0.993){
                        fadeScreenAlpha += 0.007f;
                    }
                }else {
                    if(fadeScreenAlpha >= 0.00701){
                        fadeScreenAlpha -= 0.007f;
                    }
                }
                fadeScreenColor.a = fadeScreenAlpha;
            }
        }

        if(player.getCurrentState() != Player.State.DYING){
            fadeScreenColor.a = 0f;
            fadeScreenAlpha = 0f;
        }



        // Draw the filled rectangle
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(fadeScreenColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, AdventureGame.V_WIDTH * AdventureGame.PPM, AdventureGame.V_HEIGHT * AdventureGame.PPM);
        shapeRenderer.end();
        b2dr.render(world, gameCam.combined);
        //Set to render only what camera can see
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();



//        game.batch.setShader(shader);
//        game.batch.setShader(null);
        player.draw(game.batch);
        for(UpdatableSprite sprite : topLayerSprites){
            sprite.draw(game.batch);
        }


        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        stage.draw();

        hud.stage.draw();


        if(enemyList.isEmpty()){
//            enemyList.add(new Slime(this, 3.5f, 5f));
//            FireElemental fireElemental = new FireElemental(this, 10f, 6f);
//            enemyList.add(fireElemental);
//            Minotaur minotaur = new Minotaur(this, 9, 6f);
//            enemyList.add(new Minotaur(this, 12, 6f));
//            enemyList.add(new Minotaur(this, 12, 6f));
//            healthBarsToAdd.add(new HealthBar(this, 0, 0, 2, fireElemental));
//            healthBarsToAdd.add(new HealthBar(this, 0, 0, 2, minotaur));
//
////            healthBarsToAdd.add(new HealthBar(this, 0, 0, 2, minotaur));
//            enemyList.add(minotaur);
//            enemyList.add(new Kobold(this, 12, 6f));
//            enemyList.add(new Kobold(this, 15, 6f));


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
        if(!spritesToAdd.isEmpty()){
            for(UpdatableSprite sprite : spritesToAdd){
                sprites.add(sprite);
                spritesToAdd.removeValue(sprite, true);
            }
        }

        if(!topLayerSpritesToAdd.isEmpty()){
            for(UpdatableSprite sprite : topLayerSpritesToAdd){
                topLayerSprites.add(sprite);
                topLayerSpritesToAdd.removeValue(sprite, true);
            }
        }

        if(!healthBarsToAdd.isEmpty()){
            for(HealthBar bar : healthBarsToAdd){
                healthBars.add(bar);
                healthBarsToAdd.removeValue(bar, true);
            }
        }

        if(!explosionsToAdd.isEmpty()){
            for(Explosion explosion : explosionsToAdd){
                explosions.add(explosion);
                explosionsToAdd.removeValue(explosion, true);
            }
        }
        for(UpdatableSprite sprite : sprites){
            if(sprite.safeToRemove()){
                sprites.removeValue(sprite, true);
            }
        }
        for(UpdatableSprite sprite : topLayerSprites){
            if(sprite.safeToRemove()){
                topLayerSprites.removeValue(sprite, true);
            }
        }
        for(HealthBar healthBar : healthBars){
            if(healthBar.safeToRemove){
                healthBars.removeValue(healthBar, true);
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

        for(HealthBar bar: healthBars){
            if(bar.safeToRemove){
                healthBars.removeValue(bar, true);
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
        shapeRenderer.dispose();
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
    public Array<Explosion> getExplosionsToAdd(){
        return explosionsToAdd;
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
    public Array<Enemy> getEnemyList(){
        return enemyList;
    }
    public Array<HealthBar> getHealthBars(){
        return healthBars;
    }
    public Array<HealthBar> getHealthBarsToAdd(){
        return healthBarsToAdd;
    }
    public Array<UpdatableSprite> getSpritesToAdd(){ return spritesToAdd;}
    public Array<UpdatableSprite> getTopLayerSpritesToAdd(){ return topLayerSpritesToAdd;}
    public Array<UpdatableSprite> getTopLayerSprites(){ return topLayerSprites;}
    public Array<CheckPoint> getCheckPoints(){return checkPoints;}


    public void changeMap() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                tearDownComplete = false;
                renderer.getMap().dispose(); //dispose the old map
                map = mapLoader.load("forest_castle_1.tmx"); //load the new map
                renderer.setMap(map); //set the map in your renderer
                removeEntities();

                Texture bgTexture = new Texture("BackgroundLong.png");
                background = new Sprite(bgTexture);
                Texture bgTextureFar = new Texture("BackgroundCloud.png");
                backgroundFar = new Sprite(bgTextureFar);
//
            }
        });

    }

    private void removeEntities(){
        for(Enemy enemy : enemyList){
            enemy.setToDestroy();
//            enemyList.removeValue(enemy, true);
        }
        for(UpdatableSprite sprite : sprites){
            sprite.setToDestroy();
//            sprites.removeValue(sprite, true);
        }

    }
}
