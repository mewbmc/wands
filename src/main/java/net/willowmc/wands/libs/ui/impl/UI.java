package net.willowmc.wands.libs.ui.impl;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.InventoryListener;
import net.willowmc.wands.libs.ui.InventoryManager;
import net.willowmc.wands.libs.ui.SmartInventory;
import net.willowmc.wands.libs.ui.content.InventoryContents;
import net.willowmc.wands.libs.ui.content.InventoryProvider;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;


@Getter
@Setter
public abstract class UI {

    protected static final ItemStack RETURN_ICON = Items.create(Material.BARRIER)
        .displayName(Component.text("Return", Color.RED.plain()))
        .build();

    private final Player player;
    private final String title;
    private final int rows;

    private boolean closeable = true;
    private InventoryType type = InventoryType.CHEST;


    public UI(Player player, String title, int rows) {
        this.player = player;
        this.title = title;
        this.rows = rows;
    }

    protected void init(Player player, InventoryContents contents) {}
    protected void update(Player player, InventoryContents contents) {}

    protected void handleClose() {}

    public void open() {
        final SmartInventory inventory = SmartInventory.builder()
            .title(this.title)
            .manager(InventoryManager.get())
            .closeable(this.closeable)
            .type(this.type)
            .listener(new InventoryListener<>(InventoryCloseEvent.class, event -> this.handleClose()))
            .provider(new InventoryProvider() {
                @Override
                public void init(Player player, InventoryContents contents) {
                    UI.this.init(player, contents);
                }

                @Override
                public void update(Player player, InventoryContents contents) {
                    UI.this.update(player, contents);
                }
            })
            .size(this.rows, 9)
            .build();

        inventory.open(this.player);
    }


    public void addBackButton(InventoryContents contents, Runnable callback) {
        contents.set(this.rows - 1, 4, Button.of(RETURN_ICON, event -> callback.run()));
    }

    public void addBackButton(InventoryContents contents, int slot, Runnable callback) {
        contents.set(slot, Button.of(RETURN_ICON, event -> callback.run()));
    }

    public void close() {
        this.player.closeInventory();
    }
}
