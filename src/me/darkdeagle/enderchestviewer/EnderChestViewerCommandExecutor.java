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

import me.darkdeagle.enderchestviewer.handlers.VanillaHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestViewerCommandExecutor implements CommandExecutor {

	public String prefix;
	public EnderChestViewer plugin;
	
	public EnderChestViewerCommandExecutor(EnderChestViewer plugin) {
		this.prefix = plugin.prefix;
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
			
			if(args.length > 1) {
	            player.sendMessage(prefix + ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + "/" + commandLabel + " <target>");
	            return false;
	        }
			    
			return VanillaHandler.openEnderChest(plugin, player, args);
		}
        return false;
	}
}
