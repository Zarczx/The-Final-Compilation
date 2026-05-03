package GameGUI.model.entity;

import java.util.List;

/**
 * HeroData — Strictly a Data Registry.
 * Contains only definitions and static constants for heroes, enemies, and items.
 */
public class HeroData {

    // ─── Weapon Types ────────────────────────────────────────────────────────
    public enum WeaponType { SWORD, BOW, STAFF }

    // ─── Skill Definition ────────────────────────────────────────────────────
    public static class SkillDef {
        public final String icon, name, description;
        public final double minMultiplier, maxMultiplier; // Replaced single multiplier
        public final boolean pierceArmor;
        public final int cooldown;
        public final int energyCost;

        public SkillDef(String icon, String name, String description,
                        double minMultiplier, double maxMultiplier,
                        boolean pierceArmor, int cooldown, int energyCost) {
            this.icon = icon; this.name = name; this.description = description;
            this.minMultiplier = minMultiplier; this.maxMultiplier = maxMultiplier;
            this.pierceArmor = pierceArmor;
            this.cooldown = cooldown; this.energyCost = energyCost;
        }
    }

    // ─── Weapon Definition ───────────────────────────────────────────────────
    public static class WeaponDef {
        public final WeaponType type; // <--- ADDED WEAPON TYPE
        public final String name, rarity;
        public final int atkBuff;
        public final int lifestealPercent, poisonChance, bleedChance, stunChance;
        public final int freezeChance, confuseChance, energyPerAttack, extraHitChance;

        public WeaponDef(WeaponType type, String name, String rarity, int atkBuff, int lifestealPercent,
                         int poisonChance, int bleedChance, int stunChance,
                         int freezeChance, int confuseChance, int energyPerAttack, int extraHitChance) {
            this.type = type; // <--- ASSIGNED WEAPON TYPE
            this.name = name; this.rarity = rarity; this.atkBuff = atkBuff;
            this.lifestealPercent = lifestealPercent; this.poisonChance = poisonChance;
            this.bleedChance = bleedChance; this.stunChance = stunChance;
            this.freezeChance = freezeChance; this.confuseChance = confuseChance;
            this.energyPerAttack = energyPerAttack; this.extraHitChance = extraHitChance;
        }
    }

    // ─── Armor Definition ────────────────────────────────────────────────────
    public static class ArmorDef {
        public final String name, rarity;
        public final int hpBuff, defBuff;
        public final boolean immuneDebuff, immuneEffects;
        public final int reflectChance, reflectPercent;

        public ArmorDef(String name, String rarity, int hpBuff, int defBuff,
                        boolean immuneDebuff, boolean immuneEffects, int reflectChance, int reflectPercent) {
            this.name = name; this.rarity = rarity; this.hpBuff = hpBuff; this.defBuff = defBuff;
            this.immuneDebuff = immuneDebuff; this.immuneEffects = immuneEffects;
            this.reflectChance = reflectChance; this.reflectPercent = reflectPercent;
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
        public final int maxHp, attack, defense, worldLevel, count, xpReward;


        public EnemyDefinition(String name, String role, String emoji,
                               int maxHp, int attack, int defense,
                               int worldLevel, int count, int xpReward) {
            this.name = name; this.role = role; this.emoji = emoji;
            this.maxHp = maxHp; this.attack = attack; this.defense = defense;
            this.worldLevel = worldLevel; this.count = count; this.xpReward = xpReward;
        }
    }

