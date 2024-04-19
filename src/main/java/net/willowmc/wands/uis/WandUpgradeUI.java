package net.willowmc.wands.uis;

import java.util.Map;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.ui.Button;
import net.willowmc.wands.libs.ui.content.InventoryContents;
import net.willowmc.wands.libs.ui.impl.UI;
import net.willowmc.wands.libs.utils.Replacement;
import net.willowmc.wands.model.Stat;
import net.willowmc.wands.model.Wand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandUpgradeUI extends UI {

    private final WandsService service;
    private final WandsConfig config;
    private final WandsConfig.UI uiConfig;
    private final Wand wand;
    private final ItemStack baseItem;
    private final WandsConfig.WandBlueprint.Levels configLevels;

    public WandUpgradeUI(Player player, Wand wand, ItemStack baseItem, WandsService service) {
        super(player, "Wand Menu", 6);
        this.service = service;
        this.config = service.getConfig();
        this.uiConfig = this.config.getUis();
        this.baseItem = baseItem;
        this.configLevels = this.config.getWands().get(wand.getKey()).getLevels();
        this.wand = wand;
    }

    @Override
    protected void init(Player player, InventoryContents contents) {
        int startIndex = 0;
        for (final Stat stat : Stat.values()) {
            final int maxLevels = Math.min(this.configLevels.getForStat(stat).size(), 8);
            contents.set(startIndex, this.getStatButton(stat));

            for (int i = 1; i <= maxLevels; i++) {
                contents.set(startIndex + i, this.getUpgradeButton(stat, i));
            }

            startIndex += 9;
        }

        this.addBackButton(contents, () -> new WandUI(this.getPlayer(), this.wand, this.baseItem, this.service).open());
    }

    private Button getUpgradeButton(Stat stat, int index) {
        final Map<Stat, Integer> statsLevels = this.wand.getStatsLevels();
        final Map<Integer, WandsConfig.WandBlueprint.Levels.Level> configLevels = this.configLevels.getForStat(stat);
        final boolean canUpgrade = index == statsLevels.get(stat)+1;
        final boolean alreadyUpgraded = statsLevels.get(stat) >= index;
        final Replacement replacement = new Replacement()
            .add("%level%" , String.valueOf(index))
            .add("%nextLevel%", String.valueOf(index + 1))
            .add("%cost%", String.valueOf(configLevels.get(index).getCost()))
            .add("%before%", String.valueOf(configLevels.get(Math.max(1, index - 1)).getValue()))
            .add("%after%", String.valueOf(configLevels.get(index).getValue()));

        ItemStack icon = canUpgrade ? this.uiConfig.getCanUpgradeIcon() : (alreadyUpgraded ? this.uiConfig.getAlreadyUpgradedIcon() : this.uiConfig.getCannotUpgradeIcon());
        icon = replacement.withItem(icon);

        return Button.of(icon, event -> {
            final int xp = this.getPlayer().getTotalExperience();
            final boolean hasEnoughXp = configLevels.get(index).getCost() <= this.getPlayer().getLevel();

            if (!hasEnoughXp) {
                this.getPlayer().sendMessage(this.config.getMessages().getNotEnoughExpToUpgrade());
                this.config.getMessages().getNotEnoughExpSound().play(this.getPlayer());
                return;
            }

            if (alreadyUpgraded) {
                return;
            }

            if (!canUpgrade) {
                this.getPlayer().sendMessage(this.config.getMessages().getCantUpgradeYet());
                return;
            }

            this.getPlayer().setTotalExperience(xp - configLevels.get(index).getCost());
            statsLevels.put(stat, index);
            this.service.updateItem(this.wand, this.baseItem, this.getPlayer());
            this.open();
        });
    }

    private Button getStatButton(Stat stat) {
        final ItemStack icon = this.config.getUis().getStatsButtons().get(stat);
        return Button.empty(icon);
    }


}
