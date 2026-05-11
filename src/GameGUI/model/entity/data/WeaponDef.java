package GameGUI.model.entity.data;

// ─── Weapon Definition ───────────────────────────────────────────────────
public class WeaponDef {
    public final HeroData.WeaponType type;
    public final String name, rarity;
    public final int atkBuff;
    public final int lifestealPercent, poisonChance, bleedChance, stunChance;
    public final int freezeChance, confuseChance, energyPerAttack, extraHitChance;

    public WeaponDef(HeroData.WeaponType type, String name, String rarity, int atkBuff, int lifestealPercent,
                     int poisonChance, int bleedChance, int stunChance,
                     int freezeChance, int confuseChance, int energyPerAttack, int extraHitChance) {
        this.type = type;
        this.name = name; this.rarity = rarity; this.atkBuff = atkBuff;
        this.lifestealPercent = lifestealPercent; this.poisonChance = poisonChance;
        this.bleedChance = bleedChance; this.stunChance = stunChance;
        this.freezeChance = freezeChance; this.confuseChance = confuseChance;
        this.energyPerAttack = energyPerAttack; this.extraHitChance = extraHitChance;
    }
}
