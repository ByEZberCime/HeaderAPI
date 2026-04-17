package com.headerapi.fx.byezbercime.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ConfigurationManager {

    private HashMap<Object,Object> headerConfigurations = null;

    private JavaPlugin plugin;
    private File configFile;
    private YamlConfiguration configuration;

    public ConfigurationManager(JavaPlugin plugin) {

        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.headerConfigurations = new HashMap<>();

        if (!this.configFile.getParentFile().isDirectory() && !this.configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        if (this.configFile.getParentFile().isDirectory() && this.configFile.exists()) {

            try {
                this.configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }

        register();

    }

    protected void register() {

        if (!getHeaderConfigurations().containsKey("mineskin_api")) {
            String mineskinAPICode = getConfiguration().getString("mineskin-api");
            if (mineskinAPICode != null && !mineskinAPICode.isEmpty()) {
                getHeaderConfigurations().put("mineskin_api", mineskinAPICode);
            }
        }



    }

    public File getConfigFile() {
        return configFile;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public HashMap<Object, Object> getHeaderConfigurations() {
        return headerConfigurations;
    }
}
