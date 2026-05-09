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

        int oldHp      = owner.currentHp;
        int healAmount = (int) (owner.maxHp * 0.15);
        owner.heal(healAmount);
        int restored   = owner.currentHp - oldHp;

        return String.format(
                "🧪 Healing Potion consumed. Restored %d HP  (%d → %d)",
                restored, oldHp, owner.currentHp);
    }

    /**
     * Consumes one Full Healin Potion
     * Effect: restores the hero to full HP.
     * Original text-game behaviour preserved exactly.
     */
    public String useFullHealingPotion() {
        if (fullHealingPotions <= 0) return "❌ No Full Healing Potion remaining.";
        fullHealingPotions--;

        int oldHp = owner.currentHp;
        owner.heal(owner.maxHp);    // Combatant.heal() clamps at maxHp
        int restored = owner.currentHp - oldHp;

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
            default          -> (int) (owner.maxEnergy * 0.25);
        };

        int oldEnergy = owner.energy;
        owner.restoreEnergy(restoreAmount);
        int restored = owner.energy - oldEnergy;

        return String.format(
                "✨ Energy Potion consumed. Restored %d %s  (%d → %d)",
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
        int totalDrops;
        if (isMiniBoss) {
            totalDrops = RandomUtil.range(4, 8);
        } else {
            totalDrops = RandomUtil.chance(50) ? 1 : 0;
        }

        int normCount   = 0;
        int energyCount = 0;

        for (int i = 0; i < totalDrops; i++) {
            if (RandomUtil.chance(50)) { normalHealingPotions++; normCount++;   }
            else                      { energyPotions++;         energyCount++; }
        }

        int fullCount = 0;
        if (isMiniBoss) {
            // Was: range(1,2) — now a 40% chance for exactly 1
            if (RandomUtil.chance(40)) {
                fullHealingPotions++;
                fullCount = 1;
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