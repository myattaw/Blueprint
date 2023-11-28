package me.rages.blueprint.data.blueprint;

/**
 * @author : Michael
 * @since : 6/16/2022, Thursday
 **/
public enum BlueprintDirection {

    NORTH(0), EAST(90), SOUTH(180), WEST(270);

    private final int rotation;

    BlueprintDirection(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public static BlueprintDirection fromRotation(int rotation) {
        for (BlueprintDirection direction : BlueprintDirection.values()) {
            if (direction.getRotation() == rotation) return direction;
        }
        return null;
    }

}