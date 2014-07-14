package dmillerw.menu.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.menu.data.MenuItem;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.helper.AngleHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;
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
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// Cancel out all mouse interaction (except our own) if the menu is open
		Minecraft mc = Minecraft.getMinecraft();

		if (MouseHandler.showMenu) {
			Mouse.getDX();
			Mouse.getDY();
			mc.mouseHelper.deltaX = mc.mouseHelper.deltaY = 0;
		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Minecraft mc = Minecraft.getMinecraft();
			double zLevel = 0.05D;

			if (mc.theWorld != null) {
				if (MouseHandler.showMenu) {
					ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
					renderGui(resolution, zLevel);
					renderItems(resolution, zLevel);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.theWorld != null) {
			if (MouseHandler.showMenu) {
				if (!mc.gameSettings.hideGUI) {
					renderText(event.resolution, Z_LEVEL);
				}
			}
		}
	}

	private void renderGui(ScaledResolution resolution, double zLevel) {
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
			double currAngle = ANGLE_PER_ITEM * i;
			double nextAngle = currAngle + ANGLE_PER_ITEM;
			currAngle = AngleHelper.correctAngle(currAngle);
			nextAngle = AngleHelper.correctAngle(nextAngle);

			boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

			currAngle = Math.toRadians(currAngle);
			nextAngle = Math.toRadians(nextAngle);

			double innerRadius = ((INNER_RADIUS - (mouseIn ? 2 : 0)) / 100F) * (257F / (float)resolution.getScaledHeight());
			double outerRadius = ((OUTER_RADIUS + (mouseIn ? 2 : 0)) / 100F) * (257F / (float)resolution.getScaledHeight());

			tessellator.startDrawingQuads();

			if (mouseIn) {
				tessellator.setColorRGBA_F((float) ConfigHandler.selectRed / (float) 255, (float) ConfigHandler.selectGreen / (float) 255, (float) ConfigHandler.selectBlue / (float) 255, (float) ConfigHandler.alpha / (float) 255);
			} else {
				tessellator.setColorRGBA_F((float) ConfigHandler.menuRed / (float) 255, (float) ConfigHandler.menuGreen / (float) 255, (float) ConfigHandler.menuBlue / (float) 255, (float) ConfigHandler.alpha / (float) 255);
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

	private void renderItems(ScaledResolution resolution, double zLevel) {
		GL11.glPushMatrix();

		GL11.glTranslated(resolution.getScaledWidth_double() / 2, resolution.getScaledHeight_double() / 2, 0);

		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
			MenuItem item = RadialMenu.menuItems[i];
			ItemStack stack = item != null ? item.icon : new ItemStack(Blocks.stone);

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

	}
}