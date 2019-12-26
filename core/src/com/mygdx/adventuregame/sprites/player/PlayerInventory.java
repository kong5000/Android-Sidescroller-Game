package com.mygdx.adventuregame.sprites.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.adventuregame.AdventureGame;

public class PlayerInventory {
    private Player player;
    private TextureRegion bowDialog;
    private TextureRegion fireSpellDialog;
    private TextureRegion doubleJumpDialog;
    private TextureRegion protectionRingDialog;
    private TextureRegion swordDialog;
    private TextureAtlas textureAtlas;
    public boolean hasBow = true;
    public boolean hasFireSpell = true;

    public PlayerInventory(Player player) {
        this.player = player;

        textureAtlas = player.getTextureAtlas();
        bowDialog = new TextureRegion(textureAtlas.findRegion("bow_dialog"), 0, 0, 450, 300);
        fireSpellDialog = new TextureRegion(textureAtlas.findRegion("fire_spell_dialog"), 0, 0, 450, 300);
        doubleJumpDialog = new TextureRegion(textureAtlas.findRegion("double_jump_dialog"), 0, 0, 450, 300);
        protectionRingDialog = new TextureRegion(textureAtlas.findRegion("protection_ring_dialog"), 0, 0, 450, 300);
        swordDialog = new TextureRegion(textureAtlas.findRegion("sword_dialog"), 0, 0, 450, 300);

    }



    public TextureRegion getItemDialog(int id) {
        switch (id) {
            case AdventureGame.BOW:
                return bowDialog;
            case AdventureGame.FIRE_SPELLBOOK:
                return fireSpellDialog;
            case AdventureGame.RING_OF_DOUBLE_JUMP:
                return doubleJumpDialog;
            case AdventureGame.RING_OF_PROTECTION:
                return protectionRingDialog;
            case AdventureGame.SWORD:
                return swordDialog;
            default:
                return swordDialog;
        }
    }

}
