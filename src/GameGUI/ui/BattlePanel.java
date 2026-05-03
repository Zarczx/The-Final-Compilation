package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData.HeroDefinition;
import GameGUI.model.entity.HeroData.EnemyDefinition;
import GameGUI.model.HeroFactory;
import GameGUI.logic.BattleManager;
import GameGUI.logic.ProgressionService;

import GameGUI.model.equipment.Armor;
import GameGUI.model.equipment.Bow;
import GameGUI.model.equipment.Staff;
import GameGUI.model.equipment.Sword;
import GameGUI.model.equipment.Weapon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BattlePanel extends JPanel {

    private Runnable onReturnToSelection;
    private Runnable onRestartBattle;
    private BiConsumer<Integer, Runnable> onEnemyGroupDefeated;

    private BattleManager engine;
    private Combatant currentHero;
    private Combatant currentEnemy;
    private HeroDefinition heroDef;
    private EnemyDefinition enemyDef;
    private boolean animating = false;

    private JLabel battleBg;
    private JLabel heroConditionLabel;
    private JLabel enemyConditionLabel;

    // ════════════════════════════════════════════
    // ★ HERO SPRITE FIELDS
    // ════════════════════════════════════════════
    private JLabel heroSpriteLabel;
    private javax.swing.Timer heroIdleTimer;
    private int heroSpriteFrame = 0;

    private static final int SPRITE_FRAME_COUNT = 2;
    private static final int BLADE_RUSH_FRAME_COUNT = 6;
    private static final int PIERCING_SLASH_FRAME_COUNT = 8;
    private static final int ETERNAL_CROSS_FRAME_COUNT = 8;
    private static final int KAEL_HURT_FRAME_COUNT = 2;


    private static final int SPRITE_W = 80;
    private static final int SPRITE_H = 64;
    private static final double SPRITE_SCALE = 1.5;
    private static final int SHADE_X = 900;
    private static final int SHADE_Y = 340;
    private String currentBattleBgPath = "";


    private BufferedImage[] idleFrames;
    private BufferedImage[] bladeRushFrames;
    private BufferedImage[] piercingSlashFrames;
    private BufferedImage[] eternalCrossFrames;
    private BufferedImage[] kaelHurtFrames;

    private boolean isPlayingBladeRush = false;
    private boolean isPlayingPiercingSlash = false;
    private boolean isPlayingEternalCross = false;
    private boolean isPlayingKaelHurt = false;

    // Karl sprite constants
    private static final double KARL_SPRITE_SCALE = 1.5; // tune this value down, e.g. 1.2
    private static final int KARL_IDLE_FRAME_COUNT = 8;
    private static final int KARL_HURT_FRAME_COUNT = 8;
    private static final int KARL_PIERCING_SHOT_FRAME_COUNT = 8; // adjust to actual frame count
    private static final int KARL_BULLSEYE_FRAME_COUNT = 8;
    private static final int KARL_RAIN_FRAME_COUNT = 8;

    private BufferedImage[] karlIdleFrames;
    private BufferedImage[] karlHurtFrames;
    private BufferedImage[] karlPiercingShotFrames;
    private BufferedImage[] karlBullseyeFrames;
    private BufferedImage[] karlRainFrames;

    private boolean isPlayingKarlHurt = false;
    private boolean isPlayingKarlPiercingShot = false;
    private boolean isPlayingKarlBullseye = false;
    private boolean isPlayingKarlRain = false;

    // ════════════════════════════════════════════
    // ★ ENEMY SPRITE FIELDS
    // ════════════════════════════════════════════
    private JLabel enemySpriteLabel;
    private javax.swing.Timer enemyIdleTimer;
    private int enemySpriteFrame = 0;

    // Wolf
    private BufferedImage[] wolfIdleFrames;
    private BufferedImage[] wolfHurtFrames;
    private BufferedImage[] wolfSavageHowlFrames;
    private BufferedImage[] wolfDefeatFrames;
    private BufferedImage[] wolfEntranceFrames;

    private static final int WOLF_FRAME_COUNT = 5;
    private static final int WOLF_HURT_FRAME_COUNT = 2;
    private static final int WOLF_HOWL_FRAME_COUNT = 4;
    private static final int WOLF_DEFEAT_FRAME_COUNT = 4;
    private static final int WOLF_ENTRANCE_FRAME_COUNT = 5;
    private static final int WOLF_SPEED = 170;
    private static final double ENEMY_SCALE = 1.5;
    private static final int ENEMY_Y = 365;
    private static final int ENEMY_X = 900;
    private static final int WOLF_ENTRANCE_START_X = 1100;

    private boolean isPlayingWolfHurt = false;
    private boolean isPlayingWolfSavageHowl = false;
    private boolean isPlayingWolfDefeat = false;
    private boolean isPlayingWolfEntrance = false;

    // Shade Sprite
    private BufferedImage[] spriteIdleFrames;
    private BufferedImage[] spriteHurtFrames;
    private BufferedImage[] spriteTricksterFrames;
    private BufferedImage[] spriteDefeatFrames;
    private BufferedImage[] spriteEntranceFrames;

    private static final int SPRITE_IDLE_COUNT = 5;
    private static final int SPRITE_HURT_COUNT = 5;
    private static final int SPRITE_TRICKSTER_COUNT = 5;
    private static final int SPRITE_DEFEAT_COUNT = 5;
    private static final int SPRITE_ENTRANCE_COUNT = 5;
    private static final int SPRITE_ENTRANCE_SPEED = 3000; // ADD THIS

    private boolean isPlayingSpriteHurt = false;
    private boolean isPlayingSpriteTrickster = false;
    private boolean isPlayingSpriteDefeat = false;
    private boolean isPlayingSpriteEntrance = false;

    //Dreadbark Treant
    private static final int TREANT_IDLE_FRAME_COUNT = 4;
    private static final int TREANT_HURT_FRAME_COUNT = 4;
    private static final int TREANT_ATTACK_FRAME_COUNT = 4;
    private static final int TREANT_ENTRANCE_FRAME_COUNT = 4;
    private static final int TREANT_SPEED = 250;
    private static final int TREANT_ENTRANCE_START_X = 950; // was 850
    private static final int TREANT_X = ENEMY_X - 90;  // same value as STAG_X
    private static final int TREANT_Y = ENEMY_Y - 110;  // same value as STAG_Y
    private static final double TREANT_SCALE = 0.57; // entrance only
    private static final double TREANT_IDLE_SCALE = 0.40; // idle, hurt, attack
    private static final double TREANT_ATTACK_SCALE = 0.65; // attack - tune this

    private BufferedImage[] treantIdleFrames;
    private BufferedImage[] treantHurtFrames;
    private BufferedImage[] treantAttackFrames;
    private BufferedImage[] treantEntranceFrames;

    private boolean isPlayingTreantHurt = false;
    private boolean isPlayingTreantAttack = false;
    private boolean isPlayingTreantDefeat = false;
    private boolean isPlayingTreantEntrance = false;

    // Carrion Bat
    private static final int BAT_IDLE_FRAME_COUNT = 4;
    private static final int BAT_HURT_FRAME_COUNT = 4;
    private static final int BAT_ATTACK_FRAME_COUNT = 4;
    private static final int BAT_ENTRANCE_FRAME_COUNT = 4;
    private static final int BAT_SPEED = 150;
    private static final int BAT_ENTRANCE_START_X = 1300;
    private static final int BAT_X = TREANT_X;
    private static final int BAT_Y = TREANT_Y;
    private static final double BAT_SCALE = 0.40; // tune this

    private BufferedImage[] batIdleFrames;
    private BufferedImage[] batHurtFrames;
    private BufferedImage[] batAttackFrames;
    private BufferedImage[] batEntranceFrames;

    private boolean isPlayingBatHurt = false;
    private boolean isPlayingBatAttack = false;
    private boolean isPlayingBatDefeat = false;
    private boolean isPlayingBatEntrance = false;

    // Hollow Stag
    private BufferedImage[] stagIdleFrames;
    private BufferedImage[] stagHurtFrames;
    private BufferedImage[] stagDeathlyChargeFrames;
    private BufferedImage[] stagBlackenedHowlFrames;
    private BufferedImage[] stagEntranceFrames;

    private static final int STAG_IDLE_FRAME_COUNT = 5;
    private static final int STAG_HURT_FRAME_COUNT = 5;
    private static final int STAG_CHARGE_FRAME_COUNT = 5;
    private static final int STAG_HOWL_FRAME_COUNT = 5;
    private static final int STAG_ENTRANCE_FRAME_COUNT = 5;
    private static final int STAG_SPEED = 250; // slower than wolf (170)
    private static final int STAG_ENTRANCE_START_X = 850;
    private static final int STAG_X = ENEMY_X - 120;
    private static final int STAG_Y = ENEMY_Y - 170;

    private boolean isPlayingStagHurt = false;
    private boolean isPlayingStagCharge = false;
    private boolean isPlayingStagHowl = false;
    private boolean isPlayingStagDefeat = false;
    private boolean isPlayingStagEntrance = false;

    //World2

    // Plague Vermin (World 2)
    private static final int PLAGUE_VERMIN_IDLE_FRAME_COUNT     = 4;
    private static final int PLAGUE_VERMIN_HURT_FRAME_COUNT     = 4;
    private static final int PLAGUE_VERMIN_ATTACK_FRAME_COUNT   = 4;
    private static final int PLAGUE_VERMIN_ENTRANCE_FRAME_COUNT = 4;
    private static final int PLAGUE_VERMIN_SPEED                = 200;
    private static final int PLAGUE_VERMIN_ENTRANCE_START_X     = 1300;
    private static final int PLAGUE_VERMIN_X                    = TREANT_X;
    private static final int PLAGUE_VERMIN_Y = 80; // IDLE_Y_W2, World 2 row
    private static final double PLAGUE_VERMIN_SCALE             = 0.40;

    private BufferedImage[] plagueVerminIdleFrames;
    private BufferedImage[] plagueVerminHurtFrames;
    private BufferedImage[] plagueVerminAttackFrames;
    private BufferedImage[] plagueVerminEntranceFrames;

    private boolean isPlayingPlagueVerminHurt     = false;
    private boolean isPlayingPlagueVerminAttack   = false;
    private boolean isPlayingPlagueVerminDefeat   = false;
    private boolean isPlayingPlagueVerminEntrance = false;

    // Forsaken Cultist
    private static final int FORSAKEN_CULTIST_IDLE_FRAME_COUNT     = 4;
    private static final int FORSAKEN_CULTIST_HURT_FRAME_COUNT     = 4;
    private static final int FORSAKEN_CULTIST_ATTACK_FRAME_COUNT   = 4;
    private static final int FORSAKEN_CULTIST_ENTRANCE_FRAME_COUNT = 4;
    private static final int FORSAKEN_CULTIST_SPEED                = 200;
    private static final int FORSAKEN_CULTIST_ENTRANCE_START_X     = 1300;
    private static final double FORSAKEN_CULTIST_SCALE = 0.50;

    private BufferedImage[] forsakenCultistIdleFrames;
    private BufferedImage[] forsakenCultistHurtFrames;
    private BufferedImage[] forsakenCultistAttackFrames;
    private BufferedImage[] forsakenCultistEntranceFrames;

    private boolean isPlayingForsakenCultistHurt     = false;
    private boolean isPlayingForsakenCultistAttack   = false;
    private boolean isPlayingForsakenCultistDefeat   = false;
    private boolean isPlayingForsakenCultistEntrance = false;

    // Blight Hound
    private static final int BLIGHT_HOUND_IDLE_FRAME_COUNT     = 4;
    private static final int BLIGHT_HOUND_HURT_FRAME_COUNT     = 4;
    private static final int BLIGHT_HOUND_ATTACK_FRAME_COUNT   = 4;
    private static final int BLIGHT_HOUND_ENTRANCE_FRAME_COUNT = 4;
    private static final int BLIGHT_HOUND_SPEED                = 200;
    private static final int BLIGHT_HOUND_ENTRANCE_START_X     = 1300;
    private static final int BLIGHT_HOUND_X                    = PLAGUE_VERMIN_X;
    private static final int BLIGHT_HOUND_Y = PLAGUE_VERMIN_Y + 195;
    private static final double BLIGHT_HOUND_SCALE             = 0.40;

    private BufferedImage[] blightHoundIdleFrames;
    private BufferedImage[] blightHoundHurtFrames;
    private BufferedImage[] blightHoundAttackFrames;
    private BufferedImage[] blightHoundEntranceFrames;

    private boolean isPlayingBlightHoundHurt     = false;
    private boolean isPlayingBlightHoundAttack   = false;
    private boolean isPlayingBlightHoundDefeat   = false;
    private boolean isPlayingBlightHoundEntrance = false;

    // Ghoul Footman
    private static final int GHOUL_FOOTMAN_IDLE_FRAME_COUNT     = 4;
    private static final int GHOUL_FOOTMAN_HURT_FRAME_COUNT     = 4;
    private static final int GHOUL_FOOTMAN_ATTACK_FRAME_COUNT   = 4;
    private static final int GHOUL_FOOTMAN_ENTRANCE_FRAME_COUNT = 4;
    private static final int GHOUL_FOOTMAN_SPEED                = 200;
    private static final int GHOUL_FOOTMAN_ENTRANCE_START_X     = 1300;
    private static final int GHOUL_FOOTMAN_X                    = PLAGUE_VERMIN_X;
    private static final int GHOUL_FOOTMAN_Y                    = PLAGUE_VERMIN_Y + 260;
    private static final double GHOUL_FOOTMAN_SCALE             = 0.40;

    private BufferedImage[] ghoulFootmanIdleFrames;
    private BufferedImage[] ghoulFootmanHurtFrames;
    private BufferedImage[] ghoulFootmanAttackFrames;
    private BufferedImage[] ghoulFootmanEntranceFrames;

    private boolean isPlayingGhoulFootmanHurt     = false;
    private boolean isPlayingGhoulFootmanAttack   = false;
    private boolean isPlayingGhoulFootmanDefeat   = false;
    private boolean isPlayingGhoulFootmanEntrance = false;

    // Black Jailer
    private static final int BLACK_JAILER_IDLE_FRAME_COUNT      = 4;
    private static final int BLACK_JAILER_HURT_FRAME_COUNT      = 4;
    private static final int BLACK_JAILER_CHAINS_FRAME_COUNT    = 4;
    private static final int BLACK_JAILER_LASH_FRAME_COUNT      = 4;
    private static final int BLACK_JAILER_ENTRANCE_FRAME_COUNT  = 4;
    private static final int BLACK_JAILER_SPEED                 = 240;
    private static final int BLACK_JAILER_ENTRANCE_START_X      = 1300;
    private static final int BLACK_JAILER_X                     = PLAGUE_VERMIN_X + 90;
    private static final int BLACK_JAILER_Y                     = PLAGUE_VERMIN_Y + 250;
    private static final double BLACK_JAILER_SCALE              = 0.50;

    private BufferedImage[] blackJailerIdleFrames;
    private BufferedImage[] blackJailerHurtFrames;
    private BufferedImage[] blackJailerChainsFrames;
    private BufferedImage[] blackJailerLashFrames;
    private BufferedImage[] blackJailerEntranceFrames;

    private boolean isPlayingBlackJailerHurt     = false;
    private boolean isPlayingBlackJailerChains   = false;
    private boolean isPlayingBlackJailerLash     = false;
    private boolean isPlayingBlackJailerDefeat   = false;
    private boolean isPlayingBlackJailerEntrance = false;

    // Luther Von
    private static final int LUTHER_VON_IDLE_FRAME_COUNT        = 4;
    private static final int LUTHER_VON_HURT_FRAME_COUNT        = 4;
    private static final int LUTHER_VON_DEFEATED_FRAME_COUNT    = 4;
    private static final int LUTHER_VON_ENTRANCE_FRAME_COUNT    = 4;
    private static final int LUTHER_VON_CROWN_FRAME_COUNT       = 4;
    private static final int LUTHER_VON_ASCENSION_FRAME_COUNT   = 4;
    private static final int LUTHER_VON_WRATH_FRAME_COUNT       = 4;
    private static final int LUTHER_VON_SPEED                   = 200;
    private static final int LUTHER_VON_ENTRANCE_START_X        = 1300;
    private static final int LUTHER_VON_X                       = BLACK_JAILER_X;
    private static final int LUTHER_VON_Y                       = 170;
    private static final double LUTHER_VON_SCALE                = BLACK_JAILER_SCALE;

    private BufferedImage[] lutherVonIdleFrames;
    private BufferedImage[] lutherVonHurtFrames;
    private BufferedImage[] lutherVonDefeatedFrames;
    private BufferedImage[] lutherVonEntranceFrames;
    private BufferedImage[] lutherVonCrownFrames;
    private BufferedImage[] lutherVonAscensionFrames;
    private BufferedImage[] lutherVonWrathFrames;

    private boolean isPlayingLutherVonHurt       = false;
    private boolean isPlayingLutherVonDefeat     = false;
    private boolean isPlayingLutherVonEntrance   = false;
    private boolean isPlayingLutherVonCrown      = false;
    private boolean isPlayingLutherVonAscension  = false;
    private boolean isPlayingLutherVonWrath      = false;

    // Flame Revenant
    private static final int FLAME_REVENANT_IDLE_FRAME_COUNT     = 4;
    private static final int FLAME_REVENANT_HURT_FRAME_COUNT     = 4;
    private static final int FLAME_REVENANT_ATTACK_FRAME_COUNT   = 4;
    private static final int FLAME_REVENANT_ENTRANCE_FRAME_COUNT = 4;
    private static final int FLAME_REVENANT_SPEED                = 200;
    private static final int FLAME_REVENANT_ENTRANCE_START_X     = 1300;
    private static final int FLAME_REVENANT_X                    = ENEMY_X;
    private static final int FLAME_REVENANT_Y                    = ENEMY_Y - 100;
    private static final double FLAME_REVENANT_SCALE             = 1.5;

    private BufferedImage[] flameRevenantIdleFrames;
    private BufferedImage[] flameRevenantHurtFrames;
    private BufferedImage[] flameRevenantAttackFrames;
    private BufferedImage[] flameRevenantEntranceFrames;

    private boolean isPlayingFlameRevenantHurt     = false;
    private boolean isPlayingFlameRevenantAttack   = false;
    private boolean isPlayingFlameRevenantDefeat   = false;
    private boolean isPlayingFlameRevenantEntrance = false;

    // Bone Warlock
    private static final int BONE_WARLOCK_IDLE_FRAME_COUNT     = 4;
    private static final int BONE_WARLOCK_HURT_FRAME_COUNT     = 4;
    private static final int BONE_WARLOCK_ATTACK_FRAME_COUNT   = 4;
    private static final int BONE_WARLOCK_ENTRANCE_FRAME_COUNT = 4;
    private static final int BONE_WARLOCK_SPEED                = 200;
    private static final int BONE_WARLOCK_ENTRANCE_START_X     = 1300;
    private static final int BONE_WARLOCK_X                    = ENEMY_X - 30;
    private static final int BONE_WARLOCK_Y                    = ENEMY_Y - 100;
    private static final double BONE_WARLOCK_SCALE             = 1.5;

    private BufferedImage[] boneWarlockIdleFrames;
    private BufferedImage[] boneWarlockHurtFrames;
    private BufferedImage[] boneWarlockAttackFrames;
    private BufferedImage[] boneWarlockEntranceFrames;

    private boolean isPlayingBoneWarlockHurt     = false;
    private boolean isPlayingBoneWarlockAttack   = false;
    private boolean isPlayingBoneWarlockDefeat   = false;
    private boolean isPlayingBoneWarlockEntrance = false;

    // Obsidian Crusher
    private static final int OBSIDIAN_CRUSHER_IDLE_FRAME_COUNT     = 4;
    private static final int OBSIDIAN_CRUSHER_HURT_FRAME_COUNT     = 4;
    private static final int OBSIDIAN_CRUSHER_ATTACK_FRAME_COUNT   = 4;
    private static final int OBSIDIAN_CRUSHER_ENTRANCE_FRAME_COUNT = 4;
    private static final int OBSIDIAN_CRUSHER_SPEED                = 200;
    private static final int OBSIDIAN_CRUSHER_ENTRANCE_START_X     = 1300;
    private static final int OBSIDIAN_CRUSHER_X                    = ENEMY_X;
    private static final int OBSIDIAN_CRUSHER_Y                    = ENEMY_Y - 100;
    private static final double OBSIDIAN_CRUSHER_SCALE             = 1.5;

    private BufferedImage[] obsidianCrusherIdleFrames;
    private BufferedImage[] obsidianCrusherHurtFrames;
    private BufferedImage[] obsidianCrusherAttackFrames;
    private BufferedImage[] obsidianCrusherEntranceFrames;

    private boolean isPlayingObsidianCrusherHurt     = false;
    private boolean isPlayingObsidianCrusherAttack   = false;
    private boolean isPlayingObsidianCrusherDefeat   = false;
    private boolean isPlayingObsidianCrusherEntrance = false;

    // Soulflayer Gargoyle
    private static final int SOULFLAYER_GARGOYLE_IDLE_FRAME_COUNT     = 4;
    private static final int SOULFLAYER_GARGOYLE_HURT_FRAME_COUNT     = 4;
    private static final int SOULFLAYER_GARGOYLE_ATTACK_FRAME_COUNT   = 4;
    private static final int SOULFLAYER_GARGOYLE_ENTRANCE_FRAME_COUNT = 4;
    private static final int SOULFLAYER_GARGOYLE_SPEED                = 200;
    private static final int SOULFLAYER_GARGOYLE_ENTRANCE_START_X     = 1300;
    private static final int SOULFLAYER_GARGOYLE_X                    = ENEMY_X;
    private static final int SOULFLAYER_GARGOYLE_Y                    = ENEMY_Y - 260;
    private static final double SOULFLAYER_GARGOYLE_SCALE             = 1.5;

    private BufferedImage[] soulflayerGargoyleIdleFrames;
    private BufferedImage[] soulflayerGargoyleHurtFrames;
    private BufferedImage[] soulflayerGargoyleAttackFrames;
    private BufferedImage[] soulflayerGargoyleEntranceFrames;

    private boolean isPlayingSoulflayerGargoyleHurt     = false;
    private boolean isPlayingSoulflayerGargoyleAttack   = false;
    private boolean isPlayingSoulflayerGargoyleDefeat   = false;
    private boolean isPlayingSoulflayerGargoyleEntrance = false;

    // Zyrryl
    private static final int ZYRRYL_IDLE_FRAME_COUNT      = 4;
    private static final int ZYRRYL_HURT_FRAME_COUNT      = 4;
    private static final int ZYRRYL_BONE_SHIELD_FRAME_COUNT = 4;
    private static final int ZYRRYL_GREAT_CLEAVER_FRAME_COUNT = 4;
    private static final int ZYRRYL_ENTRANCE_FRAME_COUNT  = 4;
    private static final int ZYRRYL_SPEED                 = 200;
    private static final int ZYRRYL_ENTRANCE_START_X      = 1300;
    private static final int ZYRRYL_X                     = ENEMY_X;
    private static final int ZYRRYL_Y                     = ENEMY_Y - 250;
    private static final double ZYRRYL_SCALE              = 1.5;

    private BufferedImage[] zyrrylIdleFrames;
    private BufferedImage[] zyrrylHurtFrames;
    private BufferedImage[] zyrrylBoneShieldFrames;
    private BufferedImage[] zyrrylGreatCleaverFrames;
    private BufferedImage[] zyrrylEntranceFrames;

    private boolean isPlayingZyrrylHurt        = false;
    private boolean isPlayingZyrrylBoneShield  = false;
    private boolean isPlayingZyrrylGreatCleaver = false;
    private boolean isPlayingZyrrylDefeat      = false;
    private boolean isPlayingZyrrylEntrance    = false;

    // Khai the Necromancer
    private static final int KHAI_NECRO_IDLE_FRAME_COUNT      = 4;
    private static final int KHAI_NECRO_HURT_FRAME_COUNT      = 4;
    private static final int KHAI_NECRO_SOUL_DRAIN_FRAME_COUNT = 4;
    private static final int KHAI_NECRO_ENCAPSULATION_FRAME_COUNT = 4;
    private static final int KHAI_NECRO_DARK_ASCENSION_FRAME_COUNT = 4;
    private static final int KHAI_NECRO_ENTRANCE_FRAME_COUNT  = 4;
    private static final int KHAI_NECRO_SPEED                 = 200;
    private static final int KHAI_NECRO_ENTRANCE_START_X      = 1300;
    private static final int KHAI_NECRO_X                     = ENEMY_X;
    private static final int KHAI_NECRO_Y                     = ENEMY_Y - 130;
    private static final double KHAI_NECRO_SCALE              = 1.0;

    private BufferedImage[] khaiNecroIdleFrames;
    private BufferedImage[] khaiNecroHurtFrames;
    private BufferedImage[] khaiNecroSoulDrainFrames;
    private BufferedImage[] khaiNecroEncapsulationFrames;
    private BufferedImage[] khaiNecroDarkAscensionFrames;
    private BufferedImage[] khaiNecroEntranceFrames;

    private boolean isPlayingKhaiNecroHurt          = false;
    private boolean isPlayingKhaiNecroSoulDrain     = false;
    private boolean isPlayingKhaiNecroEncapsulation = false;
    private boolean isPlayingKhaiNecroDarkAscension = false;
    private boolean isPlayingKhaiNecroDefeat        = false;
    private boolean isPlayingKhaiNecroEntrance      = false;

    // ════════════════════════════════════════════
    // ★ POSITION CONSTANTS
    // ════════════════════════════════════════════
    private static final int IDLE_X = 200;
    private static final int IDLE_Y = 340;
    private static final int ACTION_X_BASE = 210;
    private static final int ACTION_Y = 280;
    private static final int IDLE_Y_W2 = 260;
    private static final int ACTION_Y_W2 = 200;
    private static final int ENEMY_Y_W2 = 285;
    private static final int SHADE_Y_W2 = 260;
    private static final int STAG_Y_W2 = 115;
    private static final int TREANT_Y_W2 = 175;
    private static final int BAT_Y_W2 = 175;
    private boolean isWorld2Battle = false;

    // Colors
    private static final Color BG_DARK = new Color(12, 10, 22);
    private static final Color BG_PANEL = new Color(22, 20, 38);
    private static final Color GOLD = new Color(120, 80, 10);
    private static final Color GOLD_DIM = new Color(80, 55, 10);
    private static final Color TEXT_BRIGHT = new Color(240, 232, 208);
    private static final Color TEXT_DIM = new Color(140, 125, 95);
    private static final Color GREEN = new Color(0, 110, 45);
    private static final Color GREEN_DARK = new Color(0, 80, 30);
    private static final Color RED = new Color(160, 20, 20);
    private static final Color RED_DARK = new Color(110, 10, 10);
    private static final Color BLUE = new Color(30, 80, 180);
    private static final Color PURPLE = new Color(90, 30, 160);
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 200);
    private static final Color BORDER_NORM = new Color(70, 65, 100);

    // Fonts
    private static final Font FONT_STAT = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font FONT_BTN = new Font("Monospaced", Font.BOLD, 12);
    private static final Font FONT_RESULT = new Font("Monospaced", Font.BOLD, 28);

    // Widgets
    private JLabel heroEmojiLbl, heroNameLbl, heroRoleLbl, heroLvlLbl;
    private JProgressBar heroHpBar, heroEnergyBar;
    private JLabel heroHpText, heroEnergyText, heroStatusLbl;
    private JLabel enemyEmojiLbl, enemyNameLbl, enemyRoleLbl;
    private JProgressBar enemyHpBar;
    private JLabel enemyHpText, enemyStatusLbl;
    private JLabel roundLabel, turnLabel;
    private javax.swing.JTextPane logArea;
    private JButton skill1Btn, skill2Btn, skipTurnBtn, ultimateBtn, battleContinueBtn;
    private JLabel specialCdLabel;
    private JPanel resultOverlay;
    private JLabel resultIcon, resultTitle, resultSub;

    // Loot Choice UI
    private JPanel lootChoiceOverlay;
    private JLabel lootItem1Icon, lootItem1Name;
    private JTextArea lootItem1Desc;
    private JButton lootItem1Btn;
    private JLabel lootItem2Icon, lootItem2Name;
    private JTextArea lootItem2Desc;
    private JButton lootItem2Btn;

    // Sequence tracking
    private List<EnemyDefinition> enemySequence;
    private int enemySequenceIndex = 0;
    private int enemyFightIndex = 0;
    private Runnable onSequenceComplete;
    private Combatant savedHeroCombatant = null;

    // Level up tracking
    private enum PostVictoryStep {NONE, LOOT, LEVEL_UP_ANNOUNCE, LEVEL_UP_STATS, LEVEL_UP_RESTORE, OBJECTIVE, VICTORY_FLAVOUR, LOOT_FLAVOUR}

    private PostVictoryStep postVictoryStep = PostVictoryStep.NONE;
    private int lvlUp_level, lvlUp_hpGain, lvlUp_newHp, lvlUp_atkGain, lvlUp_newAtk, lvlUp_defGain, lvlUp_newDef;
    private EnemyDefinition postVictoryEnemy = null;
    private Runnable postVictoryNext = null;

    private Font normalLogFont = null;
    private Font smallLogFont = null;

    public BattlePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        setBackground(BG_DARK);
        buildUI();
    }

    public Combatant getCurrentHero() {
        return currentHero;
    }

    public void setCurrentHero(Combatant hero) {
        this.currentHero = hero; // (Change 'currentHero' to whatever your hero variable is named)
    }

    public void setOnReturnToSelection(Runnable r) {
        this.onReturnToSelection = r;
    }

    public void setOnRestartBattle(Runnable r) {
        this.onRestartBattle = r;
    }

    public void setOnEnemyGroupDefeated(BiConsumer<Integer, Runnable> cb) {
        this.onEnemyGroupDefeated = cb;
    }

    public void startEnemySequence(HeroDefinition hero, List<EnemyDefinition> sequence, Runnable onComplete) {
        System.out.println(">>> BattlePanel heroDef set to: " + (hero != null ? hero.name : "null")); // debug
        this.heroDef = hero;
        this.enemySequence = new ArrayList<>(sequence);
        this.enemySequenceIndex = 0;
        this.enemyFightIndex = 0;
        this.onSequenceComplete = onComplete;
        startNextFight();
    }

    private void startNextFight() {
        if (enemySequenceIndex >= enemySequence.size()) {
            if (onSequenceComplete != null) onSequenceComplete.run();
            return;
        }

        EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
        this.enemyDef = eDef;

        // Right after: this.enemyDef = eDef;

        if (eDef.name.equals("Khai the Gray")) {
            setBattleBackground("/assets/Backgrounds/NecroBackground.png");
        } else if (isWorld2Battle) {
            String bg = switch (eDef.name) {
                case "Plague Vermin"     -> "/assets/Backgrounds/World2BattleBackground.png";
                case "Forsaken Cultist"  -> "/assets/Backgrounds/World2BattleBackground2.png";
                case "Blight Hound"      -> "/assets/Backgrounds/World2BattleBackground3.png";
                case "Ghoul Footman"     -> "/assets/Backgrounds/World2BattleBackground4.png";
                case "The Black Jailer"  -> "/assets/Backgrounds/World2BattleBackground5.png";
                case "Luther Von"        -> "/assets/Backgrounds/World2BattleBackgroundLast.png";
                default -> currentBattleBgPath;
            };
            setBattleBackground(bg);
        } else if (isWorld3Battle()) {
            String bg = switch (eDef.name) {
                default -> currentBattleBgPath;
            };
            setBattleBackground(bg);
        }

        if (this.currentHero == null) {
            // Case 1: Brand new game! Make a hero from the blueprint.
            this.currentHero = HeroFactory.createHero(heroDef);
        } else if (savedHeroCombatant != null) {
            // Case 2: Mid-Horde (e.g. Wolf 1 to Wolf 2). Keep current hero exactly as is.
            this.currentHero = savedHeroCombatant;
            savedHeroCombatant = null;
        } else {
            // Case 3: Entering a New World or Boss fight with our veteran hero!
            // Clean up old poisons/stuns, but KEEP ALL ITEMS AND HP!
            this.currentHero.getStatusManager().resetAllEffects();
            this.currentHero.defending = false;
            this.currentHero.specialCooldown = 0;
        }


        currentEnemy = HeroFactory.createEnemy(eDef);

        this.engine = new BattleManager(currentHero, currentEnemy, heroDef, false);

        populateCombatantUI();
        refreshBattleUI();
        clearLog();

        int fightNum = enemyFightIndex + 1;
        addLog(eDef.name.toUpperCase() + " " + fightNum + "/" + eDef.count + " — FIGHT!", GOLD);

        resultOverlay.setVisible(false);
        setActionsEnabled(false);
        animating = true;
        postVictoryStep = PostVictoryStep.NONE;
        setLogFontNormal();
        if (battleContinueBtn != null) battleContinueBtn.setEnabled(false);
        setTurnLabel(true);
        startHeroIdleAnimation();

        // MINIBOSS CHECKS
        boolean isStagFight   = eDef.name.equals("The Hollow Stag");
        boolean isJailerFight = eDef.name.equals("The Black Jailer");
        boolean isLutherFight = eDef.name.equals("Luther Von");

        if (isStagFight || isJailerFight || isLutherFight) {

            String bannerText = isStagFight  ? "MINIBOSS ENCOUNTER : THE HOLLOW STAG"
                    : isJailerFight ? "MINIBOSS ENCOUNTER : THE BLACK JAILER"
                    :                 "MINIBOSS ENCOUNTER : LUTHER VON THE CORRUPTED KING";

            JLabel minibossLabel = new JLabel(bannerText, SwingConstants.CENTER);
            minibossLabel.setBounds(0, 220, 1280, 50);
            minibossLabel.setForeground(new Color(220, 20, 20));
            minibossLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));
            add(minibossLabel);
            setComponentZOrder(minibossLabel, 0);
            repaint();

            delay(2500, () -> {
                float[] alpha = {1.0f};
                javax.swing.Timer fadeOut = new javax.swing.Timer(16, null);
                fadeOut.addActionListener(ev -> {
                    alpha[0] = Math.max(0f, alpha[0] - 0.03f);
                    minibossLabel.setForeground(
                            new Color(220/255f, 20/255f, 20/255f, alpha[0])
                    );

                    if (alpha[0] <= 0f) {
                        fadeOut.stop();
                        remove(minibossLabel);
                        repaint();

                        playEnemyEntrance(() -> {
                            beginPlayerTurnSequence();
                        });
                    }
                });
                fadeOut.start();
            });

        } else {
            playEnemyEntrance(() -> {
                beginPlayerTurnSequence();
            });
        }
    }

    public void setBattleBackground(String resourcePath) {
        if (battleBg == null) return;
        currentBattleBgPath = resourcePath; // ADD THIS LINE
        java.net.URL url = getClass().getResource(resourcePath);
        if (url != null) {
            battleBg.setIcon(new ImageIcon(
                    new ImageIcon(url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        }
        isWorld2Battle = resourcePath.contains("World2Battle");
        battleBg.repaint();
    }

    private void buildUI() {

        JLabel theBg = new JLabel();
        theBg.setBounds(0, 0, 1280, 720);
        theBg.setOpaque(true);
        theBg.setBackground(BG_DARK);
        java.net.URL theBgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (theBgUrl != null)
            theBg.setIcon(new ImageIcon(new ImageIcon(theBgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH)));
        add(theBg);

        battleBg = new JLabel();
        battleBg.setBounds(0, 0, 1280, 520);
        battleBg.setOpaque(false);
        java.net.URL battleBgUrl = getClass().getResource("/assets/Backgrounds/World1BattleBackground.png");
        if (battleBgUrl != null)
            battleBg.setIcon(new ImageIcon(new ImageIcon(battleBgUrl).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        add(battleBg);

        JPanel heroCard = buildStatCard(true);
        heroCard.setBounds(10, 8, 340, 90);
        add(heroCard);

        heroSpriteLabel = buildHeroSpriteLabel();
        add(heroSpriteLabel);
        enemySpriteLabel = buildEnemySpriteLabel();
        add(enemySpriteLabel);

        JPanel enemyCard = buildStatCard(false);
        enemyCard.setBounds(920, 8, 340, 90);
        add(enemyCard);

        JLabel dialogueBg = new JLabel();
        dialogueBg.setBounds(-40, 453, 1053, 343);
        java.net.URL dbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (dbUrl != null)
            dialogueBg.setIcon(new ImageIcon(new ImageIcon(dbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH)));
        add(dialogueBg);

        turnLabel = new JLabel("YOUR TURN", SwingConstants.CENTER);
        turnLabel.setBounds(0, 8, 1280, 26);
        turnLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        turnLabel.setForeground(GREEN);
        add(turnLabel);

        roundLabel = new JLabel("Round 1", SwingConstants.CENTER);
        roundLabel.setBounds(0, 36, 1280, 22);
        roundLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        roundLabel.setForeground(GOLD);
        add(roundLabel);

        logArea = new javax.swing.JTextPane();
        logArea.setBounds(160, 575, 780, 100);
        logArea.setEditable(false);
        logArea.setOpaque(false);
        logArea.setBackground(new Color(0, 0, 0, 0));
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) logArea.setFont(Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 19f));
            else logArea.setFont(new Font("Dialog", Font.BOLD, 16));
        } catch (Exception ex) {
            logArea.setFont(new Font("Dialog", Font.BOLD, 16));
        }
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(logArea);

        battleContinueBtn = makeBtn("/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png", 964, 554, 154, 64, "Continue", 10);
        battleContinueBtn.addActionListener(e -> onContinuePressed());

        JButton menuBtn = makeBtn("/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png", 1103, 555, 148, 58, "Menu", 0);
        menuBtn.addActionListener(e -> openInventoryDialog());

        JButton backBtn = makeBtn("/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png", 970, 613, 140, 50, "Back", 20);
        JButton exitBtn = makeBtn("/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png", 1108, 613, 140, 50, "Exit", 19);
        exitBtn.addActionListener(e -> System.exit(0));

        add(battleContinueBtn);
        add(menuBtn);
        add(backBtn);
        add(exitBtn);

        // Updated sizes for a rounder, balanced button!
        int btnY = 453, btnW = 78, btnH = 78, btnGap = 24;
        int startX = (1280 - (4 * btnW + 3 * btnGap)) / 2;

        skill1Btn = makeSkillBtn("Skill 1", new Color(60, 30, 90), new Color(130, 60, 200), startX, btnY, btnW, btnH);
        skill2Btn = makeSkillBtn("Skill 2", new Color(30, 60, 90), new Color(52, 120, 219), startX + (btnW + btnGap), btnY, btnW, btnH);
        ultimateBtn = makeSkillBtn("Ultimate", new Color(90, 30, 30), new Color(192, 57, 43), startX + 2 * (btnW + btnGap), btnY, btnW, btnH);
        skipTurnBtn = makeSkillBtn("Skip Turn", new Color(30, 60, 40), new Color(39, 174, 96), startX + 3 * (btnW + btnGap), btnY, btnW, btnH);

        skill1Btn.addActionListener(e -> onPlayerAction(BattleManager.BattleAction.SKILL1));
        skill2Btn.addActionListener(e -> onPlayerAction(BattleManager.BattleAction.SKILL2));
        ultimateBtn.addActionListener(e -> onPlayerAction(BattleManager.BattleAction.ULTIMATE));
        skipTurnBtn.addActionListener(e -> onPlayerAction(BattleManager.BattleAction.SKIP_TURN));

        add(skill1Btn);
        add(skill2Btn);
        add(skipTurnBtn);
        add(ultimateBtn);

        specialCdLabel = new JLabel("");
        specialCdLabel.setBounds(0, 60, 1280, 20);
        specialCdLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        specialCdLabel.setForeground(new Color(200, 100, 50));
        specialCdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(specialCdLabel);

        resultOverlay = buildResultOverlay();
        resultOverlay.setBounds(0, 0, 1280, 720);
        resultOverlay.setVisible(false);
        add(resultOverlay);

        lootChoiceOverlay = buildLootChoiceOverlay();
        lootChoiceOverlay.setBounds(0, 0, 1280, 720);
        lootChoiceOverlay.setVisible(false);
        add(lootChoiceOverlay);

        setComponentZOrder(resultOverlay, 0);
        setComponentZOrder(specialCdLabel, 1);
        setComponentZOrder(roundLabel, 2);
        setComponentZOrder(turnLabel, 3);
        setComponentZOrder(logArea, 3);
        setComponentZOrder(battleContinueBtn, 4);
        setComponentZOrder(menuBtn, 5);
        setComponentZOrder(backBtn, 6);
        setComponentZOrder(exitBtn, 7);
        setComponentZOrder(skill1Btn, 8);
        setComponentZOrder(skill2Btn, 9);
        setComponentZOrder(skipTurnBtn, 10);
        setComponentZOrder(ultimateBtn, 11);
        setComponentZOrder(dialogueBg, 13);
        setComponentZOrder(heroCard, 14);
        setComponentZOrder(enemyCard, 15);
        setComponentZOrder(heroSpriteLabel, 16);
        setComponentZOrder(enemySpriteLabel, 17);
        setComponentZOrder(battleBg, 18);
        setComponentZOrder(theBg, 19);
    }

    private void openInventoryDialog() {
        if (currentHero == null || heroDef == null) return;

        // 1. Get the window and safely cast it to a JFrame
        Window owner = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = (owner instanceof JFrame) ? (JFrame) owner : null;

        // 2. Pass the Combatant (currentHero) and the Runnable callback
        InventoryDialog dlg = new InventoryDialog(parentFrame, currentHero, () -> {
            // This code runs when onUpdate.run() is called inside InventoryDialog
            refreshBattleUI();
            // updateUI(); // Uncomment or change this to your actual panel refresh method
        });

        dlg.setVisible(true);   // blocks (modal) until closed
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ DYNAMIC IMAGE BUTTON CONFIGURATOR
    // ════════════════════════════════════════════════════════════════════════
    private void configureSkillButton(JButton btn, String skillName, String iconText, Color bg, Color border) {
        String normalPath = null, hoverPath = null, disabledPath = null;

        if (skillName.equals("Blade Rush")) {
            normalPath = "/assets/KaelAssets/BladeRush.png";
            hoverPath = "/assets/KaelAssets/BladeRushHovered.png";
        } else if (skillName.equals("Piercing Slash")) {
            normalPath = "/assets/KaelAssets/PiercingSlash.png";
            hoverPath = "/assets/KaelAssets/PiercingSlashHovered.png";
        } else if (skillName.equals("Eternal Cross Slash")) {
            normalPath = "/assets/KaelAssets/EternalCrossSlash.png";
            hoverPath = "/assets/KaelAssets/EternalCrossSlashHovered.png";
            disabledPath = null;
        } else if (skillName.equals("Skip Turn")) {
            normalPath = "/assets/KaelAssets/SkipTurn.png";
            hoverPath = "/assets/KaelAssets/SkipTurnHovered.png";
        }

        java.net.URL nUrl = (normalPath != null) ? getClass().getResource(normalPath) : null;

        if (nUrl != null) {
            btn.setText("");
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);

            try {
                btn.setIcon(new ImageIcon(new ImageIcon(nUrl).getImage().getScaledInstance(73, 73, Image.SCALE_SMOOTH)));
                java.net.URL hUrl = null;
                if (hoverPath != null) {
                    hUrl = getClass().getResource(hoverPath);
                    if (hUrl != null)
                        btn.setRolloverIcon(new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(78, 78, Image.SCALE_SMOOTH)));
                } else
                    btn.setRolloverIcon(new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(73, 73, Image.SCALE_SMOOTH)));

                if (disabledPath != null) {
                    java.net.URL dUrl = getClass().getResource(disabledPath);
                    if (dUrl != null)
                        btn.setDisabledIcon(new ImageIcon(new ImageIcon(dUrl).getImage().getScaledInstance(150, 65, Image.SCALE_SMOOTH)));
                } else btn.setDisabledIcon(null);
            } catch (Exception e) {
                System.out.println("Error loading image for " + skillName);
            }
        } else {
            btn.setIcon(null);
            btn.setRolloverIcon(null);
            try {
                java.awt.image.BufferedImage base = ImageIO.read(nUrl);
                java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(73, 73, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D gx = out.createGraphics();
                gx.drawImage(base, 0, 0, 73, 73, null);
                gx.setColor(new Color(255, 255, 255, 140));
                gx.fillRect(0, 0, 150, 65);
                gx.dispose();
                btn.setDisabledIcon(new ImageIcon(out));
            } catch (Exception ex) {
                btn.setDisabledIcon(null);
            }
            btn.setContentAreaFilled(true);
            btn.setBorderPainted(true);
            btn.setOpaque(true);
            btn.setBackground(bg);
            btn.setText((iconText != null && !iconText.isEmpty() ? iconText + " " : "") + skillName);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border, 2),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        }
    }

    // Raw frames (no black removal) for World 3
    private BufferedImage[] idleFramesRaw;
    private BufferedImage[] bladeRushFramesRaw;
    private BufferedImage[] piercingSlashFramesRaw;
    private BufferedImage[] eternalCrossFramesRaw;
    private BufferedImage[] kaelHurtFramesRaw;

    // ════════════════════════════════════════════════════════════════════════
    // ★ SPRITE LOADERS
    // ════════════════════════════════════════════════════════════════════════
    private JLabel buildHeroSpriteLabel() {

        // Kael Idle
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_FRAME_COUNT, fh = sheet.getHeight();
                idleFrames    = new BufferedImage[SPRITE_FRAME_COUNT];
                idleFramesRaw = new BufferedImage[SPRITE_FRAME_COUNT];
                for (int i = 0; i < SPRITE_FRAME_COUNT; i++) {
                    BufferedImage sub = sheet.getSubimage(i * fw, 0, fw, fh);
                    idleFrames[i]    = removeBlackBg(sub);
                    BufferedImage argb = new BufferedImage(sub.getWidth(), sub.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(sub, 0, 0, null);
                    idleFramesRaw[i] = argb;
                }
            }
        } catch (Exception ex) {}

