package net.willowmc.wands.libs.ui.impl;

import java.util.function.BiConsumer;
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
public class QuantityUI<T> extends UI {

    private BiConsumer<T, Integer> onFinish = (object, quantity) -> {};
    private int quantity = 0;
    private T object;
    private ItemStack icon;
    private Runnable callback = () -> {};

    public QuantityUI(Player player, String title, T object, ItemStack icon) {
        super(player, title, 6);
        this.object = object;
        this.icon = icon;
    }

    @Override
    protected void update(Player player, InventoryContents contents) {
        contents.set(30, this.getQuantityButton(-10));
        contents.set(31, this.getQuantityButton(-1));
        contents.set(32, this.getQuantityButton(1));
        contents.set(33, this.getQuantityButton(10));

        this.addBackButton(contents, 21, this.callback);
        contents.set(22, this.getQuantityInfoButton());
        contents.set(23, this.getFinishButton());
    }


    private Button getQuantityInfoButton() {
        final ItemStack icon = Items.edit(this.icon)
            .displayName(Component.text(this.quantity, Color.GOLD.plain()))
            .build();
        return Button.empty(icon);
    }

    private Button getQuantityButton(int change) {
        final Material material;
        final Component name;
        if (change == 0) {
            material = Material.YELLOW_CONCRETE;
            name = Component.text("§6Reset", Color.YELLOW.plain());
        } else if (change > 0) {
            material = Material.GREEN_CONCRETE;
            name = Component.text("+", Color.GREEN.plain());
        } else {
            material = Material.RED_CONCRETE;
            name = Component.text("-", Color.RED.plain());
        }

        final ItemStack icon = Items.create(material).displayName(name).build();
        return Button.of(icon, event -> this.quantity += change);
    }

    private Button getFinishButton() {
        final ItemStack icon = Items.create(Material.LIME_CONCRETE)
            .displayName(Component.text("Finish", Color.GREEN.plain()))
            .build();
        return Button.of(icon, event -> {
            this.onFinish.accept(this.object, this.quantity);
            this.getPlayer().closeInventory();
        });
    }



}
