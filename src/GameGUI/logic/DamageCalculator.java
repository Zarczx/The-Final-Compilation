package GameGUI.logic;

import GameGUI.model.entity.Combatant;
import java.util.Random;

/**
 * DamageCalculator — Handles all math for attacks.
 *
 * All HP mutations go through Combatant's public mutators (takeDamage, heal)
 * so clamping is always consistent and never duplicated here.
 * * Note: Status/DoT damage is now handled exclusively by the StatusManager.
 */
public class DamageCalculator {

    private static final Random rng = new Random();

    // =========================================================================
    // DAMAGE FORMULA
    // =========================================================================

    /**
     * Calculates and returns the final damage dealt by {@code attacker} to
     * {@code defender}, applying skill multiplier, armor reduction, variance,
     * and the defender's block bonus if they are currently defending.
     *
     * Does NOT apply the damage — the caller is responsible for calling
     * {@code defender.takeDamage(result)} so the hit is logged correctly.
     *
     * @param attacker        the attacking Combatant
     * @param defender        the defending Combatant
     * @param skillMultiplier damage multiplier from the skill definition
     * @param pierce          if true, armor is reduced to 15% effectiveness
     * @return final damage value (minimum 1)
     */
    public static int calculateDamage(Combatant attacker, Combatant defender,
                                      double skillMultiplier, boolean pierce) {

        // effectiveAttack() and effectiveDefense() now pull the exact stats
        // updated by StatusManager and equipment!
        double base   = attacker.effectiveAttack() * skillMultiplier;
        double defVal = pierce
                ? defender.effectiveDefense() * 0.15
                : defender.effectiveDefense() * 0.50;

        // ±15% variance to keep combat feeling alive
        double variance = 0.85 + rng.nextDouble() * 0.30;
        double raw      = (base - defVal) * variance;

        int damage = (int) Math.round(Math.max(1.0, raw));

        // Defender halves damage while in guard stance
        return defender.defending ? Math.max(1, damage / 2) : damage;
    }
}