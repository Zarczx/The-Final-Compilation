package GameGUI.model.entity.data;

// ─── Armor Definition ────────────────────────────────────────────────────
public class ArmorDef {
    public final String name, rarity;
    public final int hpBuff, defBuff;
    public final boolean immuneDebuff, immuneEffects;
    public final int reflectChance, reflectPercent;

    public ArmorDef(String name, String rarity, int hpBuff, int defBuff,
                    boolean immuneDebuff, boolean immuneEffects, int reflectChance, int reflectPercent) {
        this.name = name;
        this.rarity = rarity;
        this.hpBuff = hpBuff;
        this.defBuff = defBuff;
        this.immuneDebuff = immuneDebuff;
        this.immuneEffects = immuneEffects;
        this.reflectChance = reflectChance;
        this.reflectPercent = reflectPercent;
    }
}
