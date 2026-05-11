package GameGUI.model.entity.base;

import GameGUI.model.entity.data.EnemyData;

public abstract class BaseEnemy implements EnemyData {
    private String name, role, emoji;
    private int maxHp, currentHp, attack, defense;
    private int worldLevel, count, xpReward;
    private double minMultiplier, maxMultiplier;

    public BaseEnemy(String name, String role, String emoji,
                     int maxHp, int attack, int defense,
                     int worldLevel, int count, int xpReward,
                     double minMultiplier, double maxMultiplier) {
        this.name = name;
        this.role = role;
        this.emoji = emoji;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.worldLevel = worldLevel;
        this.count = count;
        this.xpReward = xpReward;
        this.minMultiplier = minMultiplier;
        this.maxMultiplier = maxMultiplier;
    }

    // ─── Getters (Fulfilling the EnemyData contract) ─────────────────────
    @Override public String getName() { return name; }
    @Override public String getRole() { return role; }
    @Override public String getEmoji() { return emoji; }
    @Override public int getMaxHp() { return maxHp; }
    @Override public int getHp() { return currentHp; }
    @Override public int getAttack() { return attack; }
    @Override public int getDefense() { return defense; }
    @Override public int getWorldLevel() { return worldLevel; }
    @Override public int getCount() { return count; }
    @Override public int getXpReward() { return xpReward; }
    @Override public double getMinMultiplier() { return minMultiplier; }
    @Override public double getMaxMultiplier() { return maxMultiplier; }

    // ─── Basic Combat ──────────────────────────────────────────────────
    @Override
    public void takeDamage(int amount) {
        // Fixed: DamageCalculator already handles the defense subtraction!
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    @Override
    public boolean isAlive() {
        return this.currentHp > 0;
    }

    // Standard attack behavior. (Subclasses can override this later!)
    @Override
    public void performTurn(Combatant player) {
        player.takeDamage(this.attack);
    }
}