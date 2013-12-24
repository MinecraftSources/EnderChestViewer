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

package me.darkdeagle.bukkit.enderchestviewer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSHacks {
    
    private static String craftbukkitPackage;
    private static String nmsPackage;
    
    public static Player getPlayerObjectOfOfflinePlayer(String playerName, boolean useGameProfile) {
        try {
            Object minecraftServer = getMinecraftServerInstance();
            
            Class<?> class_EntityPlayer = getNMSClass("EntityPlayer");
            Class<?> class_MinecraftServer = getNMSClass("MinecraftServer");
            Class<?> class_World = getNMSClass("World");
            
            Class<?> class_GameProfile = null;
            if(useGameProfile) {
                class_GameProfile = getNMSClass("GameProfile");
            }
            
            Class<?> class_PlayerInteractManager = getNMSClass("PlayerInteractManager");
            
            Constructor<?> constructor_EntityPlayer = class_EntityPlayer.getDeclaredConstructor(class_MinecraftServer, class_World, useGameProfile ? class_GameProfile : String.class,
                    class_PlayerInteractManager);
            
            Constructor<?> constructor_GameProfile = null;
            if(useGameProfile) {
                constructor_GameProfile = class_GameProfile.getDeclaredConstructor(String.class, String.class);
            }
            
            Constructor<?> constructor_PlayerInteractManager = class_PlayerInteractManager.getDeclaredConstructor(class_World);
            
            Object gameProfile = null;
            if(useGameProfile) {
                gameProfile = constructor_GameProfile.newInstance(null, playerName);
            }
            
            Object playerInteractManager = constructor_PlayerInteractManager.newInstance(getWorldServer0());
            
            Object entityPlayer = constructor_EntityPlayer.newInstance(minecraftServer, getWorldServer0(), useGameProfile ? gameProfile : playerName,
                    playerInteractManager);
            
            Method method_getBukkitEntity = class_EntityPlayer.getDeclaredMethod("getBukkitEntity");
            
            return (Player) method_getBukkitEntity.invoke(entityPlayer);
        } catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean isServerPost16() {
        try {
            Bukkit.getServer().getServerIcon();
            return true;
        } catch(Throwable t) {
            return false;
        }
    }
    
    public static Object getWorldServer0() {
        try {
            return reflectWorldServer0();
        } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Object reflectWorldServer0() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        return getMinecraftServerInstance().getClass().getMethod("getWorldServer", int.class).invoke(getMinecraftServerInstance(), 0); //Not a method inside DedicatedServer, use getMethod
    }
    
    public static Object getMinecraftServerInstance() {
        try {
            return reflectMinecraftServerInstance();
        } catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Object reflectMinecraftServerInstance() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Class<?> class_DedicatedServer = getNMSClass("DedicatedServer");
        Method method_getServer = class_DedicatedServer.getMethod("getServer"); //Use getMethod instead of getDeclaredMethod, because the getServer method is declared in MinecraftServer, not DedicatedServer
        
        return method_getServer.invoke(null); //Forgot about this: reflection's javadocs say that if it is a static method, then parse null to the "obj" argument.
    }
    
    public static String getCraftbukkitPackage() {
        if(craftbukkitPackage != null) {
            return craftbukkitPackage;
        }
        
        return craftbukkitPackage = Bukkit.getServer().getClass().getPackage().getName() + ".";
    }
    
    public static Class<?> getCraftbukkitClass(String className) {
        try {
            return Class.forName(getCraftbukkitPackage() + className);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getNMSPackage() {
        if(nmsPackage != null) {
            return nmsPackage;
        }
        
        return nmsPackage = getCraftbukkitPackage().replace("org.bukkit.craftbukkit", "net.minecraft.server");
    }
    
    public static Class<?> getNMSClass(String className) {
        try {
            return Class.forName(getNMSPackage() + className);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getModifiablePartOfPackageName() {
        return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "").replace(".CraftServer", "");
    }
}