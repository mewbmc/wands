package net.willowmc.wands.libs.ui.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.willowmc.wands.libs.ui.Button;

@Setter
@Accessors(chain = true, fluent = true)
public class ChooseUI<T> extends PaginatedUI {

    private Map<T, ItemStack> objects = new HashMap<>();
    private Consumer<T> onChoose = (object) -> {};
    private Runnable callback = () -> {};

    public ChooseUI(Player player, String title) {
        super(player, title, 6);
    }

    @Override
    protected List<Button> getButtons() {
        return this.objects.entrySet()
            .stream()
            .map(entry ->
                Button.of(entry.getValue(), event -> {
                    this.onChoose.accept(entry.getKey());
                    this.getPlayer().closeInventory();
                })).toList();
    }
}
