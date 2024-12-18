package me.rages.blueprint.services;

import me.rages.blueprint.data.Points;
import me.rages.blueprint.services.factions.FactionService;
import me.rages.blueprint.services.skyblock.SkyblockService;
import me.rages.blueprint.util.Util;
import me.rages.reliableframework.pluginservice.PluginService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BuildCheckService implements PluginService<BuildCheckService> {

    private SkyblockService skyblockService;
    private FactionService factionService;

    @Override
    public BuildCheckService setup(JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
            this.skyblockService = new SkyblockService().setup(plugin);
        }
        System.out.println();
        if (plugin.getServer().getPluginManager().isPluginEnabled("Factions")) {
            this.factionService = new FactionService().setup(plugin);
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

        for (int x : new int[]{points.getMin().getBlockX(), points.getMax().getBlockX()}) {
            for (int z : new int[]{points.getMin().getBlockZ(), points.getMax().getBlockZ()}) {
                Location cornerLocation = loc.clone().add(x, points.getMin().getBlockY(), z);
                if (Util.isOutsideBorder(cornerLocation)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String[] pluginNames() {
        return new String[]{"ASkyBlock", "SuperiorSkyblock2", "Factions"};
    }
}
