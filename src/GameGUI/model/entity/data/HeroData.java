package GameGUI.model.entity.data;

import GameGUI.model.entity.base.BaseEnemy;

import java.util.List;

public interface HeroData {

    // ─── Weapon Types ────────────────────────────────────────────────────────
    enum WeaponType { SWORD, BOW, STAFF }

    // ─── Skill Definition ────────────────────────────────────────────────────
    class SkillDef {
        public final String icon, name, description;
        public final double minMultiplier, maxMultiplier;
        public final boolean pierceArmor;
        public final int cooldown;
        public final int energyCost;
        public final int hitCount;

        public SkillDef(String icon, String name, String description,
                        double minMultiplier, double maxMultiplier,
                        boolean pierceArmor, int cooldown, int energyCost, int hitCount) {
            this.icon = icon; this.name = name; this.description = description;
            this.minMultiplier = minMultiplier; this.maxMultiplier = maxMultiplier;
            this.pierceArmor = pierceArmor;
            this.cooldown = cooldown; this.energyCost = energyCost;
            this.hitCount = hitCount;
        }
    }

    // ─── Weapon Definition ───────────────────────────────────────────────────
    class WeaponDef {
        public final WeaponType type;
        public final String name, rarity;
        public final int atkBuff;
        public final int lifestealPercent, poisonChance, bleedChance, stunChance;
        public final int freezeChance, confuseChance, energyPerAttack, extraHitChance;

        public WeaponDef(WeaponType type, String name, String rarity, int atkBuff, int lifestealPercent,
                         int poisonChance, int bleedChance, int stunChance,
                         int freezeChance, int confuseChance, int energyPerAttack, int extraHitChance) {
            this.type = type;
            this.name = name; this.rarity = rarity; this.atkBuff = atkBuff;
            this.lifestealPercent = lifestealPercent; this.poisonChance = poisonChance;
            this.bleedChance = bleedChance; this.stunChance = stunChance;
            this.freezeChance = freezeChance; this.confuseChance = confuseChance;
            this.energyPerAttack = energyPerAttack; this.extraHitChance = extraHitChance;
        }
    }

    // ─── Armor Definition ────────────────────────────────────────────────────
    class ArmorDef {
        public final String name, rarity;
        public final int hpBuff, defBuff;
        public final boolean immuneDebuff, immuneEffects;
        public final int reflectChance, reflectPercent;

        public ArmorDef(String name, String rarity, int hpBuff, int defBuff,
                        boolean immuneDebuff, boolean immuneEffects, int reflectChance, int reflectPercent) {
            this.name = name; this.rarity = rarity; this.hpBuff = hpBuff; this.defBuff = defBuff;
            this.immuneDebuff = immuneDebuff; this.immuneEffects = immuneEffects;
            this.reflectChance = reflectChance; this.reflectPercent = reflectPercent;
        }
    }

    // ─── Hero Definition ─────────────────────────────────────────────────────
    class HeroDefinition {
        public final String name, role, emoji, backstory, passive;
        public final int maxHp, attack, defense, maxEnergy;
        public final SkillDef[] skills;
        public final WeaponDef startingWeapon;
        public final ArmorDef startingArmor;

        public HeroDefinition(String name, String role, String emoji, String backstory, String passive,
                              int maxHp, int attack, int defense, int maxEnergy,
                              SkillDef[] skills, WeaponDef startingWeapon, ArmorDef startingArmor) {
            this.name = name; this.role = role; this.emoji = emoji;
            this.backstory = backstory; this.passive = passive;
            this.maxHp = maxHp; this.attack = attack; this.defense = defense;
            this.maxEnergy = maxEnergy; this.skills = skills;
            this.startingWeapon = startingWeapon;
            this.startingArmor  = startingArmor;
        }
    }

    // ─── Enemy Definition ────────────────────────────────────────────────────


    // ─── Function Contracts (To be implemented by base classes) ──────────────

    // Armors
    /*ArmorDef getLeatherGuard();
    ArmorDef getIronVanguard();
    ArmorDef getAegisMail();
    ArmorDef getVanguardRobe();
    ArmorDef getSkyforgePlate();
    ArmorDef getCelestialBattlegear();
    */

    // Registries
    List<HeroDefinition> getHeroes();
    List<EnemyData> getWorld1Enemies();
    List<EnemyData> getWorld2Enemies();
    List<EnemyData> getWorld3Enemies();
    List<EnemyData> getFinalBossSequence();
}