package GameGUI.model.entity.base;

import GameGUI.model.entity.data.EnemyData;
import GameGUI.model.entity.data.HeroData;
import GameGUI.model.entity.data.definition.*;
import GameGUI.model.equipment.Armor;
import GameGUI.model.equipment.Bow;
import GameGUI.model.equipment.Staff;
import GameGUI.model.equipment.Sword;

import java.util.List;

public class BaseHeroData implements HeroData {

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
                                new SkillDef("✝️", "Eternal Cross Slash", "Unleashes a flurry of crossing strikes. Hits 3 times.\n⚡ Effects: Applies Bleed for 2 turns. Grants Fortified (+20% DEF for 2 turns).", 1.15, 1.35, false, 3, 20, 3)
                        },
                        Sword.OLD_BROADSWORD, Armor.LEATHER_GUARD
                ),
                new HeroDefinition(
                        "Karl Clover Dior IV", "Archer", "🏹",
                        "Born in the Forest of Silence, Karl's arrows never miss their mark.",
                        "Hunter's Instinct - +20% damage to enemies below 30% HP.",
                        80, 12, 3, 80,
                        new SkillDef[]{
                                new SkillDef("🏹", "Piercing Arrow", "Fires an arrow that slices through armor and flesh alike.\n⚡ Effects: 30% chance to inflict Bleed (2 turns).", 1.00, 1.25, true, 0, 1, 1),
                                new SkillDef("🎯", "Bullseye", "Steadies breath for a deadly precise shot. Guaranteed Critical Hit.\n⚡ Effects: 30% chance to apply Weakness (-30% DEF for 2 turns).", 1.10, 1.30, false, 0, 3, 1),
                                new SkillDef("🌩️", "Rain of a Thousand Arrows", "Releases a rapid flurry of arrows. Hits 5 times.\n⚡ Effects: Grants Nimble. Grants Strengthen (+20% ATK for 2 turns).", 1.00, 1.80, false, 3, 5, 5)
                        },
                        Bow.WOODEN_BOW, Armor.LEATHER_GUARD
                ),
                new HeroDefinition(
                        "Simon Versace", "Mage", "🧙",
                        "Driven by an unshakable dream of power, Simon bends the arcane elements to his will.",
                        "Arcane Flow - Restores +5% of total Mana each turn.",
                        60, 18, 2, 150,
                        new SkillDef[]{
                                new SkillDef("🔥", "Fireball", "Conjures a blazing orb of fire and hurls it at an enemy.\n⚡ Effects: Applies Burn (1 turn). 30% chance to Weaken (-15% ATK for 2 turns).", 1.25, 1.55, false, 0, 15, 1),
                                new SkillDef("❄️", "Ice Prison", "Encases the target in solid ice.\n⚡ Effects: 30% chance to Freeze. If frozen: Apply Fragile (-15% DEF for 1 turn).", 1.00, 1.25, false, 0, 25, 1),
                                new SkillDef("☄️", "Meteor Storm", "Summons a storm of blazing meteors. Hits 5 times.\n⚡ Effects: Grants Strengthen (+20% ATK for 2 turns). 50% chance to apply Burn (2 turns).", 0.95, 1.69, false, 3, 40, 5)
                        },
                        Staff.WOODEN_STAFF, Armor.LEATHER_GUARD
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
    public List<EnemyData> getWorld1Enemies() {
        return EnemyRegistry.getWorld1Enemies();
    }

    @Override
    public List<EnemyData> getWorld2Enemies() {
        return EnemyRegistry.getWorld2Enemies();
    }

    @Override
    public List<EnemyData> getWorld3Enemies() {
        return EnemyRegistry.getWorld3Enemies();
    }

    @Override
    public List<EnemyData> getFinalBossSequence() {
        return EnemyRegistry.getFinalBossSequence();
    }


    // ... repeat for World 3 and Final Boss
}