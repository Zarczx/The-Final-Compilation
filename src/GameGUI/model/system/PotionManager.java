package GameGUI.model.system;

import GameGUI.model.entity.Combatant;
import utils.RandomUtil;

/**
 * Potions — Manages the hero's consumable flask counts and their effects.
 *
 * Responsibilities:
 *   - Track Normal / Crimson / Cerulean flask counts.
 *   - Apply stat effects (heal / energy restore) via the Combatant's
 *     public mutators — never touching fields directly.
 *   - Return a descriptive String from every use() call so the UI
 *     (InventoryDialog) can display feedback without any System.out.
 *   - Handle loot drops and shop/reward acquisition.
 *
 * Does NOT own: equipment, progression, battle state — those live elsewhere.
 */
public class PotionManager {

    private final Combatant owner;

    // Flask counts — starting values match Inventory's original defaults
    private int normalHealingPotions = 3;   // Normal Flask  — restores 15% of max HP
    private int fullHealingPotions   = 1;   // Crimson Flask — restores full HP
    private int energyPotions        = 2;   // Cerulean Flask — restores class-specific energy

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public PotionManager(Combatant owner) {
        this.owner = owner;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public int getNormalHealingPotions() { return normalHealingPotions; }
    public int getFullHealingPotions()   { return fullHealingPotions;   }
    public int getEnergyPotions()        { return energyPotions;        }

    // =========================================================================
    // USE — apply effect, decrement count, return feedback String for the UI
    // =========================================================================

    /**
     * Consumes one Normal Flask.
     * Effect: heals the hero for 15% of their max HP.
     * Original text-game behaviour preserved exactly.
     */
    public String useNormalHealingPotion() {
        if (normalHealingPotions <= 0) return "❌ No Normal Flasks remaining.";
        normalHealingPotions--;

        int oldHp      = owner.currentHp;
        int healAmount = (int) (owner.maxHp * 0.15);
        owner.heal(healAmount);
        int restored   = owner.currentHp - oldHp;

        return String.format(
                "🧪 Flask consumed. Restored %d HP  (%d → %d)",
                restored, oldHp, owner.currentHp);
    }

    /**
     * Consumes one Crimson Flask.
     * Effect: restores the hero to full HP.
     * Original text-game behaviour preserved exactly.
     */
    public String useFullHealingPotion() {
        if (fullHealingPotions <= 0) return "❌ No Crimson Flasks remaining.";
        fullHealingPotions--;

        int oldHp = owner.currentHp;
        owner.heal(owner.maxHp);    // Combatant.heal() clamps at maxHp
        int restored = owner.currentHp - oldHp;

        return String.format(
                "🩸 Crimson Flask consumed. Vitality fully restored  (+%d HP)", restored);
    }

    /**
     * Consumes one Cerulean Flask.
     * Effect: restores energy by the class-specific amount.
     *   Swordsman → +30 Stamina
     *   Archer    → +6  Arrows
     *   Mage      → +40 Mana
     *   (other)   → +25% of maxEnergy as a safe fallback
     * Original text-game switch preserved exactly.
     */
    public String useEnergyPotion() {
        if (energyPotions <= 0) return "❌ No Cerulean Flasks remaining.";
        energyPotions--;

        int restoreAmount = switch (owner.getClassType()) {
            case "Swordsman" -> 30;
            case "Archer"    ->  6;
            case "Mage"      -> 40;
            default          -> (int) (owner.maxEnergy * 0.25);
        };

        int oldEnergy = owner.energy;
        owner.restoreEnergy(restoreAmount);
        int restored = owner.energy - oldEnergy;

        return String.format(
                "✨ Cerulean Flask consumed. Restored %d %s  (%d → %d)",
                restored, owner.getEnergyName(), oldEnergy, owner.energy);
    }

    // =========================================================================
    // LOOT — post-combat drop logic (mirrors original Potions.lootPotions)
    // Returns a summary String for the UI loot panel; empty if nothing dropped.
    // =========================================================================

    /**
     * Rolls for flask drops after an encounter.
     * Mini-boss drops are guaranteed and more generous, matching the original table.
     */
    public String lootPotions(boolean isMiniBoss) {
        int totalDrops = isMiniBoss
                ? RandomUtil.range(4, 8)
                : (RandomUtil.chance(70) ? RandomUtil.range(1, 3) : 0);

        int normCount   = 0;
        int energyCount = 0;

        for (int i = 0; i < totalDrops; i++) {
            if (RandomUtil.chance(50)) { normalHealingPotions++; normCount++;   }
            else                      { energyPotions++;         energyCount++; }
        }

        int fullCount = 0;
        if (isMiniBoss) {
            fullCount = RandomUtil.range(1, 2);
            fullHealingPotions += fullCount;
        }

        StringBuilder sb = new StringBuilder();
        if (normCount   > 0) sb.append("🧪 ").append(normCount).append("x Normal Flask\n");
        if (fullCount   > 0) sb.append("🩸 ").append(fullCount).append("x Crimson Flask\n");
        if (energyCount > 0) sb.append("✨ ").append(energyCount).append("x Cerulean Flask\n");
        return sb.toString().trim();
    }

    // =========================================================================
    // ACQUIRE — shop purchases, chest rewards, quest grants
    // =========================================================================

    public void addNormalHealingPotions(int amount) { normalHealingPotions += amount; }
    public void addFullHealingPotions(int amount)   { fullHealingPotions   += amount; }
    public void addEnergyPotions(int amount)        { energyPotions        += amount; }
}