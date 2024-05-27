package me.rages.blueprint.test;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.awt.*;
import java.nio.charset.StandardCharsets;

public class PacketSender {

    public static void sendBlockHighlight(Player player, Location location, Color color, int time) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
        ByteBuf buffer = Unpooled.buffer();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        buffer.writeLong(blockPosToLong(x, y, z));
        int argb = (0xFF & 175) << 24 | (0xFF & color.getRed()) << 16 | (0xFF & color.getGreen()) << 8 | (0xFF & color.getBlue());
        buffer.writeInt(argb);
        writeString(buffer, "");
        buffer.writeInt(time);
        packet.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_add_marker"));
        packet.getSpecificModifier(FriendlyByteBuf.class).write(0, new FriendlyByteBuf(buffer));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    public static void clearHighlights(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
        packet.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_clear"));
        packet.getSpecificModifier(FriendlyByteBuf.class).write(0, new FriendlyByteBuf(Unpooled.buffer()));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    private static long blockPosToLong(int x, int y, int z) {
        return ((long) x & 67108863L) << 38 | (long) y & 4095L | ((long) z & 67108863L) << 12;
    }

    public static void wrap(ByteBuf packet, int i) {
        while ((i & -128) != 0) {
            packet.writeByte(i & 127 | 128);
            i >>>= 7;
        }
        packet.writeByte(i);
    }

    public static void writeString(final ByteBuf packet, final String string) {
        byte[] byteArray = string.getBytes(StandardCharsets.UTF_8);
        wrap(packet, byteArray.length);
        packet.writeBytes(byteArray);
    }


    public static Color getColor(Material material) {
        net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(material);
        MapColor color = block.defaultMapColor();
        return new Color(color.col);
    }

}