package com.astro.Mobkill;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobKillListener implements Listener {

    private final AstroMobkill plugin;

    // Per-player tracking for PAPI placeholders
    private final ConcurrentHashMap<UUID, Double> totalEarned = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Double> lastEarn    = new ConcurrentHashMap<>();

    public MobKillListener(AstroMobkill plugin) {
        this.plugin = plugin;
    }

    // -----------------------------------------------
    // Public getters used by the PAPI expansion
    // -----------------------------------------------

    public double getTotalEarned(UUID uuid) {
        return totalEarned.getOrDefault(uuid, 0.0);
    }

    public double getLastEarn(UUID uuid) {
        return lastEarn.getOrDefault(uuid, 0.0);
    }

    // -----------------------------------------------
    // Event handler
    // -----------------------------------------------

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        // Must be killed by a player
        if (killer == null) return;

        EntityType type = entity.getType();
        ConfigManager cfg = plugin.getConfigManager();
        Economy economy   = plugin.getEconomy();

        // Mob must be enabled in config
        if (!cfg.isMobEnabled(type)) return;

        // Spawner check: use spawnReason via EntityMetadata stored at spawn
        if (!cfg.isSpawnerMobsGiveMoney()) {
            Boolean fromSpawner = entity.getPersistentDataContainer()
                    .get(AstroMobkill.SPAWNER_KEY, org.bukkit.persistence.PersistentDataType.BYTE) != null
                    ? true : null;
            // Fallback: check last damage cause metadata if available
            // We rely on CreatureSpawnEvent (registered in main class) to tag spawner mobs
            if (fromSpawner != null && fromSpawner) return;
        }

        double amount = cfg.rollAmount(type);
        if (amount <= 0) return;

        // Deposit money
        economy.depositPlayer(killer, amount);

        // Update PAPI tracking maps
        UUID uuid = killer.getUniqueId();
        totalEarned.merge(uuid, amount, Double::sum);
        lastEarn.put(uuid, amount);

        // Send reward message
        if (cfg.isKillMessageEnabled()) {
            String mobName = formatMobName(type);
            String amountStr = formatAmount(amount, cfg.getDecimalPlaces());

            String formatted = cfg.getKillMessageFormat()
                    .replace("{amount}", amountStr)
                    .replace("{mob}", mobName);

            killer.sendMessage(MiniMessage.miniMessage().deserialize(formatted));
        }
    }

    // -----------------------------------------------
    // Helpers
    // -----------------------------------------------

    private String formatMobName(EntityType type) {
        String name = type.name().replace('_', ' ');
        String[] words = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }

    private String formatAmount(double amount, int decimalPlaces) {
        if (decimalPlaces <= 0) return String.valueOf((long) amount);
        return String.format("%." + decimalPlaces + "f", amount);
    }
}
