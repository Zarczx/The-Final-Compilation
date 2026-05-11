package GameGUI.model.equipment;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.HeroData;
import GameGUI.model.entity.data.definition.WeaponDef;

import java.util.ArrayList;
import java.util.List;

public class Bow extends Weapon {

    // ─── Bow Constants ───────────────────────────────────────────────────────
    public static final WeaponDef WOODEN_BOW       = new WeaponDef(HeroData.WeaponType.BOW, "Wooden Bow",       "⚪", 5,  0,  0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef OAK_LONGBOW      = new WeaponDef(HeroData.WeaponType.BOW, "Oak Longbow",      "🟢", 10, 0,  0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef TWINSHOT_BOW     = new WeaponDef(HeroData.WeaponType.BOW, "Twinshot Bow",     "🔵", 20, 0,  0, 0, 0, 0, 0, 0, 20);
    public static final WeaponDef LIFEBLOOM_BOW    = new WeaponDef(HeroData.WeaponType.BOW, "Lifebloom Bow",    "🔵", 20, 8,  0, 0, 0, 0, 0, 0, 0);
    public static final WeaponDef AETHERSTRIKE_BOW = new WeaponDef(HeroData.WeaponType.BOW, "Aetherstrike Bow", "🟣", 35, 12, 0, 0, 0, 0, 0, 0, 25);
    public static final WeaponDef GOLDEN_TALON     = new WeaponDef(HeroData.WeaponType.BOW, "Golden Talon",     "🟡", 50, 15, 0, 0, 0, 0, 0, 0, 30);

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Bow(WeaponDef def) {
        super(def);
    }

    @Override
    public List<String> applyEffects(Combatant player, Combatant enemy, int damage) {
        List<String> logs = new ArrayList<>();

        applyBaseEffects(player, enemy, damage, logs);

        // Bow-specific extra hit (Piercing Shot)
        if (extraHitChance > 0 && Math.random() * 100 < extraHitChance) {
            int extraDamage = (int) (damage * (0.20 + (Math.random() * 0.20)));
            enemy.setCurrentHp(enemy.getCurrentHp() - extraDamage);
            logs.add("🏹 Twinshot! Extra arrow hit for " + extraDamage + " damage!");
        }

        return logs;
    }
}