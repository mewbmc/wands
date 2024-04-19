package net.willowmc.wands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.willowmc.wands.commands.WandsCommand;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.utils.Items;
import net.willowmc.wands.listener.WandsListener;
import net.willowmc.wands.model.Binding;
import net.willowmc.wands.model.Spell;
import net.willowmc.wands.model.SpellCooldown;
import net.willowmc.wands.model.Stat;
import net.willowmc.wands.model.Wand;
import net.willowmc.wands.tasks.SpellsActionBarTask;
import net.willowmc.wands.tasks.SpellsCooldownsTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Getter
public class WandsService {

    private final WandsPlugin plugin;
    private final WandsConfig config;
    private final SpellsCooldownsTask cooldownsTask;
    private final SpellsActionBarTask actionBarTask;
    private final Map<UUID, SpellCooldown> spellCooldowns;
    private final Map<UUID, Wand> cachedWands;

    public WandsService(WandsPlugin plugin) {
        this.plugin = plugin;
        this.config = new WandsConfig(this);
        this.cooldownsTask = new SpellsCooldownsTask(this);
        this.actionBarTask = new SpellsActionBarTask(this);
        this.cooldownsTask.start();
        this.actionBarTask.start();

        this.spellCooldowns = new HashMap<>();
        this.cachedWands = new HashMap<>();

        new WandsListener(this);
        new WandsCommand(this);
    }

    public Wand getWand(Player owner, String id) {
        final WandsConfig.WandBlueprint wandBlueprint = this.config.getWands().get(id);
        if (wandBlueprint == null) {
            return null;
        }

        return new Wand(id, owner.getUniqueId(), wandBlueprint.getItemStack());
    }

    public void updateItem(Wand wand, ItemStack baseItem, Player player) {
        final ItemStack updatedItem = wand.toItem(this);
        wand.updateItem(baseItem);
        this.cachedWands.put(wand.getId(), wand);
    }

    public Wand getWandFromItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        if (!item.hasItemMeta()) {
            return null;
        }

        final Items.Builder itemEditor = Items.edit(item);
        final PersistentDataContainer pdc = itemEditor.getItemMeta().getPersistentDataContainer();
        final UUID id = UUID.fromString(pdc.getOrDefault(WandsConfig.WAND_ID, PersistentDataType.STRING, UUID.randomUUID().toString()));
        if (this.cachedWands.containsKey(id)) {
            return this.cachedWands.get(id);
        }

        final UUID owner = UUID.fromString(pdc.getOrDefault(WandsConfig.WAND_OWNER, PersistentDataType.STRING, UUID.randomUUID().toString()));
        final String key = pdc.get(WandsConfig.WAND_KEY, PersistentDataType.STRING);
        final int strength = pdc.getOrDefault(Stat.STRENGTH.getKey(), PersistentDataType.INTEGER, 1);
        final int potency = pdc.getOrDefault(Stat.COOLDOWN_REDUCTION.getKey(), PersistentDataType.INTEGER, 1);
        final int manaConsumption = pdc.getOrDefault(Stat.MANA_CONSUMPTION.getKey(), PersistentDataType.INTEGER, 1);
        final int slots = pdc.getOrDefault(Stat.SLOTS.getKey(), PersistentDataType.INTEGER, 1);
        final int range = pdc.getOrDefault(Stat.RANGE.getKey(), PersistentDataType.INTEGER, 1);
        final Map<Binding, String> spells = this.getSpellsData(pdc);

        final Map<Stat, Integer> stats = new HashMap<>();
        stats.put(Stat.STRENGTH, strength);
        stats.put(Stat.COOLDOWN_REDUCTION, potency);
        stats.put(Stat.MANA_CONSUMPTION, manaConsumption);
        stats.put(Stat.SLOTS, slots);
        stats.put(Stat.RANGE, range);

        final Wand wand = new Wand();
        wand.setId(id);
        wand.setOwner(owner);
        wand.setKey(key);
        wand.setStatsLevels(stats);
        wand.setSpells(spells);
        wand.setItemStack(itemEditor.build());
        return wand;
    }

    public Spell getSpell(String id) {
        return this.config.getSpells().values().stream()
            .filter(spell -> spell.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public boolean isSpellOnCooldown(Player player, Spell spell) {
        final UUID playerId = player.getUniqueId();
        final SpellCooldown cooldown = this.spellCooldowns.get(playerId);
        if (cooldown == null) {
            return false;
        }

        return cooldown.getSpell().getId().equals(spell.getId());
    }

    private Map<Binding, String> getSpellsData(PersistentDataContainer pdc) {
        final Map<Binding, String> spells = new HashMap<>();
        final String spellsData = pdc.get(WandsConfig.SPELLS_KEY, PersistentDataType.STRING);
        if (spellsData == null) {
            return spells;
        }

        final String[] spellsArray = spellsData.split(";");
        for (final String spellData : spellsArray) {
            final String[] spellParts = spellData.split(":");
            final Binding binding = spellParts[0] == null ? null : Binding.valueOf(spellParts[0]);
            spells.put(binding, spellParts[1]);
        }

        return spells;
    }



}
