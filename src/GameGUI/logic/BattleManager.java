package GameGUI.logic;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.EnemyData;
import GameGUI.model.entity.data.HeroData;
import GameGUI.model.entity.data.definition.HeroDefinition;
import GameGUI.model.logic.StatusManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BattleManager — Controls turn order, action resolution, and battle outcome.
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
    private final EnemyData enemyDef;
    private final FinalBossManager finalBossManager;

    private TurnOwner currentTurn      = TurnOwner.PLAYER;
    private int       round            = 1;
    private boolean   phoenixAvailable;
    private String    lastEnemySkillName = "";

    private final int s1Cost, s2Cost, ultCost;

    public BattleManager(Combatant hero, Combatant enemy, HeroDefinition heroDef, EnemyData enemyDef, boolean hasPhoenix) {
        this.enemyDef         = enemyDef;
        this.hero             = hero;
        this.enemy            = enemy;
        this.heroDef          = heroDef;
        this.phoenixAvailable = hasPhoenix;

        if (enemy.name.equals("Khai the Necromancer")) {
            this.finalBossManager = new FinalBossManager(enemy, hero);
        } else {
            this.finalBossManager = null;
        }

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

    public boolean canUseSkill1()   { return hero.getEnergy() >= s1Cost; }
    public boolean canUseSkill2()   { return hero.getEnergy() >= s2Cost; }
    public boolean canUseUltimate() { return hero.getEnergy() >= ultCost && hero.getSpecialCooldown() == 0; }

    // =========================================================================
    // PLAYER TURN
    // =========================================================================

    public ActionResult startPlayerTurnCheck() {
        List<String> logs = new ArrayList<>();

        hero.getStatusManager().updateStatModifiers(logs);
        applyMagePassive();

        if (hero.getStatusManager().checkHardCC(logs)) {
            hero.getStatusManager().updateDoTEffects(logs);
            advanceToEnemyTurn();
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "STUNNED");
        }

        return new ActionResult(String.join("\n", logs), 0, 0, false, false, null);
    }

    public ActionResult playerAction(BattleAction action) {
        if (currentTurn != TurnOwner.PLAYER) return null;
        List<String> logs = new ArrayList<>();

        if (hero.getStatusManager().checkConfuse(logs)) {
            hero.getStatusManager().updateDoTEffects(logs);
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "CONFUSED");
        }

        ActionResult result = switch (action) {
            case SKILL1    -> resolveAttack(0, s1Cost,  false, logs);
            case SKILL2    -> resolveAttack(1, s2Cost,  false, logs);
            case ULTIMATE  -> resolveAttack(2, ultCost, true, logs);
            case SKIP_TURN -> resolveSkipTurn(logs);
        };

        hero.getStatusManager().updateDoTEffects(logs);

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

        int totalDamage = 0;
        boolean loggedBladeSwift = false;

        // Loop for multi-hit skills
        for (int i = 0; i < skill.hitCount; i++) {
            double mult;
            if (skill.name.equals("Bullseye")) {
                mult = skill.maxMultiplier * 1.5;
                if (i == 0) logs.add("🎯 Bullseye! Guaranteed Critical Hit!");
            } else {
                mult = skill.minMultiplier + (skill.maxMultiplier - skill.minMultiplier) * rng.nextDouble();
            }

            int hitDamage = DamageCalculator.calculateDamage(hero, enemy, mult, skill.pierceArmor);

            // Apply Swordsman Passive per hit
            if ("Swordsman".equals(heroDef.role) && rng.nextDouble() < 0.15) {
                hitDamage = (int) (hitDamage * 1.5);
                hero.restoreEnergy((int) (hero.getMaxEnergy() * 0.05));
                if (!loggedBladeSwift) {
                    logs.add("⚡ Blade Swift! Critical Hit + Stamina Restored.");
                    loggedBladeSwift = true; // Prevent log spam on multi-hits
                }
            }

            // Apply Archer Passive per hit
            if ("Archer".equals(heroDef.role) && (double) enemy.getCurrentHp() / enemy.getMaxHp() < 0.3) {
                hitDamage = (int) (hitDamage * 1.2);
            }

            totalDamage += hitDamage;
        }

        // Process total accumulated damage
        if (finalBossManager != null) {
            finalBossManager.processIncomingDamage(totalDamage, logs);
        } else {
            enemy.takeDamage(totalDamage);
        }

        if (isUlt) hero.setSpecialCooldown(heroDef.skills[2].cooldown);
        else hero.setSpecialCooldown(Math.max(0, hero.getSpecialCooldown() - 1));

        if (hero.inventory != null && hero.inventory.getEquippedWeapon() != null) {
            List<String> effectLogs = hero.inventory.getEquippedWeapon().applyEffects(hero, enemy, totalDamage);
            logs.addAll(effectLogs);
        }

        applySkillStatusEffects(skill.name, logs);

        String msg = hero.name + " uses " + skill.name + " for " + totalDamage + " damage"
                + (skill.hitCount > 1 ? " (" + skill.hitCount + " hits)!" : "!");

        return new ActionResult(msg, totalDamage, 0, isUlt, false, null);
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
        hero.heal((int) (hero.getMaxHp() * 0.10));
        hero.restoreEnergy(15);
        hero.setSpecialCooldown(Math.max(0, hero.getSpecialCooldown() - 1));
        return new ActionResult(hero.name + " rests, recovering HP and Energy.", 0, 0, false, true, null);
    }

    // =========================================================================
    // ENEMY TURN
    // =========================================================================

    public ActionResult enemyTurn() {
        if (currentTurn != TurnOwner.ENEMY) return null;
        List<String> logs = new ArrayList<>();

        if (finalBossManager != null) {
            finalBossManager.checkUnbrokenShield(logs);
        }

        enemy.getStatusManager().updateStatModifiers(logs);

        if (enemy.getStatusManager().checkHardCC(logs)) {
            enemy.getStatusManager().updateDoTEffects(logs);
            advanceRound();
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "STUNNED");
        }

        if (enemy.getStatusManager().checkConfuse(logs)) {
            enemy.getStatusManager().updateDoTEffects(logs);
            advanceRound();
            return new ActionResult(String.join("\n", logs), 0, 0, false, false, "CONFUSED");
        }

        lastEnemySkillName = resolveEnemySkillName();

        double mult = 0.0;
        boolean pierce = false;

        if (lastEnemySkillName.equals("Crown of Despair") ||
                lastEnemySkillName.equals("Bone Shield") ||
                lastEnemySkillName.equals("Encapsulation")) {
            mult = 0.0;
        } else {
            mult = enemyDef.getMinMultiplier() +
                    ((enemyDef.getMaxMultiplier() - enemyDef.getMinMultiplier()) * rng.nextDouble());
        }

        if (lastEnemySkillName.equals("Grave Cleaver")) {
            pierce = true;
        }
        ;
        int damage = DamageCalculator.calculateDamage(enemy, hero, mult, pierce);
        if (damage > 0) {
            hero.takeDamage(damage);
        }

        applyEnemySkillEffects(lastEnemySkillName, damage, logs);

        enemy.getStatusManager().updateDoTEffects(logs);
        advanceRound();

        String msg;
        if (damage > 0) {
            msg = enemy.name + " uses " + lastEnemySkillName + " for " + damage + " damage!";
        } else {
            msg = enemy.name + " uses " + lastEnemySkillName + "!";
        }

        if (!logs.isEmpty()) msg += "\n" + String.join("\n", logs);

        return new ActionResult(msg, damage, 0, false, false, null);
    }

    private String resolveEnemySkillName() {
        StatusManager heroSM = hero.getStatusManager();
        StatusManager enemySM = enemy.getStatusManager();

        return switch (enemy.name) {
            case "Rotfang Wolf"        -> "Savage Howl";
            case "Shade Sprite"        -> "Trickster Strike";
            case "Dreadbark Treant"    -> "Root Snare";
            case "Carrion Bat"         -> "Screech";
            case "Plague Vermin"       -> "Plague Bite";
            case "Forsaken Cultist"    -> "Shadow Bolt";
            case "Blight Hound"        -> "Corpse Explosion";
            case "Ghoul Footman"       -> "Rotten Cleave";
            case "Flame Revenant"      -> "Ember Burst";
            case "Bone Warlock"        -> "Marrow Bolt";
            case "Obsidian Crusher"    -> "Magma Slam";
            case "Soulflayer Gargoyle" -> "Soul Scream";

            // ERROR FIXES: Uses StatusManager.Effect enum instead of boolean methods
            case "The Hollow Stag" -> {
                if (!heroSM.has(StatusManager.Effect.FRAGILE)) yield "Blackened Howl";
                else yield "Deathly Charge";
            }
            case "The Black Jailer" -> rng.nextBoolean() ? "Tormenting Lash" : "Shackling Chains";
            case "Luther Von" -> {
                if (!heroSM.has(StatusManager.Effect.WEAKENED)) {
                    yield rng.nextBoolean() ? "Crown of Despair" : "Dark Judgement";
                } else {
                    yield (rng.nextDouble() < 0.66) ? "Dark Judgement" : "Kings Wrath";
                }
            }
            case "Zyrryl" -> {
                if (!enemySM.has(StatusManager.Effect.FORTIFIED)) yield "Bone Shield";
                else yield "Grave Cleaver";
            }
            case "Khai the Necromancer" -> finalBossManager.determineNextSkill();
            default -> "Attack";
        };
    }

    public void advanceRound() {
        round++;
        currentTurn = TurnOwner.PLAYER;
        if (hero.getSpecialCooldown() > 0) hero.setSpecialCooldown(hero.getSpecialCooldown() - 1);
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
            hero.restoreEnergy((int) (hero.getMaxEnergy() * 0.05));
        }
    }

    private void applyEnemySkillEffects(String skillName, int damageDealt, List<String> logs) {
        StatusManager heroSM = hero.getStatusManager();
        StatusManager enemySM = enemy.getStatusManager();

        switch (skillName) {
            case "Trickster Strike" -> {
                // ERROR FIX: applyConfuse takes (int turns, List<String> logs)
                if (rng.nextDouble() < 0.30) heroSM.applyConfuse(1, logs);
            }
            case "Root Snare" -> {
                if (rng.nextDouble() < 0.20) heroSM.applyStun(logs);
            }
            case "Screech" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyWeaken((int)(hero.attack * 0.20), 2, logs);
            }
            case "Deathly Charge" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyStun(logs);
            }
            case "Blackened Howl" -> {
                heroSM.applyFragile((int)(hero.defense * 0.20), 2, logs);
            }
            case "Plague Bite" -> {
                heroSM.applyPoison(1, logs);
            }
            case "Shadow Bolt" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyWeaken((int)(hero.attack * 0.20), 2, logs);
            }
            case "Corpse Explosion" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyFragile((int)(hero.defense * 0.30), 2, logs);
            }
            case "Rotten Cleave", "Tormenting Lash" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyBleed(2, logs);
            }
            case "Shackling Chains" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyStun(logs);
            }
            case "Crown of Despair" -> {
                heroSM.applyWeaken((int)(hero.attack * 0.20), 2, logs);
            }
            case "Kings Wrath" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyStun(logs);
            }
            case "Ember Burst" -> {
                heroSM.applyBurn(1, logs);
            }
            case "Marrow Bolt" -> {
                if (rng.nextDouble() < 0.30) heroSM.applyWeaken((int)(hero.attack * 0.30), 2, logs);
            }
            case "Magma Slam" -> {
                if (rng.nextDouble() < 0.20) heroSM.applyStun(logs);
            }
            case "Soul Scream" -> {
                // ERROR FIX: applyConfuse takes (int turns, List<String> logs)
                if (rng.nextDouble() < 0.50) heroSM.applyConfuse(1, logs);
            }
            case "Bone Shield" -> {
                enemySM.applyFortify(50, 2, logs);
            }
            case "Soul Drain" -> {
                if (finalBossManager != null) {
                    finalBossManager.executeSoulDrain(damageDealt, logs);
                } else {
                    enemy.heal(damageDealt);
                    logs.add("🩸 Khai drains your essence, healing for " + damageDealt + " HP!");
                }
            }
            case "Encapsulation" -> {
                if (finalBossManager != null) {
                    finalBossManager.executeEncapsulation(logs);
                }
            }
            case "Dark Ascension" -> {
                if (finalBossManager != null) {
                    finalBossManager.executeDarkAscension(logs);
                } else {
                    if (rng.nextDouble() < 0.30) {
                        heroSM.applyWeaken((int)(hero.attack * 0.30), 2, logs);
                        logs.add("😱 You are paralyzed by Fear! (ATK Decreased)");
                    }
                }
            }
        }
    }
}