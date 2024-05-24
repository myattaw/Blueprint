package me.rages.blueprint.services.worldedit;

import me.rages.blueprint.data.blueprint.Blueprint;

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
    void readSchematic(File file, Map<String, Blueprint> schemDataMap);

}