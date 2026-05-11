package GameGUI.model.entity.data;

// ─── Hero Definition ─────────────────────────────────────────────────────
public class HeroDefinition {
    public final String name, role, emoji, backstory, passive;
    public final int maxHp, attack, defense, maxEnergy;
    public final SkillDef[] skills;
    public final WeaponDef startingWeapon;
    public final ArmorDef startingArmor;

    public HeroDefinition(String name, String role, String emoji, String backstory, String passive,
                          int maxHp, int attack, int defense, int maxEnergy,
                          SkillDef[] skills, WeaponDef startingWeapon, ArmorDef startingArmor) {
        this.name = name;
        this.role = role;
        this.emoji = emoji;
        this.backstory = backstory;
        this.passive = passive;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.maxEnergy = maxEnergy;
        this.skills = skills;
        this.startingWeapon = startingWeapon;
        this.startingArmor = startingArmor;
    }
}
