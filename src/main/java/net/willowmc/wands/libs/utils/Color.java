package net.willowmc.wands.libs.utils;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@AllArgsConstructor
public enum Color {

    WHITE(TextColor.color(250, 250, 250)),
    SILVER(TextColor.color(192, 192, 192)),
    GRAY(TextColor.color(184, 184, 184)),
    DARK_GRAY(TextColor.color(92, 92, 92)),
    ORANGE(TextColor.color(248, 106, 19)),
    GREEN(TextColor.color(71, 255, 0)),
    DARK_GREEN(TextColor.color(70, 170, 62)),
    RED(TextColor.color(255, 0, 33)),
    DARK_RED(TextColor.color(139, 48, 27)),
    YELLOW(TextColor.color(255, 197, 8)),
    BROWN(TextColor.color(123, 102, 106)),
    LIGHT_BROWN(TextColor.color(157, 94, 74)),
    PINK(TextColor.color(238, 27, 123)),
    BLUE(TextColor.color(46, 98, 255)),
    DARK_PURPLE(TextColor.color(81, 49, 255)),
    LIGHT_PURPLE(TextColor.color(159, 89, 255)),
    EMPTY(TextColor.color(0, 0, 0)),
    LIGHT_BLUE(TextColor.color(81, 230, 255)),
    GOLD(TextColor.color(255, 215, 0));

    private final TextColor color;

    public Style plain() {
        return Style.style(this.color, TextDecoration.ITALIC.withState(false), TextDecoration.BOLD.withState(false));
    }

    public Style custom(TextColor color, TextDecoration... textDecorations) {
        return Style.style(color, textDecorations);
    }

    public Style bold() {
        return Style.style(this.color, TextDecoration.ITALIC.withState(false), TextDecoration.BOLD.withState(true));
    }

    public TextColor get() {
        return this.color;
    }

    public static Style fromPercent(int percent) {
        return Style.style(
            TextColor.color(255, 255 - (255 * percent / 100), 0),
            TextDecoration.ITALIC.withState(false),
            TextDecoration.BOLD.withState(false)
        );
    }

}
