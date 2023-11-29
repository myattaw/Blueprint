package me.rages.blueprint.util;

import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Util {

    public static Set<Vector> getHollowCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Set<Vector> vectors = new HashSet<>();
        for (float x = minX; x <= maxX; x += .5) {
            for (float y = minY; y <= maxY; y += .5) {
                for (float z = minZ; z <= maxZ; z += .5) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        vectors.add(new Vector(x, y, z));
                    }
                }
            }
        }
        return vectors;
    }

}
