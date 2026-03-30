package GameGUI.logic;

import GameGUI.model.HeroData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleLogic {

    // ─── Combatant ────────────────────────────────────────────────────────────
    public static class Combatant {
        public final String name, role, emoji;
        public final Special special;

        // Mutable stats (leveling modifies these)
        public int maxHp, attack, defense, maxEnergy;
        public int baseAttack, baseDefense;

        public int currentHp, energy;
        public boolean defending;
        public int specialCooldown;
        public int attackModifier, defenseModifier;
        public int dotDamage, dotTurnsLeft;

        // Leveling
        public int level = 1;
        public int exp   = 0;
        public int nextLevelExp;
        public String lastLevelUpMsg = null;

        private static final int[] XP_TABLE = {
                0, 100, 115, 130, 150, 175, 200, 230, 265, 305, 355,
                405, 465, 535, 615, 720, 875, 1040, 1350, 1650, 2200,
                2500, 2900, 3350, 3800, 4400, 5000, 5800, 6600, 7400
        };

        // Status effects
        public boolean stunned, frozen, confused, nimble;

        public Combatant(String name, String role, String emoji,
                         int maxHp, int attack, int defense,
                         int energy, int maxEnergy, Special special) {
            this.name = name; this.role = role; this.emoji = emoji;
            this.maxHp = maxHp;
            this.baseAttack  = attack; this.attack  = attack;
            this.baseDefense = defense; this.defense = defense;
            this.special = special;
            this.currentHp = maxHp;
            this.energy = energy; this.maxEnergy = maxEnergy;
            this.nextLevelExp = XP_TABLE.length > 1 ? XP_TABLE[1] : 100;
        }

        public boolean isAlive() { return currentHp > 0; }
        public int effectiveAttack()  { return Math.max(1, attack  + attackModifier); }
        public int effectiveDefense() { return Math.max(0, defense + defenseModifier); }

        /** Add XP and trigger level-ups. Returns true if leveled up. */
        public boolean gainExp(int amount) {
            if (level >= 30) return false;
            exp += amount;
            boolean leveled = false;
            while (level < 30 && level < XP_TABLE.length - 1 && exp >= nextLevelExp) {
                levelUp();
                leveled = true;
            }
            return leveled;
        }

        private void levelUp() {
            if (level >= 30) return;
            level++;
            int oldMaxHp  = maxHp;
            int oldAtk    = baseAttack;
            int oldDef    = baseDefense;
            int w = 1; // World 1
            switch (role) {
                case "Swordsman" -> { maxHp += 60 + w*5; baseAttack += 7+w; baseDefense += 3+w; }
                case "Archer"    -> { maxHp += 56 + w*5; baseAttack += 8+w; baseDefense += 2+w; }
                case "Mage"      -> { maxHp += 50 + w*5; baseAttack += 9+w; baseDefense += 1+w; }
                default          -> { maxHp += 55 + w*5; baseAttack += 7+w; baseDefense += 2+w; }
            }
            int weaponBonus = attack  - oldAtk  - attackModifier;
            int armorBonus  = defense - oldDef  - defenseModifier;
            attack  = baseAttack  + weaponBonus + attackModifier;
            defense = baseDefense + armorBonus  + defenseModifier;
            currentHp = Math.min(maxHp, currentHp + (int)(maxHp * 0.50));
            energy    = Math.min(maxEnergy, energy + (int)(maxEnergy * 0.50));
            exp -= nextLevelExp;
            if (level < XP_TABLE.length) nextLevelExp = XP_TABLE[level];
            // Store structured info for BattlePanel display
            int hpGain  = maxHp - oldMaxHp;
            int atkGain = baseAttack - oldAtk;
            int defGain = baseDefense - oldDef;
            lastLevelUpMsg = String.format(
                    "LVL_UP|%d|%d|%d|%d|%d|%d|%d|%d",
                    level, hpGain, maxHp, atkGain, attack, defGain, defense, maxEnergy
            );
        }
    }

    // ─── Special ─────────────────────────────────────────────────────────────
    public static class Special {
        public final String name, icon, description;
        public final double multiplier;
        public final boolean pierceArmor;
        public final int cooldown;

        public Special(String name, String icon, String description,
                       double multiplier, boolean pierceArmor, int cooldown) {
            this.name = name; this.icon = icon; this.description = description;
            this.multiplier = multiplier; this.pierceArmor = pierceArmor;
            this.cooldown = cooldown;
        }
    }

    public enum BattleAction { SKILL1, SKILL2, ULTIMATE, SKIP_TURN }
    public enum TurnOwner    { PLAYER, ENEMY }
    public enum BattleOutcome{ ONGOING, VICTORY, DEFEAT }

    // ─── ActionResult ────────────────────────────────────────────────────────
    public static class ActionResult {
        public final String logMessage;
        public final boolean wasDefend, isSpecial, isBerserk, isMultiHit;
        public final int damageDealt, dotDamageApplied;
        public final String target;
        public final String statusEffect; // e.g. "STUN", "BLEED", "BURN", null

        public ActionResult(String logMessage, boolean wasDefend, int damageDealt,
                            int dotDamageApplied, String target,
                            boolean isSpecial, boolean isBerserk,
                            boolean isMultiHit, String statusEffect) {
            this.logMessage = logMessage; this.wasDefend = wasDefend;
            this.damageDealt = damageDealt; this.dotDamageApplied = dotDamageApplied;
            this.target = target; this.isSpecial = isSpecial; this.isBerserk = isBerserk;
            this.isMultiHit = isMultiHit; this.statusEffect = statusEffect;
        }
    }

    // ─── Fields ───────────────────────────────────────────────────────────────
    private final Combatant hero, enemy;
    private TurnOwner currentTurn = TurnOwner.PLAYER;
    private int round = 1;
    private boolean reviveUsed = false;
    private boolean phoenixSoulstoneAvailable = false;
    private final List<String> battleLog = new ArrayList<>();
    private final Random rng = new Random();

    // Hero skill energy costs (set from HeroData on startBattle)
    private int skill1Cost = 5, skill2Cost = 10, ultimateCost = 20;
    private HeroData.HeroDefinition heroDef;
    private HeroData.WeaponDef weaponDef; // starting weapon — drives on-hit effects

    public BattleLogic(Combatant hero, Combatant enemy, boolean hasPhoenix) {
        this.hero = hero; this.enemy = enemy;
        this.phoenixSoulstoneAvailable = hasPhoenix;
    }

    public void setHeroDef(HeroData.HeroDefinition def) {
        this.heroDef = def;
        this.weaponDef = (def != null) ? def.startingWeapon : null;
        if (def != null && def.skills != null && def.skills.length >= 3) {
            skill1Cost   = def.skills[0].energyCost;
            skill2Cost   = def.skills[1].energyCost;
            ultimateCost = def.skills[2].energyCost;
        }
    }

    // ─── Getters ─────────────────────────────────────────────────────────────
    public Combatant getHero()  { return hero; }
    public Combatant getEnemy() { return enemy; }
    public TurnOwner getCurrentTurn() { return currentTurn; }
    public int getRound() { return round; }
    public List<String> getBattleLog() { return battleLog; }
    public boolean isReviveUsed() { return reviveUsed; }
    public boolean isPhoenixSoulstoneAvailable() { return phoenixSoulstoneAvailable; }
    public int getSkill1Cost()   { return skill1Cost; }
    public int getSkill2Cost()   { return skill2Cost; }
    public int getUltimateCost() { return ultimateCost; }
    public boolean canUseSkill1()   { return hero.energy >= skill1Cost; }
    public boolean canUseSkill2()   { return hero.energy >= skill2Cost; }
    public boolean canUseUltimate() { return hero.energy >= ultimateCost && hero.specialCooldown == 0; }

    // ─── Player Action ────────────────────────────────────────────────────────
    public ActionResult playerAction(BattleAction action) {
        if (currentTurn != TurnOwner.PLAYER) return null;
        updateModifiers(hero);

        // Passive: Arcane Flow — restore 5% mana each turn (Simon)
        if (heroDef != null && heroDef.role.equals("Mage")) {
            int flow = (int)(hero.maxEnergy * 0.05);
            hero.energy = Math.min(hero.maxEnergy, hero.energy + flow);
        }

        ActionResult result;
        switch (action) {
            case SKILL1   -> result = resolveSkill1();
            case SKILL2   -> result = resolveSkill2();
            case ULTIMATE -> result = resolveUltimate();
            case SKIP_TURN -> result = resolveSkipTurn();
            default       -> result = resolveSkill1();
        }

        int dot = applyDoT(hero);
        return new ActionResult(result.logMessage, result.wasDefend, result.damageDealt,
                dot, result.target, result.isSpecial, false, result.isMultiHit, result.statusEffect);
    }

    // ─── Skill 1 ─────────────────────────────────────────────────────────────
    private ActionResult resolveSkill1() {
        if (!canUseSkill1()) return insufficientEnergy("Skill 1");
        hero.energy -= skill1Cost;

        String heroName = heroDef != null ? heroDef.name : hero.name;
        String skillName = heroDef != null ? heroDef.skills[0].name : "Skill 1";
        double mult = heroDef != null ? heroDef.skills[0].multiplier : 1.15;
        boolean pierce = heroDef != null && heroDef.skills[0].pierceArmor;

        int dmg = calcDamage(hero, enemy, mult, pierce);

        // Passive: Blade Swift (Kael) — 15% crit chance gives +5% stamina
        if (heroDef != null && heroDef.role.equals("Swordsman") && rng.nextDouble() < 0.15) {
            dmg = (int)(dmg * 1.5);
            int gain = (int)(hero.maxEnergy * 0.05);
            hero.energy = Math.min(hero.maxEnergy, hero.energy + gain);
        }

        // Passive: Hunter's Instinct (Karl) — +20% if enemy < 30% HP
        if (heroDef != null && heroDef.role.equals("Archer")) {
            if ((double)enemy.currentHp / enemy.maxHp < 0.3) dmg = (int)(dmg * 1.2);
        }

        enemy.currentHp = clamp(enemy.currentHp - dmg, 0, enemy.maxHp);
        hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);

        // On-hit weapon effects (lifesteal, confuse, poison, energy)
        String weaponFx = applyWeaponEffects(dmg);

        // 30% chance status effect
        String status = null;
        if (rng.nextDouble() < 0.30) {
            if (heroDef != null && heroDef.role.equals("Swordsman")) {
                hero.attackModifier += (int)(hero.attack * 0.20); status = "STRENGTHEN";
            } else if (heroDef != null && heroDef.role.equals("Archer")) {
                enemy.dotDamage = Math.max(1, (int)(enemy.maxHp * 0.05));
                enemy.dotTurnsLeft += 2; status = "BLEED";
            } else if (heroDef != null && heroDef.role.equals("Mage")) {
                enemy.dotDamage = Math.max(1, (int)(enemy.maxHp * 0.04));
                enemy.dotTurnsLeft += 1; status = "BURN";
            }
        }

        String fullStatus = (status != null && weaponFx != null) ? status + ", " + weaponFx
                : (status != null) ? status : weaponFx;
        String msg = hero.name + " uses " + skillName + " for " + dmg + " damage!" +
                (fullStatus != null ? " (" + fullStatus + "!)" : "");
        log(msg);
        return new ActionResult(msg, false, dmg, 0, "enemy", false, false, false, fullStatus);
    }

    // ─── Skill 2 ─────────────────────────────────────────────────────────────
    private ActionResult resolveSkill2() {
        if (!canUseSkill2()) return insufficientEnergy("Skill 2");
        hero.energy -= skill2Cost;

        String skillName = heroDef != null ? heroDef.skills[1].name : "Skill 2";
        double mult = heroDef != null ? heroDef.skills[1].multiplier : 1.35;
        boolean pierce = heroDef != null && heroDef.skills[1].pierceArmor;

        int dmg = calcDamage(hero, enemy, mult, pierce);

        // Blade Swift passive for Kael skill 2
        if (heroDef != null && heroDef.role.equals("Swordsman") && rng.nextDouble() < 0.15) {
            dmg = (int)(dmg * 1.5);
            int gain = (int)(hero.maxEnergy * 0.05);
            hero.energy = Math.min(hero.maxEnergy, hero.energy + gain);
        }
        // Hunter's Instinct for Karl
        if (heroDef != null && heroDef.role.equals("Archer")) {
            if ((double)enemy.currentHp / enemy.maxHp < 0.3) dmg = (int)(dmg * 1.2);
            // Bullseye — guaranteed crit
            dmg = (int)(dmg * 1.5);
        }

        enemy.currentHp = clamp(enemy.currentHp - dmg, 0, enemy.maxHp);
        hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);

        // On-hit weapon effects
        String weaponFx2 = applyWeaponEffects(dmg);

        // 30% status
        String status = null;
        if (rng.nextDouble() < 0.30) {
            if (heroDef != null && heroDef.role.equals("Swordsman")) {
                enemy.stunned = true; status = "STUN";
            } else if (heroDef != null && heroDef.role.equals("Archer")) {
                enemy.defenseModifier -= (int)(enemy.defense * 0.30); status = "WEAKEN DEF";
            } else if (heroDef != null && heroDef.role.equals("Mage")) {
                if (rng.nextDouble() < 0.50) { enemy.frozen = true; status = "FREEZE"; }
            }
        }

        String fullStatus2 = (status != null && weaponFx2 != null) ? status + ", " + weaponFx2
                : (status != null) ? status : weaponFx2;
        String msg = hero.name + " uses " + skillName + " for " + dmg + " damage!" +
                (fullStatus2 != null ? " (" + fullStatus2 + "!)" : "");
        log(msg);
        return new ActionResult(msg, false, dmg, 0, "enemy", false, false, false, fullStatus2);
    }

    // ─── Ultimate ────────────────────────────────────────────────────────────
    private ActionResult resolveUltimate() {
        if (!canUseUltimate()) {
            if (hero.specialCooldown > 0)
                return insufficientEnergy("Ultimate (cooldown: " + hero.specialCooldown + ")");
            return insufficientEnergy("Ultimate");
        }
        hero.energy -= ultimateCost;
        hero.specialCooldown = (heroDef != null && heroDef.skills != null)
                ? heroDef.skills[2].cooldown : 3;

        String skillName = heroDef != null ? heroDef.skills[2].name : "Ultimate";
        double mult = heroDef != null ? heroDef.skills[2].multiplier : 1.40;
        boolean pierce = heroDef != null && heroDef.skills[2].pierceArmor;

        // Multi-hit ultimates
        int hits = 1;
        if (heroDef != null) {
            if (heroDef.role.equals("Swordsman")) hits = 3; // Eternal Cross Slash
            else if (heroDef.role.equals("Archer")) hits = 5; // Rain of a Thousand Arrows
            else if (heroDef.role.equals("Mage"))   hits = 5; // Meteor Storm
        }

        int totalDmg = 0;
        StringBuilder hitLog = new StringBuilder();
        for (int i = 1; i <= hits; i++) {
            int dmg = calcDamage(hero, enemy, mult, pierce);
            // Blade Swift passive
            if (heroDef != null && heroDef.role.equals("Swordsman") && rng.nextDouble() < 0.15) {
                dmg = (int)(dmg * 1.5);
                int gain = (int)(hero.maxEnergy * 0.05);
                hero.energy = Math.min(hero.maxEnergy, hero.energy + gain);
            }
            if (heroDef != null && heroDef.role.equals("Archer")) {
                if ((double)enemy.currentHp / enemy.maxHp < 0.3) dmg = (int)(dmg * 1.2);
            }
            totalDmg += dmg;
            if (hits > 1) hitLog.append("Hit ").append(i).append(": ").append(dmg).append("  ");
        }
        enemy.currentHp = clamp(enemy.currentHp - totalDmg, 0, enemy.maxHp);

        // On-hit weapon effects (applied once for the full combo)
        String weaponFxU = applyWeaponEffects(totalDmg);

        // Status effects
        String status = null;
        if (heroDef != null) {
            if (heroDef.role.equals("Swordsman")) {
                enemy.dotDamage = Math.max(1, (int)(enemy.maxHp * 0.05));
                enemy.dotTurnsLeft += 2;
                hero.defenseModifier += (int)(hero.defense * 0.20);
                status = "BLEED + FORTIFY";
            } else if (heroDef.role.equals("Archer")) {
                hero.nimble = true;
                hero.attackModifier += (int)(hero.attack * 0.20);
                status = "NIMBLE + STRENGTHEN";
            } else if (heroDef.role.equals("Mage")) {
                hero.attackModifier += (int)(hero.attack * 0.20);
                if (rng.nextDouble() < 0.50) {
                    enemy.dotDamage = Math.max(1, (int)(enemy.maxHp * 0.04));
                    enemy.dotTurnsLeft += 2;
                    status = "STRENGTHEN + BURN";
                } else status = "STRENGTHEN";
            }
        }

        String fullStatusU = (status != null && weaponFxU != null) ? status + ", " + weaponFxU
                : (status != null) ? status : weaponFxU;
        String msg = hero.name + " unleashes " + skillName + "! Total: " + totalDmg + " damage!" +
                (hits > 1 ? "\n" + hitLog : "") +
                (fullStatusU != null ? "\n(" + fullStatusU + "!)" : "");
        log(msg);
        return new ActionResult(msg, false, totalDmg, 0, "enemy", true, false, hits > 1, fullStatusU);
    }

    // ─── Skip Turn ───────────────────────────────────────────────────────────
    private ActionResult resolveSkipTurn() {
        int restoreHp = (int)(hero.maxHp * 0.10);
        int restoreEnergy;
        if (heroDef != null) {
            restoreEnergy = switch (heroDef.role) {
                case "Swordsman" -> 10;
                case "Archer" -> 3;
                case "Mage" -> 20;
                default -> 15;
            };
        } else restoreEnergy = 10;

        hero.currentHp = Math.min(hero.maxHp, hero.currentHp + restoreHp);
        hero.energy    = Math.min(hero.maxEnergy, hero.energy + restoreEnergy);
        hero.defending = true;
        hero.specialCooldown = Math.max(0, hero.specialCooldown - 1);

        String msg = hero.name + " skips turn. Restored " + restoreHp + " HP & " + restoreEnergy + " Energy.";
        log(msg);
        return new ActionResult(msg, true, 0, 0, "hero", false, false, false, null);
    }

    private ActionResult insufficientEnergy(String skillName) {
        String msg = "Not enough energy to use " + skillName + "!";
        log(msg);
        return new ActionResult(msg, false, 0, 0, "hero", false, false, false, null);
    }

    // ─── Enemy Turn ──────────────────────────────────────────────────────────
    public ActionResult enemyTurn() {
        if (currentTurn != TurnOwner.ENEMY) return null;
        updateModifiers(enemy);
        hero.defending = false;

        // Status checks
        if (enemy.stunned) {
            enemy.stunned = false;
            String msg = enemy.name + " is stunned! Turn skipped.";
            log(msg);
            round++;
            if (hero.specialCooldown > 0) hero.specialCooldown--;
            currentTurn = TurnOwner.PLAYER;
            return new ActionResult(msg, false, 0, 0, "enemy", false, false, false, "STUNNED");
        }
        if (enemy.frozen) {
            enemy.frozen = false;
            String msg = enemy.name + " is frozen! Turn skipped.";
            log(msg);
            round++;
            if (hero.specialCooldown > 0) hero.specialCooldown--;
            currentTurn = TurnOwner.PLAYER;
            return new ActionResult(msg, false, 0, 0, "enemy", false, false, false, "FROZEN");
        }

        ActionResult result;
        double roll = rng.nextDouble();

        if (roll < 0.15) {
            result = resolveDefend(enemy);
        } else if (enemy.currentHp < enemy.maxHp * 0.4 && roll < 0.45) {
            // Berserk at low HP
            result = resolveAttack(enemy, hero, 1.5, false, true);
        } else {
            result = resolveAttack(enemy, hero, 1.0, false, false);
        }

        int dot = applyDoT(enemy);
        round++;
        if (hero.specialCooldown > 0) hero.specialCooldown--;
        currentTurn = TurnOwner.PLAYER;

        return new ActionResult(result.logMessage, result.wasDefend, result.damageDealt,
                dot, result.target, result.isSpecial, result.isBerserk, false, null);
    }

    // ─── Outcome ─────────────────────────────────────────────────────────────
    public BattleOutcome checkOutcome() {
        if (!enemy.isAlive()) return BattleOutcome.VICTORY;
        if (!hero.isAlive())  return BattleOutcome.DEFEAT;
        return BattleOutcome.ONGOING;
    }

    // ─── Revive ──────────────────────────────────────────────────────────────
    public boolean attemptRevive() {
        if (phoenixSoulstoneAvailable) {
            hero.currentHp = hero.maxHp / 2;
            hero.energy    = hero.maxEnergy / 2;
            phoenixSoulstoneAvailable = false;
            currentTurn = TurnOwner.PLAYER;
            return true;
        }
        return false;
    }

    public void confirmRevive() {
        hero.currentHp = hero.maxHp / 2;
        hero.energy    = hero.maxEnergy / 2;
        hero.attackModifier = hero.defenseModifier = 0;
        hero.dotDamage = hero.dotTurnsLeft = 0;
        hero.defending = false;
        reviveUsed = true;
        currentTurn = TurnOwner.PLAYER;
    }

    public void advanceToEnemyTurn() { currentTurn = TurnOwner.ENEMY; }

    // ─── Private helpers ─────────────────────────────────────────────────────
    private ActionResult resolveAttack(Combatant atk, Combatant def,
                                       double mult, boolean pierce, boolean berserk) {
        int dmg = calcDamage(atk, def, mult, pierce);
        def.currentHp = clamp(def.currentHp - dmg, 0, def.maxHp);
        atk.defending = false;
        String target = atk == hero ? "enemy" : "hero";
        String msg = berserk
                ? atk.name + " goes BERSERK! Strikes for " + dmg + " damage!"
                : atk.name + " attacks for " + dmg + " damage!";
        log(msg);
        return new ActionResult(msg, false, dmg, 0, target, false, berserk, false, null);
    }

    private ActionResult resolveDefend(Combatant c) {
        c.defending = true;
        String msg = c.name + " braces for impact! (Damage halved)";
        log(msg);
        return new ActionResult(msg, true, 0, 0, c == hero ? "hero" : "enemy", false, false, false, null);
    }

    private int calcDamage(Combatant atk, Combatant def, double mult, boolean pierce) {
        double base    = atk.effectiveAttack() * mult;
        double defVal  = pierce ? def.effectiveDefense() * 0.15 : def.effectiveDefense() * 0.5;
        double variance= 0.85 + rng.nextDouble() * 0.30;
        double raw     = (base - defVal) * variance;
        int dmg        = (int) Math.round(Math.max(1.0, raw));
        return def.defending ? Math.max(1, dmg / 2) : dmg;
    }

    private void updateModifiers(Combatant c) {
        if (c.attackModifier  > 0) c.attackModifier  = Math.max(0, c.attackModifier  - 1);
        if (c.defenseModifier < 0) c.defenseModifier = Math.min(0, c.defenseModifier + 1);
        if (c.defenseModifier > 0) c.defenseModifier = Math.max(0, c.defenseModifier - 1);
    }

    private int applyDoT(Combatant c) {
        if (c.dotTurnsLeft <= 0 || c.dotDamage <= 0) return 0;
        int dmg = c.dotDamage;
        c.currentHp = clamp(c.currentHp - dmg, 0, c.maxHp);
        c.dotTurnsLeft = Math.max(0, c.dotTurnsLeft - 1);
        return dmg;
    }

    // ─── Weapon on-hit effects ────────────────────────────────────────────────
    // Returns a status string if an effect triggered, null otherwise
    // Mutates hero (lifesteal, energy) and enemy (confuse, poison DoT)
    private String applyWeaponEffects(int damageDealt) {
        if (weaponDef == null || damageDealt <= 0) return null;
        StringBuilder effects = new StringBuilder();

        // Lifesteal
        if (weaponDef.lifestealPercent > 0) {
            int heal = Math.max(1, (int)(damageDealt * weaponDef.lifestealPercent / 100.0));
            heal = Math.min(heal, hero.maxHp - hero.currentHp);
            if (heal > 0) {
                hero.currentHp += heal;
                if (effects.length() > 0) effects.append(", ");
                effects.append("Lifesteal +").append(heal).append("HP");
            }
        }

        // Energy per attack (Arc Surge style)
        if (weaponDef.energyPerAttack > 0) {
            hero.energy = Math.min(hero.maxEnergy, hero.energy + weaponDef.energyPerAttack);
        }

        // Confuse chance
        if (weaponDef.confuseChance > 0 && rng.nextInt(100) < weaponDef.confuseChance) {
            enemy.confused = true;
            if (effects.length() > 0) effects.append(", ");
            effects.append("CONFUSE");
        }

        // Poison chance
        if (weaponDef.poisonChance > 0 && rng.nextInt(100) < weaponDef.poisonChance) {
            enemy.dotDamage  = Math.max(enemy.dotDamage,  Math.max(1, (int)(enemy.maxHp * 0.03)));
            enemy.dotTurnsLeft += 2;
            if (effects.length() > 0) effects.append(", ");
            effects.append("POISON");
        }

        return effects.length() > 0 ? effects.toString() : null;
    }

    private int clamp(int v, int min, int max) { return Math.min(max, Math.max(min, v)); }
    private void log(String msg) { battleLog.add(msg); }
}