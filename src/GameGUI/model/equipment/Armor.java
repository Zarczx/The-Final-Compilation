package GameGUI.model.equipment;

import GameGUI.model.entity.HeroData;

public class Armor {
    public String name, rarity;
    public int hpBuff; // <-- ADDED THIS
    public int defBuff, addDefBuff;
    public boolean immuneDebuff, immuneEffects, hasEnchantment;
    public int reflectChance, reflectPercent;

    public Armor(HeroData.ArmorDef def) {
        this.name = def.name;
        this.rarity = def.rarity;

        this.hpBuff = def.hpBuff; // <-- ADDED THIS
        this.defBuff = def.defBuff;

        this.immuneDebuff = def.immuneDebuff;
        this.immuneEffects = def.immuneEffects;
        this.reflectChance = def.reflectChance;
        this.reflectPercent = def.reflectPercent;
    }

    public String getName() { return name; }
    public int getDefBuff() { return defBuff + addDefBuff; }
}