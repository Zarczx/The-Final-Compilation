package GameGUI.model.entity.data;

import GameGUI.model.entity.base.BaseEnemy;
import GameGUI.model.entity.data.definition.*;

import java.util.List;

public interface HeroData {

    // ─── Weapon Types ────────────────────────────────────────────────────────
    enum WeaponType { SWORD, BOW, STAFF }

    // ─── Enemy Definition ────────────────────────────────────────────────────


    // ─── Function Contracts (To be implemented by base classes) ──────────────

    // Armors
    /*ArmorDef getLeatherGuard();
    ArmorDef getIronVanguard();
    ArmorDef getAegisMail();
    ArmorDef getVanguardRobe();
    ArmorDef getSkyforgePlate();
    ArmorDef getCelestialBattlegear();
    */

    // Registries
    List<HeroDefinition> getHeroes();
    List<EnemyData> getWorld1Enemies();
    List<EnemyData> getWorld2Enemies();
    List<EnemyData> getWorld3Enemies();
    List<EnemyData> getFinalBossSequence();
}