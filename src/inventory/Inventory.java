package inventory;

import characters.Character;
import utils.ColorUtil;
import utils.InputUtil;
import utils.PrintUtil;

public class Inventory {

    private final Character player;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private final Potions potions;
    private boolean hasPhoenixSoulstone = false;

    public Inventory(Character owner) {
        this.player = owner;
        this.potions = new Potions(owner);
    }

    // --- Getters and Setters ---
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public Potions getPotions() { return potions; }

    public void setEquippedWeapon(Weapon weapon) { equippedWeapon = weapon; }
    public void setEquippedArmor(Armor armor) { equippedArmor = armor; }

    public boolean hasPhoenixSoulstone() {
        return hasPhoenixSoulstone;
    }
    public void acquirePhoenixSoulstone() {
        hasPhoenixSoulstone = true;
    }
    public void usePhoenixSoulstone() {
        hasPhoenixSoulstone = false;
    }

    // --- Generates a bar (wider, no colors) ---
    private String generateBar(int current, int max) {
        int totalBars = 44; // wider bar
        int filledBars = (int) ((double) current / max * totalBars);

        return "█".repeat(filledBars) +
                "░".repeat(totalBars - filledBars);
    }

    // --- Helper function to get weapon emoji ---
    private String getWeaponEmoji() {
        String classType = player.getClassType();

        return switch (classType) {
            case "Swordsman" -> "🗡️";
            case "Archer" -> "🏹";
            case "Mage" -> "🔮";
            default -> "⚔️"; // default weapon emoji
        };
    }

    // --- Inventory Menu ---
    public void openInventory() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            // --- Borders in bold bright cyan ---
            System.out.println(ColorUtil.boldBrightCyan("╔══════════════════════════════════════════════════════════════════════╗"));
            System.out.println(ColorUtil.boldBrightCyan("               🎒 ╦ ╔╗╔ ╦  ╦ ╔═╗ ╔╗╔ ╔╦╗ ╔═╗ ╦═╗ ╦ ╦ 🎒"));
            System.out.println(ColorUtil.boldBrightCyan("               🎒 ║ ║║║ ╚╗╔╝ ║╣  ║║║  ║  ║ ║ ╠╦╝ ╚╦╝ 🎒"));
            System.out.println(ColorUtil.boldBrightCyan("               🎒 ╩ ╝╚╝  ╚╝  ╚═╝ ╝╚╝  ╩  ╚═╝ ╩╚═  ╩  🎒"));
            System.out.println(ColorUtil.boldBrightCyan("╠══════════════════════════════════════════════════════════════════════╣"));

            // --- HP and Energy Bars with labels ---
            System.out.printf("  %-21s %-4s %s %s%n",
                    ColorUtil.boldBrightGreen("HP"),
                    ColorUtil.boldBrightGreen(generateBar(player.getHp(), player.getMaxHP())),
                    ColorUtil.boldBrightGreen(player.getHp() + " / " + player.getMaxHP()),
                    ColorUtil.boldBrightGreen("💚"));

            // --- Energy Bar with label ---
            System.out.printf("  %-21s %-4s %s %s%n",
                    ColorUtil.boldBrightWhite(player.getEnergyName()),
                    ColorUtil.boldBrightWhite(generateBar(player.getEnergy(), player.getMaxEnergy())),
                    ColorUtil.boldBrightWhite(player.getEnergy() + " / " + player.getMaxEnergy()),
                    ColorUtil.boldBrightWhite(player.getEnergyEmoji()));


            System.out.println(ColorUtil.boldBrightCyan("  ────────────────────────────────────────────────────────────────────"));

            // --- EQUIPPED ITEMS ---
            System.out.printf("  %s %-10s %s%n",
                    getWeaponEmoji(),
                    ColorUtil.cyan("Equipped Weapon  :  "),
                    equippedWeapon != null ? ColorUtil.boldBrightYellow(equippedWeapon.getName()) : ColorUtil.boldBrightYellow("No Weapon Equipped"));

            System.out.printf("  %s %-10s %s%n",
                    "🛡️ ",
                    ColorUtil.cyan("Equipped Armor  :  "),
                    equippedArmor != null ? ColorUtil.boldBrightYellow(equippedArmor.getName()) : ColorUtil.boldBrightYellow("No Armor Equipped"));

            System.out.println(ColorUtil.boldBrightCyan("  ──────────────────────────────────────────"));

