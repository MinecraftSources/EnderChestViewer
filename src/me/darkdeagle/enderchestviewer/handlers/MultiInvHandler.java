package me.darkdeagle.enderchestviewer.handlers;

import me.darkdeagle.enderchestviewer.EnderChestViewer;
import net.minecraft.server.v1_5_R3.DedicatedServer;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.PlayerInteractManager;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import uk.co.tggl.pluckerpluck.multiinv.MultiInv;
import uk.co.tggl.pluckerpluck.multiinv.api.MIAPIPlayer;
import uk.co.tggl.pluckerpluck.multiinv.inventory.MIEnderchestInventory;

public class MultiInvHandler {
    
    public static boolean openEnderChest(EnderChestViewer plugin, Player bukkitPlayer, String[] args, MultiInv multiinv) {
        String prefix = plugin.prefix;
        
        //Can the player view other people ender chests?
        boolean canViewOthers = bukkitPlayer.hasPermission("enderchestviewer.viewothers");
          
        //Check if the player can view other people ender chests, or if the command was executed without arguments
        if(!canViewOthers || args.length == 0) {
            MIAPIPlayer player = multiinv.getAPI().getPlayerInstance(bukkitPlayer.getName(), bukkitPlayer.getWorld().getName(), bukkitPlayer.getGameMode());
            
            //Command executed with arguments, but the player has no access to see other people ender chests. So, tell him about that
            if(!(args.length == 0)) {
                bukkitPlayer.sendMessage(prefix + "You cannot view the Ender Chest of somebody else. So, showing your own.");
            } else {
                //The player has given no arguments; show his own ender chest.
                bukkitPlayer.sendMessage(prefix + "Openning your Ender Chest.");
            }
            //Open it :)
            Inventory inventory = null;
            player.getEnderchest().loadIntoInventory(inventory);
            bukkitPlayer.openInventory(inventory);
            //Put the player on the view list, with the target for itself
            plugin.viewListGlobal.put(bukkitPlayer.getName(), bukkitPlayer);
            plugin.viewListMultiinv.put(bukkitPlayer.getName(), player);
            
            //Have a nice sound! :P
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
            
            //Stop this method from running, even though it will do nothing
            return true;
        }
          
        //Declare the target player name
        String targetName = args[0];
        
        //The player has permissions to view the other ender chest he is trying to access, so, do some checks
        if(canViewOthers) {
            
            //Declare some variables to work with
            Player bukkitOnlineTarget = plugin.getServer().getPlayer(targetName);
            OfflinePlayer bukkitOfflineTarget = plugin.getServer().getOfflinePlayer(targetName);
            
            //Check if the online target is null, which means that the player is offline or never played.
            if(bukkitOnlineTarget != null) {
                
                String worldName;
                GameMode gameMode;
                
                //Set the world name and the game mode that we are going to use to get the ender chest
                if(args[1] != null) worldName = args[1]; else worldName = bukkitOnlineTarget.getWorld().getName();
                if(args[2] != null) {
                    try{
                        gameMode = GameMode.getByValue(Integer.parseInt(args[2]));
                    } catch(NumberFormatException e) {
                        bukkitPlayer.sendMessage(prefix + "The GameMode must be 0 (Survival), 1 (Creative) or 2 (Adventure)");
                        return false;
                    }
                } else gameMode = bukkitOnlineTarget.getGameMode();
                
                //If its online, do a easy thing and go get his ender chest contents.
                Inventory inventory = null;
                MIAPIPlayer target = multiinv.getAPI().getPlayerInstance(targetName, worldName, gameMode);
                
                target.getEnderchest().loadIntoInventory(inventory);
                bukkitPlayer.openInventory(inventory);
                
                plugin.viewListGlobal.put(bukkitPlayer.getName(), bukkitOnlineTarget);
                plugin.viewListMultiinv.put(bukkitPlayer.getName(), target);
                bukkitPlayer.sendMessage(prefix + "The Ender Chest of " + bukkitOnlineTarget.getDisplayName() + " is now open for you.");
                bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
                return true;
            } else {
                //Has the target has never played before or did the player type something wrong?
                if(!bukkitOfflineTarget.hasPlayedBefore() || bukkitOfflineTarget == null) {
                    bukkitPlayer.sendMessage(prefix + "The player " + targetName + " hasn't played before or maybe you typed something wrong.");
                    return false;
                } else {
                    String worldName;
                    GameMode gameMode;
                    
                    //Set the world name and the game mode that we are going to use to get the ender chest
                    if(args[1] != null) worldName = args[1]; else worldName = bukkitPlayer.getWorld().getName();
                    if(args[2] != null) {
                        try{
                            gameMode = GameMode.getByValue(Integer.parseInt(args[2]));
                        } catch(NumberFormatException e) {
                            bukkitPlayer.sendMessage(prefix + "The GameMode must be 0 (Survival), 1 (Creative) or 2 (Adventure)");
                            return false;
                        }
                    } else gameMode = bukkitPlayer.getGameMode();
                    
                    //Create and load the target EntityPlayer
                    EntityPlayer entityPlayer = new EntityPlayer(DedicatedServer.getServer(), ((CraftWorld)plugin.getServer().getWorld(worldName)).getHandle(), targetName, new PlayerInteractManager(((CraftWorld)plugin.getServer().getWorld(worldName)).getHandle()));
                    Player bukkitTarget = entityPlayer.getBukkitEntity();
                    
                    Inventory inventory = null;
                    MIAPIPlayer target = multiinv.getAPI().getPlayerInstance(targetName, worldName, gameMode);
                    
                    target.getEnderchest().loadIntoInventory(inventory);
                    
                    bukkitPlayer.openInventory(inventory);
                    
                    plugin.viewListGlobal.put(bukkitPlayer.getName(), bukkitTarget);
                    plugin.viewListMultiinv.put(bukkitPlayer.getName(), target);
                    
                    bukkitPlayer.sendMessage(prefix + "The Ender Chest of " + bukkitTarget.getName() + " is now open for you.");
                    
                    bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void closeEnderChest(EnderChestViewer plugin, InventoryCloseEvent event, MultiInv multiinv) {
        Player bukkitPlayer = (Player) event.getPlayer();
        
        //Now checking of the player is contained on the viewers list
        if(plugin.viewListGlobal.containsKey(bukkitPlayer.getName())) {
            
            //Get the target of this Ender Chest View command, in 2 ways: the bukkit way and the multi inv way
            Player bukkitTarget = plugin.viewListGlobal.get(bukkitPlayer.getName());
            MIAPIPlayer target = plugin.viewListMultiinv.get(bukkitTarget.getName());
            
            //If it's safe to save the data, lets save it!
            
            //Create a ItemStack[] with the new container items
            ItemStack[] items = event.getInventory().getContents();
            MIEnderchestInventory ec = new MIEnderchestInventory(items);
            
            //Set the new ender chest contents
            target.setEnderchest(ec);
            
            //Remove the player from the viewers list
            plugin.viewListGlobal.remove(bukkitPlayer.getName());
            plugin.viewListMultiinv.remove(bukkitPlayer.getName());
            
            //Play the sound of the Ender Chest closing :)
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.CHEST_CLOSE, 5.0f, 1.0f);
        }
    }
}
