package dmillerw.menu.handler;

import dmillerw.menu.MineMenu;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.GuiRadialMenu;
import dmillerw.menu.helper.AngleHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(modid = MineMenu.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final int ITEM_RENDER_ANGLE_OFFSET = -2;
    private static final double OUTER_RADIUS = 80;
    private static final double INNER_RADIUS = 60;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RadialMenu.tickTimer();

            Minecraft mc = Minecraft.getInstance();
            if ((mc.world == null || mc.isGamePaused()) && GuiRadialMenu.active) {
                GuiRadialMenu.deactivate();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.world != null && !mc.gameSettings.hideGUI && !mc.isGamePaused()) {
                if (GuiRadialMenu.active) {
                    renderGui();
                    renderItems();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && GuiRadialMenu.active) {
            event.setCanceled(true);
        }

        if (!(event instanceof RenderGameOverlayEvent.Post) || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.world != null && !mc.gameSettings.hideGUI && !mc.isGamePaused() && GuiRadialMenu.active) {
            renderText();
        }
    }

    private static void renderGui() {
        Minecraft mc = Minecraft.getInstance();
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
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        double mouseAngle = AngleHelper.getMouseAngle();
        mouseAngle -= 270;
        mouseAngle = AngleHelper.correctAngle(mouseAngle);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = ANGLE_PER_ITEM * i;
            double nextAngle = currAngle + ANGLE_PER_ITEM;
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

            currAngle = Math.toRadians(currAngle);
            nextAngle = Math.toRadians(nextAngle);

            double innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (257F / (float) mc.mainWindow.getScaledHeight());
            double outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (257F / (float) mc.mainWindow.getScaledHeight());

            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

            float r, g, b, alpha;

            if (mouseIn) {
                r = (float) ConfigHandler.VISUAL.selectRed.get() / (float) 255;
                g = (float) ConfigHandler.VISUAL.selectGreen.get() / (float) 255;
                b = (float) ConfigHandler.VISUAL.selectBlue.get() / (float) 255;
                alpha = (float) ConfigHandler.VISUAL.selectAlpha.get() / (float) 255;
            } else {
                r = (float) ConfigHandler.VISUAL.menuRed.get() / (float) 255;
                g = (float) ConfigHandler.VISUAL.menuGreen.get() / (float) 255;
                b = (float) ConfigHandler.VISUAL.menuBlue.get() / (float) 255;
                alpha = (float) ConfigHandler.VISUAL.menuAlpha.get() / (float) 255;
            }

            bufferBuilder.pos(Math.cos(currAngle) * mc.mainWindow.getScaledHeight() / mc.mainWindow.getScaledWidth() * innerRadius, Math.sin(currAngle) * innerRadius, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.pos(Math.cos(currAngle) * mc.mainWindow.getScaledHeight() / mc.mainWindow.getScaledWidth() * outerRadius, Math.sin(currAngle) * outerRadius, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.pos(Math.cos(nextAngle) * mc.mainWindow.getScaledHeight() / mc.mainWindow.getScaledWidth() * outerRadius, Math.sin(nextAngle) * outerRadius, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.pos(Math.cos(nextAngle) * mc.mainWindow.getScaledHeight() / mc.mainWindow.getScaledWidth() * innerRadius, Math.sin(nextAngle) * innerRadius, 0).color(r, g, b, alpha).endVertex();

            tessellator.draw();
        }

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }

    private static void renderItems() {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.pushMatrix();
        GlStateManager.translated(mc.mainWindow.getScaledWidth() * 0.5D, mc.mainWindow.getScaledHeight() * 0.5D, 0);
        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ConfigHandler.GENERAL.menuButtonIcon.get().toString()));
            ItemStack stack = (item != null && !item.icon.isEmpty()) ? item.icon : (menuButton == null ? ItemStack.EMPTY : new ItemStack(menuButton));

            double angle = (ANGLE_PER_ITEM * i + (ANGLE_PER_ITEM * ITEM_RENDER_ANGLE_OFFSET)) - ANGLE_PER_ITEM / 2;
            double drawOffset = 1.5;
            double drawX = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;
            double drawY = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = (length * Math.cos(StrictMath.toRadians(angle)));
            drawY = (length * Math.sin(StrictMath.toRadians(angle)));

            ItemRenderHelper.renderItem((int) drawX, (int) drawY, stack);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private static void renderText() {
        Minecraft mc = Minecraft.getInstance();
        MainWindow window = mc.mainWindow;
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
                    string = TextFormatting.RED + "EDIT: " + TextFormatting.WHITE + string;
                }

                int drawX = window.getScaledWidth() / 2 - fontRenderer.getStringWidth(string) / 2;
                int drawY = window.getScaledHeight() / 2;

                int drawWidth = mc.fontRenderer.getStringWidth(string);
                int drawHeight = mc.fontRenderer.FONT_HEIGHT;

                float padding = 5F;

                // Background
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GlStateManager.disableTexture2D();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

                float r = (float) ConfigHandler.VISUAL.menuRed.get() / (float) 255;
                float g = (float) ConfigHandler.VISUAL.menuGreen.get() / (float) 255;
                float b = (float) ConfigHandler.VISUAL.menuBlue.get() / (float) 255;
                float alpha = (float) ConfigHandler.VISUAL.menuAlpha.get() / (float) 255;

                bufferBuilder.pos(drawX - padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.pos(drawX + drawWidth + padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.pos(drawX + drawWidth + padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.pos(drawX - padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();

                tessellator.draw();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();

                // Text
                fontRenderer.drawStringWithShadow(string, drawX, drawY, 0xFFFFFF);
            }
        }
    }
}