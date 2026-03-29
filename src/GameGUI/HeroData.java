package GameGUI;

import GameGUI.BattleLogic.*;
import java.util.List;

public class HeroData {

    // ─── Skill definition ────────────────────────────────────────────────────
    public static class SkillDef {
        public final String icon, name, description;
        public final double multiplier;
        public final boolean pierceArmor;
        public final int cooldown;
        public final int energyCost;

        public SkillDef(String icon, String name, String description,
                        double multiplier, boolean pierceArmor, int cooldown, int energyCost) {
            this.icon = icon; this.name = name; this.description = description;
            this.multiplier = multiplier; this.pierceArmor = pierceArmor;
            this.cooldown = cooldown; this.energyCost = energyCost;
        }
    }

    // ─── Weapon definition ───────────────────────────────────────────────────
    // Mirrors console Staff/Sword/Bow stats — applied when buildHero is called
    public static class WeaponDef {
        public final String name, rarity;
        public final int atkBuff;           // flat ATK added to hero base
        public final int confuseChance;     // % chance to confuse enemy on hit
        public final int lifestealPercent;  // % of damage restored as HP
        public final int poisonChance;      // % chance to apply poison DoT
        public final int energyPerAttack;   // energy restored after each attack

        public WeaponDef(String name, String rarity, int atkBuff,
                         int confuseChance, int lifestealPercent,
                         int poisonChance, int energyPerAttack) {
            this.name = name; this.rarity = rarity; this.atkBuff = atkBuff;
            this.confuseChance = confuseChance; this.lifestealPercent = lifestealPercent;
            this.poisonChance = poisonChance; this.energyPerAttack = energyPerAttack;
        }
    }

    // ─── Armor definition ────────────────────────────────────────────────────
    public static class ArmorDef {
        public final String name, rarity;
        public final int hpBuff;    // flat HP added to hero maxHp
        public final int defBuff;   // flat DEF added to hero base defense

        public ArmorDef(String name, String rarity, int hpBuff, int defBuff) {
            this.name = name; this.rarity = rarity;
            this.hpBuff = hpBuff; this.defBuff = defBuff;
        }
    }

    // ─── Starting weapons (World 1) ──────────────────────────────────────────
    // All match console atkBuff=5 exactly
    public static final WeaponDef OLD_BROADSWORD =
            new WeaponDef("Old Broadsword", "White", 5, 0, 0, 0, 0);
    public static final WeaponDef WOODEN_BOW =
            new WeaponDef("Wooden Bow", "White", 5, 0, 0, 0, 0);
    public static final WeaponDef WOODEN_STAFF =
            new WeaponDef("Wooden Staff", "White", 5, 0, 0, 0, 0);

    // ─── Starting armor (World 1, shared) ────────────────────────────────────
    // Leather Guard: +5 DEF only — matches console Armor.LEATHER_GUARD defBuff=5
    public static final ArmorDef LEATHER_GUARD =
            new ArmorDef("Leather Guard", "White", 0, 5);

    // ─── Hero definition ─────────────────────────────────────────────────────
    public static class HeroDefinition {
        public final String name, role, emoji, backstory, passive;
        public final int maxHp, attack, defense, maxEnergy;
        public final SkillDef[] skills;
        public final WeaponDef startingWeapon;
        public final ArmorDef  startingArmor;

        public HeroDefinition(String name, String role, String emoji, String backstory, String passive,
                              int maxHp, int attack, int defense, int maxEnergy,
                              SkillDef[] skills, WeaponDef startingWeapon, ArmorDef startingArmor) {
            this.name = name; this.role = role; this.emoji = emoji;
            this.backstory = backstory; this.passive = passive;
            this.maxHp = maxHp; this.attack = attack; this.defense = defense;
            this.maxEnergy = maxEnergy; this.skills = skills;
            this.startingWeapon = startingWeapon;
            this.startingArmor  = startingArmor;
        }
    }

    // ─── Enemy definition ────────────────────────────────────────────────────
    public static class EnemyDefinition {
        public final String name, role, emoji;
        public final int maxHp, attack, defense;
        public final int worldLevel;
        public final int count;
        public final int xpReward; // XP given per individual kill (matches console rewardExp())
        public final String encounterDialogue;
        public final String defeatDialogue;

