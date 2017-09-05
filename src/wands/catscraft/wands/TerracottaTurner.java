package wands.catscraft.wands;

import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import simple.brainsynder.api.ItemMaker;
import wands.catscraft.commands.Command;
import wands.catscraft.commands.CommandListener;

public class TerracottaTurner implements Listener, CommandListener {

    @Command(name="blockTurner")
    public void run (Player player) {
        Item drop = player.getWorld().dropItem(player.getEyeLocation(), getWand());
        drop.setPickupDelay(0);
    }

    @EventHandler
    public void rotate(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack stack = event.getPlayer().getEquipment().getItemInMainHand();
        if (stack == null) return;
        if (stack.getType() != Material.STICK) return;

        if (stack.isSimilar(getWand())) {
            Block block = event.getClickedBlock();
            if (block == null) return;
            if (block.getType().toString().contains("GLAZED")) return;
            BlockState state = block.getState();
            if (!(state instanceof Directional)) return;
            Directional directional = ((Directional) state);
            BlockFace face = directional.getFacing();

            if (!canRotate(event.getPlayer(), block.getLocation())) {
                event.getPlayer().sendMessage("§cYou are not permitted to rotate that block.");
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                return;
            }

            switch (face) {
                case NORTH:
                    directional.setFacingDirection(BlockFace.EAST);
                case EAST:
                    directional.setFacingDirection(BlockFace.SOUTH);
                case SOUTH:
                    directional.setFacingDirection(BlockFace.WEST);
                case WEST:
                    directional.setFacingDirection(BlockFace.NORTH);
            }
        }
    }


    private ItemStack getWand() {
        ItemMaker maker = new ItemMaker(Material.STICK);
        maker.setName("&eTerra&6-&eRotate");
        maker.addLoreLine("&eRight click Glazed Terracotta to rotate it");
        maker.enchant(Enchantment.DURABILITY, 1);
        maker.setFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
        );
        return maker.create();
    }

    private boolean canRotate(Player player, Location location) {
        try {
            com.intellectualcrafters.plot.object.Location loc = new com.intellectualcrafters.plot.object.Location(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Plot plot = loc.getPlotAbs();
            if (plot == null) {
                return player.hasPermission("plots.admin.rotateTerra.road");
            } else {
                if (plot.hasOwner()) {
                    if (player.hasPermission("plots.admin.rotateTerra.other")) {
                        if (plot.isOwner(player.getUniqueId())) {
                            return true;
                        } else if (plot.isDenied(player.getUniqueId())) {
                            return false;
                        } else {
                            return player.hasPermission("plots.admin.rotateTerra.other");
                        }
                    } else {
                        if (plot.isOwner(player.getUniqueId())) {
                            return true;
                        } else if (plot.isDenied(player.getUniqueId())) {
                            return false;
                        } else {
                            return player.hasPermission("plots.admin.rotateTerra.other");
                        }
                    }
                } else {
                    return player.hasPermission("plots.admin.rotateTerra.unowned");
                }
            }
        }catch (Exception e) {
            return true;
        }
    }
}