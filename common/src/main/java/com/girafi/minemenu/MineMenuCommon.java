package com.girafi.minemenu;

import com.girafi.minemenu.data.json.MenuLoader;
import com.girafi.minemenu.helper.KeyReflectionHelper;
import com.girafi.minemenu.network.packet.server.PacketUseItem;
import commonnetwork.api.Network;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class MineMenuCommon {
    public static File menuFolder;
    public static File menuFile;
    public static final KeyMapping WHEEL = new KeyMapping("key.open_menu", GLFW.GLFW_KEY_R, "key.categories.misc");

    public static void loadCommon(File gameDirPath) {
        menuFolder = new File(gameDirPath, Constants.MOD_ID);
        menuFile = new File(menuFolder, "menu.json");

        Network.registerPacket(PacketUseItem.CHANNEL, PacketUseItem.class, PacketUseItem::encode, PacketUseItem::decode, PacketUseItem::handle);
        KeyReflectionHelper.gatherFields();
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