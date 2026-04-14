package GameGUI.model.system;

import GameGUI.model.entity.Combatant;
import GameGUI.model.equipment.Armor;
import GameGUI.model.equipment.Weapon;

/**
 * Inventory — Manages the hero's equipped gear and consumable flasks.
 *
 * Responsibilities:
 *   - Hold the currently equipped Weapon and Armor.
 *   - Trigger stat recalculation on Combatant when gear changes.
 *   - Own a Potions instance and expose its API so callers only need
 *     one reference (hero.inventory) for both gear and flask operations.
 *
 * Does NOT own: HP/energy values, battle modifiers, progression — those
 * live on Combatant. Does NOT implement potion effects directly — that
 * logic lives in Potions.
 */
public class InventoryManager {

    private final Combatant owner;

    // ── Gear slots ────────────────────────────────────────────────────────────
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    // ── Consumables ───────────────────────────────────────────────────────────
    public final PotionManager potions;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public InventoryManager(Combatant owner) {
        this.owner   = owner;
        this.potions = new PotionManager(owner);
    }

    // =========================================================================
    // GEAR — Weapon
    // =========================================================================

    public Weapon getEquippedWeapon() { return equippedWeapon; }

    /**
     * Equips a weapon and immediately recalculates the hero's attack stat.
     * Pass {@code null} to unequip.
     */
    public void setEquippedWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        owner.recalculateBuffs();
    }

    // =========================================================================
    // GEAR — Armor
    // =========================================================================

    public Armor getEquippedArmor() { return equippedArmor; }

    /**
     * Equips an armor piece, swapping out the old one cleanly.
     * Strips the previous armor's HP buff before applying the new one
     * so maxHp never stacks incorrectly across gear swaps.
     * Pass {@code null} to unequip.
     */
    public void setEquippedArmor(Armor armor) {
        // Remove old armor's HP contribution
        if (this.equippedArmor != null) {
            owner.maxHp    -= this.equippedArmor.hpBuff;
            owner.currentHp = Math.min(owner.currentHp, owner.maxHp);
        }

        this.equippedArmor = armor;

        // Apply new armor's HP contribution
        if (armor != null) {
            owner.maxHp += armor.hpBuff;
        }

        owner.recalculateBuffs();
    }

    // =========================================================================
    // FLASK PASS-THROUGHS
    // Expose Potions API directly so callers don't need a second reference.
    // All logic and return values come from Potions — nothing is duplicated here.
    // =========================================================================

    public int getNormalHealingPotions()  { return potions.getNormalHealingPotions(); }
    public int getFullHealingPotions()    { return potions.getFullHealingPotions();   }
    public int getEnergyPotions()         { return potions.getEnergyPotions();        }

    public String useNormalHealingPotion() { return potions.useNormalHealingPotion(); }
    public String useFullHealingPotion()   { return potions.useFullHealingPotion();   }
    public String useEnergyPotion()        { return potions.useEnergyPotion();        }

    public void addNormalHealingPotions(int amount) { potions.addNormalHealingPotions(amount); }
    public void addFullHealingPotions(int amount)   { potions.addFullHealingPotions(amount);   }
    public void addEnergyPotions(int amount)        { potions.addEnergyPotions(amount);        }

    public String lootPotions(boolean isMiniBoss)   { return potions.lootPotions(isMiniBoss);  }
}