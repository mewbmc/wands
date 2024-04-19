package net.willowmc.wands.libs.tasks;

import org.bukkit.plugin.java.JavaPlugin;

public class Tasks {

    public static Task create(JavaPlugin plugin, int delay, int period, Runnable runnable) {
        return new Task(plugin, delay, period) {
            @Override
            protected void run() {
                runnable.run();
            }
        };
    }

}
