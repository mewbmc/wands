package net.willowmc.wands.libs.configs.serialization.minecraft;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.willowmc.wands.libs.configs.serialization.Serializer.Specific;

public class ComponentSerializer implements Specific<Component, String> {

    @Override
    public Component deserialize(Class<?> fieldClass, String serialized) {
        return MiniMessage.miniMessage().deserialize(serialized);
    }

    @Override
    public String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    @Override
    public boolean isCompatibleWith(Class clazz) {
        return Component.class.isAssignableFrom(clazz);
    }
}