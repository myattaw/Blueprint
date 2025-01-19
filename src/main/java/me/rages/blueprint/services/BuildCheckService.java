package me.rages.blueprint.services;

import me.rages.blueprint.data.Points;
import me.rages.blueprint.services.factions.FactionService;
import me.rages.blueprint.services.skyblock.SkyblockService;
import me.rages.blueprint.util.Util;
import me.rages.reliableframework.pluginservice.PluginService;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class BuildCheckService implements PluginService<BuildCheckService> {

    private SkyblockService skyblockService;
    private FactionService factionService;

    private int dispenserLimit;
    private int tileEntityLimit;


    @Override
    public BuildCheckService setup(JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
            this.skyblockService = new SkyblockService().setup(plugin);
        }
        if (plugin.getServer().getPluginManager().isPluginEnabled("Factions")) {
            this.factionService = new FactionService().setup(plugin);
        }

        dispenserLimit = plugin.getConfig().getInt("settings.dispenser-limit");
        tileEntityLimit = plugin.getConfig().getInt("settings.tile-entity-limit");

        return this;
    }

    public boolean canBuild(Player player, Points<Vector, Vector> points, Location loc) {

        Chunk chunk = loc.getChunk();
        BlockState[] tileEntities = chunk.getTileEntities();

        // Check tile entity limit
        if (tileEntityLimit != -1 && tileEntities.length > tileEntityLimit) {
            player.sendMessage(ChatColor.RED + String.format("There is more than %d tile entities in this chunk!", tileEntityLimit));
            return false;
        }

        // Count dispensers and enforce dispenser limit
        long dispenserCount = Arrays.stream(tileEntities)
                .filter(blockState -> blockState.getType() == Material.DISPENSER)
                .count();

        if (dispenserLimit != -1 && dispenserCount > dispenserLimit) {
            player.sendMessage(ChatColor.RED + String.format("There is more than %d dispensers in this chunk!", dispenserLimit));
            return false;
        }

        for (int x : new int[]{points.getMin().getBlockX(), points.getMax().getBlockX()}) {
            for (int z : new int[]{points.getMin().getBlockZ(), points.getMax().getBlockZ()}) {
                Location cornerLocation = loc.clone().add(x, points.getMin().getBlockY(), z);
                if (Util.isOutsideBorder(cornerLocation)) {
                    return false;
                }
            }
        }

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
