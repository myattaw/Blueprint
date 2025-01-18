package me.rages.blueprint.test;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.awt.*;
import java.nio.charset.StandardCharsets;

public class PacketSender {

    public static void sendBlockHighlight(Player player, Location location, Color color, int time) {
        sendClientBoundCustomPayloadPacket(
                player,
                new GameTestAddMarkerDebugPayload(// create GameTestAddMarkerDebugPayload
                        new BlockPos(
                                location.getBlockX(),
                                location.getBlockY(),
                                location.getBlockZ()
                        ),
                        color.getRGB(),// Convert to integer RGB
                        "",// Text floating over the debug marker
                        time// Duration in milliseconds, here it is infinite
                )
        );
    }

    public static void clearHighlights(Player player) {
        sendClientBoundCustomPayloadPacket(
                player,
                new GameTestClearMarkersDebugPayload()
        );
    }

    public static void sendClientBoundCustomPayloadPacket(Player player, CustomPacketPayload payload) {
        // Convert Bukkit Player to ServerPlayer
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        // Create Client boundCustomPayloadPacket
        ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(payload);
        // Send packet to player
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        connection.send(packet);
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