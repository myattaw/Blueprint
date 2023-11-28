package me.rages.blueprint.service;

import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.service.impl.WorldEditService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author : Michael
 * @since : 6/17/2022, Friday
 **/
public class ServiceManager {

    private BlueprintPlugin plugin;
    private Map<String, PluginService> pluginMap = new HashMap<>();

    /**
     * Default constructor
     *
     * @param plugin
     */
    private ServiceManager(BlueprintPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Create instance of HookManager
     *
     * @param plugin CustomItemsPlugin
     * @return instance of HookManager
     */
    public static ServiceManager createServiceManager(BlueprintPlugin plugin) {
        return new ServiceManager(plugin);
    }

    /**
     * Register plugin hook by name
     *
     * @param pluginName    name of the hook
     * @param pluginService object of the plugin hook
     */
    public ServiceManager registerService(String pluginName, PluginService pluginService) {
        for (String name : pluginService.pluginNames()) {
            if (plugin.getServer().getPluginManager().getPlugin(name) == null) continue;
            plugin.getLogger().log(Level.INFO, "Successfully hooked into " + name);
            pluginMap.put(pluginName, (PluginService<?>) pluginService.setup(plugin));
        }
        return this;
    }

    public WorldEditService getWorldEdit() {
        return (WorldEditService) pluginMap.get("worldedit");
    }


}