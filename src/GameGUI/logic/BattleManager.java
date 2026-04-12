package GameGUI.logic;

import GameGUI.model.Combatant;
import GameGUI.model.HeroData.HeroDefinition;
import GameGUI.model.HeroData.WeaponDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BattleManager — Controls the flow of the battle, turn order, and action resolution.
 */
public class BattleManager {
    public enum BattleAction { SKILL1, SKILL2, ULTIMATE, SKIP_TURN }
    public enum TurnOwner { PLAYER, ENEMY }
    public enum BattleOutcome { ONGOING, VICTORY, DEFEAT }

    public static class ActionResult {
        public final String logMessage;
        public final int damageDealt, dotDamageApplied;
        public final boolean isSpecial, wasDefend;
        public final String statusEffect;

        public ActionResult(String logMessage, int damageDealt, int dotDamageApplied, boolean isSpecial, boolean wasDefend, String statusEffect) {
            this.logMessage = logMessage; this.damageDealt = damageDealt; this.dotDamageApplied = dotDamageApplied;
            this.isSpecial = isSpecial; this.wasDefend = wasDefend; this.statusEffect = statusEffect;
        }
    }

    private final Combatant hero, enemy;
    private TurnOwner currentTurn = TurnOwner.PLAYER;
    private int round = 1;
    private boolean phoenixAvailable;

    private HeroDefinition heroDef;
    private WeaponDef weaponDef;
    private int s1Cost = 5, s2Cost = 10, ultCost = 20;
    private String lastEnemySkillName = "";

    private final Random rng = new Random();

    public BattleManager(Combatant hero, Combatant enemy, HeroDefinition heroDef, boolean hasPhoenix) {
        this.hero = hero;
        this.enemy = enemy;
        this.heroDef = heroDef;
        this.weaponDef = heroDef != null ? heroDef.startingWeapon : null;
        this.phoenixAvailable = hasPhoenix;

        if (heroDef != null && heroDef.skills != null && heroDef.skills.length >= 3) {
            s1Cost = heroDef.skills[0].energyCost;
            s2Cost = heroDef.skills[1].energyCost;
            ultCost = heroDef.skills[2].energyCost;
        }
    }

    public Combatant getHero() { return hero; }
    public Combatant getEnemy() { return enemy; }
    public int getRound() { return round; }
    public TurnOwner getCurrentTurn() { return currentTurn; }
    public String getLastEnemySkillName() { return lastEnemySkillName; }

    public boolean canUseSkill1() { return hero.energy >= s1Cost; }
    public boolean canUseSkill2() { return hero.energy >= s2Cost; }
    public boolean canUseUltimate() { return hero.energy >= ultCost && hero.specialCooldown == 0; }


    public ActionResult playerAction(BattleAction action) {
        if (currentTurn != TurnOwner.PLAYER) return null;
        updateModifiers(hero);

        // Mage passive: Arcane Flow
        if (heroDef != null && "Mage".equals(heroDef.role)) {
            hero.energy = Math.min(hero.maxEnergy, hero.energy + (int)(hero.maxEnergy * 0.05));
        }

        ActionResult result = switch (action) {
            case SKILL1 -> resolveAttack(heroDef.skills[0].name, heroDef.skills[0].minMultiplier, heroDef.skills[0].maxMultiplier, heroDef.skills[0].pierceArmor, s1Cost, false);
            case SKILL2 -> resolveAttack(heroDef.skills[1].name, heroDef.skills[1].minMultiplier, heroDef.skills[1].maxMultiplier, heroDef.skills[1].pierceArmor, s2Cost, false);
            case ULTIMATE -> resolveAttack(heroDef.skills[2].name, heroDef.skills[2].minMultiplier, heroDef.skills[2].maxMultiplier, heroDef.skills[2].pierceArmor, ultCost, true);
            case SKIP_TURN -> resolveSkipTurn();
        };

        int dot = DamageCalculator.applyDoT(hero);
        return new ActionResult(result.logMessage, result.damageDealt, dot, result.isSpecial, result.wasDefend, result.statusEffect);
    }

