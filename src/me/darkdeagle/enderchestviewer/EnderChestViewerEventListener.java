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

import me.darkdeagle.enderchestviewer.handlers.MultiInvHandler;
import me.darkdeagle.enderchestviewer.handlers.VanillaHandler;
import net.minecraft.server.v1_5_R3.Block;
import net.minecraft.server.v1_5_R3.EntityHuman;

import org.bukkit.Material;
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

import uk.co.tggl.pluckerpluck.multiinv.MultiInv;

public class EnderChestViewerEventListener implements Listener {
    
    private EnderChestViewer plugin;
    private String prefix;
    
    public EnderChestViewerEventListener(final EnderChestViewer plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;
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
        
        if(plugin.getServer().getPluginManager().getPlugin("MultiInv") != null) {
            MultiInv multiInv = (MultiInv) plugin.getServer().getPluginManager().getPlugin("MultiInv");
            
            MultiInvHandler.closeEnderChest(plugin, event, multiInv);
        }
        else {
            VanillaHandler.closeEnderChest(plugin, event);
        }
    }
}