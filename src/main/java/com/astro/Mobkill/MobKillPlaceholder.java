package com.astro.Mobkill;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class MobKillPlaceholder extends PlaceholderExpansion {

    private final AstroMobkill plugin;
    private final MobKillListener listener;

    public MobKillPlaceholder(AstroMobkill plugin, MobKillListener listener) {
        this.plugin   = plugin;
        this.listener = listener;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "astromobkill";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Astro";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Stay registered after PAPI reload
    }

    /**
     * Available placeholders:
     *   %astromobkill_total_earned%  → total money earned killing mobs this session
     *   %astromobkill_last_earn%     → last amount earned
     */
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        int decimals = plugin.getConfigManager().getDecimalPlaces();

        switch (params) {
            case "total_earned" -> {
                double total = listener.getTotalEarned(player.getUniqueId());
                return formatDouble(total, decimals);
            }
            case "last_earn" -> {
                double last = listener.getLastEarn(player.getUniqueId());
                return formatDouble(last, decimals);
            }
            default -> { return null; }
        }
    }

    private String formatDouble(double value, int decimals) {
        if (decimals <= 0) return String.valueOf((long) value);
        return String.format("%." + decimals + "f", value);
    }
}
