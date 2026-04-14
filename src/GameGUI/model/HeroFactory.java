package GameGUI.model;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;
import GameGUI.model.equipment.*;

public class HeroFactory {

    // Helper method to create the correct subclass based on the weapon definition
    public static Weapon instantiateWeapon(HeroData.WeaponDef def) {
        if (def == null) return null;
        return switch (def.type) {
            case SWORD -> new Sword(def);
            case BOW -> new Bow(def);
            case STAFF -> new Staff(def);
        };
    }


    public static Combatant createHero(HeroData.HeroDefinition def) {
        // ★ We no longer need to build a BattleLogic.Special here!

        int hp     = def.maxHp + (def.startingArmor != null ? def.startingArmor.hpBuff : 0);
        int atk    = def.attack + (def.startingWeapon != null ? def.startingWeapon.atkBuff : 0);
        int defStat= def.defense + (def.startingArmor != null ? def.startingArmor.defBuff : 0);

        // ★ Removed 'special' from the end of this list
        Combatant hero = new Combatant(
                def.name, def.role, def.emoji,
                hp, atk, defStat,
                def.maxEnergy, def.maxEnergy
        );

        hero.specialCooldown = 3;

        if (def.startingWeapon != null) {
            hero.inventory.setEquippedWeapon(instantiateWeapon(def.startingWeapon));
        }

        if (def.startingArmor != null) {
            hero.inventory.setEquippedArmor(new Armor(def.startingArmor));
        }

        return hero;
    }

    public static Combatant createEnemy(HeroData.EnemyDefinition def) {
        // ★ Removed the 'null' at the end that used to represent 'special'
        return new Combatant(
                def.name, def.role, def.emoji,
                def.maxHp, def.attack, def.defense,
                0, 0
        );
    }
}