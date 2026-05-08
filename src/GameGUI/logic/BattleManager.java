package GameGUI.logic;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;
import GameGUI.model.entity.HeroData.HeroDefinition;
import GameGUI.model.logic.StatusManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BattleManager — Controls turn order, action resolution, and battle outcome.
 * Refactored to isolate math from GUI timing to prevent visual desyncs.
 */
public class BattleManager {

    public enum BattleAction  { SKILL1, SKILL2, ULTIMATE, SKIP_TURN }
    public enum TurnOwner     { PLAYER, ENEMY }
    public enum BattleOutcome { ONGOING, VICTORY, DEFEAT }

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

    private final Combatant      hero, enemy;
    private final HeroDefinition heroDef;
    private final Random         rng = new Random();
    private final HeroData.EnemyDefinition enemyDef;

    private TurnOwner currentTurn      = TurnOwner.PLAYER;
    private int       round            = 1;
    private boolean   phoenixAvailable;
    private String    lastEnemySkillName = "";

    private final int s1Cost, s2Cost, ultCost;

    public BattleManager(Combatant hero, Combatant enemy, HeroDefinition heroDef, HeroData.EnemyDefinition enemyDef, boolean hasPhoenix) {
        this.enemyDef         = enemyDef;
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

    public Combatant  getHero()               { return hero;               }
    public Combatant  getEnemy()              { return enemy;              }
    public int        getRound()              { return round;              }
    public TurnOwner  getCurrentTurn()        { return currentTurn;        }
    public String     getLastEnemySkillName() { return lastEnemySkillName; }

    public boolean canUseSkill1()   { return hero.energy >= s1Cost; }
    public boolean canUseSkill2()   { return hero.energy >= s2Cost; }
    public boolean canUseUltimate() { return hero.energy >= ultCost && hero.specialCooldown == 0; }

    // =========================================================================
    // PLAYER TURN
    // =========================================================================

    /**
     * Checks for status effects and pre-turn updates at the start of the player sequence.
     */
    public ActionResult startPlayerTurnCheck() {
        List<String> logs = new ArrayList<>();

        // 1. Pre-Turn Checks (Buffs and Passives)
        hero.getStatusManager().updateStatModifiers(logs);
        applyMagePassive();

        // 2. Check Hard CC (Stun/Freeze)
        if (hero.getStatusManager().checkHardCC(logs)) {
            hero.getStatusManager().updateDoTEffects(logs);
            advanceToEnemyTurn(); // Instantly skips player turn in the engine
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "STUNNED");
        }

        return new ActionResult(String.join("\n", logs), 0, 0, false, false, null);
    }

    /**
     * Resolves the player's chosen action and applies post-turn DoTs.
     */
    public ActionResult playerAction(BattleAction action) {
        if (currentTurn != TurnOwner.PLAYER) return null;
        List<String> logs = new ArrayList<>();

        // 1. Check Soft CC (Confusion check)
        if (hero.getStatusManager().checkConfuse(logs)) {
            hero.getStatusManager().updateDoTEffects(logs);
            // Turn is still considered "used" even if confused
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "CONFUSED");
        }

        // 2. Resolve Action
        ActionResult result = switch (action) {
            case SKILL1    -> resolveAttack(0, s1Cost,  false, logs);
            case SKILL2    -> resolveAttack(1, s2Cost,  false, logs);
            case ULTIMATE  -> resolveAttack(2, ultCost, true, logs);
            case SKIP_TURN -> resolveSkipTurn(logs);
        };

        // 3. Post-Turn DoT
        hero.getStatusManager().updateDoTEffects(logs);

        // Bundle everything into a single string for the GUI
        String combinedLogs = result.logMessage;
        if (!logs.isEmpty()) {
            combinedLogs += "\n" + String.join("\n", logs);
        }

