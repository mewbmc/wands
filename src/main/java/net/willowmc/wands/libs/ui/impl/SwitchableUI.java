package net.willowmc.wands.libs.ui.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;

@Setter
@Accessors(chain = true, fluent = true)
public class SwitchableUI<T> extends PaginatedUI {

    private List<Switchable<T>> objects = new ArrayList<>();
    private BiConsumer<T, Boolean> onSwitch = (object, condition) -> {};
    private Runnable callback = () -> {};

    public SwitchableUI(Player player, String title) {
        super(player, title, 6);
    }

    @Override
    protected List<Button> getButtons() {
        return this.objects
            .stream()
            .map(switchable -> {
                final Component originalDisplayName = switchable.icon().getItemMeta().displayName();
                final Component displayName = originalDisplayName
                    .append(Component.text(" - "))
                    .append(switchable.state() ? Component.text("ON", Color.GREEN.plain()) : Component.text("OFF", Color.RED.plain()));

                final ItemStack icon = Items.edit(switchable.icon())
                    .displayName(displayName)
                    .glow(switchable.state())
                    .build();

                return Button.of(icon, event -> {
                    this.onSwitch.accept(switchable.object(), switchable.state());
                    switchable.state(!switchable.state());
                    this.open();
                });
            }).toList();
    }

    @Override
    public void callback() {
        this.callback.run();
    }

    @Getter
    @Setter
    public static class Switchable<T> {
        private final T object;
        private final ItemStack icon;
        private boolean state;

        public Switchable(T object, ItemStack icon, boolean state) {
            this.object = object;
            this.icon = icon;
            this.state = state;
        }
    }

}
