package net.willowmc.wands.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.block.Action;

@Getter
@AllArgsConstructor
public enum Binding {

    LEFT_CLICK("Left Click", "LC"),
    RIGHT_CLICK("Right Click", "RC"),
    SHIFT_LEFT_CLICK("Shift + Left Click", "Shift+LC"),
    SHIFT_RIGHT_CLICK("Shift + Right Click", "Shift+RC");

    private final String title;
    private final String shortcut;

    public static Binding fromAction(Action action, boolean sneaking) {
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            return sneaking ? SHIFT_LEFT_CLICK : LEFT_CLICK;
        } else {
            return sneaking ? SHIFT_RIGHT_CLICK : RIGHT_CLICK;
        }
    }
}
