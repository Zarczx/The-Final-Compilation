package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.ArrayList;
import java.util.List;

public class Staff extends Weapon {

    // ─── Staff Constants ─────────────────────────────────────────────────────
    public static final HeroData.WeaponDef WOODEN_STAFF       = new HeroData.WeaponDef(HeroData.WeaponType.STAFF, "Wooden Staff",       "⚪", 5,  0,  0, 0, 0, 0, 0,  0, 0);
    public static final HeroData.WeaponDef APPRENTICE_STAFF   = new HeroData.WeaponDef(HeroData.WeaponType.STAFF, "Apprentice's Staff", "🟢", 10, 0,  0, 0, 0, 0, 0,  0, 0);
    public static final HeroData.WeaponDef MYSTIC_MIND_STAFF  = new HeroData.WeaponDef(HeroData.WeaponType.STAFF, "Mystic Mind Staff",  "🔵", 20, 0,  0, 0, 0, 0, 30, 0, 0);
    public static final HeroData.WeaponDef FLAMEHEART_STAFF   = new HeroData.WeaponDef(HeroData.WeaponType.STAFF, "Flameheart Staff",   "🔵", 20, 8,  0, 0, 0, 0, 0,  0, 0);
    public static final HeroData.WeaponDef AETHERIC_STAFF     = new HeroData.WeaponDef(HeroData.WeaponType.STAFF, "Aetheric Staff",     "🟣", 35, 12, 0, 0, 0, 0, 25, 0, 0);
    public static final HeroData.WeaponDef CHRONOMANCER_STAFF = new HeroData.WeaponDef(HeroData.WeaponType.STAFF, "Chronomancer Staff", "🟡", 50, 15, 0, 0, 0, 0, 35, 0, 0);

    // ─── Constructor ─────────────────────────────────────────────────────────
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