// Kael BladeRush
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelBladeRush.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLADE_RUSH_FRAME_COUNT, fh = sheet.getHeight();
                bladeRushFrames    = new BufferedImage[BLADE_RUSH_FRAME_COUNT];
                bladeRushFramesRaw = new BufferedImage[BLADE_RUSH_FRAME_COUNT];
                for (int i = 0; i < BLADE_RUSH_FRAME_COUNT; i++) {
                    BufferedImage sub = sheet.getSubimage(i * fw, 0, fw, fh);
                    bladeRushFrames[i]    = removeBlackBg(sub);
                    BufferedImage argb = new BufferedImage(sub.getWidth(), sub.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(sub, 0, 0, null);
                    bladeRushFramesRaw[i] = argb;
                }
            }
        } catch (Exception ex) {}

// Kael PiercingSlash
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelPiercingSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PIERCING_SLASH_FRAME_COUNT, fh = sheet.getHeight();
                piercingSlashFrames    = new BufferedImage[PIERCING_SLASH_FRAME_COUNT];
                piercingSlashFramesRaw = new BufferedImage[PIERCING_SLASH_FRAME_COUNT];
                for (int i = 0; i < PIERCING_SLASH_FRAME_COUNT; i++) {
                    BufferedImage sub = sheet.getSubimage(i * fw, 0, fw, fh);
                    piercingSlashFrames[i]    = removeBlackBg(sub);
                    BufferedImage argb = new BufferedImage(sub.getWidth(), sub.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(sub, 0, 0, null);
                    piercingSlashFramesRaw[i] = argb;
                }
            }
        } catch (Exception ex) {}

// Kael EternalCrossSlash
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelEternalCrossSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ETERNAL_CROSS_FRAME_COUNT, fh = sheet.getHeight();
                eternalCrossFrames    = new BufferedImage[ETERNAL_CROSS_FRAME_COUNT];
                eternalCrossFramesRaw = new BufferedImage[ETERNAL_CROSS_FRAME_COUNT];
                for (int i = 0; i < ETERNAL_CROSS_FRAME_COUNT; i++) {
                    BufferedImage sub = sheet.getSubimage(i * fw, 0, fw, fh);
                    eternalCrossFrames[i]    = removeBlackBg(sub);
                    BufferedImage argb = new BufferedImage(sub.getWidth(), sub.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(sub, 0, 0, null);
                    eternalCrossFramesRaw[i] = argb;
                }
            }
        } catch (Exception ex) {}

