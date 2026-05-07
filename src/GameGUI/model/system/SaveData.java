package GameGUI.model.system;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for Game Saves.
 * This class contains NO logic. It only holds raw data that can be
 * easily converted to and from a JSON file.
 */
public class SaveData {

    // ── Meta Data (Used to show info on the Load Screen) ──
    public int slotNumber;
    public String timestamp;
    public String heroName;
    public String heroRole;
    public int level;

    // ── Core Stats ──
    public int currentHp;
    public int maxHp;
    public int energy;
    public int maxEnergy;
    public int baseAttack;
    public int baseDefense;
    public int exp;
    public int soulShards;

    // ── Equipment ──
    // We only save the NAMES of the items, not the objects themselves.
    // When loading, the game will look up these names and generate the real items.
    public String equippedWeaponName;
    public String equippedArmorName;

    // We save the enchantments so Magic Shop upgrades survive!
    public Map<String, String> weaponEnchantments = new HashMap<>();
    public int weaponAddedAtk = 0;
    public int weaponAddedLifesteal = 0;

    public boolean armorHasFortified = false;

    // ── Consumables ──
    public int normalFlasks;
    public int crimsonFlasks;
    public int ceruleanFlasks;

    // ── Progression State ──
    public int currentWorld;
    public int savedEnemySequenceIndex;
    public int savedEnemyFightIndex;


    // Empty constructor is REQUIRED for JSON libraries (like Gson) to work properly
    public SaveData() {
    }
}