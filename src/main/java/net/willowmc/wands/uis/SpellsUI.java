package net.willowmc.wands.uis;

import java.util.List;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.impl.PaginatedUI;
import net.willowmc.wands.model.Binding;
import net.willowmc.wands.model.Spell;
import net.willowmc.wands.model.Wand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpellsUI extends PaginatedUI {

    private final WandsService service;
    private final WandsConfig config;
    private final ItemStack baseItem;
    private final Wand wand;
    private final Binding binding;

    public SpellsUI(Player player, Binding binding, Wand wand, ItemStack baseItem, WandsService service) {
        super(player, "Spells Menu - " + binding.getTitle(), 6);
        this.service = service;
        this.baseItem = baseItem;
        this.config = service.getConfig();
        this.wand = wand;
        this.binding = binding;
    }

    @Override
    protected List<Button> getButtons() {
        return this.config.getSpells().values()
            .stream()
            .map(this::getSpellButton)
            .toList();
    }

    @Override
    public void callback() {
        final WandUI wandUI = new WandUI(this.getPlayer(), this.wand, this.baseItem, this.service);
        wandUI.open();
    }

    private Button getSpellButton(Spell spell) {
        final ItemStack icon = spell.getIcon();

        return Button.of(icon, e -> {
            this.wand.replaceSpell(this.service, this.binding, spell);
            this.service.updateItem(this.wand, this.baseItem, this.getPlayer());

            final WandUI wandUI = new WandUI(this.getPlayer(), this.wand, this.baseItem, this.service);
            wandUI.open();
        });
    }
}
