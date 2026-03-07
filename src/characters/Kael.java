package characters;

import utils.SoundUtil;
import enemies.FinalBoss;
import utils.ColorUtil;
import utils.InputUtil;
import utils.PrintUtil;
import utils.RandomUtil;

public class Kael extends  Character{      // 15% crit chance

    public Kael() {
        super("Kael Saint Laurent", "Swordsman" ,100, 5, 100, 12);
    }

    @Override
    public void displaySkills() {
        System.out.println();
        System.out.println(ColorUtil.boldBrightCyan("┌────────────────────────────────────── ⚔️ KAEL'S SKILLS ⚔️ ───────────────────────────────────────┐"));

// Passive
        System.out.println("  " + ColorUtil.boldBrightYellow("✨ Passive – Blade Swift"));
        System.out.println("  " + ColorUtil.cyan("When a Critical Hit occurs, 🔋 Kael gains ") + ColorUtil.boldBrightYellow("+5%") + ColorUtil.cyan(" Stamina.\n"));

// Skill 1
        System.out.println("  " + ColorUtil.boldBrightYellow("🗡️ Skill 1 – Blade Rush (🔋 5 Stamina)"));
        System.out.println("  " + ColorUtil.cyan("📜 Description: A quick, fluid slash that catches the opponent off guard."));
        System.out.println("  " + ColorUtil.cyan("💥 Damage: (") + ColorUtil.boldBrightYellow((int)(attack * 1.15) + " — " + (int)(attack * 1.35)) + ColorUtil.cyan(")"));
        System.out.println("  " + ColorUtil.cyan("⚡ Effects:"));
        System.out.println("    - " + ColorUtil.cyan("💪 30% chance to apply Strengthen (+20% ATK for 2 turns)\n"));

// Skill 2
        System.out.println("  " + ColorUtil.boldBrightYellow("⚔️ Skill 2 – Piercing Slash (🔋 10 Stamina)"));
        System.out.println("  " + ColorUtil.cyan("📜 Description: A powerful, focused strike aimed to pierce enemy's armor."));
        System.out.println("  " + ColorUtil.cyan("💥 Damage: (") + ColorUtil.boldBrightYellow((int)(attack * 1.35) + " — " + (int)(attack * 1.55)) + ColorUtil.cyan(") — Ignores Defense"));
        System.out.println("  " + ColorUtil.cyan("⚡ Effects:"));
        System.out.println("    - " + ColorUtil.cyan("💫 30% chance to Stun (1 turn)\n"));

// Ultimate
        System.out.println("  " + ColorUtil.boldBrightYellow("✝️ Ultimate – Eternal Cross Slash (🔋 20 Stamina)"));
        System.out.println("  " + ColorUtil.cyan("📜 Description: Kael unleashes a flurry of crossing strikes infused with unyielding determination."));
        System.out.println("  " + ColorUtil.cyan("💥 Damage: 3 hits, each dealing (") + ColorUtil.boldBrightYellow((int)(attack * 1.00) + " — " + (int)(attack * 1.80)) + ColorUtil.cyan(")"));
        System.out.println("  " + ColorUtil.cyan("⚡ Effects:"));
        System.out.println("    - " + ColorUtil.cyan("🩸 Applies Bleed for 2 turns"));
        System.out.println("    - " + ColorUtil.cyan("🛡️ Grants Fortified (+20% DEF for 2 turns)"));

        System.out.println(ColorUtil.boldBrightCyan("└───────────────────────────────────────────────────────────────────────────────────────────────────┘"));
        System.out.println();
    }

