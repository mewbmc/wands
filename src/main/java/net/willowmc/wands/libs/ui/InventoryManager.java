package net.willowmc.wands.libs.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.willowmc.wands.libs.ui.content.InventoryContents;
import net.willowmc.wands.libs.ui.opener.ChestInventoryOpener;
import net.willowmc.wands.libs.ui.opener.InventoryOpener;
import net.willowmc.wands.libs.ui.opener.SpecialInventoryOpener;

public class InventoryManager {

    private final JavaPlugin plugin;
    private final PluginManager pluginManager;

    private final Map<UUID, SmartInventory> inventories;
    private final Map<UUID, InventoryContents> contents;

    private final List<InventoryOpener> defaultOpeners;
    private final List<InventoryOpener> openers;

    private static InventoryManager instance;

    public static InventoryManager get() {
        if (instance == null) {
            instance = new InventoryManager();
        }

        return instance;
    }

    public InventoryManager() {
        this.plugin = JavaPlugin.getProvidingPlugin(InventoryManager.class);
        this.pluginManager = Bukkit.getPluginManager();

        this.inventories = new HashMap<>();
        this.contents = new HashMap<>();

        this.defaultOpeners = Arrays.asList(
            new ChestInventoryOpener(),
            new SpecialInventoryOpener()
        );

        this.openers = new ArrayList<>();
        this.init();
    }


    public void init() {
        this.pluginManager.registerEvents(new InvListener(), this.plugin);

        new InvTask().runTaskTimer(this.plugin, 1, 20);
    }

    public Optional<InventoryOpener> findOpener(InventoryType type) {
        Optional<InventoryOpener> opInv = this.openers.stream()
            .filter(opener -> opener.supports(type))
            .findAny();

        if (!opInv.isPresent()) {
            opInv = this.defaultOpeners.stream()
                .filter(opener -> opener.supports(type))
                .findAny();
        }

        return opInv;
    }

    public void registerOpeners(InventoryOpener... openers) {
        this.openers.addAll(Arrays.asList(openers));
    }

    public List<Player> getOpenedPlayers(SmartInventory inv) {
        final List<Player> list = new ArrayList<>();

        this.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv)) {
                list.add(Bukkit.getPlayer(player));
            }
        });

        return list;
    }

    public Optional<SmartInventory> getInventory(Player p) {
        return Optional.ofNullable(this.inventories.get(p.getUniqueId()));
    }

    protected void setInventory(Player p, SmartInventory inv) {
        if (inv == null) {
            this.inventories.remove(p.getUniqueId());
        } else {
            this.inventories.put(p.getUniqueId(), inv);
        }
    }

    public Optional<InventoryContents> getContents(Player p) {
        return Optional.ofNullable(this.contents.get(p.getUniqueId()));
    }

    protected void setContents(Player p, InventoryContents contents) {
        if (contents == null) {
            this.contents.remove(p.getUniqueId());
        } else {
            this.contents.put(p.getUniqueId(), contents);
        }
    }

    public void handleInventoryOpenError(SmartInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while opening SmartInventory:", exception);
    }

    public void handleInventoryUpdateError(SmartInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while updating SmartInventory:", exception);
    }

    @SuppressWarnings("unchecked")
    class InvListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent e) {
            final Player p = (Player) e.getWhoClicked();

            if (!InventoryManager.this.inventories.containsKey(p.getUniqueId())) {
                return;
            }

            // Restrict putting items from the bottom inventory into the top inventory
            final Inventory clickedInventory = e.getClickedInventory();
            if (clickedInventory == p.getOpenInventory().getBottomInventory()) {
                if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getAction() == InventoryAction.NOTHING && e.getClick() != ClickType.MIDDLE) {
                    e.setCancelled(true);
                    return;
                }
            }

            if (clickedInventory == p.getOpenInventory().getTopInventory()) {
                e.setCancelled(true);

                final int row = e.getSlot() / 9;
                final int column = e.getSlot() % 9;

                if (row < 0 || column < 0) {
                    return;
                }

                final SmartInventory inv = InventoryManager.this.inventories.get(p.getUniqueId());

                if (row >= inv.getRows() || column >= inv.getColumns()) {
                    return;
                }

                inv.getListeners()
                    .stream()
                    .filter(listener -> listener.getType() == InventoryClickEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(e));

                InventoryManager.this.contents.get(p.getUniqueId()).get(row, column).ifPresent(item -> item.run(e));

                p.updateInventory();
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryDrag(InventoryDragEvent e) {
            final Player p = (Player) e.getWhoClicked();

            if (!InventoryManager.this.inventories.containsKey(p.getUniqueId())) {
                return;
            }

            final SmartInventory inv = InventoryManager.this.inventories.get(p.getUniqueId());

            for (final int slot : e.getRawSlots()) {
                if (slot >= p.getOpenInventory().getTopInventory().getSize()) {
                    continue;
                }

                e.setCancelled(true);
                break;
            }

            inv.getListeners()
                .stream()
                .filter(listener -> listener.getType() == InventoryDragEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryOpen(InventoryOpenEvent e) {
            final Player p = (Player) e.getPlayer();

            if (!InventoryManager.this.inventories.containsKey(p.getUniqueId())) {
                return;
            }

            final SmartInventory inv = InventoryManager.this.inventories.get(p.getUniqueId());

            inv.getListeners()
                .stream()
                .filter(listener -> listener.getType() == InventoryOpenEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent e) {
            final Player p = (Player) e.getPlayer();

            if (!InventoryManager.this.inventories.containsKey(p.getUniqueId())) {
                return;
            }

            final SmartInventory inv = InventoryManager.this.inventories.get(p.getUniqueId());

            inv.getListeners()
                .stream()
                .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(e));

            if (inv.isCloseable()) {
                e.getInventory().clear();

                InventoryManager.this.inventories.remove(p.getUniqueId());
                InventoryManager.this.contents.remove(p.getUniqueId());
            } else {
                Bukkit.getScheduler().runTask(InventoryManager.this.plugin, () -> p.openInventory(e.getInventory()));
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent e) {
            final Player p = e.getPlayer();

            if (!InventoryManager.this.inventories.containsKey(p.getUniqueId())) {
                return;
            }

            final SmartInventory inv = InventoryManager.this.inventories.get(p.getUniqueId());

            inv.getListeners()
                .stream()
                .filter(listener -> listener.getType() == PlayerQuitEvent.class)
                .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(e));

            InventoryManager.this.inventories.remove(p.getUniqueId());
            InventoryManager.this.contents.remove(p.getUniqueId());
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent e) {
            new HashMap<>(InventoryManager.this.inventories).forEach((player, inv) -> {
                inv.getListeners()
                    .stream()
                    .filter(listener -> listener.getType() == PluginDisableEvent.class)
                    .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(e));

                inv.close(Bukkit.getPlayer(player));
            });

            InventoryManager.this.inventories.clear();
            InventoryManager.this.contents.clear();
        }

    }

    class InvTask extends BukkitRunnable {
        @Override
        public void run() {
            new HashMap<>(InventoryManager.this.inventories).forEach((uuid, inv) -> {
                final Player player = Bukkit.getPlayer(uuid);

                try {
                    inv.getProvider().update(player, InventoryManager.this.contents.get(uuid));
                } catch (Exception e) {
                    InventoryManager.this.handleInventoryUpdateError(inv, player, e);
                }
            });
        }

    }

}
