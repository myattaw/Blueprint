package me.rages.blueprint.service;

import me.rages.blueprint.BlueprintPlugin;

/**
 * @author : Michael
 * @since : 6/17/2022, Friday
 **/
public interface PluginService<T> {

    /***
     * Initializes the PluginHook and returns itself for access
     * @param plugin CustomItemsPlugin
     * @return the PluginHook created
     */
    T setup(BlueprintPlugin plugin);

    /***
     * Plugin names
     * @return possible names the plugin may use
     */
    String[] pluginNames();

}