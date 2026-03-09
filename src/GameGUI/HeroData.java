package GameGUI;

import GameGUI.BattleLogic.Combatant;
import GameGUI.BattleLogic.Special;

import java.util.List;

/**
 * HeroData.java
 *
 * All hero definitions built directly from your actual character files:
 *
 *   Kael.java  → Kael Saint Laurent  (Swordsman, hp:100, def:5,  energy:100, atk:12)
 *   Karl.java  → Karl Clover Dior IV (Archer,    hp:80,  def:3,  energy:24,  atk:14)
 *   Simon.java → Simon Versace       (Mage,      hp:60,  def:2,  energy:120, atk:18)
 *   Null.java  → Null                (Mage,      hp:99999,def:1, energy:9999,atk:9999)
 *
 * Energy names/emojis mirror Character.getEnergyName() and getEnergyEmoji():
 *   Swordsman → "Stamina" / "🔋"
 *   Archer    → "Arrows"  / "➶"
 *   Mage      → "Mana"    / "💧"
 *
 * Skill data (name, icon, cost, multipliers, hits) taken directly from each
 * character's displaySkills() and skill method implementations.
 */
public class HeroData {

    // ─── Playable Heroes ─────────────────────────────────────────────────────

    public static final List<HeroDefinition> HEROES = List.of(

            // ── Kael Saint Laurent — Swordsman ───────────────────────────────────
            // Source: super("Kael Saint Laurent", "Swordsman", 100, 5, 100, 12)
            new HeroDefinition(
                    "Kael Saint Laurent", "Swordsman", "⚔️",
                    100, 5, 100, 100, 12,
                    "Stamina", "🔋",
                    new SkillDefinition[]{
                            new SkillDefinition("Blade Rush",          "🗡️",
                                    "A quick slash that catches the opponent off guard.\n" +
                                            "30% chance to apply Strengthen (+20% ATK for 2 turns)",
                                    5,  1.15, 1.35, false, 1),
                            new SkillDefinition("Piercing Slash",      "⚔️",
                                    "A focused strike that pierces enemy armor. Ignores Defense.\n" +
                                            "30% chance to Stun for 1 turn",
                                    10, 1.35, 1.55, true,  1),
                            new SkillDefinition("Eternal Cross Slash", "✝️",
                                    "3 crossing strikes with unyielding determination.\n" +
                                            "Applies Bleed (2 turns) + Fortified (+20% DEF for 2 turns)",
                                    20, 1.00, 1.80, false, 3)
                    },
                    "Kael Saint Laurent was born in the shadow of the Black Castle, in a family\n" +
                            "that lived modestly within the crumbling Town of Decay. His childhood was\n" +
                            "marked by sickness in the streets and guards demanding bribes, yet Kael\n" +
                            "never let the rot of the world steal his spirit.\n\n" +
                            "Even as a child, he carried himself with the bearing of a leader —\n" +
                            "always playing the protector, never the aggressor.",
                    "Blade Swift — On Critical Hit: gain +5% max Stamina"
            ),

            // ── Karl Clover Dior IV — Archer ─────────────────────────────────────
            // Source: super("Karl Clover Dior IV", "Archer", 80, 3, 24, 14)
            new HeroDefinition(
                    "Karl Clover Dior IV", "Archer", "🏹",
                    80, 3, 24, 24, 14,
                    "Arrows", "➶",
                    new SkillDefinition[]{
                            new SkillDefinition("Piercing Arrow",           "🏹",
                                    "An arrow that slices through armor and flesh. Ignores Defense.\n" +
                                            "30% chance to inflict Bleed (2 turns)",
                                    1,  1.00, 1.25, true,  1),
                            new SkillDefinition("Bullseye",                 "🎯",
                                    "A deadly precise shot. Guaranteed Critical Hit (×1.5).\n" +
                                            "30% chance to apply Weakness (-30% DEF for 2 turns)",
                                    3,  1.10, 1.30, false, 1),
                            new SkillDefinition("Rain of a Thousand Arrows","🌩️",
                                    "5 rapid arrows overwhelming the opponent.\n" +
                                            "Grants Nimble + Strengthen (+20% ATK for 2 turns)",
                                    5,  0.70, 0.80, false, 5)
                    },
                    "Karl Clover Dior IV was born and raised in the Forest of Silence, where\n" +
                            "danger lurks in every shadow. His father taught him the bow not as a\n" +
                            "weapon of glory, but as a means of survival against Rotfang Wolves\n" +
                            "and the twisted creatures that haunted their home.\n\n" +
                            "Now he hunts not just for survival, but to restore the balance of\n" +
                            "the place he calls home.",
                    "Hunter's Instinct — Deal +20% damage to enemies below 30% HP"
            ),

            // ── Simon Versace — Mage ─────────────────────────────────────────────
            // Source: super("Simon Versace", "Mage", 60, 2, 120, 18)
            new HeroDefinition(
                    "Simon Versace", "Mage", "🔮",
                    60, 2, 120, 120, 18,
                    "Mana", "💧",
                    new SkillDefinition[]{
                            new SkillDefinition("Fireball",     "🔥",
                                    "A blazing orb hurled at the enemy. Applies Burn (1 turn).\n" +
                                            "30% chance to Weaken target (-15% ATK for 2 turns)",
                                    15, 1.25, 1.55, false, 1),
                            new SkillDefinition("Ice Prison",   "❄️",
                                    "Encases the target in solid ice. 30% chance to Freeze (skip 1 turn).\n" +
                                            "If frozen: apply Fragile (-15% DEF for 1 turn)",
                                    25, 1.00, 1.25, false, 1),
                            new SkillDefinition("Meteor Storm", "☄️",
                                    "5 blazing meteors strike the opponent.\n" +
                                            "Grants Strengthen (+20% ATK for 2 turns), 50% chance to Burn (2 turns)",
                                    40, 0.60, 0.90, false, 5)
                    },
                    "Simon Versace — a mage with an unshakable dream of becoming the most\n" +
                            "powerful sorcerer alive. From childhood, mana coursed naturally through\n" +
                            "his veins, earning him the title of prodigy in the whispers of others.\n\n" +
                            "He hails from the Forest of Silence. If he truly wished to claim power,\n" +
                            "he would need more than learning — he would need experience.",
                    "Arcane Flow — Restore +5% of total Mana each turn"
            ),

            // ── Null — Mage (debug/secret) ────────────────────────────────────────
            // Source: super("Null", "Mage", 99999, 1, 9999, 9999)
            new HeroDefinition(
                    "Null", "Mage", "🌌",
                    99999, 1, 9999, 9999, 9999,
                    "Mana", "💧",
                    new SkillDefinition[]{
                            new SkillDefinition("Direct Hit",  "💥",
                                    "A direct, overwhelming strike. Pure power with no finesse.",
                                    5,  2.0, 2.0, false, 1),
                            new SkillDefinition("Obliterate",  "🔥",
                                    "A catastrophic blast that reduces the target to ash.",
                                    10, 4.0, 4.0, false, 1),
                            new SkillDefinition("World End",   "🌌",
                                    "Reality-shattering devastation. No defense can withstand this.",
                                    20, 10.0, 10.0, true, 1)
                    },
                    "Null is not a name — it is an absence.\n\n" +
                            "Where other warriors carry histories and scars, Null carries only\n" +
                            "a singular, terrible purpose. The universe did not create Null.\n" +
                            "Null simply... appeared.",
                    "Void Presence — Immune to all status effects"
            )
    );

