package GameGUI.model.equipment;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.WeaponDef;
import GameGUI.model.entity.data.WeaponType;
import GameGUI.model.logic.StatusManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Weapon {
    public WeaponType type;
    public String name, rarity;
    public int atkBuff, lifestealPercent, poisonChance, bleedChance;
    public int stunChance, freezeChance, confuseChance, energyPerAttack, extraHitChance;

    public int addLifestealPercent = 0;
    public Map<String, String> enchantments = new HashMap<>();

    public Weapon(WeaponDef def) {
        this.type             = def.type;
        this.name             = def.name;
        this.rarity           = def.rarity;
        this.atkBuff          = def.atkBuff;
        this.lifestealPercent = def.lifestealPercent;
        this.poisonChance     = def.poisonChance;
        this.bleedChance      = def.bleedChance;
        this.stunChance       = def.stunChance;
        this.freezeChance     = def.freezeChance;
        this.confuseChance    = def.confuseChance;
        this.energyPerAttack  = def.energyPerAttack;
        this.extraHitChance   = def.extraHitChance;
    }

    public int getAtkBuff() { return atkBuff; }

    public abstract List<String> applyEffects(Combatant player, Combatant enemy, int damage);

    protected void applyBaseEffects(Combatant player, Combatant enemy, int damage, List<String> logs) {
        // 💖 Lifesteal
        int totalLifesteal = lifestealPercent + addLifestealPercent;
        if (totalLifesteal > 0) {
            int heal = (int)(damage * (totalLifesteal / 100.0));
            heal = Math.min(heal, player.getMaxHp() - player.getCurrentHp());
            if (heal > 0) {
                player.heal(heal);
                logs.add("💖 " + name + " restores " + heal + " HP!");
            }
        }

        // ☠️ Status Effects — all delegated to StatusManager.
        // Immunity checks are now handled inside StatusManager itself, so no
        // extra guard needed here.
        StatusManager sm = enemy.getStatusManager();

        if (Math.random() * 100 < poisonChance)  sm.applyPoison(2, logs);
        if (Math.random() * 100 < bleedChance)   sm.applyBleed(2, logs);
        if (Math.random() * 100 < stunChance)    sm.applyStun(logs);
        if (Math.random() * 100 < freezeChance)  sm.applyFreeze(logs);

        // Confusion now lasts 2 turns instead of being consumed in one check
        if (Math.random() * 100 < confuseChance) sm.applyConfuse(2, logs);
    }
}