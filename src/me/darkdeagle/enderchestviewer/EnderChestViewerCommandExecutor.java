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

import net.minecraft.server.v1_5_R3.DedicatedServer;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.MinecraftServer;
import net.minecraft.server.v1_5_R3.PlayerInteractManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestViewerCommandExecutor implements CommandExecutor {

	public String prefix;
	public EnderChestViewer plugin;
	
	public EnderChestViewerCommandExecutor(EnderChestViewer plugin, String prefix) {
		this.prefix = prefix;
		this.plugin = plugin;
	}

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
			String[] args) {
		
        //Is this command being executed from the console?
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command cannot be executed from the console!");
			return false;
		}
		
		Player player = (Player) sender;
		
		//Does the player have access to the command?
		if(cmd.getName().equalsIgnoreCase("enderchestviewer")) {
			if(!player.hasPermission("enderchestviewer.enderchestviewer")) {
				player.sendMessage(prefix + ChatColor.DARK_RED + "You don't have permission to use this command.");
				return false;
			}
			
			//Can the player view other people ender chests?
			boolean canViewOthers = player.hasPermission("enderchestviewer.viewothers");
			
			//Check if the player can view other people ender chests, or if the command was executed without arguments
			if(!canViewOthers || args.length == 0) {
			    //Command executed with arguments, but the player has no access to see other people ender chests. So, tell him about that
			    if(!(args.length == 0)) {
			        player.sendMessage(prefix + "You cannot view the Ender Chest of somebody else. So, showing your own.");
			    } else {
			        //The player has given no arguments; show his own ender chest.
			        player.sendMessage(prefix + "Openning your Ender Chest.");
			    }
			    //Open it :)
			    player.openInventory(player.getEnderChest());
			    //Put the player on the view list, with the target for itself
			    plugin.viewList.put(player.getName(), player);
			    
			    //Have a nice sound! :P
			    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
			    
			    //Stop this method from running, even though it will do nothing
			    return true;
			}
			
			//Declare the target player name
            String targetName = args[0];
			
			//The player has permissions to view the other ender chest he is trying to access, so, do some checks
			if(canViewOthers) {
			    //Declare some variables to work with
			    Player onlineTarget = plugin.getServer().getPlayer(targetName);
			    OfflinePlayer offlineTarget = plugin.getServer().getOfflinePlayer(targetName);
			    
			    //Check if the online target is null, wich means that the player is offline.
			    if(onlineTarget != null) {
			        //If its online, do a easy thing and go get his ender chest contents.
			        player.openInventory(onlineTarget.getEnderChest());
			        plugin.viewList.put(player.getName(), onlineTarget);
			        player.sendMessage(prefix + "The Ender Chest of " + onlineTarget.getDisplayName() + " is now open for you.");
			        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
			        return true;
			    } else {
			        //Has the target has never played before or did the player typed something wrong?
			        if(!offlineTarget.hasPlayedBefore() || offlineTarget == null) {
			            player.sendMessage(prefix + "The player " + targetName + " hasn't played before. Maybe you typed something wrong.");
			            return false;
			        } else {
			            //Get the MinecraftServer instance
			            MinecraftServer minecraftServer = DedicatedServer.getServer();
			            
			            //Create and load the target EntityPlayer
                        EntityPlayer entityPlayer = new EntityPlayer(DedicatedServer.getServer(), minecraftServer.getWorldServer(0), targetName, new PlayerInteractManager(minecraftServer.getWorldServer(0)));
                        entityPlayer.getBukkitEntity().loadData();
                        
                        Player target = entityPlayer.getBukkitEntity();
                        
                        ItemStack[] items = target.getEnderChest().getContents();
                        Inventory inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
                        inventory.setContents(items);
                        
                        player.openInventory(inventory);
                        
                        plugin.viewList.put(player.getName(), target);
                        
                        player.sendMessage(prefix + "The Ender Chest of " + target.getDisplayName() + " is now open for you.");
                        
                        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
                        
                        return true;
			        }
			    }
			}
		}
		return false;
	}
}
