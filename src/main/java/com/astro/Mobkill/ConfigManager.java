package com.astro.Mobkill;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    // -----------------------------------------------
    // Settings
    // -----------------------------------------------

    public boolean isSpawnerMobsGiveMoney() {
        return plugin.getConfig().getBoolean("settings.spawner-mobs-give-money", false);
    }

    public boolean isKillMessageEnabled() {
        return plugin.getConfig().getBoolean("settings.kill-message", true);
    }

    public String getKillMessageFormat() {
        return plugin.getConfig().getString("settings.kill-message-format",
                "<green><bold>+${amount}</bold></green> <gray>por matar</gray> <yellow>{mob}</yellow>");
    }

    public String getNoRewardMessage() {
        return plugin.getConfig().getString("settings.no-reward-message", "");
    }

    public int getDecimalPlaces() {
        return plugin.getConfig().getInt("settings.decimal-places", 2);
    }

    public double getGlobalMultiplier() {
        return plugin.getConfig().getDouble("settings.global-multiplier", 1.0);
    }

    // -----------------------------------------------
    // Mob configuration
    // -----------------------------------------------

    private String mobKey(EntityType type) {
        return "mobs." + type.name();
    }

    public boolean isMobEnabled(EntityType type) {
        return plugin.getConfig().getBoolean(mobKey(type) + ".enabled", false);
    }

    public double getMobMin(EntityType type) {
        return plugin.getConfig().getDouble(mobKey(type) + ".min", 1.0);
    }

    public double getMobMax(EntityType type) {
        return plugin.getConfig().getDouble(mobKey(type) + ".max", 5.0);
    }

    /**
     * Returns a random amount between min and max for the given mob type,
     * applying the global multiplier.
     */
    public double rollAmount(EntityType type) {
        double min = getMobMin(type);
        double max = getMobMax(type);
        if (min >= max) return round(min * getGlobalMultiplier());
        double amount = min + (random.nextDouble() * (max - min));
        return round(amount * getGlobalMultiplier());
    }

    private double round(double value) {
        int places = getDecimalPlaces();
        if (places <= 0) return Math.round(value);
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