        public EnemyDefinition(String name, String role, String emoji,
                               int maxHp, int attack, int defense,
                               int worldLevel, int count, int xpReward,
                               String encounterDialogue, String defeatDialogue) {
            this.name = name; this.role = role; this.emoji = emoji;
            this.maxHp = maxHp; this.attack = attack; this.defense = defense;
            this.worldLevel = worldLevel; this.count = count; this.xpReward = xpReward;
            this.encounterDialogue = encounterDialogue;
            this.defeatDialogue = defeatDialogue;
        }
    }

    // ─── Hero roster ─────────────────────────────────────────────────────────
    public static final List<HeroDefinition> HEROES = List.of(

            new HeroDefinition(
                    "Kael Saint Laurent", "Swordsman", "⚔️",
                    "Kael Saint Laurent was born in the shadow of the Black Castle...\n" +
                            "He spent hours watching soldiers train, turning mimicry into skill.\n" +
                            "In a place where hope was rare, Kael became a quiet anchor.",
                    "Blade Swift — Critical hits restore +5% Stamina",
                    100, 12, 5, 100,
                    new SkillDef[]{
                            new SkillDef("⚔", "Blade Rush",
                                    "A quick slash. 30% chance to Strengthen (+20% ATK 2 turns).",
                                    1.15, false, 0, 5),
                            new SkillDef("X", "Piercing Slash",
                                    "Armor-piercing strike. 30% chance to Stun.",
                                    1.35, true, 0, 10),
                            new SkillDef("+", "Eternal Cross Slash",
                                    "3-hit combo. Applies Bleed 2 turns. Grants +20% DEF 2 turns.",
                                    1.40, false, 3, 20)
                    },
                    OLD_BROADSWORD, LEATHER_GUARD
            ),

            new HeroDefinition(
                    "Karl Clover Dior IV", "Archer", "O",
                    "Karl was born in the Forest of Silence, raised by an archer father.\n" +
                            "His instincts are quiet, patient, always watching.\n" +
                            "He hunts to restore balance to his corrupted homeland.",
                    "Hunter's Instinct — Deal +20% damage to enemies below 30% HP",
                    80, 14, 3, 24,
                    new SkillDef[]{
                            new SkillDef(">", "Piercing Arrow",
                                    "Armor-piercing. 30% chance to Bleed (2 turns).",
                                    1.00, true, 0, 1),
                            new SkillDef("*", "Bullseye",
                                    "Guaranteed critical hit x1.5. 30% chance to Weaken DEF -30%.",
                                    1.10, false, 0, 3),
                            new SkillDef("~", "Rain of a Thousand Arrows",
                                    "5-hit combo. Grants Nimble & Strengthen +20% ATK 2 turns.",
                                    0.75, false, 3, 5)
                    },
                    WOODEN_BOW, LEATHER_GUARD
            ),

            new HeroDefinition(
                    "Simon Versace", "Mage", "O",
                    "Simon Versace — a mage with an unshakable dream of greatest power.\n" +
                            "Mana coursed naturally through his veins from childhood.\n" +
                            "He seeks experience beyond the confines of books.",
                    "Arcane Flow — Restore +5% Mana each turn",
                    60, 18, 2, 120,
                    new SkillDef[]{
                            new SkillDef("F", "Fireball",
                                    "Applies Burn 1 turn. 30% chance to Weaken ATK -15% 2 turns.",
                                    1.25, false, 0, 15),
                            new SkillDef("I", "Ice Prison",
                                    "50% chance to Freeze (skip turn) + Fragile DEF -15% 1 turn.",
                                    1.00, false, 0, 25),
                            new SkillDef("M", "Meteor Storm",
                                    "5-hit combo. 50% chance Burn 2 turns. Strengthen +20% ATK 2 turns.",
                                    0.75, false, 3, 40)
                    },
                    WOODEN_STAFF, LEATHER_GUARD
            ),

            new HeroDefinition(
                    "???", "Unknown", "?",
                    "A mysterious entity whose origin remains unknown.",
                    null,
                    50, 10, 2, 50,
                    new SkillDef[]{
                            new SkillDef("?", "Unknown Skill 1", "???", 1.0, false, 0, 5),
                            new SkillDef("?", "Unknown Skill 2", "???", 1.2, false, 0, 10),
                            new SkillDef("?", "Unknown Ultimate", "???", 1.5, false, 3, 20)
                    },
                    null, null
            )
    );

