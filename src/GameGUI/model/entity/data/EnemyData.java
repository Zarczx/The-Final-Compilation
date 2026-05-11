package GameGUI.model.entity.data;

import GameGUI.model.entity.base.Combatant;

public interface EnemyData {
    // ─── Getters ─────────────────────────────────────────────────────────
    String getName();
    String getRole();
    String getEmoji();

    int getMaxHp();
    int getHp();
    int getAttack();
    int getDefense();

    int getWorldLevel();
    int getCount();
    int getXpReward();

    double getMinMultiplier();
    double getMaxMultiplier();

    // ─── Combat Actions ──────────────────────────────────────────────────
    void takeDamage(int amount);
    boolean isAlive();
    void performTurn(Combatant player);
}