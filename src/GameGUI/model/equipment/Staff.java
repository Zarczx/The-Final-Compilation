package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.ArrayList;
import java.util.List;

public class Staff extends Weapon {

    public Staff(HeroData.WeaponDef def) {
        super(def);
    }

    @Override
    public List<String> applyEffects(Combatant player, Combatant enemy, int damage) {
        List<String> logs = new ArrayList<>();

        applyBaseEffects(player, enemy, damage, logs);

        // Staff-specific effect (Energy Restoration)
        if (energyPerAttack > 0) {
            int restore = energyPerAttack;
            player.energy = Math.min(player.maxEnergy, player.energy + restore);
            logs.add("✨ Arc Surge! Restored " + restore + " Energy!");
        }

        return logs;
    }
}