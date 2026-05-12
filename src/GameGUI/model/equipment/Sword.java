package GameGUI.model.equipment;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.WeaponDef;
import GameGUI.model.entity.data.WeaponType;

import java.util.ArrayList;
import java.util.List;

public class Sword extends Weapon {

    // ─── Sword Constants ─────────────────────────────────────────────────────
    public static final WeaponDef OLD_BROADSWORD     = new WeaponDef(WeaponType.SWORD, "Old Broadsword",     "⚪", 5,  0,  0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef IRON_SHORTSWORD    = new WeaponDef(WeaponType.SWORD, "Iron Shortsword",    "🟢", 10, 0,  0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef TWINSTRIKE_BLADE   = new WeaponDef(WeaponType.SWORD, "Twinstrike Blade",   "🔵", 15, 0,  0, 0, 0, 0, 0, 0, 10);
    public static final WeaponDef LIFEBOND_BLADE     = new WeaponDef(WeaponType.SWORD, "Lifebond Blade",     "🔵", 15, 3,  0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef ECLIPSE_GREATSWORD = new WeaponDef(WeaponType.SWORD, "Eclipse Greatsword", "🟣", 30, 5, 0, 0, 0, 0, 0, 0, 25);
    public static final WeaponDef CELESTIAL_EDGE     = new WeaponDef(WeaponType.SWORD, "Celestial Edge",     "🟡", 50, 15, 0, 0, 0, 0, 0, 0, 35);

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Sword(WeaponDef def) {
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
            enemy.setCurrentHp(enemy.getCurrentHp() - extraDamage);
            logs.add("⚡ Sword combo! Extra hit for " + extraDamage + " damage!");
        }

        return logs;
    }
}