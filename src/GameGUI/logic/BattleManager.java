package GameGUI.logic;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData.HeroDefinition;

import java.util.List;
import java.util.Random;

/**
 * BattleManager — Controls turn order, action resolution, and battle outcome.
 *
 * Rule: BattleManager never writes Combatant fields directly.
 * All HP and energy changes go through Combatant's public mutators:
 *   takeDamage(), heal(), spendEnergy(), restoreEnergy()
 * This keeps clamping logic in one place and makes the battle log trustworthy.
 */
public class BattleManager {

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum BattleAction  { SKILL1, SKILL2, ULTIMATE, SKIP_TURN }
    public enum TurnOwner     { PLAYER, ENEMY }
    public enum BattleOutcome { ONGOING, VICTORY, DEFEAT }

    // ── Result Container ──────────────────────────────────────────────────────

    public static class ActionResult {
        public final String logMessage;
        public final int    damageDealt, dotDamageApplied;
        public final boolean isSpecial, wasDefend;
        public final String statusEffect;

        public ActionResult(String logMessage, int damageDealt, int dotDamageApplied,
                            boolean isSpecial, boolean wasDefend, String statusEffect) {
            this.logMessage       = logMessage;
            this.damageDealt      = damageDealt;
            this.dotDamageApplied = dotDamageApplied;
            this.isSpecial        = isSpecial;
            this.wasDefend        = wasDefend;
            this.statusEffect     = statusEffect;
        }
    }

    // ── State ─────────────────────────────────────────────────────────────────

    private final Combatant      hero, enemy;
    private final HeroDefinition heroDef;
    private final Random         rng = new Random();

    private TurnOwner currentTurn      = TurnOwner.PLAYER;
    private int       round            = 1;
    private boolean   phoenixAvailable;
    private String    lastEnemySkillName = "";

    // Skill energy costs — pulled from heroDef on construction
    private final int s1Cost, s2Cost, ultCost;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public BattleManager(Combatant hero, Combatant enemy,
                         HeroDefinition heroDef, boolean hasPhoenix) {
        this.hero             = hero;
        this.enemy            = enemy;
        this.heroDef          = heroDef;
        this.phoenixAvailable = hasPhoenix;

        if (heroDef != null && heroDef.skills != null && heroDef.skills.length >= 3) {
            s1Cost  = heroDef.skills[0].energyCost;
            s2Cost  = heroDef.skills[1].energyCost;
            ultCost = heroDef.skills[2].energyCost;
        } else {
            s1Cost = 5; s2Cost = 10; ultCost = 20;
        }
    }

    // =========================================================================
    // ACCESSORS
    // =========================================================================

    public Combatant  getHero()               { return hero;               }
    public Combatant  getEnemy()              { return enemy;              }
    public int        getRound()              { return round;              }
    public TurnOwner  getCurrentTurn()        { return currentTurn;        }
    public String     getLastEnemySkillName() { return lastEnemySkillName; }

    public boolean canUseSkill1()   { return hero.energy >= s1Cost;                                    }
    public boolean canUseSkill2()   { return hero.energy >= s2Cost;                                    }
    public boolean canUseUltimate() { return hero.energy >= ultCost && hero.specialCooldown == 0;      }

    // =========================================================================
    // PLAYER TURN
    // =========================================================================

    public ActionResult playerAction(BattleAction action) {
        if (currentTurn != TurnOwner.PLAYER) return null;

        tickModifiers(hero);
        applyMagePassive();

        ActionResult result = switch (action) {
            case SKILL1    -> resolveAttack(0, s1Cost,  false);
            case SKILL2    -> resolveAttack(1, s2Cost,  false);
            case ULTIMATE  -> resolveAttack(2, ultCost, true);
            case SKIP_TURN -> resolveSkipTurn();
        };

        // DoT that was applied TO the hero ticks at end of player turn
        int dot = DamageCalculator.applyDoT(hero);
        return new ActionResult(result.logMessage, result.damageDealt, dot,
                result.isSpecial, result.wasDefend, result.statusEffect);
    }

    // ── Skill attack ──────────────────────────────────────────────────────────

    private ActionResult resolveAttack(int skillIndex, int cost, boolean isUlt) {
        var skill = heroDef.skills[skillIndex];

        hero.spendEnergy(cost);     // Combatant.spendEnergy() — safe, already checked canUse*()

        // Random multiplier in the skill's defined range
        double mult   = skill.minMultiplier + (skill.maxMultiplier - skill.minMultiplier) * rng.nextDouble();
        int    damage = DamageCalculator.calculateDamage(hero, enemy, mult, skill.pierceArmor);

        // Swordsman passive — Blade Swift: 15% crit chance, grants 5% stamina on crit
        if ("Swordsman".equals(heroDef.role) && rng.nextDouble() < 0.15) {
            damage = (int) (damage * 1.5);
            hero.restoreEnergy((int) (hero.maxEnergy * 0.05));
        }

        // Archer passive — Hunter's Instinct: +20% damage below 30% enemy HP
        if ("Archer".equals(heroDef.role) && (double) enemy.currentHp / enemy.maxHp < 0.3) {
            damage = (int) (damage * 1.2);
        }

        enemy.takeDamage(damage);   // Combatant.takeDamage() — clamps at 0

        // Cooldown management
        if (isUlt) {
            hero.specialCooldown = heroDef.skills[2].cooldown;
        } else {
            hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);
        }

