package GameGUI.model.logic;

import GameGUI.model.entity.Combatant;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class StatusManager {
    private final Combatant owner;

    public enum Effect {
        STUNNED, FROZEN, CONFUSED,
        POISONED, BLEEDING, BURNING,
        WEAKENED, FRAGILE,
        STRENGTHENED, FORTIFIED
    }

    private final Map<Effect, Integer> activeEffects = new EnumMap<>(Effect.class);

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

    public boolean has(Effect effect) {
        return activeEffects.getOrDefault(effect, 0) > 0;
    }

    public int turnsLeft(Effect effect) {
        return activeEffects.getOrDefault(effect, 0);
    }

    private boolean isImmuneToEffects() {
        var armor = owner.inventory.getEquippedArmor();
        return armor != null && armor.immuneEffects;
    }

    private boolean isImmuneToDebuffs() {
        var armor = owner.inventory.getEquippedArmor();
        return armor != null && armor.immuneDebuff;
    }

    // =========================================================================
    // APPLY — CC & DoT (Guarded against 0-turn applications)
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

    public void applyConfuse(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Confusion!");
            return;
        }
        if (turns > 0) {
            activeEffects.merge(Effect.CONFUSED, turns, Integer::sum);
            logs.add("🌀 " + owner.name + " is Confused for " + turns + " turn(s)!");
        }
    }

    public void applyPoison(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Poison!");
            return;
        }
        if (turns > 0) {
            activeEffects.merge(Effect.POISONED, turns, Integer::sum);
            logs.add("☠️ " + owner.name + " is poisoned for " + turns + " turn(s)!");
        }
    }

    public void applyBleed(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Bleed!");
            return;
        }
        if (turns > 0) {
            activeEffects.merge(Effect.BLEEDING, turns, Integer::sum);
            logs.add("🩸 " + owner.name + " is bleeding for " + turns + " turn(s)!");
        }
    }

    public void applyBurn(int turns, List<String> logs) {
        if (isImmuneToEffects()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Burn!");
            return;
        }
        if (turns > 0) {
            activeEffects.merge(Effect.BURNING, turns, Integer::sum);
            logs.add("🔥 " + owner.name + " is burned for " + turns + " turn(s)!");
        }
    }

    // =========================================================================
    // APPLY — STAT MODIFIERS (Guarded against 0-turn applications)
    // =========================================================================

    public void applyWeaken(int amount, int turns, List<String> logs) {
        if (isImmuneToDebuffs()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Weaken!");
            return;
        }
        atkDebuffs.add(new StatModifier(amount, turns));
        owner.attack = Math.max(1, owner.attack - amount);
        if (turns > 0) {
            activeEffects.merge(Effect.WEAKENED, turns, Math::max);
        }
        logs.add("📉 " + owner.name + "'s ATK is reduced by " + amount + " for " + turns + " turn(s)!");
    }

    public void applyFragile(int amount, int turns, List<String> logs) {
        if (isImmuneToDebuffs()) {
            logs.add("🛡️ " + owner.name + "'s armor resists the Fragile!");
            return;
        }
        defDebuffs.add(new StatModifier(amount, turns));
        owner.defense = Math.max(0, owner.defense - amount);
        if (turns > 0) {
            activeEffects.merge(Effect.FRAGILE, turns, Math::max);
        }
        logs.add("🛡️ " + owner.name + "'s DEF is reduced by " + amount + " for " + turns + " turn(s)!");
    }

    public void applyStrengthen(int amount, int turns, List<String> logs) {
        atkBuffs.add(new StatModifier(amount, turns));
        owner.attack += amount;
        if (turns > 0) {
            activeEffects.merge(Effect.STRENGTHENED, turns, Math::max);
        }
        logs.add("💪 " + owner.name + "'s ATK is boosted by " + amount + " for " + turns + " turn(s)!");
    }

    public void applyFortify(int amount, int turns, List<String> logs) {
        defBuffs.add(new StatModifier(amount, turns));
        owner.defense += amount;
        if (turns > 0) {
            activeEffects.merge(Effect.FORTIFIED, turns, Math::max);
        }
        logs.add("🛡️ " + owner.name + "'s DEF is boosted by " + amount + " for " + turns + " turn(s)!");
    }

    // =========================================================================
    // UPDATE LOGIC (Ticks and Expirations)
    // =========================================================================

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

    public boolean checkConfuse(List<String> logs) {
        if (!has(Effect.CONFUSED)) return false;
        tick(Effect.CONFUSED);
        if (Math.random() < 0.40) {
            logs.add("🌀 " + owner.name + " is too confused to act!");
            return true;
        }
        return false;
    }

    public void updateDoTEffects(List<String> logs) {
        if (has(Effect.POISONED)) {
            int damage = Math.min(45, Math.max(1, (int)(owner.getMaxHp() * 0.03)));
            owner.takeDamage(damage);
            logs.add("☠️ POISON deals " + damage + " damage to " + owner.name + "!");
            tick(Effect.POISONED);
        }
        if (has(Effect.BLEEDING)) {
            int missingHp = owner.getMaxHp() - owner.getCurrentHp();
            int damage    = Math.min(45, Math.max(1, (int)(missingHp * 0.05)));
            owner.takeDamage(damage);
            logs.add("🩸 BLEED deals " + damage + " damage to " + owner.name + "!");
            tick(Effect.BLEEDING);
        }
        if (has(Effect.BURNING)) {
            int rawBurn  = (int)((owner.getMaxHp() * 0.02) + (owner.getCurrentHp() * 0.02));
            int burnCap  = (int)(owner.getMaxHp() * 0.06);
            int damage   = Math.min(45, Math.max(1, Math.min(rawBurn, burnCap)));
            owner.takeDamage(damage);
            logs.add("🔥 BURN deals " + damage + " damage to " + owner.name + "!");
            tick(Effect.BURNING);
        }
    }

    public void updateStatModifiers(List<String> logs) {
        for (int i = atkBuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = atkBuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.attack -= mod.amount;
                atkBuffs.remove(i);
                logs.add("📉 " + owner.name + "'s ATK buff wore off.");
            }
        }
        tick(Effect.STRENGTHENED);

        for (int i = atkDebuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = atkDebuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.attack += mod.amount;
                atkDebuffs.remove(i);
                logs.add("📈 " + owner.name + "'s ATK debuff expired.");
            }
        }
        tick(Effect.WEAKENED);

        for (int i = defBuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = defBuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.defense -= mod.amount;
                defBuffs.remove(i);
                logs.add("🛡️ " + owner.name + "'s DEF buff wore off.");
            }
        }
        tick(Effect.FORTIFIED);

        for (int i = defDebuffs.size() - 1; i >= 0; i--) {
            StatModifier mod = defDebuffs.get(i);
            if (--mod.turnsLeft <= 0) {
                owner.defense += mod.amount;
                defDebuffs.remove(i);
                logs.add("🛡️ " + owner.name + "'s DEF debuff expired.");
            }
        }
        tick(Effect.FRAGILE);
    }

    private void tick(Effect effect) {
        activeEffects.merge(effect, -1, (current, delta) ->
                current + delta <= 0 ? null : current + delta);
    }

    public void resetAllEffects() {
        activeEffects.clear();
        atkBuffs.clear();
        atkDebuffs.clear();
        defBuffs.clear();
        defDebuffs.clear();
        owner.recalculateBuffs();
    }

    public String getActiveStatusesDisplay() {
        if (activeEffects.isEmpty()) return "Status: Normal";
        StringBuilder sb = new StringBuilder("Status: ");
        List<String> activeList = new ArrayList<>();
        for (Map.Entry<Effect, Integer> entry : activeEffects.entrySet()) {
            if (entry.getValue() <= 0) continue;
            Effect effect = entry.getKey();
            String icon = switch (effect) {
                case STUNNED -> "💫"; case FROZEN -> "❄️"; case CONFUSED -> "🌀";
                case POISONED -> "☠️"; case BLEEDING -> "🩸"; case BURNING -> "🔥";
                case WEAKENED -> "📉"; case FRAGILE -> "🔻";
                case STRENGTHENED -> "💪"; case FORTIFIED -> "🛡️";
            };
            String formattedName = effect.name().substring(0, 1).toUpperCase() + effect.name().substring(1).toLowerCase();
            activeList.add(icon + " " + formattedName + " [" + entry.getValue() + "]");
        }
        sb.append(String.join(" | ", activeList));
        return sb.toString();
    }

    public void announceConditions(List<String> logs) {
        if (!activeEffects.isEmpty()) {
            String conditions = getActiveStatusesDisplay().replace("Status: ", "").trim();
            logs.add("⚠️ " + owner.name + " is currently affected by: [" + conditions + "]");
        }
    }
}