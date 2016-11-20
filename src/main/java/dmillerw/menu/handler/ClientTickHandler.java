package dmillerw.menu.handler;

import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.gui.CompatibleScaledResolution;
import dmillerw.menu.gui.GuiRadialMenu;
import dmillerw.menu.helper.AngleHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class ClientTickHandler {

    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;

    private static final int ITEM_RENDER_ANGLE_OFFSET = -2;

    private static final double OUTER_RADIUS = 80;
    private static final double INNER_RADIUS = 60;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RadialMenu.tickTimer();

            Minecraft mc = Minecraft.getMinecraft();
            if ((mc.world == null || mc.isGamePaused()) && GuiRadialMenu.active) {
                GuiRadialMenu.deactivate();
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.world != null && !mc.gameSettings.hideGUI && !mc.isGamePaused()) {
                if (GuiRadialMenu.active) {
                    CompatibleScaledResolution resolution = new CompatibleScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    renderGui(resolution);
                    renderItems(resolution);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && GuiRadialMenu.active) {
            event.setCanceled(true);
        }

        if (!(event instanceof RenderGameOverlayEvent.Post) || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null && !mc.gameSettings.hideGUI && !mc.isGamePaused() && GuiRadialMenu.active) {
            renderText(event.getResolution());
        }
    }

    private void renderGui(CompatibleScaledResolution resolution) {
        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

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

            double innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (257F / (float) resolution.getScaledHeight());
            double outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (257F / (float) resolution.getScaledHeight());

            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);

            float r, g, b, alpha;

            if (mouseIn) {
                if (disabled) {
                    r = (float) 200 / (float) 255;
                    g = (float) 200 / (float) 255;
                    b = (float) 200 / (float) 255;
                    alpha = (float) ConfigHandler.selectAlpha / (float) 255;
                } else {
                    r = (float) ConfigHandler.selectRed / (float) 255;
                    g = (float) ConfigHandler.selectGreen / (float) 255;
                    b = (float) ConfigHandler.selectBlue / (float) 255;
                    alpha = (float) ConfigHandler.selectAlpha / (float) 255;
                }
            } else {
                r = (float) ConfigHandler.menuRed / (float) 255;
                g = (float) ConfigHandler.menuGreen / (float) 255;
                b = (float) ConfigHandler.menuBlue / (float) 255;
                alpha = (float) ConfigHandler.menuAlpha / (float) 255;
            }

            vertexbuffer.pos(Math.cos(currAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * innerRadius, Math.sin(currAngle) * innerRadius, 0).color(r, g, b, alpha).endVertex();
            vertexbuffer.pos(Math.cos(currAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * outerRadius, Math.sin(currAngle) * outerRadius, 0).color(r, g, b, alpha).endVertex();
            vertexbuffer.pos(Math.cos(nextAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * outerRadius, Math.sin(nextAngle) * outerRadius, 0).color(r, g, b, alpha).endVertex();
            vertexbuffer.pos(Math.cos(nextAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * innerRadius, Math.sin(nextAngle) * innerRadius, 0).color(r, g, b, alpha).endVertex();

            tessellator.draw();
        }

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }

    private void renderItems(CompatibleScaledResolution resolution) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(resolution.getScaledWidth_double() / 2, resolution.getScaledHeight_double() / 2, 0);
        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            ItemStack stack = (item != null && !item.icon.isEmpty()) ? item.icon : ( ConfigHandler.removeStoneOnMenuButton ? ItemStack.EMPTY : new ItemStack(Blocks.STONE));

            double angle = (ANGLE_PER_ITEM * i + (ANGLE_PER_ITEM * ITEM_RENDER_ANGLE_OFFSET)) - ANGLE_PER_ITEM / 2;
            double drawOffset = 1.5; //TODO Make constant
            double drawX = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;
            double drawY = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = (length * Math.cos(StrictMath.toRadians(angle)));
            drawY = (length * Math.sin(StrictMath.toRadians(angle)));

            ItemRenderHelper.renderItem((float) drawX, (float) drawY, stack);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void renderText(ScaledResolution resolution) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;
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
                    string = TextFormatting.RED + "EDIT: " + TextFormatting.WHITE + string;
                }

                int drawX = resolution.getScaledWidth() / 2 - fontRenderer.getStringWidth(string) / 2;
                int drawY = resolution.getScaledHeight() / 2;

                int drawWidth = mc.fontRendererObj.getStringWidth(string);
                int drawHeight = mc.fontRendererObj.FONT_HEIGHT;

                float padding = 5F;

                // Background
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GlStateManager.disableTexture2D();
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();
                vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);

                float r = (float) ConfigHandler.menuRed / (float) 255;
                float g = (float) ConfigHandler.menuGreen / (float) 255;
                float b = (float) ConfigHandler.menuBlue / (float) 255;
                float alpha = (float) ConfigHandler.menuAlpha / (float) 255;

                vertexbuffer.pos(drawX - padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                vertexbuffer.pos(drawX + drawWidth + padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                vertexbuffer.pos(drawX + drawWidth + padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();
                vertexbuffer.pos(drawX - padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();

                tessellator.draw();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();

                // Text
                fontRenderer.drawStringWithShadow(string, drawX, drawY, 0xFFFFFF);
            }
        }
    }
}