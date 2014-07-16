package dmillerw.menu.network.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author dmillerw
 */
public class PacketUseItem implements IMessage, IMessageHandler<PacketUseItem, IMessage> {

	public int slot;

	public PacketUseItem() {

	}

	public PacketUseItem(int slot) {
		this.slot = slot;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
	}

	@Override
	public IMessage onMessage(PacketUseItem message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		player.theItemInWorldManager.tryUseItem(player, player.worldObj, player.inventory.getStackInSlot(message.slot));
		return null;
	}
}
