package dmillerw.menu.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public abstract class Packet <REQ extends Packet<REQ>> implements IMessage, IMessageHandler<REQ, REQ> {

    @Override
    public REQ onMessage(REQ message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            runServer(message, ctx.getServerHandler().playerEntity);
        } else {
            runClient(message, getPlayerClient());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void runClient(final REQ packet, final EntityPlayer player) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                packet.handleClientSide(player);
            }
        });
    }

    private void runServer(final REQ packet, final EntityPlayer player) {
        player.getServer().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                packet.handleServerSide(player);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    protected EntityPlayer getPlayerClient() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            fromBytes(new PacketBuffer(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        toBytes(new PacketBuffer(buf));
    }

    @SideOnly(Side.CLIENT)
    protected abstract void handleClientSide(EntityPlayer player);

    protected abstract void handleServerSide(EntityPlayer player);

    protected abstract void toBytes(PacketBuffer buffer);

    protected abstract void fromBytes(PacketBuffer buffer) throws IOException;
}