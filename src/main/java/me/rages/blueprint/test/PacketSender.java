package me.rages.blueprint.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.lucko.helper.Schedulers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.awt.*;
import java.nio.charset.StandardCharsets;

public class PacketSender {

    //TODO: convert to protocol lib
    public static void sendBlockHighlight(final Player player, Location location, Color colour, int time) {
        ByteBuf packet = Unpooled.buffer();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        packet.writeLong(blockPosToLong(x, y, z));
        int argb = (0xFF & 175) << 24 | (0xFF & colour.getRed()) << 16 | (0xFF & colour.getGreen()) << 8 | (0xFF & colour.getBlue());
        packet.writeInt(argb);
        writeString(packet, "");
        packet.writeInt(time); //TODO: see if we can override this to 0 to cancel
        sendPayload(player, "debug/game_test_add_marker", packet);
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

    private static void sendPayload(final Player receiver, String channel, ByteBuf bytes) {
        ClientboundCustomPayloadPacket customPayloadPacket = new ClientboundCustomPayloadPacket(new ResourceLocation(channel), new FriendlyByteBuf(bytes));
        sendPacket((CraftPlayer) receiver, customPayloadPacket);
    }

    private static void sendPacket(final CraftPlayer player, final Packet packet) {
        Schedulers.async().run(() -> player.getHandle().connection.send(packet));
    }

    public static Color getColor(Material material) {
        net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(material);
        MapColor color = block.defaultMapColor();
        return new Color(color.col);
    }

}