package net.willowmc.wands.libs.tasks;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Accessors(fluent = true)
public abstract class Task {

    private final JavaPlugin plugin;
    private final int delay;
    private final int period;
    private final boolean async;
    private int iterations;

    private int taskId = -1;

    public Task(JavaPlugin plugin, int delay, int period) {
        this.plugin = plugin;
        this.delay = delay;
        this.period = period;
        this.async = true;
    }
    public Task(JavaPlugin plugin, int delay, int period, boolean async) {
        this.plugin = plugin;
        this.delay = delay;
        this.period = period;
        this.async = async;
    }
    public Task(JavaPlugin plugin, int period, boolean async) {
        this.plugin = plugin;
        this.delay = 0;
        this.period = period;
        this.async = async;
    }
    public Task(JavaPlugin plugin, int period) {
        this.plugin = plugin;
        this.delay = 0;
        this.period = period;
        this.async = true;
    }

    protected abstract void run();

    public void start() {
        final BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            this.run();
            this.iterations = (this.iterations >= 1440) ? 0 : this.iterations + 1;
        }, this.delay, this.period);

        this.taskId = bukkitTask.getTaskId();
    }

    public void stop() {
        if (this.taskId == -1) {
            return;
        }

        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}
