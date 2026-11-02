package com.girafi.minemenu;

import com.girafi.minemenu.network.packet.server.PacketUseItem;
import commonnetwork.api.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class MineMenuCommon {
    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    public static File menuFolder;
    public static File globalMenuFile;
    public static File menuFile;

    public static void loadMenuJson(File gameDirPath) {
        menuFolder = new File(gameDirPath, Constants.MOD_ID);
        globalMenuFile = new File(menuFolder, "menu.json");
        menuFile = globalMenuFile;
    }

    public static void registerPackets() {
        Network.registerPacket(PacketUseItem.type(), PacketUseItem.class, PacketUseItem.STREAM_CODEC, PacketUseItem::handle);
    }
}