    // ─── World 1 enemies — stats match console World1Enemy classes exactly ───
    public static final List<EnemyDefinition> WORLD1_ENEMIES = List.of(

            new EnemyDefinition(
                    "Rotfang Wolf", "Undead Beast", "W",
                    40, 14, 3,
                    1, 3, 34,
                    "Three Rotfang Wolves emerge from the tree line!\nTheir glowing red eyes fixate on you.",
                    "The last wolf collapses. The adrenaline cools, but the forest feels no safer."
            ),

            new EnemyDefinition(
                    "Shade Sprite", "Lost Soul", "S",
                    80, 24, 4,
                    1, 2, 170,
                    "Shadows twist into vague human shapes that flicker in and out of existence.\nShade Sprites — the jealous souls of fallen travelers.",
                    "With a final shriek, the sprites disperse like fog in the wind."
            ),

            new EnemyDefinition(
                    "Dreadbark Treant", "Corrupted Ancient", "T",
                    200, 46, 5,
                    1, 2, 164,
                    "The earth heaves! Two Dreadbark Treants pull themselves from the ground.\nTheir hollow eyes burn with green necrotic fire.",
                    "The massive Treants freeze and collapse. Small green sprouts rise from the ash."
            ),

            new EnemyDefinition(
                    "Carrion Bat", "Winged Predator", "B",
                    260, 64, 6,
                    1, 4, 108,
                    "A shrill screech pierces the silence! Four Carrion Bats dive from the branches.\nTheir fangs drip with venom. They circle, sensing your fatigue.",
                    "The last bat crashes down. The stench of decay lifts into the cold wind."
            ),

            new EnemyDefinition(
                    "The Hollow Stag", "Corrupted Guardian", "H",
                    450, 100, 21,
                    1, 1, 926,
                    "From behind a blackened oak steps a massive stag, twelve feet tall.\nIts antlers glow with white fire. Its eyes burn with ancient, crushing sadness.",
                    "The Hollow Stag staggers. The white fire in its antlers flickers and dies.\nIt dissolves into particles of pure light."
            )
    );

    public static final List<EnemyDefinition> ENEMIES = WORLD1_ENEMIES;

    // ─── Build Combatant from HeroDefinition (applies weapon + armor bonuses) ─
    public static Combatant buildHero(HeroDefinition def) {
        BattleLogic.Special special = null;
        if (def.skills != null && def.skills.length >= 3) {
            SkillDef ult = def.skills[2];
            special = new BattleLogic.Special(
                    ult.name, ult.icon, ult.description,
                    ult.multiplier, ult.pierceArmor, ult.cooldown
            );
        }

        // Base stats
        int hp     = def.maxHp;
        int atk    = def.attack;
        int def_   = def.defense;

        // Apply starting armor bonus
        if (def.startingArmor != null) {
            hp  += def.startingArmor.hpBuff;
            def_ += def.startingArmor.defBuff;
        }
        // Apply starting weapon ATK bonus
        if (def.startingWeapon != null) {
            atk += def.startingWeapon.atkBuff;
        }

        return new Combatant(
                def.name, def.role, def.emoji,
                hp, atk, def_,
                def.maxEnergy, def.maxEnergy,
                special
        ) {{
            specialCooldown = 3; // Ultimate locked for first 3 turns (matches console ultimateCounter=3)
        }};
    }

    // ─── Build Combatant from EnemyDefinition ────────────────────────────────
    public static Combatant buildEnemy(EnemyDefinition def) {
        return new Combatant(
                def.name, def.role, def.emoji,
                def.maxHp, def.attack, def.defense,
                0, 0, null
        );
    }
}