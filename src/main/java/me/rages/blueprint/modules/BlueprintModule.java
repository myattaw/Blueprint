package me.rages.blueprint.modules;

import lombok.Getter;
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
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.data.blueprint.BlueprintDirection;
import me.rages.blueprint.generator.BlueprintGenerator;
import me.rages.blueprint.services.BuildCheckService;
import me.rages.blueprint.ui.ConfirmUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class BlueprintModule implements TerminableModule {

    private BlueprintPlugin plugin;
    private NamespacedKey blueprintKey;
    @Getter
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
                                    cmd.sender().sendMessage(Message.BLUEPRINT_NO_PERMISSION.getColorized());
                                    return;
                                }

                                Player target = cmd.arg(1).parse(Player.class).orElse(null);
                                if (target == null) {
                                    cmd.sender().sendMessage(ChatColor.RED + "Could not find the specified player.");
                                } else {
                                    String name = cmd.rawArg(2).toLowerCase();
                                    target.getInventory().addItem(getBlueprintItem(name, 0, 1));
                                    target.sendMessage(Message.BLUEPRINT_ITEM_RECEIVED.getColorized().replace("{name}", name));
                                    cmd.sender().sendMessage(Message.BLUEPRINT_ITEM_GIVEN.getColorized()
                                            .replace("{name}", name)
                                            .replace("{player}", target.getName())
                                    );
                                }
                                break;
                            case "list":
                                if (!cmd.sender().hasPermission("blueprint.list")) {
                                    cmd.sender().sendMessage(Message.BLUEPRINT_NO_PERMISSION.getColorized());
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

        Events.subscribe(BlockPlaceEvent.class)
                .filter(event -> event.getItemInHand() != null && event.getItemInHand().hasItemMeta())
                .handler(event -> {
                    ItemStack itemStack = event.getItemInHand();
                    String name = itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(blueprintKey, PersistentDataType.STRING, null);

                    Blueprint blueprint = plugin.getBlueprintDataMap().get(name);

                    if (blueprint != null) {
                        event.setCancelled(true);
                    }
                }).bindWith(consumer);

        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> event.getItem() != null && event.getItem().hasItemMeta())
                .handler(event -> {

                    ItemStack itemStack = event.getItem();
                    String name = itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(blueprintKey, PersistentDataType.STRING, null);
                    Player player = event.getPlayer();

                    Blueprint blueprint = plugin.getBlueprintDataMap().get(name);

                    if (blueprint == null) {
                        return;
                    }

                    // Using player direction (make this configurable so we can use rotate menu)
                    BlueprintDirection bpDirection = BlueprintDirection.fromRotation(
                            blueprint.isSnapToChunk() ? 180 : getPlayerDirection(player)
                    );


                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        if (itemStack != null && name != null) {
                            Block block = event.getClickedBlock().getRelative(event.getBlockFace());

                            if (blueprint.isSnapToChunk()) {
                                block = event.getClickedBlock().getChunk().getBlock(15, block.getY(), 15);
                            }

                            blueprint.sendOutline(player, block, bpDirection);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (event.getClickedBlock() != null) {
                        Block block = event.getClickedBlock().getRelative(event.getBlockFace());

                        if (blueprint.isSnapToChunk()) {
                            block = event.getClickedBlock().getChunk().getBlock(15, block.getY(), 15);
                        }

                        Location loc = block.getLocation();
                        BuildCheckService buildCheckService = plugin.getServiceManager().getService(BuildCheckService.class);
                        // check if bp config is using player direction
                        if (buildCheckService == null || buildCheckService.canBuild(player, blueprint.getPoints().get(bpDirection), loc)) {
                            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                if (itemStack != null && name != null) {
                                    new ConfirmUI(plugin, itemStack, blueprint, bpDirection, player, loc).open();
                                }
                            }
                        } else {
                            player.sendMessage(Message.BLUEPRINT_PLACEMENT_FAILED.getColorized());
                        }
                    }

                }).bindWith(consumer);

        Schedulers.sync().runRepeating(() -> {
            if (!plugin.getBlueprintGenerators().isEmpty()) {
                Iterator<BlueprintGenerator> iterator = plugin.getBlueprintGenerators().iterator();
                while (iterator.hasNext()) {
                    BlueprintGenerator generator = iterator.next();
                    if (generator.build(plugin.getBlueprintDataMap())) { // check if we finished building
                        generator.getPlayer().sendMessage(Message.BLUEPRINT_TASK_FINISHED.getColorized());
                        iterator.remove();
                    }
                }
            }
        }, 5L, 5L).bindWith(consumer);
    }

    public ItemStack getBlueprintItem(String name, int direction, int amount) {

        Blueprint blueprint = plugin.getBlueprintDataMap().get(name);
        ItemStack itemStack = ItemStackBuilder
                .of(blueprint.getMaterial())
                .enchant(Enchantment.DURABILITY)
                .name(blueprint.getDisplayName().replace("{name}", name))
                .transformMeta(itemMeta -> {
                    List<String> itemLore = blueprint.getDisplayLore()
                            .stream()
                            .map(lore -> Text.colorize(lore.replace("{direction}", Objects.requireNonNull(BlueprintDirection.fromRotation(direction)).getName())))
                            .collect(Collectors.toList());
                    itemMeta.setLore(itemLore);
                })
                .amount(amount)
                .transformMeta(itemMeta -> {
                    itemMeta.getPersistentDataContainer().set(blueprintKey, PersistentDataType.STRING, name);
                    itemMeta.getPersistentDataContainer().set(directionKey, PersistentDataType.INTEGER, 0);
                }).build();


        return itemStack;
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

    public int getPlayerDirection(Player player) {
        double degrees = (player.getLocation().getYaw() - 180) % 360;
        if (degrees < 0) {
            degrees += 360;
        }

        if (0 <= degrees && degrees < 45) {
            return 0;
        } else if (45 <= degrees && degrees < 135) {
            return 90;
        } else if (135 <= degrees && degrees < 225) {
            return 180;
        } else if (225 <= degrees && degrees < 315) {
            return 270;
        } else if (315 <= degrees && degrees < 360.0) {
            return 0;
        }
        return 0;
    }

}
