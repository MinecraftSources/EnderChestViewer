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

package me.darkdeagle.bukkit.enderchestviewer.handlers;

import me.darkdeagle.bukkit.enderchestviewer.EnderChestViewer;

import net.minecraft.server.v1_6_R3.DedicatedServer;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.PlayerInteractManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VanillaHandler {
    
    public static boolean openEnderChest(EnderChestViewer plugin, Player player, String[] args) {
        String prefix = plugin.prefix;
        
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
            plugin.viewListGlobal.put(player.getName(), player);
            
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
            
            //Check if the online target is null, which means that the player is offline.
            if(onlineTarget != null) {
                //If its online, do a easy thing and go get his ender chest contents.
                player.openInventory(onlineTarget.getEnderChest());
                plugin.viewListGlobal.put(player.getName(), onlineTarget);
                player.sendMessage(prefix + "The Ender Chest of " + onlineTarget.getDisplayName() + " is now open for you.");
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
                return true;
            } else {
                //Has the target has never played before or did the player type something wrong?
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
                    
                    plugin.viewListGlobal.put(player.getName(), target);
                    
                    player.sendMessage(prefix + "The Ender Chest of " + target.getDisplayName() + " is now open for you.");
                    
                    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void closeEnderChest(EnderChestViewer plugin, InventoryCloseEvent event, boolean save) {
        Player player = (Player) event.getPlayer();
        
        //Now checking of the player is contained on the viewers list
        if(plugin.viewListGlobal.containsKey(player.getName())) {
            
            //Get the target of this Ender Chest View command
            Player target = plugin.viewListGlobal.get(player.getName());
            
            //If it's safe to save the data, lets save it!
            
            //MultiInv support: Do NOT save the data, because MultiInv saves it
            if(save) {
                //Create a ItemStack[] with the new container items
                ItemStack[] items = event.getInventory().getContents();
            
                //Set the new ender chest contents
                target.getEnderChest().clear();
                target.getEnderChest().setContents(items);
            
                //Save the target data
                //If the player is the target, or the target is now online, don't save the data, because bukkit handles it
                if(!((player == target) || target.isOnline())) target.saveData();
            }
            
            //Remove the player from the viewers list
            plugin.viewListGlobal.remove(player.getName());
            
            //Play the sound of the Ender Chest closing :)
            player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
        }
    }
}