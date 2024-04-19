package net.willowmc.wands.tasks;

import net.willowmc.wands.WandsService;
import net.willowmc.wands.libs.tasks.Task;
import net.willowmc.wands.model.Wand;
import org.bukkit.Bukkit;

public class SpellsActionBarTask extends Task {

    private final WandsService service;

    public SpellsActionBarTask(WandsService service) {
        super(service.getPlugin(), 50, 20);
        this.service = service;
    }

    @Override
    protected void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Wand wand = this.service.getWandFromItem(player.getInventory().getItemInMainHand());
            if (wand == null) {
                return;
            }

            player.sendActionBar(wand.getActionbarComponent(this.service));
        });

    }
}