// Kael Hurt
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KAEL_HURT_FRAME_COUNT, fh = sheet.getHeight();
                kaelHurtFrames    = new BufferedImage[KAEL_HURT_FRAME_COUNT];
                kaelHurtFramesRaw = new BufferedImage[KAEL_HURT_FRAME_COUNT];
                for (int i = 0; i < KAEL_HURT_FRAME_COUNT; i++) {
                    BufferedImage sub = sheet.getSubimage(i * fw, 0, fw, fh);
                    kaelHurtFrames[i]    = removeBlackBg(sub);
                    BufferedImage argb = new BufferedImage(sub.getWidth(), sub.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(sub, 0, 0, null);
                    kaelHurtFramesRaw[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_FRAME_COUNT, fh = sheet.getHeight();
                idleFrames = new BufferedImage[SPRITE_FRAME_COUNT];
                for (int i = 0; i < SPRITE_FRAME_COUNT; i++)
                    idleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_FRAME_COUNT, fh = sheet.getHeight();
                idleFrames = new BufferedImage[SPRITE_FRAME_COUNT];
                for (int i = 0; i < SPRITE_FRAME_COUNT; i++)
                    idleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelBladeRush.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLADE_RUSH_FRAME_COUNT, fh = sheet.getHeight();
                bladeRushFrames = new BufferedImage[BLADE_RUSH_FRAME_COUNT];
                for (int i = 0; i < BLADE_RUSH_FRAME_COUNT; i++)
                    bladeRushFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelPiercingSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PIERCING_SLASH_FRAME_COUNT, fh = sheet.getHeight();
                piercingSlashFrames = new BufferedImage[PIERCING_SLASH_FRAME_COUNT];
                for (int i = 0; i < PIERCING_SLASH_FRAME_COUNT; i++)
                    piercingSlashFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelEternalCrossSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ETERNAL_CROSS_FRAME_COUNT, fh = sheet.getHeight();
                eternalCrossFrames = new BufferedImage[ETERNAL_CROSS_FRAME_COUNT];
                for (int i = 0; i < ETERNAL_CROSS_FRAME_COUNT; i++)
                    eternalCrossFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KAEL_HURT_FRAME_COUNT, fh = sheet.getHeight();
                kaelHurtFrames = new BufferedImage[KAEL_HURT_FRAME_COUNT];
                for (int i = 0; i < KAEL_HURT_FRAME_COUNT; i++)
                    kaelHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }
        //for World3 Issue transparent being too aggresive causes pixel difflation in sprites

        try {
            java.net.URL url = getClass().getResource("/assets/KarlAssets/KarlIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KARL_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                karlIdleFrames = new BufferedImage[KARL_IDLE_FRAME_COUNT];
                for (int i = 0; i < KARL_IDLE_FRAME_COUNT; i++)
                    karlIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KarlAssets/KarlHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KARL_HURT_FRAME_COUNT, fh = sheet.getHeight();
                karlHurtFrames = new BufferedImage[KARL_HURT_FRAME_COUNT];
                for (int i = 0; i < KARL_HURT_FRAME_COUNT; i++)
                    karlHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KarlAssets/KarlPiercingShot.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KARL_PIERCING_SHOT_FRAME_COUNT, fh = sheet.getHeight();
                karlPiercingShotFrames = new BufferedImage[KARL_PIERCING_SHOT_FRAME_COUNT];
                for (int i = 0; i < KARL_PIERCING_SHOT_FRAME_COUNT; i++)
                    karlPiercingShotFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KarlAssets/KarlBullseye.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KARL_BULLSEYE_FRAME_COUNT, fh = sheet.getHeight();
                karlBullseyeFrames = new BufferedImage[KARL_BULLSEYE_FRAME_COUNT];
                for (int i = 0; i < KARL_BULLSEYE_FRAME_COUNT; i++)
                    karlBullseyeFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KarlAssets/KarlRainOfaThousandArrows.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KARL_RAIN_FRAME_COUNT, fh = sheet.getHeight();
                karlRainFrames = new BufferedImage[KARL_RAIN_FRAME_COUNT];
                for (int i = 0; i < KARL_RAIN_FRAME_COUNT; i++)
                    karlRainFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        JLabel sprite = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                boolean isKarl = heroDef != null && heroDef.name.equals("Karl Clover Dior IV");
                boolean useRaw = !isKarl && isWorld3Battle();

                BufferedImage[] frames =
                        isPlayingBladeRush       ? (useRaw ? bladeRushFramesRaw     : bladeRushFrames) :
                                isPlayingPiercingSlash   ? (useRaw ? piercingSlashFramesRaw : piercingSlashFrames) :
                                        isPlayingEternalCross    ? (useRaw ? eternalCrossFramesRaw  : eternalCrossFrames) :
                                                isPlayingKaelHurt        ? (useRaw ? kaelHurtFramesRaw      : kaelHurtFrames) :
                                                        isPlayingKarlPiercingShot ? karlPiercingShotFrames :
                                                                isPlayingKarlBullseye    ? karlBullseyeFrames :
                                                                        isPlayingKarlRain        ? karlRainFrames :
                                                                                isPlayingKarlHurt        ? karlHurtFrames :
                                                                                        isKarl                   ? karlIdleFrames :
                                                                                                useRaw                   ? idleFramesRaw : idleFrames;
                if (frames == null || heroSpriteFrame >= frames.length) return;
                BufferedImage frame = frames[heroSpriteFrame];
                if (frame == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(frame, 0, 0, (int) (frame.getWidth() * SPRITE_SCALE), (int) (frame.getHeight() * SPRITE_SCALE), null);
                g2.dispose();

                boolean isShade = enemyDef != null && enemyDef.name.equals("Shade Sprite");
                boolean isStag = enemyDef != null && enemyDef.name.equals("The Hollow Stag");

                if (isPlayingWolfHurt) frames = wolfHurtFrames;
                else if (isPlayingWolfSavageHowl) frames = wolfSavageHowlFrames;
                else if (isPlayingWolfDefeat) frames = wolfDefeatFrames;
                else if (isPlayingWolfEntrance) frames = wolfEntranceFrames;
                else if (isPlayingSpriteHurt) frames = spriteHurtFrames;
                else if (isPlayingSpriteTrickster) frames = spriteTricksterFrames;
                else if (isPlayingSpriteDefeat) frames = spriteDefeatFrames;
                else if (isPlayingSpriteEntrance) frames = spriteEntranceFrames;
                else if (isPlayingStagHurt) frames = stagHurtFrames;
                else if (isPlayingStagCharge) frames = stagDeathlyChargeFrames;
                else if (isPlayingStagHowl) frames = stagBlackenedHowlFrames;
                else if (isPlayingStagEntrance) frames = stagEntranceFrames;
                else if (isStag) frames = stagIdleFrames;
                else if (isShade) frames = spriteIdleFrames;
                else frames = wolfIdleFrames;

                double scale = (isShade || isStag) ? SPRITE_SCALE : ENEMY_SCALE;
            }
        };

        int labelW = SPRITE_W;
        int labelH = SPRITE_H;
        if (idleFrames != null) {
            labelW = Math.max(labelW, (int)(idleFrames[0].getWidth() * SPRITE_SCALE));
            labelH = Math.max(labelH, (int)(idleFrames[0].getHeight() * SPRITE_SCALE));
        }
        if (karlIdleFrames != null) {
            labelW = Math.max(labelW, (int)(karlIdleFrames[0].getWidth() * KARL_SPRITE_SCALE));
            labelH = Math.max(labelH, (int)(karlIdleFrames[0].getHeight() * KARL_SPRITE_SCALE));
        }
        sprite.setBounds(IDLE_X, IDLE_Y, labelW, labelH);

        return sprite;
    }

    private boolean isWorld3Battle() {
        return currentBattleBgPath.contains("World3BG");
    }

    private JLabel buildEnemySpriteLabel() {
        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_FRAME_COUNT, fh = sheet.getHeight();
                wolfIdleFrames = new BufferedImage[WOLF_FRAME_COUNT];
                for (int i = 0; i < WOLF_FRAME_COUNT; i++)
                    wolfIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_HURT_FRAME_COUNT, fh = sheet.getHeight();
                wolfHurtFrames = new BufferedImage[WOLF_HURT_FRAME_COUNT];
                for (int i = 0; i < WOLF_HURT_FRAME_COUNT; i++)
                    wolfHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfSavageHowl.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_HOWL_FRAME_COUNT, fh = sheet.getHeight();
                wolfSavageHowlFrames = new BufferedImage[WOLF_HOWL_FRAME_COUNT];
                for (int i = 0; i < WOLF_HOWL_FRAME_COUNT; i++)
                    wolfSavageHowlFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfDefeat.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_DEFEAT_FRAME_COUNT, fh = sheet.getHeight();
                wolfDefeatFrames = new BufferedImage[WOLF_DEFEAT_FRAME_COUNT];
                for (int i = 0; i < WOLF_DEFEAT_FRAME_COUNT; i++)
                    wolfDefeatFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                wolfEntranceFrames = new BufferedImage[WOLF_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < WOLF_ENTRANCE_FRAME_COUNT; i++)
                    wolfEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_IDLE_COUNT, fh = sheet.getHeight();
                spriteIdleFrames = new BufferedImage[SPRITE_IDLE_COUNT];
                for (int i = 0; i < SPRITE_IDLE_COUNT; i++)
                    spriteIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_HURT_COUNT, fh = sheet.getHeight();
                spriteHurtFrames = new BufferedImage[SPRITE_HURT_COUNT];
                for (int i = 0; i < SPRITE_HURT_COUNT; i++)
                    spriteHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteTricksterStrike.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_TRICKSTER_COUNT, fh = sheet.getHeight();
                spriteTricksterFrames = new BufferedImage[SPRITE_TRICKSTER_COUNT];
                for (int i = 0; i < SPRITE_TRICKSTER_COUNT; i++)
                    spriteTricksterFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteDefeat.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_DEFEAT_COUNT, fh = sheet.getHeight();
                spriteDefeatFrames = new BufferedImage[SPRITE_DEFEAT_COUNT];
                for (int i = 0; i < SPRITE_DEFEAT_COUNT; i++)
                    spriteDefeatFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_ENTRANCE_COUNT, fh = sheet.getHeight();
                spriteEntranceFrames = new BufferedImage[SPRITE_ENTRANCE_COUNT];
                for (int i = 0; i < SPRITE_ENTRANCE_COUNT; i++)
                    spriteEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/DreadbarkTreantIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / TREANT_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                treantIdleFrames = new BufferedImage[TREANT_IDLE_FRAME_COUNT];
                for (int i = 0; i < TREANT_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    treantIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/DreadbarkTreantHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / TREANT_HURT_FRAME_COUNT, fh = sheet.getHeight();
                treantHurtFrames = new BufferedImage[TREANT_HURT_FRAME_COUNT];
                for (int i = 0; i < TREANT_HURT_FRAME_COUNT; i++)
                    treantHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/DreadbarkTreantRootSnare.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / TREANT_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                treantAttackFrames = new BufferedImage[TREANT_ATTACK_FRAME_COUNT];
                for (int i = 0; i < TREANT_ATTACK_FRAME_COUNT; i++) {
                    // Convert to ARGB without removing black bg
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    treantAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/DreadbarkTreantEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / TREANT_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                treantEntranceFrames = new BufferedImage[TREANT_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < TREANT_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    treantEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/CarrionBatsIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BAT_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                batIdleFrames = new BufferedImage[BAT_IDLE_FRAME_COUNT];
                for (int i = 0; i < BAT_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    batIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/CarrionBatsHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BAT_HURT_FRAME_COUNT, fh = sheet.getHeight();
                batHurtFrames = new BufferedImage[BAT_HURT_FRAME_COUNT];
                for (int i = 0; i < BAT_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    batHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/CarrionBatsScreech.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BAT_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                batAttackFrames = new BufferedImage[BAT_ATTACK_FRAME_COUNT];
                for (int i = 0; i < BAT_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    batAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/CarrionBatsEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BAT_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                batEntranceFrames = new BufferedImage[BAT_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < BAT_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    batEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {
        }


        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                stagEntranceFrames = new BufferedImage[STAG_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < STAG_ENTRANCE_FRAME_COUNT; i++)
                    stagEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                stagIdleFrames = new BufferedImage[STAG_IDLE_FRAME_COUNT];
                for (int i = 0; i < STAG_IDLE_FRAME_COUNT; i++)
                    stagIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_HURT_FRAME_COUNT, fh = sheet.getHeight();
                stagHurtFrames = new BufferedImage[STAG_HURT_FRAME_COUNT];
                for (int i = 0; i < STAG_HURT_FRAME_COUNT; i++)
                    stagHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagDeathlyCharge.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_CHARGE_FRAME_COUNT, fh = sheet.getHeight();
                stagDeathlyChargeFrames = new BufferedImage[STAG_CHARGE_FRAME_COUNT];
                for (int i = 0; i < STAG_CHARGE_FRAME_COUNT; i++)
                    stagDeathlyChargeFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagBlackenedHowl.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_HOWL_FRAME_COUNT, fh = sheet.getHeight();
                stagBlackenedHowlFrames = new BufferedImage[STAG_HOWL_FRAME_COUNT];
                for (int i = 0; i < STAG_HOWL_FRAME_COUNT; i++)
                    stagBlackenedHowlFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {
        }

        // Plague Vermin
        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/PlagueVerminIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PLAGUE_VERMIN_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                plagueVerminIdleFrames = new BufferedImage[PLAGUE_VERMIN_IDLE_FRAME_COUNT];
                for (int i = 0; i < PLAGUE_VERMIN_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    plagueVerminIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/PlagueVerminHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PLAGUE_VERMIN_HURT_FRAME_COUNT, fh = sheet.getHeight();
                plagueVerminHurtFrames = new BufferedImage[PLAGUE_VERMIN_HURT_FRAME_COUNT];
                for (int i = 0; i < PLAGUE_VERMIN_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    plagueVerminHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/PlagueVerminPlagueBite.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PLAGUE_VERMIN_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                plagueVerminAttackFrames = new BufferedImage[PLAGUE_VERMIN_ATTACK_FRAME_COUNT];
                for (int i = 0; i < PLAGUE_VERMIN_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    plagueVerminAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/PlagueVerminEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PLAGUE_VERMIN_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                plagueVerminEntranceFrames = new BufferedImage[PLAGUE_VERMIN_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < PLAGUE_VERMIN_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    plagueVerminEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/ForsakenCultistIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FORSAKEN_CULTIST_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                forsakenCultistIdleFrames = new BufferedImage[FORSAKEN_CULTIST_IDLE_FRAME_COUNT];
                for (int i = 0; i < FORSAKEN_CULTIST_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    forsakenCultistIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/ForsakenCultistHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FORSAKEN_CULTIST_HURT_FRAME_COUNT, fh = sheet.getHeight();
                forsakenCultistHurtFrames = new BufferedImage[FORSAKEN_CULTIST_HURT_FRAME_COUNT];
                for (int i = 0; i < FORSAKEN_CULTIST_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    forsakenCultistHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/ForsakenCultistShadowBolt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FORSAKEN_CULTIST_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                forsakenCultistAttackFrames = new BufferedImage[FORSAKEN_CULTIST_ATTACK_FRAME_COUNT];
                for (int i = 0; i < FORSAKEN_CULTIST_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    forsakenCultistAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/ForsakenCultistEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FORSAKEN_CULTIST_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                forsakenCultistEntranceFrames = new BufferedImage[FORSAKEN_CULTIST_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < FORSAKEN_CULTIST_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    forsakenCultistEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlightHoundIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLIGHT_HOUND_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                blightHoundIdleFrames = new BufferedImage[BLIGHT_HOUND_IDLE_FRAME_COUNT];
                for (int i = 0; i < BLIGHT_HOUND_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blightHoundIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlightHoundHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLIGHT_HOUND_HURT_FRAME_COUNT, fh = sheet.getHeight();
                blightHoundHurtFrames = new BufferedImage[BLIGHT_HOUND_HURT_FRAME_COUNT];
                for (int i = 0; i < BLIGHT_HOUND_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blightHoundHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlightHoundCorpseExplosion.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLIGHT_HOUND_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                blightHoundAttackFrames = new BufferedImage[BLIGHT_HOUND_ATTACK_FRAME_COUNT];
                for (int i = 0; i < BLIGHT_HOUND_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blightHoundAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlightHoundEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLIGHT_HOUND_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                blightHoundEntranceFrames = new BufferedImage[BLIGHT_HOUND_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < BLIGHT_HOUND_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blightHoundEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/GhoulFootmanIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / GHOUL_FOOTMAN_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                ghoulFootmanIdleFrames = new BufferedImage[GHOUL_FOOTMAN_IDLE_FRAME_COUNT];
                for (int i = 0; i < GHOUL_FOOTMAN_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    ghoulFootmanIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/GhoulFootmanHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / GHOUL_FOOTMAN_HURT_FRAME_COUNT, fh = sheet.getHeight();
                ghoulFootmanHurtFrames = new BufferedImage[GHOUL_FOOTMAN_HURT_FRAME_COUNT];
                for (int i = 0; i < GHOUL_FOOTMAN_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    ghoulFootmanHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/GhoulFootmanRottenCleave.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / GHOUL_FOOTMAN_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                ghoulFootmanAttackFrames = new BufferedImage[GHOUL_FOOTMAN_ATTACK_FRAME_COUNT];
                for (int i = 0; i < GHOUL_FOOTMAN_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    ghoulFootmanAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/GhoulFootmanEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / GHOUL_FOOTMAN_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                ghoulFootmanEntranceFrames = new BufferedImage[GHOUL_FOOTMAN_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < GHOUL_FOOTMAN_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    ghoulFootmanEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlackJailerIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLACK_JAILER_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                blackJailerIdleFrames = new BufferedImage[BLACK_JAILER_IDLE_FRAME_COUNT];
                for (int i = 0; i < BLACK_JAILER_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blackJailerIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlackJailerHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLACK_JAILER_HURT_FRAME_COUNT, fh = sheet.getHeight();
                blackJailerHurtFrames = new BufferedImage[BLACK_JAILER_HURT_FRAME_COUNT];
                for (int i = 0; i < BLACK_JAILER_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blackJailerHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlackJailerShacklingChains.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLACK_JAILER_CHAINS_FRAME_COUNT, fh = sheet.getHeight();
                blackJailerChainsFrames = new BufferedImage[BLACK_JAILER_CHAINS_FRAME_COUNT];
                for (int i = 0; i < BLACK_JAILER_CHAINS_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blackJailerChainsFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlackJailerTormentingLash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLACK_JAILER_LASH_FRAME_COUNT, fh = sheet.getHeight();
                blackJailerLashFrames = new BufferedImage[BLACK_JAILER_LASH_FRAME_COUNT];
                for (int i = 0; i < BLACK_JAILER_LASH_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blackJailerLashFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/BlackJailerEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLACK_JAILER_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                blackJailerEntranceFrames = new BufferedImage[BLACK_JAILER_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < BLACK_JAILER_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    blackJailerEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonIdleFrames = new BufferedImage[LUTHER_VON_IDLE_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_HURT_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonHurtFrames = new BufferedImage[LUTHER_VON_HURT_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonDefeated.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_DEFEATED_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonDefeatedFrames = new BufferedImage[LUTHER_VON_DEFEATED_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_DEFEATED_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonDefeatedFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonEntranceFrames = new BufferedImage[LUTHER_VON_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonCrownOfDespair.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_CROWN_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonCrownFrames = new BufferedImage[LUTHER_VON_CROWN_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_CROWN_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonCrownFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonDarkAscension.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_ASCENSION_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonAscensionFrames = new BufferedImage[LUTHER_VON_ASCENSION_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_ASCENSION_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonAscensionFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World2EnemyAssets/LutherVonKingsWrath.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / LUTHER_VON_WRATH_FRAME_COUNT, fh = sheet.getHeight();
                lutherVonWrathFrames = new BufferedImage[LUTHER_VON_WRATH_FRAME_COUNT];
                for (int i = 0; i < LUTHER_VON_WRATH_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    lutherVonWrathFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/FlameRevenantIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FLAME_REVENANT_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                flameRevenantIdleFrames = new BufferedImage[FLAME_REVENANT_IDLE_FRAME_COUNT];
                for (int i = 0; i < FLAME_REVENANT_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    flameRevenantIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/FlameRevenantHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FLAME_REVENANT_HURT_FRAME_COUNT, fh = sheet.getHeight();
                flameRevenantHurtFrames = new BufferedImage[FLAME_REVENANT_HURT_FRAME_COUNT];
                for (int i = 0; i < FLAME_REVENANT_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    flameRevenantHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/FlameRevenantEmberBurst.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FLAME_REVENANT_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                flameRevenantAttackFrames = new BufferedImage[FLAME_REVENANT_ATTACK_FRAME_COUNT];
                for (int i = 0; i < FLAME_REVENANT_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    flameRevenantAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/FlameRevenantWalk.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / FLAME_REVENANT_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                flameRevenantEntranceFrames = new BufferedImage[FLAME_REVENANT_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < FLAME_REVENANT_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    flameRevenantEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/BoneWarlockIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BONE_WARLOCK_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                boneWarlockIdleFrames = new BufferedImage[BONE_WARLOCK_IDLE_FRAME_COUNT];
                for (int i = 0; i < BONE_WARLOCK_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    boneWarlockIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/BoneWarlockHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BONE_WARLOCK_HURT_FRAME_COUNT, fh = sheet.getHeight();
                boneWarlockHurtFrames = new BufferedImage[BONE_WARLOCK_HURT_FRAME_COUNT];
                for (int i = 0; i < BONE_WARLOCK_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    boneWarlockHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/BoneWarlockMarrowBolt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BONE_WARLOCK_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                boneWarlockAttackFrames = new BufferedImage[BONE_WARLOCK_ATTACK_FRAME_COUNT];
                for (int i = 0; i < BONE_WARLOCK_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    boneWarlockAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/BoneWarlockWalk.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BONE_WARLOCK_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                boneWarlockEntranceFrames = new BufferedImage[BONE_WARLOCK_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < BONE_WARLOCK_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    boneWarlockEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ObsidianCrusherIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / OBSIDIAN_CRUSHER_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                obsidianCrusherIdleFrames = new BufferedImage[OBSIDIAN_CRUSHER_IDLE_FRAME_COUNT];
                for (int i = 0; i < OBSIDIAN_CRUSHER_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    obsidianCrusherIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ObsidianCrusherHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / OBSIDIAN_CRUSHER_HURT_FRAME_COUNT, fh = sheet.getHeight();
                obsidianCrusherHurtFrames = new BufferedImage[OBSIDIAN_CRUSHER_HURT_FRAME_COUNT];
                for (int i = 0; i < OBSIDIAN_CRUSHER_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    obsidianCrusherHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ObsidianCrusherMagmaSlam.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / OBSIDIAN_CRUSHER_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                obsidianCrusherAttackFrames = new BufferedImage[OBSIDIAN_CRUSHER_ATTACK_FRAME_COUNT];
                for (int i = 0; i < OBSIDIAN_CRUSHER_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    obsidianCrusherAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ObsidianCrusherWalk.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / OBSIDIAN_CRUSHER_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                obsidianCrusherEntranceFrames = new BufferedImage[OBSIDIAN_CRUSHER_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < OBSIDIAN_CRUSHER_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    obsidianCrusherEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/SoulFlayerGargoyleIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SOULFLAYER_GARGOYLE_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                soulflayerGargoyleIdleFrames = new BufferedImage[SOULFLAYER_GARGOYLE_IDLE_FRAME_COUNT];
                for (int i = 0; i < SOULFLAYER_GARGOYLE_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    soulflayerGargoyleIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/SoulFlayerGargoyleHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SOULFLAYER_GARGOYLE_HURT_FRAME_COUNT, fh = sheet.getHeight();
                soulflayerGargoyleHurtFrames = new BufferedImage[SOULFLAYER_GARGOYLE_HURT_FRAME_COUNT];
                for (int i = 0; i < SOULFLAYER_GARGOYLE_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    soulflayerGargoyleHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/SoulFlayerGargoyleSoulScream.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SOULFLAYER_GARGOYLE_ATTACK_FRAME_COUNT, fh = sheet.getHeight();
                soulflayerGargoyleAttackFrames = new BufferedImage[SOULFLAYER_GARGOYLE_ATTACK_FRAME_COUNT];
                for (int i = 0; i < SOULFLAYER_GARGOYLE_ATTACK_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    soulflayerGargoyleAttackFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/SoulFlayerGargoyleWalk.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SOULFLAYER_GARGOYLE_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                soulflayerGargoyleEntranceFrames = new BufferedImage[SOULFLAYER_GARGOYLE_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < SOULFLAYER_GARGOYLE_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    soulflayerGargoyleEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ZyrrylIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ZYRRYL_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                zyrrylIdleFrames = new BufferedImage[ZYRRYL_IDLE_FRAME_COUNT];
                for (int i = 0; i < ZYRRYL_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    zyrrylIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ZyrrylHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ZYRRYL_HURT_FRAME_COUNT, fh = sheet.getHeight();
                zyrrylHurtFrames = new BufferedImage[ZYRRYL_HURT_FRAME_COUNT];
                for (int i = 0; i < ZYRRYL_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    zyrrylHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ZyrrylBoneShield.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ZYRRYL_BONE_SHIELD_FRAME_COUNT, fh = sheet.getHeight();
                zyrrylBoneShieldFrames = new BufferedImage[ZYRRYL_BONE_SHIELD_FRAME_COUNT];
                for (int i = 0; i < ZYRRYL_BONE_SHIELD_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    zyrrylBoneShieldFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ZyrrylGreatCleaver.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ZYRRYL_GREAT_CLEAVER_FRAME_COUNT, fh = sheet.getHeight();
                zyrrylGreatCleaverFrames = new BufferedImage[ZYRRYL_GREAT_CLEAVER_FRAME_COUNT];
                for (int i = 0; i < ZYRRYL_GREAT_CLEAVER_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    zyrrylGreatCleaverFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World3EnemyAssets/ZyrrylWalk.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ZYRRYL_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                zyrrylEntranceFrames = new BufferedImage[ZYRRYL_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < ZYRRYL_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    zyrrylEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/FinalBossAssets/KhaiIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KHAI_NECRO_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                khaiNecroIdleFrames = new BufferedImage[KHAI_NECRO_IDLE_FRAME_COUNT];
                for (int i = 0; i < KHAI_NECRO_IDLE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    khaiNecroIdleFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/FinalBossAssets/KhaiHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KHAI_NECRO_HURT_FRAME_COUNT, fh = sheet.getHeight();
                khaiNecroHurtFrames = new BufferedImage[KHAI_NECRO_HURT_FRAME_COUNT];
                for (int i = 0; i < KHAI_NECRO_HURT_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    khaiNecroHurtFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/FinalBossAssets/KhaiSoulDrain.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KHAI_NECRO_SOUL_DRAIN_FRAME_COUNT, fh = sheet.getHeight();
                khaiNecroSoulDrainFrames = new BufferedImage[KHAI_NECRO_SOUL_DRAIN_FRAME_COUNT];
                for (int i = 0; i < KHAI_NECRO_SOUL_DRAIN_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    khaiNecroSoulDrainFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/FinalBossAssets/KhaiEncapsulation.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KHAI_NECRO_ENCAPSULATION_FRAME_COUNT, fh = sheet.getHeight();
                khaiNecroEncapsulationFrames = new BufferedImage[KHAI_NECRO_ENCAPSULATION_FRAME_COUNT];
                for (int i = 0; i < KHAI_NECRO_ENCAPSULATION_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    khaiNecroEncapsulationFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/FinalBossAssets/KhaiDarkAscension.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KHAI_NECRO_DARK_ASCENSION_FRAME_COUNT, fh = sheet.getHeight();
                khaiNecroDarkAscensionFrames = new BufferedImage[KHAI_NECRO_DARK_ASCENSION_FRAME_COUNT];
                for (int i = 0; i < KHAI_NECRO_DARK_ASCENSION_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    khaiNecroDarkAscensionFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/FinalBossAssets/KhaiWalk.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KHAI_NECRO_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                khaiNecroEntranceFrames = new BufferedImage[KHAI_NECRO_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < KHAI_NECRO_ENTRANCE_FRAME_COUNT; i++) {
                    BufferedImage frame = sheet.getSubimage(i * fw, 0, fw, fh);
                    BufferedImage argb = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    argb.getGraphics().drawImage(frame, 0, 0, null);
                    khaiNecroEntranceFrames[i] = argb;
                }
            }
        } catch (Exception ex) {}

        JLabel sprite = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage[] frames;
                boolean isShade = enemyDef != null && enemyDef.name.equals("Shade Sprite");
                boolean isStag = enemyDef != null && enemyDef.name.equals("The Hollow Stag");
                boolean isTreant = enemyDef != null && enemyDef.name.equals("Dreadbark Treant");

                if (isPlayingWolfHurt) frames = wolfHurtFrames;
                else if (isPlayingWolfSavageHowl) frames = wolfSavageHowlFrames;
                else if (isPlayingWolfDefeat) frames = wolfDefeatFrames;
                else if (isPlayingWolfEntrance) frames = wolfEntranceFrames;
                else if (isPlayingSpriteHurt) frames = spriteHurtFrames;
                else if (isPlayingSpriteTrickster) frames = spriteTricksterFrames;
                else if (isPlayingSpriteDefeat)    frames = spriteDefeatFrames;
                else if (isPlayingSpriteEntrance)  frames = spriteEntranceFrames;
                else if (isPlayingStagHurt)        frames = stagHurtFrames;
                else if (isPlayingStagCharge)      frames = stagDeathlyChargeFrames;
                else if (isPlayingStagHowl)        frames = stagBlackenedHowlFrames;
                else if (isPlayingStagEntrance)    frames = stagEntranceFrames;
                else if (isPlayingStagDefeat)      frames = stagHurtFrames;
                else if (isPlayingTreantHurt)      frames = treantHurtFrames;
                else if (isPlayingTreantAttack)    frames = treantAttackFrames;
                else if (isPlayingTreantDefeat)    frames = treantHurtFrames;
                else if (isPlayingTreantEntrance)  frames = treantEntranceFrames;
                else if (isPlayingBatHurt)      frames = batHurtFrames;
                else if (isPlayingBatAttack)    frames = batAttackFrames;
                else if (isPlayingBatDefeat)    frames = batHurtFrames;
                else if (isPlayingBatEntrance)  frames = batEntranceFrames;
                else if (isPlayingPlagueVerminHurt)         frames = plagueVerminHurtFrames;
                else if (isPlayingPlagueVerminAttack)       frames = plagueVerminAttackFrames;
                else if (isPlayingPlagueVerminDefeat)       frames = plagueVerminHurtFrames;
                else if (isPlayingPlagueVerminEntrance)     frames = plagueVerminEntranceFrames;
                else if (isPlayingForsakenCultistHurt)      frames = forsakenCultistHurtFrames;      // ADD
                else if (isPlayingForsakenCultistAttack)    frames = forsakenCultistAttackFrames;    // ADD
                else if (isPlayingForsakenCultistDefeat)    frames = forsakenCultistHurtFrames;      // ADD
                else if (isPlayingForsakenCultistEntrance)  frames = forsakenCultistEntranceFrames;  // ADD
                else if (isPlayingBlightHoundHurt)      frames = blightHoundHurtFrames;
                else if (isPlayingBlightHoundAttack)    frames = blightHoundAttackFrames;
                else if (isPlayingBlightHoundDefeat)    frames = blightHoundHurtFrames;
                else if (isPlayingBlightHoundEntrance)  frames = blightHoundEntranceFrames;
                else if (isPlayingGhoulFootmanHurt)     frames = ghoulFootmanHurtFrames;
                else if (isPlayingGhoulFootmanAttack)   frames = ghoulFootmanAttackFrames;
                else if (isPlayingGhoulFootmanDefeat)   frames = ghoulFootmanHurtFrames;
                else if (isPlayingGhoulFootmanEntrance) frames = ghoulFootmanEntranceFrames;
                else if (isPlayingBlackJailerHurt)     frames = blackJailerHurtFrames;
                else if (isPlayingBlackJailerChains)   frames = blackJailerChainsFrames;
                else if (isPlayingBlackJailerLash)     frames = blackJailerLashFrames;
                else if (isPlayingBlackJailerDefeat)   frames = blackJailerHurtFrames;
                else if (isPlayingBlackJailerEntrance) frames = blackJailerEntranceFrames;
                else if (isPlayingLutherVonHurt)      frames = lutherVonHurtFrames;
                else if (isPlayingLutherVonCrown)     frames = lutherVonCrownFrames;
                else if (isPlayingLutherVonAscension) frames = lutherVonAscensionFrames;
                else if (isPlayingLutherVonWrath)     frames = lutherVonWrathFrames;
                else if (isPlayingLutherVonDefeat)    frames = lutherVonDefeatedFrames;
                else if (isPlayingLutherVonEntrance)  frames = lutherVonEntranceFrames;
                else if (isPlayingFlameRevenantHurt)     frames = flameRevenantHurtFrames;
                else if (isPlayingFlameRevenantAttack)   frames = flameRevenantAttackFrames;
                else if (isPlayingFlameRevenantDefeat)   frames = flameRevenantHurtFrames;
                else if (isPlayingFlameRevenantEntrance) frames = flameRevenantEntranceFrames;
                else if (isPlayingBoneWarlockHurt)     frames = boneWarlockHurtFrames;
                else if (isPlayingBoneWarlockAttack)   frames = boneWarlockAttackFrames;
                else if (isPlayingBoneWarlockDefeat)   frames = boneWarlockHurtFrames;
                else if (isPlayingBoneWarlockEntrance) frames = boneWarlockEntranceFrames;
                else if (isPlayingObsidianCrusherHurt)     frames = obsidianCrusherHurtFrames;
                else if (isPlayingObsidianCrusherAttack)   frames = obsidianCrusherAttackFrames;
                else if (isPlayingObsidianCrusherDefeat)   frames = obsidianCrusherHurtFrames;
                else if (isPlayingObsidianCrusherEntrance) frames = obsidianCrusherEntranceFrames;
                else if (isPlayingSoulflayerGargoyleHurt)     frames = soulflayerGargoyleHurtFrames;
                else if (isPlayingSoulflayerGargoyleAttack)   frames = soulflayerGargoyleAttackFrames;
                else if (isPlayingSoulflayerGargoyleDefeat)   frames = soulflayerGargoyleHurtFrames;
                else if (isPlayingSoulflayerGargoyleEntrance) frames = soulflayerGargoyleEntranceFrames;
                else if (isPlayingZyrrylHurt)         frames = zyrrylHurtFrames;
                else if (isPlayingZyrrylBoneShield)   frames = zyrrylBoneShieldFrames;
                else if (isPlayingZyrrylGreatCleaver) frames = zyrrylGreatCleaverFrames;
                else if (isPlayingZyrrylDefeat)       frames = zyrrylHurtFrames;
                else if (isPlayingZyrrylEntrance)     frames = zyrrylEntranceFrames;
                else if (isPlayingKhaiNecroHurt)          frames = khaiNecroHurtFrames;
                else if (isPlayingKhaiNecroSoulDrain)     frames = khaiNecroSoulDrainFrames;
                else if (isPlayingKhaiNecroEncapsulation) frames = khaiNecroEncapsulationFrames;
                else if (isPlayingKhaiNecroDarkAscension) frames = khaiNecroDarkAscensionFrames;
                else if (isPlayingKhaiNecroDefeat)        frames = khaiNecroHurtFrames;
                else if (isPlayingKhaiNecroEntrance)      frames = khaiNecroEntranceFrames;
                else if (isKhaiNecro())                   frames = khaiNecroIdleFrames;
                else if (isStag)                            frames = stagIdleFrames;
                else if (isStag)                   frames = stagIdleFrames;
                else if (isTreant)                 frames = treantIdleFrames;
                else if (isBat())                  frames = batIdleFrames;   // ADD
                else if (isPlagueVermin())         frames = plagueVerminIdleFrames;
                else if (isForsakenCultist())      frames = forsakenCultistIdleFrames;
                else if (isBlightHound())          frames = blightHoundIdleFrames;
                else if (isGhoulFootman())         frames = ghoulFootmanIdleFrames;
                else if (isBlackJailer())          frames = blackJailerIdleFrames;
                else if (isLutherVon())        frames = lutherVonIdleFrames;
                else if (isFlameRevenant())        frames = flameRevenantIdleFrames;
                else if (isBoneWarlock())    frames = boneWarlockIdleFrames;
                else if (isObsidianCrusher())    frames = obsidianCrusherIdleFrames;
                else if (isSoulflayerGargoyle()) frames = soulflayerGargoyleIdleFrames;
                else if (isZyrryl()) frames = zyrrylIdleFrames;
                else if (isShade)                  frames = spriteIdleFrames;
                else                               frames = wolfIdleFrames;

                if (frames == null || enemySpriteFrame >= frames.length) return;
                BufferedImage frame = frames[enemySpriteFrame];
                if (frame == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isTreantEnemy = enemyDef != null && enemyDef.name.equals("Dreadbark Treant");
                boolean isTreantEntrance = isTreantEnemy && isPlayingTreantEntrance;
                boolean isTreantAttack   = isTreantEnemy && isPlayingTreantAttack;
                boolean isBatEnemy          = enemyDef != null && enemyDef.name.equals("Carrion Bat");
                boolean isPlagueVerminEnemy = enemyDef != null && enemyDef.name.equals("Plague Vermin");
                boolean isForsakenCultistEnemy = enemyDef != null && enemyDef.name.equals("Forsaken Cultist");
                boolean isBlightHoundEnemy = enemyDef != null && enemyDef.name.equals("Blight Hound");
                boolean isGhoulFootmanEnemy = enemyDef != null && enemyDef.name.equals("Ghoul Footman");
                boolean isBlackJailerEnemy = enemyDef != null && enemyDef.name.equals("The Black Jailer");
                boolean isLutherVonEnemy = enemyDef != null && enemyDef.name.equals("Luther Von");


                double scale = isTreantEntrance    ? TREANT_SCALE
                        : isTreantAttack       ? TREANT_ATTACK_SCALE
                        : isTreantEnemy        ? TREANT_IDLE_SCALE
                        : isBatEnemy           ? BAT_SCALE
                        : isPlagueVerminEnemy  ? PLAGUE_VERMIN_SCALE
                        : isForsakenCultistEnemy ? FORSAKEN_CULTIST_SCALE
                        : isBlightHoundEnemy   ? BLIGHT_HOUND_SCALE      // ADD
                        : isGhoulFootmanEnemy  ? GHOUL_FOOTMAN_SCALE
                        : isBlackJailerEnemy   ? BLACK_JAILER_SCALE
                        : isLutherVonEnemy     ? LUTHER_VON_SCALE
                        : isFlameRevenant()    ? FLAME_REVENANT_SCALE
                        : isBoneWarlock()      ? BONE_WARLOCK_SCALE
                        : isObsidianCrusher()    ? OBSIDIAN_CRUSHER_SCALE
                        : isSoulflayerGargoyle() ? SOULFLAYER_GARGOYLE_SCALE
                        : isZyrryl() ? ZYRRYL_SCALE
                        : isKhaiNecro()        ? KHAI_NECRO_SCALE
                        : ENEMY_SCALE;
                g2.drawImage(frame, 0, 0, (int) (frame.getWidth() * scale), (int) (frame.getHeight() * scale), null);


            }
        };

        // Size the label to the largest frame across all enemies so nothing overflows
        int labelW = 0, labelH = 0;
        if (wolfIdleFrames != null) {
            labelW = Math.max(labelW, (int) (wolfIdleFrames[0].getWidth() * ENEMY_SCALE));
            labelH = Math.max(labelH, (int) (wolfIdleFrames[0].getHeight() * ENEMY_SCALE));
        }
        if (spriteIdleFrames != null) {
            labelW = Math.max(labelW, (int) (spriteIdleFrames[0].getWidth() * ENEMY_SCALE));
            labelH = Math.max(labelH, (int) (spriteIdleFrames[0].getHeight() * ENEMY_SCALE));
        }
        if (treantIdleFrames != null) {
            labelW = Math.max(labelW, (int) (treantIdleFrames[0].getWidth() * TREANT_IDLE_SCALE));
            labelH = Math.max(labelH, (int) (treantIdleFrames[0].getHeight() * TREANT_IDLE_SCALE));
        }
        if (batIdleFrames != null) {
            labelW = Math.max(labelW, (int) (batIdleFrames[0].getWidth() * BAT_SCALE));
            labelH = Math.max(labelH, (int) (batIdleFrames[0].getHeight() * BAT_SCALE));
        }
        if (stagIdleFrames != null) {
            labelW = Math.max(labelW, (int) (stagIdleFrames[0].getWidth() * ENEMY_SCALE));
            labelH = Math.max(labelH, (int) (stagIdleFrames[0].getHeight() * ENEMY_SCALE));
        }
        if (plagueVerminIdleFrames != null) {
            labelW = Math.max(labelW, (int)(plagueVerminIdleFrames[0].getWidth()  * PLAGUE_VERMIN_SCALE));
            labelH = Math.max(labelH, (int)(plagueVerminIdleFrames[0].getHeight() * PLAGUE_VERMIN_SCALE));
        }
        if (forsakenCultistIdleFrames != null) {
            labelW = Math.max(labelW, (int)(forsakenCultistIdleFrames[0].getWidth() * FORSAKEN_CULTIST_SCALE));
            labelH = Math.max(labelH, (int)(forsakenCultistIdleFrames[0].getHeight() * FORSAKEN_CULTIST_SCALE));
        }
        if (blightHoundIdleFrames != null) {
            labelW = Math.max(labelW, (int)(blightHoundIdleFrames[0].getWidth()  * BLIGHT_HOUND_SCALE));
            labelH = Math.max(labelH, (int)(blightHoundIdleFrames[0].getHeight() * BLIGHT_HOUND_SCALE));
        }
        if (ghoulFootmanIdleFrames != null) {
            labelW = Math.max(labelW, (int)(ghoulFootmanIdleFrames[0].getWidth()  * GHOUL_FOOTMAN_SCALE));
            labelH = Math.max(labelH, (int)(ghoulFootmanIdleFrames[0].getHeight() * GHOUL_FOOTMAN_SCALE));
        }
        if (blackJailerIdleFrames != null) {
            labelW = Math.max(labelW, (int)(blackJailerIdleFrames[0].getWidth()  * BLACK_JAILER_SCALE));
            labelH = Math.max(labelH, (int)(blackJailerIdleFrames[0].getHeight() * BLACK_JAILER_SCALE));
        }
        if (lutherVonIdleFrames != null) {
            labelW = Math.max(labelW, (int)(lutherVonIdleFrames[0].getWidth()  * LUTHER_VON_SCALE));
            labelH = Math.max(labelH, (int)(lutherVonIdleFrames[0].getHeight() * LUTHER_VON_SCALE));
        }
        if (flameRevenantIdleFrames != null) {
            labelW = Math.max(labelW, (int)(flameRevenantIdleFrames[0].getWidth()  * FLAME_REVENANT_SCALE));
            labelH = Math.max(labelH, (int)(flameRevenantIdleFrames[0].getHeight() * FLAME_REVENANT_SCALE));
        }
        if (boneWarlockIdleFrames != null) {
            labelW = Math.max(labelW, (int)(boneWarlockIdleFrames[0].getWidth()  * BONE_WARLOCK_SCALE));
            labelH = Math.max(labelH, (int)(boneWarlockIdleFrames[0].getHeight() * BONE_WARLOCK_SCALE));
        }
        if (obsidianCrusherIdleFrames != null) {
            labelW = Math.max(labelW, (int)(obsidianCrusherIdleFrames[0].getWidth()  * OBSIDIAN_CRUSHER_SCALE));
            labelH = Math.max(labelH, (int)(obsidianCrusherIdleFrames[0].getHeight() * OBSIDIAN_CRUSHER_SCALE));
        }
        if (soulflayerGargoyleIdleFrames != null) {
            labelW = Math.max(labelW, (int)(soulflayerGargoyleIdleFrames[0].getWidth()  * SOULFLAYER_GARGOYLE_SCALE));
            labelH = Math.max(labelH, (int)(soulflayerGargoyleIdleFrames[0].getHeight() * SOULFLAYER_GARGOYLE_SCALE));
        }
        if (zyrrylIdleFrames != null) {
            labelW = Math.max(labelW, (int)(zyrrylIdleFrames[0].getWidth()  * ZYRRYL_SCALE));
            labelH = Math.max(labelH, (int)(zyrrylIdleFrames[0].getHeight() * ZYRRYL_SCALE));
        }
        if (khaiNecroIdleFrames != null) {
            labelW = Math.max(labelW, (int)(khaiNecroIdleFrames[0].getWidth()  * KHAI_NECRO_SCALE));
            labelH = Math.max(labelH, (int)(khaiNecroIdleFrames[0].getHeight() * KHAI_NECRO_SCALE));
        }

        if (labelW == 0) labelW = SPRITE_W;
        if (labelH == 0) labelH = SPRITE_H;

        sprite.setBounds(ENEMY_X, ENEMY_Y, labelW, labelH);
        sprite.setOpaque(false);
        sprite.setVisible(false);
        return sprite;
    }

    private BufferedImage removeBlackBg(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < src.getWidth(); x++)
            for (int y = 0; y < src.getHeight(); y++) {
                int px = src.getRGB(x, y);
                int r = (px >> 16) & 0xFF;
                int g = (px >> 8) & 0xFF;
                int b = px & 0xFF;
                // Only remove pixels that are very close to pure black
                // AND are near the border or surrounded by other black pixels
                boolean isPureBlack = r < 15 && g < 15 && b < 15;
                out.setRGB(x, y, isPureBlack ? 0x00000000 : px);
            }
        return out;
    }

    private int getHeroIdleX() {
        if (currentBattleBgPath.contains("World3BG21.5")) {
            return IDLE_X - 30;
        }
        if (currentBattleBgPath.contains("World3BG15.5")) {
            return IDLE_X + 100;
        }
        return IDLE_X;
    }

    private int getHeroActionX_W3() {
        if (currentBattleBgPath.contains("World3BG21.5")) {
            return ACTION_X_BASE - 30;
        }
        if (currentBattleBgPath.contains("World3BG15.5")) {
            return ACTION_X_BASE + 100;
        }
        return ACTION_X_BASE;
    }

    private int getHeroIdleY() {
        if (currentBattleBgPath.contains("ForsakenCultist") || currentBattleBgPath.contains("World2BattleBackground2")) {
            return IDLE_Y_W2 + 70;
        }
        if (currentBattleBgPath.contains("World2BattleBackground3")) {
            return IDLE_Y_W2 + 60;
        }
        if (currentBattleBgPath.contains("World2BattleBackground4")) {
            return IDLE_Y_W2 + 120;
        }
        if (currentBattleBgPath.contains("World2BattleBackground5")) {
            return IDLE_Y_W2 + 160;
        }
        if (currentBattleBgPath.contains("World2BattleBackgroundLast")) {
            return IDLE_Y_W2 + 80;
        }
        if (currentBattleBgPath.contains("World3BG15.5")) {
            return IDLE_Y + 80;
        }
        if (currentBattleBgPath.contains("World3BG21.5")) {
            return IDLE_Y + 80;
        }
        if (currentBattleBgPath.contains("World3BG27")) {
            return IDLE_Y - 100;
        }
        if (currentBattleBgPath.contains("World3BG30.5")) {
            return IDLE_Y - 50;
        }
        if (currentBattleBgPath.contains("NecroBackground")){
            return IDLE_Y + 80;
        }
        return isWorld2Battle ? IDLE_Y_W2 : IDLE_Y;
    }

    private int getHeroActionY() {
        if (currentBattleBgPath.contains("ForsakenCultist") || currentBattleBgPath.contains("World2BattleBackground2")) {
            return ACTION_Y_W2 + 70;
        }
        if (currentBattleBgPath.contains("World2BattleBackground3")) {
            return ACTION_Y_W2 + 60;
        }
        if (currentBattleBgPath.contains("World2BattleBackground4")) {
            return ACTION_Y_W2 + 120;
        }
        if (currentBattleBgPath.contains("World2BattleBackground5")) {
            return ACTION_Y_W2 + 160;
        }
        if (currentBattleBgPath.contains("World2BattleBackgroundLast")) {
            return ACTION_Y_W2 + 80;
        }
        if (currentBattleBgPath.contains("World3BG15.5")) {
            return ACTION_Y + 80;
        }
        if (currentBattleBgPath.contains("World3BG21.5")) {
            return ACTION_Y + 80;
        }
        if (currentBattleBgPath.contains("World3BG27")) {
            return ACTION_Y - 100;
        }
        if (currentBattleBgPath.contains("World3BG30.5")) {
            return ACTION_Y - 50;
        }
        if (currentBattleBgPath.contains("NecroBackground")){
            return ACTION_Y+ 80;
        }
        return isWorld2Battle ? ACTION_Y_W2 : ACTION_Y;
    }

    private boolean isKarlHero() {
        return heroDef != null && heroDef.name.equals("Karl Clover Dior IV");
    }

    //Enemy
    private int getEnemyActionY() {
        if (currentBattleBgPath.contains("World2BattleBackground3")) {
            return BLIGHT_HOUND_Y;
        }
        return ENEMY_Y;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ ANIMATION TRIGGERS
    // ════════════════════════════════════════════════════════════════════════
    private void startHeroIdleAnimation() {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        isPlayingBladeRush = false; isPlayingPiercingSlash = false;
        isPlayingEternalCross = false; isPlayingKaelHurt = false;
        isPlayingKarlHurt = false; isPlayingKarlPiercingShot = false;
        isPlayingKarlBullseye = false; isPlayingKarlRain = false;
        heroSpriteFrame = 0;

        boolean karl = isKarlHero();

        // ★ DEBUG — remove these after fixing
        System.out.println("Karl frames loaded: " + (karlIdleFrames != null));
        System.out.println("isKarlHero: " + karl);
        System.out.println("heroDef name: " + (heroDef != null ? heroDef.name : "null"));

        BufferedImage[] heroIdle = karl ? karlIdleFrames : idleFrames;
        int frameCount = karl ? KARL_IDLE_FRAME_COUNT : SPRITE_FRAME_COUNT;

        if (heroIdle != null) {
            int w = (int) (heroIdle[0].getWidth() * SPRITE_SCALE);
            int h = (int) (heroIdle[0].getHeight() * SPRITE_SCALE);
            heroSpriteLabel.setBounds(getHeroIdleX(), getHeroIdleY(), w, h);
        }
        heroIdleTimer = new javax.swing.Timer(220, e -> {
            heroSpriteFrame = (heroSpriteFrame + 1) % frameCount;
            if (heroSpriteLabel != null) heroSpriteLabel.repaint();
        });
        heroIdleTimer.start();
    }

    private void startEnemyIdleAnimation(EnemyDefinition eDef) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        isPlayingWolfHurt = false; isPlayingWolfSavageHowl = false;
        isPlayingWolfDefeat = false; isPlayingWolfEntrance = false;
        isPlayingSpriteHurt = false; isPlayingSpriteTrickster = false;
        isPlayingSpriteDefeat = false; isPlayingSpriteEntrance = false;
        isPlayingTreantHurt = false; isPlayingTreantAttack = false;
        isPlayingTreantDefeat = false; isPlayingTreantEntrance = false;
        isPlayingBatHurt = false; isPlayingBatAttack = false;
        isPlayingBatDefeat = false; isPlayingBatEntrance = false;
        isPlayingStagHurt = false; isPlayingStagCharge = false;
        isPlayingStagHowl = false; isPlayingStagDefeat = false; isPlayingStagEntrance = false;
        isPlayingPlagueVerminHurt = false; isPlayingPlagueVerminAttack = false;
        isPlayingPlagueVerminDefeat = false; isPlayingPlagueVerminEntrance = false;
        isPlayingForsakenCultistHurt = false; isPlayingForsakenCultistAttack = false;
        isPlayingForsakenCultistDefeat = false; isPlayingForsakenCultistEntrance = false;
        isPlayingBlightHoundHurt = false; isPlayingBlightHoundAttack = false;
        isPlayingBlightHoundDefeat = false; isPlayingBlightHoundEntrance = false;
        isPlayingGhoulFootmanHurt = false; isPlayingGhoulFootmanAttack = false;
        isPlayingGhoulFootmanDefeat = false; isPlayingGhoulFootmanEntrance = false;
        isPlayingBlackJailerHurt = false; isPlayingBlackJailerChains = false;
        isPlayingBlackJailerLash = false; isPlayingBlackJailerDefeat = false;
        isPlayingBlackJailerEntrance = false;
        isPlayingLutherVonHurt = false; isPlayingLutherVonCrown = false;
        isPlayingLutherVonAscension = false; isPlayingLutherVonWrath = false;
        isPlayingLutherVonDefeat = false; isPlayingLutherVonEntrance = false;
        isPlayingFlameRevenantHurt = false; isPlayingFlameRevenantAttack = false;
        isPlayingFlameRevenantDefeat = false; isPlayingFlameRevenantEntrance = false;
        isPlayingBoneWarlockHurt = false; isPlayingBoneWarlockAttack = false;
        isPlayingBoneWarlockDefeat = false; isPlayingBoneWarlockEntrance = false;
        isPlayingObsidianCrusherHurt = false; isPlayingObsidianCrusherAttack = false;
        isPlayingObsidianCrusherDefeat = false; isPlayingObsidianCrusherEntrance = false;
        isPlayingSoulflayerGargoyleHurt = false; isPlayingSoulflayerGargoyleAttack = false;
        isPlayingSoulflayerGargoyleDefeat = false; isPlayingSoulflayerGargoyleEntrance = false;
        isPlayingZyrrylHurt = false; isPlayingZyrrylBoneShield = false;
        isPlayingZyrrylGreatCleaver = false; isPlayingZyrrylDefeat = false;
        isPlayingZyrrylEntrance = false;
        isPlayingKhaiNecroHurt = false; isPlayingKhaiNecroSoulDrain = false;
        isPlayingKhaiNecroEncapsulation = false; isPlayingKhaiNecroDarkAscension = false;
        isPlayingKhaiNecroDefeat = false; isPlayingKhaiNecroEntrance = false;
        enemySpriteFrame = 0;

        if (eDef.name.equals("Rotfang Wolf") && wolfIdleFrames != null) {
            int w = (int) (wolfIdleFrames[0].getWidth() * ENEMY_SCALE);
            int h = (int) (wolfIdleFrames[0].getHeight() * ENEMY_SCALE);
            enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(WOLF_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % WOLF_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Shade Sprite") && spriteIdleFrames != null) {
            int w = (int) (spriteIdleFrames[0].getWidth() * ENEMY_SCALE);
            int h = (int) (spriteIdleFrames[0].getHeight() * ENEMY_SCALE);
            enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(220, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % SPRITE_IDLE_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("The Hollow Stag") && stagIdleFrames != null) {
            int w = (int) (stagIdleFrames[0].getWidth() * ENEMY_SCALE);
            int h = (int) (stagIdleFrames[0].getHeight() * ENEMY_SCALE);
            enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(STAG_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % STAG_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start(); // THIS WAS MISSING
        } else if (eDef.name.equals("Dreadbark Treant") && treantIdleFrames != null) {
            int w = (int) (treantIdleFrames[0].getWidth() * TREANT_IDLE_SCALE);
            int h = (int) (treantIdleFrames[0].getHeight() * TREANT_IDLE_SCALE);
            enemySpriteLabel.setBounds(TREANT_X, TREANT_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(TREANT_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % TREANT_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Carrion Bat") && batIdleFrames != null) {
            int w = (int) (batIdleFrames[0].getWidth() * BAT_SCALE);
            int h = (int) (batIdleFrames[0].getHeight() * BAT_SCALE);
            enemySpriteLabel.setBounds(BAT_X, BAT_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(BAT_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % BAT_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Plague Vermin") && plagueVerminIdleFrames != null) {
            int w = (int)(plagueVerminIdleFrames[0].getWidth()  * PLAGUE_VERMIN_SCALE);
            int h = (int)(plagueVerminIdleFrames[0].getHeight() * PLAGUE_VERMIN_SCALE);
            enemySpriteLabel.setBounds(PLAGUE_VERMIN_X, PLAGUE_VERMIN_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(PLAGUE_VERMIN_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % PLAGUE_VERMIN_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Forsaken Cultist") && forsakenCultistIdleFrames != null) {
            int w = (int) (forsakenCultistIdleFrames[0].getWidth() * FORSAKEN_CULTIST_SCALE);
            int h = (int) (forsakenCultistIdleFrames[0].getHeight() * FORSAKEN_CULTIST_SCALE);
            enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y - 70, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(FORSAKEN_CULTIST_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % FORSAKEN_CULTIST_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Blight Hound") && blightHoundIdleFrames != null) {
        int w = (int)(blightHoundIdleFrames[0].getWidth()  * BLIGHT_HOUND_SCALE);
        int h = (int)(blightHoundIdleFrames[0].getHeight() * BLIGHT_HOUND_SCALE);
        enemySpriteLabel.setBounds(BLIGHT_HOUND_X, BLIGHT_HOUND_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemyIdleTimer = new javax.swing.Timer(BLIGHT_HOUND_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % BLIGHT_HOUND_IDLE_FRAME_COUNT;
            if (enemySpriteLabel != null) enemySpriteLabel.repaint();
        });
        enemyIdleTimer.start();
        } else if (eDef.name.equals("Ghoul Footman") && ghoulFootmanIdleFrames != null) {
            int w = (int) (ghoulFootmanIdleFrames[0].getWidth() * GHOUL_FOOTMAN_SCALE);
            int h = (int) (ghoulFootmanIdleFrames[0].getHeight() * GHOUL_FOOTMAN_SCALE);
            enemySpriteLabel.setBounds(GHOUL_FOOTMAN_X, GHOUL_FOOTMAN_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(GHOUL_FOOTMAN_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % GHOUL_FOOTMAN_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("The Black Jailer") && blackJailerIdleFrames != null) {
            int w = (int) (blackJailerIdleFrames[0].getWidth() * BLACK_JAILER_SCALE);
            int h = (int) (blackJailerIdleFrames[0].getHeight() * BLACK_JAILER_SCALE);
            enemySpriteLabel.setBounds(BLACK_JAILER_X, BLACK_JAILER_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(BLACK_JAILER_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % BLACK_JAILER_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Luther Von") && lutherVonIdleFrames != null) {
            int w = (int) (lutherVonIdleFrames[0].getWidth() * LUTHER_VON_SCALE);
            int h = (int) (lutherVonIdleFrames[0].getHeight() * LUTHER_VON_SCALE);
            enemySpriteLabel.setBounds(LUTHER_VON_X, LUTHER_VON_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(LUTHER_VON_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % LUTHER_VON_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        }else if (eDef.name.equals("Flame Revenant") && flameRevenantIdleFrames != null) {
            int w = (int) (flameRevenantIdleFrames[0].getWidth() * FLAME_REVENANT_SCALE);
            int h = (int) (flameRevenantIdleFrames[0].getHeight() * FLAME_REVENANT_SCALE);
            enemySpriteLabel.setBounds(FLAME_REVENANT_X, FLAME_REVENANT_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(FLAME_REVENANT_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % FLAME_REVENANT_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Bone Warlock") && boneWarlockIdleFrames != null) {
            int w = (int) (boneWarlockIdleFrames[0].getWidth() * BONE_WARLOCK_SCALE);
            int h = (int) (boneWarlockIdleFrames[0].getHeight() * BONE_WARLOCK_SCALE);
            enemySpriteLabel.setBounds(BONE_WARLOCK_X, BONE_WARLOCK_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(BONE_WARLOCK_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % BONE_WARLOCK_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Obsidian Crusher") && obsidianCrusherIdleFrames != null) {
            int w = (int) (obsidianCrusherIdleFrames[0].getWidth() * OBSIDIAN_CRUSHER_SCALE);
            int h = (int) (obsidianCrusherIdleFrames[0].getHeight() * OBSIDIAN_CRUSHER_SCALE);
            enemySpriteLabel.setBounds(OBSIDIAN_CRUSHER_X, OBSIDIAN_CRUSHER_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(OBSIDIAN_CRUSHER_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % OBSIDIAN_CRUSHER_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Soulflayer Gargoyle") && soulflayerGargoyleIdleFrames != null) {
            int w = (int) (soulflayerGargoyleIdleFrames[0].getWidth() * SOULFLAYER_GARGOYLE_SCALE);
            int h = (int) (soulflayerGargoyleIdleFrames[0].getHeight() * SOULFLAYER_GARGOYLE_SCALE);
            enemySpriteLabel.setBounds(SOULFLAYER_GARGOYLE_X, SOULFLAYER_GARGOYLE_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(SOULFLAYER_GARGOYLE_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % SOULFLAYER_GARGOYLE_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Zyrryl") && zyrrylIdleFrames != null) {
            int w = (int) (zyrrylIdleFrames[0].getWidth() * ZYRRYL_SCALE);
            int h = (int) (zyrrylIdleFrames[0].getHeight() * ZYRRYL_SCALE);
            enemySpriteLabel.setBounds(ZYRRYL_X, ZYRRYL_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(ZYRRYL_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % ZYRRYL_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Khai the Necromancer") && khaiNecroIdleFrames != null) {
            int w = (int)(khaiNecroIdleFrames[0].getWidth()  * KHAI_NECRO_SCALE);
            int h = (int)(khaiNecroIdleFrames[0].getHeight() * KHAI_NECRO_SCALE);
            enemySpriteLabel.setBounds(KHAI_NECRO_X, KHAI_NECRO_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(KHAI_NECRO_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % KHAI_NECRO_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        }else {
            enemySpriteLabel.setVisible(false);
        }
    }

    private void stopEnemyAnimation() {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (enemySpriteLabel != null) enemySpriteLabel.setVisible(false);
    }

    //Helper Methods
    private boolean isShadeSprite() { return enemyDef != null && enemyDef.name.equals("Shade Sprite"); }
    private boolean isTreant() { return enemyDef != null && enemyDef.name.equals("Dreadbark Treant"); }
    private boolean isBat() { return enemyDef != null && enemyDef.name.equals("Carrion Bat"); }
    private boolean isPlagueVermin() { return enemyDef != null && enemyDef.name.equals("Plague Vermin"); }
    private boolean isForsakenCultist() { return enemyDef != null && enemyDef.name.equals("Forsaken Cultist"); }
    private boolean isBlightHound() { return enemyDef != null && enemyDef.name.equals("Blight Hound"); }
    private boolean isGhoulFootman() { return enemyDef != null && enemyDef.name.equals("Ghoul Footman"); }
    private boolean isBlackJailer() { return enemyDef != null && enemyDef.name.equals("The Black Jailer"); }
    private boolean isLutherVon() { return enemyDef != null && enemyDef.name.equals("Luther Von"); }
    private boolean isFlameRevenant() { return enemyDef != null && enemyDef.name.equals("Flame Revenant"); }
    private boolean isBoneWarlock() { return enemyDef != null && enemyDef.name.equals("Bone Warlock"); }
    private boolean isObsidianCrusher() { return enemyDef != null && enemyDef.name.equals("Obsidian Crusher"); }
    private boolean isSoulflayerGargoyle() { return enemyDef != null && enemyDef.name.equals("Soulflayer Gargoyle"); }
    private boolean isZyrryl() { return enemyDef != null && enemyDef.name.equals("Zyrryl"); }
    private boolean isKhaiNecro() { return enemyDef != null && enemyDef.name.equals("Khai the Necromancer"); }


    private boolean isStag() {
        return enemyDef != null && enemyDef.name.equals("The Hollow Stag");
    }

    private void playEnemyHurt(Runnable onDone) {
        if (isStag())                  playStagHurtAnimation(onDone);
        else if (isTreant())           playTreantHurtAnimation(onDone);
        else if (isBat())              playBatHurtAnimation(onDone);
        else if (isPlagueVermin())     playPlagueVerminHurtAnimation(onDone);
        else if (isForsakenCultist())  playForsakenCultistHurtAnimation(onDone);
        else if (isBlightHound())      playBlightHoundHurtAnimation(onDone);
        else if (isShadeSprite())      playSpriteHurtAnimation(onDone);
        else if (isGhoulFootman())     playGhoulFootmanHurtAnimation(onDone);
        else if (isBlackJailer())      playBlackJailerHurtAnimation(onDone);
        else if (isLutherVon())        playLutherVonHurtAnimation(onDone);
        else if (isFlameRevenant())    playFlameRevenantHurtAnimation(onDone);
        else if (isBoneWarlock())      playBoneWarlockHurtAnimation(onDone);
        else if (isObsidianCrusher())    playObsidianCrusherHurtAnimation(onDone);
        else if (isSoulflayerGargoyle()) playSoulflayerGargoyleHurtAnimation(onDone);
        else if (isZyrryl()) playZyrrylHurtAnimation(onDone);
        else if (isKhaiNecro()) playKhaiNecroHurtAnimation(onDone);
        else                           playWolfHurtAnimation(onDone);
    }

    private void playEnemyAttack(Runnable onDone) {
        if (isStag()) {
            String lastSkill = (engine != null) ? engine.getLastEnemySkillName() : null;
            if ("Blackened Howl".equals(lastSkill)) playStagBlackenedHowlAnimation(onDone);
            else                                     playStagDeathlyChargeAnimation(onDone);
        } else if (isTreant())           playTreantAttackAnimation(onDone);
        else if (isBat())                playBatAttackAnimation(onDone);
        else if (isPlagueVermin())       playPlagueVerminAttackAnimation(onDone);
        else if (isForsakenCultist())    playForsakenCultistAttackAnimation(onDone);
        else if (isBlightHound())        playBlightHoundAttackAnimation(onDone);
        else if (isShadeSprite())        playSpriteTricksterAnimation(onDone);
        else if (isGhoulFootman())       playGhoulFootmanAttackAnimation(onDone);
        else if (isFlameRevenant())    playFlameRevenantAttackAnimation(onDone);
        else if (isBoneWarlock())    playBoneWarlockAttackAnimation(onDone);
        else if (isObsidianCrusher())    playObsidianCrusherAttackAnimation(onDone);
        else if (isSoulflayerGargoyle()) playSoulflayerGargoyleAttackAnimation(onDone);
        else if (isBlackJailer()) {
            String lastSkill = (engine != null) ? engine.getLastEnemySkillName() : null;
            if ("Shackling Chains".equals(lastSkill)) playBlackJailerChainsAnimation(onDone);
            else                                       playBlackJailerLashAnimation(onDone);
        }else if (isLutherVon()) {
            String lastSkill = (engine != null) ? engine.getLastEnemySkillName() : null;
            if ("Crown of Despair".equals(lastSkill))   playLutherVonCrownAnimation(onDone);
            else if ("Dark Ascension".equals(lastSkill)) playLutherVonAscensionAnimation(onDone);
            else                                          playLutherVonWrathAnimation(onDone);
        } else if (isZyrryl()) {
            String lastSkill = (engine != null) ? engine.getLastEnemySkillName() : null;
            if ("Bone Shield".equals(lastSkill))    playZyrrylBoneShieldAnimation(onDone);
            else                                     playZyrrylGreatCleaverAnimation(onDone);
        }else if (isKhaiNecro()) {
            String lastSkill = (engine != null) ? engine.getLastEnemySkillName() : null;
            if ("Soul Drain".equals(lastSkill))      playKhaiNecroSoulDrainAnimation(onDone);
            else if ("Encapsulation".equals(lastSkill)) playKhaiNecroEncapsulationAnimation(onDone);
            else                                      playKhaiNecroDarkAscensionAnimation(onDone);
        }
        else                             playWolfSavageHowlAnimation(onDone);
    }

    private void playEnemyDefeat(Runnable onDone) {
        if (isStag())                  playStagDefeatAnimation(onDone);
        else if (isTreant())           playTreantDefeatAnimation(onDone);
        else if (isBat())              playBatDefeatAnimation(onDone);
        else if (isPlagueVermin())     playPlagueVerminDefeatAnimation(onDone);
        else if (isForsakenCultist())  playForsakenCultistDefeatAnimation(onDone);
        else if (isBlightHound())      playBlightHoundDefeatAnimation(onDone);
        else if (isGhoulFootman())     playGhoulFootmanDefeatAnimation(onDone);
        else if (isShadeSprite())      playSpriteDefeatAnimation(onDone);
        else if (isBlackJailer())      playBlackJailerDefeatAnimation(onDone);
        else if (isLutherVon())        playLutherVonDefeatAnimation(onDone);
        else if (isFlameRevenant())    playFlameRevenantDefeatAnimation(onDone);
        else if (isBoneWarlock())    playBoneWarlockDefeatAnimation(onDone);
        else if (isObsidianCrusher())    playObsidianCrusherDefeatAnimation(onDone);
        else if (isSoulflayerGargoyle()) playSoulflayerGargoyleDefeatAnimation(onDone);
        else if (isZyrryl()) playZyrrylDefeatAnimation(onDone);
        else if (isKhaiNecro()) playKhaiNecroDefeatAnimation(onDone);
        else                           playWolfDefeatAnimation(onDone);
    }

    private void playEnemyEntrance(Runnable onDone) {
        if (isStag())                  playStagEntranceAnimation(onDone);
        else if (isTreant())           playTreantEntranceAnimation(onDone);
        else if (isBat())              playBatEntranceAnimation(onDone);
        else if (isPlagueVermin())     playPlagueVerminEntranceAnimation(onDone);
        else if (isForsakenCultist())  playForsakenCultistEntranceAnimation(onDone);
        else if (isBlightHound())      playBlightHoundEntranceAnimation(onDone);
        else if (isGhoulFootman())     playGhoulFootmanEntranceAnimation(onDone);
        else if (isBlackJailer())      playBlackJailerEntranceAnimation(onDone);
        else if (isShadeSprite())      playSpriteEntranceAnimation(onDone);
        else if (isLutherVon())        playLutherVonEntranceAnimation(onDone);
        else if (isFlameRevenant())    playFlameRevenantEntranceAnimation(onDone);
        else if (isBoneWarlock())    playBoneWarlockEntranceAnimation(onDone);
        else if (isObsidianCrusher())    playObsidianCrusherEntranceAnimation(onDone);
        else if (isSoulflayerGargoyle()) playSoulflayerGargoyleEntranceAnimation(onDone);
        else if (isZyrryl()) playZyrrylEntranceAnimation(onDone);
        else if (isKhaiNecro()) playKhaiNecroEntranceAnimation(onDone);
        else                           playWolfEntranceAnimation(onDone);
    }


    // WOLF
    private void playWolfDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfDefeatFrames == null) {
            enemySpriteLabel.setVisible(false);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingWolfDefeat = true;
        enemySpriteFrame = 0;
        int w = (int) (wolfDefeatFrames[0].getWidth() * ENEMY_SCALE), h = (int) (wolfDefeatFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < WOLF_DEFEAT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                delay(400, () -> {
                    isPlayingWolfDefeat = false;
                    enemySpriteLabel.setVisible(false);
                    if (onDone != null) onDone.run();
                });
            }
        });
        t.start();
    }


    private void playWolfEntranceAnimation(Runnable onDone) {
        if (wolfEntranceFrames == null) {
            if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingWolfEntrance = true;
        enemySpriteFrame = 0;
        int w = (int) (wolfEntranceFrames[0].getWidth() * ENEMY_SCALE), h = (int) (wolfEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(WOLF_ENTRANCE_START_X, ENEMY_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(120, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % WOLF_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {WOLF_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 10;
            if (currentX[0] <= ENEMY_X) {
                enemySpriteLabel.setLocation(ENEMY_X, ENEMY_Y);
                ((javax.swing.Timer) e.getSource()).stop();
                frameTimer.stop();
                isPlayingWolfEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else {
                enemySpriteLabel.setLocation(currentX[0], ENEMY_Y);
            }
        });
        slideTimer.start();
    }

    private void playWolfHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfHurtFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingWolfHurt = true;
        enemySpriteFrame = 0;
        int w = (int) (wolfHurtFrames[0].getWidth() * ENEMY_SCALE), h = (int) (wolfHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < WOLF_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingWolfHurt = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playWolfSavageHowlAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfSavageHowlFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingWolfSavageHowl = true;
        enemySpriteFrame = 0;
        int w = (int) (wolfSavageHowlFrames[0].getWidth() * ENEMY_SCALE), h = (int) (wolfSavageHowlFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < WOLF_HOWL_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingWolfSavageHowl = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    // SPRITE
    private void playSpriteHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteHurtFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingSpriteHurt = true;
        enemySpriteFrame = 0;
        int w = (int) (spriteHurtFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (spriteHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < SPRITE_HURT_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingSpriteHurt = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playSpriteTricksterAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteTricksterFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingSpriteTrickster = true;
        enemySpriteFrame = 0;
        int w = (int) (spriteTricksterFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (spriteTricksterFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(SHADE_X - 20, SHADE_Y - 15, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < SPRITE_TRICKSTER_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingSpriteTrickster = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playSpriteDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteDefeatFrames == null) {
            enemySpriteLabel.setVisible(false);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingSpriteDefeat = true;
        enemySpriteFrame = 0;
        int w = (int) (spriteDefeatFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (spriteDefeatFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < SPRITE_DEFEAT_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                delay(400, () -> {
                    isPlayingSpriteDefeat = false;
                    enemySpriteLabel.setVisible(false);
                    if (onDone != null) onDone.run();
                });
            }
        });
        t.start();
    }

    private void playSpriteEntranceAnimation(Runnable onDone) {
        if (spriteEntranceFrames == null) {
            if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingSpriteEntrance = true;
        enemySpriteFrame = 0;
        int w = (int) (spriteEntranceFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (spriteEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_ENTRANCE_START_X, SHADE_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(SPRITE_ENTRANCE_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % SPRITE_ENTRANCE_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {STAG_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(40, e -> {
            currentX[0] -= 3;
            if (currentX[0] <= SHADE_X) {
                enemySpriteLabel.setLocation(SHADE_X, SHADE_Y);
                ((javax.swing.Timer) e.getSource()).stop();
                frameTimer.stop();
                isPlayingSpriteEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else {
                enemySpriteLabel.setLocation(currentX[0], SHADE_Y);
            }
        });
        slideTimer.start();
    }

    //DREADBARK TREANT
    private void playTreantEntranceAnimation(Runnable onDone) {
        if (treantEntranceFrames == null) {
            if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingTreantEntrance = true;
        enemySpriteFrame = 0;
        int w = (int) (treantEntranceFrames[0].getWidth() * TREANT_SCALE);
        int h = (int) (treantEntranceFrames[0].getHeight() * TREANT_SCALE);
        enemySpriteLabel.setBounds(TREANT_ENTRANCE_START_X, TREANT_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(TREANT_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % TREANT_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {TREANT_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(40, e -> {
            currentX[0] -= 3;
            if (currentX[0] <= TREANT_X) {
                enemySpriteLabel.setLocation(TREANT_X, TREANT_Y);
                ((javax.swing.Timer) e.getSource()).stop();
                frameTimer.stop();
                isPlayingTreantEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else {
                enemySpriteLabel.setLocation(currentX[0], TREANT_Y);
            }
        });
        slideTimer.start();
    }

    private void playTreantHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (treantHurtFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingTreantHurt = true;
        enemySpriteFrame = 0;
        int w = (int) (treantHurtFrames[0].getWidth() * TREANT_IDLE_SCALE);
        int h = (int) (treantHurtFrames[0].getHeight() * TREANT_IDLE_SCALE);
        enemySpriteLabel.setBounds(TREANT_X, TREANT_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < TREANT_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingTreantHurt = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playTreantAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (treantAttackFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingTreantAttack = true;
        enemySpriteFrame = 0;
        int w = (int) (treantAttackFrames[0].getWidth() * TREANT_ATTACK_SCALE);
        int h = (int) (treantAttackFrames[0].getHeight() * TREANT_ATTACK_SCALE);
        enemySpriteLabel.setBounds(TREANT_X, TREANT_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < TREANT_ATTACK_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingTreantAttack = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playTreantDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (treantHurtFrames == null) {
            enemySpriteLabel.setVisible(false);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingTreantDefeat = true;
        enemySpriteFrame = 0;
        int w = (int) (treantHurtFrames[0].getWidth() * TREANT_IDLE_SCALE);
        int h = (int) (treantHurtFrames[0].getHeight() * TREANT_IDLE_SCALE);
        enemySpriteLabel.setBounds(TREANT_X, TREANT_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < TREANT_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                delay(400, () -> {
                    isPlayingTreantDefeat = false;
                    enemySpriteLabel.setVisible(false);
                    if (onDone != null) onDone.run();
                });
            }
        });
        t.start();
    }

    private void playBatEntranceAnimation(Runnable onDone) {
        if (batEntranceFrames == null) {
            if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingBatEntrance = true;
        enemySpriteFrame = 0;
        int w = (int) (batEntranceFrames[0].getWidth() * BAT_SCALE);
        int h = (int) (batEntranceFrames[0].getHeight() * BAT_SCALE);
        enemySpriteLabel.setBounds(BAT_ENTRANCE_START_X, BAT_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(200, e -> {  // 200ms = faster
            enemySpriteFrame = (enemySpriteFrame + 1) % BAT_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {BAT_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12; // faster slide
            if (currentX[0] <= BAT_X) {
                enemySpriteLabel.setLocation(BAT_X, BAT_Y);
                ((javax.swing.Timer) e.getSource()).stop();
                frameTimer.stop();
                isPlayingBatEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else {
                enemySpriteLabel.setLocation(currentX[0], BAT_Y);
            }
        });
        slideTimer.start();
    }

    private void playBatHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (batHurtFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingBatHurt = true;
        enemySpriteFrame = 0;
        int w = (int) (batHurtFrames[0].getWidth() * BAT_SCALE);
        int h = (int) (batHurtFrames[0].getHeight() * BAT_SCALE);
        enemySpriteLabel.setBounds(BAT_X, BAT_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BAT_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingBatHurt = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playBatAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (batAttackFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingBatAttack = true;
        enemySpriteFrame = 0;
        int w = (int) (batAttackFrames[0].getWidth() * BAT_SCALE);
        int h = (int) (batAttackFrames[0].getHeight() * BAT_SCALE);
        enemySpriteLabel.setBounds(BAT_X, BAT_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BAT_ATTACK_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingBatAttack = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playBatDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (batHurtFrames == null) {
            enemySpriteLabel.setVisible(false);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingBatDefeat = true;
        enemySpriteFrame = 0;
        int w = (int) (batHurtFrames[0].getWidth() * BAT_SCALE);
        int h = (int) (batHurtFrames[0].getHeight() * BAT_SCALE);
        enemySpriteLabel.setBounds(BAT_X, BAT_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < BAT_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                delay(400, () -> {
                    isPlayingBatDefeat = false;
                    enemySpriteLabel.setVisible(false);
                    if (onDone != null) onDone.run();
                });
            }
        });
        t.start();
    }

    //HOLLOW STAG
    private void playStagEntranceAnimation(Runnable onDone) {
        if (stagEntranceFrames == null) {
            if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingStagEntrance = true;
        enemySpriteFrame = 0;
        int w = (int) (stagEntranceFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (stagEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_ENTRANCE_START_X, STAG_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(STAG_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % STAG_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {STAG_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(40, e -> {
            currentX[0] -= 3;
            if (currentX[0] <= STAG_X) {
                enemySpriteLabel.setLocation(STAG_X, STAG_Y);
                ((javax.swing.Timer) e.getSource()).stop();
                frameTimer.stop();
                isPlayingStagEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else {
                enemySpriteLabel.setLocation(currentX[0], STAG_Y);
            }
        });
        slideTimer.start();
    }

    private void playStagHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagHurtFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingStagHurt = true;
        enemySpriteFrame = 0;
        int w = (int) (stagHurtFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (stagHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < STAG_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingStagHurt = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playStagDeathlyChargeAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagDeathlyChargeFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingStagCharge = true;
        enemySpriteFrame = 0;
        int w = (int) (stagDeathlyChargeFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (stagDeathlyChargeFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < STAG_CHARGE_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingStagCharge = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playStagBlackenedHowlAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagBlackenedHowlFrames == null || !enemySpriteLabel.isVisible()) {
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingStagHowl = true;
        enemySpriteFrame = 0;
        int w = (int) (stagBlackenedHowlFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (stagBlackenedHowlFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < STAG_HOWL_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingStagHowl = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playStagDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagHurtFrames == null) {
            enemySpriteLabel.setVisible(false);
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingStagDefeat = true;
        enemySpriteFrame = 0;
        int w = (int) (stagHurtFrames[0].getWidth() * ENEMY_SCALE);
        int h = (int) (stagHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < STAG_HURT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++;
                enemySpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                delay(400, () -> {
                    isPlayingStagDefeat = false;
                    enemySpriteLabel.setVisible(false);
                    if (onDone != null) onDone.run();
                });
            }
        });
        t.start();
    }

    // PLAGUE VERMIN
    private void playPlagueVerminEntranceAnimation(Runnable onDone) {
        if (plagueVerminEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingPlagueVerminEntrance = true; enemySpriteFrame = 0;
        int w = (int)(plagueVerminEntranceFrames[0].getWidth()  * PLAGUE_VERMIN_SCALE);
        int h = (int)(plagueVerminEntranceFrames[0].getHeight() * PLAGUE_VERMIN_SCALE);
        enemySpriteLabel.setBounds(PLAGUE_VERMIN_ENTRANCE_START_X, PLAGUE_VERMIN_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(PLAGUE_VERMIN_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % PLAGUE_VERMIN_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {PLAGUE_VERMIN_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= PLAGUE_VERMIN_X) {
                enemySpriteLabel.setLocation(PLAGUE_VERMIN_X, PLAGUE_VERMIN_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingPlagueVerminEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], PLAGUE_VERMIN_Y); }
        });
        slideTimer.start();
    }

    private void playPlagueVerminHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (plagueVerminHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingPlagueVerminHurt = true; enemySpriteFrame = 0;
        int w = (int)(plagueVerminHurtFrames[0].getWidth()  * PLAGUE_VERMIN_SCALE);
        int h = (int)(plagueVerminHurtFrames[0].getHeight() * PLAGUE_VERMIN_SCALE);
        enemySpriteLabel.setBounds(PLAGUE_VERMIN_X, PLAGUE_VERMIN_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < PLAGUE_VERMIN_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingPlagueVerminHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playPlagueVerminAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (plagueVerminAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingPlagueVerminAttack = true; enemySpriteFrame = 0;
        int w = (int)(plagueVerminAttackFrames[0].getWidth()  * PLAGUE_VERMIN_SCALE);
        int h = (int)(plagueVerminAttackFrames[0].getHeight() * PLAGUE_VERMIN_SCALE);
        enemySpriteLabel.setBounds(PLAGUE_VERMIN_X, PLAGUE_VERMIN_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < PLAGUE_VERMIN_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingPlagueVerminAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playPlagueVerminDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (plagueVerminHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingPlagueVerminDefeat = true; enemySpriteFrame = 0;
        int w = (int)(plagueVerminHurtFrames[0].getWidth()  * PLAGUE_VERMIN_SCALE);
        int h = (int)(plagueVerminHurtFrames[0].getHeight() * PLAGUE_VERMIN_SCALE);
        enemySpriteLabel.setBounds(PLAGUE_VERMIN_X, PLAGUE_VERMIN_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < PLAGUE_VERMIN_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingPlagueVerminDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    // FORSAKEN CULTIST

    private void playForsakenCultistEntranceAnimation(Runnable onDone) {
        if (forsakenCultistEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingForsakenCultistEntrance = true; enemySpriteFrame = 0;
        int w = (int)(forsakenCultistEntranceFrames[0].getWidth()  * FORSAKEN_CULTIST_SCALE);
        int h = (int)(forsakenCultistEntranceFrames[0].getHeight() * FORSAKEN_CULTIST_SCALE);
        enemySpriteLabel.setBounds(FORSAKEN_CULTIST_ENTRANCE_START_X, ENEMY_Y - 120, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(FORSAKEN_CULTIST_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % FORSAKEN_CULTIST_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {FORSAKEN_CULTIST_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= ENEMY_X) {
                enemySpriteLabel.setLocation(ENEMY_X, ENEMY_Y - 120);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingForsakenCultistEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], ENEMY_Y - 120); }
        });
        slideTimer.start();
    }

    private void playForsakenCultistHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (forsakenCultistHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingForsakenCultistHurt = true; enemySpriteFrame = 0;
        int w = (int)(forsakenCultistHurtFrames[0].getWidth()  * FORSAKEN_CULTIST_SCALE);
        int h = (int)(forsakenCultistHurtFrames[0].getHeight() * FORSAKEN_CULTIST_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y - 70, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(70, e -> {
            if (frame[0] < FORSAKEN_CULTIST_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingForsakenCultistHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playForsakenCultistAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (forsakenCultistAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingForsakenCultistAttack = true; enemySpriteFrame = 0;
        int w = (int)(forsakenCultistAttackFrames[0].getWidth()  * FORSAKEN_CULTIST_SCALE);
        int h = (int)(forsakenCultistAttackFrames[0].getHeight() * FORSAKEN_CULTIST_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y - 90, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < FORSAKEN_CULTIST_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingForsakenCultistAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playForsakenCultistDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (forsakenCultistHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingForsakenCultistDefeat = true; enemySpriteFrame = 0;
        int w = (int)(forsakenCultistHurtFrames[0].getWidth()  * FORSAKEN_CULTIST_SCALE);
        int h = (int)(forsakenCultistHurtFrames[0].getHeight() * FORSAKEN_CULTIST_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y - 70, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < FORSAKEN_CULTIST_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingForsakenCultistDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playBlightHoundEntranceAnimation(Runnable onDone) {
        if (blightHoundEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingBlightHoundEntrance = true; enemySpriteFrame = 0;
        int w = (int)(blightHoundEntranceFrames[0].getWidth()  * BLIGHT_HOUND_SCALE);
        int h = (int)(blightHoundEntranceFrames[0].getHeight() * BLIGHT_HOUND_SCALE);
        enemySpriteLabel.setBounds(BLIGHT_HOUND_ENTRANCE_START_X, BLIGHT_HOUND_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(BLIGHT_HOUND_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % BLIGHT_HOUND_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {BLIGHT_HOUND_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= BLIGHT_HOUND_X) {
                enemySpriteLabel.setLocation(BLIGHT_HOUND_X, BLIGHT_HOUND_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingBlightHoundEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], BLIGHT_HOUND_Y); }
        });
        slideTimer.start();
    }

    private void playBlightHoundHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blightHoundHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBlightHoundHurt = true; enemySpriteFrame = 0;
        int w = (int)(blightHoundHurtFrames[0].getWidth()  * BLIGHT_HOUND_SCALE);
        int h = (int)(blightHoundHurtFrames[0].getHeight() * BLIGHT_HOUND_SCALE);
        enemySpriteLabel.setBounds(BLIGHT_HOUND_X, BLIGHT_HOUND_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BLIGHT_HOUND_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBlightHoundHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBlightHoundAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blightHoundAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBlightHoundAttack = true; enemySpriteFrame = 0;
        int w = (int)(blightHoundAttackFrames[0].getWidth()  * BLIGHT_HOUND_SCALE);
        int h = (int)(blightHoundAttackFrames[0].getHeight() * BLIGHT_HOUND_SCALE);
        enemySpriteLabel.setBounds(BLIGHT_HOUND_X, BLIGHT_HOUND_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BLIGHT_HOUND_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBlightHoundAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBlightHoundDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blightHoundHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingBlightHoundDefeat = true; enemySpriteFrame = 0;
        int w = (int)(blightHoundHurtFrames[0].getWidth()  * BLIGHT_HOUND_SCALE);
        int h = (int)(blightHoundHurtFrames[0].getHeight() * BLIGHT_HOUND_SCALE);
        enemySpriteLabel.setBounds(BLIGHT_HOUND_X, BLIGHT_HOUND_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < BLIGHT_HOUND_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingBlightHoundDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playGhoulFootmanEntranceAnimation(Runnable onDone) {
        if (ghoulFootmanEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingGhoulFootmanEntrance = true; enemySpriteFrame = 0;
        int w = (int)(ghoulFootmanEntranceFrames[0].getWidth()  * GHOUL_FOOTMAN_SCALE);
        int h = (int)(ghoulFootmanEntranceFrames[0].getHeight() * GHOUL_FOOTMAN_SCALE);
        enemySpriteLabel.setBounds(GHOUL_FOOTMAN_ENTRANCE_START_X, GHOUL_FOOTMAN_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(GHOUL_FOOTMAN_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % GHOUL_FOOTMAN_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {GHOUL_FOOTMAN_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= GHOUL_FOOTMAN_X) {
                enemySpriteLabel.setLocation(GHOUL_FOOTMAN_X, GHOUL_FOOTMAN_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingGhoulFootmanEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], GHOUL_FOOTMAN_Y); }
        });
        slideTimer.start();
    }

    private void playGhoulFootmanHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (ghoulFootmanHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingGhoulFootmanHurt = true; enemySpriteFrame = 0;
        int w = (int)(ghoulFootmanHurtFrames[0].getWidth()  * GHOUL_FOOTMAN_SCALE);
        int h = (int)(ghoulFootmanHurtFrames[0].getHeight() * GHOUL_FOOTMAN_SCALE);
        enemySpriteLabel.setBounds(GHOUL_FOOTMAN_X, GHOUL_FOOTMAN_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < GHOUL_FOOTMAN_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingGhoulFootmanHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playGhoulFootmanAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (ghoulFootmanAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingGhoulFootmanAttack = true; enemySpriteFrame = 0;
        int w = (int)(ghoulFootmanAttackFrames[0].getWidth()  * GHOUL_FOOTMAN_SCALE);
        int h = (int)(ghoulFootmanAttackFrames[0].getHeight() * GHOUL_FOOTMAN_SCALE);
        enemySpriteLabel.setBounds(GHOUL_FOOTMAN_X, GHOUL_FOOTMAN_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < GHOUL_FOOTMAN_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingGhoulFootmanAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playGhoulFootmanDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (ghoulFootmanHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingGhoulFootmanDefeat = true; enemySpriteFrame = 0;
        int w = (int)(ghoulFootmanHurtFrames[0].getWidth()  * GHOUL_FOOTMAN_SCALE);
        int h = (int)(ghoulFootmanHurtFrames[0].getHeight() * GHOUL_FOOTMAN_SCALE);
        enemySpriteLabel.setBounds(GHOUL_FOOTMAN_X, GHOUL_FOOTMAN_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < GHOUL_FOOTMAN_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingGhoulFootmanDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playBlackJailerEntranceAnimation(Runnable onDone) {
        if (blackJailerEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingBlackJailerEntrance = true; enemySpriteFrame = 0;
        int w = (int)(blackJailerEntranceFrames[0].getWidth()  * BLACK_JAILER_SCALE);
        int h = (int)(blackJailerEntranceFrames[0].getHeight() * BLACK_JAILER_SCALE);
        enemySpriteLabel.setBounds(BLACK_JAILER_ENTRANCE_START_X, BLACK_JAILER_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(BLACK_JAILER_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % BLACK_JAILER_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {BLACK_JAILER_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= BLACK_JAILER_X) {
                enemySpriteLabel.setLocation(BLACK_JAILER_X, BLACK_JAILER_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingBlackJailerEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], BLACK_JAILER_Y); }
        });
        slideTimer.start();
    }

    private void playBlackJailerHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blackJailerHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBlackJailerHurt = true; enemySpriteFrame = 0;
        int w = (int)(blackJailerHurtFrames[0].getWidth()  * BLACK_JAILER_SCALE);
        int h = (int)(blackJailerHurtFrames[0].getHeight() * BLACK_JAILER_SCALE);
        enemySpriteLabel.setBounds(BLACK_JAILER_X, BLACK_JAILER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BLACK_JAILER_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBlackJailerHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBlackJailerChainsAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blackJailerChainsFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBlackJailerChains = true; enemySpriteFrame = 0;
        int w = (int)(blackJailerChainsFrames[0].getWidth()  * BLACK_JAILER_SCALE);
        int h = (int)(blackJailerChainsFrames[0].getHeight() * BLACK_JAILER_SCALE);
        enemySpriteLabel.setBounds(BLACK_JAILER_X, BLACK_JAILER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BLACK_JAILER_CHAINS_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBlackJailerChains = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBlackJailerLashAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blackJailerLashFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBlackJailerLash = true; enemySpriteFrame = 0;
        int w = (int)(blackJailerLashFrames[0].getWidth()  * BLACK_JAILER_SCALE);
        int h = (int)(blackJailerLashFrames[0].getHeight() * BLACK_JAILER_SCALE);
        enemySpriteLabel.setBounds(BLACK_JAILER_X, BLACK_JAILER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BLACK_JAILER_LASH_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBlackJailerLash = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBlackJailerDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (blackJailerHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingBlackJailerDefeat = true; enemySpriteFrame = 0;
        int w = (int)(blackJailerHurtFrames[0].getWidth()  * BLACK_JAILER_SCALE);
        int h = (int)(blackJailerHurtFrames[0].getHeight() * BLACK_JAILER_SCALE);
        enemySpriteLabel.setBounds(BLACK_JAILER_X, BLACK_JAILER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < BLACK_JAILER_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingBlackJailerDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playLutherVonEntranceAnimation(Runnable onDone) {
        if (lutherVonEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingLutherVonEntrance = true; enemySpriteFrame = 0;
        int w = (int)(lutherVonEntranceFrames[0].getWidth()  * LUTHER_VON_SCALE);
        int h = (int)(lutherVonEntranceFrames[0].getHeight() * LUTHER_VON_SCALE);
        enemySpriteLabel.setBounds(LUTHER_VON_ENTRANCE_START_X, LUTHER_VON_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(LUTHER_VON_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % LUTHER_VON_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {LUTHER_VON_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= LUTHER_VON_X) {
                enemySpriteLabel.setLocation(LUTHER_VON_X, LUTHER_VON_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingLutherVonEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], LUTHER_VON_Y); }
        });
        slideTimer.start();
    }

    private void playLutherVonHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (lutherVonHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingLutherVonHurt = true; enemySpriteFrame = 0;
        int w = (int)(lutherVonHurtFrames[0].getWidth()  * LUTHER_VON_SCALE);
        int h = (int)(lutherVonHurtFrames[0].getHeight() * LUTHER_VON_SCALE);
        enemySpriteLabel.setBounds(LUTHER_VON_X, LUTHER_VON_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < LUTHER_VON_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingLutherVonHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playLutherVonCrownAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (lutherVonCrownFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingLutherVonCrown = true; enemySpriteFrame = 0;
        int w = (int)(lutherVonCrownFrames[0].getWidth()  * LUTHER_VON_SCALE);
        int h = (int)(lutherVonCrownFrames[0].getHeight() * LUTHER_VON_SCALE);
        enemySpriteLabel.setBounds(LUTHER_VON_X, LUTHER_VON_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < LUTHER_VON_CROWN_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingLutherVonCrown = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playLutherVonAscensionAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (lutherVonAscensionFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingLutherVonAscension = true; enemySpriteFrame = 0;
        int w = (int)(lutherVonAscensionFrames[0].getWidth()  * LUTHER_VON_SCALE);
        int h = (int)(lutherVonAscensionFrames[0].getHeight() * LUTHER_VON_SCALE);
        enemySpriteLabel.setBounds(LUTHER_VON_X, LUTHER_VON_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < LUTHER_VON_ASCENSION_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingLutherVonAscension = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playLutherVonWrathAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (lutherVonWrathFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingLutherVonWrath = true; enemySpriteFrame = 0;
        int w = (int)(lutherVonWrathFrames[0].getWidth()  * LUTHER_VON_SCALE);
        int h = (int)(lutherVonWrathFrames[0].getHeight() * LUTHER_VON_SCALE);
        enemySpriteLabel.setBounds(LUTHER_VON_X, LUTHER_VON_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < LUTHER_VON_WRATH_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingLutherVonWrath = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playLutherVonDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (lutherVonDefeatedFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingLutherVonDefeat = true; enemySpriteFrame = 0;
        int w = (int)(lutherVonDefeatedFrames[0].getWidth()  * LUTHER_VON_SCALE);
        int h = (int)(lutherVonDefeatedFrames[0].getHeight() * LUTHER_VON_SCALE);
        enemySpriteLabel.setBounds(LUTHER_VON_X, LUTHER_VON_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < LUTHER_VON_DEFEATED_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingLutherVonDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playFlameRevenantEntranceAnimation(Runnable onDone) {
        if (flameRevenantEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingFlameRevenantEntrance = true; enemySpriteFrame = 0;
        int w = (int)(flameRevenantEntranceFrames[0].getWidth()  * FLAME_REVENANT_SCALE);
        int h = (int)(flameRevenantEntranceFrames[0].getHeight() * FLAME_REVENANT_SCALE);
        enemySpriteLabel.setBounds(FLAME_REVENANT_ENTRANCE_START_X, FLAME_REVENANT_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(FLAME_REVENANT_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % FLAME_REVENANT_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {FLAME_REVENANT_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= FLAME_REVENANT_X) {
                enemySpriteLabel.setLocation(FLAME_REVENANT_X, FLAME_REVENANT_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingFlameRevenantEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], FLAME_REVENANT_Y); }
        });
        slideTimer.start();
    }

    private void playFlameRevenantHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (flameRevenantHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingFlameRevenantHurt = true; enemySpriteFrame = 0;
        int w = (int)(flameRevenantHurtFrames[0].getWidth()  * FLAME_REVENANT_SCALE);
        int h = (int)(flameRevenantHurtFrames[0].getHeight() * FLAME_REVENANT_SCALE);
        enemySpriteLabel.setBounds(FLAME_REVENANT_X, FLAME_REVENANT_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < FLAME_REVENANT_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingFlameRevenantHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playFlameRevenantAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (flameRevenantAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingFlameRevenantAttack = true; enemySpriteFrame = 0;
        int w = (int)(flameRevenantAttackFrames[0].getWidth()  * FLAME_REVENANT_SCALE);
        int h = (int)(flameRevenantAttackFrames[0].getHeight() * FLAME_REVENANT_SCALE);
        enemySpriteLabel.setBounds(FLAME_REVENANT_X, FLAME_REVENANT_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < FLAME_REVENANT_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingFlameRevenantAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playFlameRevenantDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (flameRevenantHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingFlameRevenantDefeat = true; enemySpriteFrame = 0;
        int w = (int)(flameRevenantHurtFrames[0].getWidth()  * FLAME_REVENANT_SCALE);
        int h = (int)(flameRevenantHurtFrames[0].getHeight() * FLAME_REVENANT_SCALE);
        enemySpriteLabel.setBounds(FLAME_REVENANT_X, FLAME_REVENANT_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < FLAME_REVENANT_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingFlameRevenantDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playBoneWarlockEntranceAnimation(Runnable onDone) {
        if (boneWarlockEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingBoneWarlockEntrance = true; enemySpriteFrame = 0;
        int w = (int)(boneWarlockEntranceFrames[0].getWidth()  * BONE_WARLOCK_SCALE);
        int h = (int)(boneWarlockEntranceFrames[0].getHeight() * BONE_WARLOCK_SCALE);
        enemySpriteLabel.setBounds(BONE_WARLOCK_ENTRANCE_START_X, BONE_WARLOCK_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(BONE_WARLOCK_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % BONE_WARLOCK_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {BONE_WARLOCK_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= BONE_WARLOCK_X) {
                enemySpriteLabel.setLocation(BONE_WARLOCK_X, BONE_WARLOCK_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingBoneWarlockEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], BONE_WARLOCK_Y); }
        });
        slideTimer.start();
    }

    private void playBoneWarlockHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (boneWarlockHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBoneWarlockHurt = true; enemySpriteFrame = 0;
        int w = (int)(boneWarlockHurtFrames[0].getWidth()  * BONE_WARLOCK_SCALE);
        int h = (int)(boneWarlockHurtFrames[0].getHeight() * BONE_WARLOCK_SCALE);
        enemySpriteLabel.setBounds(BONE_WARLOCK_X, BONE_WARLOCK_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BONE_WARLOCK_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBoneWarlockHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBoneWarlockAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (boneWarlockAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingBoneWarlockAttack = true; enemySpriteFrame = 0;
        int w = (int)(boneWarlockAttackFrames[0].getWidth()  * BONE_WARLOCK_SCALE);
        int h = (int)(boneWarlockAttackFrames[0].getHeight() * BONE_WARLOCK_SCALE);
        enemySpriteLabel.setBounds(BONE_WARLOCK_X, BONE_WARLOCK_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < BONE_WARLOCK_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBoneWarlockAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playBoneWarlockDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (boneWarlockHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingBoneWarlockDefeat = true; enemySpriteFrame = 0;
        int w = (int)(boneWarlockHurtFrames[0].getWidth()  * BONE_WARLOCK_SCALE);
        int h = (int)(boneWarlockHurtFrames[0].getHeight() * BONE_WARLOCK_SCALE);
        enemySpriteLabel.setBounds(BONE_WARLOCK_X, BONE_WARLOCK_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < BONE_WARLOCK_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingBoneWarlockDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playObsidianCrusherEntranceAnimation(Runnable onDone) {
        if (obsidianCrusherEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingObsidianCrusherEntrance = true; enemySpriteFrame = 0;
        int w = (int)(obsidianCrusherEntranceFrames[0].getWidth()  * OBSIDIAN_CRUSHER_SCALE);
        int h = (int)(obsidianCrusherEntranceFrames[0].getHeight() * OBSIDIAN_CRUSHER_SCALE);
        enemySpriteLabel.setBounds(OBSIDIAN_CRUSHER_ENTRANCE_START_X, OBSIDIAN_CRUSHER_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(OBSIDIAN_CRUSHER_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % OBSIDIAN_CRUSHER_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {OBSIDIAN_CRUSHER_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= OBSIDIAN_CRUSHER_X) {
                enemySpriteLabel.setLocation(OBSIDIAN_CRUSHER_X, OBSIDIAN_CRUSHER_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingObsidianCrusherEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], OBSIDIAN_CRUSHER_Y); }
        });
        slideTimer.start();
    }

    private void playObsidianCrusherHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (obsidianCrusherHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingObsidianCrusherHurt = true; enemySpriteFrame = 0;
        int w = (int)(obsidianCrusherHurtFrames[0].getWidth()  * OBSIDIAN_CRUSHER_SCALE);
        int h = (int)(obsidianCrusherHurtFrames[0].getHeight() * OBSIDIAN_CRUSHER_SCALE);
        enemySpriteLabel.setBounds(OBSIDIAN_CRUSHER_X, OBSIDIAN_CRUSHER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < OBSIDIAN_CRUSHER_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingObsidianCrusherHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playObsidianCrusherAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (obsidianCrusherAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingObsidianCrusherAttack = true; enemySpriteFrame = 0;
        int w = (int)(obsidianCrusherAttackFrames[0].getWidth()  * OBSIDIAN_CRUSHER_SCALE);
        int h = (int)(obsidianCrusherAttackFrames[0].getHeight() * OBSIDIAN_CRUSHER_SCALE);
        enemySpriteLabel.setBounds(OBSIDIAN_CRUSHER_X, OBSIDIAN_CRUSHER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < OBSIDIAN_CRUSHER_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingObsidianCrusherAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playObsidianCrusherDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (obsidianCrusherHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingObsidianCrusherDefeat = true; enemySpriteFrame = 0;
        int w = (int)(obsidianCrusherHurtFrames[0].getWidth()  * OBSIDIAN_CRUSHER_SCALE);
        int h = (int)(obsidianCrusherHurtFrames[0].getHeight() * OBSIDIAN_CRUSHER_SCALE);
        enemySpriteLabel.setBounds(OBSIDIAN_CRUSHER_X, OBSIDIAN_CRUSHER_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < OBSIDIAN_CRUSHER_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingObsidianCrusherDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playSoulflayerGargoyleEntranceAnimation(Runnable onDone) {
        if (soulflayerGargoyleEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingSoulflayerGargoyleEntrance = true; enemySpriteFrame = 0;
        int w = (int)(soulflayerGargoyleEntranceFrames[0].getWidth()  * SOULFLAYER_GARGOYLE_SCALE);
        int h = (int)(soulflayerGargoyleEntranceFrames[0].getHeight() * SOULFLAYER_GARGOYLE_SCALE);
        enemySpriteLabel.setBounds(SOULFLAYER_GARGOYLE_ENTRANCE_START_X, SOULFLAYER_GARGOYLE_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(SOULFLAYER_GARGOYLE_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % SOULFLAYER_GARGOYLE_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {SOULFLAYER_GARGOYLE_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= SOULFLAYER_GARGOYLE_X) {
                enemySpriteLabel.setLocation(SOULFLAYER_GARGOYLE_X, SOULFLAYER_GARGOYLE_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingSoulflayerGargoyleEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], SOULFLAYER_GARGOYLE_Y); }
        });
        slideTimer.start();
    }

    private void playSoulflayerGargoyleHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (soulflayerGargoyleHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingSoulflayerGargoyleHurt = true; enemySpriteFrame = 0;
        int w = (int)(soulflayerGargoyleHurtFrames[0].getWidth()  * SOULFLAYER_GARGOYLE_SCALE);
        int h = (int)(soulflayerGargoyleHurtFrames[0].getHeight() * SOULFLAYER_GARGOYLE_SCALE);
        enemySpriteLabel.setBounds(SOULFLAYER_GARGOYLE_X, SOULFLAYER_GARGOYLE_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < SOULFLAYER_GARGOYLE_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingSoulflayerGargoyleHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playSoulflayerGargoyleAttackAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (soulflayerGargoyleAttackFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingSoulflayerGargoyleAttack = true; enemySpriteFrame = 0;
        int w = (int)(soulflayerGargoyleAttackFrames[0].getWidth()  * SOULFLAYER_GARGOYLE_SCALE);
        int h = (int)(soulflayerGargoyleAttackFrames[0].getHeight() * SOULFLAYER_GARGOYLE_SCALE);
        enemySpriteLabel.setBounds(SOULFLAYER_GARGOYLE_X, SOULFLAYER_GARGOYLE_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < SOULFLAYER_GARGOYLE_ATTACK_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingSoulflayerGargoyleAttack = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playSoulflayerGargoyleDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (soulflayerGargoyleHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingSoulflayerGargoyleDefeat = true; enemySpriteFrame = 0;
        int w = (int)(soulflayerGargoyleHurtFrames[0].getWidth()  * SOULFLAYER_GARGOYLE_SCALE);
        int h = (int)(soulflayerGargoyleHurtFrames[0].getHeight() * SOULFLAYER_GARGOYLE_SCALE);
        enemySpriteLabel.setBounds(SOULFLAYER_GARGOYLE_X, SOULFLAYER_GARGOYLE_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < SOULFLAYER_GARGOYLE_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingSoulflayerGargoyleDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playZyrrylEntranceAnimation(Runnable onDone) {
        if (zyrrylEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingZyrrylEntrance = true; enemySpriteFrame = 0;
        int w = (int)(zyrrylEntranceFrames[0].getWidth()  * ZYRRYL_SCALE);
        int h = (int)(zyrrylEntranceFrames[0].getHeight() * ZYRRYL_SCALE);
        enemySpriteLabel.setBounds(ZYRRYL_ENTRANCE_START_X, ZYRRYL_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(ZYRRYL_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % ZYRRYL_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {ZYRRYL_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= ZYRRYL_X) {
                enemySpriteLabel.setLocation(ZYRRYL_X, ZYRRYL_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingZyrrylEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], ZYRRYL_Y); }
        });
        slideTimer.start();
    }

    private void playZyrrylHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (zyrrylHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingZyrrylHurt = true; enemySpriteFrame = 0;
        int w = (int)(zyrrylHurtFrames[0].getWidth()  * ZYRRYL_SCALE);
        int h = (int)(zyrrylHurtFrames[0].getHeight() * ZYRRYL_SCALE);
        enemySpriteLabel.setBounds(ZYRRYL_X, ZYRRYL_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < ZYRRYL_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingZyrrylHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playZyrrylBoneShieldAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (zyrrylBoneShieldFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingZyrrylBoneShield = true; enemySpriteFrame = 0;
        int w = (int)(zyrrylBoneShieldFrames[0].getWidth()  * ZYRRYL_SCALE);
        int h = (int)(zyrrylBoneShieldFrames[0].getHeight() * ZYRRYL_SCALE);
        enemySpriteLabel.setBounds(ZYRRYL_X, ZYRRYL_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < ZYRRYL_BONE_SHIELD_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingZyrrylBoneShield = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playZyrrylGreatCleaverAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (zyrrylGreatCleaverFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingZyrrylGreatCleaver = true; enemySpriteFrame = 0;
        int w = (int)(zyrrylGreatCleaverFrames[0].getWidth()  * ZYRRYL_SCALE);
        int h = (int)(zyrrylGreatCleaverFrames[0].getHeight() * ZYRRYL_SCALE);
        enemySpriteLabel.setBounds(ZYRRYL_X, ZYRRYL_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < ZYRRYL_GREAT_CLEAVER_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingZyrrylGreatCleaver = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playZyrrylDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (zyrrylHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingZyrrylDefeat = true; enemySpriteFrame = 0;
        int w = (int)(zyrrylHurtFrames[0].getWidth()  * ZYRRYL_SCALE);
        int h = (int)(zyrrylHurtFrames[0].getHeight() * ZYRRYL_SCALE);
        enemySpriteLabel.setBounds(ZYRRYL_X, ZYRRYL_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < ZYRRYL_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingZyrrylDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playKhaiNecroEntranceAnimation(Runnable onDone) {
        if (khaiNecroEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingKhaiNecroEntrance = true; enemySpriteFrame = 0;
        int w = (int)(khaiNecroEntranceFrames[0].getWidth()  * KHAI_NECRO_SCALE);
        int h = (int)(khaiNecroEntranceFrames[0].getHeight() * KHAI_NECRO_SCALE);
        enemySpriteLabel.setBounds(KHAI_NECRO_ENTRANCE_START_X, KHAI_NECRO_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(KHAI_NECRO_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % KHAI_NECRO_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {KHAI_NECRO_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 12;
            if (currentX[0] <= KHAI_NECRO_X) {
                enemySpriteLabel.setLocation(KHAI_NECRO_X, KHAI_NECRO_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingKhaiNecroEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], KHAI_NECRO_Y); }
        });
        slideTimer.start();
    }

    private void playKhaiNecroHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (khaiNecroHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingKhaiNecroHurt = true; enemySpriteFrame = 0;
        int w = (int)(khaiNecroHurtFrames[0].getWidth()  * KHAI_NECRO_SCALE);
        int h = (int)(khaiNecroHurtFrames[0].getHeight() * KHAI_NECRO_SCALE);
        enemySpriteLabel.setBounds(KHAI_NECRO_X, KHAI_NECRO_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < KHAI_NECRO_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKhaiNecroHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playKhaiNecroSoulDrainAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (khaiNecroSoulDrainFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingKhaiNecroSoulDrain = true; enemySpriteFrame = 0;
        int w = (int)(khaiNecroSoulDrainFrames[0].getWidth()  * KHAI_NECRO_SCALE);
        int h = (int)(khaiNecroSoulDrainFrames[0].getHeight() * KHAI_NECRO_SCALE);
        enemySpriteLabel.setBounds(KHAI_NECRO_X, KHAI_NECRO_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < KHAI_NECRO_SOUL_DRAIN_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKhaiNecroSoulDrain = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playKhaiNecroEncapsulationAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (khaiNecroEncapsulationFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingKhaiNecroEncapsulation = true; enemySpriteFrame = 0;
        int w = (int)(khaiNecroEncapsulationFrames[0].getWidth()  * KHAI_NECRO_SCALE);
        int h = (int)(khaiNecroEncapsulationFrames[0].getHeight() * KHAI_NECRO_SCALE);
        enemySpriteLabel.setBounds(KHAI_NECRO_X, KHAI_NECRO_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < KHAI_NECRO_ENCAPSULATION_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKhaiNecroEncapsulation = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playKhaiNecroDarkAscensionAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (khaiNecroDarkAscensionFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingKhaiNecroDarkAscension = true; enemySpriteFrame = 0;
        int w = (int)(khaiNecroDarkAscensionFrames[0].getWidth()  * KHAI_NECRO_SCALE);
        int h = (int)(khaiNecroDarkAscensionFrames[0].getHeight() * KHAI_NECRO_SCALE);
        enemySpriteLabel.setBounds(KHAI_NECRO_X, KHAI_NECRO_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < KHAI_NECRO_DARK_ASCENSION_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKhaiNecroDarkAscension = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playKhaiNecroDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (khaiNecroHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingKhaiNecroDefeat = true; enemySpriteFrame = 0;
        int w = (int)(khaiNecroHurtFrames[0].getWidth()  * KHAI_NECRO_SCALE);
        int h = (int)(khaiNecroHurtFrames[0].getHeight() * KHAI_NECRO_SCALE);
        enemySpriteLabel.setBounds(KHAI_NECRO_X, KHAI_NECRO_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < KHAI_NECRO_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingKhaiNecroDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }


    // HERO ACTIONS
    private void playHeroHurtAnimation(Runnable onDone) {
        if (isKarlHero()) playKarlHurtAnimation(onDone);
        else              playKaelHurtAnimation(onDone);
    }

    private void playKaelHurtAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (kaelHurtFrames == null) {
            startHeroIdleAnimation();
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingKaelHurt = true;
        heroSpriteFrame = 0;
        int w = (int) (kaelHurtFrames[0].getWidth() * SPRITE_SCALE);
        int h = (int) (kaelHurtFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getHeroIdleX(), getHeroIdleY(), w, h);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(150, e -> {
            if (frame[0] < KAEL_HURT_FRAME_COUNT) {
                heroSpriteFrame = frame[0]++;
                heroSpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingKaelHurt = false;
                startHeroIdleAnimation();
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private int getActionX(BufferedImage[] frames) {
        boolean karl = isKarlHero();
        double scale = karl ? KARL_SPRITE_SCALE : SPRITE_SCALE;
        int labelW = (int)(frames[0].getWidth() * scale);
        BufferedImage[] idleRef = karl ? karlIdleFrames : idleFrames;
        double idleScale = karl ? KARL_SPRITE_SCALE : SPRITE_SCALE;
        int idleW = idleRef != null ? (int)(idleRef[0].getWidth() * idleScale) : SPRITE_W;
        int baseX = (currentBattleBgPath.contains("World3BG15.5") || currentBattleBgPath.contains("World3BG21.5"))
                ? ACTION_X_BASE + 100 : ACTION_X_BASE;
        return baseX - (labelW - idleW) / 2;

    }

    private void playBladeRushAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (bladeRushFrames == null) {
            startHeroIdleAnimation();
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingBladeRush = true;
        heroSpriteFrame = 0;
        int lW = (int) (bladeRushFrames[0].getWidth() * SPRITE_SCALE);
        int lH = (int) (bladeRushFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(bladeRushFrames), getHeroActionY(), lW, lH);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(130, e -> {
            if (frame[0] < BLADE_RUSH_FRAME_COUNT) {
                heroSpriteFrame = frame[0]++;
                heroSpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingBladeRush = false;
                startHeroIdleAnimation();
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playPiercingSlashAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (piercingSlashFrames == null) {
            startHeroIdleAnimation();
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingPiercingSlash = true;
        heroSpriteFrame = 0;
        int lW = (int) (piercingSlashFrames[0].getWidth() * SPRITE_SCALE);
        int lH = (int) (piercingSlashFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(piercingSlashFrames), getHeroActionY(), lW, lH);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(110, e -> {
            if (frame[0] < PIERCING_SLASH_FRAME_COUNT) {
                heroSpriteFrame = frame[0]++;
                heroSpriteLabel.repaint();
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                isPlayingPiercingSlash = false;
                startHeroIdleAnimation();
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void playEternalCrossSlashAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (eternalCrossFrames == null) {
            startHeroIdleAnimation();
            if (onDone != null) onDone.run();
            return;
        }
        isPlayingEternalCross = true;
        heroSpriteFrame = 0;
        int lW = (int) (eternalCrossFrames[0].getWidth() * SPRITE_SCALE);
        int lH = (int) (eternalCrossFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(eternalCrossFrames), getHeroActionY(), lW, lH);
        heroSpriteLabel.repaint();
        int[] frame = {0}, repeat = {0};
        javax.swing.Timer t = new javax.swing.Timer(60, e -> {
            if (frame[0] < ETERNAL_CROSS_FRAME_COUNT) {
                heroSpriteFrame = frame[0]++;
                heroSpriteLabel.repaint();
            } else {
                repeat[0]++;
                if (repeat[0] < 2) {
                    frame[0] = 0;
                    heroSpriteFrame = 0;
                    heroSpriteLabel.repaint();
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    isPlayingEternalCross = false;
                    startHeroIdleAnimation();
                    if (onDone != null) onDone.run();
                }
            }
        });
        t.start();
    }

    private void playKarlHurtAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (karlHurtFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingKarlHurt = true; heroSpriteFrame = 0;
        int w = (int)(karlHurtFrames[0].getWidth() * SPRITE_SCALE);
        int h = (int)(karlHurtFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getHeroIdleX(), getHeroIdleY(), w, h);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        new javax.swing.Timer(150, e -> {
            if (frame[0] < KARL_HURT_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKarlHurt = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        }).start();
    }

    private void playKarlPiercingShotAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (karlPiercingShotFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingKarlPiercingShot = true; heroSpriteFrame = 0;
        int w = (int)(karlPiercingShotFrames[0].getWidth() * SPRITE_SCALE);
        int h = (int)(karlPiercingShotFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(karlPiercingShotFrames), getHeroActionY(), w, h);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        new javax.swing.Timer(130, e -> {
            if (frame[0] < KARL_PIERCING_SHOT_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKarlPiercingShot = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        }).start();
    }

    private void playKarlBullseyeAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (karlBullseyeFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingKarlBullseye = true; heroSpriteFrame = 0;
        int w = (int)(karlBullseyeFrames[0].getWidth() * SPRITE_SCALE);
        int h = (int)(karlBullseyeFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(karlBullseyeFrames), getHeroActionY(), w, h);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        new javax.swing.Timer(110, e -> {
            if (frame[0] < KARL_BULLSEYE_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKarlBullseye = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        }).start();
    }

    private void playKarlRainAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (karlRainFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingKarlRain = true; heroSpriteFrame = 0;
        int w = (int)(karlRainFrames[0].getWidth() * KARL_SPRITE_SCALE);
        int h = (int)(karlRainFrames[0].getHeight() * KARL_SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(karlRainFrames), getHeroActionY(), w, h);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        new javax.swing.Timer(130, e -> {
            if (frame[0] < KARL_RAIN_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKarlRain = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        }).start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ BATTLE LOGIC
    // ════════════════════════════════════════════════════════════════════════
    private void onPlayerAction(BattleManager.BattleAction action) {
        if (animating || engine.getCurrentTurn() != BattleManager.TurnOwner.PLAYER) return;
        animating = true;
        setActionsEnabled(false);
        clearLog();
        setTurnLabel(true);

        // Math is calculated in the background
        BattleManager.ActionResult pResult = engine.playerAction(action);
        engine.advanceToEnemyTurn();

        Runnable afterHeroAnim = () -> {
            // ★ UI updates precisely when the hit connects!
            refreshBattleUI();

            if (engine.checkOutcome() == BattleManager.BattleOutcome.VICTORY) {
                playEnemyHurt(() -> {
                    clearLog();
                    if (pResult != null) addLogFromResult(pResult, true);
                    playEnemyDefeat(() -> {
                        handleVictory();
                        animating = false;
                    });
                });
            } else {
                playEnemyHurt(() -> {
                    clearLog();
                    if (pResult != null) addLogFromResult(pResult, true);

                    Timer t1 = new Timer(1500, e -> {
                        clearLog();
                        executeEnemyTurnSequence();
                    });
                    t1.setRepeats(false);
                    t1.start();
                });
            }
        };

        if (isKarlHero()) {
            if (action == BattleManager.BattleAction.SKILL1)        playKarlPiercingShotAnimation(afterHeroAnim);
            else if (action == BattleManager.BattleAction.SKILL2)   playKarlBullseyeAnimation(afterHeroAnim);
            else if (action == BattleManager.BattleAction.ULTIMATE) playKarlRainAnimation(afterHeroAnim);
            else {
                if (pResult != null) addLogFromResult(pResult, true);
                if (engine.checkOutcome() == BattleManager.BattleOutcome.VICTORY) {
                    handleVictory();
                    animating = false;
                    return;
                }
                Timer t1 = new Timer(900, e -> {
                    engine.advanceToEnemyTurn();
                    setTurnLabel(false);
                    BattleManager.ActionResult er = engine.enemyTurn();
                    refreshBattleUI();
                    playEnemyAttack(() -> {
                        playHeroHurtAnimation(() -> {
                            clearLog();
                            if (er != null) addEnemyAttackLog(er);
                            if (engine.checkOutcome() == BattleManager.BattleOutcome.DEFEAT) handleDefeat();
                            else {
                                Timer t3 = new Timer(800, ev2 -> {
                                    clearLog();
                                    setTurnLabel(true);
                                    setActionsEnabled(true);
                                    animating = false;
                                });
                                t3.setRepeats(false);
                                t3.start();
                            }
                        });
                    });
                });
                t1.setRepeats(false);
                t1.start();
            }
        } else {
            if (action == BattleManager.BattleAction.SKILL1)        playBladeRushAnimation(afterHeroAnim);
            else if (action == BattleManager.BattleAction.SKILL2)   playPiercingSlashAnimation(afterHeroAnim);
            else if (action == BattleManager.BattleAction.ULTIMATE) playEternalCrossSlashAnimation(afterHeroAnim);
            else {
                if (pResult != null) addLogFromResult(pResult, true);
                if (engine.checkOutcome() == BattleManager.BattleOutcome.VICTORY) {
                    handleVictory();
                    animating = false;
                    return;
                }
                Timer t1 = new Timer(900, e -> {
                    engine.advanceToEnemyTurn();
                    setTurnLabel(false);
                    BattleManager.ActionResult er = engine.enemyTurn();
                    refreshBattleUI();
                    playEnemyAttack(() -> {
                        playHeroHurtAnimation(() -> {
                            clearLog();
                            if (er != null) addEnemyAttackLog(er);
                            if (engine.checkOutcome() == BattleManager.BattleOutcome.DEFEAT) handleDefeat();
                            else {
                                Timer t3 = new Timer(800, ev2 -> {
                                    clearLog();
                                    setTurnLabel(true);
                                    setActionsEnabled(true);
                                    animating = false;
                                });
                                t3.setRepeats(false);
                                t3.start();
                            }
                        });
                    });
                });
                t1.setRepeats(false);
                t1.start();
            }
        }
    }
    private void addEnemyAttackLog(BattleManager.ActionResult r) {
        // We no longer need the massive switch statement because
        // BattleManager now formats the string perfectly for us
        addLogFromResult(r, false);
    }

    private void beginPlayerTurnSequence() {
        setTurnLabel(true);
        BattleManager.ActionResult preCheck = engine.startPlayerTurnCheck();

        if (preCheck != null && preCheck.logMessage != null && !preCheck.logMessage.isEmpty()) {
            addLogFromResult(preCheck, true);
        }
        refreshBattleUI();

        // If the Pre-Check caused the turn to immediately pass to the enemy (e.g., Stunned/Frozen)
        if (engine.getCurrentTurn() == BattleManager.TurnOwner.ENEMY) {
            animating = true;
            setActionsEnabled(false);

            // Check if player died to DoT before the enemy even acts
            if (engine.checkOutcome() == BattleManager.BattleOutcome.DEFEAT) {
                handleDefeat();
                return;
            }

            // Wait a moment so the player can read the "Turn Skipped" message, then start enemy turn
            Timer skipTimer = new Timer(1800, e -> {
                clearLog();
                executeEnemyTurnSequence();
            });
            skipTimer.setRepeats(false);
            skipTimer.start();
        } else {
            // Player is free to move. Enable the buttons!
            setActionsEnabled(true);
            animating = false;
        }
    }

    private void executeEnemyTurnSequence() {
        setTurnLabel(false);
        // Math is calculated in the background
        BattleManager.ActionResult er = engine.enemyTurn();

        boolean enemyAttacked = false;
        if (er != null && er.logMessage != null) {
            if (er.logMessage.contains("uses")) {
                enemyAttacked = true;
            }
        }

        Runnable postEnemyAction = () -> {
            // ★ UI updates precisely when the hit connects!
            refreshBattleUI();
            clearLog();
            if (er != null) addLogFromResult(er, false);

        msg = switch (enemyDef.name) {
            case "Rotfang Wolf" -> "Rotfang Wolf uses Savage Howl" + damage;
            case "Shade Sprite" -> "Shade Sprite uses Trickster Strike" + damage;
            case "Dreadbark Treant" -> "Dreadbark Treant uses Root Snare" + damage;
            case "Carrion Bat" -> "Carrion Bat uses Screech" + damage;
            case "The Hollow Stag"  -> "The Hollow Stag uses " + engine.getLastEnemySkillName() + damage;
            case "Plague Vermin" -> "Plague Vermin uses Plague Bite" + damage;
            case "Forsaken Cultist" -> "Forsaken Cultist uses Shadow Bolt" + damage;
            case "Blight Hound" -> "Blight Hound uses Corpse Explosion" + damage;
            case "Ghoul Footman" -> "Ghoul Footman uses Rotten Cleave" + damage;
            case "The Black Jailer" -> "The Black Jailer uses " + engine.getLastEnemySkillName() + damage;
            case "Luther Von" -> "Luther Von uses " + engine.getLastEnemySkillName() + damage;
            case "Flame Revenant" -> "Flame Revenant uses Ember Burst" + damage;
            case "Bone Warlock" -> "Bone Warlock uses Marrow Bolt" + damage;
            case "Obsidian Crusher" -> "Obsidian Crusher uses Magma Slam" + damage;
            case "Soulflayer Gargoyle" -> "Soulflayer Gargoyle uses Soul Scream" + damage;
            case "Zyrryl" -> "Zyrryl uses " + engine.getLastEnemySkillName() + damage;
            case "Khai the Necromancer" -> "Khai the Necromancer uses " + engine.getLastEnemySkillName() + damage;
            default                 -> null;
        };

        if (enemyAttacked) {
            // Animation plays first, THEN postEnemyAction updates the HP and log
            playEnemyAttack(() -> playKaelHurtAnimation(postEnemyAction));
        } else {
            // If Stunned/Frozen, just update UI and logs immediately
            refreshBattleUI();
            postEnemyAction.run();
        }
    }
    private void handleVictory() {
        if (enemySequence != null) {
            EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
            enemyFightIndex++;

            // 1. Calculate Per-Kill Rewards
            int individualXp = eDef.xpReward;
            boolean isBoss = eDef.name.equals("The Hollow Stag") ||
                    eDef.name.equals("The Black Jailer") ||
                    eDef.name.equals("Luther Von") ||
                    eDef.name.equals("The Tower Warden");
            int individualShards = isBoss ? 10 : 1;

            // 2. Apply Rewards Immediately (Math only, no summary yet)
            boolean leveledUp = ProgressionService.gainExp(currentHero, individualXp, 1);
            currentHero.soulShards += individualShards;

            refreshBattleUI();

            // 3. Clear the log FIRST, then print the kill info so it stays on screen
            clearLog();
            addLog(getPerKillMessage(eDef, enemyFightIndex), GREEN);
            addLog("✨ Gained " + individualXp + " XP and " + individualShards + " Shard(s)!", GOLD);

            if (leveledUp) {
                addLog("🌟 LEVEL UP! Stats increased!", new Color(130, 85, 0));
                // Note: The actual stat breakdown UI will happen at the end of the horde
            }

            // 4. Branch based on whether the horde is finished
            if (enemyFightIndex < eDef.count) {
                // --- MID-HORDE ---
                savedHeroCombatant = engine.getHero();

                // Wait 2 seconds so the player can read the XP, then spawn next enemy
                delay(2000, () -> {
                    clearLog();
                    addLog(getNextApproachMessage(eDef, enemyFightIndex), GOLD);
                    delay(1000, () -> {
                        startNextFight();
                    });
                });
            } else {
                // --- END OF HORDE ---
                savedHeroCombatant = engine.getHero();
                enemySequenceIndex++;
                enemyFightIndex = 0;
                setActionsEnabled(false);

                Runnable afterSequence = () -> {
                    if (enemySequenceIndex < enemySequence.size()) {
                        if (onEnemyGroupDefeated != null)
                            onEnemyGroupDefeated.accept(enemySequenceIndex, this::startNextFight);
                        else delay(500, this::startNextFight);
                    } else {
                        if (onSequenceComplete != null) onSequenceComplete.run();
                    }
                };

                // Wait 2 seconds so they can read the final kill's XP, then show summary
                delay(2000, () -> {
                    startPostVictorySequence(eDef, afterSequence);
                });
            }
        } else {
            showResult(true);
        }
    }

    private void startPostVictorySequence(EnemyDefinition eDef, Runnable onDone) {
        postVictoryEnemy = eDef;
        postVictoryNext = onDone;
        postVictoryStep = PostVictoryStep.LOOT;
        lvlUp_level = 0;

        // NO XP OR SHARDS ARE ADDED TO THE HERO HERE!
        // They were already paid out per-kill.

        // If the player leveled up at ANY point during the horde, the data is waiting here:
        if (currentHero.lastLevelUpData != null) {
            parseLevelUpMsg(currentHero.lastLevelUpData);
            currentHero.lastLevelUpData = null; // Clear it after reading
        }

        setLogFontSmall();
        clearLog();

        // Calculate the summary totals strictly for the UI text
        int totalXp = eDef.xpReward * eDef.count;
        boolean isBoss = eDef.name.equals("The Hollow Stag") ||
                eDef.name.equals("The Black Jailer") ||
                eDef.name.equals("Luther Von") ||
                eDef.name.equals("The Tower Warden");
        int totalShards = (isBoss ? 10 : 1) * eDef.count;

        addLog("⚔️ HORDE CLEARED!", GOLD);
        addLog("Total Rewards Secured:", new Color(200, 200, 200));
        addLog("  +" + totalShards + " Soul Shard(s)", new Color(20, 80, 160));
        addLog("  +" + totalXp + " XP", new Color(140, 90, 0));

        battleContinueBtn.setEnabled(true);
        turnLabel.setText("");
        roundLabel.setText("");
        specialCdLabel.setText("");
        stopEnemyAnimation();
    }

    private void onContinuePressed() {
        if (postVictoryStep == PostVictoryStep.NONE) return;
        battleContinueBtn.setEnabled(false);

        switch (postVictoryStep) {
            case LOOT -> {
                clearLog();
                if (lvlUp_level > 0) {
                    postVictoryStep = PostVictoryStep.LEVEL_UP_ANNOUNCE;
                    setLogFontSmall();
                    addLog("✨ LEVEL UP! You are now Level " + lvlUp_level + "! ✨", new Color(130, 85, 0));
                } else {
                    postVictoryStep = PostVictoryStep.OBJECTIVE;
                    setLogFontNormal();
                    addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD);
                }
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_ANNOUNCE -> {
                postVictoryStep = PostVictoryStep.LEVEL_UP_STATS;
                clearLog();
                addLog("💚 Max HP  : +" + lvlUp_hpGain + "  →  " + lvlUp_newHp, new Color(0, 120, 50));
                addLog("⚔  Max ATK : +" + lvlUp_atkGain + "  →  " + lvlUp_newAtk, new Color(140, 70, 0));
                addLog("🛡  DEF     : +" + lvlUp_defGain + "  →  " + lvlUp_newDef, new Color(20, 70, 160));
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_STATS -> {
                postVictoryStep = PostVictoryStep.LEVEL_UP_RESTORE;
                clearLog();
                addLog("50% of HP & Energy Restored!", new Color(0, 100, 40));
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_RESTORE -> {
                postVictoryStep = PostVictoryStep.OBJECTIVE;
                setLogFontNormal();
                clearLog();
                addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD);
                battleContinueBtn.setEnabled(true);
            }
            case OBJECTIVE -> {
                postVictoryStep = PostVictoryStep.VICTORY_FLAVOUR;
                clearLog();
                addLog(getVictoryFlavourText(postVictoryEnemy), GREEN);
                battleContinueBtn.setEnabled(true);
            }
            case VICTORY_FLAVOUR -> {
                postVictoryStep = PostVictoryStep.LOOT_FLAVOUR;
                clearLog();
                addLog(getLootFlavourText(postVictoryEnemy), new Color(100, 65, 10));
                battleContinueBtn.setEnabled(true);
            }
            case LOOT_FLAVOUR -> {
                postVictoryStep = PostVictoryStep.NONE;
                EnemyDefinition savedEnemy = postVictoryEnemy; // Save a reference
                postVictoryEnemy = null;
                clearLog();
                setLogFontNormal();

                // ★ INJECT MINI-BOSS LOOT HERE ★
                grantMiniBossLoot(savedEnemy, () -> {
                    if (postVictoryNext != null) {
                        Runnable next = postVictoryNext;
                        postVictoryNext = null;
                        next.run();
                    }
                });
            }
        }
    }

    private void handleDefeat() {
        addLog("You have fallen...", RED);

        try {
            if ((boolean) currentHero.getClass().getField("hasPhoenixSoulstone").get(currentHero)) {
                currentHero.getClass().getField("hasPhoenixSoulstone").set(currentHero, false);
                currentHero.currentHp = currentHero.maxHp;
                clearLog();
                addLog("Phoenix Soulstone activated! Revived!", GREEN);
                refreshBattleUI();
                setTurnLabel(true);
                setActionsEnabled(true);
                animating = false;
                return;
            }
        } catch (Exception ignored) {
        }

        String ans = JOptionPane.showInputDialog(this, "Q: What keyword is used to inherit a class in Java?");
        if (ans != null && ans.trim().equalsIgnoreCase("extends")) {
            currentHero.currentHp = currentHero.maxHp / 2;
            currentHero.energy = currentHero.maxEnergy / 2;
            clearLog();
            addLog("Correct! Revived at 50% HP!", GREEN);
            refreshBattleUI();
            setTurnLabel(true);
            setActionsEnabled(true);
            animating = false;
        } else {
            showResult(false);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ FLAVOUR TEXTS
    // ════════════════════════════════════════════════════════════════════════
    private String getPerKillMessage(EnemyDefinition eDef, int killed) {
        String score = killed + "/" + eDef.count;
        return switch (eDef.name) {
            case "Rotfang Wolf" -> "The wolf whimpers and dissolves into black smoke. (" + score + ")";
            case "Shade Sprite" -> "You dispelled the Shade Sprite! (" + score + ")";
            case "Dreadbark Treant" -> "You felled the Dreadbark Treant! (" + score + ")";
            case "Carrion Bat" -> "You slayed the Carrion Bat! (" + score + ")";
            case "The Hollow Stag" -> "The Hollow Stag has fallen! (" + score + ")";
            default -> eDef.name + " defeated! (" + score + ")";
        };
    }

    private String getNextApproachMessage(EnemyDefinition eDef, int killed) {
        int next = killed + 1;
        return switch (eDef.name) {
            case "Rotfang Wolf" -> "Another wolf snarls and steps forward! (" + next + "/" + eDef.count + ")";
            case "Shade Sprite" ->
                    "The mist swirls — another soul screams into existence! (" + next + "/" + eDef.count + ")";
            case "Dreadbark Treant" ->
                    "The ground quakes again! The second ancient giant lumbers forward! (" + next + "/" + eDef.count + ")";
            case "Carrion Bat" ->
                    "Another screech echoes above — the swarm continues! (" + next + "/" + eDef.count + ")";
            default -> "Another " + eDef.name + " approaches! (" + next + "/" + eDef.count + ")";
        };
    }

    private String getObjectiveCompleteText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf" -> "OBJECTIVE: DEFEAT 3 ROTFANG WOLVES!  (3/3)";
            case "Shade Sprite" -> "OBJECTIVE: DEFEAT 2 SHADE SPRITES!  (2/2)";
            case "Dreadbark Treant" -> "OBJECTIVE: DEFEAT 2 DREADBARK TREANTS!  (2/2)";
            case "Carrion Bat" -> "OBJECTIVE: DEFEAT 4 CARRION BATS!  (4/4)";
            case "The Hollow Stag" -> "OBJECTIVE: DEFEAT THE HOLLOW STAG!  COMPLETE";
            default -> "OBJECTIVE: " + eDef.name.toUpperCase() + " CLEARED!";
        };
    }

    private String getVictoryFlavourText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf" ->
                    "Victory! The last of the Rotfang Wolves collapses.\nThe adrenaline in your veins cools, but the forest feels no safer.";
            case "Shade Sprite" ->
                    "With a final shriek, the sprites disperse like fog in the wind.\nThe mist recedes. The whispering in your mind finally stops.";
            case "Dreadbark Treant" ->
                    "The massive Treants freeze and collapse.\nWhere they fall, small green sprouts rise from the ash.";
            case "Carrion Bat" ->
                    "The last bat crashes into the ground.\nThe forest grows quiet. The stench of decay lifts into the cold wind.";
            case "The Hollow Stag" ->
                    "MINI-BOSS DEFEATED!\nThe Stag staggers. The white fire in its antlers flickers and dies.\nIt dissolves into particles of pure light.";
            default -> "You have defeated " + eDef.name + "!";
        };
    }

    private String getLootFlavourText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf" -> "You bandage your wounds and collect what little the wolves carried.";
            case "Shade Sprite" -> "You feel your strength returning after overcoming the darkness.";
            case "Dreadbark Treant" ->
                    "You emerge covered in dust, but victorious. Treasures fall from the decaying wood.";
            case "Carrion Bat" -> "You catch your breath. You feel stronger... and richer.";
            case "The Hollow Stag" -> "You reach out and grasp the light. It pulses with quiet power.";
            default -> "You collect your rewards.";
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ GENERAL UTILITIES
    // ════════════════════════════════════════════════════════════════════════
    private void parseLevelUpMsg(String msg) {
        String[] p = msg.split("\\|");
        if (p.length < 8) return;
        lvlUp_level = Integer.parseInt(p[1]);
        lvlUp_hpGain = Integer.parseInt(p[2]);
        lvlUp_newHp = Integer.parseInt(p[3]);
        lvlUp_atkGain = Integer.parseInt(p[4]);
        lvlUp_newAtk = Integer.parseInt(p[5]);
        lvlUp_defGain = Integer.parseInt(p[6]);
        lvlUp_newDef = Integer.parseInt(p[7]);
    }

    private void addLogFromResult(BattleManager.ActionResult r, boolean isPlayer) {
        // Safety check to prevent null errors
        if (r == null || r.logMessage == null || r.logMessage.trim().isEmpty()) {
            return;
        }

        Color baseColor = isPlayer ? GREEN : RED;
        if (r.wasDefend) baseColor = BLUE;
        if (r.isSpecial) baseColor = PURPLE;

        // Split the large block of text back into individual lines
        String[] logLines = r.logMessage.split("\n");

        for (String log : logLines) {
            log = log.trim();
            if (log.isEmpty()) continue; // Skip blank lines

            Color logColor = baseColor;

            // Dynamic text coloring based on effect emojis!
            if (log.contains("☠️") || log.contains("🩸") || log.contains("🔥")) {
                logColor = new Color(220, 100, 20); // Orange-red for DoT
            } else if (log.contains("❄️") || log.contains("💫") || log.contains("🌀")) {
                logColor = new Color(180, 100, 250); // Purple for CC
            } else if (log.contains("💖") || log.contains("✨")) {
                logColor = new Color(50, 200, 100); // Bright green for heal/mana
            } else if (log.contains("🛡️") || log.contains("📉") || log.contains("🔻") || log.contains("💪")) {
                logColor = new Color(150, 150, 150); // Gray for buff expirations
            }

            addLog(log, logColor);
        }
    }

    private void addLog(String text, Color color) {
        javax.swing.text.StyledDocument doc = logArea.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setForeground(attrs, color);
        javax.swing.text.StyleConstants.setFontFamily(attrs, logArea.getFont().getFamily());
        javax.swing.text.StyleConstants.setFontSize(attrs, logArea.getFont().getSize());
        javax.swing.text.StyleConstants.setBold(attrs, true);
        try {
            doc.insertString(doc.getLength(), text + "\n", attrs);
        } catch (Exception e) {
        }
        logArea.setCaretPosition(doc.getLength());
    }

    private void clearLog() {
        logArea.setText("");
    }

    private void setTurnLabel(boolean isPlayer) {
        if (turnLabel != null) {
            turnLabel.setText(isPlayer ? "YOUR TURN" : "ENEMY TURN");
            turnLabel.setForeground(isPlayer ? GREEN : RED);
        }
    }

    private void setActionsEnabled(boolean enabled) {
        skill1Btn.setEnabled(enabled && (engine == null || engine.canUseSkill1()));
        skill2Btn.setEnabled(enabled && (engine == null || engine.canUseSkill2()));
        skipTurnBtn.setEnabled(enabled);
        ultimateBtn.setEnabled(enabled && engine != null && engine.canUseUltimate());
    }

    private void populateCombatantUI() {
        heroEmojiLbl.setText(heroDef.emoji);
        heroNameLbl.setText(heroDef.name);
        heroRoleLbl.setText(heroDef.role);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + currentHero.level);

        String enemyEmoji = switch (enemyDef.name) {
            case "Rotfang Wolf" -> "🐾";
            case "Shade Sprite" -> "👻";
            default -> enemyDef.emoji;
        };
        enemyEmojiLbl.setText(enemyEmoji);
        enemyNameLbl.setText(enemyDef.name);
        enemyRoleLbl.setText(enemyDef.role);

        heroHpBar.setMaximum(currentHero.maxHp);
        enemyHpBar.setMaximum(currentEnemy.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(currentHero.maxEnergy);

        if (heroDef.skills != null && heroDef.skills.length >= 3) {
            configureSkillButton(skill1Btn, heroDef.skills[0].name, heroDef.skills[0].icon, new Color(60, 30, 90), new Color(130, 60, 200));
            configureSkillButton(skill2Btn, heroDef.skills[1].name, heroDef.skills[1].icon, new Color(30, 60, 90), new Color(52, 120, 219));
            configureSkillButton(ultimateBtn, heroDef.skills[2].name, heroDef.skills[2].icon, new Color(90, 30, 30), new Color(192, 57, 43));
        }
        configureSkillButton(skipTurnBtn, "Skip Turn", "", new Color(30, 60, 40), new Color(39, 174, 96));
    }

    private void refreshBattleUI() {
        // ★ FIX: Constantly update the maximums so mid-fight level-ups scale the bars correctly!
        heroHpBar.setMaximum(currentHero.maxHp);
        enemyHpBar.setMaximum(currentEnemy.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(currentHero.maxEnergy);

        heroHpBar.setValue(currentHero.currentHp);
        heroHpText.setText(currentHero.currentHp + "/" + currentHero.maxHp);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + currentHero.level);

        enemyHpBar.setValue(currentEnemy.currentHp);
        enemyHpText.setText(currentEnemy.currentHp + "/" + currentEnemy.maxHp);

        if (heroEnergyBar != null) {
            heroEnergyBar.setValue(currentHero.energy);
            heroEnergyText.setText(currentHero.energy + "/" + currentHero.maxEnergy);
        }

        roundLabel.setText("Round " + engine.getRound());
        int cd = currentHero.specialCooldown;
        specialCdLabel.setText(cd > 0 ? "CD: " + cd : "");

        skill1Btn.setEnabled(engine == null || engine.canUseSkill1());
        skill2Btn.setEnabled(engine == null || engine.canUseSkill2());
        ultimateBtn.setEnabled(cd == 0 && (engine == null || engine.canUseUltimate()));

        // ★ REMOVED the heroStatusLbl and enemyStatusLbl "Defending" checks here!

        if (heroConditionLabel != null && currentHero != null && currentHero.getStatusManager() != null) {
            heroConditionLabel.setText(currentHero.getStatusManager().getActiveStatusesDisplay());
        }
        if (enemyConditionLabel != null && currentEnemy != null && currentEnemy.getStatusManager() != null) {
            enemyConditionLabel.setText(currentEnemy.getStatusManager().getActiveStatusesDisplay());
        }
    }

    private void initLogFonts() {
        if (normalLogFont != null) return;
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, fs);
                normalLogFont = base.deriveFont(Font.BOLD, 19f);
                smallLogFont = base.deriveFont(Font.BOLD, 14f);
            }
        } catch (Exception ex) {
        }
        if (normalLogFont == null) normalLogFont = new Font("Dialog", Font.BOLD, 16);
        if (smallLogFont == null) smallLogFont = new Font("Dialog", Font.BOLD, 12);
    }

    private void setLogFontSmall() {
        initLogFonts();
        logArea.setFont(smallLogFont);
    }

    private void setLogFontNormal() {
        initLogFonts();
        logArea.setFont(normalLogFont);
    }

    private void delay(int ms, Runnable action) {
        javax.swing.Timer t = new javax.swing.Timer(ms, null);
        t.setRepeats(false);
        t.addActionListener(e -> {
            t.stop();
            action.run();
        });
        t.start();
    }

    private void showResult(boolean victory) {
        resultIcon.setText(victory ? "WIN" : "LOSE");
        resultTitle.setText(victory ? "VICTORY" : "DEFEAT");
        resultTitle.setForeground(victory ? GOLD : RED);
        resultSub.setText(victory ? heroDef.name + " wins!" : heroDef.name + " has fallen...");
        resultOverlay.setBounds(0, 0, getWidth(), getHeight());
        setComponentZOrder(resultOverlay, 0);
        resultOverlay.setVisible(true);
        revalidate();
        repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ SWING BUILDER HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private JButton makeBtn(String normalPath, String hoverPath, int x, int y, int w, int h, String fallback, int hoverOffset) {
        JButton btn = new JButton();
        btn.setBounds(x, y, w, h);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        java.net.URL nUrl = getClass().getResource(normalPath);
        java.net.URL hUrl = getClass().getResource(hoverPath);
        if (nUrl != null) {
            ImageIcon ni = new ImageIcon(new ImageIcon(nUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            ImageIcon hi = hUrl != null ? new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(w + hoverOffset, h + hoverOffset, Image.SCALE_SMOOTH)) : ni;
            btn.setIcon(ni);
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setIcon(hi);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setIcon(ni);
                }
            });
        } else {
            btn.setText(fallback);
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            btn.setBackground(BG_PANEL);
        }
        return btn;
    }

    private JButton makeSkillBtn(String label, Color bg, Color border, int x, int y, int w, int h) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 2),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(border.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private JPanel buildStatCard(boolean isHero) {
        final Color cardBg = new Color(10, 9, 20, 220);
        final Color cardBorder = isHero ? GREEN_DARK : RED_DARK;
        JPanel card = new JPanel(null) {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(cardBorder);
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel emoji = new JLabel("", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emoji.setBounds(4, 4, 40, 40);
        card.add(emoji);

        JLabel name = new JLabel("");
        name.setFont(new Font("Monospaced", Font.BOLD, 11));
        name.setForeground(isHero ? GREEN : RED);
        name.setBounds(50, 4, 215, 16);
        card.add(name);

        JLabel lvlLbl = new JLabel("Lv.1");
        lvlLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        lvlLbl.setForeground(GOLD);
        lvlLbl.setBounds(270, 4, 60, 16);
        card.add(lvlLbl);

        JLabel role = new JLabel("");
        role.setFont(new Font("Monospaced", Font.ITALIC, 9));
        role.setForeground(TEXT_DIM);
        role.setBounds(50, 20, 150, 14);
        card.add(role);

        // Moved "Defending" label up here next to the role so it doesn't overlap
        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        statusLbl.setForeground(BLUE);
        statusLbl.setBounds(200, 20, 100, 14);
        card.add(statusLbl);

        JLabel hpLbl = new JLabel("HP");
        hpLbl.setFont(FONT_STAT);
        hpLbl.setForeground(TEXT_DIM);
        hpLbl.setBounds(4, 38, 40, 14);
        card.add(hpLbl);

        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setForeground(isHero ? GREEN : RED);
        hpBar.setBackground(new Color(30, 28, 50));
        hpBar.setBorder(null);
        hpBar.setBounds(50, 38, 175, 8);
        card.add(hpBar);

        JLabel hpText = new JLabel("—");
        hpText.setFont(FONT_STAT);
        hpText.setForeground(TEXT_BRIGHT);
        hpText.setBounds(228, 34, 62, 16);
        card.add(hpText);

        // ★ NEW: Condition Label (Poisoned, Stunned, etc.)
        JLabel conditionLbl = new JLabel("Cond: Normal");
        conditionLbl.setFont(new Font("Monospaced", Font.BOLD, 9));
        conditionLbl.setForeground(new Color(255, 200, 50));
        conditionLbl.setBounds(50, 72, 280, 14);
        card.add(conditionLbl);

        if (isHero) {
            heroEmojiLbl = emoji;
            heroNameLbl = name;
            heroRoleLbl = role;
            heroLvlLbl = lvlLbl;
            heroHpBar = hpBar;
            heroHpText = hpText;
            heroStatusLbl = statusLbl;
            heroConditionLabel = conditionLbl; // Assigning Hero Label

            JLabel epLbl = new JLabel("EP");
            epLbl.setFont(FONT_STAT);
            epLbl.setForeground(TEXT_DIM);
            epLbl.setBounds(4, 54, 40, 14);
            card.add(epLbl);

            JProgressBar energyBar = new JProgressBar(0, 100);
            energyBar.setValue(100);
            energyBar.setForeground(new Color(200, 180, 80));
            energyBar.setBackground(new Color(30, 28, 50));
            energyBar.setBorder(null);
            energyBar.setBounds(50, 54, 175, 6);
            card.add(energyBar);

            JLabel energyText = new JLabel("—");
            energyText.setFont(FONT_STAT);
            energyText.setForeground(TEXT_BRIGHT);
            energyText.setBounds(228, 50, 62, 14);
            card.add(energyText);

            heroEnergyBar = energyBar;
            heroEnergyText = energyText;
        } else {
            enemyEmojiLbl = emoji;
            enemyNameLbl = name;
            enemyRoleLbl = role;
            enemyHpBar = hpBar;
            enemyHpText = hpText;
            enemyStatusLbl = statusLbl;
            enemyConditionLabel = conditionLbl; // Assigning Enemy Label
        }
        return card;
    }

    private JPanel buildResultOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(OVERLAY_BG);
        overlay.setOpaque(true);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(20, 18, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DIM, 1),
                new EmptyBorder(36, 50, 36, 50)));

        resultIcon = new JLabel("WIN", SwingConstants.CENTER);
        resultIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        resultIcon.setAlignmentX(CENTER_ALIGNMENT);

        resultTitle = new JLabel("VICTORY", SwingConstants.CENTER);
        resultTitle.setFont(FONT_RESULT);
        resultTitle.setForeground(GOLD);
        resultTitle.setAlignmentX(CENTER_ALIGNMENT);

        resultSub = new JLabel(" ", SwingConstants.CENTER);
        resultSub.setFont(new Font("Monospaced", Font.ITALIC, 13));
        resultSub.setForeground(TEXT_DIM);
        resultSub.setAlignmentX(CENTER_ALIGNMENT);
        resultSub.setBorder(new EmptyBorder(4, 0, 24, 0));

        JButton restartBtn = new JButton("Fight Again");
        restartBtn.setFont(FONT_BTN);
        restartBtn.setForeground(new Color(20, 15, 5));
        restartBtn.setBackground(new Color(120, 92, 24));
        restartBtn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD, 1), new EmptyBorder(10, 24, 10, 24)));
        restartBtn.setFocusPainted(false);
        restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartBtn.setAlignmentX(CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> {
            if (onRestartBattle != null) onRestartBattle.run();
        });

        JButton backBtn2 = new JButton("New Champion");
        backBtn2.setFont(FONT_BTN);
        backBtn2.setForeground(GOLD);
        backBtn2.setBackground(new Color(22, 20, 38));
        backBtn2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD_DIM, 1), new EmptyBorder(10, 24, 10, 24)));
        backBtn2.setFocusPainted(false);
        backBtn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn2.setAlignmentX(CENTER_ALIGNMENT);
        backBtn2.addActionListener(e -> {
            if (onReturnToSelection != null) onReturnToSelection.run();
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btns.setBackground(new Color(20, 18, 36));
        btns.add(restartBtn);
        btns.add(backBtn2);

        card.add(resultIcon);
        card.add(Box.createVerticalStrut(8));
        card.add(resultTitle);
        card.add(resultSub);
        card.add(btns);
        overlay.add(card);
        return overlay;
    }

    private JPanel buildLootChoiceOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(OVERLAY_BG);
        overlay.setOpaque(true);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(20, 18, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 2),
                new EmptyBorder(30, 40, 30, 40)));

        JLabel title = new JLabel("CHOOSE YOUR REWARD", SwingConstants.CENTER);
        title.setFont(FONT_RESULT);
        title.setForeground(GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Only one may be taken...", SwingConstants.CENTER);
        subtitle.setFont(new Font("Monospaced", Font.ITALIC, 14));
        subtitle.setForeground(TEXT_DIM);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        JPanel itemsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        itemsPanel.setBackground(new Color(20, 18, 36));
        itemsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setBackground(new Color(30, 28, 45));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(15, 15, 15, 15)));

        lootItem1Icon = new JLabel("?", SwingConstants.CENTER);
        lootItem1Icon.setFont(new Font("Monospaced", Font.BOLD, 48));
        lootItem1Icon.setForeground(TEXT_DIM);
        lootItem1Icon.setPreferredSize(new Dimension(100, 100));
        lootItem1Icon.setAlignmentX(CENTER_ALIGNMENT);

        lootItem1Name = new JLabel("Item 1 Name", SwingConstants.CENTER);
        lootItem1Name.setFont(new Font("Monospaced", Font.BOLD, 16));
        lootItem1Name.setForeground(TEXT_BRIGHT);
        lootItem1Name.setAlignmentX(CENTER_ALIGNMENT);

        lootItem1Desc = new JTextArea("Item 1 Description.\n+Stats\nEffects");
        lootItem1Desc.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lootItem1Desc.setForeground(TEXT_DIM);
        lootItem1Desc.setBackground(new Color(30, 28, 45));
        lootItem1Desc.setLineWrap(true);
        lootItem1Desc.setWrapStyleWord(true);
        lootItem1Desc.setEditable(false);
        lootItem1Desc.setFocusable(false);
        lootItem1Desc.setAlignmentX(CENTER_ALIGNMENT);
        lootItem1Desc.setBorder(new EmptyBorder(10, 0, 15, 0));

        lootItem1Btn = makeSkillBtn("Take Item", new Color(40, 80, 40), GREEN, 0, 0, 120, 40);
        lootItem1Btn.setAlignmentX(CENTER_ALIGNMENT);

        p1.add(lootItem1Icon);
        p1.add(Box.createVerticalStrut(10));
        p1.add(lootItem1Name);
        p1.add(lootItem1Desc);
        p1.add(lootItem1Btn);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setBackground(new Color(30, 28, 45));
        p2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(15, 15, 15, 15)));

        lootItem2Icon = new JLabel("?", SwingConstants.CENTER);
        lootItem2Icon.setFont(new Font("Monospaced", Font.BOLD, 48));
        lootItem2Icon.setForeground(TEXT_DIM);
        lootItem2Icon.setPreferredSize(new Dimension(100, 100));
        lootItem2Icon.setAlignmentX(CENTER_ALIGNMENT);

        lootItem2Name = new JLabel("Item 2 Name", SwingConstants.CENTER);
        lootItem2Name.setFont(new Font("Monospaced", Font.BOLD, 16));
        lootItem2Name.setForeground(TEXT_BRIGHT);
        lootItem2Name.setAlignmentX(CENTER_ALIGNMENT);

        lootItem2Desc = new JTextArea("Item 2 Description.\n+Stats\nEffects");
        lootItem2Desc.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lootItem2Desc.setForeground(TEXT_DIM);
        lootItem2Desc.setBackground(new Color(30, 28, 45));
        lootItem2Desc.setLineWrap(true);
        lootItem2Desc.setWrapStyleWord(true);
        lootItem2Desc.setEditable(false);
        lootItem2Desc.setFocusable(false);
        lootItem2Desc.setAlignmentX(CENTER_ALIGNMENT);
        lootItem2Desc.setBorder(new EmptyBorder(10, 0, 15, 0));

        lootItem2Btn = makeSkillBtn("Take Item", new Color(40, 80, 40), GREEN, 0, 0, 120, 40);
        lootItem2Btn.setAlignmentX(CENTER_ALIGNMENT);

        p2.add(lootItem2Icon);
        p2.add(Box.createVerticalStrut(10));
        p2.add(lootItem2Name);
        p2.add(lootItem2Desc);
        p2.add(lootItem2Btn);

        itemsPanel.add(p1);
        itemsPanel.add(p2);

        card.add(title);
        card.add(subtitle);
        card.add(itemsPanel);
        overlay.add(card);

        return overlay;
    }

    public void enableDevTools() {
        JPanel devPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        devPanel.setBackground(new Color(0, 0, 0, 0));
        devPanel.setOpaque(false);
        devPanel.setBounds(0, 100, 1200, 35); // Made wider to fit the new tools

        // --- EXISTING HIT TOOLS ---
        JLabel lbl = new JLabel("DEV DMG:");
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(new Color(255, 200, 0));
        devPanel.add(lbl);

        JSpinner dmgSpinner = new JSpinner(new SpinnerNumberModel(50, 50, 5000, 50));
        dmgSpinner.setPreferredSize(new Dimension(80, 22));
        dmgSpinner.setFont(new Font("Monospaced", Font.BOLD, 11));
        devPanel.add(dmgSpinner);

        JButton hitHeroBtn = new JButton("Hit Hero");
        hitHeroBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        hitHeroBtn.setForeground(Color.WHITE);
        hitHeroBtn.setBackground(new Color(140, 30, 30));
        hitHeroBtn.setFocusPainted(false);
        hitHeroBtn.addActionListener(e -> {
            if (currentHero == null) return;
            int dmg = (int) dmgSpinner.getValue();
            currentHero.currentHp = Math.max(0, currentHero.currentHp - dmg);
            refreshBattleUI();
            addLog("[DEV] Hero hit for " + dmg + " damage!", new Color(255, 80, 80));
        });
        devPanel.add(hitHeroBtn);

        JButton hitEnemyBtn = new JButton("Hit Enemy");
        hitEnemyBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        hitEnemyBtn.setForeground(Color.WHITE);
        hitEnemyBtn.setBackground(new Color(30, 100, 140));
        hitEnemyBtn.setFocusPainted(false);
        hitEnemyBtn.addActionListener(e -> {
            if (currentEnemy == null) return;
            int dmg = (int) dmgSpinner.getValue();
            currentEnemy.currentHp = Math.max(0, currentEnemy.currentHp - dmg);
            refreshBattleUI();
            addLog("[DEV] Enemy hit for " + dmg + " damage!", new Color(80, 180, 255));
        });
        devPanel.add(hitEnemyBtn);

        JButton killEnemyBtn = new JButton("Kill Enemy");
        killEnemyBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        killEnemyBtn.setForeground(Color.WHITE);
        killEnemyBtn.setBackground(new Color(80, 30, 120));
        killEnemyBtn.setFocusPainted(false);
        killEnemyBtn.addActionListener(e -> {
            if (currentEnemy == null) return;
            currentEnemy.currentHp = 0;
            refreshBattleUI();
            addLog("[DEV] Enemy instantly killed!", new Color(200, 100, 255));
        });
        devPanel.add(killEnemyBtn);

        // --- NEW: ENEMY STAT OVERRIDES ---
        devPanel.add(Box.createHorizontalStrut(20)); // Spacer

        JLabel statLbl = new JLabel("OVERWRITE ENEMY (HP/ATK/DEF):");
        statLbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        statLbl.setForeground(new Color(200, 255, 100));
        devPanel.add(statLbl);

        JSpinner hpSpin = new JSpinner(new SpinnerNumberModel(1000, 1, 99999, 100));
        hpSpin.setPreferredSize(new Dimension(80, 22));
        hpSpin.setToolTipText("Max HP");
        devPanel.add(hpSpin);

        JSpinner atkSpin = new JSpinner(new SpinnerNumberModel(50, 0, 9999, 5));
        atkSpin.setPreferredSize(new Dimension(60, 22));
        atkSpin.setToolTipText("Attack");
        devPanel.add(atkSpin);

        JSpinner defSpin = new JSpinner(new SpinnerNumberModel(10, 0, 9999, 5));
        defSpin.setPreferredSize(new Dimension(60, 22));
        defSpin.setToolTipText("Defense");
        devPanel.add(defSpin);

        JButton setStatsBtn = new JButton("Apply");
        setStatsBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        setStatsBtn.setForeground(Color.WHITE);
        setStatsBtn.setBackground(new Color(20, 120, 40));
        setStatsBtn.setFocusPainted(false);
        setStatsBtn.addActionListener(e -> {
            if (currentEnemy == null) return;

            currentEnemy.maxHp = (int) hpSpin.getValue();
            currentEnemy.currentHp = currentEnemy.maxHp;

            currentEnemy.baseAttack = (int) atkSpin.getValue();
            currentEnemy.attack = currentEnemy.baseAttack;

            currentEnemy.baseDefense = (int) defSpin.getValue();
            currentEnemy.defense = currentEnemy.baseDefense;

            refreshBattleUI();
            addLog("[DEV] Enemy stats forcibly updated! (HP:" + currentEnemy.maxHp + " ATK:" + currentEnemy.attack + " DEF:" + currentEnemy.defense + ")", new Color(100, 255, 100));
        });
        devPanel.add(setStatsBtn);

        add(devPanel);
        setComponentZOrder(devPanel, 0);
        revalidate();
        repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ MINI-BOSS LOOT SYSTEM
    // ════════════════════════════════════════════════════════════════════════
    private void grantMiniBossLoot(EnemyDefinition eDef, Runnable onDone) {
        if (currentHero == null || eDef == null) {
            onDone.run();
            return;
        }

        String role = currentHero.role;

        if (eDef.name.equals("The Hollow Stag")) {
            Weapon w = switch(role) {
                case "Swordsman" -> new Sword(Sword.IRON_SHORTSWORD);
                case "Archer"    -> new Bow(Bow.OAK_LONGBOW);
                default          -> new Staff(Staff.APPRENTICE_STAFF);
            };
            Armor a = new Armor(GameGUI.model.entity.HeroData.IRON_VANGUARD);

            currentHero.inventory.setEquippedWeapon(w);
            currentHero.inventory.setEquippedArmor(a);
            currentHero.recalculateBuffs();

            addLog("🎁 Obtained: " + w.name + " & " + a.name + "!", new Color(200, 180, 50));
            delay(2000, onDone);
        }
        else if (eDef.name.equals("The Black Jailer") || eDef.name.equals("Luther Von")) {
            // World 2 Mini-boss gives a choice!
            showWorld2LootChoice(role, onDone);
        }
        else if (eDef.name.equals("Zyrryl")) {
            Weapon w = switch(role) {
                case "Swordsman" -> new Sword(Sword.ECLIPSE_GREATSWORD);
                case "Archer"    -> new Bow(Bow.AETHERSTRIKE_BOW);
                default          -> new Staff(Staff.AETHERIC_STAFF);
            };
            Armor a = new Armor(GameGUI.model.entity.HeroData.SKYFORGE_PLATE);

            currentHero.inventory.setEquippedWeapon(w);
            currentHero.inventory.setEquippedArmor(a);
            currentHero.recalculateBuffs();

            addLog("🎁 Obtained: " + w.name + " & " + a.name + "!", new Color(200, 180, 50));
            delay(2000, onDone);
        } else {
            // Not a mini-boss, just proceed
            onDone.run();
        }
    }

    private void showWorld2LootChoice(String role, Runnable onDone) {
        Weapon w1, w2;
        if (role.equals("Swordsman")) {
            w1 = new Sword(Sword.TWINSTRIKE_BLADE);
            w2 = new Sword(Sword.LIFEBOND_BLADE);
        } else if (role.equals("Archer")) {
            w1 = new Bow(Bow.TWINSHOT_BOW);
            w2 = new Bow(Bow.LIFEBLOOM_BOW);
        } else {
            w1 = new Staff(Staff.MYSTIC_MIND_STAFF);
            w2 = new Staff(Staff.FLAMEHEART_STAFF);
        }

        lootChoiceOverlay.setVisible(true);
        setComponentZOrder(lootChoiceOverlay, 0);

        // Setup Item 1 (Offensive Weapon + Aegis Mail)
        lootItem1Icon.setText("⚔️");
        lootItem1Name.setText(w1.name);
        lootItem1Desc.setText("ATK: +" + w1.atkBuff + "\n" + getWeaponEffectDesc(w1) + "\n\nIncludes: Aegis Mail (+25 DEF)");
        for (ActionListener al : lootItem1Btn.getActionListeners()) lootItem1Btn.removeActionListener(al);
        lootItem1Btn.addActionListener(e -> {
            lootChoiceOverlay.setVisible(false);
            currentHero.inventory.setEquippedWeapon(w1);
            currentHero.inventory.setEquippedArmor(new Armor(GameGUI.model.entity.HeroData.AEGIS_MAIL));
            currentHero.recalculateBuffs();
            addLog("🎁 Chose: " + w1.name + " & Aegis Mail!", new Color(200, 180, 50));
            delay(1500, onDone);
        });

        // Setup Item 2 (Utility Weapon + Vanguard Robe)
        lootItem2Icon.setText("⚔️");
        lootItem2Name.setText(w2.name);
        lootItem2Desc.setText("ATK: +" + w2.atkBuff + "\n" + getWeaponEffectDesc(w2) + "\n\nIncludes: Vanguard Robe (+25 DEF)");
        for (ActionListener al : lootItem2Btn.getActionListeners()) lootItem2Btn.removeActionListener(al);
        lootItem2Btn.addActionListener(e -> {
            lootChoiceOverlay.setVisible(false);
            currentHero.inventory.setEquippedWeapon(w2);
            currentHero.inventory.setEquippedArmor(new Armor(GameGUI.model.entity.HeroData.VANGUARD_ROBE));
            currentHero.recalculateBuffs();
            addLog("🎁 Chose: " + w2.name + " & Vanguard Robe!", new Color(200, 180, 50));
            delay(1500, onDone);
        });
    }

    private String getWeaponEffectDesc(Weapon w) {
        if (w.extraHitChance > 0) return "20% chance to deal extra damage";
        if (w.lifestealPercent > 0) return "Restores " + w.lifestealPercent + "% HP of damage dealt";
        if (w.stunChance > 0) return "30% chance to Stun enemy";
        return "A powerful weapon.";
    }
}