        return new ActionResult(combinedLogs.trim(), result.damageDealt, 0,
                result.isSpecial, result.wasDefend, result.statusEffect);
    }

    private ActionResult resolveAttack(int skillIndex, int cost, boolean isUlt, List<String> logs) {
        var skill = heroDef.skills[skillIndex];
        hero.spendEnergy(cost);

        double mult;
        if (skill.name.equals("Bullseye")) {
            mult = skill.maxMultiplier * 1.5; // Guaranteed crit for Karl
            logs.add("🎯 Bullseye! Guaranteed Critical Hit!");
        } else {
            mult = skill.minMultiplier + (skill.maxMultiplier - skill.minMultiplier) * rng.nextDouble();
        }

        int damage = DamageCalculator.calculateDamage(hero, enemy, mult, skill.pierceArmor);

        // Class Passives
        if ("Swordsman".equals(heroDef.role) && rng.nextDouble() < 0.15) {
            damage = (int) (damage * 1.5);
            hero.restoreEnergy((int) (hero.maxEnergy * 0.05));
            logs.add("⚡ Blade Swift! Critical Hit + Stamina Restored.");
        }
        if ("Archer".equals(heroDef.role) && (double) enemy.currentHp / enemy.maxHp < 0.3) {
            damage = (int) (damage * 1.2);
        }

        enemy.takeDamage(damage);

        if (isUlt) hero.specialCooldown = heroDef.skills[2].cooldown;
        else hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);

        // Apply Weapon Effects
        if (hero.inventory != null && hero.inventory.getEquippedWeapon() != null) {
            List<String> effectLogs = hero.inventory.getEquippedWeapon().applyEffects(hero, enemy, damage);
            logs.addAll(effectLogs);
        }

        // Skill-Specific Status Effects
        applySkillStatusEffects(skill.name, logs);

        String msg = hero.name + " uses " + skill.name + " for " + damage + " damage!";
        return new ActionResult(msg, damage, 0, isUlt, false, null);
    }

    private void applySkillStatusEffects(String skillName, List<String> logs) {
        StatusManager enemySM = enemy.getStatusManager();
        StatusManager heroSM = hero.getStatusManager();

        switch (skillName) {
            case "Blade Rush" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyStrengthen((int)(hero.attack * 0.20), 2, logs);
            }
            case "Piercing Slash" -> {
                if (rng.nextDouble() < 0.30) enemySM.applyStun(logs);
            }
            case "Eternal Cross Slash" -> {
                enemySM.applyBleed(2, logs);
                heroSM.applyFortify((int)(hero.defense * 0.20), 2, logs);
            }
            case "Piercing Arrow" -> {
                if (rng.nextDouble() < 0.30) enemySM.applyBleed(2, logs);
            }
            case "Bullseye" -> {
                if (rng.nextDouble() < 0.30) enemySM.applyFragile((int)(enemy.defense * 0.30), 2, logs);
            }
            case "Rain of a Thousand Arrows" -> {
                heroSM.applyStrengthen((int)(hero.attack * 0.20), 2, logs);
            }
            case "Fireball" -> {
                enemySM.applyBurn(1, logs);
                if (rng.nextDouble() < 0.30) enemySM.applyWeaken((int)(enemy.attack * 0.15), 2, logs);
            }
            case "Ice Prison" -> {
                if (rng.nextDouble() < 0.30) {
                    enemySM.applyFreeze(logs);
                    enemySM.applyFragile((int)(enemy.defense * 0.15), 1, logs);
                }
            }
            case "Meteor Storm" -> {
                heroSM.applyStrengthen((int)(hero.attack * 0.20), 2, logs);
                if (rng.nextDouble() < 0.50) enemySM.applyBurn(2, logs);
            }
        }
    }

    private ActionResult resolveSkipTurn(List<String> logs) {
        hero.heal((int) (hero.maxHp * 0.10));
        hero.restoreEnergy(15);
        hero.defending = true;
        hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);
        return new ActionResult(hero.name + " rests, recovering HP and Energy.", 0, 0, false, true, null);
    }

    // =========================================================================
    // ENEMY TURN
    // =========================================================================

    /**
     * Executes the enemy action. Turn advancing is handled by the GUI to maintain sync.
     */
    public ActionResult enemyTurn() {
        if (currentTurn != TurnOwner.ENEMY) return null;
        List<String> logs = new ArrayList<>();

        enemy.getStatusManager().updateStatModifiers(logs);
        hero.defending = false;

        // Check Hard CC (Stun/Freeze)
        if (enemy.getStatusManager().checkHardCC(logs)) {
            enemy.getStatusManager().updateDoTEffects(logs);
            advanceRound(); // Engine moves to next round state
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "STUNNED");
        }

        // Check Soft CC (Confusion)
        if (enemy.getStatusManager().checkConfuse(logs)) {
            enemy.getStatusManager().updateDoTEffects(logs);
            advanceRound();
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "CONFUSED");
        }

        lastEnemySkillName = resolveEnemySkillName();
        double mult = enemyDef.minMultiplier +
                (enemyDef.maxMultiplier - enemyDef.minMultiplier) + rng.nextDouble();
        int damage = DamageCalculator.calculateDamage(enemy, hero, mult, false);
        hero.takeDamage(damage);

        enemy.getStatusManager().updateDoTEffects(logs);
        advanceRound();

        String msg = enemy.name + " uses " + lastEnemySkillName + " for " + damage + " damage!";
        if (!logs.isEmpty()) msg += "\n" + String.join("\n", logs);

        return new ActionResult(msg, damage, 0, false, false, null);
    }

    private String resolveEnemySkillName() {
        return switch (enemy.name) {
            case "Rotfang Wolf"        -> "Savage Howl";
            case "Shade Sprite"        -> "Trickster Strike";
            case "Dreadbark Treant"    -> "Root Snare";
            case "Carrion Bat"         -> "Screech";
            case "Plague Vermin"       -> "Plague Bite";
            case "Forsaken Cultist"    -> "Shadow Bolt";
            case "Blight Hound"        -> "Corpse Explosion";
            case "Ghoul Footman"       -> "Rotten Cleave";
            case "The Hollow Stag"     -> rng.nextBoolean() ? "Deathly Charge"  : "Blackened Howl";
            case "The Black Jailer"    -> rng.nextBoolean() ? "Tormenting Lash" : "Shackling Chains";
            case "Luther Von"          -> switch (rng.nextInt(3)) {
                case 0  -> "Crown of Despair";
                case 1  -> "Dark Ascension";
                default -> "Kings Wrath";
            };
            case "Zyrryl"              -> rng.nextBoolean() ? "Great Cleaver" : "Bone Shield";
            case "Khai the Necromancer" -> switch (rng.nextInt(3)) {
                case 0  -> "Soul Drain";
                case 1  -> "Encapsulation";
                default -> "Dark Ascension";
            };
            default -> "Attack";
        };
    }

    /**
     * Closes the current turn and prepares for the next round.
     */
    public void advanceRound() {
        round++;
        currentTurn = TurnOwner.PLAYER;
        if (hero.specialCooldown > 0) hero.specialCooldown--;
    }

    public void advanceToEnemyTurn() { currentTurn = TurnOwner.ENEMY; }

    public BattleOutcome checkOutcome() {
        if (!enemy.isAlive()) {
            boolean isMiniBoss = enemy.name.equals("The Hollow Stag") ||
                                 enemy.name.equals("The Black Jailer") ||
                                 enemy.name.equals("Luther Von") ||
                                 enemy.name.equals("Zyrryl") ||
                                 enemy.name.equals("Khai the Necromancer");

            hero.inventory.lootPotions(isMiniBoss);
            return BattleOutcome.VICTORY;
        }
        if (!hero.isAlive())  return BattleOutcome.DEFEAT;
        return BattleOutcome.ONGOING;
    }

    private void applyMagePassive() {
        if (heroDef != null && "Mage".equals(heroDef.role)) {
            hero.restoreEnergy((int) (hero.maxEnergy * 0.05));
        }
    }
}