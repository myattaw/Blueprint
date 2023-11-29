package me.rages.blueprint.service.impl.worldedit;

import com.google.common.io.Files;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.block.BaseBlock;

import me.rages.blueprint.data.Points;
import me.rages.blueprint.data.blueprint.BlueprintBlock;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import org.bukkit.util.Vector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

public class WorldEdit7Reader implements WorldEditReader {

    @Override
    @SuppressWarnings("unchecked")
    public void readSchematic(File file, Map<String, Blueprint> schemDataMap) {
        Blueprint schemData = new Blueprint(file.getName());
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        for (BlueprintDirection direction : BlueprintDirection.values()) {
            int rotation = direction.getRotation();
            if (format != null) {
                try (Closer closer = Closer.create()) {
                    FileInputStream fis = closer.register(new FileInputStream(file));
                    BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
                    ClipboardReader reader = closer.register(format.getReader(bis));
                    Clipboard clipboard = reader.read();
                    ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

                    // rotating
                    AffineTransform transform = new AffineTransform();
                    transform = transform.rotateY(-rotation);
                    clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));
                    BlockTransformExtent extent = new BlockTransformExtent(clipboard, transform);

                    Vector3 minimum = transform.apply(clipboard.getMinimumPoint().toVector3().subtract(clipboard.getOrigin().toVector3()));
                    Vector3 maximum = transform.apply(clipboard.getMaximumPoint().toVector3().subtract(clipboard.getOrigin().toVector3()));

                    Vector3 min = minimum.getMinimum(maximum);
                    Vector3 max = minimum.getMaximum(maximum);

                    schemData.getPoints().put(direction, new Points<>(
                            new Vector(min.getX(), min.getY(), min.getZ()),
                            new Vector(max.getX(), max.getY(), max.getZ()))
                    );
                    //

                    for (BlockVector3 vector3 : clipboardHolder.getClipboard().getRegion()) {

                        BaseBlock baseBlock = extent.getFullBlock(vector3);

                        if (baseBlock.getBlockType().getMaterial().isAir()) continue;

                        // paste location
                        Vector3 loc = transform.apply(vector3.subtract(clipboard.getOrigin()).toVector3());

                        schemData.getBlockPositions().get(direction)
                                .add(BlueprintBlock.from(new Vector(loc.getX(), loc.getY(), loc.getZ()))
                                        .setBlockData(BukkitAdapter.adapt(baseBlock)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            schemData.getBlockPositions().get(direction).sort(Comparator.comparingInt(o -> o.getPosition().getBlockY()));
        }
        schemDataMap.put(Files.getNameWithoutExtension(file.getName()).toLowerCase(), schemData);
        // add the schematic to a list of schematics
    }


}
