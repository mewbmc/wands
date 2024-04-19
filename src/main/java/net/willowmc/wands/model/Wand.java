package net.willowmc.wands.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Data
@NoArgsConstructor
public class Wand {

    private UUID id;
    private UUID owner;
    private String key;
    private Map<Stat, Integer> statsLevels;
    private ItemStack itemStack;
    private Map<Binding, String> spells;

    public Wand(String key, UUID owner, ItemStack itemStack) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.key = key;
        this.itemStack = itemStack;
        this.spells = new HashMap<>();
        this.statsLevels = Map.of(
            Stat.STRENGTH, 1,
            Stat.COOLDOWN_REDUCTION, 1,
            Stat.MANA_CONSUMPTION, 1,
            Stat.SLOTS, 1,
            Stat.RANGE, 1
        );
    }

    public ItemStack toItem(WandsService service) {
        final Items.Builder itemEditor = Items.edit(this.itemStack);
        final ItemMeta meta = itemEditor.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        final WandsConfig config = service.getConfig();
        pdc.set(WandsConfig.WAND_ID, PersistentDataType.STRING, this.id.toString());
        pdc.set(WandsConfig.WAND_KEY, PersistentDataType.STRING, this.key);
        pdc.set(WandsConfig.WAND_OWNER, PersistentDataType.STRING, this.owner.toString());
        pdc.set(Stat.STRENGTH.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.STRENGTH, 1));
        pdc.set(Stat.COOLDOWN_REDUCTION.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.COOLDOWN_REDUCTION, 1));
        pdc.set(Stat.MANA_CONSUMPTION.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.MANA_CONSUMPTION, 1));
        pdc.set(Stat.SLOTS.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.SLOTS, 1));
        pdc.set(Stat.RANGE.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.RANGE, 1));
        pdc.set(WandsConfig.SPELLS_KEY, PersistentDataType.STRING, this.getSpellsData());
        itemEditor.itemMeta(meta);
        return itemEditor.build();
    }


    public void replaceSpell(WandsService service, Binding binding, Spell spell) {
        this.spells.values().removeIf(spellId -> spellId != null && spellId.equals(spell.getId()));
        this.spells.put(binding, spell.getId());
    }

    public void updateItem(ItemStack baseItem) {
        final ItemMeta meta = baseItem.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(WandsConfig.WAND_ID, PersistentDataType.STRING, this.id.toString());
        pdc.set(WandsConfig.WAND_KEY, PersistentDataType.STRING, this.key);
        pdc.set(WandsConfig.WAND_OWNER, PersistentDataType.STRING, this.owner.toString());
        pdc.set(Stat.STRENGTH.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.STRENGTH, 1));
        pdc.set(Stat.COOLDOWN_REDUCTION.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.COOLDOWN_REDUCTION, 1));
        pdc.set(Stat.MANA_CONSUMPTION.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.MANA_CONSUMPTION, 1));
        pdc.set(Stat.SLOTS.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.SLOTS, 1));
        pdc.set(Stat.RANGE.getKey(), PersistentDataType.INTEGER, this.statsLevels.getOrDefault(Stat.RANGE, 1));
        pdc.set(WandsConfig.SPELLS_KEY, PersistentDataType.STRING, this.getSpellsData());
        baseItem.setItemMeta(meta);
    }

    private String getSpellsData() {
        final StringBuilder spellsData = new StringBuilder();
        for (final Binding binding : Binding.values()) {
            spellsData.append(binding.name())
                .append(":")
                .append(this.spells.get(binding) == null ? "null" : this.spells.get(binding))
                .append(";");
        }

        return spellsData.toString();
    }

    public Map<Stat, Double> getStatsValues(WandsService service) {
        final WandsConfig config = service.getConfig();
        final Map<Stat, Double> statsValues = new HashMap<>();
        final WandsConfig.WandBlueprint.Levels levels = config.getWands().get(this.key).getLevels();

        this.statsLevels.forEach((stat, level) -> {
            final WandsConfig.WandBlueprint.Levels.Level statLevel = levels.getForStat(stat).get(level);
            final double value = statLevel == null ? 1 : statLevel.getValue();
            statsValues.put(stat, value);
        });

        return statsValues;
    }

    public Component getActionbarComponent(WandsService service) {
        Component component = Component.empty();
        for (final Binding binding : Binding.values()) {
            final Spell spell = service.getSpell(this.spells.get(binding));
            component = component.append(Component.text(binding.getShortcut()))
                .append(Component.text(" - "))
                .append(spell == null ? Component.text("Empty", Color.RED.plain()) : spell.getIcon().getItemMeta().displayName())
                .append(Component.text("  "));
        }

        return component;
    }

}
