package GameGUI.model;

import GameGUI.logic.BattleLogic;

public class HeroFactory {

    public static Combatant createHero(HeroData.HeroDefinition def) {
        BattleLogic.Special special = null;

        // Map the 3rd skill (Ultimate) to the special system
        if (def.skills != null && def.skills.length >= 3) {
            HeroData.SkillDef ult = def.skills[2];
            special = new BattleLogic.Special(
                    ult.name, ult.icon, ult.description,
                    ult.maxMultiplier, ult.pierceArmor, ult.cooldown // <-- Changed here
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

        hero.specialCooldown = 3;

        return hero;
    }

    public static Combatant createEnemy(HeroData.EnemyDefinition def) {
        return new Combatant(
                def.name, def.role, def.emoji,
                def.maxHp, def.attack, def.defense,
                0, 0, null
        );
    }
}