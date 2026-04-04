package GameGUI.model;

import java.util.List;

/**
 * HeroData — Strictly a Data Registry.
 * Contains only definitions and static constants for heroes, enemies, and items.
 */
public class HeroData {

    // ─── Skill Definition ────────────────────────────────────────────────────
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

    // ─── Weapon Definition ───────────────────────────────────────────────────
    public static class WeaponDef {
        public final String name, rarity;
        public final int atkBuff;
        public final int confuseChance;
        public final int lifestealPercent;
        public final int poisonChance;
        public final int energyPerAttack;

        public WeaponDef(String name, String rarity, int atkBuff,
                         int confuseChance, int lifestealPercent,
                         int poisonChance, int energyPerAttack) {
            this.name = name; this.rarity = rarity; this.atkBuff = atkBuff;
            this.confuseChance = confuseChance; this.lifestealPercent = lifestealPercent;
            this.poisonChance = poisonChance; this.energyPerAttack = energyPerAttack;
        }
    }

    // ─── Armor Definition ────────────────────────────────────────────────────
    public static class ArmorDef {
        public final String name, rarity;
        public final int hpBuff;
        public final int defBuff;

        public ArmorDef(String name, String rarity, int hpBuff, int defBuff) {
            this.name = name; this.rarity = rarity;
            this.hpBuff = hpBuff; this.defBuff = defBuff;
        }
    }

    // ─── Hero Definition ─────────────────────────────────────────────────────
    public static class HeroDefinition {
        public final String name, role, emoji, backstory, passive;
        public final int maxHp, attack, defense, maxEnergy;
        public final SkillDef[] skills;
        public final WeaponDef startingWeapon;
        public final ArmorDef startingArmor;

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

    // ─── Enemy Definition ────────────────────────────────────────────────────
    public static class EnemyDefinition {
        public final String name, role, emoji;
        public final int maxHp, attack, defense;
        public final int worldLevel, count, xpReward;

        public EnemyDefinition(String name, String role, String emoji,
                               int maxHp, int attack, int defense,
                               int worldLevel, int count, int xpReward) {
            this.name = name; this.role = role; this.emoji = emoji;
            this.maxHp = maxHp; this.attack = attack; this.defense = defense;
            this.worldLevel = worldLevel; this.count = count; this.xpReward = xpReward;
        }
    }

    // ─── Shared Item Constants ───────────────────────────────────────────────
    public static final WeaponDef OLD_BROADSWORD = new WeaponDef("Old Broadsword", "White", 5, 0, 0, 0, 0);
    public static final WeaponDef WOODEN_BOW     = new WeaponDef("Wooden Bow", "White", 5, 0, 0, 0, 0);
    public static final WeaponDef WOODEN_STAFF   = new WeaponDef("Wooden Staff", "White", 5, 0, 0, 0, 0);
    public static final ArmorDef  LEATHER_GUARD  = new ArmorDef("Leather Guard", "White", 0, 5);

