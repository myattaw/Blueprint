package me.rages.blueprint.data.blueprint;

import lombok.Getter;
import me.rages.blueprint.data.Points;

import java.util.*;
import org.bukkit.util.Vector;


public class BlueprintData {

    private String name;

    @Getter private Map<BlueprintDirection, List<BlueprintBlock>> blockPositions;
    @Getter private Map<BlueprintDirection, Points<Vector, Vector>> points;

    public BlueprintData(String name) {
        this.name = name;
        this.blockPositions = new HashMap<>();
        this.points = new HashMap<>();
        for (BlueprintDirection direction : BlueprintDirection.values()) {
            blockPositions.put(direction, new ArrayList<>());
        }
    }

}