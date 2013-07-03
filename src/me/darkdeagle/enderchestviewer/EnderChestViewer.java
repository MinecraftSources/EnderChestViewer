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
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class EnderChestViewer extends JavaPlugin {

	private CommandExecutor commandExecutor;
	private Listener eventListener;
	
	public String prefix;
	
	public HashMap<String, Player> viewListGlobal = new HashMap<String, Player>();
	
	@Override
	public void onEnable() {
	    prefix = ChatColor.DARK_BLUE + "[" + this.getDescription().getName() + "] " + ChatColor.WHITE;
	    
		this.commandExecutor = new EnderChestViewerCommandExecutor(this);
		this.getCommand("enderchestviewer").setExecutor(commandExecutor);
		
		this.eventListener = new EnderChestViewerEventListener(this);
		this.getServer().getPluginManager().registerEvents(eventListener, this);
		
		this.getLogger().info("is now enabled!");
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("is now disabled!");
	}
}
