package me.rages.blueprint.service.impl;

import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import me.rages.blueprint.service.PluginService;
import me.rages.blueprint.service.impl.factions.FactionService;
import me.rages.blueprint.service.impl.skyblock.SkyblockService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BuildCheckService implements PluginService {

    private SkyblockService skyblockService;
    private FactionService factionService;

    @Override
    public BuildCheckService setup(BlueprintPlugin plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
            this.skyblockService = new SkyblockService();
        }
        if (plugin.getServer().getPluginManager().isPluginEnabled("Factions")) {
            this.factionService = new FactionService();
        }
        return this;
    }

    public boolean canBuild(Player player, Points<Vector, Vector> points, Location loc) {
        if (skyblockService != null) {
            return skyblockService.canBuild(player, points, loc);
        }

        if (factionService != null) {
            return factionService.canBuild(player, points, loc);
        }

        return true;
    }

    @Override
    public String[] pluginNames() {
        return new String[]{"ASkyBlock", "SuperiorSkyblock2", "Factions"};
    }
}
