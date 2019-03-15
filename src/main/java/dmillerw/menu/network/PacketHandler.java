package dmillerw.menu.network;

import dmillerw.menu.network.packet.server.PacketUseItem;
import dmillerw.menu.reference.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Reference.MOD_ID, "mine_menu_channel"))
            .clientAcceptedVersions(v -> true)
            .serverAcceptedVersions(v -> true)
            .networkProtocolVersion(() -> "MINEMENU1")
            .simpleChannel();

    public static void initialize() {
        CHANNEL.registerMessage(0, PacketUseItem.class, PacketUseItem::encode, PacketUseItem::decode, PacketUseItem.Handler::handle);
    }
}