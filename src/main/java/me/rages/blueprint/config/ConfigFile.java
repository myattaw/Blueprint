package me.rages.blueprint.config;

import lombok.Getter;
import me.rages.blueprint.BlueprintPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class ConfigFile {

    @Getter private FileConfiguration config;
    @Getter private File configFile;
    @Getter private String fileName;
    @Getter private BlueprintPlugin plugin;

    public ConfigFile(BlueprintPlugin plugin, String fileName) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        this.configFile = new File(plugin.getDataFolder(), String.format("%s.yml", fileName.toLowerCase()));
        try {
            this.configFile.createNewFile();
            plugin.getLogger().log(Level.INFO, "Creating new %s configuration file!", fileName);
        }
        catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create configuration file!");
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        this.fileName = fileName;
        this.plugin = plugin;
    }

    public abstract ConfigFile init();

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void save() {
        try {
            this.config.save(this.configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}