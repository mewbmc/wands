package net.willowmc.wands.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.NamespacedKey;

@Getter
@AllArgsConstructor
public enum Stat {

    STRENGTH(new NamespacedKey("wands", "strength")),
    COOLDOWN_REDUCTION(new NamespacedKey("wands", "cooldown_reduction")),
    MANA_CONSUMPTION(new NamespacedKey("wands", "mana_consumption")),
    SLOTS(new NamespacedKey("wands", "slots")),
    RANGE(new NamespacedKey("wands", "range"));

    private final NamespacedKey key;

}
