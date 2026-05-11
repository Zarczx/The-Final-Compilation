package GameGUI.model.entity.data.definition;

// ─── Skill Definition ────────────────────────────────────────────────────
public class SkillDef {
    public final String icon, name, description;
    public final double minMultiplier, maxMultiplier;
    public final boolean pierceArmor;
    public final int cooldown;
    public final int energyCost;
    public final int hitCount;

    public SkillDef(String icon, String name, String description,
                    double minMultiplier, double maxMultiplier,
                    boolean pierceArmor, int cooldown, int energyCost, int hitCount) {
        this.icon = icon; this.name = name; this.description = description;
        this.minMultiplier = minMultiplier; this.maxMultiplier = maxMultiplier;
        this.pierceArmor = pierceArmor;
        this.cooldown = cooldown; this.energyCost = energyCost;
        this.hitCount = hitCount;
    }
}
