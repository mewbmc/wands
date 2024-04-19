package net.willowmc.wands;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.willowmc.wands.libs.commands.CommandsService;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class WandsPlugin extends JavaPlugin {

    private CommandsService commandsService;
    private WandsService service;
    private Economy economy;

    @Override
    public void onEnable() {
        this.commandsService = new CommandsService(this);
        this.service = new WandsService(this);
       // this.economy = this.setupEconomy();
    }

    private Economy setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new RuntimeException("Economy provider not found.");
        }

        return economyProvider.getProvider();
    }
}
