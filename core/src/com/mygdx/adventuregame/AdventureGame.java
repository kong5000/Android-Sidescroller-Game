package com.mygdx.adventuregame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.adventuregame.screens.PlayScreen;

public class AdventureGame extends Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 200;
	public static final float PPM = 100;
	//Memory intensive, made public so different screen can have access
	public SpriteBatch batch;
	public static final int HEALTHBAR_WIDTH = 225;
	public static final int HEALTHBAR_HEIGHT = 36;


	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short ENEMY_BIT = 4;
	public static final short ENEMY_HEAD_BIT = 8;
	public static final short PLAYER_SWORD_BIT = 16;
	public static final short ENEMY_ATTACK_BIT = 32;
	public static final short ENEMY_PROJECTILE_BIT = 64;
	public static final short PLAYER_PROJECTILE_BIT = 128;
	public static final short FIRE_SPELL_BIT= 256;
	public static final short PLATFORM_BIT= 512;
	public static final short SPIKE_BIT= 1024;


	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render(); //delegate to the PlayScreen
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
