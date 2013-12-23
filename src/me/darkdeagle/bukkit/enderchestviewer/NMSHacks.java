package me.darkdeagle.bukkit.enderchestviewer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSHacks {
    
    private static String craftbukkitPackage;
    private static String nmsPackage;
    
    public static Player getPlayerObjectOfOfflinePlayer(String playerName) {
        try {
            Object minecraftServer = getMinecraftServerInstance();
            
            Class<?> class_EntityPlayer = getNMSClass("EntityPlayer");
            Class<?> class_DedicatedServer = getNMSClass("DedicatedServer");
            Class<?> class_WorldServer = getNMSClass("WorldServer");
            Class<?> class_GameProfile = getNMSClass("GameProfile");
            Class<?> class_PlayerInteractManager = getNMSClass("PlayerInteractManager");
            
            Constructor<?> constructor_EntityPlayer = class_EntityPlayer.getDeclaredConstructor(class_DedicatedServer, class_WorldServer, class_GameProfile,
                    class_PlayerInteractManager);
            Constructor<?> constructor_GameProfile = class_GameProfile.getDeclaredConstructor(String.class, String.class);
            Constructor<?> constructor_PlayerInteractManager = class_PlayerInteractManager.getDeclaredConstructor(class_WorldServer);
            
            Object gameProfile = constructor_GameProfile.newInstance(null, playerName);
            Object playerInteractManager = constructor_PlayerInteractManager.newInstance(getWorldServer0());
            
            Object entityPlayer = constructor_EntityPlayer.newInstance(minecraftServer, getWorldServer0(), gameProfile, playerInteractManager);
            
            Method method_getBukkitEntity = class_EntityPlayer.getDeclaredMethod("getBukkitEntity");
            
            return (Player) method_getBukkitEntity.invoke(entityPlayer);
        } catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
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
    
    private static Object reflectWorldServer0() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return getMinecraftServerInstance().getClass().getDeclaredMethod("getWorldServer", int.class).invoke(getMinecraftServerInstance(), 0);
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
        Method method_getServer = class_DedicatedServer.getDeclaredMethod("getServer");
        
        return method_getServer.invoke(class_DedicatedServer);
    }
    
    public static String getCraftbukkitPackage() {
        if(craftbukkitPackage != null) {
            return craftbukkitPackage;
        }
        
        return craftbukkitPackage = Bukkit.getServer().getClass().getPackage().getName() + ".";
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getCraftbukkitClass(String className) {
        try {
            return (Class<T>) Class.forName(getCraftbukkitPackage() + className);
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
    
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getNMSClass(String className) {
        try {
            return (Class<T>) Class.forName(getNMSPackage() + className);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}