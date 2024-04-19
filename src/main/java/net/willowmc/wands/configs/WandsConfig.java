package net.willowmc.wands.configs;

import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.libs.configs.Config;
import net.willowmc.wands.libs.configs.annotations.Ignore;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;
import net.willowmc.wands.libs.utils.Sound;
import net.willowmc.wands.model.Binding;
import net.willowmc.wands.model.Spell;
import net.willowmc.wands.model.Stat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@Getter
@NoArgsConstructor
public class WandsConfig extends Config {

    @Ignore public static final NamespacedKey SPELLS_KEY = new NamespacedKey("wands", "spells");
    @Ignore public static final NamespacedKey WAND_ID = new NamespacedKey("wands", "wand_id");
    @Ignore public static final NamespacedKey WAND_KEY = new NamespacedKey("wands", "wand");
    @Ignore public static final NamespacedKey WAND_OWNER = new NamespacedKey("wands", "wand_owner");

    private Map<String, Spell> spells = Map.of(
        "spell1", new Spell(),
        "spell2", new Spell()
    );

    private Map<String, WandBlueprint> wands = Map.of(
        "wand1", new WandBlueprint(),
        "wand2", new WandBlueprint()
    );

    private UI uis = new UI();
    private Messages messages = new Messages();

    public WandsConfig(WandsService service) {
        super("config.yml", service.getPlugin().getDataFolder().getPath());
        this.reload();
    }

    @Getter
    @NoArgsConstructor
    public static class UI {
        private int upgradeSlot = 39;
        private int ownershipSlot = 41;

        private Map<Binding, Integer> spellsSlots = Map.of(
            Binding.LEFT_CLICK, 20,
            Binding.RIGHT_CLICK, 21,
            Binding.SHIFT_LEFT_CLICK, 23,
            Binding.SHIFT_RIGHT_CLICK, 24
        );

        private Map<Stat, ItemStack> statsButtons = Map.of(
            Stat.STRENGTH, Items.create(Material.IRON_SWORD)
                .displayName(Component.text("Strength", Color.GOLD.plain()))
                .build(),
            Stat.COOLDOWN_REDUCTION, Items.create(Material.DIAMOND)
                .displayName(Component.text("Potency", Color.GOLD.plain()))
                .build(),
            Stat.MANA_CONSUMPTION, Items.create(Material.LAPIS_LAZULI)
                .displayName(Component.text("Mana Consumption", Color.GOLD.plain()))
                .build(),
            Stat.SLOTS, Items.create(Material.CHEST)
                .displayName(Component.text("Slots", Color.GOLD.plain()))
                .build(),
            Stat.RANGE, Items.create(Material.BOW)
                .displayName(Component.text("Range", Color.GOLD.plain()))
                .build()
        );

        private ItemStack closedSlotIcon = Items.create(Material.BLACK_STAINED_GLASS_PANE)
            .displayName(Component.text("Locked", Color.RED.plain()))
            .build();

        private ItemStack emptySpellIcon = Items.create(Material.BARRIER)
            .displayName(Component.text("Empty", Color.RED.plain()))
            .build();

        private ItemStack upgradeIcon = Items.create(Material.NETHER_STAR)
            .displayName(Component.text("Upgrade", Color.GOLD.plain()))
            .build();

        private ItemStack ownershipIcon = Items.create(Material.PLAYER_HEAD)
            .displayName(Component.text("Ownership", Color.GOLD.plain()))
            .build();

        private ItemStack alreadyUpgradedIcon = Items.create(Material.BLUE_CONCRETE)
            .displayName(Component.text("Already Upgraded", Color.BLUE.plain()))
            .build();

        private ItemStack cannotUpgradeIcon = Items.create(Material.RED_CONCRETE)
            .displayName(Component.text("Cannot Upgrade", Color.RED.plain()))
            .loreLines(
                Component.text("Cost: <cost>", Color.GRAY.get()),
                Component.text("Change: <before> -> <after>", Color.GRAY.get())
            ).build();

        private ItemStack canUpgradeIcon = Items.create(Material.LIME_CONCRETE)
            .displayName(Component.text("Can Upgrade", Color.GREEN.plain()))
            .loreLines(
                Component.text("Cost: <cost>", Color.GRAY.get()),
                Component.text("Change: <before> -> <after>", Color.GRAY.get())
            )
            .build();

    }


    @Getter
    @NoArgsConstructor
    public static class Messages {
        private Component notEnoughMana = Component.text("You don't have enough mana to cast this spell!", Color.RED.plain());
        private Component notEnoughExpToUpgrade = Component.text("You don't have enough experience to upgrade this wand!", Color.RED.plain());
        private Component notAWand = Component.text("This item is not a wand!", Color.RED.plain());
        private Component cantUpgradeYet = Component.text("You can't upgrade this yet!", Color.RED.plain());
        private Component spellOnCooldown = Component.text("This spell is on cooldown!", Color.RED.plain());
        private Sound notEnoughExpSound = Sound.of(org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
        private Sound notEnoughManaSound = Sound.of(org.bukkit.Sound.ENTITY_VILLAGER_HURT, 1.0F, 1.0F);
        private Sound cooldownSound = Sound.of(org.bukkit.Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
        private Sound upgradeSound = Sound.of(org.bukkit.Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
    }

    @Getter
    @NoArgsConstructor
    public static class WandBlueprint {
        private ItemStack itemStack = Items.create(Material.STICK)
            .displayName(Component.text("Wand Name", Color.GOLD.plain()))
            .lore(Component.text("Wand Description", Color.GRAY.plain()))
            .customModelData(10)
            .build();

        private Levels levels = new Levels();

        @Getter
        @NoArgsConstructor
        public static class Levels {
            private Map<Integer, Level> strength = Map.of(1, new Level());
            private Map<Integer, Level> cooldownReduction = Map.of(1, new Level());
            private Map<Integer, Level> manaConsumption = Map.of(1, new Level());
            private Map<Integer, Level> slots = Map.of(1, new Level());
            private Map<Integer, Level> range = Map.of(1, new Level());

            public Map<Integer, Level> getForStat(Stat stat) {
                return switch (stat) {
                    case STRENGTH -> this.strength;
                    case COOLDOWN_REDUCTION -> this.cooldownReduction;
                    case MANA_CONSUMPTION -> this.manaConsumption;
                    case SLOTS -> this.slots;
                    case RANGE -> this.range;
                };
            }

            @Getter
            @NoArgsConstructor
            public static class Level {
                private int cost = 100;
                private double value = 1.0;
            }

        }
    }

}
