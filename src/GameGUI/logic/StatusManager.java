package GameGUI.model.logic;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class StatusManager {
    private final Combatant owner;

    // =========================================================================
    // EFFECT ENUM
    // All status effects live here. Adding a new one only requires a new entry.
    // =========================================================================
    public enum Effect {
        // Hard CC (1-turn, skip turn)
        STUNNED,
        FROZEN,

        // Soft CC (turn-based duration)
        CONFUSED,

        // DoT (turn-based duration)
        POISONED,
        BLEEDING,
        BURNING,

        // Stat Debuffs (turn-based duration)
        WEAKENED,   // -ATK
        FRAGILE,    // -DEF

        // Stat Buffs (turn-based duration)
        STRENGTHENED, // +ATK
        FORTIFIED     // +DEF
    }

    // =========================================================================
    // CORE STORAGE
    // One map holds ALL effects and their remaining turns.
    // CC booleans (stunned/frozen) use 1 = active, 0 = inactive.
    // =========================================================================
    private final Map<Effect, Integer> activeEffects = new EnumMap<>(Effect.class);

    // Stat modifier lists are still needed to track the actual numeric amounts
    // so we can correctly reverse them when they expire.
    private final List<StatModifier> atkBuffs   = new ArrayList<>();
    private final List<StatModifier> atkDebuffs = new ArrayList<>();
    private final List<StatModifier> defBuffs   = new ArrayList<>();
    private final List<StatModifier> defDebuffs = new ArrayList<>();

    private static class StatModifier {
        int amount;
        int turnsLeft;
        StatModifier(int amount, int turnsLeft) {
            this.amount    = amount;
            this.turnsLeft = turnsLeft;
        }
    }

    public StatusManager(Combatant owner) {
        this.owner = owner;
    }

    // =========================================================================
    // PUBLIC QUERY HELPERS
    // =========================================================================

    /** Returns true if the given effect is currently active. */
    public boolean has(Effect effect) {
        return activeEffects.getOrDefault(effect, 0) > 0;
    }

    /** Returns the remaining turns for the given effect (0 = not active). */
    public int turnsLeft(Effect effect) {
        return activeEffects.getOrDefault(effect, 0);
    }

    // =========================================================================
    // IMMUNITY CHECKS  (reads equipped armor — fixes the immuneEffects bug)
    // =========================================================================

    /**
     * Returns true if the owner's armor grants immunity to DoT / CC effects.
     * Covers: Poison, Bleed, Burn, Stun, Freeze, Confuse.
     */
    private boolean isImmuneToEffects() {
        var armor = owner.inventory.getEquippedArmor();
        return armor != null && armor.immuneEffects;
    }

    /**
     * Returns true if the owner's armor grants immunity to stat debuffs.
     * Covers: Weakened, Fragile.
     */
    private boolean isImmuneToDebuffs() {
        var armor = owner.inventory.getEquippedArmor();
        return armor != null && armor.immuneDebuff;
    }

    // =========================================================================
    // APPLY — CC & DoT
    // =========================================================================

    public void applyStun(List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Stun!");
            return;
        }
        activeEffects.put(Effect.STUNNED, 1);
        logs.add("💫 " + owner.name + " is Stunned!");
    }

    public void applyFreeze(List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Freeze!");
            return;
        }
        activeEffects.put(Effect.FROZEN, 1);
        logs.add("❄️ " + owner.name + " is Frozen solid!");
    }

    /**
     * Confusion now lasts for a given number of turns instead of being consumed
     * after a single check. Each turn there is a 40% chance the action is wasted.
     */
    public void applyConfuse(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Confusion!");
            return;
        }
        // Stack turns if already confused
        activeEffects.merge(Effect.CONFUSED, turns, Integer::sum);
        logs.add("🌀 " + owner.name + " is Confused for " + turns + " turn(s)!");
    }

    public void applyPoison(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Poison!");
            return;
        }
        activeEffects.merge(Effect.POISONED, turns, Integer::sum);
        logs.add("☠️ " + owner.name + " is poisoned for " + turns + " turn(s)!");
    }

    public void applyBleed(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Bleed!");
            return;
        }
        activeEffects.merge(Effect.BLEEDING, turns, Integer::sum);
        logs.add("🩸 " + owner.name + " is bleeding for " + turns + " turn(s)!");
    }

    public void applyBurn(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Burn!");
            return;
        }
        activeEffects.merge(Effect.BURNING, turns, Integer::sum);
        logs.add("🔥 " + owner.name + " is burned for " + turns + " turn(s)!");
    }

    // =========================================================================
    // APPLY — STAT DEBUFFS
    // =========================================================================

    /** Reduces the owner's ATK by {@code amount} for {@code turns} turns. */
    public void applyWeaken(int amount, int turns, List<String> logs) {
        if (isImmuneToDebuffs()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Weaken!");
            return;
        }
        atkDebuffs.add(new StatModifier(amount, turns));
        owner.attack = Math.max(1, owner.attack - amount);
        activeEffects.merge(Effect.WEAKENED, turns, Math::max); // track for UI display
        logs.add("📉 " + owner.name + "'s ATK is reduced by " + amount + " for " + turns + " turn(s)!");
    }

    /** Reduces the owner's DEF by {@code amount} for {@code turns} turns. */
    public void applyFragile(int amount, int turns, List<String> logs) {
        if (isImmuneToDebuffs()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Fragile!");
            return;
        }
        defDebuffs.add(new StatModifier(amount, turns));
        owner.defense = Math.max(0, owner.defense - amount);
        activeEffects.merge(Effect.FRAGILE, turns, Math::max); // track for UI display
        logs.add("🛡️ " + owner.name + "'s DEF is reduced by " + amount + " for " + turns + " turn(s)!");
    }

    // =========================================================================
    // APPLY — STAT BUFFS
    // =========================================================================

    /** Increases the owner's ATK by {@code amount} for {@code turns} turns. */
    public void applyStrengthen(int amount, int turns, List<String> logs) {
        atkBuffs.add(new StatModifier(amount, turns));
        owner.attack += amount;
        activeEffects.merge(Effect.STRENGTHENED, turns, Math::max);
        logs.add("💪 " + owner.name + "'s ATK is boosted by " + amount + " for " + turns + " turn(s)!");
    }

    /** Increases the owner's DEF by {@code amount} for {@code turns} turns. */
    public void applyFortify(int amount, int turns, List<String> logs) {
        defBuffs.add(new StatModifier(amount, turns));
        owner.defense += amount;
        activeEffects.merge(Effect.FORTIFIED, turns, Math::max);
        logs.add("🛡️ " + owner.name + "'s DEF is boosted by " + amount + " for " + turns + " turn(s)!");
    }

    // =========================================================================
    // TURN CHECKS — CC
    // =========================================================================

    /**
     * Call at the START of a combatant's turn.
     * Returns true if the turn should be fully skipped (Frozen or Stunned).
     */
    public boolean checkHardCC(List<String> logs) {
        if (has(Effect.FROZEN)) {
            logs.add("❄️ " + owner.name + " is Frozen solid — TURN SKIPPED!");
            activeEffects.remove(Effect.FROZEN);
            return true;
        }
        if (has(Effect.STUNNED)) {
            logs.add("💫 " + owner.name + " is Stunned — TURN SKIPPED!");
            activeEffects.remove(Effect.STUNNED);
            return true;
        }
        return false;
    }

    /**
     * Call when the combatant is about to attack.
     * Confusion now persists for multiple turns; each turn has a 40% miss chance.
     * Returns true if the attack should be cancelled this turn.
     */
    public boolean checkConfuse(List<String> logs) {
        if (!has(Effect.CONFUSED)) return false;

        // Tick down one turn of confusion
        activeEffects.merge(Effect.CONFUSED, -1, (current, delta) ->
                current + delta <= 0 ? null : current + delta);

        if (Math.random() < 0.40) {
            logs.add("🌀 " + owner.name + " is too confused to act!");
            return true; // attack is wasted this turn
        }
        return false;
    }

    // =========================================================================
    // UPDATE — DoT (call once per turn)
    // =========================================================================

    // =========================================================================
    // UPDATE — DoT (call once per turn)
    // =========================================================================

    public void updateDoTEffects(List<String> logs) {
        if (has(Effect.POISONED)) {
            int damage = Math.min(45, Math.max(1, (int)(owner.maxHp * 0.03)));
            owner.takeDamage(damage);
            logs.add("☠️ POISON deals " + damage + " damage to " + owner.name + "!");
            tick(Effect.POISONED);
        }

        if (has(Effect.BLEEDING)) {
            int missingHp = owner.maxHp - owner.currentHp;
            int damage    = Math.min(45, Math.max(1, (int)(missingHp * 0.05)));
            owner.takeDamage(damage);
            logs.add("🩸 BLEED deals " + damage + " damage to " + owner.name + "!");
            tick(Effect.BLEEDING);
        }

        if (has(Effect.BURNING)) {
            int rawBurn  = (int)((owner.maxHp * 0.02) + (owner.currentHp * 0.02));
            int burnCap  = (int)(owner.maxHp * 0.06);
            int damage   = Math.min(45, Math.max(1, Math.min(rawBurn, burnCap)));
            owner.takeDamage(damage);
            logs.add("🔥 BURN deals " + damage + " damage to " + owner.name + "!");
            tick(Effect.BURNING);
        }
    }



    // =========================================================================
    // UPDATE — STAT MODIFIERS (call once per turn)
    // =========================================================================

    public void updateStatModifiers(List<String> logs) {
        // ATK Buffs
        for (int i = atkBuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = atkBuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.attack -= mod.amount; // Remove the buff
                atkBuffs.remove(i);
                logs.add("📉 " + owner.name + "'s ATK buff wore off.");
            }
        }
        tick(Effect.STRENGTHENED); // ★ FIX: Ticks down the GUI display!

        // ATK Debuffs
        for (int i = atkDebuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = atkDebuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.attack += mod.amount; // Add the stolen stat back
                atkDebuffs.remove(i);
                logs.add("📈 " + owner.name + "'s ATK debuff expired.");
            }
        }
        tick(Effect.WEAKENED); // ★ FIX: Ticks down the GUI display!

        // DEF Buffs
        for (int i = defBuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = defBuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.defense -= mod.amount; // Remove the buff
                defBuffs.remove(i);
                logs.add("🛡️ " + owner.name + "'s DEF buff wore off.");
            }
        }
        tick(Effect.FORTIFIED); // ★ FIX: Ticks down the GUI display!

        // DEF Debuffs
        for (int i = defDebuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = defDebuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.defense += mod.amount; // Add the stolen stat back
                defDebuffs.remove(i);
                logs.add("🛡️ " + owner.name + "'s DEF debuff expired.");
            }
        }
        tick(Effect.FRAGILE); // ★ FIX: Ticks down the GUI display!
    }

    // =========================================================================
    // PRIVATE HELPER — tick one turn off an effect, removing it at 0
    // =========================================================================

    private void tick(Effect effect) {
        activeEffects.merge(effect, -1, (current, delta) ->
                current + delta <= 0 ? null : current + delta);
    }

    // =========================================================================
    // RESET ALL (called between fights)
    // =========================================================================

    public void resetAllEffects() {
        activeEffects.clear();

        atkBuffs.clear();
        atkDebuffs.clear();
        defBuffs.clear();
        defDebuffs.clear();

        owner.recalculateBuffs(); // restore base stats

        // Hard CC flags are now inside activeEffects so clear() above handles them.
    }


    // =========================================================================
    // GUI DISPLAY HELPER
    // =========================================================================

    // =========================================================================
    // GUI DISPLAY HELPER
    // =========================================================================

    public String getActiveStatusesDisplay() {
        if (activeEffects.isEmpty()) {
            return "Status: Normal";
        }

        StringBuilder sb = new StringBuilder("Status: ");
        List<String> activeList = new ArrayList<>();

        for (Map.Entry<Effect, Integer> entry : activeEffects.entrySet()) {
            Effect effect = entry.getKey();
            int turns = entry.getValue();

            String icon = switch (effect) {
                case STUNNED -> "💫";
                case FROZEN -> "❄️";
                case CONFUSED -> "🌀";
                case POISONED -> "☠️";
                case BLEEDING -> "🩸";
                case BURNING -> "🔥";
                case WEAKENED -> "📉";
                case FRAGILE -> "🔻";
                case STRENGTHENED -> "💪";
                case FORTIFIED -> "🛡️";
            };

            // Capitalize only the first letter (e.g., POISONED -> Poisoned)
            String rawName = effect.name();
            String formattedName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

            // Format: ☠️ Poisoned [2]
            activeList.add(icon + " " + formattedName + " [" + turns + "]");
        }

        sb.append(String.join(" | ", activeList));
        return sb.toString();
    }

    // =========================================================================
    // DIALOGUE BOX ANNOUNCER
    // =========================================================================

    public void announceConditions(List<String> logs) {
        if (!activeEffects.isEmpty()) {
            // Gets the UI string we made earlier, removes the "Cond: " text, and announces it!
            String conditions = getActiveStatusesDisplay().replace("Cond: ", "").trim();
            logs.add("⚠️ " + owner.name + " is currently affected by: [" + conditions + "]");
        }
    }
}