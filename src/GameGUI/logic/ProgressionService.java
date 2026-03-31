package GameGUI.logic;

import GameGUI.model.Combatant;

/**
 * ProgressionService — Handles all math related to XP, Leveling, and Stat Growth.
 * This removes the math from the UI and Battle Logic.
 */
public class ProgressionService {

    private static final int[] XP_TABLE = {
            0, 100, 115, 130, 150, 175, 200, 230, 265, 305, 355,
            405, 465, 535, 615, 720, 875, 1040, 1350, 1650, 2200,
            2500, 2900, 3350, 3800, 4400, 5000, 5800, 6600, 7400
    };

    /**
     * Adds XP to the combatant and triggers level-ups if necessary.
     * Returns true if the character leveled up during this transaction.
     */
    public static boolean gainExp(Combatant hero, int amount, int currentWorldLevel) {
        if (hero.level >= 30) return false;

        hero.exp += amount;
        boolean leveledUp = false;

        while (hero.level < 30 && hero.level < XP_TABLE.length - 1 && hero.exp >= hero.nextLevelExp) {
            applyLevelUp(hero, currentWorldLevel);
            leveledUp = true;
        }

        return leveledUp;
    }

    /**
     * Applies the permanent stat increases based on Role and World Level.
     */
    private static void applyLevelUp(Combatant hero, int worldLevel) {
        if (hero.level >= 30) return;

        hero.level++;

        int oldMaxHp = hero.maxHp;
        int oldAtk   = hero.baseAttack;
        int oldDef   = hero.baseDefense;

        // Apply class-specific growth formulas
        switch (hero.role) {
            case "Swordsman" -> {
                hero.maxHp += 60 + (worldLevel * 5);
                hero.baseAttack += 7 + worldLevel;
                hero.baseDefense += 3 + worldLevel;
            }
            case "Archer" -> {
                hero.maxHp += 56 + (worldLevel * 5);
                hero.baseAttack += 8 + worldLevel;
                hero.baseDefense += 2 + worldLevel;
            }
            case "Mage" -> {
                hero.maxHp += 50 + (worldLevel * 5);
                hero.baseAttack += 9 + worldLevel;
                hero.baseDefense += 1 + worldLevel;
            }
            default -> {
                hero.maxHp += 55 + (worldLevel * 5);
                hero.baseAttack += 7 + worldLevel;
                hero.baseDefense += 2 + worldLevel;
            }
        }

        // Keep the difference between Base and Total stats intact
        int weaponBonus = hero.attack - oldAtk - hero.attackModifier;
        int armorBonus  = hero.defense - oldDef - hero.defenseModifier;

        hero.attack  = hero.baseAttack + weaponBonus + hero.attackModifier;
        hero.defense = hero.baseDefense + armorBonus + hero.defenseModifier;

        // Restore 50% HP and Energy
        hero.currentHp = Math.min(hero.maxHp, hero.currentHp + (int)(hero.maxHp * 0.50));
        hero.energy    = Math.min(hero.maxEnergy, hero.energy + (int)(hero.maxEnergy * 0.50));

        // Deduct XP requirement for the next level
        hero.exp -= hero.nextLevelExp;
        if (hero.level < XP_TABLE.length) {
            hero.nextLevelExp = XP_TABLE[hero.level];
        }

        // Store the exact stat gains so the UI can read them later to show the Level Up screen
        int hpGain  = hero.maxHp - oldMaxHp;
        int atkGain = hero.baseAttack - oldAtk;
        int defGain = hero.baseDefense - oldDef;

        hero.lastLevelUpData = String.format(
                "LVL_UP|%d|%d|%d|%d|%d|%d|%d|%d",
                hero.level, hpGain, hero.maxHp, atkGain, hero.attack, defGain, hero.defense, hero.maxEnergy
        );
    }
}