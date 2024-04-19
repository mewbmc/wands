package net.willowmc.wands.commands;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.willowmc.wands.WandsPlugin;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.model.Wand;
import net.willowmc.wands.uis.WandUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandsCommand {

    private final WandsService service;
    private final WandsConfig config;

    public WandsCommand(WandsService service) {
        this.service = service;
        this.config = service.getConfig();
        service.getPlugin().getCommandsService()
            .create("wand", this::menu)
            .subCommand("give", this::give)
            .subCommand("reload", this::reload)
            .register();
    }

    private Command.Builder<CommandSender> menu(Command.Builder<CommandSender> command) {
        return command
            .handler(context -> {
                final Player player = (Player) context.getSender();
                final ItemStack item = player.getInventory().getItemInMainHand();
                final Wand wand = this.service.getWandFromItem(item);
                if (wand == null) {
                    player.sendMessage(this.config.getMessages().getNotAWand());
                    return;
                }

                final WandUI menu = new WandUI(player, wand, item, this.service);
                menu.open();
            });
    }


    private Command.Builder<CommandSender> reload(Command.Builder<CommandSender> command) {
        return command
            .permission("wands.command.reload")
            .handler(context -> {
                this.service.getConfig().reload();
                context.getSender().sendMessage("§aConfiguration reloaded");
            });
    }

    private Command.Builder<CommandSender> give(Command.Builder<CommandSender> command) {
        final StringArgument wandIdsArgument = StringArgument.builder("wandId")
            .withSuggestionsProvider((context, input) -> this.service.getConfig().getWands().keySet().stream().toList())
            .build();

        return command
            .permission("wands.command.give")
            .argument(PlayerArgument.of("target"))
            .argument(wandIdsArgument)
            .handler(context -> {
                final CommandSender sender = (CommandSender) context.getSender();
                final Player target = (Player) context.get("target");
                final String wandId = (String) context.get("wandId");
                final Wand wand = this.service.getWand(target, wandId);
                if (wand == null) {
                    sender.sendMessage("§cInvalid wand id");
                    return;
                }

                target.getInventory().addItem(wand.toItem(this.service));
                sender.sendMessage(Component.text("Wand given to " + target.getName(), Color.GOLD.plain()));
            });
    }

}
