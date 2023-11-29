package me.rages.blueprint.generator;

import lombok.Getter;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.blueprint.BlueprintBlock;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

/**
 * @author : Michael
 * @since : 6/25/2022, Saturday
 **/
public class BlueprintGenerator {

    private String key; // key of the SchemBucket
    private int blockIndex; // current block index from List<SchemBlock>
    private BlueprintDirection direction; // the direction the player picked
    private Location location;
    @Getter private Player player; // who placed the SchemBucket
    private boolean fastMode = false; // builds the schematic layer by layer


    /**
     * Default constructor
     *
     * @param key
     * @param direction
     * @param location
     */
    private BlueprintGenerator(String key, BlueprintDirection direction, Location location) {
        this.key = key;
        this.direction = direction;
        this.location = location;
        this.blockIndex = 0;
    }

    /**
     * Create SchemGenerator from key, direction and location
     *
     * @param key
     * @param direction
     * @param location
     * @return new instance of SchemGenerator
     */
    public static BlueprintGenerator create(String key, BlueprintDirection direction, Location location) {
        return new BlueprintGenerator(key, direction, location);
    }

    /**
     * Add player to generator
     *
     * @param player
     * @return generator
     */
    public BlueprintGenerator addPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Change schematic to fast building mode
     *
     * @param flag
     * @return generator
     */
    public BlueprintGenerator setFastMode(boolean flag) {
        this.fastMode = flag;
        return this;
    }

    /**
     * Builds schematic
     *
     * @param plugin
     * @param blueprintDataMap
     * @return if the schematic is finished
     */
    public boolean build(BlueprintPlugin plugin, Map<String, Blueprint> blueprintDataMap) {
        // add fast place
        List<BlueprintBlock> blocks = blueprintDataMap.get(key).getBlockPositions().get(direction);
        Vector pos = blocks.get(blockIndex).getPosition();
        if (fastMode) {
            int currY = pos.getBlockY(); // get initial Y of the block
            while (blockIndex < blocks.size() && currY == blocks.get(blockIndex).getPosition().getBlockY()) { // check if we are on the same layer
                Location loc = location.clone().add(blocks.get(blockIndex).getPosition());

                if (loc.getBlock().isLiquid() || loc.getBlock().getType().isAir()) {
                    loc.getBlock().setBlockData((BlockData) blocks.get(blockIndex++).getBlockData(), false);
                }
            }
        } else {
            Location loc = location.clone().add(pos);
            if (loc.getBlock().isLiquid() || loc.getBlock().getType().isAir()) {
                loc.getBlock().setBlockData((BlockData) blocks.get(blockIndex++).getBlockData(), false);
            }
        }

        return blockIndex >= blocks.size(); // we reached the end of the build
    }

}