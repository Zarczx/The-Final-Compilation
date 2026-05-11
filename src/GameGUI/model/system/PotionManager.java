package GameGUI.model.system;

import GameGUI.model.entity.base.Combatant;
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
    private int normalHealingPotions = 0;   // Normal Healing Potion  — restores 15% of max HP
    private int fullHealingPotions   = 0;   // Full Healing Potion — restores full HP
    private int energyPotions        = 0;   // Energy Potion — restores class-specific energy

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
     * Consumes one Normal Potion.
     * Effect: heals the hero for 15% of their max HP.
     * Original text-game behaviour preserved exactly.
     */
    public String useNormalHealingPotion() {
        if (normalHealingPotions <= 0) return "❌ No Healing Potions remaining.";
        normalHealingPotions--;

        int oldHp      = owner.getCurrentHp();
        int healAmount = (int) (owner.getMaxHp() * 0.15);
        owner.heal(healAmount);
        int restored   = owner.getCurrentHp() - oldHp;

        return String.format(
                "🧪 Healing Potion consumed. Restored %d HP  (%d → %d)",
                restored, oldHp, owner.getCurrentHp());
    }

    /**
     * Consumes one Full Healin Potion
     * Effect: restores the hero to full HP.
     * Original text-game behaviour preserved exactly.
     */
    public String useFullHealingPotion() {
        if (fullHealingPotions <= 0) return "❌ No Full Healing Potion remaining.";
        fullHealingPotions--;

        int oldHp = owner.getCurrentHp();
        owner.heal(owner.getMaxHp());    // Combatant.heal() clamps at maxHp
        int restored = owner.getCurrentHp() - oldHp;

        return String.format(
                "🩸 Full Healing Potion consumed. Health fully restored  (+%d HP)", restored);
    }

    /**
     * Consumes one Energy Potion
     * Effect: restores energy by the class-specific amount.
     *   Swordsman → +30 Stamina
     *   Archer    → +6  Arrows
     *   Mage      → +40 Mana
     *   (other)   → +25% of maxEnergy as a safe fallback
     * Original text-game switch preserved exactly.
     */
    public String useEnergyPotion() {
        if (energyPotions <= 0) return "❌ No Energy Potions remaining.";
        energyPotions--;

        int restoreAmount = switch (owner.getClassType()) {
            case "Swordsman" -> 30;
            case "Archer"    ->  6;
            case "Mage"      -> 40;
            default          -> (int) (owner.getMaxEnergy() * 0.25);
        };

        int oldEnergy = owner.getEnergy();
        owner.restoreEnergy(restoreAmount);
        int restored = owner.getEnergy() - oldEnergy;

        return String.format(
                "✨ Energy Potion consumed. Restored %d %s  (%d → %d)",
                restored, owner.getEnergyName(), oldEnergy, owner.getEnergy());
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
        int normCount   = 0;
        int energyCount = 0;
        int fullCount   = 0;

        if (isMiniBoss) {
            // Exactly 4 potions, 50/50 split between normal and energy
            for (int i = 0; i < 4; i++) {
                if (RandomUtil.chance(50)) { normalHealingPotions++; normCount++; }
                else                      { energyPotions++;         energyCount++; }
            }
            // Guaranteed exactly 1 full healing potion
            fullHealingPotions++;
            fullCount = 1;

        } else {
            // Regular enemies: 50% chance to drop exactly 1 potion
            if (RandomUtil.chance(50)) {
                if (RandomUtil.chance(50)) { normalHealingPotions++; normCount++; }
                else                      { energyPotions++;         energyCount++; }
            }
        }

        StringBuilder sb = new StringBuilder();
        if (normCount   > 0) sb.append("🧪 ").append(normCount).append("x Healing Potion\n");
        if (fullCount   > 0) sb.append("🩸 ").append(fullCount).append("x Full Healing Potion\n");
        if (energyCount > 0) sb.append("✨ ").append(energyCount).append("x Energy Potion\n");
        return sb.toString().trim();
    }

    // =========================================================================
    // ACQUIRE — shop purchases, chest rewards, quest grants
    // =========================================================================

    public void addNormalHealingPotions(int amount) { normalHealingPotions += amount; }
    public void addFullHealingPotions(int amount)   { fullHealingPotions   += amount; }
    public void addEnergyPotions(int amount)        { energyPotions        += amount; }
}