package GameGUI.logic;

import GameGUI.model.entity.Combatant;

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
        if (hero.getLevel() >= 30) return false;

        hero.setExp(hero.getExp() + amount);
        boolean leveledUp = false;

        while (hero.getLevel() < 30 && hero.getLevel() < XP_TABLE.length - 1 && hero.getExp() >= hero.getNextLevelExp()) {
            applyLevelUp(hero, currentWorldLevel);
            leveledUp = true;
        }

        return leveledUp;
    }

    /**
     * Applies the permanent stat increases based on Role and World Level.
     */
    /**
     * Applies the permanent stat increases based on Role and World Level.
     */
    private static void applyLevelUp(Combatant hero, int worldLevel) {
        if (hero.getLevel() >= 30) return;

        hero.setLevel(hero.getLevel() + 1);

        int oldMaxHp = hero.getMaxHp();
        int oldAtk   = hero.getBaseAttack();
        int oldDef   = hero.getBaseDefense();

        // Apply class-specific growth formulas to BASE stats
        switch (hero.role) {
            case "Swordsman" -> {
                hero.setMaxHp(hero.getMaxHp() + 60 + (worldLevel * 5));
                hero.setBaseAttack(hero.getBaseAttack() + 7 + worldLevel);
                hero.setBaseDefense(hero.getBaseDefense() + 3 + worldLevel);
            }
            case "Archer" -> {
                hero.setMaxHp(hero.getMaxHp() + 56 + (worldLevel * 5));
                hero.setBaseAttack(hero.getBaseAttack() + 8 + worldLevel);
                hero.setBaseDefense(hero.getBaseDefense() + 2 + worldLevel);
            }
            case "Mage" -> {
                hero.setMaxHp(hero.getMaxHp() + 50 + (worldLevel * 5));
                hero.setBaseAttack(hero.getBaseAttack() + 9 + worldLevel);
                hero.setBaseDefense(hero.getBaseDefense() + 1 + worldLevel);
            }
            default -> {
                hero.setMaxHp(hero.getMaxHp() + 55 + (worldLevel * 5));
                hero.setBaseAttack(hero.getBaseAttack() + 7 + worldLevel);
                hero.setBaseDefense(hero.getBaseDefense() + 2 + worldLevel);
            }
        }

        // ★ THE FIX: Simply call recalculateBuffs!
        // This will automatically add the weapon and armor stats to the newly increased base stats.
        hero.recalculateBuffs();

        // Restore 50% HP and Energy
        hero.heal((int)(hero.getMaxHp() * 0.50)); // Use the safe mutator!
        hero.restoreEnergy((int)(hero.getMaxEnergy() * 0.50));

        // Deduct XP requirement for the next level
        hero.setExp(hero.getExp() - hero.getNextLevelExp());
        if (hero.getLevel() < XP_TABLE.length) {
            hero.setNextLevelExp(XP_TABLE[hero.getLevel()]);
        }

        // Store the exact stat gains so the UI can read them later to show the Level Up screen
        int hpGain  = hero.getMaxHp() - oldMaxHp;
        int atkGain = hero.getBaseAttack() - oldAtk;
        int defGain = hero.getBaseDefense() - oldDef;

        hero.setLastLevelUpData(String.format(
                "LVL_UP|%d|%d|%d|%d|%d|%d|%d|%d",
                hero.getLevel(), hpGain, hero.getMaxHp(), atkGain, hero.attack, defGain, hero.defense, hero.getMaxEnergy()
        ));
    }
}