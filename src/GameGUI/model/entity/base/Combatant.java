package GameGUI.model.entity.base;

import java.util.EnumSet;
import java.util.Set;

import GameGUI.model.entity.ShopPassive;
import GameGUI.model.system.InventoryManager;
import GameGUI.model.logic.StatusManager;


/**
 * Combatant — The live state of a hero during a run.
 */
public class Combatant {

    public final String name, role, emoji;

    // ── Core Stats ────────────────────────────────────────────────────────────
    private int maxHp;
    private int currentHp;
    private int baseAttack;
    public int attack;
    private int baseDefense;
    public int defense;
    private int maxEnergy;
    private int energy;

    // ── Battle State ──────────────────────────────────────────────────────────
    private int specialCooldown;

    // ── Progression & Currency ────────────────────────────────────────────────
    private int level        = 1;
    private int exp          = 0;
    private int nextLevelExp = 100;
    private int soulShards   = 0;
    private String lastLevelUpData = null;

    // Store passives dynamically
    private final Set<ShopPassive> activePassives = EnumSet.noneOf(ShopPassive.class);

    public void addPassive(ShopPassive passive) {
        activePassives.add(passive);
    }

    public boolean hasPassive(ShopPassive passive) {
        return activePassives.contains(passive);
    }

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

        this.setMaxHp(maxHp);
        this.setCurrentHp(maxHp);

        this.setBaseAttack(attack);   this.attack  = attack;
        this.setBaseDefense(defense);  this.defense = defense;

        this.setEnergy(energy);
        this.setMaxEnergy(maxEnergy);

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
        setCurrentHp(Math.min(getCurrentHp() + amount, getMaxHp()));
    }

    public void takeDamage(int amount) {
        setCurrentHp(Math.max(0, getCurrentHp() - amount));
    }

    public void restoreEnergy(int amount) {
        setEnergy(Math.min(getEnergy() + amount, getMaxEnergy()));
    }

    public boolean spendEnergy(int amount) {
        if (getEnergy() < amount) return false;
        setEnergy(getEnergy() - amount);
        return true;
    }

    // =========================================================================
    // COMBAT HELPERS
    // =========================================================================

    public boolean isAlive()      { return getCurrentHp() > 0; }

    public int effectiveAttack()  { return Math.max(1, attack); }
    public int effectiveDefense() { return Math.max(0, defense); }

    /**
     * Resets all transient battle state so this Combatant is ready for a new fight.
     */
    public void resetForNewBattle() {
        setCurrentHp(getMaxHp());
        setEnergy(getMaxEnergy());
        setSpecialCooldown(0);

        // Tell the StatusManager to wipe all DoTs, CC, and Buffs!
        statusManager.resetAllEffects();
    }

    public void recalculateBuffs() {
        int weaponAtk = (inventory.getEquippedWeapon() != null)
                ? inventory.getEquippedWeapon().getAtkBuff() : 0;
        int armorDef  = (inventory.getEquippedArmor() != null)
                ? inventory.getEquippedArmor().getDefBuff()  : 0;

        this.attack  = this.getBaseAttack() + weaponAtk;
        this.defense = this.getBaseDefense() + armorDef;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public void setBaseAttack(int baseAttack) {
        this.baseAttack = baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public void setBaseDefense(int baseDefense) {
        this.baseDefense = baseDefense;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getSpecialCooldown() {
        return specialCooldown;
    }

    public void setSpecialCooldown(int specialCooldown) {
        this.specialCooldown = specialCooldown;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getNextLevelExp() {
        return nextLevelExp;
    }

    public void setNextLevelExp(int nextLevelExp) {
        this.nextLevelExp = nextLevelExp;
    }

    public int getSoulShards() {
        return soulShards;
    }

    public void setSoulShards(int soulShards) {
        this.soulShards = soulShards;
    }

    public String getLastLevelUpData() {
        return lastLevelUpData;
    }

    public void setLastLevelUpData(String lastLevelUpData) {
        this.lastLevelUpData = lastLevelUpData;
    }
}