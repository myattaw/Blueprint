package me.rages.blueprint.data.blueprint;

import java.util.Optional;
import org.bukkit.util.Vector;


/**
 * @author : Michael
 * @since : 6/16/2022, Thursday
 **/
public class BlueprintBlock<T> {

    private T blockData; // main data type for XMaterial, BlockData or Integer
    private Optional<T> optionalData; // byte for older versions of minecraft
    private Vector position;

    /**
     * Default constructor with SchemBlock position
     *
     * @param position
     */
    private BlueprintBlock(Vector position) {
        this.position = position;
    }

    /**
     * Create SchemBlock type from position
     *
     * @param position
     * @return instance of SchemBlock
     */
    public static BlueprintBlock from(Vector position) {
        return new BlueprintBlock(position);
    }

    /**
     * Add blockData to SchemBlock
     *
     * @param data
     * @return
     */
    public BlueprintBlock setBlockData(T data) {
        this.blockData = data;
        return this;
    }

    /**
     * Add optionalData to SchemBlock
     *
     * @param optionalData
     * @return
     */
    public BlueprintBlock setOptionalData(Optional<T> optionalData) {
        this.optionalData = optionalData;
        return this;
    }

    /**
     * Get block data from type
     *
     * @return returns byte or BlockData depending on version
     */
    public T getBlockData() {
        return blockData;
    }

    /**
     * Optional data for older versions
     *
     * @return the optional data type
     */
    public Optional<T> getOptionalData() {
        return optionalData;
    }

    /**
     * Coordinates of the SchemBlock
     *
     * @return
     */
    public Vector getPosition() {
        return position;
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