    @Override
    public String showBackstory() {
        return "┌───────────────────────────── 📘 KAEL'S BACKSTORY 📘 ─────────────────────────────┐\n" +
                "  Kael Saint Laurent was born in the shadow of the Black Castle, in a family that\n" +
                "  lived modestly within the crumbling Town of Decay. His childhood was\n" +
                "  marked by sickness in the streets and guards demanding bribes, yet Kael never\n" +
                "  let the rot of the world steal his spirit. He spent hours watching the castle\n" +
                "  soldiers train, imitating their sword movements with sticks until his hands\n" +
                "  blistered, slowly turning mimicry into skill.\n" +
                "\n" +
                "  Even as a child, he carried himself with the bearing of a leader. Kael would\n" +
                "  gather the other children of the town into ragtag games of knights and kings,\n" +
                "  but unlike the corrupted rulers above, he always played the protector—\n" +
                "  shielding his friends from imagined dangers. In a place where hope was rare,\n" +
                "  Kael's presence became a quiet anchor, hinting that even in the midst of\n" +
                "  decay, someone could rise to stand against the darkness.\n" +
                "└──────────────────────────────────────────────────────────────────────────────────┘";
    }

    // Passive - Blade Swift
    private int bladeSwift(int damage) {
        if (RandomUtil.chance(15)) {
            System.out.println(ColorUtil.brightMagenta("⚡ Critical Hit! Blade Swift activated!"));
            PrintUtil.pause(800);
            damage = (int) (damage * 1.5);

            // Gain +5% of max energy (bonus adrenaline)
            int energyGained = (int)(maxEnergy * 0.05);
            int before = energy;
            this.restoreEnergy(energyGained);
            int after = energy;

            System.out.println(ColorUtil.brightMagenta("✨ Gained +" + energyGained + " energy from precision! " + "(🔋 " + before + " → " + after + ")"));
            PrintUtil.pause(800);
        }
        return damage;
    }

    // Skill 1 - Blade Rush
    public void bladeRush(Character target){
        PrintUtil.pause(500);
        SoundUtil.playDelayed("characters/kael_skill1.wav", 500);
        PrintUtil.print(ColorUtil.boldBrightGreen("                                                          \n" +
                "                .........                                 \n" +
                "            .:-:.::-::...                                 \n" +
                "         ..:..::.                                         \n" +
                "        .:.::.                -@%-                        \n" +
                "       .: .:.                .#%#%*-.                     \n" +
                "       .:. ::.              .=@@*=:                       \n" +
                "        .:...--:..         :%@%%#@%-                      \n" +
                "          ..:::===--:. ..:*@##%#%%+.                      \n" +
                "                   .:=+*%%%###@@@@=                       \n" +
                "                      ....::-#@%@@%=.                     \n" +
                "                           .#@@@@@@@+.                    \n" +
                "                           :#@%@@#%@%=                    \n" +
                "                          :#@#:   .+%%.                   \n" +
                "                         :#@-.     -#%.                   \n" +
                "                        :*=.        -%:                   \n" +
                "                       .*%:         -%%#:                 \n" +
                "                                                          "));

        int energyCost = 5;
        consumeEnergy(energyCost);

        System.out.println(ColorUtil.boldBrightGreen("🗡️ You used Blade Rush on " + target.getName() + " (🔋-" + energyCost + " Stamina)"));
        PrintUtil.pause(800);

        if(this.getEffects().checkConfuse()) return;

        int damage = (int) RandomUtil.range(attack * 1.15,attack * 1.35);
        damage = bladeSwift(damage);
        int reduced = calculateDamage(target, damage);

        System.out.println(
                ColorUtil.brightGreen("💔 Target is hit for ") +
                        ColorUtil.boldBrightWhite(String.valueOf(reduced)) +
                        ColorUtil.brightGreen(" Damage!")
        );

        PrintUtil.pause(800);
        target.takeDamage(reduced);

        // 30% chance to apply Strengthen (+20% ATK for 2 turns)
        if (RandomUtil.chance(30)) {
            getEffects().applyAttackBuff(20, 2);
        }

        this.getWeapon().applyEffects(this,target,reduced);
    }

