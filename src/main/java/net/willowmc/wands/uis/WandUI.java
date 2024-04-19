package net.willowmc.wands.uis;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.content.InventoryContents;
import net.willowmc.wands.libs.ui.impl.ChooseUI;
import net.willowmc.wands.libs.ui.impl.ConfirmationUI;
import net.willowmc.wands.libs.ui.impl.UI;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;
import net.willowmc.wands.model.Binding;
import net.willowmc.wands.model.Spell;
import net.willowmc.wands.model.Stat;
import net.willowmc.wands.model.Wand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandUI extends UI {

    private final WandsService service;
    private final WandsConfig.UI uiConfig;
    private final ItemStack baseItem;
    private final Wand wand;

    public WandUI(Player player, Wand wand, ItemStack item, WandsService service) {
        super(player, "Wand Menu", 6);
        this.service = service;
        this.uiConfig = service.getConfig().getUis();
        this.baseItem = item;
        this.wand = wand;
    }

    @Override
    protected void init(Player player, InventoryContents contents) {
        final Map<Binding, Integer> slots = new HashMap<>(this.uiConfig.getSpellsSlots())
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        final int openedSlots = this.wand.getStatsValues(this.service).get(Stat.SLOTS).intValue();

        int index = 1;
        for (final Map.Entry<Binding, Integer> entry : slots.entrySet()) {
            final Binding binding = entry.getKey();
            final Integer slot = entry.getValue();
            final Spell spell = this.service.getSpell(this.wand.getSpells().get(binding));

            final Button button;
            if (index <= openedSlots) {
                button = this.getSpellButton(binding, spell);
            } else {
                button = this.getClosedSlotButton(binding);
            }

            contents.set(slot, button);
            index++;
        }

        contents.set(this.uiConfig.getUpgradeSlot(), this.getUpgradeButton());
        contents.set(this.uiConfig.getOwnershipSlot(), this.getOwnershipButton());
    }

    private Button getClosedSlotButton(Binding binding) {
        final ItemStack icon = this.uiConfig.getClosedSlotIcon();
        return Button.empty(icon);
    }

    private Button getSpellButton(Binding binding, @Nullable Spell spell) {
        final Component displayName = (spell == null ? this.uiConfig.getEmptySpellIcon() : spell.getIcon()).getItemMeta().displayName()
            .append(Component.text(" (" + binding.getTitle() + ")", Color.GRAY.plain()));

        final ItemStack icon = Items.edit(spell == null ? this.uiConfig.getEmptySpellIcon() : spell.getIcon())
            .displayName(displayName)
            .build();

        return Button.of(icon, event -> {
            if (event.isShiftClick()) {
                this.wand.getSpells().remove(binding);
                this.service.updateItem(this.wand, this.baseItem, this.getPlayer());
                this.open();
                return;
            }

            final SpellsUI spellsUI = new SpellsUI(this.getPlayer(), binding, this.wand, this.baseItem, this.service);
            spellsUI.open();
        });
    }

    private Button getUpgradeButton() {
        final ItemStack icon = this.uiConfig.getUpgradeIcon();

        return Button.of(icon, event -> {
            final WandUpgradeUI upgradeUI = new WandUpgradeUI(this.getPlayer(), this.wand, this.baseItem, this.service);
            upgradeUI.open();
        });
    }

    private Button getOwnershipButton() {
        final ItemStack icon = this.uiConfig.getOwnershipIcon();
        return Button.of(icon, event -> {
            final Map<Player, ItemStack> objects = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> !player.equals(this.getPlayer()))
                  .collect(Collectors.toMap(player -> player, player -> Items.create(Material.PLAYER_HEAD)
                    .displayName(Component.text(player.getName(), Color.GOLD.plain()))
                    .headOwner(player)
                    .build()));

            final ChooseUI<Player> chooseUI = new ChooseUI<Player>(this.getPlayer(), "Choose new wand owner")
                .objects(objects)
                .callback(this::open)
                .onChoose(player -> {
                    final ConfirmationUI confirmationUI = new ConfirmationUI(this.getPlayer(), "Transfer the wand to " + player.getName() + "?")
                        .onAccept(() -> {
                            this.wand.setOwner(player.getUniqueId());
                            player.getInventory().addItem(this.wand.toItem(this.service).clone());
                            this.baseItem.setAmount(this.baseItem.getAmount() - 1);
                        });

                    confirmationUI.open();
                });

            chooseUI.open();
        });
    }

}
