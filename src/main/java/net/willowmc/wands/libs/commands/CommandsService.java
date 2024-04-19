package net.willowmc.wands.libs.commands;

import cloud.commandframework.Command;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CommandsService {

    private PaperCommandManager<CommandSender> manager;

    public CommandsService(JavaPlugin plugin) {
        try {
            this.manager = new PaperCommandManager<>(
                plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public RootCommandBuilder create(String label) {
        return this.create(label, UnaryOperator.identity());
    }

    public RootCommandBuilder create(String label, UnaryOperator<Command.Builder<CommandSender>> creator) {
        return this.create(List.of(label), creator);
    }

    public RootCommandBuilder create(List<String> labels, UnaryOperator<Command.Builder<CommandSender>> creator) {
        return this.builder(labels, creator);
    }

    private RootCommandBuilder builder(List<String> labels, UnaryOperator<Command.Builder<CommandSender>> creator) {
        return new RootCommandBuilder(labels, creator);
    }

    public RootCommandBuilder create(List<String> labels) {
        return this.create(labels, UnaryOperator.identity());
    }

    public class RootCommandBuilder extends CommandBuilder<RootCommandBuilder> {
        public RootCommandBuilder(List<String> aliases, UnaryOperator<Command.Builder<CommandSender>> creator) {
            super(aliases, creator);
        }

        public void register() {
            Command.Builder<CommandSender> rootCommand = CommandsService.this.manager.commandBuilder(
                this.aliases.get(0),
                this.aliases.stream().skip(1).toArray(String[]::new)
            );

            if (this.creator != null) {
                rootCommand = this.creator.apply(rootCommand);
            }

            final Command<CommandSender> command = rootCommand.build();
            CommandsService.this.manager.command(command);

            for (final CommandBuilder subCommand : this.subCommands) {
                subCommand.register(rootCommand);
            }
        }
    }

    public class CommandBuilder<B extends CommandBuilder<B>> {
        final String label;
        final List<String> aliases;
        final List<CommandBuilder> subCommands;
        final UnaryOperator<Command.Builder<CommandSender>> creator;

        public CommandBuilder(String label, UnaryOperator<Command.Builder<CommandSender>> creator) {
            this.label = label;
            this.creator = creator;
            this.aliases = new ArrayList<>();
            this.subCommands = new ArrayList<>();
        }

        public CommandBuilder(List<String> aliases, UnaryOperator<Command.Builder<CommandSender>> creator) {
            this.label = aliases.get(0);
            this.creator = creator;
            this.aliases = aliases;
            this.subCommands = new ArrayList<>();
        }

        public B subCommand(String label, UnaryOperator<Command.Builder<CommandSender>> creator) {
            return this.subCommand(List.of(label), creator);
        }

        public B subCommand(List<String> aliases, UnaryOperator<Command.Builder<CommandSender>> creator) {
            final CommandBuilder commandBuilder = new CommandBuilder(aliases, creator);

            this.subCommands.add(commandBuilder);
            return (B) this;
        }

        public void register(Command.Builder<CommandSender> rootCommand) {
            Command.Builder<CommandSender> command = rootCommand.literal(this.aliases.get(0), this.aliases.stream().skip(1).toArray(String[]::new));
            command = this.creator.apply(command);

            CommandsService.this.manager.command(command);
        }

    }
}
