package net.willowmc.wands.libs.utils;

import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.intellij.lang.annotations.RegExp;

public class TextUtils {


    public static TextReplacementConfig replace(Map<String, Object> replacementMap) {
        final TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder()
            .build();

        replacementMap.forEach((@RegExp var key, var value) -> {
            final Component component = Component.text(String.valueOf(value));
            textReplacementConfig.toBuilder().match(key).replacement(component);
        });

        return textReplacementConfig;
    }

    public static List<Component> designComparison(Map<String, ? extends Pair<Integer, Integer>> comparisonMap) {
        final List<Component> components = new ArrayList<>();
        comparisonMap.forEach((key, pair) -> {
            final boolean hasEnough = pair.right() >= pair.left();
            final TextComponent symbol = Component.text(hasEnough ? "✅ " : "❌ ", hasEnough ? Color.GREEN.plain() : Color.RED.plain());
            final TextComponent text = Component.text(pair.right() + "/" + pair.left() + " " + key, hasEnough ? Color.GREEN.plain() : Color.RED.plain());
            components.add(symbol.append(text));
        });

        return components;
    }

    public static List<Component> alignComponents(List<? extends Component> components) {
        int longestString = 0;

        for (final Component component : components) {
            final String plainText = PlainTextComponentSerializer.plainText().serialize(component);
            if (plainText.length() > longestString) {
                longestString = plainText.length();
            }
        }

        final List<Component> alignedComponentList = new ArrayList<>();

        for (final Component component : components) {
            final String plainText = PlainTextComponentSerializer.plainText().serialize(component);
            final String alignedString = " ".repeat(Math.max(0, (longestString - plainText.length()) / 2)) +
                                         plainText;
            alignedComponentList.add(Component.text(alignedString).style(component.style()));
        }

        return alignedComponentList;
    }

    public static List<Component> divideComponent(Component component, int maxLength) {
        final String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        final List<String> dividedString = divideString(plainText, maxLength);

        final List<Component> dividedComponent = new ArrayList<>();

        for (final String string : dividedString) {
            dividedComponent.add(Component.text(string).style(component.style()));
        }

        return dividedComponent;
    }

    public static List<String> divideString(String string, int maxLength) {
        final List<String> dividedString = new ArrayList<>();
        final String[] words = string.split(" ");

        StringBuilder currentString = new StringBuilder();

        for (final String word : words) {
            if (currentString.length() + word.length() + 1 > maxLength) {
                dividedString.add(currentString.toString());
                currentString = new StringBuilder();
            }

            currentString.append(word).append(" ");
        }

        if (!currentString.isEmpty()) {
            dividedString.add(currentString.toString());
        }

        return dividedString;
    }

    public static String getCollectionName(Class<?> clazz) {
        final StringBuilder builder = new StringBuilder();
        final String className = clazz.getSimpleName();

        for (int i = 0; i < className.length(); i++) {
            final char c = className.charAt(i);

            if (i == 0) {
                builder.append(c);
                continue;
            }

            if (Character.isUpperCase(c)) {
                builder.append("_");
            }

            builder.append(c);
        }

        return builder.toString().toLowerCase();
    }

    public static String formatDouble(final double number, final int numbersAfterDot) {
        return String.format("%." + numbersAfterDot + "f", number);
    }

    public static String getProgressString(int percent) {
        final int scoresToPaint = percent / 10;

        final StringBuilder progressLineBuilder = new StringBuilder("§f" + percent + "% " + (scoresToPaint != 0 ? "§a" : "§7"));

        for (int i = 0; i < 10; i++) {
            progressLineBuilder.append("-");

            if (i + 1 == scoresToPaint) {
                progressLineBuilder.append("§7");
            }
        }

        return progressLineBuilder.toString();
    }

    public static List<String> alignStrings(List<String> stringList) {
        int longestString = 0;

        for (final String string : stringList) {
            if (string.length() > longestString) {
                longestString = string.length();
            }
        }

        final List<String> alignedStringList = new ArrayList<>();

        for (final String string : stringList) {
            final String alignedString = IntStream
                                             .range(0, Math.max(0, (longestString - string.length()) / 2))
                                             .mapToObj(i -> " ")
                                             .collect(Collectors.joining()) + string;

            alignedStringList.add(alignedString);
        }

        return alignedStringList;
    }

    public static Component makeGradient(String text, TextColor color1, TextColor color2) {
        Component component = Component.empty();

        int i = 0;
        for (final char c : text.toCharArray()) {
            component = component.append(Component.text(c, Style.style(TextColor.color(
                color1.red() + (color1.red() > 127 ? -1 * i : i),
                color1.green() + (color1.green() > 127 ? -1 * i : i),
                color1.blue() + (color1.blue() > 127 ? -1 * i : i)
            ))));
            if (c != ' ') {
                i += 1;
            }
        }

        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component makeGradient(String text, TextColor baseColor, int step) {
        Component component = Component.empty();

        int i = 0;
        for (final char c : text.toCharArray()) {
            component = component.append(Component.text(c, Style.style(TextColor.color(
                baseColor.red() + (baseColor.red() > 127 ? -1 * i : i),
                baseColor.green() + (baseColor.green() > 127 ? -1 * i : i),
                baseColor.blue() + (baseColor.blue() > 127 ? -1 * i : i)
            ))));
            if (c != ' ') {
                i += step;
            }
        }

        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component makeGradient(String text,Style style, int step) {
        Component component = Component.empty();

        int i = 0;
        for (final char c : text.toCharArray()) {
            component = component.append(Component.text(c, TextColor.color(
                style.color().red() + (style.color().red() > 127 ? -1 * i : i),
                style.color().green() + (style.color().green() > 127 ? -1 * i : i),
                style.color().blue() + (style.color().blue() > 127 ? -1 * i : i)
            )));

            if (c != ' ') {
                i += step;
            }

        }

        return component.decorations(style.decorations());
    }

}