    // ─── Hero Registry ───────────────────────────────────────────────────────
    // ─── Hero Registry ───────────────────────────────────────────────────────
    public static final List<HeroDefinition> HEROES = List.of(
            new HeroDefinition(
                    "Kael Saint Laurent", "Swordsman", "⚔️",
                    "Born in the shadow of the Black Castle, Kael seeks to avenge his fallen brethren. His blade is sworn to eradicate the darkness.",
                    "Blade Swift",
                    99999, 20, 5, 9999,
                    new SkillDef[]{
                            new SkillDef("⚔", "Blade Rush", "A quick, standard slash.", 1.15, false, 0, 5),
                            new SkillDef("X", "Piercing Slash", "An armor-piercing strike.", 1.35, true, 0, 10),
                            new SkillDef("+", "Eternal Cross Slash", "A devastating 3-hit combo.", 1.40, false, 3, 20)
                    },
                    OLD_BROADSWORD, LEATHER_GUARD
            ),
            new HeroDefinition(
                    "Karl Clover Dior IV", "Archer", "🏹",
                    "Born in the Forest of Silence, Karl's arrows never miss their mark. He hunts the beasts that corrupted his homeland.",
                    "Hunter's Instinct",
                    80, 14, 3, 24,
                    new SkillDef[]{
                            new SkillDef(">", "Piercing Arrow", "A sharp, piercing shot.", 1.00, true, 0, 1),
                            new SkillDef("*", "Bullseye", "A heavy critical shot.", 1.10, false, 0, 3),
                            new SkillDef("~", "Rain of Arrows", "A rapid volley of arrows.", 0.75, false, 3, 5)
                    },
                    WOODEN_BOW, LEATHER_GUARD
            ),
            new HeroDefinition(
                    "Simon Versace", "Mage", "🧙",
                    "Driven by an unshakable dream of power, Simon bends the arcane elements to his will. Magic flows through his veins.",
                    "Arcane Flow",
                    60, 18, 2, 120,
                    new SkillDef[]{
                            new SkillDef("F", "Fireball", "Hurls a ball of searing fire.", 1.25, false, 0, 15),
                            new SkillDef("I", "Ice Prison", "Freezes the enemy in their tracks.", 1.00, false, 0, 25),
                            new SkillDef("M", "Meteor Storm", "Summons destructive meteors.", 0.75, false, 3, 40)
                    },
                    WOODEN_STAFF, LEATHER_GUARD
            ),
            new HeroDefinition(
                    "Null", "Mage", "🌌",
                    "An anomaly in the system. It was never meant to exist. Reality bends and breaks in its presence.",
                    "System Override",
                    99999, 9999, 9999, 9999,
                    new SkillDef[]{
                            new SkillDef("💥", "Direct Hit", "A simple but devastating strike.", 1.0, true, 0, 1),
                            new SkillDef("🔥", "Obliterate", "Erases the target's existence.", 2.0, true, 0, 1),
                            new SkillDef("🌌", "World End", "Brings forth the end of reality.", 10.0, true, 3, 1)
                    },
                    null, null // Null has no need for mortal weapons or armor
            )
    );

    // ─── World 1 Enemy Registry ──────────────────────────────────────────────
    public static final List<EnemyDefinition> WORLD1_ENEMIES = List.of(
            new EnemyDefinition("Rotfang Wolf", "Undead Beast", "W", 40, 14, 3, 1, 3, 34),
            new EnemyDefinition("Shade Sprite", "Lost Soul", "S", 80, 24, 4, 1, 2, 170),
            new EnemyDefinition("Dreadbark Treant", "Ancient", "T", 200, 46, 5, 1, 2, 164),
            new EnemyDefinition("Carrion Bat", "Predator", "B", 260, 64, 6, 1, 4, 108),
            new EnemyDefinition("The Hollow Stag", "Guardian", "H", 450, 100, 21, 1, 1, 926)
    );

    public static final List<EnemyDefinition> WORLD2_ENEMIES = List.of(
            new EnemyDefinition("Plague Vermin", "Infected Beast", "🐀", 520, 13, 90, 2, 3, 540),
            new EnemyDefinition("Forsaken Cultist", "Dark Mage", "🔮", 580, 14, 105, 2, 2, 620),
            new EnemyDefinition("Blight Hound", "Undead Beast", "🐕‍🦺", 620, 13, 112, 2, 2, 680),
            new EnemyDefinition("Ghoul Footman", "Undead Soldier", "🧟‍♂️", 700, 18, 128, 2, 4, 820),
            new EnemyDefinition("The Black Jailer", "Miniboss", "⛓️", 1000, 24, 200, 2, 1, 1800),
            new EnemyDefinition("Luther Von", "Corrupted King", "👑", 1250, 29, 250, 2, 1, 2500)
    );

    // ─── World 3 Enemy Registry ──────────────────────────────────────────────
    public static final List<EnemyDefinition> WORLD3_ENEMIES = List.of(
            new EnemyDefinition("Flame Revenant", "Fire Spirit", "🔥", 1080, 28, 220, 3, 3, 950),
            new EnemyDefinition("Bone Warlock", "Undead Mage", "💀", 1280, 41, 262, 3, 2, 1150),
            new EnemyDefinition("Obsidian Crusher", "Magma Golem", "🗿", 1580, 48, 286, 3, 2, 1400),
            new EnemyDefinition("Soulflayer Gargoyle", "Demon Beast", "🦇", 1420, 48, 280, 3, 2, 1300),
            new EnemyDefinition("Zyrryl", "The Tower Warden", "🛡️", 3880, 67, 320, 3, 1, 4000)
    );

    // ─── Final Boss Registry ─────────────────────────────────────────────────
    public static final List<EnemyDefinition> FINAL_BOSS_SEQUENCE = List.of(
            new EnemyDefinition("Khai the Necromancer", "Final Boss", "👹", 5000, 50, 350, 3, 1, 10000)
    );
}