package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.ArrayList;
import java.util.List;

public class Bow extends Weapon {

    public Bow(HeroData.WeaponDef def) {
        super(def);
    }

    @Override
    public List<String> applyEffects(Combatant player, Combatant enemy, int damage) {
        List<String> logs = new ArrayList<>();

        applyBaseEffects(player, enemy, damage, logs);

        // Bow-specific extra hit (Piercing Shot)
        if (extraHitChance > 0 && Math.random() * 100 < extraHitChance) {
            int extraDamage = (int) (damage * (0.20 + (Math.random() * 0.20)));
            enemy.currentHp -= extraDamage;
            logs.add("🏹 Twinshot! Extra arrow hit for " + extraDamage + " damage!");
        }

        return logs;
    }
}