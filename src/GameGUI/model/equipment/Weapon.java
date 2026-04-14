package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Weapon {
    public HeroData.WeaponType type;
    public String name, rarity;
    public int atkBuff, lifestealPercent, poisonChance, bleedChance;
    public int stunChance, freezeChance, confuseChance, energyPerAttack, extraHitChance;

    // Mutable properties (for transferring enchantments)
    public int addLifestealPercent = 0;
    public Map<String, String> enchantments = new HashMap<>();

    public Weapon(HeroData.WeaponDef def) {
        this.type = def.type;
        this.name = def.name;
        this.rarity = def.rarity;
        this.atkBuff = def.atkBuff;
        this.lifestealPercent = def.lifestealPercent;
        this.poisonChance = def.poisonChance;
        this.bleedChance = def.bleedChance;
        this.stunChance = def.stunChance;
        this.freezeChance = def.freezeChance;
        this.confuseChance = def.confuseChance;
        this.energyPerAttack = def.energyPerAttack;
        this.extraHitChance = def.extraHitChance;
    }

    public int getAtkBuff() { return atkBuff; }

    // ★ ABSTRACT METHOD: Every specific weapon must implement this
    public abstract List<String> applyEffects(Combatant player, Combatant enemy, int damage);

    // ★ HELPER METHOD: Handles Lifesteal, Poison, Bleed, etc., shared by all weapons
    protected void applyBaseEffects(Combatant player, Combatant enemy, int damage, List<String> logs) {
        // 💖 Lifesteal
        int totalLifesteal = lifestealPercent + addLifestealPercent;
        if (totalLifesteal > 0) {
            int heal = (int) (damage * (totalLifesteal / 100.0));
            heal = Math.min(heal, player.maxHp - player.currentHp);
            if (heal > 0) {
                player.currentHp += heal;
                logs.add("💖 " + name + " restores " + heal + " HP!");
            }
        }

        // ☠️ Status Effects
        if (Math.random() * 100 < poisonChance) { enemy.dotDamage = 5; enemy.dotTurnsLeft = 2; logs.add("☠️ Poisoned enemy!"); }
        if (Math.random() * 100 < bleedChance) { enemy.dotDamage = 5; enemy.dotTurnsLeft = 2; logs.add("🩸 Enemy is bleeding!"); }
        if (Math.random() * 100 < stunChance) { enemy.stunned = true; logs.add("⛓️ Stunned enemy!"); }
        if (Math.random() * 100 < freezeChance) { enemy.frozen = true; logs.add("❄️ Froze enemy!"); }
        if (Math.random() * 100 < confuseChance) { enemy.confused = true; logs.add("🌀 Confused enemy!"); }
    }
}