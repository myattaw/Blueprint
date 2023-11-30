package me.rages.blueprint.service.impl.skyblock;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.service.PluginService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class SkyblockService implements PluginService {

    private final ASkyBlockAPI api = ASkyBlockAPI.getInstance();

    @Override
    public SkyblockService setup(BlueprintPlugin plugin) {

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

        if(api.getIslandAt(location) == null)
        {
            return true;
        }

        return api.getIslandAt(location).getMembers().contains(player.getUniqueId());
    }



    @Override
    public String[] pluginNames() {
        return new String[]{"ASkyBlock"};
    }
}