    // ─── Enemy Definitions ────────────────────────────────────────────────────

    public static final List<EnemyDefinition> ENEMIES = List.of(

            new EnemyDefinition("Rotfang Wolf",       "Forest Predator",             "🐺",
                    60,  10, 3,  1),
            new EnemyDefinition("Dreadlord Malachar", "Undead Warlord",              "💀",
                    120, 20, 12, 2),
            new EnemyDefinition("Ignaroth the Burnt", "Ancient Drake",               "🐉",
                    150, 25, 18, 2),
            new EnemyDefinition("Gorethak",           "Chaos Berserker",             "👹",
                    110, 28, 6,  3),
            new EnemyDefinition("The Lich Varos",     "Necrotic Sorcerer — Final Boss","🧿",
                    200, 30, 14, 3)
    );

    // ─── Factory methods ──────────────────────────────────────────────────────

    /**
     * Builds a BattleLogic.Combatant from a HeroDefinition.
     * The ultimate (skills[2]) becomes the Special used in BattlePanel.
     */

    public static Combatant buildHero(HeroDefinition def) {
        SkillDefinition ult = def.skills[2];
        Special special = new Special(
                ult.name, ult.icon, ult.description,
                (ult.minMult + ult.maxMult) / 2.0,
                ult.piercesArmor,
                3   // ultimateCounter = 3 from Character.java
        );
        return new Combatant(
                def.name, def.classType, def.emoji,
                def.hp, def.attack, def.defense,
                def.energy, def.maxEnergy,
                special
        );
    }

