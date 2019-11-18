package com.mygdx.adventuregame.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adventuregame.AdventureGame;

public class Hud implements Disposable {
    public Stage stage;
    //Need a new viewport that is stationary
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label worldLabel;

    public Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(AdventureGame.V_WIDTH, AdventureGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        Table table = new Table();
        table.top();
        table.setFillParent(true); // table is now the size of stage
        countdownLabel = new Label(
                String.format("%03d", worldTimer),
                new Label.LabelStyle(new BitmapFont(),
                        Color.WHITE));

        scoreLabel = new Label(
                String.format("%06d", score),
                new Label.LabelStyle(new BitmapFont(),
                        Color.WHITE));
        timeLabel = new Label(
                "TIME",
                new Label.LabelStyle(new BitmapFont(),
                        Color.WHITE));
        worldLabel = new Label(
                "LEVEL 1",
                new Label.LabelStyle(new BitmapFont(),
                        Color.WHITE));

        table.add(scoreLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(countdownLabel).expandX().padTop(10);

        stage.addActor(table);

    }


    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setScore(int value){
        score = value;
        scoreLabel.setText(String.format("%03d", score));
    }
}
