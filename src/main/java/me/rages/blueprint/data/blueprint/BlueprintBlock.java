package me.rages.blueprint.data.blueprint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.util.Vector;

import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BlueprintBlock<T> {

    private T blockData;
    private Optional<T> optionalData;
    private Vector position;

    private BlueprintBlock(Vector position) {
        this.position = position;
    }

    public static <T> BlueprintBlock<T> from(Vector position) {
        return new BlueprintBlock<>(position);
    }

    public BlueprintBlock<T> setBlockData(T data) {
        this.blockData = data;
        return this;
    }

    public BlueprintBlock<T> setOptionalData(Optional<T> optionalData) {
        this.optionalData = optionalData;
        return this;
    }

    @Override
    public String toString() {
        return "BlueprintBlock{" +
                "blockData=" + blockData +
                ", optionalData=" + optionalData +
                ", position=" + position +
                '}';
    }
}

