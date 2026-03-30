package GameGUI.model;

import GameGUI.logic.BattleLogic;

/**
 * HeroFactory — Handles the "How" of creating game objects.
 * Separates instantiation logic from data storage.
 */
public class HeroFactory {

    /**
     * Builds a playable Hero Combatant from a definition.
     * Maps skills to the special cooldown system and applies equipment buffs.
     */
    public static Combatant createHero(HeroData.HeroDefinition def) {
        BattleLogic.Special special = null;

        // Map the 3rd skill (Ultimate) to the special system
        if (def.skills != null && def.skills.length >= 3) {
            HeroData.SkillDef ult = def.skills[2];
            special = new BattleLogic.Special(
                    ult.name, ult.icon, ult.description,
                    ult.multiplier, ult.pierceArmor, ult.cooldown
            );
        }

        // Apply equipment bonuses (starting gear)
        int hp     = def.maxHp + (def.startingArmor != null ? def.startingArmor.hpBuff : 0);
        int atk    = def.attack + (def.startingWeapon != null ? def.startingWeapon.atkBuff : 0);
        int defStat= def.defense + (def.startingArmor != null ? def.startingArmor.defBuff : 0);

        Combatant hero = new Combatant(
                def.name, def.role, def.emoji,
                hp, atk, defStat,
                def.maxEnergy, def.maxEnergy,
                special
        );

        // Ultimate locked for first 3 turns (matches console reference)
        hero.specialCooldown = 3;

        return hero;
    }

    /**
     * Builds an Enemy Combatant from a definition.
     */
    public static Combatant createEnemy(HeroData.EnemyDefinition def) {
        return new Combatant(
                def.name, def.role, def.emoji,
                def.maxHp, def.attack, def.defense,
                0, 0, null
        );
    }
}