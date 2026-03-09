package GameGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BattleLogic.java
 *
 * Pure battle engine — no UI, no Swing.
 * Ported from Battle.java (battle.Battle) and adapted for GUI use.
 *
 * Responsibilities:
 *  - Turn management (player turn / enemy turn)
 *  - Damage calculation with variance (mirrors Battle.java behavior)
 *  - Defense / buff / debuff modifiers
 *  - DoT (poison/burn) effects after each action
 *  - Special ability cooldown tracking
 *  - Revive / Phoenix Soulstone logic (flags only — UI handles display)
 *  - Battle-end detection (victory / defeat)
 *  - Battle log (plain strings, no color util — GUI applies its own styling)
 */
public class BattleLogic {

    // ─── Inner model classes ──────────────────────────────────────────────────

    public static class Combatant {
        public final String name;
        public final String role;
        public final String emoji;
        public final int maxHp;
        public final int attack;
        public final int defense;
        public final Special special;   // null for enemy

        public int currentHp;
        public int energy;
        public int maxEnergy;
        public boolean defending;
        public int specialCooldown;
        public int attackModifier;      // mirrors getEffects().updateAttackModifiers()
        public int defenseModifier;     // mirrors getEffects().updateDefenseModifiers()
        public int dotDamage;           // mirrors updateDoTEffects() — damage per turn
        public int dotTurnsLeft;

        public Combatant(String name, String role, String emoji,
                         int maxHp, int attack, int defense,
                         int energy, int maxEnergy,
                         Special special) {
            this.name          = name;
            this.role          = role;
            this.emoji         = emoji;
            this.maxHp         = maxHp;
            this.attack        = attack;
            this.defense       = defense;
            this.special       = special;
            this.currentHp     = maxHp;
            this.energy        = energy;
            this.maxEnergy     = maxEnergy;
            this.defending     = false;
            this.specialCooldown = 0;
            this.attackModifier  = 0;
            this.defenseModifier = 0;
            this.dotDamage     = 0;
            this.dotTurnsLeft  = 0;
        }

        public boolean isAlive() { return currentHp > 0; }

        public int effectiveAttack()  { return Math.max(1, attack  + attackModifier);  }
        public int effectiveDefense() { return Math.max(0, defense + defenseModifier); }
    }

    public static class Special {
        public final String name;
        public final String icon;
        public final String description;
        public final double multiplier;
        public final boolean pierceArmor;  // rogue-style: ignores 70% of defense
        public final int cooldown;

        public Special(String name, String icon, String description,
                       double multiplier, boolean pierceArmor, int cooldown) {
            this.name        = name;
            this.icon        = icon;
            this.description = description;
            this.multiplier  = multiplier;
            this.pierceArmor = pierceArmor;
            this.cooldown    = cooldown;
        }
    }

    /** Mirrors Battle.java's pre-battle option: QUIT */
    public enum BattleAction { ATTACK, DEFEND, SPECIAL }

    public enum TurnOwner { PLAYER, ENEMY }

    /** Result returned after every action so the UI can react */
    public static class ActionResult {
        public final String logMessage;
        public final boolean wasDefend;
        public final int damageDealt;       // 0 if defend
        public final int dotDamageApplied;  // after-action DoT tick
        public final String target;         // "hero" or "enemy"
        public final boolean isSpecial;
        public final boolean isBerserk;     // enemy berserk mode (Battle.java low-HP behavior)

        public ActionResult(String logMessage, boolean wasDefend, int damageDealt,
                            int dotDamageApplied, String target,
                            boolean isSpecial, boolean isBerserk) {
            this.logMessage       = logMessage;
            this.wasDefend        = wasDefend;
            this.damageDealt      = damageDealt;
            this.dotDamageApplied = dotDamageApplied;
            this.target           = target;
            this.isSpecial        = isSpecial;
            this.isBerserk        = isBerserk;
        }
    }

    public enum BattleOutcome { ONGOING, VICTORY, DEFEAT }

    // ─── Fields ───────────────────────────────────────────────────────────────

