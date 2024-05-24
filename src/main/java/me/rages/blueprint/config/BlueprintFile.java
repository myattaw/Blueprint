package me.rages.blueprint.config;

import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.reliableframework.files.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Map;


public class BlueprintFile extends ConfigFile {

    public BlueprintFile(BlueprintPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public BlueprintFile init() {
        ConfigurationSection blueprints = getConfig().getConfigurationSection("blueprints");
        // Iterate through each blueprint key
        BlueprintPlugin blueprintPlugin = (BlueprintPlugin) getPlugin();
        for (Map.Entry<String, Blueprint> entry : blueprintPlugin.getBlueprintDataMap().entrySet()) {
            String key = entry.getKey();
            Blueprint value = entry.getValue();
            // Check if the section for the blueprint key exists in the configuration
            if (blueprints == null || !blueprints.contains(key)) {
                // If the section does not exist, create it and set default values
                blueprints = getConfig().createSection("blueprints." + key);
                blueprints.set("material", "PAPER");
                blueprints.set("display-name", key + " Blueprint");
                blueprints.set("display-lore", Arrays.asList("&7Blueprint lore"));
                blueprints.set("use-fast-place", false);
                blueprints.set("use-player-rotations", false);
                blueprints.set("use-snap-to-chunk", false);
            }
            // Read values from the configuration and set them in the blueprint object
            value.setDisplayName(blueprints.getString(
                    key + ".display-name",
                    ChatColor.DARK_PURPLE + key + " Blueprint")
            );
            value.setMaterial(Material.valueOf(blueprints.getString(key + ".material")));
            value.setDisplayLore(blueprints.getStringList(key + ".display-lore"));
            value.setFastPlace(blueprints.getBoolean(key + ".use-fast-place", false));
            value.setUsePlayerRotation(blueprints.getBoolean(key + ".use-player-rotations", false));
            value.setSnapToChunk(blueprints.getBoolean(key + ".use-snap-to-chunk", false));
        }
        // Save the updated configuration to the file
        this.save();
        return this;
    }
}
