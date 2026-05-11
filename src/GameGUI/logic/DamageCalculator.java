package GameGUI.logic;

import GameGUI.model.entity.base.Combatant;

/**
 * DamageCalculator — Handles all math for attacks.
 *
 * All HP mutations go through Combatant's public mutators (takeDamage, heal)
 * so clamping is always consistent and never duplicated here.
 * Note: Status/DoT damage is now handled exclusively by the StatusManager.
 */
public class DamageCalculator {

    // =========================================================================
    // DAMAGE FORMULA
    // =========================================================================

    /**
     * Calculates and returns the final damage dealt by {@code attacker} to
     * {@code defender}, applying skill multiplier and flat armor reduction.
     *
     * Does NOT apply the damage — the caller is responsible for calling
     * {@code defender.takeDamage(result)} so the hit is logged correctly.
     *
     * @param attacker        the attacking Combatant
     * @param defender        the defending Combatant
     * @param skillMultiplier damage multiplier from the skill definition
     * @param pierce          if true, armor is ignored completely (True Damage)
     * @return final damage value
     */
    public static int calculateDamage(Combatant attacker, Combatant defender,
                                      double skillMultiplier, boolean pierce) {

        // 1. Calculate base damage from multiplier (which already contains RNG bounds from BattleManager)
        int damage = (int) Math.round(attacker.effectiveAttack() * skillMultiplier);

        // 2. Armor Pierce logic (Text version ignored defense entirely for piercing attacks)
        int defVal = pierce ? 0 : defender.effectiveDefense();

        // 3. Flat defense subtraction
        damage -= defVal;

        // 4. Floor at 0 (Prevents healing the enemy if defense is higher than attack)
        if (damage < 0) {
            damage = 0;
        }

        return damage;
    }
}