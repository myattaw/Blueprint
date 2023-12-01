package me.rages.blueprint.data.blueprint;

/**
 * @author : Michael
 * @since : 6/16/2022, Thursday
 **/
public enum BlueprintDirection {

    NORTH("North", 0), EAST("East", 90), SOUTH("South", 180), WEST("West", 270);

    private final String name;
    private final int rotation;

    BlueprintDirection(String name, int rotation) {
        this.name = name;
        this.rotation = rotation;
    }

    public String getName() {
        return name;
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

    public BlueprintDirection rightDirection() {
        int nextRotation = (rotation + 90) % 360;
        return fromRotation(nextRotation);
    }

    public BlueprintDirection leftDirection() {
        int nextRotation = (rotation - 90 + 360) % 360;
        return fromRotation(nextRotation);
    }

}