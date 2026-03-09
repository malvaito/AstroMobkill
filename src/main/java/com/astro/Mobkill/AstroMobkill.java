package com.astro.Mobkill;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class AstroMobkill extends JavaPlugin {

    // Static singleton
    private static AstroMobkill instance;

    // Persistent Data key used to mark mob-spawner-sourced entities
    public static NamespacedKey SPAWNER_KEY;

    // Core components
    private Economy economy;
    private ConfigManager configManager;
    private MobKillListener mobKillListener;

    @Override
    public void onEnable() {
        instance = this;
        SPAWNER_KEY = new NamespacedKey(this, "from_spawner");

        // Config
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        // Vault economy
        economy = VaultHook.setupEconomy(this);
        if (economy == null) {
            getLogger().severe("No se pudo conectar con Vault/EssentialsX. Deshabilitando AstroMobkill.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Economía conectada: " + economy.getName());

        // Listeners
        mobKillListener = new MobKillListener(this);
        getServer().getPluginManager().registerEvents(mobKillListener, this);
        getServer().getPluginManager().registerEvents(new SpawnerTagListener(this), this);

        // PlaceholderAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MobKillPlaceholder(this, mobKillListener).register();
            getLogger().info("PlaceholderAPI detectado — expansión registrada.");
        } else {
            getLogger().warning("PlaceholderAPI no encontrado. Los placeholders no estarán disponibles.");
        }

        // Command
        var cmd = getCommand("astromobkill");
        if (cmd != null) {
            AstroMobkillCommand commandHandler = new AstroMobkillCommand(this);
            cmd.setExecutor(commandHandler);
            cmd.setTabCompleter(commandHandler);
        }

        getLogger().info("AstroMobkill v" + getDescription().getVersion() + " habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AstroMobkill deshabilitado.");
    }

    // -----------------------------------------------
    // Getters
    // -----------------------------------------------

    public static AstroMobkill getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MobKillListener getMobKillListener() {
        return mobKillListener;
    }
}
