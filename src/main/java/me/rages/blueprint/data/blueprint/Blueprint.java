package me.rages.blueprint.data.blueprint;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.serialize.BlockPosition;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.test.PacketSender;
import me.rages.blueprint.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Blueprint {

    @Getter private String name;

    @Getter private Map<BlueprintDirection, List<BlueprintBlock>> blockPositions;
    @Getter private Map<BlueprintDirection, Points<Vector, Vector>> points;

    @Getter @Setter private Material material;
    @Getter @Setter private String displayName;
    @Getter @Setter private List<String> displayLore;
    @Getter @Setter private boolean fastPlace;
    @Getter @Setter private boolean snapToChunk;
    @Getter @Setter private boolean usePlayerRotation;
    @Getter @Setter private int placeCooldown;

    private Map<UUID, Set<BlockPosition>> outlineCache = new HashMap<>();

    @Getter
    private final Map<UUID, Long> cooldowns;

    public Blueprint(String name) {
        this.name = Files.getNameWithoutExtension(name);
        this.blockPositions = new HashMap<>();
        this.points = new HashMap<>();
        this.cooldowns = new HashMap<>();
        for (BlueprintDirection direction : BlueprintDirection.values()) {
            blockPositions.put(direction, new ArrayList<>());
        }
    }

    public void sendOutline(Player player, Block block, BlueprintDirection direction) {
        clearOutlines(player);
        Set<BlockPosition> positions = new HashSet<>();
        if (false) {
            // do below for bedrock players
            Points<Vector, Vector> data = points.get(direction);
            Util.getHollowCube(
                            data.getMin().getBlockX(), data.getMin().getBlockY(), data.getMin().getBlockZ(),
                            data.getMax().getBlockX(), data.getMax().getBlockY(), data.getMax().getBlockZ())
                    .forEach(vec -> {
                        Block outlineBlock = block.getLocation().add(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getBlock();
                        if (outlineBlock.isLiquid() || outlineBlock.getType() == Material.AIR) {
                            positions.add(BlockPosition.of(outlineBlock));
                            player.sendBlockChange(
                                    outlineBlock.getLocation(),
                                    Material.LIME_STAINED_GLASS.createBlockData()
                            );
                        }
                    });

            Schedulers.sync().runLater(() -> clearOutlines(player), 15, TimeUnit.SECONDS);
        } else {
            List<BlueprintBlock> bp = blockPositions.get(direction);
//            PacketSender.clearHighlights(player);
            bp.forEach(blueprintBlock -> {
                Vector pos = blueprintBlock.getPosition();
                Location loc = block.getLocation().clone().add(pos);
                BlockData blockData = ((BlockData) blueprintBlock.getBlockData());
                if (blockData.getMaterial().isSolid() &&
                        !BlueprintPlugin.getInstance().getIgnoredTypes().contains(blockData.getMaterial())) {
                    PacketSender.sendBlockHighlight(
                            player,
                            loc,
                            PacketSender.getColor(blockData.getMaterial()),
                            3000
                    );
                }
            });
        }
        outlineCache.put(player.getUniqueId(), positions);
    }

    public void clearOutlines(Player player) {
        if (outlineCache.containsKey(player.getUniqueId())) {
            outlineCache.get(player.getUniqueId()).forEach(pos -> {
                if (pos.toLocation().getBlock().isLiquid() || pos.toLocation().getBlock().getType().isAir()) {
                    player.sendBlockChange(
                            pos.toLocation(),
                            Material.AIR.createBlockData()
                    );
                }
            });
            outlineCache.remove(player.getUniqueId());
        }
    }


}