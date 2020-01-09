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

	public static final int BOW = 1;
	public static final int FIRE_SPELLBOOK = 2;
	public static final int RING_OF_DOUBLE_JUMP = 3;
	public static final int SMALL_HEALTH = 4;
	public static final int MEDIUM_HEALTH = 5;
	public static final int LARGE_HEALTH = 6;
	public static final int SWORD = 7;
	public static final int RING_OF_REGENERATION = 8;
	public static final int RING_OF_PROTECTION = 9;
	public static final int GOLD_COIN = 10;

	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short ENEMY_BIT = 4;
	public static final short ENEMY_HEAD_BIT = 8;
	public static final short PLAYER_SWORD_BIT = 16;
	public static final short ENEMY_ATTACK_BIT = 32;
	public static final short ENEMY_PROJECTILE_BIT = 64;
	public static final short PLAYER_PROJECTILE_BIT = 128;
	public static final short FIRE_SPELL_BIT= 256;
	public static final short PLATFORM_BIT= -32;
	public static final short ENVIRONMENT_SENSOR_BIT= 512;
	public static final short SPIKE_BIT= 1024;
	public static final short ITEM_BIT= 2048;
	public static final short BOSS_ATTACK_BIT = -1;
	public static final short WALL_RUN_BIT = -4;
	public static final short MOVING_BLOCK_BIT= -8;
	public static final short MOVING_BLOCK_SENSOR= -16;

	public static final float BOW_LOCATION = 2544f;
	public static final float RING_OF_PROTECTION_LOCATION = 2848f;
	public static final float SWORD_LOCATION = 4224f;
	public static final float DOUBLE_JUMP_LOCATION = 5792f;
	public static final float RING_OF_REGEN_LOCATION = 7472f;
	public static final float FULL_HEALTH_LOCATION_1 = 7904f;
	public static final float SWORD_LOCATION_2 = 8656f;
	public static final float BOW_LOCATION_2 = 8800f;


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
