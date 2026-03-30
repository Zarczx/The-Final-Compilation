package GameGUI.logic;

import GameGUI.model.Combatant;
import GameGUI.model.HeroData.WeaponDef;

import java.util.Random;

/**
 * DamageCalculator — Handles all math for attacks, defense, and weapon effects.
 */
public class DamageCalculator {
    private static final Random rng = new Random();

    public static int calculateDamage(Combatant attacker, Combatant defender, double skillMultiplier, boolean pierce) {
        double base = attacker.effectiveAttack() * skillMultiplier;
        double defVal = pierce ? defender.effectiveDefense() * 0.15 : defender.effectiveDefense() * 0.5;

        // Add a slight variance (85% to 115% damage)
        double variance = 0.85 + rng.nextDouble() * 0.30;
        double raw = (base - defVal) * variance;

        int damage = (int) Math.round(Math.max(1.0, raw));
        return defender.defending ? Math.max(1, damage / 2) : damage;
    }

    public static String applyWeaponEffects(Combatant hero, Combatant enemy, WeaponDef weaponDef, int damageDealt) {
        if (weaponDef == null || damageDealt <= 0) return null;
        StringBuilder effects = new StringBuilder();

        // Lifesteal
        if (weaponDef.lifestealPercent > 0) {
            int heal = Math.max(1, (int)(damageDealt * weaponDef.lifestealPercent / 100.0));
            heal = Math.min(heal, hero.maxHp - hero.currentHp);
            if (heal > 0) {
                hero.currentHp += heal;
                effects.append("Lifesteal +").append(heal).append("HP ");
            }
        }

        // Energy Restore
        if (weaponDef.energyPerAttack > 0) {
            hero.energy = Math.min(hero.maxEnergy, hero.energy + weaponDef.energyPerAttack);
        }

        // Confuse
        if (weaponDef.confuseChance > 0 && rng.nextInt(100) < weaponDef.confuseChance) {
            enemy.confused = true;
            effects.append("CONFUSE ");
        }

        // Poison
        if (weaponDef.poisonChance > 0 && rng.nextInt(100) < weaponDef.poisonChance) {
            enemy.dotDamage = Math.max(enemy.dotDamage, Math.max(1, (int)(enemy.maxHp * 0.03)));
            enemy.dotTurnsLeft += 2;
            effects.append("POISON");
        }

        return effects.length() > 0 ? effects.toString().trim() : null;
    }

    public static int applyDoT(Combatant c) {
        if (c.dotTurnsLeft <= 0 || c.dotDamage <= 0) return 0;
        int dmg = c.dotDamage;
        c.currentHp = Math.max(0, c.currentHp - dmg);
        c.dotTurnsLeft--;
        return dmg;
    }
}