    /** Builds a BattleLogic.Combatant from an EnemyDefinition. */
    public static Combatant buildEnemy(EnemyDefinition def) {
        return new Combatant(
                def.name, def.role, def.emoji,
                def.hp, def.attack, def.defense,
                0, 0, null
        );
    }

    // ─── Data classes ─────────────────────────────────────────────────────────

    public static class HeroDefinition {
        public final String name;
        public final String classType;    // mirrors Character.classType
        public final String emoji;
        public final int hp;
        public final int defense;
        public final int energy;
        public final int maxEnergy;
        public final int attack;
        public final String energyName;   // mirrors Character.getEnergyName()
        public final String energyEmoji;  // mirrors Character.getEnergyEmoji()
        public final SkillDefinition[] skills;       // [0]=skill1, [1]=skill2, [2]=ultimate
        public final String backstory;    // mirrors showBackstory()
        public final String passive;
        public Special special;
        public String[] story;
        public String role;


        public HeroDefinition(String name, String classType, String emoji,
                              int hp, int defense, int energy, int maxEnergy, int attack,
                              String energyName, String energyEmoji,
                              SkillDefinition[] skills,
                              String backstory, String passive) {
            this.name        = name;
            this.classType   = classType;
            this.emoji       = emoji;
            this.hp          = hp;
            this.defense     = defense;
            this.energy      = energy;
            this.maxEnergy   = maxEnergy;
            this.attack      = attack;
            this.energyName  = energyName;
            this.energyEmoji = energyEmoji;
            this.skills      = skills;
            this.backstory   = backstory;
            this.passive     = passive;
            this.role = classType;
            this.story = backstory.split("\n\n");
        }
    }

    public static class SkillDefinition {
        public final String  name;
        public final String  icon;
        public final String  description;
        public final int     energyCost;   // stamina/mana/arrows cost from each skill method
        public final double  minMult;      // RandomUtil.range(atk * minMult, atk * maxMult)
        public final double  maxMult;
        public final boolean piercesArmor; // true = ignores defense (Piercing Slash, etc.)
        public final int     hits;         // number of hits (3 for CrossSlash, 5 for multi-hits)

        public SkillDefinition(String name, String icon, String description,
                               int energyCost, double minMult, double maxMult,
                               boolean piercesArmor, int hits) {
            this.name         = name;
            this.icon         = icon;
            this.description  = description;
            this.energyCost   = energyCost;
            this.minMult      = minMult;
            this.maxMult      = maxMult;
            this.piercesArmor = piercesArmor;
            this.hits         = hits;
        }

        /** e.g. "🗡️ Blade Rush (🔋 5 Stamina)" — for display in selection screen */
        public String displayLabel(String energyEmoji, String energyName) {
            return icon + " " + name + " (" + energyEmoji + " " + energyCost + " " + energyName + ")";
        }

        /** Average multiplier for stat tooltips */
        public double avgMult() { return (minMult + maxMult) / 2.0; }
    }

    public static class EnemyDefinition {
        public final String name;
        public final String role;
        public final String emoji;
        public final int    hp;
        public final int    attack;
        public final int    defense;
        public final int    worldLevel;

        public EnemyDefinition(String name, String role, String emoji,
                               int hp, int attack, int defense, int worldLevel) {
            this.name       = name;
            this.role       = role;
            this.emoji      = emoji;
            this.hp         = hp;
            this.attack     = attack;
            this.defense    = defense;
            this.worldLevel = worldLevel;
        }
    }
}