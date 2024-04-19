package net.willowmc.wands.listener;

import java.util.UUID;
import net.willowmc.wands.WandsPlugin;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.model.Binding;
import net.willowmc.wands.model.Spell;
import net.willowmc.wands.model.Wand;
import net.willowmc.wands.uis.WandUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class WandsListener implements Listener {

    private final WandsService service;
    private final WandsPlugin plugin;

    public WandsListener(WandsService service) {
        this.service = service;
        this.plugin = service.getPlugin();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        final Wand wand = this.service.getWandFromItem(item);
        if (wand == null) {
            return;
        }

        final Player player = event.getPlayer();
        final Binding binding = Binding.fromAction(event.getAction(), player.isSneaking());
        final Spell spell = this.service.getSpell(wand.getSpells().get(binding));
        if (spell == null) {
            return;
        }

        spell.cast(this.service, wand, player);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (this.service.getCachedWands().values().stream().noneMatch(wand -> wand.getOwner().equals(uuid))) {
            return;
        }

        this.service.getCachedWands().entrySet().removeIf(entry -> entry.getValue().getOwner().equals(uuid));
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        final ItemStack item = event.getItemDrop().getItemStack();
        final Wand wand = this.service.getWandFromItem(item);
        if (wand == null) {
            return;
        }

        event.setCancelled(true);
        final WandUI ui = new WandUI(event.getPlayer(), wand, item, this.service);
        ui.open();
    }

}
