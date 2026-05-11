package GameGUI.model.equipment;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.WeaponDef;
import GameGUI.model.entity.data.WeaponType;

import java.util.ArrayList;
import java.util.List;

public class Staff extends Weapon {

    // ─── Staff Constants ─────────────────────────────────────────────────────
    public static final WeaponDef WOODEN_STAFF       = new WeaponDef(WeaponType.STAFF, "Wooden Staff",       "⚪", 5,  0,  0, 0, 0, 0, 0,  0, 0);
    public static final WeaponDef APPRENTICE_STAFF   = new WeaponDef(WeaponType.STAFF, "Apprentice's Staff", "🟢", 10, 0,  0, 0, 0, 0, 0,  0, 0);
    public static final WeaponDef MYSTIC_MIND_STAFF  = new WeaponDef(WeaponType.STAFF, "Mystic Mind Staff",  "🔵", 20, 0,  0, 0, 0, 0, 30, 0, 0);
    public static final WeaponDef FLAMEHEART_STAFF   = new WeaponDef(WeaponType.STAFF, "Flameheart Staff",   "🔵", 20, 8,  0, 0, 0, 0, 0,  0, 0);
    public static final WeaponDef AETHERIC_STAFF     = new WeaponDef(WeaponType.STAFF, "Aetheric Staff",     "🟣", 35, 12, 0, 0, 0, 0, 25, 0, 0);
    public static final WeaponDef CHRONOMANCER_STAFF = new WeaponDef(WeaponType.STAFF, "Chronomancer Staff", "🟡", 50, 15, 0, 0, 0, 0, 35, 0, 0);

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Staff(WeaponDef def) {
        super(def);
    }

    @Override
    public List<String> applyEffects(Combatant player, Combatant enemy, int damage) {
        List<String> logs = new ArrayList<>();

        applyBaseEffects(player, enemy, damage, logs);

        // Staff-specific effect (Energy Restoration)
        if (energyPerAttack > 0) {
            int restore = energyPerAttack;
            player.setEnergy(Math.min(player.getMaxEnergy(), player.getEnergy() + restore));
            logs.add("✨ Arc Surge! Restored " + restore + " Energy!");
        }

        return logs;
    }
}