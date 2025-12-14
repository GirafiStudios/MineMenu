package com.girafi.minemenu;

import com.girafi.minemenu.data.json.MenuLoader;
import com.girafi.minemenu.network.packet.server.PacketUseItem;
import commonnetwork.api.Network;
import net.minecraft.core.RegistryAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class MineMenuCommon {
    public static final Logger LOGGER = LogManager.getLogger("Mine Menu");
    public static File menuFolder;
    public static File menuFile;

    public static void loadMenuJson(File gameDirPath) {
        menuFolder = new File(gameDirPath, Constants.MOD_ID);
        menuFile = new File(menuFolder, "menu.json");
    }

    public static void registerPackets() {
        Network.registerPacket(PacketUseItem.type(), PacketUseItem.class, PacketUseItem.STREAM_CODEC, PacketUseItem::handle);
    }

    public static void setupMenuLoader(RegistryAccess registryAccess) {
        if (!menuFolder.exists()) {
            menuFolder.mkdir();
        }

        if (!menuFile.exists()) {
            MenuLoader.save(menuFile);
        }
        MenuLoader.load(menuFile);
    }
}