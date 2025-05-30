package org.quorix.horsesCharacteristics;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HorsesCharacteristics extends JavaPlugin implements Listener, CommandExecutor{
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("horse_characteristics_reload")).setExecutor(this);
    }

    @EventHandler
    public void onHorseSneakClick(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if (!player.isSneaking()) return;
        if (!(target instanceof AbstractHorse horse)) return;

        double jumpStrength = horse.getJumpStrength();
        double health = horse.getHealth();
        double maxHealth = Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        double speed = Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue();

        String formattedSpeed = String.format("%.2f", speed);
        String formattedJump = String.format("%.2f", jumpStrength);
        String formattedHealth = String.format("%.1f", health);
        String formattedMaxHealth = String.format("%.1f", maxHealth);

        String message = config.getString("messages.horse_information", "<gray><yellow>Speed - %speed%</yellow> | <red>Health - %health% / %max_health%</red> | <green>Jump Strength - %jump_strength%</green></gray>");

        message = message.replace("%speed%", formattedSpeed)
                .replace("%jump_strength%", formattedJump)
                .replace("%health%", formattedHealth)
                .replace("%max_health%", formattedMaxHealth);

        Component component = miniMessage.deserialize(message);
        Audience audience = (Audience) player;
        audience.sendActionBar(component);

        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        reloadConfig();
        config = getConfig();

        Audience audience = (Audience) commandSender;
        String message = config.getString("messages.reload_success", "<green>Plugin successfully reloaded</green>");
        Component component = miniMessage.deserialize(message);

        audience.sendMessage(component);

        return true;
    }
}
