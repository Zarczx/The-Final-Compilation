package GameGUI.model.entity.data;

import java.util.List;

public interface HeroData {

    // Registries
    List<HeroDefinition> getHeroes();
    List<EnemyData> getWorld1Enemies();
    List<EnemyData> getWorld2Enemies();
    List<EnemyData> getWorld3Enemies();
    List<EnemyData> getFinalBossSequence();
}