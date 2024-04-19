package net.willowmc.wands.libs.ui.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.SmartInventory;

public interface InventoryContents {

    SmartInventory inventory();

    Pagination pagination();

    Optional<SlotIterator> iterator(String id);

    SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn);

    SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn);

    SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos);

    SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos);

    Button[][] all();

    Optional<SlotPos> firstEmpty();

    Optional<Button> get(int row, int column);

    Optional<Button> get(SlotPos slotPos);

    InventoryContents set(int row, int column, Button item);

    InventoryContents set(int rawSlot, Button item);

    InventoryContents set(SlotPos slotPos, Button item);

    InventoryContents add(Button item);

    InventoryContents fill(Button item);

    InventoryContents fillRow(int row, Button item);

    InventoryContents fillColumn(int column, Button item);

    InventoryContents fillBorders(Button item);

    InventoryContents fillRect(int fromRow, int fromColumn,
                               int toRow, int toColumn, Button item);

    InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, Button item);

    <T> T property(String name);

    <T> T property(String name, T def);

    InventoryContents setProperty(String name, Object value);

    void clear();

    class Impl implements InventoryContents {

        private final SmartInventory inv;
        private final UUID player;

        private final Button[][] contents;

        private final Pagination pagination = new Pagination.Impl();
        private final Map<String, SlotIterator> iterators = new HashMap<>();
        private final Map<String, Object> properties = new HashMap<>();

        public Impl(SmartInventory inv, UUID player) {
            this.inv = inv;
            this.player = player;
            this.contents = new Button[inv.getRows()][inv.getColumns()];
        }

        @Override
        public SmartInventory inventory() {
            return this.inv;
        }

        @Override
        public Pagination pagination() {
            return this.pagination;
        }

        @Override
        public Optional<SlotIterator> iterator(String id) {
            return Optional.ofNullable(this.iterators.get(id));
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
            final SlotIterator iterator = new SlotIterator.Impl(this, this.inv,
                type, startRow, startColumn
            );

            this.iterators.put(id, iterator);
            return iterator;
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
            return new SlotIterator.Impl(this, this.inv, type, startRow, startColumn);
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos) {
            return this.newIterator(id, type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos) {
            return this.newIterator(type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public Button[][] all() {
            return this.contents;
        }

        @Override
        public Optional<SlotPos> firstEmpty() {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    if (!this.get(row, column).isPresent()) {
                        return Optional.of(new SlotPos(row, column));
                    }
                }
            }

            return Optional.empty();
        }

        @Override
        public Optional<Button> get(int row, int column) {
            if (row >= this.contents.length) {
                return Optional.empty();
            }
            if (column >= this.contents[row].length) {
                return Optional.empty();
            }

            return Optional.ofNullable(this.contents[row][column]);
        }

        @Override
        public Optional<Button> get(SlotPos slotPos) {
            return this.get(slotPos.getRow(), slotPos.getColumn());
        }

        @Override
        public InventoryContents set(int row, int column, Button item) {
            if (row >= this.contents.length) {
                return this;
            }
            if (column >= this.contents[row].length) {
                return this;
            }

            this.contents[row][column] = item;
            this.update(row, column, item != null ? item.getItem() : null);
            return this;
        }

        @Override
        public InventoryContents set(int rowSlot, Button item) {
            final int row = rowSlot / this.inv.getColumns();
            final int column = rowSlot % this.inv.getColumns();
            this.contents[row][column] = item;
            this.update(row, column, item != null ? item.getItem() : null);
            return this;
        }

        @Override
        public InventoryContents set(SlotPos slotPos, Button item) {
            return this.set(slotPos.getRow(), slotPos.getColumn(), item);
        }

        @Override
        public InventoryContents add(Button item) {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    if (this.contents[row][column] == null) {
                        this.set(row, column, item);
                        return this;
                    }
                }
            }

            return this;
        }

        @Override
        public InventoryContents fill(Button item) {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[row].length; column++) {
                    this.set(row, column, item);
                }
            }

            return this;
        }

        @Override
        public InventoryContents fillRow(int row, Button item) {
            if (row >= this.contents.length) {
                return this;
            }

            for (int column = 0; column < this.contents[row].length; column++) {
                this.set(row, column, item);
            }

            return this;
        }

        @Override
        public InventoryContents fillColumn(int column, Button item) {
            for (int row = 0; row < this.contents.length; row++) {
                this.set(row, column, item);
            }

            return this;
        }

        @Override
        public InventoryContents fillBorders(Button item) {
            this.fillRect(0, 0, this.inv.getRows() - 1, this.inv.getColumns() - 1, item);
            return this;
        }

        @Override
        public InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Button item) {
            for (int row = fromRow; row <= toRow; row++) {
                for (int column = fromColumn; column <= toColumn; column++) {
                    if (row != fromRow && row != toRow && column != fromColumn && column != toColumn) {
                        continue;
                    }

                    this.set(row, column, item);
                }
            }

            return this;
        }

        @Override
        public InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, Button item) {
            return this.fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name) {
            return (T) this.properties.get(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name, T def) {
            return this.properties.containsKey(name) ? (T) this.properties.get(name) : def;
        }

        @Override
        public InventoryContents setProperty(String name, Object value) {
            this.properties.put(name, value);
            return this;
        }

        @Override
        public void clear() {
            for (int row = 0; row < this.contents.length; row++) {
                for (int column = 0; column < this.contents[0].length; column++) {
                    this.set(row, column, null);
                }
            }
        }

        private void update(int row, int column, ItemStack item) {
            final Player currentPlayer = Bukkit.getPlayer(this.player);
            if (!this.inv.getManager().getOpenedPlayers(this.inv).contains(currentPlayer)) {
                return;
            }

            final Inventory topInventory = currentPlayer.getOpenInventory().getTopInventory();
            topInventory.setItem(this.inv.getColumns() * row + column, item);
        }

    }

}