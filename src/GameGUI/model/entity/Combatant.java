package GameGUI.model.entity;

import GameGUI.model.system.InventoryManager;

/**
 * Combatant — The live state of a hero during a run.
 *
 * Responsibilities (this file only):
 *   - Hero identity: name, role, emoji.
 *   - Current and base stats: HP, attack, defense, energy.
 *   - Battle-state flags: defending, stunned, modifiers, DoT.
 *   - Progression and currency: level, exp, soul shards.
 *   - Shop passives (boolean flags).
 *   - Stat mutators: heal(), takeDamage(), restoreEnergy(), spendEnergy().
 *   - Stat recalculation from gear (recalculateBuffs).
 *   - Identity helpers: getClassType(), getEnergyName(), getEnergyEmoji().
 *
 * Does NOT own: gear slots, flask counts, loot logic — those live in
 * Inventory and Potions respectively.
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
    public int attackModifier, defenseModifier;
    public int dotDamage, dotTurnsLeft;

    // ── Status Effects ────────────────────────────────────────────────────────
    public boolean stunned, frozen, confused, nimble;

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

    // ── Inventory (gear + flasks) ─────────────────────────────────────────────
    public final InventoryManager inventory;

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
    }

    // =========================================================================
    // IDENTITY HELPERS
    // =========================================================================

    /** Class label used by Potions for the energy-restore switch. */
    public String getClassType() { return role; }

    /** Human-readable energy resource name shown in flask feedback and UI labels. */
    public String getEnergyName() {
        return switch (role) {
            case "Swordsman" -> "Stamina";
            case "Archer"    -> "Arrow";
            case "Mage"      -> "Mana";
            default          -> "Energy";
        };
    }

    /** Energy resource emoji, useful for dynamic UI labels. */
    public String getEnergyEmoji() {
        return switch (role) {
            case "Swordsman" -> "⚡";
            case "Archer"    -> "🏹";
            case "Mage"      -> "🔮";
            default          -> "⚡";
        };
    }

    // =========================================================================
    // STAT MUTATORS
    // All HP and energy changes route through here for consistent clamping.
    // Never set currentHp or energy directly from outside this class.
    // =========================================================================

    /** Adds {@code amount} HP, clamped to maxHp. */
    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    /** Subtracts {@code amount} HP, clamped to 0. */
    public void takeDamage(int amount) {
        currentHp = Math.max(0, currentHp - amount);
    }

    /** Adds {@code amount} energy, clamped to maxEnergy. */
    public void restoreEnergy(int amount) {
        energy = Math.min(energy + amount, maxEnergy);
    }

    /**
     * Spends {@code amount} energy.
     * @return true if the cost was paid; false if the hero couldn't afford it.
     */
    public boolean spendEnergy(int amount) {
        if (energy < amount) return false;
        energy -= amount;
        return true;
    }

    // =========================================================================
    // COMBAT HELPERS
    // =========================================================================

    public boolean isAlive()      { return currentHp > 0; }
    public int effectiveAttack()  { return Math.max(1, attack  + attackModifier); }
    public int effectiveDefense() { return Math.max(0, defense + defenseModifier); }

    /**
     * Resets all transient battle state so this Combatant is ready for a new fight.
     * Call this on both hero and enemy at the start of every BattleManager session.
     * Does NOT reset progression, inventory, or soul shards.
     */
    public void resetForNewBattle() {
        currentHp       = maxHp;
        energy          = maxEnergy;
        defending       = false;
        stunned         = false;
        frozen          = false;
        confused        = false;
        nimble          = false;
        specialCooldown = 0;
        attackModifier  = 0;
        defenseModifier = 0;
        dotDamage       = 0;
        dotTurnsLeft    = 0;
    }

    /**
     * Recomputes attack and defense from base stats + equipped gear.
     * Called automatically by Inventory whenever a weapon or armor changes.
     * HP buff from armor is managed separately in Inventory.setEquippedArmor().
     */
    public void recalculateBuffs() {
        int weaponAtk = (inventory.getEquippedWeapon() != null)
                ? inventory.getEquippedWeapon().getAtkBuff() : 0;
        int armorDef  = (inventory.getEquippedArmor() != null)
                ? inventory.getEquippedArmor().getDefBuff()  : 0;

        this.attack  = this.baseAttack  + weaponAtk;
        this.defense = this.baseDefense + armorDef;
    }
}