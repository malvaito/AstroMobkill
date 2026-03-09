package com.astro.Mobkill;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Tags mobs that spawned from a spawner with a persistent data key
 * so that MobKillListener can identify them later.
 */
public class SpawnerTagListener implements Listener {

    private final AstroMobkill plugin;

    public SpawnerTagListener(AstroMobkill plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            Entity entity = event.getEntity();
            entity.getPersistentDataContainer()
                  .set(AstroMobkill.SPAWNER_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }
}
