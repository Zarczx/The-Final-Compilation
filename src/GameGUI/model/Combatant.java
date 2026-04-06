package GameGUI.model;

import GameGUI.logic.BattleLogic;

/**
 * Combatant — The "Live" state of a character.
 * Stores mutable stats, status effects, and progression data.
 */
public class Combatant {
    public final String name, role, emoji;
    public final BattleLogic.Special special;

    // Core Stats
    public int maxHp, attack, defense, maxEnergy;
    public int baseAttack, baseDefense;
    public int currentHp, energy;

    // Battle State
    public boolean defending;
    public int specialCooldown;
    public int attackModifier, defenseModifier;
    public int dotDamage, dotTurnsLeft;

    // Progression & Currency
    public int level = 1;
    public int exp = 0;
    public int nextLevelExp = 100;
    public String lastLevelUpData = null; // Raw data string for UI parsing
    public int soulShards = 0;

    // Shop Passives
    public boolean hasVitalSurge = false;
    public boolean hasShockBind = false;
    public boolean hasFrostArrow = false;
    public boolean hasArcSurge = false;
    public boolean hasVenomInfusion = false;
    public boolean hasRazorEdge = false;
    public boolean hasFortifiedPlating = false;
    public boolean hasPhoenixSoulstone = false;

    // Status Toggles
    public boolean stunned, frozen, confused, nimble;

    // ★ NEW: The GUI-adapted Inventory System
    public final Inventory inventory;

    public Combatant(String name, String role, String emoji,
                     int maxHp, int attack, int defense,
                     int energy, int maxEnergy, BattleLogic.Special special) {
        this.name = name; this.role = role; this.emoji = emoji;
        this.maxHp = maxHp; this.currentHp = maxHp;
        this.baseAttack = attack; this.attack = attack;
        this.baseDefense = defense; this.defense = defense;
        this.energy = energy; this.maxEnergy = maxEnergy;
        this.special = special;

        // Initialize the inventory for this combatant
        this.inventory = new Inventory();
    }

    public boolean isAlive() { return currentHp > 0; }

    public int effectiveAttack()  { return Math.max(1, attack  + attackModifier); }
    public int effectiveDefense() { return Math.max(0, defense + defenseModifier); }

    // ★ NEW: Recalculates stats based on equipped gear (Migrated from Character.java)
    public void recalculateBuffs() {
        int weaponAtkBuff = (inventory.getEquippedWeapon() != null) ? inventory.getEquippedWeapon().getAtkBuff() : 0;
        int armorDefBuff = (inventory.getEquippedArmor() != null) ? inventory.getEquippedArmor().getDefBuff() : 0;

        this.attack = this.baseAttack + weaponAtkBuff;
        this.defense = this.baseDefense + armorDefBuff;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ INVENTORY DATA STRUCTURE
    // ════════════════════════════════════════════════════════════════════════
    public class Inventory {
        private Weapon equippedWeapon;
        private Armor equippedArmor;

        // Potion counts (Migrated from Potions.java)
        private int normalHealingPotions = 3; // Starting amounts
        private int fullHealingPotions = 1;
        private int energyPotions = 2;

        public Inventory() {}

        // --- Gear Getters & Setters ---
        public Weapon getEquippedWeapon() { return equippedWeapon; }
        public void setEquippedWeapon(Weapon weapon) {
            this.equippedWeapon = weapon;
            recalculateBuffs();
        }

        public Armor getEquippedArmor() { return equippedArmor; }
        public void setEquippedArmor(Armor armor) {
            this.equippedArmor = armor;
            recalculateBuffs();
        }

        // --- Potion Management ---
        public int getNormalHealingPotions() { return normalHealingPotions; }
        public void addNormalHealingPotions(int amount) { this.normalHealingPotions += amount; }
        public void useNormalHealingPotion() {
            if (normalHealingPotions > 0) normalHealingPotions--;
        }

        public int getFullHealingPotions() { return fullHealingPotions; }
        public void addFullHealingPotions(int amount) { this.fullHealingPotions += amount; }
        public void useFullHealingPotion() {
            if (fullHealingPotions > 0) fullHealingPotions--;
        }

        public int getEnergyPotions() { return energyPotions; }
        public void addEnergyPotions(int amount) { this.energyPotions += amount; }
        public void useEnergyPotion() {
            if (energyPotions > 0) energyPotions--;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ GEAR DATA CLASSES
    // Note: If you already have Weapon.java and Armor.java in your GameGUI.model
    // package, you can delete these inner classes and import yours instead.
    // ════════════════════════════════════════════════════════════════════════
    public static class Weapon {
        public String name, rarity;
        public int atkBuff, lifestealPercent, poisonChance, bleedChance;
        public int stunChance, freezeChance, confuseChance, energyPerAttack, extraHitChance;
        public java.util.Map<String, String> enchantments = new java.util.HashMap<>();

        // Constructor creates a live copy from the static definition
        public Weapon(GameGUI.model.HeroData.WeaponDef def) {
            this.name = def.name; this.rarity = def.rarity; this.atkBuff = def.atkBuff;
            this.lifestealPercent = def.lifestealPercent; this.poisonChance = def.poisonChance;
            this.bleedChance = def.bleedChance; this.stunChance = def.stunChance;
            this.freezeChance = def.freezeChance; this.confuseChance = def.confuseChance;
            this.energyPerAttack = def.energyPerAttack; this.extraHitChance = def.extraHitChance;
        }
        public String getName() { return name; }
        public int getAtkBuff() { return atkBuff; }
    }

    public static class Armor {
        public String name, rarity;
        public int defBuff, addDefBuff;
        public boolean immuneDebuff, immuneEffects, hasEnchantment;
        public int reflectChance, reflectPercent;

        // Constructor creates a live copy from the static definition
        public Armor(GameGUI.model.HeroData.ArmorDef def) {
            this.name = def.name; this.rarity = def.rarity; this.defBuff = def.defBuff;
            this.immuneDebuff = def.immuneDebuff; this.immuneEffects = def.immuneEffects;
            this.reflectChance = def.reflectChance; this.reflectPercent = def.reflectPercent;
        }
        public String getName() { return name; }
        public int getDefBuff() { return defBuff + addDefBuff; } // Accounts for transferred buffs
    }
}