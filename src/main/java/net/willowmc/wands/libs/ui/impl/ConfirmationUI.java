package net.willowmc.wands.libs.ui.impl;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.content.InventoryContents;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;

@Setter
@Accessors(chain = true, fluent = true)
public class ConfirmationUI extends UI {

    private Runnable onAccept;
    private Runnable onDeny;

    public ConfirmationUI(Player player, String title) {
        super(player, title, 3);
    }

    @Override
    protected void init(Player player, InventoryContents contents) {
        contents.set(1, 6, this.buildDeclineButton());
        contents.set(1, 2, this.buildAcceptButton());
    }

    private Button buildAcceptButton() {
        final ItemStack icon = Items.create(Material.LIME_CONCRETE)
            .displayName(Component.text("§aAccept", Color.GREEN.plain()))
            .build();

        return Button.of(icon, event -> {
            this.onAccept.run();
            this.getPlayer().closeInventory();
        });
    }

    private Button buildDeclineButton() {
        final ItemStack icon = Items.create(Material.RED_CONCRETE)
            .displayName(Component.text("§cDeny", Color.RED.plain()))
            .build();

        return Button.of(icon, event -> {
            this.onDeny.run();
            this.getPlayer().closeInventory();
        });
    }
    //
    //@Override
    //public void handleClose() {
    //    if (this.onDeny == null) {
    //        return;
    //    }
    //
    //    this.onDeny.run();
    //}

}
