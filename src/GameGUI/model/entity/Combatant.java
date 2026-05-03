package GameGUI.model.entity;

import GameGUI.model.system.InventoryManager;
import GameGUI.model.logic.StatusManager;

/**
 * Combatant — The live state of a hero during a run.
 */
public class Combatant {

    public final String name, role, emoji;

    // ── Core Stats ────────────────────────────────────────────────────────────
    public int maxHp, currentHp;
    public int baseAttack,  attack;
    public int baseDefense, defense;
    public int maxEnergy,   energy;

    // ── Battle State ──────────────────────────────────────────────────────────
    public boolean defending;
    public int specialCooldown;

    // REMOVED: dotDamage, dotTurnsLeft, attackModifier, defenseModifier
    // REMOVED: stunned, frozen, confused, nimble
    // (These are now entirely handled by StatusManager!)

    // ── Progression & Currency ────────────────────────────────────────────────
    public int level        = 1;
    public int exp          = 0;
    public int nextLevelExp = 100;
    public int soulShards   = 0;
    public String lastLevelUpData = null;

    // ── Shop Passives ─────────────────────────────────────────────────────────
    public boolean hasVitalSurge       = false;
    public boolean hasShockBind        = false;
    public boolean hasFrostArrow       = false;
    public boolean hasArcSurge         = false;
    public boolean hasVenomInfusion    = false;
    public boolean hasRazorEdge        = false;
    public boolean hasFortifiedPlating = false;
    public boolean hasPhoenixSoulstone = false;

    // ── Inventory & Status ────────────────────────────────────────────────────
    public final InventoryManager inventory;
    private final StatusManager statusManager;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public Combatant(String name, String role, String emoji,
                     int maxHp, int attack, int defense,
                     int energy, int maxEnergy) {
        this.name = name; this.role = role; this.emoji = emoji;

        this.maxHp     = maxHp;
        this.currentHp = maxHp;

        this.baseAttack  = attack;   this.attack  = attack;
        this.baseDefense = defense;  this.defense = defense;

        this.energy    = energy;
        this.maxEnergy = maxEnergy;

        this.inventory = new InventoryManager(this);
        this.statusManager = new StatusManager(this);
    }

    // =========================================================================
    // IDENTITY HELPERS
    // =========================================================================

    public String getClassType() { return role; }

    public String getEnergyName() {
        return switch (role) {
            case "Swordsman" -> "Stamina";
            case "Archer"    -> "Arrow";
            case "Mage"      -> "Mana";
            default          -> "Energy";
        };
    }

    public String getEnergyEmoji() {
        return switch (role) {
            case "Swordsman" -> "⚡";
            case "Archer"    -> "🏹";
            case "Mage"      -> "🔮";
            default          -> "⚡";
        };
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    // =========================================================================
    // STAT MUTATORS
    // =========================================================================

    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    public void takeDamage(int amount) {
        currentHp = Math.max(0, currentHp - amount);
    }

    public void restoreEnergy(int amount) {
        energy = Math.min(energy + amount, maxEnergy);
    }

    public boolean spendEnergy(int amount) {
        if (energy < amount) return false;
        energy -= amount;
        return true;
    }

    // =========================================================================
    // COMBAT HELPERS
    // =========================================================================

    public boolean isAlive()      { return currentHp > 0; }

    // Because StatusManager directly modifies this.attack and this.defense,
    // we no longer need to add "modifiers" here. Just return the stat!
    public int effectiveAttack()  { return Math.max(1, attack); }
    public int effectiveDefense() { return Math.max(0, defense); }

    /**
     * Resets all transient battle state so this Combatant is ready for a new fight.
     */
    public void resetForNewBattle() {
        currentHp       = maxHp;
        energy          = maxEnergy;
        defending       = false;
        specialCooldown = 0;

        // Tell the StatusManager to wipe all DoTs, CC, and Buffs!
        statusManager.resetAllEffects();
    }

    public void recalculateBuffs() {
        int weaponAtk = (inventory.getEquippedWeapon() != null)
                ? inventory.getEquippedWeapon().getAtkBuff() : 0;
        int armorDef  = (inventory.getEquippedArmor() != null)
                ? inventory.getEquippedArmor().getDefBuff()  : 0;

        this.attack  = this.baseAttack  + weaponAtk;
        this.defense = this.baseDefense + armorDef;
    }
}