package GameGUI.model.entity;

import GameGUI.model.equipment.Bow;
import GameGUI.model.equipment.Staff;
import GameGUI.model.equipment.Sword;

import java.util.List;

public class BaseHeroData implements HeroData {

    // ─── Armor Methods ─────────────────────────────────────────────────────
    @Override
    public ArmorDef getLeatherGuard() {
        return new ArmorDef("Leather Guard", "⚪", 0, 5, false, false, 0, 0);
    }

    @Override
    public ArmorDef getIronVanguard() {
        return new ArmorDef("Iron Vanguard", "🟢", 0, 10, false, false, 0, 0);
    }

    @Override
    public ArmorDef getAegisMail() {
        return new ArmorDef("Aegis Mail", "🔵", 0, 25, true, false, 0, 0);
    }

    @Override
    public ArmorDef getVanguardRobe() {
        return new ArmorDef("Vanguard Robe", "🟣", 0, 25, false, true, 0, 0);
    }

    @Override
    public ArmorDef getSkyforgePlate() {
        return new ArmorDef("Skyforge Plate", "🟣", 0, 40, true, true, 20, 15);
    }

    @Override
    public ArmorDef getCelestialBattlegear() {
        return new ArmorDef("Celestial Battlegear", "🟡", 0, 50, true, true, 30, 20);
    }

    // ─── Registry Methods ───────────────────────────────────────────────────
    @Override
    public List<HeroDefinition> getHeroes() {
        return List.of(
                new HeroDefinition(
                        "Kael Saint Laurent", "Swordsman", "⚔️",
                        "Born in the shadow of the Black Castle, Kael seeks to avenge his fallen brethren.",
                        "Blade Swift - Gains 5% Stamina on Critical Hit.",
                        100, 10, 5, 60, // Note: Set to normal stats if not testing!
                        new SkillDef[]{
                                new SkillDef("🗡️", "Blade Rush", "A quick, fluid slash that catches the opponent off guard.\n⚡ Effects: 30% chance to apply Strengthen (+20% ATK for 2 turns).", 1.15, 1.35, false, 0, 5, 1),
                                new SkillDef("⚔️", "Piercing Slash", "A powerful, focused strike aimed to pierce enemy's armor.\n⚡ Effects: 30% chance to Stun (1 turn).", 1.35, 1.55, true, 0, 10, 1),
                                new SkillDef("✝️", "Eternal Cross Slash", "Unleashes a flurry of crossing strikes. Hits 3 times.\n⚡ Effects: Applies Bleed for 2 turns. Grants Fortified (+20% DEF for 2 turns).", 1.00, 1.80, false, 3, 20, 3)
                        },
                        Sword.OLD_BROADSWORD, getLeatherGuard()
                ),
                new HeroDefinition(
                        "Karl Clover Dior IV", "Archer", "🏹",
                        "Born in the Forest of Silence, Karl's arrows never miss their mark.",
                        "Hunter's Instinct - +20% damage to enemies below 30% HP.",
                        80, 12, 3, 80,
                        new SkillDef[]{
                                new SkillDef("🏹", "Piercing Arrow", "Fires an arrow that slices through armor and flesh alike.\n⚡ Effects: 30% chance to inflict Bleed (2 turns).", 1.00, 1.25, true, 0, 1, 1),
                                new SkillDef("🎯", "Bullseye", "Steadies breath for a deadly precise shot. Guaranteed Critical Hit.\n⚡ Effects: 30% chance to apply Weakness (-30% DEF for 2 turns).", 1.10, 1.30, false, 0, 3, 1),
                                new SkillDef("🌩️", "Rain of a Thousand Arrows", "Releases a rapid flurry of arrows. Hits 5 times.\n⚡ Effects: Grants Nimble. Grants Strengthen (+20% ATK for 2 turns).", 0.70, 0.80, false, 3, 5, 5)
                        },
                        Bow.WOODEN_BOW, getLeatherGuard()
                ),
                new HeroDefinition(
                        "Simon Versace", "Mage", "🧙",
                        "Driven by an unshakable dream of power, Simon bends the arcane elements to his will.",
                        "Arcane Flow - Restores +5% of total Mana each turn.",
                        60, 18, 2, 120,
                        new SkillDef[]{
                                new SkillDef("🔥", "Fireball", "Conjures a blazing orb of fire and hurls it at an enemy.\n⚡ Effects: Applies Burn (1 turn). 30% chance to Weaken (-15% ATK for 2 turns).", 1.25, 1.55, false, 0, 15, 1),
                                new SkillDef("❄️", "Ice Prison", "Encases the target in solid ice.\n⚡ Effects: 30% chance to Freeze. If frozen: Apply Fragile (-15% DEF for 1 turn).", 1.00, 1.25, false, 0, 25, 1),
                                new SkillDef("☄️", "Meteor Storm", "Summons a storm of blazing meteors. Hits 5 times.\n⚡ Effects: Grants Strengthen (+20% ATK for 2 turns). 50% chance to apply Burn (2 turns).", 2.0, 2.40, false, 3, 40, 5)
                        },
                        Staff.WOODEN_STAFF, getLeatherGuard()
                ),
                new HeroDefinition(
                        "Null", "Mage", "🌌",
                        "An anomaly in the system. It was never meant to exist.",
                        "System Override",
                        99999, 9999, 9999, 9999,
                        new SkillDef[]{
                                new SkillDef("💥", "Direct Hit", "A simple but devastating strike.", 2.0, 2.0, true, 0, 1, 1),
                                new SkillDef("🔥", "Obliterate", "Erases the target's existence.", 4.0, 4.0, true, 0, 1, 1),
                                new SkillDef("🌌", "World End", "Brings forth the end of reality.", 10.0, 10.0, true, 3, 1, 1)
                        },
                        null, null
                )
        );
    }

