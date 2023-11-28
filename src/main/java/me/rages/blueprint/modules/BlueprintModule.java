package me.rages.blueprint.modules;

import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Message;
import me.rages.blueprint.data.Points;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import me.rages.blueprint.generator.BlueprintGenerator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BlueprintModule implements TerminableModule {

    private BlueprintPlugin plugin;
    private NamespacedKey blueprintKey;
    private NamespacedKey directionKey;

    public BlueprintModule(BlueprintPlugin plugin) {
        this.plugin = plugin;
        this.blueprintKey = new NamespacedKey(plugin, "BP_NAME");
        this.directionKey = new NamespacedKey(plugin, "BP_DIRECTION");
    }

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {

        Commands.create()
                .tabHandler(c -> {
                    if (c.args().size() == 1) {
                        return Arrays.asList("give", "list");
                    } else if (c.args().size() == 2) {
                        List<String> users = new ArrayList<>();
                        Players.all().forEach(player -> users.add(player.getName()));
                        return users;
                    } else if (c.args().size() == 3) {
                        return new ArrayList<>(plugin.getBlueprintDataMap().keySet());
                    }
                    return null;
                })
                .handler(cmd -> {

                    String arg = cmd.arg(0).parse(String.class).orElse(null);
                    if (arg == null) {

                    } else {
                        switch (arg.toLowerCase()) {
                            case "give":
                                if (!cmd.sender().hasPermission("blueprint.give")) {
                                    cmd.sender().sendMessage(ChatColor.RED + "You do not have access to this command.");
                                    return;
                                }

                                Player target = cmd.arg(1).parse(Player.class).orElse(null);
                                if (target == null) {
                                    cmd.sender().sendMessage(ChatColor.RED + "Could not find the specified player.");
                                } else {
                                    String name = cmd.rawArg(2).toLowerCase();
                                    giveBlueprintItem(target, name);
                                    target.sendMessage(Message.BLUEPRINT_ITEM_RECEIVED.getColorized().replace("{name}", name));
                                    cmd.sender().sendMessage(Message.BLUEPRINT_ITEM_GIVEN.getColorized()
                                            .replace("{name}", name)
                                            .replace("{player}", target.getName())
                                    );
                                }
                                break;
                            case "list":
                                if (!cmd.sender().hasPermission("blueprint.list")) {
                                    cmd.sender().sendMessage(ChatColor.RED + "You do not have access to this command.");
                                    return;
                                }

                                cmd.sender().sendMessage(Text.colorize("&7------[ &9Blueprints &7]------"));
                                cmd.sender().sendMessage(ChatColor.GRAY + String.join(",", plugin.getBlueprintDataMap().keySet()));

                                break;
                            default:
                                break;
                        }
                    }
                })
                .registerAndBind(consumer, "blueprint");

        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> event.getItem() != null && event.getItem().hasItemMeta())
                .handler(event -> {

                    ItemStack itemStack = event.getItem();

                    String name = itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(blueprintKey, PersistentDataType.STRING, null);
                    int direction = itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(directionKey, PersistentDataType.INTEGER, 0);

                    Player player = event.getPlayer();

                    if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (itemStack != null && name != null) {
                            //TODO: add build check here
                            Location loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
                            if (removeItem(event.getPlayer(), itemStack, 1)) {
                                plugin.getBlueprintGenerators().add(
                                        BlueprintGenerator.create(name, BlueprintDirection.fromRotation(direction), loc)
                                                .addPlayer(player)
                                                .setFastMode(true)
                                );

                                Points<Vector, Vector> points = plugin.getBlueprintDataMap().get(name).getPoints().get(BlueprintDirection.fromRotation(direction));

                                Arrays.stream(Message.BLUEPRINT_TASK_STARTED.getAllColorized())
                                        .map(message -> message.replace("{time}", points.getMax().getY() - points.getMin().getY() + " Seconds"))
                                        .forEach(player::sendMessage);
                            }
                        }
                    }

                }).bindWith(consumer);

        Schedulers.sync().runRepeating(() -> {
            if (!plugin.getBlueprintGenerators().isEmpty()) {
                Iterator<BlueprintGenerator> iterator = plugin.getBlueprintGenerators().iterator();
                while (iterator.hasNext()) {
                    BlueprintGenerator generator = iterator.next();
                    if (generator.build(plugin, plugin.getBlueprintDataMap())) { // check if we finished building
                        generator.getPlayer().sendMessage(Message.BLUEPRINT_TASK_FINISHED.getColorized());
                        iterator.remove();
                    }
                }
            }
        }, 1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS).bindWith(consumer);
    }

    public void giveBlueprintItem(Player player, String name) {
        ItemStack itemStack = ItemStackBuilder.of(Material.valueOf(plugin.getConfig().getString("blueprint-item.type")))
                .name(plugin.getConfig().getString("blueprint-item.name").replace("{name}", name))
                .lore(plugin.getConfig().getStringList("blueprint-item.lore"))
                .transformMeta(itemMeta -> {
                    itemMeta.getPersistentDataContainer().set(blueprintKey, PersistentDataType.STRING, name);
                    itemMeta.getPersistentDataContainer().set(directionKey, PersistentDataType.INTEGER, 0);
                })
                .build();

        player.getInventory().addItem(itemStack);
    }

    public boolean removeItem(Player player, ItemStack itemStack, int amount) {
        Map<Integer, ? extends ItemStack> items = player.getInventory().all(itemStack.getType());

        int found = 0;
        for (ItemStack stack : items.values()) {
            if (isCustomNameMatch(stack, itemStack)) { // check namespace keys
                found += stack.getAmount();
            }
        }

        int count = amount;
        if (count > found) {
            return false;
        }

        for (Integer index : items.keySet()) {
            ItemStack stack = items.get(index);

            if (!isCustomNameMatch(stack, itemStack)) {
                continue; // Skip if the custom names don't match
            }

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed) {
                player.getInventory().setItem(index, null);
            } else {
                stack.setAmount(stack.getAmount() - removed);
            }

            if (count <= 0) {
                break;
            }
        }
        return true;
    }

    public static boolean isCustomNameMatch(ItemStack stack1, ItemStack stack2) {
        // change this to check namespace keys
        String customName1 = stack1.getItemMeta().getDisplayName();
        String customName2 = stack2.getItemMeta().getDisplayName();

        return customName1.equals(customName2);
    }


}
