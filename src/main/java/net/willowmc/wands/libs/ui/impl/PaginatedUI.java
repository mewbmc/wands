package net.willowmc.wands.libs.ui.impl;

import java.util.List;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.content.InventoryContents;
import net.willowmc.wands.libs.ui.content.Pagination;
import net.willowmc.wands.libs.ui.content.SlotIterator;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;

@Setter
public abstract class PaginatedUI extends UI {

    protected static final ItemStack NEXT_PAGE_ICON = Items.create(Material.ARROW)
        .displayName(Component.text("Next Page", Color.RED.plain()))
        .build();

    protected static final ItemStack PREVIOUS_PAGE_ICON = Items.create(Material.ARROW)
        .displayName(Component.text("Previous Page", Color.RED.plain()))
        .build();

    protected static final ItemStack UPDATE_ICON = Items.create(Material.COMPASS)
        .displayName(Component.text("Update", Color.GREEN.plain()))
        .build();


    public PaginatedUI(Player player, String title) {
        super(player, title, 6);
    }

    public PaginatedUI(Player player, String title, int rows) {
        super(player, title, rows);
    }

    @Override
    protected void init(Player player, InventoryContents contents) {
        final Pagination pagination = contents.pagination();
        pagination.setItemsPerPage(this.getRows() * 9 - 9);
        pagination.setItems(this.getButtons().toArray(new Button[0]));
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        contents.set(this.getRows() - 1, 0, this.getPreviousPageButton(pagination));
        contents.set(this.getRows() - 1, 4, this.getReturnButton());
        contents.set(this.getRows() - 1, 5, this.getUpdateButton());
        contents.set(this.getRows() - 1, 8, this.getNextPageButton(pagination));
    }

    public Button getNextPageButton(Pagination pagination) {
        return Button.of(NEXT_PAGE_ICON, event -> {
            if (pagination.isLast()) {
                return;
            }

            pagination.next();
        });
    }
    public Button getPreviousPageButton(Pagination pagination) {
        return Button.of(PREVIOUS_PAGE_ICON, event -> {
            if (pagination.isFirst()) {
                return;
            }

            pagination.previous();
        });
    }

    private Button getUpdateButton() {
        return Button.of(UPDATE_ICON, event -> this.open());
    }


    public Button getReturnButton() {
        return Button.of(RETURN_ICON, event -> this.callback());
    }

    protected abstract List<Button> getButtons();
    public void callback() {
        this.close();
    }

}
