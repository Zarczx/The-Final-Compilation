package GameGUI.model.entity.base;

// Import all the enemy packages so the registry can see them!
import GameGUI.model.entity.data.EnemyData;
import GameGUI.model.entity.world1.*;
import GameGUI.model.entity.world2.*;
import GameGUI.model.entity.world3.*;
import GameGUI.model.entity.boss.*;

import java.util.List;

public class EnemyRegistry {

    public static List<EnemyData> getWorld1Enemies() {
        return List.of(
                new RotfangWolf(),
                new ShadeSprite(),
                new DreadbarkTreant(),
                new CarrionBat(),
                new HollowStag()
        );
    }

    public static List<EnemyData> getWorld2Enemies() {
        return List.of(
                new PlagueVermin(),
                new ForsakenCultist(),
                new BlightHound(),
                new GhoulFootman(),
                new BlackJailer(),
                new LutherVon()
        );
    }

    public static List<EnemyData> getWorld3Enemies() {
        return List.of(
                new FlameRevenant(),
                new BoneWarlock(),
                new ObsidianCrusher(),
                new SoulflayerGargoyle(),
                new Zyrryl()
        );
    }

    public static List<EnemyData> getFinalBossSequence() {
        return List.of(
                new KhaiTheNecromancer()
        );
    }
}