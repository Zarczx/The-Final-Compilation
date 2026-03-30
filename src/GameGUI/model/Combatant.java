package GameGUI.model;

import GameGUI.logic.BattleLogic;

/**
 * Combatant — The "Live" state of a character.
 * Stores mutable stats, status effects, and progression data.
 */
public class Combatant {
    public final String name, role, emoji;
    public final BattleLogic.Special special;

    // Core Stats
    public int maxHp, attack, defense, maxEnergy;
    public int baseAttack, baseDefense;
    public int currentHp, energy;

    // Battle State
    public boolean defending;
    public int specialCooldown;
    public int attackModifier, defenseModifier;
    public int dotDamage, dotTurnsLeft;

    // Progression & Currency
    public int level = 1;
    public int exp = 0;
    public int nextLevelExp = 100;
    public String lastLevelUpData = null; // Raw data string for UI parsing
    public int soulShards = 0;            // ADDED: Currency for the Magic Shop

    // Shop Passives (NEW)
    public boolean hasVitalSurge = false;
    public boolean hasShockBind = false;
    public boolean hasFrostArrow = false;
    public boolean hasArcSurge = false;
    public boolean hasVenomInfusion = false;
    public boolean hasRazorEdge = false;
    public boolean hasFortifiedPlating = false;
    public boolean hasPhoenixSoulstone = false;
    // Status Toggles
    public boolean stunned, frozen, confused, nimble;

    public Combatant(String name, String role, String emoji,
                     int maxHp, int attack, int defense,
                     int energy, int maxEnergy, BattleLogic.Special special) {
        this.name = name; this.role = role; this.emoji = emoji;
        this.maxHp = maxHp; this.currentHp = maxHp;
        this.baseAttack = attack; this.attack = attack;
        this.baseDefense = defense; this.defense = defense;
        this.energy = energy; this.maxEnergy = maxEnergy;
        this.special = special;
    }

    public boolean isAlive() { return currentHp > 0; }

    public int effectiveAttack()  { return Math.max(1, attack  + attackModifier); }
    public int effectiveDefense() { return Math.max(0, defense + defenseModifier); }
}