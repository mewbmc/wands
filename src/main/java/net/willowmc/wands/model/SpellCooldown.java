package net.willowmc.wands.model;

import java.util.UUID;
import lombok.Getter;
import net.willowmc.wands.WandsService;

@Getter
public class SpellCooldown {

    private final UUID player;
    private final Spell spell;
    private final long cooldown;
    private final long end;

    public SpellCooldown(UUID player, WandsService service, Wand wand, Spell spell) {
        this.player = player;
        this.spell = spell;
        this.cooldown = this.getCooldown(service, wand, spell);
        this.end = System.currentTimeMillis() + this.cooldown * 1000;
    }

    private int getCooldown(WandsService service, Wand wand, Spell spell) {
        final double potencyValue = wand.getStatsValues(service).get(Stat.COOLDOWN_REDUCTION).doubleValue();
        return (int) (this.getBaseCooldown(spell) * (1 - potencyValue / 100));
    }

    private int getBaseCooldown(Spell spell) {
        return spell.toMythic().getConfig().getInt("Cooldown", 0);
    }

}
