package me.rages.blueprint.service.impl.factions;

import com.massivecraft.factions.listeners.FactionsBlockListener;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.service.PluginService;
import me.rages.blueprint.service.impl.skyblock.SkyblockService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FactionService implements PluginService {


    @Override
    public FactionService setup(BlueprintPlugin plugin) {
        return this;
    }

    public boolean canBuild(Player player, Points<Vector, Vector> points, Location loc) {
        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMin().getBlockX(), points.getMin().getBlockY(), points.getMin().getBlockZ())))
            return false;

        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMin().getBlockX(), points.getMax().getBlockY(), points.getMin().getBlockZ())))
            return false;

        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMin().getBlockX(), points.getMin().getBlockY(), points.getMax().getBlockZ())))
            return false;

        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMin().getBlockX(), points.getMax().getBlockY(), points.getMax().getBlockZ())))
            return false;

        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMax().getBlockX(), points.getMin().getBlockY(), points.getMax().getBlockZ())))
            return false;

        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMax().getBlockX(), points.getMax().getBlockY(), points.getMax().getBlockZ())))
            return false;

        if (!canPlayerBuild(player, new Location(loc.getWorld(), points.getMax().getBlockX(), points.getMin().getBlockY(), points.getMin().getBlockZ())))
            return false;

        return canPlayerBuild(player, new Location(loc.getWorld(), points.getMax().getBlockX(), points.getMax().getBlockY(), points.getMin().getBlockZ()));
    }

    public boolean canPlayerBuild(Player player, Location location) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "build", true);
    }


    @Override
    public String[] pluginNames() {
        return new String[]{"Factions"};
    }
}
