package net.willowmc.wands.libs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
public class Replacement {

    private Map<Object, Object> replacements = new HashMap<>();
    private String message = " ";

    public Replacement(String message) {
        this.message = message;
    }

    public Replacement(String[] message) {
        this.message = String.join(" ", message);
    }

    public Replacement add(Object current, Object replacement) {
        this.replacements.put(current, replacement);
        return this;
    }

    public ItemStack withItem(ItemStack item) {
        final Component title = this.withComponent(item.getItemMeta().displayName());
        final List<Component> lore = new ArrayList<>();

        if (item.getItemMeta().hasLore()) {
            final List<Component> replacedLore = item.getItemMeta().lore()
                .stream()
                .map(this::withComponent)
                .toList();

            lore.addAll(replacedLore);
        }

        return Items.edit(item)
            .displayName(title)
            .lore(lore)
            .build();
    }

    private Component withComponent(Component component) {
        final String serialized = MiniMessage.miniMessage().serialize(component);
        final Replacement replaced = this.withMessage(serialized);
        return MiniMessage.miniMessage().deserialize(replaced.toString());
    }


    public String toString() {
        this.replacements.keySet().forEach(current -> this.message = this.message.replace(String.valueOf(current), String.valueOf(this.replacements.get(current))));
        return this.message;
    }

}
