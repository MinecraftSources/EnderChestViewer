/**
 * This file is part of EnderChestViewer.

    EnderChestViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    EnderChestViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with EnderChestViewer.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.darkdeagle.enderchestviewer;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class EnderChestViewer extends JavaPlugin implements Listener {

	public PluginDescriptionFile pdfFile;
	public CommandExecutor commandExecutor;
	public HashMap<String, Player> viewList = new HashMap<String, Player>();
	
	@Override
	public void onEnable() {
		pdfFile = this.getDescription();
		commandExecutor = new EnderChestViewerCommandExecutor(this, ChatColor.DARK_BLUE + "[" + pdfFile.getName() + "] " + ChatColor.WHITE);
		this.getCommand("enderchestviewer").setExecutor(commandExecutor);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("is now enabled!");
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("is now disabled!");
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
	    
	    //Create a player object
	    Player player = (Player) event.getPlayer();
	    
	    //Now checking of the player is contained on the viewers list
		if(this.viewList.containsKey(player.getName())) {
		    
	        //Get the target of this Ender Chest View command
            Player target = this.viewList.get(player.getName());
	        
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
		    this.viewList.remove(player.getName());
		    
		    //Play the sound of the Ender Chest closing :)
			player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 5.0f, 1.0f);
		}
	}
}
