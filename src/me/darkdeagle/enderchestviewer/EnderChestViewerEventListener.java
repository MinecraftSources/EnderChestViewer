package me.darkdeagle.enderchestviewer;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class EnderChestViewerEventListener implements Listener {
    
    private EnderChestViewer plugin;
    public EnderChestViewerEventListener(final EnderChestViewer plugin, final String prefix) {
        this.plugin = plugin;
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
