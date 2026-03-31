package GameGUI;

import GameGUI.HeroData.HeroDefinition;
import javax.swing.*;

public class TestMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Battle Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            BattlePanel battlePanel = new BattlePanel();

            // ── Pick your test hero here ──
            HeroDefinition hero = HeroData.HEROES.get(0); // 0 = Kael, 1 = Karl, 2 = Simon

            // ── Pick your test enemy here ──
            HeroData.EnemyDefinition enemy = HeroData.WORLD1_ENEMIES.get(1); // Shade Sprite

            battlePanel.setOnReturnToSelection(() -> System.out.println("Return to selection"));
            battlePanel.setOnRestartBattle(() -> battlePanel.startBattle(hero, enemy));

            battlePanel.startBattle(hero, enemy);

            frame.setContentPane(battlePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}