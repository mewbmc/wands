package net.willowmc.wands.tasks;

import net.willowmc.wands.WandsService;
import net.willowmc.wands.libs.tasks.Task;
import net.willowmc.wands.model.SpellCooldown;

public class SpellsCooldownsTask extends Task {

    private final WandsService service;

    public SpellsCooldownsTask(WandsService service) {
        super(service.getPlugin(), 0, 20);
        this.service = service;
    }

    @Override
    protected void run() {
        this.service.getSpellCooldowns().entrySet().removeIf(entry -> {
            final SpellCooldown cooldown = entry.getValue();
            return cooldown.getEnd() <= System.currentTimeMillis();
        });
    }

}
