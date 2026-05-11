package GameGUI.model.system;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.DataManager;
import GameGUI.model.entity.data.HeroDefinition;
import GameGUI.model.equipment.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SaveManager — Handles converting game state to JSON and writing it safely to the disk.
 */
public class SaveManager {

    // The folder where saves will live
    private static final String SAVE_DIR = "saves/";

    // Gson is the library that turns Java objects into beautifully formatted JSON text
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Creates the "saves" folder if it doesn't exist yet
    private static void ensureDirectoryExists() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * The ATOMIC SAVE method.
     * Extracts data from Kael, writes to a temp file, then safely overwrites the real save.
     */
    public static boolean saveGame(Combatant hero, Combatant enemy, int slotNumber, int currentWorld, int seqIndex, int fightIndex, boolean isPrefiActive) {
        ensureDirectoryExists();

        // 1. Create our "Dumb" DTO
        SaveData data = new SaveData();

        // 2. Meta Data
        data.slotNumber = slotNumber;
        data.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        data.heroName = hero.name;
        data.heroRole = hero.role != null ? hero.role : "Swordsman";
        data.level = hero.getLevel();

        // 3. Core Stats
        data.currentHp = hero.getCurrentHp();
        data.maxHp = hero.getMaxHp();
        data.energy = hero.getEnergy();
        data.maxEnergy = hero.getMaxEnergy();
        data.baseAttack = hero.getBaseAttack();
        data.baseDefense = hero.getBaseDefense();
        data.exp = hero.getExp();
        data.soulShards = hero.getSoulShards();

        // 4. Equipment
        Weapon w = hero.inventory.getEquippedWeapon();
        if (w != null) {
            data.equippedWeaponName = w.name;
            data.weaponEnchantments = w.enchantments;
            data.weaponAddedLifesteal = w.addLifestealPercent;
        }

        Armor a = hero.inventory.getEquippedArmor();
        if (a != null) {
            data.equippedArmorName = a.name;
            data.armorHasFortified = a.hasEnchantment;
        }

        // 5. Consumables (Reaching into your Potions Manager)
        data.normalFlasks = hero.inventory.getNormalHealingPotions();
        data.crimsonFlasks = hero.inventory.getFullHealingPotions();
        data.ceruleanFlasks = hero.inventory.getEnergyPotions();

        // 6. Progression
        data.currentWorld = currentWorld;
        data.savedEnemySequenceIndex = seqIndex;
        data.savedEnemyFightIndex = fightIndex;
        data.savedEnemyCurrentHp = (enemy != null) ? enemy.getCurrentHp() : -1;
        data.savedEnemyMaxHp     = (enemy != null) ? enemy.getMaxHp() : -1;

        data.isPrefiScreenActive = isPrefiActive;
        boolean isFightingKhai = (enemy != null && enemy.name != null && enemy.name.toLowerCase().contains("khai"));
        data.isFinalBossSequence = (isPrefiActive || isFightingKhai);

        // ════════════════════════════════════════════════════
        // 7. ★ ATOMIC SAVE LOGIC ★
        // ════════════════════════════════════════════════════
        try {
            // Convert the DTO to a JSON string
            String jsonText = GSON.toJson(data);

            // File paths
            File tempFile = new File(SAVE_DIR + "slot_" + slotNumber + "_temp.json");
            File finalFile = new File(SAVE_DIR + "slot_" + slotNumber + ".json");

            // Step A: Write to the temporary file first
            Files.writeString(tempFile.toPath(), jsonText);

            // Step B: If that succeeded, replace the old file with the new one!
            Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Game saved successfully to Slot " + slotNumber + "!");
            return true;

        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to save game!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads the JSON file and turns it back into a SaveData object.
     */
    public static SaveData loadGame(int slotNumber) {
        try {
            File file = new File(SAVE_DIR + "slot_" + slotNumber + ".json");
            if (!file.exists()) return null;

            String jsonText = Files.readString(file.toPath());
            return GSON.fromJson(jsonText, SaveData.class);
        } catch (Exception e) {
            System.err.println("Failed to load save slot " + slotNumber);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Rebuilds a fully functioning Combatant from the dumb SaveData object.
     */
    public static Combatant reconstructHero(SaveData data) {
        if (data == null) return null;

        // 1. Find the base blueprint based on the saved role/name
        HeroDefinition baseDef =
                DataManager.getData().getHeroes().get(0); // Default to Kael

        for (HeroDefinition def : DataManager.getData().getHeroes()) {
            if (def.name.equals(data.heroName)) {
                baseDef = def;
                break;
            }
        }

        // 2. Create a fresh hero, then overwrite their stats with the saved data
        Combatant hero = GameGUI.model.HeroFactory.createHero(baseDef);

        hero.setLevel(data.level);
        hero.setCurrentHp(data.currentHp);
        hero.setMaxHp(data.maxHp);
        hero.setEnergy(data.energy);
        hero.setMaxEnergy(data.maxEnergy);
        hero.setBaseAttack(data.baseAttack);
        hero.setBaseDefense(data.baseDefense);
        hero.setExp(data.exp);
        hero.setSoulShards(data.soulShards);

        // 3. Restore Consumables
        hero.inventory.potions.addNormalHealingPotions(data.normalFlasks);
        hero.inventory.potions.addFullHealingPotions(data.crimsonFlasks);
        hero.inventory.potions.addEnergyPotions(data.ceruleanFlasks);

        // 4. Restore Weapon
        if (data.equippedWeaponName != null) {
            Weapon w = switch (data.equippedWeaponName) {
                // Swords
                case "Iron Shortsword" -> new Sword(Sword.IRON_SHORTSWORD);
                case "Twinstrike Blade" -> new Sword(Sword.TWINSTRIKE_BLADE);
                case "Lifebond Blade" -> new Sword(Sword.LIFEBOND_BLADE);
                case "Eclipse Greatsword" -> new Sword(Sword.ECLIPSE_GREATSWORD);
                case "Celestial Edge" -> new Sword(Sword.CELESTIAL_EDGE);

                // Bows
                case "Oak Longbow" -> new Bow(Bow.OAK_LONGBOW);
                case "Twinshot Bow" -> new Bow(Bow.TWINSHOT_BOW);
                case "Lifebloom Bow" -> new Bow(Bow.LIFEBLOOM_BOW);
                case "Aetherstrike Bow" -> new Bow(Bow.AETHERSTRIKE_BOW);
                case "Golden Talon" -> new Bow(Bow.GOLDEN_TALON);

                // Staffs
                case "Apprentice Staff" -> new Staff(Staff.APPRENTICE_STAFF);
                case "Mystic Mind Staff" -> new Staff(Staff.MYSTIC_MIND_STAFF);
                case "Flameheart Staff" -> new Staff(Staff.FLAMEHEART_STAFF);
                case "Aetheric Staff" -> new Staff(Staff.AETHERIC_STAFF);
                case "Chronomancer Staff" -> new Staff(Staff.CHRONOMANCER_STAFF);

                default -> new Sword(Sword.OLD_BROADSWORD);
            };

            // Restore Magic Shop enchantments!
            w.enchantments = data.weaponEnchantments;
            w.addLifestealPercent = data.weaponAddedLifesteal;

            hero.inventory.setEquippedWeapon(w);
        }

        // 5. Restore Armor
        if (data.equippedArmorName != null) {
            Armor a = switch (data.equippedArmorName) {
                case "Iron Vanguard" -> new Armor(Armor.AEGIS_MAIL);
                case "Aegis Mail" -> new Armor(Armor.AEGIS_MAIL);
                case "Vanguard Robe" -> new Armor(Armor.VANGUARD_ROBE);
                case "Skyforge Plate" -> new Armor(Armor.SKYFORGE_PLATE);
                case "Celestial Battlegear" -> new Armor(Armor.CELESTIAL_BATTLEGEAR);
                default -> new Armor(Armor.LEATHER_GUARD);
            };

            a.hasEnchantment = data.armorHasFortified;
            hero.inventory.setEquippedArmor(a);
        }

        hero.recalculateBuffs();
        return hero;
    }

    public static void deleteGame(int slotNumber) {
        try {
            java.io.File file = new java.io.File(SAVE_DIR + "slot_" + slotNumber + ".json");
            if (file.exists()) {
                file.delete();
                System.out.println("Slot " + slotNumber + " deleted successfully.");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to delete save file!");
            e.printStackTrace();
        }
    }
}