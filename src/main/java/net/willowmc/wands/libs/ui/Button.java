package net.willowmc.wands.libs.ui;

import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Button {

    private ItemStack item;
    private Consumer<InventoryClickEvent> consumer;

    private Button(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    public static Button empty(ItemStack item) {
        return of(item, e -> {});
    }

    public static Button of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new Button(item, consumer);
    }

    public void run(InventoryClickEvent e) { consumer.accept(e); }

    public ItemStack getItem() { return item; }

}
