package dmillerw.menu.handler;

import dmillerw.menu.MineMenu;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = MineMenu.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MineMenuKeyMappings {
    public static final KeyMapping WHEEL = new KeyMapping("key.open_menu", GLFW.GLFW_KEY_R, "key.categories.misc");

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(WHEEL);
    }
}