package com.girafi.minemenu;

import com.girafi.minemenu.data.json.MenuLoader;
import com.girafi.minemenu.network.packet.server.PacketUseItem;
import commonnetwork.api.Network;
import org.apache.commons.logging.Log;
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
        Network.registerPacket(PacketUseItem.CHANNEL, PacketUseItem.class, PacketUseItem::encode, PacketUseItem::decode, PacketUseItem::handle);
    }

    public static void setupMenuLoader() {
        if (!menuFolder.exists()) {
            menuFolder.mkdir();
        }

        if (!menuFile.exists()) {
            MenuLoader.save(menuFile);
        }
        MenuLoader.load(menuFile);
    }
}