    private ActionResult resolveAttack(String skillName, double minMult, double maxMult, boolean pierce, int cost, boolean isUlt) {
        hero.energy -= cost;

        // Calculate a random multiplier between min and max
        double actualMult = minMult + (maxMult - minMult) * rng.nextDouble();
        int damage = DamageCalculator.calculateDamage(hero, enemy, actualMult, pierce);

        // Swordsman passive (Crit)
        if ("Swordsman".equals(heroDef.role) && rng.nextDouble() < 0.15) {
            damage = (int)(damage * 1.5);
            hero.energy = Math.min(hero.maxEnergy, hero.energy + (int)(hero.maxEnergy * 0.05));
        }

        // Archer passive (Execute)
        if ("Archer".equals(heroDef.role) && ((double)enemy.currentHp / enemy.maxHp < 0.3)) {
            damage = (int)(damage * 1.2);
        }

        enemy.currentHp = Math.max(0, enemy.currentHp - damage);

        if (isUlt) {
            hero.specialCooldown = heroDef.skills[2].cooldown;
        } else {
            hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);
        }

        String weaponFx = DamageCalculator.applyWeaponEffects(hero, enemy, hero.inventory.getEquippedWeapon(), damage);
        String msg = hero.name + " uses " + skillName + " for " + damage + " damage!";
        return new ActionResult(msg, damage, 0, isUlt, false, weaponFx);
    }

    private ActionResult resolveSkipTurn() {
        int hpRestored = (int)(hero.maxHp * 0.10);
        hero.currentHp = Math.min(hero.maxHp, hero.currentHp + hpRestored);
        hero.energy = Math.min(hero.maxEnergy, hero.energy + 15);
        hero.defending = true;
        hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);
        return new ActionResult(hero.name + " rests, recovering HP and Energy.", 0, 0, false, true, null);
    }

    public ActionResult enemyTurn() {
        if (currentTurn != TurnOwner.ENEMY) return null;
        updateModifiers(enemy);
        hero.defending = false;

        if (enemy.stunned || enemy.frozen) {
            enemy.stunned = false; enemy.frozen = false;
            advanceRound();
            return new ActionResult(enemy.name + " is incapacitated! Turn skipped.", 0, 0, false, false, "STUNNED");
        }

        // Pick skill name for enemies that have named attacks
        lastEnemySkillName = switch (enemy.name) {
            case "Rotfang Wolf"      -> "Savage Howl";
            case "Shade Sprite"      -> "Trickster Strike";
            case "Dreadbark Treant"  -> "Root Snare";
            case "Carrion Bat"       -> "Screech";
            case "Plague Vermin"     -> "Plague Bite";
            case "Forsaken Cultist"  -> "Shadow Bolt";
            case "Blight Hound"      -> "Corpse Explosion";
            case "Ghoul Footman"     -> "Rotten Cleave";
            case "The Hollow Stag"   -> rng.nextBoolean() ? "Deathly Charge" : "Blackened Howl";
            case "The Black Jailer"  -> rng.nextBoolean() ? "Tormenting Lash" : "Shackling Chains";
            case "Luther Von" -> {
                int roll = rng.nextInt(3);
                yield roll == 0 ? "Crown of Despair" : roll == 1 ? "Dark Ascension" : "Kings Wrath";
            }
            default                  -> "Attack";
        };

        int damage = DamageCalculator.calculateDamage(enemy, hero, 1.0, false);
        hero.currentHp = Math.max(0, hero.currentHp - damage);

        int dot = DamageCalculator.applyDoT(enemy);
        advanceRound();

        return new ActionResult(enemy.name + " uses " + lastEnemySkillName + " for " + damage + " damage!", damage, dot, false, false, null);
    }

    private void advanceRound() {
        round++;
        currentTurn = TurnOwner.PLAYER;
        if (hero.specialCooldown > 0) hero.specialCooldown--;
    }

    public void advanceToEnemyTurn() { currentTurn = TurnOwner.ENEMY; }

    public BattleOutcome checkOutcome() {
        if (!enemy.isAlive()) return BattleOutcome.VICTORY;
        if (!hero.isAlive()) return BattleOutcome.DEFEAT;
        return BattleOutcome.ONGOING;
    }

    private void updateModifiers(Combatant c) {
        if (c.attackModifier > 0) c.attackModifier--;
        if (c.defenseModifier > 0) c.defenseModifier--;
    }
}