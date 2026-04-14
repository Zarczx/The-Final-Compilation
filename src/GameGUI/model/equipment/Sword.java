package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.ArrayList;
import java.util.List;

public class Sword extends Weapon {

    public Sword(HeroData.WeaponDef def) {
        super(def);
    }

    @Override
    public List<String> applyEffects(Combatant player, Combatant enemy, int damage) {
        List<String> logs = new ArrayList<>();

        // 1. Apply the standard Lifesteal and Status effects first
        applyBaseEffects(player, enemy, damage, logs);

        // 2. Apply Sword-specific effects (Extra Hit)
        if (extraHitChance > 0 && Math.random() * 100 < extraHitChance) {
            int extraDamage = (int) (damage * (0.20 + (Math.random() * 0.20))); // 20-40% extra
            enemy.currentHp -= extraDamage;
            logs.add("⚡ Sword combo! Extra hit for " + extraDamage + " damage!");
        }

        return logs;
    }
}