    private final Combatant hero;
    private final Combatant enemy;
    private TurnOwner currentTurn;
    private int round;
    private boolean reviveUsed;
    private boolean phoenixSoulstoneAvailable; // mirrors player.getInventory().hasPhoenixSoulstone()
    private final List<String> battleLog;
    private final Random rng;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public BattleLogic(Combatant hero, Combatant enemy, boolean hasPhoenixSoulstone) {
        this.hero                     = hero;
        this.enemy                    = enemy;
        this.currentTurn              = TurnOwner.PLAYER;
        this.round                    = 1;
        this.reviveUsed               = false;
        this.phoenixSoulstoneAvailable = hasPhoenixSoulstone;
        this.battleLog                = new ArrayList<>();
        this.rng                      = new Random();
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    public Combatant getHero()  { return hero;  }
    public Combatant getEnemy() { return enemy; }
    public TurnOwner getCurrentTurn() { return currentTurn; }
    public int getRound() { return round; }
    public List<String> getBattleLog() { return battleLog; }
    public boolean isReviveUsed() { return reviveUsed; }
    public boolean isPhoenixSoulstoneAvailable() { return phoenixSoulstoneAvailable; }

    /**
     * Execute one player action.
     * Mirrors Battle.java's player.turn(enemy) call.
     * Returns the result so the GUI can animate / update bars.
     */
    public ActionResult playerAction(BattleAction action) {
        if (currentTurn != TurnOwner.PLAYER) return null;

        // mirrors: player.getEffects().updateAttackModifiers() / updateDefenseModifiers()
        updateModifiers(hero);

        ActionResult result;

        switch (action) {
            case ATTACK  -> result = resolveAttack(hero, enemy, 1.0, false, false);
            case DEFEND  -> result = resolveDefend(hero);
            case SPECIAL -> result = resolveSpecial(hero, enemy);
            default      -> result = resolveAttack(hero, enemy, 1.0, false, false);
        }

        // mirrors: player.getEffects().updateDoTEffects()
        int dot = applyDoT(hero);
        if (dot > 0) {
            log("🔥 " + hero.name + " takes " + dot + " burn/poison damage!");
        }

        // Cooldown tick happens at end of enemy turn (see enemyTurn())
        return new ActionResult(result.logMessage, result.wasDefend, result.damageDealt,
                dot, result.target, result.isSpecial, false);
    }

    /**
     * Execute one enemy turn.
     * Mirrors Battle.java's enemy.turn(player) with the same AI:
     *  - 15% chance to defend
     *  - Berserk at < 40% HP (mirrors low-HP aggression hinted in Battle.java)
     *  - Otherwise normal attack
     */
    public ActionResult enemyTurn() {
        if (currentTurn != TurnOwner.ENEMY) return null;

        // mirrors: enemy.getEffects().updateAttackModifiers() / updateDefenseModifiers()
        updateModifiers(enemy);

        // Reset hero's defend flag (it only lasts one round)
        hero.defending = false;

        ActionResult result;
        double roll = rng.nextDouble();

        if (roll < 0.15) {
            result = resolveDefend(enemy);
        } else if (roll < 0.30 && enemy.currentHp < enemy.maxHp * 0.4) {
            // Berserk — mirrors Battle.java's "enemy.turn(player)" at low HP
            result = resolveAttack(enemy, hero, 1.5, false, true);
        } else {
            result = resolveAttack(enemy, hero, 1.0, false, false);
        }

        // mirrors: enemy.getEffects().updateDoTEffects()
        int dot = applyDoT(enemy);
        if (dot > 0) {
            log("🔥 " + enemy.name + " takes " + dot + " burn/poison damage!");
        }

        // Advance round and tick hero's special cooldown
        round++;
        if (hero.specialCooldown > 0) hero.specialCooldown--;

        currentTurn = TurnOwner.PLAYER;

        return new ActionResult(result.logMessage, result.wasDefend, result.damageDealt,
                dot, result.target, result.isSpecial, result.isBerserk);
    }

    /**
     * Check win/loss after each action.
     * Mirrors the isAlive() checks in Battle.java's battleLoop().
     */
    public BattleOutcome checkOutcome() {
        if (!enemy.isAlive()) return BattleOutcome.VICTORY;
        if (!hero.isAlive())  return BattleOutcome.DEFEAT;
        return BattleOutcome.ONGOING;
    }

    /**
     * Attempt a revive after defeat.
     * Mirrors Battle.java's Phoenix Soulstone / ReviveTrial logic.
     * Returns true if revive was applied.
     */
    public boolean attemptRevive() {
        if (phoenixSoulstoneAvailable) {
            hero.currentHp = hero.maxHp / 2;
            hero.energy    = hero.maxEnergy / 2;
            phoenixSoulstoneAvailable = false;
            log("🕊️ The Phoenix Soulstone revives " + hero.name + "!");
            log("💚 HP: " + hero.currentHp + " | Energy: " + hero.energy);
            currentTurn = TurnOwner.PLAYER;
            return true;
        }
        if (!reviveUsed) {
            // mirrors ReviveTrial.run() — GUI handles the actual mini-game/check
            // Here we just flag it; GUI calls confirmRevive() after its own logic
            return false;
        }
        return false;
    }

    /**
     * Called by GUI after the player passes the ReviveTrial.
     * Mirrors Battle.java: setHp(maxHp/2), setEnergy(maxEnergy/2), resetAllEffects().
     */
    public void confirmRevive() {
        hero.currentHp = hero.maxHp / 2;
        hero.energy    = hero.maxEnergy / 2;
        hero.attackModifier  = 0;   // mirrors player.getEffects().resetAllEffects()
        hero.defenseModifier = 0;
        hero.dotDamage       = 0;
        hero.dotTurnsLeft    = 0;
        hero.defending       = false;
        reviveUsed = true;
        currentTurn = TurnOwner.PLAYER;
        log("✨ Knowledge revives " + hero.name + "! Restored with 50% HP and Energy.");
    }

    /** Switch turn to enemy (called by GUI after player action animation). */
    public void advanceToEnemyTurn() {
        currentTurn = TurnOwner.ENEMY;
    }

    // ─── Health Bar Generator ─────────────────────────────────────────────────

    /**
     * Mirrors Battle.java generateBar().
     * Returns a 20-char block string for display in the GUI log or tooltips.
     */
    public String generateBar(int current, int max) {
        int length = 20;
        int filled = (int) Math.round((double) current / max * length);
        int empty  = length - filled;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append("█");
        for (int i = 0; i < empty;  i++) bar.append("░");
        return bar.toString();
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private ActionResult resolveAttack(Combatant attacker, Combatant defender,
                                       double multiplier, boolean pierceArmor, boolean berserk) {
        int dmg = calcDamage(attacker, defender, multiplier, pierceArmor);
        defender.currentHp = clamp(defender.currentHp - dmg, 0, defender.maxHp);
        attacker.defending = false;

        String who    = attacker == hero ? "⚔️ " + hero.name : "👹 " + enemy.name;
        String target = attacker == hero ? "enemy" : "hero";
        String msg    = berserk
                ? who + " goes BERSERK and strikes for " + dmg + " damage! 🔥"
                : who + " attacks for " + dmg + " damage!";
        log(msg);
        return new ActionResult(msg, false, dmg, 0, target, false, berserk);
    }

    private ActionResult resolveDefend(Combatant c) {
        c.defending = true;
        String msg = (c == hero)
                ? "🛡️ " + hero.name  + " takes a defensive stance! Damage halved next hit."
                : "🛡️ " + enemy.name + " braces for impact!";
        log(msg);
        return new ActionResult(msg, true, 0, 0, c == hero ? "hero" : "enemy", false, false);
    }

    private ActionResult resolveSpecial(Combatant attacker, Combatant defender) {
        if (attacker.specialCooldown > 0 || attacker.special == null) {
            return resolveAttack(attacker, defender, 1.0, false, false);
        }
        Special sp  = attacker.special;
        int dmg     = calcDamage(attacker, defender, sp.multiplier, sp.pierceArmor);
        defender.currentHp       = clamp(defender.currentHp - dmg, 0, defender.maxHp);
        attacker.specialCooldown = sp.cooldown;
        attacker.defending       = false;

        String msg = "✨ " + attacker.name + " unleashes " + sp.icon + " " + sp.name
                + " for " + dmg + " damage!";
        log(msg);
        return new ActionResult(msg, false, dmg, 0, "enemy", true, false);
    }

    /**
     * Core damage formula — mirrors Battle.java's implied calc:
     * base = effectiveAttack * multiplier
     * reduced by half of defender's effective defense
     * ±15% variance
     * halved again if defender is defending
     */
    private int calcDamage(Combatant attacker, Combatant defender,
                           double multiplier, boolean pierceArmor) {
        double base    = attacker.effectiveAttack() * multiplier;
        double defense = pierceArmor
                ? defender.effectiveDefense() * 0.3
                : defender.effectiveDefense() * 0.5;
        double variance = 0.85 + rng.nextDouble() * 0.30;
        double raw      = (base - defense) * variance;
        int dmg         = (int) Math.round(Math.max(1.0, raw));
        return defender.defending ? Math.max(1, dmg / 2) : dmg;
    }

    /** Mirrors player.getEffects().updateAttackModifiers() / updateDefenseModifiers() */
    private void updateModifiers(Combatant c) {
        // Base implementation: modifiers decay by 1 per turn (extendable)
        if (c.attackModifier  > 0) c.attackModifier--;
        if (c.defenseModifier > 0) c.defenseModifier--;
    }

    /** Mirrors player.getEffects().updateDoTEffects() */
    private int applyDoT(Combatant c) {
        if (c.dotTurnsLeft <= 0 || c.dotDamage <= 0) return 0;
        int dmg = c.dotDamage;
        c.currentHp    = clamp(c.currentHp - dmg, 0, c.maxHp);
        c.dotTurnsLeft = Math.max(0, c.dotTurnsLeft - 1);
        return dmg;
    }

    private int clamp(int v, int min, int max) { return Math.min(max, Math.max(min, v)); }

    private void log(String msg) { battleLog.add(msg); }
}