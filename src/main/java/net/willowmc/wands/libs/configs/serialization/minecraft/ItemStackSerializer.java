package net.willowmc.wands.libs.configs.serialization.minecraft;

import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.willowmc.wands.libs.configs.serialization.Serializer;
import net.willowmc.wands.libs.configs.serialization.Serializer.Specific;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ItemStackSerializer implements Specific<ItemStack, Map<String, Object>> {

    @Override
    public ItemStack deserialize(Class<?> fieldClass, Map<String, Object> map) {
        final ItemStack itemStack;

        try {
            itemStack = new ItemStack(Material.valueOf((String) map.get("type")), ((Number) map.get("amount")).intValue());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to deserialize ItemStack type: " + map.get("type") + " as it does not exist");
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (map.containsKey("displayName")) {
            itemMeta.displayName(Serializer.COMPONENT.deserialize(Component.class, (String) map.get("displayName")));
        }

        if (map.containsKey("lore")) {
            final Collection<?> lore = (Collection<?>) map.get("lore");
            final List<Component> deserializedLore = lore.stream()
                .map(str -> Serializer.COMPONENT.deserialize(Component.class, (String) str))
                .toList();
            itemMeta.lore(deserializedLore);
        }

        if (map.containsKey("customModelData")) {
            itemMeta.setCustomModelData(((Number) map.get("customModelData")).intValue());
        }

        if (map.containsKey("enchantments")) {
            final Map<?, ?> enchantments = (Map<?, ?>) map.get("enchantments");
            enchantments.forEach((enchantment, level) -> itemStack.addUnsafeEnchantment(
                Objects.requireNonNull(Enchantment.getByName((String) enchantment)),
                ((Number) level).intValue())
            );
        }

        if (map.containsKey("itemFlags")) {
            final Collection<?> itemFlags = (Collection<?>) map.get("itemFlags");
            itemFlags.forEach(itemFlag -> itemMeta.addItemFlags(ItemFlag.valueOf((String) itemFlag)));
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public Map<String, Object> serialize(ItemStack itemstack) {
        final Map<String, Object> map = new HashMap<>();

        map.put("type", itemstack.getType().name());
        map.put("amount", itemstack.getAmount());

        if (itemstack.getItemMeta().hasDisplayName()) {
            map.put("displayName", Serializer.COMPONENT.serialize(itemstack.getItemMeta().displayName()));
        }
        if (itemstack.getItemMeta().hasLore()) {
            final JSONArray lore = new JSONArray();
            itemstack.getItemMeta().lore().forEach(component -> lore.add(Serializer.COMPONENT.serialize(component)));
            map.put("lore", lore);
        }
        if (itemstack.getItemMeta().hasCustomModelData()) {
            map.put("customModelData", itemstack.getItemMeta().getCustomModelData());
        }
        if (!itemstack.getEnchantments().isEmpty()) {
            final JSONObject enchantments = new JSONObject();
            itemstack.getEnchantments().forEach((enchantment, integer) -> enchantments.put(enchantment.getName(), integer));
            map.put("enchantments", enchantments);
        }


        return map;
    }

}
