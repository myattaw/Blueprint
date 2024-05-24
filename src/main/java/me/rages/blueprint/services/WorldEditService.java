package me.rages.blueprint.services;

import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.services.worldedit.WorldEdit7Reader;
import me.rages.blueprint.services.worldedit.WorldEditReader;
import me.rages.reliableframework.pluginservice.PluginService;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;

public class WorldEditService implements PluginService {

    private WorldEditReader worldEditReader;

    @Override
    public WorldEditService setup(JavaPlugin plugin) {
        Plugin foundPlugin = null;
        for (String name : pluginNames()) {
            if (plugin.getServer().getPluginManager().isPluginEnabled(name)) {
                foundPlugin = plugin.getServer().getPluginManager().getPlugin(name);
            }
        }

        // we could not find any usable WorldEdit plugins
        if (foundPlugin != null) {
            // determine if we want to use the latest WorldEdit
            try {
                Class.forName("com.sk89q.worldedit.math.BlockVector3"); // newer WorldEdit versions use this
                this.worldEditReader = new WorldEdit7Reader(); // 1.13 -> 1.19
            } catch (ClassNotFoundException ignored) {
//                this.worldEditReader = new WorldEdit6Reader(); // 1.8 -> 1.12.2
            }
            return this;
        } else {
            plugin.getServer().getLogger().log(Level.SEVERE, "Could not find WorldEdit! (forcing plugin to disable)");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }
    }

    public void readSchematic(File file, Map<String, Blueprint> blueprintDataMap) {
        worldEditReader.readSchematic(file, blueprintDataMap);
    }

    @Override
    public String[] pluginNames() {
        return new String[]{"WorldEdit", "FastAsyncWorldEdit"};
    }

}
