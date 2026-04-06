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

    public static String applyWeaponEffects(Combatant hero, Combatant enemy, Combatant.Weapon weapon, int baseDamage) {
        if (weapon == null) return null;

        java.util.Random rng = new java.util.Random();
        StringBuilder fxLog = new StringBuilder();

        // 💖 Lifesteal
        if (weapon.lifestealPercent > 0) {
            int heal = (int) (baseDamage * (weapon.lifestealPercent / 100.0));
            hero.currentHp = Math.min(hero.maxHp, hero.currentHp + heal);
            fxLog.append("Lifesteal +").append(heal).append("HP. ");
        }

        // ⚡ Arc Surge / Energy Restore
        if (weapon.energyPerAttack > 0) {
            hero.energy = Math.min(hero.maxEnergy, hero.energy + weapon.energyPerAttack);
            fxLog.append("Restored ").append(weapon.energyPerAttack).append(" Energy. ");
        }

        // ☠️ Poison & 🩸 Bleed
        if (weapon.poisonChance > 0 && rng.nextInt(100) < weapon.poisonChance) {
            // Apply your poison logic here (e.g. enemy.dotDamage += ...)
            fxLog.append("Poisoned! ");
        }
        if (weapon.bleedChance > 0 && rng.nextInt(100) < weapon.bleedChance) {
            fxLog.append("Bleeding! ");
        }

        // ⛓️ Crowd Control (Stun, Freeze, Confuse)
        if (weapon.stunChance > 0 && rng.nextInt(100) < weapon.stunChance) {
            enemy.stunned = true;
            fxLog.append("Stunned! ");
        }
        if (weapon.freezeChance > 0 && rng.nextInt(100) < weapon.freezeChance) {
            enemy.frozen = true;
            fxLog.append("Frozen! ");
        }
        if (weapon.confuseChance > 0 && rng.nextInt(100) < weapon.confuseChance) {
            enemy.confused = true;
            fxLog.append("Confused! ");
        }

        // ⚡ Extra Hit
        if (weapon.extraHitChance > 0 && rng.nextInt(100) < weapon.extraHitChance) {
            int extraDmg = (int) (baseDamage * (0.2 + (0.2 * rng.nextDouble()))); // 20% to 40% of base
            enemy.currentHp = Math.max(0, enemy.currentHp - extraDmg);
            fxLog.append("Extra Hit (").append(extraDmg).append(" DMG)! ");
        }

        return fxLog.length() > 0 ? fxLog.toString().trim() : null;
    }

    public static int applyDoT(Combatant c) {
        if (c.dotTurnsLeft <= 0 || c.dotDamage <= 0) return 0;
        int dmg = c.dotDamage;
        c.currentHp = Math.max(0, c.currentHp - dmg);
        c.dotTurnsLeft--;
        return dmg;
    }
}