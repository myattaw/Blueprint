package me.rages.blueprint.ui;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.text3.Text;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RotateUI extends Gui {

    private BlueprintPlugin plugin;
    private Blueprint blueprint;
    private ItemStack itemStack;

    public RotateUI(BlueprintPlugin plugin, Blueprint blueprint, Player player, ItemStack itemStack) {
        super(player, 1, plugin.getConfig().getString("rotate-gui.title").replace("{name}", blueprint.getName()));
        this.plugin = plugin;
        this.blueprint = blueprint;
        this.itemStack = itemStack;
    }

    @Override
    public void redraw() {

        int rotation = Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().getOrDefault(plugin.getBlueprintModule().getDirectionKey(), PersistentDataType.INTEGER, 0);
        BlueprintDirection direction = BlueprintDirection.fromRotation(rotation);

        setItem(0,
                ItemStackBuilder.of(Material.valueOf(plugin.getConfig().getString("rotate-gui.items.rotate-left.type")))
                        .name(plugin.getConfig().getString("rotate-gui.items.rotate-left.name").replace("{name}", blueprint.getName()))
                        .transformMeta(itemMeta -> {
                            List<String> itemLore = plugin.getConfig().getStringList("rotate-gui.items.rotate-left.lore")
                                    .stream()
                                    .map(lore -> {
                                        assert direction != null;
                                        return Text.colorize(lore.replace("{direction}", direction.leftDirection().getName()));
                                    })
                                    .collect(Collectors.toList());
                            itemMeta.setLore(itemLore);
                        })
                        .build(() -> {
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.getPersistentDataContainer().set(
                                    plugin.getBlueprintModule().getDirectionKey(),
                                    PersistentDataType.INTEGER,
                                    direction.leftDirection().getRotation()
                            );
                            List<String> itemLore = plugin.getConfig().getStringList("blueprint-item.lore")
                                    .stream()
                                    .map(lore -> Text.colorize(lore.replace("{direction}", Objects.requireNonNull(direction.leftDirection()).getName())))
                                    .collect(Collectors.toList());
                            itemMeta.setLore(itemLore);
                            itemStack.setItemMeta(itemMeta);
                            redraw();
                        })
        );

        setItem(4, Item.builder(itemStack).build());

        setItem(8,
                ItemStackBuilder.of(Material.valueOf(plugin.getConfig().getString("rotate-gui.items.rotate-right.type")))
                        .name(plugin.getConfig().getString("rotate-gui.items.rotate-left.name").replace("{name}", blueprint.getName()))
                        .transformMeta(itemMeta -> {
                            List<String> itemLore = plugin.getConfig().getStringList("rotate-gui.items.rotate-right.lore")
                                    .stream()
                                    .map(lore -> {
                                        assert direction != null;
                                        return Text.colorize(lore.replace("{direction}", direction.rightDirection().getName()));
                                    })
                                    .collect(Collectors.toList());
                            itemMeta.setLore(itemLore);
                        })
                        .build(() -> {
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.getPersistentDataContainer().set(
                                    plugin.getBlueprintModule().getDirectionKey(),
                                    PersistentDataType.INTEGER,
                                    direction.rightDirection().getRotation()
                            );
                            List<String> itemLore = plugin.getConfig().getStringList("blueprint-item.lore")
                                    .stream()
                                    .map(lore -> Text.colorize(lore.replace("{direction}", Objects.requireNonNull(direction.rightDirection()).getName())))
                                    .collect(Collectors.toList());
                            itemMeta.setLore(itemLore);
                            itemStack.setItemMeta(itemMeta);
                            redraw();
                        })
        );

    }

}