    @Override
    public List<EnemyDefinition> getWorld1Enemies() {
        return List.of(
                new EnemyDefinition("Rotfang Wolf",     "Undead Beast", "W", 40,  14,  3,  1, 3, 34,  1.00, 1.20),
                new EnemyDefinition("Shade Sprite",     "Lost Soul",    "S", 80,  24,  4,  1, 2, 170, 1.00, 1.20),
                new EnemyDefinition("Dreadbark Treant", "Ancient",      "T", 200, 46,  5,  1, 2, 164, 1.00, 1.20),
                new EnemyDefinition("Carrion Bat",      "Predator",     "B", 260, 64,  6,  1, 4, 108, 1.00, 1.20),
                new EnemyDefinition("The Hollow Stag",  "Guardian",     "H", 450, 100, 21, 1, 1, 926, 1.00, 1.20)
        );
    }

    @Override
    public List<EnemyDefinition> getWorld2Enemies() {
        return List.of(
                new EnemyDefinition("Plague Vermin",    "Infected Beast",  "🐀",     520,   90,  13, 2, 3,  135, 1.00, 1.20),
                new EnemyDefinition("Forsaken Cultist", "Dark Mage",       "🔮",     580,  105,  14, 2, 2,  233, 1.00, 1.20),
                new EnemyDefinition("Blight Hound",     "Undead Beast",    "🐕‍🦺",     620,  112,  13, 2, 3,  383, 1.00, 1.20),
                new EnemyDefinition("Ghoul Footman",    "Undead Soldier",  "🧟‍♂️",     700,  128,  18, 2, 2,  360, 1.00, 1.20),
                new EnemyDefinition("The Black Jailer", "Miniboss",        "⛓️",    1000,  200,  24, 2, 1, 1915, 1.00, 1.70),
                new EnemyDefinition("Luther Von",       "Corrupted King",  "👑",    1250,  250,  29, 2, 1, 5200, 0.70, 1.15)
        );
    }

    @Override
    public List<EnemyDefinition> getWorld3Enemies() {
        return List.of(
                new EnemyDefinition("Flame Revenant",      "Fire Spirit",  "🔥", 1080, 220, 28, 3, 3, 1633, 1.00, 1.20),
                new EnemyDefinition("Bone Warlock",        "Undead Mage",  "💀", 1280, 262, 41, 3, 3, 2083, 1.00, 1.20),
                new EnemyDefinition("Obsidian Crusher",    "Magma Golem",  "🗿", 1580, 286, 48, 3, 2, 1900, 1.00, 1.20),
                new EnemyDefinition("Soulflayer Gargoyle", "Demon Beast",  "🦇", 1420, 280, 48, 3, 3, 3800, 1.00, 1.20),
                new EnemyDefinition("Zyrryl",              "Tower Warden", "🛡️", 3880, 320, 67, 3, 1, 7900, 1.00, 1.15)
        );
    }

    @Override
    public List<EnemyDefinition> getFinalBossSequence() {
        return List.of(
                new EnemyDefinition("Khai the Necromancer", "Final Boss", "👹", 5000, 350, 50, 3, 1, 999999, 0.80, 1.30)
        );
    }
}