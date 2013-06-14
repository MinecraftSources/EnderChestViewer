package me.darkdeagle.enderchestviewer;

import net.minecraft.server.v1_5_R3.Block;
import net.minecraft.server.v1_5_R3.EntityHuman;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class EnderChestViewerEventListener implements Listener {
    
    private EnderChestViewer plugin;
    private String prefix;
    
    public EnderChestViewerEventListener(final EnderChestViewer plugin, final String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
    }
    
    @EventHandler
    public void onPlayerPrepareCraftEvent(PrepareItemCraftEvent event) {
        ItemStack recipeOutput = event.getRecipe().getResult();
        
        if(recipeOutput.getType() == Material.ENDER_CHEST && !event.getView().getPlayer().hasPermission("enderchestviewer.block.craft")) {
            recipeOutput.setType(Material.AIR);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if(event.getBlockPlaced() == Block.ENDER_CHEST && !player.hasPermission("enderchestviewer.block.place")) {
            player.sendMessage(prefix + "You don't have permission to place Ender Chests.");
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(InventoryOpenEvent event) {
        if(!(event.getPlayer() instanceof EntityHuman)) return;
        
        Player player = plugin.getServer().getPlayer(event.getPlayer().getName());
        
        if(event.getInventory().getType() == InventoryType.ENDER_CHEST && !player.hasPermission("enderchestviewer.block.use")) {
            player.sendMessage(prefix + "You don't have permission to use Ender Chests.");
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if(event.getBlock() == Block.ENDER_CHEST && !player.hasPermission("enderchestviewer.block.break")) {
            player.sendMessage(prefix + "You don't have permission to break Ender Chests.");
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        
        //Create a player object
        Player player = (Player) event.getPlayer();
        
        //Now checking of the player is contained on the viewers list
        if(plugin.viewList.containsKey(player.getName())) {
            
            //Get the target of this Ender Chest View command
            Player target = plugin.viewList.get(player.getName());
            
            //If it's safe to save the data, lets save it!
            
            //Create a ItemStack[] with the new container items
            ItemStack[] items = event.getInventory().getContents();
            
            //Set the new ender chest contents
            target.getEnderChest().clear();
            target.getEnderChest().setContents(items);
            
            //Save the target data
            //If the player is the target, or the target is now online, don't save the data, because bukkit handles it
            if(!((player == target) || target.isOnline())) target.saveData();
            
            //Remove the player from the viewers list
            plugin.viewList.remove(player.getName());
            
            //Play the sound of the Ender Chest closing :)
            player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 5.0f, 1.0f);
        }
    }
    
}