    // ─── Shared Item Constants ───────────────────────────────────────────────
    // ⚔️ SWORDS (Focus on Extra Hit Chance)
    public static final WeaponDef OLD_BROADSWORD     = new WeaponDef(WeaponType.SWORD, "Old Broadsword", "⚪", 5, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef IRON_SHORTSWORD    = new WeaponDef(WeaponType.SWORD, "Iron Shortsword", "🟢", 10, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef TWINSTRIKE_BLADE   = new WeaponDef(WeaponType.SWORD, "Twinstrike Blade", "🔵", 20, 0, 0, 0, 0, 0, 0, 0, 20);
    public static final WeaponDef LIFEBOND_BLADE     = new WeaponDef(WeaponType.SWORD, "Lifebond Blade", "🔵", 20, 8, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef ECLIPSE_GREATSWORD = new WeaponDef(WeaponType.SWORD, "Eclipse Greatsword", "🟣", 35, 12, 0, 0, 0, 0, 0, 0, 25);
    public static final WeaponDef CELESTIAL_EDGE     = new WeaponDef(WeaponType.SWORD, "Celestial Edge", "🟡", 50, 15, 0, 0, 0, 0, 0, 0, 30);

    // 🏹 BOWS (Focus on Extra Hit Chance & Lifesteal)
    public static final WeaponDef WOODEN_BOW         = new WeaponDef(WeaponType.BOW, "Wooden Bow", "⚪", 5, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef OAK_LONGBOW        = new WeaponDef(WeaponType.BOW, "Oak Longbow", "🟢", 10, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef TWINSHOT_BOW       = new WeaponDef(WeaponType.BOW, "Twinshot Bow", "🔵", 20, 0, 0, 0, 0, 0, 0, 0, 20);
    public static final WeaponDef LIFEBLOOM_BOW      = new WeaponDef(WeaponType.BOW, "Lifebloom Bow", "🔵", 20, 8, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef AETHERSTRIKE_BOW   = new WeaponDef(WeaponType.BOW, "Aetherstrike Bow", "🟣", 35, 12, 0, 0, 0, 0, 0, 0, 25);
    public static final WeaponDef GOLDEN_TALON       = new WeaponDef(WeaponType.BOW, "Golden Talon", "🟡", 50, 15, 0, 0, 0, 0, 0, 0, 30);

    // 🔮 STAFFS (Focus on Confuse Chance)
    public static final WeaponDef WOODEN_STAFF       = new WeaponDef(WeaponType.STAFF, "Wooden Staff", "⚪", 5, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef APPRENTICE_STAFF   = new WeaponDef(WeaponType.STAFF, "Apprentice's Staff", "🟢", 10, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef MYSTIC_MIND_STAFF  = new WeaponDef(WeaponType.STAFF, "Mystic Mind Staff", "🔵", 20, 0, 0, 0, 0, 0, 30, 0, 0);
    public static final WeaponDef FLAMEHEART_STAFF   = new WeaponDef(WeaponType.STAFF, "Flameheart Staff", "🔵", 20, 8, 0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef AETHERIC_STAFF     = new WeaponDef(WeaponType.STAFF, "Aetheric Staff", "🟣", 35, 12, 0, 0, 0, 0, 25, 0, 0);
    public static final WeaponDef CHRONOMANCER_STAFF = new WeaponDef(WeaponType.STAFF, "Chronomancer Staff", "🟡", 50, 15, 0, 0, 0, 0, 35, 0, 0);
    // 🛡️ ARMORS
    public static final ArmorDef  LEATHER_GUARD      = new ArmorDef("Leather Guard", "⚪", 0, 5, false, false, 0, 0);
    public static final ArmorDef  IRON_VANGUARD      = new ArmorDef("Iron Vanguard", "🟢", 0, 10, false, false, 0, 0);
    public static final ArmorDef  AEGIS_MAIL         = new ArmorDef("Aegis Mail", "🔵", 0, 25, true, false, 0, 0);
    public static final ArmorDef  VANGUARD_ROBE      = new ArmorDef("Vanguard Robe", "🟣", 0, 25, false, true, 0, 0);
    public static final ArmorDef  SKYFORGE_PLATE     = new ArmorDef("Skyforge Plate", "🟣", 0, 40, true, true, 20, 15);
    public static final ArmorDef  CELESTIAL_BATTLEGEAR = new ArmorDef("Celestial Battlegear", "🟡", 0, 50, true, true, 30, 20);

    // ─── Hero Registry ───────────────────────────────────────────────────────
    public static final List<HeroDefinition> HEROES = List.of(
            new HeroDefinition(
                    "Kael Saint Laurent", "Swordsman", "⚔️",
                    "Born in the shadow of the Black Castle, Kael seeks to avenge his fallen brethren.",
                    "Blade Swift - Gains 5% Stamina on Critical Hit.",
                    99999, 20, 5, 9999, // Note: Set to normal stats if not testing!
                    new SkillDef[]{
                            new SkillDef("🗡️", "Blade Rush", "A quick, fluid slash that catches the opponent off guard.\n⚡ Effects: 30% chance to apply Strengthen (+20% ATK for 2 turns).", 1.15, 1.35, false, 0, 5),
                            new SkillDef("⚔️", "Piercing Slash", "A powerful, focused strike aimed to pierce enemy's armor.\n⚡ Effects: 30% chance to Stun (1 turn).", 1.35, 1.55, true, 0, 10),
                            new SkillDef("✝️", "Eternal Cross Slash", "Unleashes a flurry of crossing strikes. Hits 3 times.\n⚡ Effects: Applies Bleed for 2 turns. Grants Fortified (+20% DEF for 2 turns).", 1.00, 1.80, false, 3, 20)
                    },
                    OLD_BROADSWORD, LEATHER_GUARD
            ),
            new HeroDefinition(
                    "Karl Clover Dior IV", "Archer", "🏹",
                    "Born in the Forest of Silence, Karl's arrows never miss their mark.",
                    "Hunter's Instinct - +20% damage to enemies below 30% HP.",
                    80, 14, 3, 24,
                    new SkillDef[]{
                            new SkillDef("🏹", "Piercing Arrow", "Fires an arrow that slices through armor and flesh alike.\n⚡ Effects: 30% chance to inflict Bleed (2 turns).", 1.00, 1.25, true, 0, 1),
                            new SkillDef("🎯", "Bullseye", "Steadies breath for a deadly precise shot. Guaranteed Critical Hit.\n⚡ Effects: 30% chance to apply Weakness (-30% DEF for 2 turns).", 1.10, 1.30, false, 0, 3),
                            new SkillDef("🌩️", "Rain of a Thousand Arrows", "Releases a rapid flurry of arrows. Hits 5 times.\n⚡ Effects: Grants Nimble. Grants Strengthen (+20% ATK for 2 turns).", 0.70, 0.80, false, 3, 5)
                    },
                    WOODEN_BOW, LEATHER_GUARD
            ),
            new HeroDefinition(
                    "Simon Versace", "Mage", "🧙",
                    "Driven by an unshakable dream of power, Simon bends the arcane elements to his will.",
                    "Arcane Flow - Restores +5% of total Mana each turn.",
                    60, 18, 2, 120,
                    new SkillDef[]{
                            new SkillDef("🔥", "Fireball", "Conjures a blazing orb of fire and hurls it at an enemy.\n⚡ Effects: Applies Burn (1 turn). 30% chance to Weaken (-15% ATK for 2 turns).", 1.25, 1.55, false, 0, 15),
                            new SkillDef("❄️", "Ice Prison", "Encases the target in solid ice.\n⚡ Effects: 30% chance to Freeze. If frozen: Apply Fragile (-15% DEF for 1 turn).", 1.00, 1.25, false, 0, 25),
                            new SkillDef("☄️", "Meteor Storm", "Summons a storm of blazing meteors. Hits 5 times.\n⚡ Effects: Grants Strengthen (+20% ATK for 2 turns). 50% chance to apply Burn (2 turns).", 0.60, 0.90, false, 3, 40)
                    },
                    WOODEN_STAFF, LEATHER_GUARD
            ),
            new HeroDefinition(
                    "Null", "Mage", "🌌",
                    "An anomaly in the system. It was never meant to exist.",
                    "System Override",
                    99999, 9999, 9999, 9999,
                    new SkillDef[]{
                            new SkillDef("💥", "Direct Hit", "A simple but devastating strike.", 2.0, 2.0, true, 0, 1),
                            new SkillDef("🔥", "Obliterate", "Erases the target's existence.", 4.0, 4.0, true, 0, 1),
                            new SkillDef("🌌", "World End", "Brings forth the end of reality.", 10.0, 10.0, true, 3, 1)
                    },
                    null, null
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