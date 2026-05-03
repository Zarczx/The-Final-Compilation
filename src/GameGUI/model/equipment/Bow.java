package GameGUI.model.equipment;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.ArrayList;
import java.util.List;

public class Bow extends Weapon {

    // ─── Bow Constants ───────────────────────────────────────────────────────
    public static final HeroData.WeaponDef WOODEN_BOW       = new HeroData.WeaponDef(HeroData.WeaponType.BOW, "Wooden Bow",       "⚪", 5,  0,  0, 0, 0, 0, 0, 0, 0);
    public static final HeroData.WeaponDef OAK_LONGBOW      = new HeroData.WeaponDef(HeroData.WeaponType.BOW, "Oak Longbow",      "🟢", 10, 0,  0, 0, 0, 0, 0, 0, 0);
    public static final HeroData.WeaponDef TWINSHOT_BOW     = new HeroData.WeaponDef(HeroData.WeaponType.BOW, "Twinshot Bow",     "🔵", 20, 0,  0, 0, 0, 0, 0, 0, 20);
    public static final HeroData.WeaponDef LIFEBLOOM_BOW    = new HeroData.WeaponDef(HeroData.WeaponType.BOW, "Lifebloom Bow",    "🔵", 20, 8,  0, 0, 0, 0, 0, 0, 0);
    public static final HeroData.WeaponDef AETHERSTRIKE_BOW = new HeroData.WeaponDef(HeroData.WeaponType.BOW, "Aetherstrike Bow", "🟣", 35, 12, 0, 0, 0, 0, 0, 0, 25);
    public static final HeroData.WeaponDef GOLDEN_TALON     = new HeroData.WeaponDef(HeroData.WeaponType.BOW, "Golden Talon",     "🟡", 50, 15, 0, 0, 0, 0, 0, 0, 30);

    // ─── Constructor ─────────────────────────────────────────────────────────
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