    // Skill 2 - Piercing Slash
    public void piercingSlash(Character target){
        SoundUtil.playDelayed("characters/kael_skill2.wav", 500);
        PrintUtil.print(ColorUtil.boldBrightGreen("                                                          \n" +
                "                               ...                        \n" +
                "                              .=@@*.                      \n" +
                "                              :@@*+=:                     \n" +
                "                             -*@@%%=.                     \n" +
                "  ................         .=%@%##@%:                     \n" +
                "    ....................::-#%**%%%%=.                     \n" +
                "              ..........:+##%#*@@@@=.                     \n" +
                "                       ... . :@@@@@@+.                    \n" +
                "                             %@@@@@@@=.                   \n" +
                "                            =*@%++=*@%+                   \n" +
                "                           -#@+.    *%*                   \n" +
                "                          :%#.      -%#                   \n" +
                "                         -#-         =%:                  \n" +
                "                        .=*.         :=**:                \n" +
                "                                                          "));

        int energyCost = 10;
        consumeEnergy(energyCost);

        System.out.println(ColorUtil.boldBrightGreen("⚔\uFE0F You used Piercing Slash on " + target.getName() + " (🔋-" + energyCost + " Stamina)"));
        PrintUtil.pause(800);

        if(this.getEffects().checkConfuse()) return;

        int damage = (int) RandomUtil.range(attack * 1.35,attack * 1.55);
        int reduced = bladeSwift(damage);

        System.out.println(
                ColorUtil.brightGreen("💔 Target is hit for ")
                        + ColorUtil.boldBrightWhite(String.valueOf(reduced))
                        + ColorUtil.brightGreen(" Pure Damage!")
        );

        PrintUtil.pause(800);
        target.takeDamage(reduced);

        // 30% chance to apply Stun (from the sheer impact)
        if (RandomUtil.chance(30)) {
            target.getEffects().applyStun();
            if (target instanceof FinalBoss fb) fb.applyStun();
        }

        this.getWeapon().applyEffects(this,target,reduced);
    }

    //Ultimate - Eternal Cross Slash
    public void eternalCrossSlash(Character target){
        SoundUtil.playDelayed("characters/kael_skill3.wav", 500);
        PrintUtil.print(ColorUtil.boldBrightGreen("                                                          \n" +
                "                                ..:.                      \n" +
                "             .:--:   ::::::--:        :.                  \n" +
                "          .:.   .--.    ..   -=::::--- .                  \n" +
                "        :: . .:.      .: :::                              \n" +
                "       .  . :.       :::-.     +@@=                       \n" +
                "       . .  :.      .:::      -@%==:      ...             \n" +
                "       ...   ..     .:.      -#@%%#-     ..:::            \n" +
                "         :::..:-=-::::      =%%%#*%*      .:-::.          \n" +
                "             .:..:+*+=-:::-#%=*%%%%:     .::::::-         \n" +
                "                  ..   .=**##**%%@%:     ::-. ...:        \n" +
                "                   .         :@@%%@@=  .:.-.   :  :       \n" +
                "                             @@%@@%%@-:..:.    .: -       \n" +
                "                            =#@#==:*%-  ..     :. :.      \n" +
                "                        .  -#@:   ::   :   .:.:   .       \n" +
                "                          :+*::--.  :#*     :.   ..       \n" +
                "                        .:       ::. -%                   \n" +
                "                         ==...        -+=                 \n" +
                "                                                          "));

        int energyCost = 20;
        consumeEnergy(energyCost);
        int totalDamage = 0;

        System.out.println(ColorUtil.boldBrightGreen("✝️ You unleash your ultimate technique: Eternal Cross Slash!" + " (🔋-" + energyCost + " Stamina)"));
        PrintUtil.pause(800);

        for(int i = 1; i <= 3; i++){
            int damage = (int) RandomUtil.range(attack * 1.00,attack * 1.80);
            damage = bladeSwift(damage);
            int reduced = calculateDamage(target, damage);

            if(this.getEffects().checkConfuse()) reduced = 0;
            totalDamage += reduced;

            System.out.println(
                    ColorUtil.brightGreen(" →🔪 Hit " + i + "! 💔 You slashed the Target for ")
                            + ColorUtil.boldBrightWhite(String.valueOf(reduced))
                            + ColorUtil.brightGreen(" damage!")
            );

            PrintUtil.pause(800);

        }

        System.out.println(
                ColorUtil.brightGreen("⚔️💥 Eternal Cross Slash finished! Total Damage dealt: ")
                        + ColorUtil.boldBrightWhite(String.valueOf(totalDamage))
        );

        PrintUtil.pause(800);

        target.takeDamage(totalDamage);
        target.getEffects().applyBleed(2);

        this.getWeapon().applyEffects(this,target,totalDamage);
        this.getEffects().applyDefenseBuff(20, 2);

        ultimateCounter = 3;
    }

