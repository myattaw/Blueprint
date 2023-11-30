package me.rages.blueprint.service.impl;

import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import me.rages.blueprint.service.PluginService;
import me.rages.blueprint.service.impl.skyblock.SkyblockService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BuildCheckService implements PluginService {

    private SkyblockService skyblockService;

    @Override
    public BuildCheckService setup(BlueprintPlugin plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("ASkyBlock")) {
            this.skyblockService = new SkyblockService();
        }
        return this;
    }

    public boolean canBuild(Player player, Points<Vector, Vector> points, Location loc) {
        if (skyblockService != null) {
            return skyblockService.canBuild(player, points, loc);
        }

        return true;
    }

    @Override
    public String[] pluginNames() {
        return new String[]{"ASkyBlock"};
    }
}
