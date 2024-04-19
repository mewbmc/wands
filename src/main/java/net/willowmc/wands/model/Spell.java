package net.willowmc.wands.model;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.user.SkillsUser;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.willowmc.wands.WandsService;
import net.willowmc.wands.configs.WandsConfig;
import net.willowmc.wands.libs.utils.Color;
import net.willowmc.wands.libs.utils.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@NoArgsConstructor
public class Spell {

    private String id = "spell_id";
    private String mythicSkillId = "mythic_spell_id";
    private int manaCost = 10;
    private int radius = 10;
    private ItemStack icon = Items.create(Material.DIAMOND)
        .displayName(Component.text("Spell Name", Color.GOLD.plain()))
        .lore(List.of(Component.text("Spell description", Color.GRAY.plain())))
        .customModelData(1)
        .build();

    public void cast(WandsService service, Wand wand, Player player) {
        final Skill skill = this.toMythic();
        final WandsConfig config = service.getConfig();
        final boolean hasEnoughMana = this.hasEnoughMana(service, wand, player);
        final boolean hasCooldown = service.isSpellOnCooldown(player, this);
        if (hasCooldown) {
            player.sendMessage(config.getMessages().getSpellOnCooldown());
            config.getMessages().getCooldownSound().play(player);
            return;
        }

        if (!hasEnoughMana) {
            player.sendMessage(config.getMessages().getNotEnoughMana());
            config.getMessages().getNotEnoughManaSound().play(player);
            return;
        }

        final SkillMetadata skillMetadata = this.getSkillMetadata(service, wand, player);
        skill.execute(skillMetadata);
        this.handleMana(service, wand, player);
        this.handleCooldown(service, wand, player);
    }

    public Skill toMythic() {
        final Optional<Skill> optionalSkill = MythicBukkit.inst().getSkillManager().getSkill(this.mythicSkillId);
        if (optionalSkill.isEmpty()) {
            throw new IllegalArgumentException("Failed to cast spell with mythic id " + this.mythicSkillId + " as it does not exist");
        }
        return optionalSkill.get();
    }

    private boolean hasEnoughMana(WandsService service, Wand wand, Player player) {
        final AuraSkillsApi auraApi = AuraSkillsApi.get();
        final SkillsUser user = auraApi.getUser(player.getUniqueId());
        final int adjustedManaCost = this.getAdjustedManaCost(service, wand);

        return user.getMana() >= adjustedManaCost;
    }

    private int getAdjustedManaCost(WandsService service, Wand wand) {
        final Map<Stat, Double> statsValues = wand.getStatsValues(service);
        return (int) (this.manaCost * statsValues.get(Stat.MANA_CONSUMPTION));
    }

    private void handleMana(WandsService service, Wand wand, Player player) {
        final AuraSkillsApi auraApi = AuraSkillsApi.get();
        final SkillsUser user = auraApi.getUser(player.getUniqueId());
        final int adjustedManaCost = this.getAdjustedManaCost(service, wand);

        user.setMana(user.getMana() - adjustedManaCost);
    }

    private SkillMetadata getSkillMetadata(WandsService service, Wand wand, Player player) {
        final Map<Stat, Double> statsValues = wand.getStatsValues(service);
        final AbstractPlayer mythicPlayer = new BukkitPlayer(player);
        final GenericCaster caster = new GenericCaster(mythicPlayer);

        final SkillMetadataImpl skillMetadata = new SkillMetadataImpl(SkillTriggers.API, caster, new BukkitPlayer(player));
        final AbstractLocation targetLocation = this.getTargetLocation(service, wand, player);
        skillMetadata.setLocationTarget(targetLocation);
        skillMetadata.setPower(statsValues.get(Stat.STRENGTH).floatValue());
        return skillMetadata;
    }

    private AbstractLocation getTargetLocation(WandsService service, Wand wand, Player player) {
        final Map<Stat, Double> statsValues = wand.getStatsValues(service);
        final int range = statsValues.get(Stat.RANGE).intValue();
        final Position playerPosition = Position.of(player.getTargetBlock(null, range).getLocation());
        return new AbstractLocation(playerPosition);
    }

    private Collection<AbstractEntity> getTargetsInLocation(AbstractLocation location, Player player) {
        final Location bukkitLocation = location.toPosition().toLocation();
        return bukkitLocation.getNearbyLivingEntities(this.radius).stream()
            .filter(entity -> entity != player)
            .map(BukkitEntity::new)
            .map(AbstractEntity.class::cast)
            .toList();
    }

    private void handleCooldown(WandsService service, Wand wand, Player player) {
        final UUID uniqueId = player.getUniqueId();
        final SpellCooldown cooldown = new SpellCooldown(uniqueId, service, wand, this);
        service.getSpellCooldowns().put(uniqueId, cooldown);
    }

}
