package me.rages.blueprint.ui;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Message;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import me.rages.blueprint.generator.BlueprintGenerator;
import me.rages.blueprint.test.PacketSender;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class ConfirmUI extends Gui {

    private final int[] CONFIRM_SLOTS = {0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21};
    private final int[] CANCEL_SLOTS = {5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26};

    private BlueprintPlugin plugin;
    private Blueprint blueprint;
    private BlueprintDirection direction;
    private Location location;
    private ItemStack itemStack;

    public ConfirmUI(BlueprintPlugin plugin, ItemStack itemStack, Blueprint blueprint, BlueprintDirection direction, Player player, Location location) {
        super(player, 3, plugin.getConfig().getString("confirm-gui.title"));
        this.plugin = plugin;
        this.direction = direction;
        this.blueprint = blueprint;
        this.location = location;
        this.itemStack = itemStack;
    }

    @Override
    public void redraw() {
        if (isFirstDraw()) {
            Arrays.stream(CONFIRM_SLOTS)
                    .forEach(CONFIRM_SLOT -> setItem(CONFIRM_SLOT,
                            ItemStackBuilder.of(Material.valueOf(plugin.getConfig().getString("confirm-gui.items.confirm.type")))
                                    .name(plugin.getConfig().getString("confirm-gui.items.confirm.name"))
                                    .lore(plugin.getConfig().getStringList("confirm-gui.items.confirm.lore"))
                                    .build(() -> {
                                        if (plugin.getBlueprintModule().removeItem(getPlayer(), itemStack, 1)) {

                                            BlueprintGenerator generator = BlueprintGenerator.create(blueprint.getName().toLowerCase(), direction, location)
                                                    .addPlayer(getPlayer())
                                                    .setFastMode(blueprint.isFastPlace())
                                                    .setSnapToChunk(blueprint.isSnapToChunk())
                                                    .setUsePlayerRotation(blueprint.isUsePlayerRotation());


                                            plugin.getBlueprintGenerators().add(generator);
                                            Points<Vector, Vector> points = blueprint.getPoints().get(direction);
                                            String time;
                                            if (generator.isFastMode()) {
                                                time = String.format("%d Seconds", (int) Math.ceil((points.getMax().getY() - points.getMin().getY()) / 4f));
                                            } else {
                                                time = String.format("%d Seconds", (int) Math.ceil(blueprint.getBlockPositions().get(direction).size() / 4f));
                                            }

                                            String finalTime = time;
                                            Arrays.stream(Message.BLUEPRINT_TASK_STARTED.getAllColorized())
                                                    .map(message -> message.replace("{time}", finalTime))
                                                    .forEach(getPlayer()::sendMessage);

                                            if (blueprint.getPlaceCooldown() != 0) {
                                                blueprint.getCooldowns().put(
                                                        getPlayer().getUniqueId(),
                                                        System.currentTimeMillis() + (blueprint.getPlaceCooldown() * 1_000L)
                                                );
                                            }

                                            blueprint.clearOutlines(getPlayer());
                                            PacketSender.clearHighlights(getPlayer());
                                            close();
                                        } else {
                                            getPlayer().sendMessage(ChatColor.RED + "Failed to find blueprint item in your inventory.");
                                        }
                                        close();
                                    })
                    ));

            Arrays.stream(CANCEL_SLOTS).forEachOrdered(i -> setItem(i,
                    ItemStackBuilder.of(Material.valueOf(plugin.getConfig().getString("confirm-gui.items.cancel.type")))
                            .name(plugin.getConfig().getString("confirm-gui.items.cancel.name"))
                            .lore(plugin.getConfig().getStringList("confirm-gui.items.cancel.lore"))
                            .build(this::close)));

            setItem(4, ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build(null));
            setItem(13, ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build(null));
            setItem(22, ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build(null));
        }
    }
}
