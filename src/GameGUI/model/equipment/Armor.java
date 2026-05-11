package GameGUI.model.equipment;

import GameGUI.model.entity.data.HeroData;

public class Armor {
    public String name, rarity;
    public int hpBuff;
    public int defBuff, addDefBuff;
    public boolean immuneDebuff, immuneEffects, hasEnchantment;
    public int reflectChance, reflectPercent;

    // ─── Pre-defined Armors ─────────────────────────────────────────────────
    public static final HeroData.ArmorDef LEATHER_GUARD =
            new HeroData.ArmorDef("Leather Guard", "⚪", 0, 5, false, false, 0, 0);

    public static final HeroData.ArmorDef IRON_VANGUARD =
            new HeroData.ArmorDef("Iron Vanguard", "🟢", 0, 10, false, false, 0, 0);

    public static final HeroData.ArmorDef AEGIS_MAIL =
            new HeroData.ArmorDef("Aegis Mail", "🔵", 0, 25, true, false, 0, 0);

    public static final HeroData.ArmorDef VANGUARD_ROBE =
            new HeroData.ArmorDef("Vanguard Robe", "🟣", 0, 25, false, true, 0, 0);

    public static final HeroData.ArmorDef SKYFORGE_PLATE =
            new HeroData.ArmorDef("Skyforge Plate", "🟣", 0, 40, true, true, 20, 15);

    public static final HeroData.ArmorDef CELESTIAL_BATTLEGEAR =
            new HeroData.ArmorDef("Celestial Battlegear", "🟡", 0, 50, true, true, 30, 20);

    // ─── Constructor & Methods ──────────────────────────────────────────────
    public Armor(HeroData.ArmorDef def) {
        this.name = def.name;
        this.rarity = def.rarity;

        this.hpBuff = def.hpBuff;
        this.defBuff = def.defBuff;

        this.immuneDebuff = def.immuneDebuff;
        this.immuneEffects = def.immuneEffects;
        this.reflectChance = def.reflectChance;
        this.reflectPercent = def.reflectPercent;
    }

    public String getName() { return name; }
    public int getDefBuff() { return defBuff + addDefBuff; }
}