    @Override
    public void turn(Character target) {
        boolean isValid = false;

        while (!isValid) {
            // Display skills
            System.out.println(ColorUtil.boldBrightGreen("[1]") + " " + ColorUtil.green("🗡️ Skill 1   -  Blade Rush (🔋 5 Stamina)"));
            System.out.println(ColorUtil.boldBrightGreen("[2]") + " " + ColorUtil.green("⚔\uFE0F Skill 2   -  Piercing Slash (🔋 10 Stamina)"));
            System.out.println(ColorUtil.boldBrightGreen("[3]") + " " + ColorUtil.green("✝️ Ultimate  -  Eternal Cross Slash (🔋 20 Stamina)"
                    + (ultimateCounter > 0 ? " " + ColorUtil.boldBrightRed(" ❌ Cooldown: " + ultimateCounter + " turn/s") : "")));
            System.out.println(ColorUtil.boldBrightGreen("[4]") + " " + ColorUtil.green("\uD83D\uDEE1\uFE0F Skip Turn -  Restore 10% of Max HP and 10 Stamina"));
            System.out.println(ColorUtil.boldBrightGreen("[5]") + " " + ColorUtil.green("📜 Show Menu"));
            System.out.print(ColorUtil.boldBrightWhite("Choose your action: "));

            int choice = InputUtil.scanInput();
            PrintUtil.shortLine();

            switch (choice) {
                case 1 -> {
                    if (energy >= 5) {
                        bladeRush(target);
                        isValid = true;
                        ultimateCounter--;
                    } else {
                        System.out.println(ColorUtil.boldBrightRed("❌ Not enough Stamina to use Blade Rush! Choose again."));
                        PrintUtil.shortLine();
                    }
                }
                case 2 -> {
                    if (energy >= 10) {
                        piercingSlash(target);
                        isValid = true;
                        ultimateCounter--;
                    } else {
                        System.out.println(ColorUtil.boldBrightRed("❌ Not enough Stamina to use Piercing Slash! Choose again."));
                        PrintUtil.shortLine();
                    }
                }
                case 3 -> {
                    if (ultimateCounter > 0) {
                        System.out.println(ColorUtil.boldBrightRed("❌ Ultimate is on cooldown! Can only be used after " + ultimateCounter + " turn/s."));
                        PrintUtil.shortLine();
                    } else if (energy >= 20) {
                        eternalCrossSlash(target);
                        isValid = true;
                    } else {
                        System.out.println(ColorUtil.boldBrightRed("❌ Not enough Stamina to use Eternal Cross Slash! Choose again."));
                        PrintUtil.shortLine();
                    }
                }
                case 4 -> {
                    skipTurn();
                    isValid = true;
                    ultimateCounter--;
                }
                case 5 -> { displayMenu(this, target); } // does not consume turn
                default -> {
                    System.out.println(ColorUtil.boldBrightRed("❌ Invalid action! Try again."));
                    PrintUtil.shortLine();
                }
            }
        }
    }


}
