package me.rages.blueprint.service.impl.worldedit;

import me.rages.blueprint.data.blueprint.BlueprintData;

import java.io.File;
import java.util.Map;

/**
 * @author : Michael
 * @since : 6/23/2022, Thursday
 **/
public interface WorldEditReader {

    /**
     * Reads a schematic from file and adds to list
     * @param file the schematic file
     * @param schemDataMap storage for all schematics
     */
    void readSchematic(File file, Map<String, BlueprintData> schemDataMap);

}