            // --- POTIONS ---
            System.out.println( ColorUtil.cyan("  🍃 Normal Healing Potion  :  ") + ColorUtil.boldBrightYellow(""+getPotions().getNormalHealingPotions()));
            System.out.println( ColorUtil.cyan("  💞 Full Healing Potion    :  ") + ColorUtil.boldBrightYellow(""+getPotions().getFullHealingPotions()));
            System.out.println( ColorUtil.cyan("  ✨ Energy Potion          :  ") + ColorUtil.boldBrightYellow(""+getPotions().getEnergyPotions()));
            System.out.println( ColorUtil.cyan("  💠 Soul Shards            :  ") + ColorUtil.boldBrightYellow(""+player.getSoulShards()));

            System.out.println(ColorUtil.boldBrightCyan("╚══════════════════════════════════════════════════════════════════════╝"));

            // --- MENU ---
            PrintUtil.line();
            System.out.println("[1] ⚔️ Show Weapon Info");
            System.out.println("[2] 🛡️ Show Armor Info");
            System.out.println("[3] 🍃 Use Normal Healing Potion");
            System.out.println("[4] 💞 Use Full Healing Potion");
            System.out.println("[5] ⚡  Use Energy Potion");
            System.out.println("[0] 🔙 Back");
            System.out.print("Choose an option: ");

            int choice = InputUtil.scanInput();
            PrintUtil.line();

            switch (choice) {

                case 1 -> {
                    if (equippedWeapon != null) equippedWeapon.displayInfo();
                    else System.out.println(ColorUtil.boldBrightRed("❌") + " No weapon equipped!");
                    InputUtil.pressEnterToContinue();
                }

                case 2 -> {
                    if (equippedArmor != null) equippedArmor.displayInfo();
                    else System.out.println(ColorUtil.boldBrightRed("❌") + " No armor equipped!");
                    InputUtil.pressEnterToContinue();
                }

                case 3 -> usePotion("normal");
                case 4 -> usePotion("full");
                case 5 -> usePotion("energy");

                case 0 -> exit = true;

                default -> System.out.println(ColorUtil.boldBrightRed("❌") + " Invalid input! Try again.");

            }
        }
    }


    // --- Potion Handling ---
    private void usePotion(String type) {
        int current, max, available;

        switch (type) {
            case "normal" -> {
                current = player.getHp();
                max = player.getMaxHP();
                available = potions.getNormalHealingPotions();
            }
            case "full" -> {
                current = player.getHp();
                max = player.getMaxHP();
                available = potions.getFullHealingPotions();
            }
            case "energy" -> {
                current = player.getEnergy();
                max = player.getMaxEnergy();
                available = potions.getEnergyPotions();
            }
            default -> { return; }
        }

        if (available <= 0) {
            System.out.println(ColorUtil.boldBrightRed("❌ No potions of this type left!"));
            InputUtil.pressEnterToContinue();
            return;
        }

        int amount = 0;
        while (true) {
            System.out.println("You have " + available + " potion(s).");
            System.out.print("How many do you want to use? ");
            amount = InputUtil.scanInput();
            if (amount == 0) return;
            if (amount > 0 && amount <= available) break;
            System.out.println(ColorUtil.boldBrightRed("❌ Invalid amount! Please enter between 1 and " + available));
        }

        if (!areYouSure()) {
            System.out.println(ColorUtil.boldBrightRed("Potion not used..."));
            return;
        }

        for (int i = 0; i < amount; i++) {
            switch (type) {
                case "normal" -> {
                    if (player.getHp() < player.getMaxHP()) {
                        potions.useNormalHealingPotion();
                    }
                    else {
                        System.out.println(ColorUtil.boldBrightGreen("💡 HP is full. Remaining potions not used."));
                        InputUtil.pressEnterToContinue();
                        return;
                    }
                }
                case "full" -> {
                    if (player.getHp() < player.getMaxHP()) {
                        potions.useFullHealingPotion();
                    } else {
                        System.out.println(ColorUtil.boldBrightGreen("💡 HP is full. Remaining potions not used."));
                        InputUtil.pressEnterToContinue();
                        return;
                    }
                }
                case "energy" -> {
                    if (player.getEnergy() < player.getMaxEnergy()) {
                        potions.useEnergyPotion();
                    } else {
                        System.out.println(ColorUtil.boldBrightGreen("💡 Energy is full. Remaining potions not used."));
                        InputUtil.pressEnterToContinue();
                        return;
                    }
                }
            }
        }
        InputUtil.pressEnterToContinue();
    }

    // --- Confirmation prompt ---
    private boolean areYouSure() {
        int confirm;
        do {
            System.out.print("Are you sure you want to use a potion? (1 = Yes, 0 = No): ");
            confirm = InputUtil.scanInput();
            if (confirm == 1) break;
            else if (confirm == 0) break;
            else System.out.println(ColorUtil.boldBrightRed("❌ Invalid input! Try again.\n"));
        } while (true);
        return confirm == 1;
    }


}
