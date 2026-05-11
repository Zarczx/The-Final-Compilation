package GameGUI.logic;

import GameGUI.model.entity.Combatant;
import java.util.List;
import java.util.Random;

/**
 * FinalBossManager — Handles the unique mechanics for Khai the Necromancer.
 * Intercepts damage to process shield logic and executes his custom AI.
 */
public class FinalBossManager {

    private final Combatant boss;
    private final Combatant hero;
    private final Random rng = new Random();

    // ── Boss State ────────────────────────────────────────────────────────
    public int activeShield = 0;
    public int nullEnergyStacks = 0;
    public int voidEnergyStacks = 0;

    private boolean encapsulated = false;
    private boolean shieldBroken = false;

    public FinalBossManager(Combatant boss, Combatant hero) {
        this.boss = boss;
        this.hero = hero;
    }

    // =========================================================================
    // DAMAGE INTERCEPTION & SHIELD LOGIC
    // =========================================================================

    /**
     * Called by BattleManager when the player deals damage.
     * Processes the shield before applying remaining damage to HP.
     */
    public void processIncomingDamage(int damage, List<String> logs) {
        if (damage <= 0) return;

        if (activeShield > 0) {
            if (damage >= activeShield) {
                int leftover = damage - activeShield;
                activeShield = 0;
                shieldBroken = true;
                logs.add("💥 Khai's dark barrier shatters!");

                checkBrokenShield(logs);

                if (leftover > 0) {
                    boss.takeDamage(leftover);
                    logs.add("💔 Khai takes " + leftover + " overflow damage!");
                }
            } else {
                activeShield -= damage;
                logs.add("🛡️ Khai's barrier absorbs the hit! (" + activeShield + " Shield remaining)");
            }
        } else {
            boss.takeDamage(damage);
        }
    }

    private void checkBrokenShield(List<String> logs) {
        if (encapsulated && shieldBroken) {
            voidEnergyStacks++;
            boss.setBaseDefense((int) (boss.getBaseDefense() * 1.05));
            boss.recalculateBuffs(); // Refresh effective stats
            logs.add("🕳️ Khai absorbs the shattered fragments! +1 Void Energy (+5% Permanent DEF)");
            encapsulated = false; // Prevent multiple procs
        }
    }

    public void checkUnbrokenShield(List<String> logs) {
        if (encapsulated && !shieldBroken) {
            nullEnergyStacks++;
            boss.setBaseAttack((int) (boss.getBaseAttack() * 1.05));
            boss.recalculateBuffs();
            logs.add("🔮 The barrier completes its cycle! +1 Null Energy (+5% Permanent ATK)");
        }

        // Reset shield state at the start of his turn
        encapsulated = false;
        shieldBroken = false;
        activeShield = 0;
    }

    // =========================================================================
    // BOSS AI & SKILLS
    // =========================================================================

    public String determineNextSkill() {
        double hpPercent = (double) boss.getCurrentHp() / boss.getMaxHp();

        if (hpPercent > 0.80) {
            return (rng.nextDouble() < 0.80) ? "Encapsulation" : "Soul Drain";
        } else if (hpPercent > 0.30) {
            int roll = rng.nextInt(100);
            if (roll < 20) return "Soul Drain";
            else if (roll < 50) return "Encapsulation";
            else return "Dark Ascension";
        } else {
            int roll = rng.nextInt(100);
            if (roll < 10) return "Encapsulation";
            else if (roll < 40) return "Dark Ascension";
            else return "Soul Drain";
        }
    }

    public void executeEncapsulation(List<String> logs) {
        if (!encapsulated) {
            activeShield = 50;
            encapsulated = true;
            shieldBroken = false;
            logs.add("🧿 A dark barrier forms around Khai! (+50 Shield for 1 turn)");
        }
    }

    public void executeSoulDrain(int damageDealt, List<String> logs) {
        boss.heal(damageDealt);
        logs.add("🩸 Khai drains your essence, healing for " + damageDealt + " HP!");
    }

    public void executeDarkAscension(List<String> logs) {
        if (rng.nextDouble() < 0.30) {
            hero.getStatusManager().applyWeaken((int)(hero.effectiveAttack() * 0.30), 2, logs);
            logs.add("😱 You are paralyzed by Fear! (ATK Decreased)");
        }
    }
}