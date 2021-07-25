package dmillerw.menu.handler;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dmillerw.menu.MineMenu;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.RadialMenuScreen;
import dmillerw.menu.helper.AngleHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(modid = MineMenu.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final double OUTER_RADIUS = 80;
    private static final double INNER_RADIUS = 60;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RadialMenu.tickTimer();

            Minecraft mc = Minecraft.getInstance();
            if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
                RadialMenuScreen.deactivate();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level != null && !mc.options.hideGui && !mc.isPaused()) {
                if (RadialMenuScreen.active) {
                    renderGui();
                    renderItems();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.LAYER && RadialMenuScreen.active) {
            event.setCanceled(true);
        }

        if (!(event instanceof RenderGameOverlayEvent.Post) || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            renderText(event.getMatrixStack());
        }
    }

    private static void renderGui() {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();

        RenderSystem.disableTexture();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        //RenderSystem.matrixMode(GL11.GL_MODELVIEW); //TODO
        poseStack.pushPose();
        poseStack.setIdentity();

        //RenderSystem.matrixMode(GL11.GL_PROJECTION); //TODO
        poseStack.pushPose();
        poseStack.setIdentity();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        double mouseAngle = AngleHelper.getMouseAngle();
        mouseAngle-=(ANGLE_PER_ITEM/2);
        mouseAngle = AngleHelper.correctAngle(mouseAngle);
        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = (ANGLE_PER_ITEM * i)+90+(ANGLE_PER_ITEM/2);
            double nextAngle = (currAngle + ANGLE_PER_ITEM);
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);
            double truecurrAngle = (ANGLE_PER_ITEM * i);
            double truenextAngle = (truecurrAngle + ANGLE_PER_ITEM);
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = (mouseAngle > truecurrAngle && mouseAngle < truenextAngle);

            currAngle = Math.toRadians(currAngle);
            nextAngle = Math.toRadians(nextAngle);

            double innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (257F / (float) mc.getWindow().getGuiScaledHeight());
            double outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (257F / (float) mc.getWindow().getGuiScaledHeight());

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

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

            bufferBuilder.vertex(Math.cos(currAngle) * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getGuiScaledWidth() * innerRadius, Math.sin(currAngle) * innerRadius, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.vertex(Math.cos(currAngle) * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getGuiScaledWidth() * outerRadius, Math.sin(currAngle) * outerRadius, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.vertex(Math.cos(nextAngle) * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getGuiScaledWidth() * outerRadius, Math.sin(nextAngle) * outerRadius, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.vertex(Math.cos(nextAngle) * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getGuiScaledWidth() * innerRadius, Math.sin(nextAngle) * innerRadius, 0).color(r, g, b, alpha).endVertex();

            tessellator.end();
        }

        poseStack.popPose();
        //RenderSystem.matrixMode(GL11.GL_MODELVIEW); //TODO
        poseStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();

        poseStack.popPose();
    }

    private static void renderItems() {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(mc.getWindow().getGuiScaledWidth() * 0.5D, mc.getWindow().getGuiScaledHeight() * 0.5D, 0);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ConfigHandler.GENERAL.menuButtonIcon.get().toString()));
            ItemStack stack = (item != null && !item.icon.isEmpty()) ? item.icon : (menuButton == null ? ItemStack.EMPTY : new ItemStack(menuButton));

            double angle = (ANGLE_PER_ITEM * i) - (90);
            double drawOffset = 1.5;
            double drawX = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;
            double drawY = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = (length * Math.cos(StrictMath.toRadians(angle)));
            drawY = (length * Math.sin(StrictMath.toRadians(angle)));

            ItemRenderHelper.renderItem((int) drawX, (int) drawY, stack);
        }

        poseStack.popPose();
    }

    private static void renderText(PoseStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        Font fontRenderer = mc.font;
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
                if (RadialMenuScreen.hasShiftDown() && item != null) {
                    string = ChatFormatting.RED + "EDIT: " + ChatFormatting.WHITE + string;
                }

                int drawX = window.getGuiScaledWidth() / 2 - fontRenderer.width(string) / 2;
                int drawY = window.getGuiScaledHeight() / 2;

                int drawWidth = mc.font.width(string);
                int drawHeight = mc.font.lineHeight;

                float padding = 5F;

                // Background
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                RenderSystem.disableTexture();
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuilder();
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                float r = (float) ConfigHandler.VISUAL.menuRed.get() / (float) 255;
                float g = (float) ConfigHandler.VISUAL.menuGreen.get() / (float) 255;
                float b = (float) ConfigHandler.VISUAL.menuBlue.get() / (float) 255;
                float alpha = (float) ConfigHandler.VISUAL.menuAlpha.get() / (float) 255;

                bufferBuilder.vertex(drawX - padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(drawX + drawWidth + padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(drawX + drawWidth + padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(drawX - padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();

                tessellator.end();
                RenderSystem.enableTexture();
                RenderSystem.disableBlend();

                // Text
                fontRenderer.drawShadow(matrixStack, string, drawX, drawY, 0xFFFFFF, false);
            }
        }
    }
}