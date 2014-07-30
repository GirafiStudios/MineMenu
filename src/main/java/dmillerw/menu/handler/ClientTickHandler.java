package dmillerw.menu.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.gui.CompatibleScaledResolution;
import dmillerw.menu.gui.GuiRadialMenu;
import dmillerw.menu.helper.AngleHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import dmillerw.menu.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class ClientTickHandler {

    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;

    public static final int ITEM_RENDER_ANGLE_OFFSET = -2;

    private static final double OUTER_RADIUS = 80;
    private static final double INNER_RADIUS = 60;

    private static final float Z_LEVEL = 0.05F;

    public static void register() {
        ClientTickHandler clientTickHandler = new ClientTickHandler();
        FMLCommonHandler.instance().bus().register(clientTickHandler);
        MinecraftForge.EVENT_BUS.register(clientTickHandler);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            double zLevel = 0.05D;

            if (mc.theWorld != null && !mc.gameSettings.hideGUI && !mc.isGamePaused()) {
                if (GuiRadialMenu.active) {
                    CompatibleScaledResolution resolution = new CompatibleScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    renderGui(resolution, zLevel);
                    renderItems(resolution, zLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && GuiRadialMenu.active) {
            event.setCanceled(true);
        }

        if (!(event instanceof RenderGameOverlayEvent.Post) || event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && !mc.gameSettings.hideGUI && !mc.isGamePaused() && GuiRadialMenu.active) {
            renderText(event.resolution, Z_LEVEL);
        }
    }

    private void renderGui(CompatibleScaledResolution resolution, double zLevel) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        Tessellator tessellator = Tessellator.instance;

        double mouseAngle = AngleHelper.getMouseAngle();
        mouseAngle -= 270; // I DON'T KNOW WHERE THIS 270 EVEN COMES FROM!!! :(
        mouseAngle = AngleHelper.correctAngle(mouseAngle);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            boolean disabled = item != null && !ActionSessionData.availableActions.contains(item.clickAction.getClickAction());
            double currAngle = ANGLE_PER_ITEM * i;
            double nextAngle = currAngle + ANGLE_PER_ITEM;
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

            currAngle = Math.toRadians(currAngle);
            nextAngle = Math.toRadians(nextAngle);

            double innerRadius = ((INNER_RADIUS - (mouseIn ? 2 : 0)) / 100F) * (257F / (float) resolution.getScaledHeight());
            double outerRadius = ((OUTER_RADIUS + (mouseIn ? 2 : 0)) / 100F) * (257F / (float) resolution.getScaledHeight());

            tessellator.startDrawingQuads();

            if (mouseIn) {
                if (disabled) {
                    tessellator.setColorRGBA_F((float) 200 / (float) 255, (float) 200 / (float) 255, (float) 200 / (float) 255, (float) ClientProxy.selectAlpha / (float) 255);
                } else {
                    tessellator.setColorRGBA_F((float) ClientProxy.selectRed / (float) 255, (float) ClientProxy.selectGreen / (float) 255, (float) ClientProxy.selectBlue / (float) 255, (float) ClientProxy.selectAlpha / (float) 255);
                }
            } else {
                tessellator.setColorRGBA_F((float) ClientProxy.menuRed / (float) 255, (float) ClientProxy.menuGreen / (float) 255, (float) ClientProxy.menuBlue / (float) 255, (float) ClientProxy.menuAlpha / (float) 255);
            }

            tessellator.addVertex(Math.cos(currAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * innerRadius, Math.sin(currAngle) * innerRadius, 0);
            tessellator.addVertex(Math.cos(currAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * outerRadius, Math.sin(currAngle) * outerRadius, 0);
            tessellator.addVertex(Math.cos(nextAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * outerRadius, Math.sin(nextAngle) * outerRadius, 0);
            tessellator.addVertex(Math.cos(nextAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * innerRadius, Math.sin(nextAngle) * innerRadius, 0);

            tessellator.draw();
        }

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }

    private void renderItems(CompatibleScaledResolution resolution, double zLevel) {
        GL11.glPushMatrix();

        GL11.glTranslated(resolution.getScaledWidth_double() / 2, resolution.getScaledHeight_double() / 2, 0);

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            ItemStack stack = (item != null && item.icon != null) ? item.icon : new ItemStack(Blocks.stone);

            switch (stack.getItemSpriteNumber()) {
                case 1:
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                    break;
                case 0:
                default:
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                    break;
            }

            double angle = (ANGLE_PER_ITEM * i + (ANGLE_PER_ITEM * ITEM_RENDER_ANGLE_OFFSET)) - ANGLE_PER_ITEM / 2;
            double drawOffset = 1.5; //TODO Make constant
            double drawX = INNER_RADIUS + drawOffset;
            double drawY = INNER_RADIUS + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = (length * Math.cos(StrictMath.toRadians(angle)));
            drawY = (length * Math.sin(StrictMath.toRadians(angle)));

            ItemRenderHelper.renderItem((float) drawX, (float) drawY, 0.05F, stack);
        }

        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
    }

    private void renderText(ScaledResolution resolution, double zLevel) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;
        double mouseAngle = AngleHelper.getMouseAngle();
        mouseAngle -= ClientTickHandler.ANGLE_PER_ITEM / 2;
        mouseAngle = 360 - mouseAngle;
        mouseAngle = AngleHelper.correctAngle(mouseAngle);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = ClientTickHandler.ANGLE_PER_ITEM * i;
            double nextAngle = currAngle + ClientTickHandler.ANGLE_PER_ITEM;
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

            if (mouseIn) {
                MenuItem item = RadialMenu.getActiveArray()[i];
                String string = item == null ? "Add Item" : item.title;
                if (GuiRadialMenu.isShiftKeyDown() && item != null) {
                    string = EnumChatFormatting.RED + "EDIT: " + EnumChatFormatting.WHITE + string;
                }

                int drawX = resolution.getScaledWidth() / 2 - fontRenderer.getStringWidth(string) / 2;
                int drawY = resolution.getScaledHeight() / 2;

                int drawWidth = mc.fontRenderer.getStringWidth(string);
                int drawHeight = mc.fontRenderer.FONT_HEIGHT;

                float padding = 5F;

                // Background
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();

                tessellator.setColorRGBA_F((float) ClientProxy.menuRed / (float) 255, (float) ClientProxy.menuGreen / (float) 255, (float) ClientProxy.menuBlue / (float) 255, (float) ClientProxy.menuAlpha / (float) 255);

                tessellator.addVertex(drawX - padding,             drawY + drawHeight + padding, 0);
                tessellator.addVertex(drawX + drawWidth + padding, drawY + drawHeight + padding, 0);
                tessellator.addVertex(drawX + drawWidth + padding, drawY - padding, 0);
                tessellator.addVertex(drawX - padding,             drawY - padding, 0);

                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);

                // Text
                fontRenderer.drawStringWithShadow(string, drawX, drawY, 0xFFFFFF);
            }
        }
    }
}