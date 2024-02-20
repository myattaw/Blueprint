package me.rages.blueprint.service.impl.factions;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.service.PluginService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FactionService implements PluginService {


    @Override
    public FactionService setup(BlueprintPlugin plugin) {
        return this;
    }

    public boolean canBuild(Player player, Points<Vector, Vector> points, Location loc) {
        for (int x : new int[]{points.getMin().getBlockX(), points.getMax().getBlockX()}) {
            for (int y : new int[]{points.getMin().getBlockY(), points.getMax().getBlockY()}) {
                for (int z : new int[]{points.getMin().getBlockZ(), points.getMax().getBlockZ()}) {
                    Location cornerLocation = loc.clone().add(x, y, z);
                    if (!canPlayerBuild(player, cornerLocation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean canPlayerBuild(Player player, Location location) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "build", true);
    }


    @Override
    public String[] pluginNames() {
        return new String[]{"Factions"};
    }
}