        // Weapon on-hit effects (lifesteal, poison, stun, etc.)
        String weaponFx = null;
        if (hero.inventory != null && hero.inventory.getEquippedWeapon() != null) {
            List<String> effectLogs = hero.inventory.getEquippedWeapon().applyEffects(hero, enemy, damage);
            if (!effectLogs.isEmpty()) weaponFx = String.join(" | ", effectLogs);
        }

        String msg = hero.name + " uses " + skill.name + " for " + damage + " damage!";
        return new ActionResult(msg, damage, 0, isUlt, false, weaponFx);
    }

    // ── Skip / rest ───────────────────────────────────────────────────────────

    private ActionResult resolveSkipTurn() {
        // Rest: recover 10% HP and a flat 15 energy, enter guard stance
        hero.heal((int) (hero.maxHp * 0.10));       // Combatant.heal() — clamps at maxHp
        hero.restoreEnergy(15);                      // Combatant.restoreEnergy() — clamps at maxEnergy
        hero.defending = true;
        hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);

        return new ActionResult(
                hero.name + " rests, recovering HP and Energy.",
                0, 0, false, true, null);
    }

    // =========================================================================
    // ENEMY TURN
    // =========================================================================

    public ActionResult enemyTurn() {
        if (currentTurn != TurnOwner.ENEMY) return null;

        tickModifiers(enemy);
        hero.defending = false;

        // Stun / freeze skips the enemy's action
        if (enemy.stunned || enemy.frozen) {
            enemy.stunned = false;
            enemy.frozen  = false;
            advanceRound();
            return new ActionResult(
                    enemy.name + " is incapacitated! Turn skipped.",
                    0, 0, false, false, "STUNNED");
        }

        lastEnemySkillName = resolveEnemySkillName();

        int damage = DamageCalculator.calculateDamage(enemy, hero, 1.0, false);
        hero.takeDamage(damage);    // Combatant.takeDamage() — clamps at 0

        // DoT that was applied TO the enemy ticks at end of enemy turn
        int dot = DamageCalculator.applyDoT(enemy);
        advanceRound();

        return new ActionResult(
                enemy.name + " uses " + lastEnemySkillName + " for " + damage + " damage!",
                damage, dot, false, false, null);
    }

    // ── Enemy skill name table ────────────────────────────────────────────────

    private String resolveEnemySkillName() {
        return switch (enemy.name) {
            case "Rotfang Wolf"     -> "Savage Howl";
            case "Shade Sprite"     -> "Trickster Strike";
            case "Dreadbark Treant" -> "Root Snare";
            case "Carrion Bat"      -> "Screech";
            case "Plague Vermin"    -> "Plague Bite";
            case "Forsaken Cultist" -> "Shadow Bolt";
            case "Blight Hound"     -> "Corpse Explosion";
            case "Ghoul Footman"    -> "Rotten Cleave";
            case "The Hollow Stag"  -> rng.nextBoolean() ? "Deathly Charge"    : "Blackened Howl";
            case "The Black Jailer" -> rng.nextBoolean() ? "Tormenting Lash"   : "Shackling Chains";
            case "Luther Von"       -> switch (rng.nextInt(3)) {
                case 0  -> "Crown of Despair";
                case 1  -> "Dark Ascension";
                default -> "Kings Wrath";
            };
            default -> "Attack";
        };
    }

    // =========================================================================
    // ROUND MANAGEMENT
    // =========================================================================

    private void advanceRound() {
        round++;
        currentTurn = TurnOwner.PLAYER;
        if (hero.specialCooldown > 0) hero.specialCooldown--;
    }

    public void advanceToEnemyTurn() { currentTurn = TurnOwner.ENEMY; }

    // =========================================================================
    // OUTCOME
    // =========================================================================

    public BattleOutcome checkOutcome() {
        if (!enemy.isAlive()) return BattleOutcome.VICTORY;
        if (!hero.isAlive())  return BattleOutcome.DEFEAT;
        return BattleOutcome.ONGOING;
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /** Ticks down temporary attack/defense modifiers by 1 each turn (min 0). */
    private void tickModifiers(Combatant c) {
        if (c.attackModifier  > 0) c.attackModifier--;
        if (c.defenseModifier > 0) c.defenseModifier--;
    }

    /** Mage passive — Arcane Flow: restores 5% mana at the start of each player turn. */
    private void applyMagePassive() {
        if (heroDef != null && "Mage".equals(heroDef.role)) {
            hero.restoreEnergy((int) (hero.maxEnergy * 0.05));  // Combatant.restoreEnergy()
        }
    }
}