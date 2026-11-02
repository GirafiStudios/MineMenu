package com.girafi.minemenu.data.menu;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.json.MenuLoader;
import com.girafi.minemenu.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

public class MenuSession {

    public static void onJoin() {
        File file = resolveMenuFile();
        MineMenuCommon.menuFile = file;
        RadialMenu.clear();

        if (!file.exists()) {
            createMenuFile(file);
        }
        MenuLoader.load(file);
    }

    public static void onLeave() {
        RadialMenu.clear();
        MineMenuCommon.menuFile = MineMenuCommon.globalMenuFile;
    }

    private static File resolveMenuFile() {
        if (Config.GENERAL.menuScope.get() == Config.MenuScope.GLOBAL) {
            return MineMenuCommon.globalMenuFile;
        }
        Minecraft mc = Minecraft.getInstance();

        // Singleplayer & worlds opened to LAN by this player are stored inside the world folder, so it moves with the world
        IntegratedServer singleplayer = mc.getSingleplayerServer();
        if (singleplayer != null) {
            return singleplayer.getWorldPath(LevelResource.ROOT).resolve(Constants.MOD_ID).resolve("menu.json").normalize().toFile();
        }

        // Dedicated servers: one file per address
        ServerData server = mc.getCurrentServer();
        if (server != null && !server.isLan() && !server.isRealm() && server.ip != null && !server.ip.isBlank()) {
            return new File(new File(MineMenuCommon.menuFolder, "servers"), getServerFileName(server.ip));
        }
        return MineMenuCommon.globalMenuFile;
    }

    static String getServerFileName(String ip) {
        ServerAddress address = ServerAddress.parseString(ip.trim());
        String host = address.getHost().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9.-]", "_");
        return host + "_" + address.getPort() + ".json";
    }

    /**
     * New worlds and servers start with a copy of the global menu, so players keep their usual button configuration.
     */
    private static void createMenuFile(File file) {
        File global = MineMenuCommon.globalMenuFile;
        if (!file.equals(global) && global.exists()) {
            try {
                Files.createDirectories(file.toPath().getParent());
                Files.copy(global.toPath(), file.toPath());
                return;
            } catch (IOException ex) {
                Constants.LOG.error("Failed to copy the global menu to {}. Starting with an empty menu.", file, ex);
            }
        }
        MenuLoader.save(file);
    }
}
