package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.ArrayList;
import java.util.List;

public class Sword extends Weapon {

    // ─── Sword Constants ─────────────────────────────────────────────────────
    public static final HeroData.WeaponDef OLD_BROADSWORD     = new HeroData.WeaponDef(HeroData.WeaponType.SWORD, "Old Broadsword",     "⚪", 5,  0,  0, 0, 0, 0, 0, 0, 0);
    public static final HeroData.WeaponDef IRON_SHORTSWORD    = new HeroData.WeaponDef(HeroData.WeaponType.SWORD, "Iron Shortsword",    "🟢", 10, 0,  0, 0, 0, 0, 0, 0, 0);
    public static final HeroData.WeaponDef TWINSTRIKE_BLADE   = new HeroData.WeaponDef(HeroData.WeaponType.SWORD, "Twinstrike Blade",   "🔵", 20, 0,  0, 0, 0, 0, 0, 0, 20);
    public static final HeroData.WeaponDef LIFEBOND_BLADE     = new HeroData.WeaponDef(HeroData.WeaponType.SWORD, "Lifebond Blade",     "🔵", 20, 8,  0, 0, 0, 0, 0, 0, 0);
    public static final HeroData.WeaponDef ECLIPSE_GREATSWORD = new HeroData.WeaponDef(HeroData.WeaponType.SWORD, "Eclipse Greatsword", "🟣", 35, 12, 0, 0, 0, 0, 0, 0, 25);
    public static final HeroData.WeaponDef CELESTIAL_EDGE     = new HeroData.WeaponDef(HeroData.WeaponType.SWORD, "Celestial Edge",     "🟡", 50, 15, 0, 0, 0, 0, 0, 0, 30);

    // ─── Constructor ─────────────────────────────────────────────────────────
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