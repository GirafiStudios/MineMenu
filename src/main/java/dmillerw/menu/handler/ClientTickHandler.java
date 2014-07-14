package dmillerw.menu.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.menu.data.MenuItem;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.helper.RenderHelper;
import dmillerw.menu.math.Point2D;
import javafx.scene.paint.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class ClientTickHandler {

	public static final ClientTickHandler INSTANCE = new ClientTickHandler();

	public static void register() {
		FMLCommonHandler.instance().bus().register(ClientTickHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientTickHandler.INSTANCE);
	}

	public static Color highlight = Color.GREEN.brighter();

	public double radialDeltaX;
	public double radialDeltaY;

	private boolean lastLMB;
	private boolean lastRMB;

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().theWorld != null) {
			Minecraft mc = Minecraft.getMinecraft();

			if (mc.currentScreen == null && MouseHandler.showMenu) {
				lastLMB = Mouse.isButtonDown(0);
				lastRMB = Mouse.isButtonDown(1);
			} else {
				MouseHandler.showMenu = false;
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if (mc.theWorld != null) {
			if (MouseHandler.showMenu) {
				ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

				int mx = Mouse.getX();
				int my = Mouse.getY();
				int roundness = RadialMenu.MAX_ITEMS;

				double mouse = Math.atan2(my - mc.displayHeight / 2, mx - mc.displayWidth / 2);
				mouse = Math.toDegrees(mouse);
				if (mouse < 0) {
					mouse += 360;
				}

				float rad1 = (0.52F) * 1 * (257F / (float) resolution.getScaledHeight());
				float rad2 = (0.82F) * 1 * (257F / (float) resolution.getScaledHeight());

				if (!mc.gameSettings.hideGUI) {
					GL11.glDisable(GL11.GL_TEXTURE_2D);

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();

					GL11.glMatrixMode(GL11.GL_PROJECTION);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();

					double zLev = 0.05D;

					Tessellator tessellator = Tessellator.instance;

					tessellator.setColorRGBA_F(0, 0, 0, 0.05F);

					for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
						tessellator.startDrawingQuads();

						int vertexPer = roundness / RadialMenu.MAX_ITEMS;
						double anglePer = 360F / RadialMenu.MAX_ITEMS;
						boolean mouseIn = mouse > anglePer * i && mouse < anglePer * (i + 1);

						for (int j = vertexPer * i; j <= vertexPer * (i + 1); j++) {
							double drawAngle = Math.toRadians(Math.toDegrees(Math.PI * 2 * j / roundness) + 0.5);
							double nextAngle = Math.toRadians(Math.toDegrees(Math.PI * 2 * (j + 1) / roundness) - 0.5);
//							GL11.glColor4f(0, 0, mouseIn ? 1 : 0, 0.4F);
//							GL11.glVertex3d(Math.cos(drawAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * rad, Math.sin(drawAngle) * rad, zLev);

							tessellator.setColorRGBA_F(0, 0, mouseIn ? 1 : 0, 0.25F);

							tessellator.addVertex(Math.cos(drawAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * rad1, Math.sin(drawAngle) * rad1, zLev);
							tessellator.addVertex(Math.cos(drawAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * rad2, Math.sin(drawAngle) * rad2, zLev);
							tessellator.addVertex(Math.cos(nextAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * rad2, Math.sin(nextAngle) * rad2, zLev);
							tessellator.addVertex(Math.cos(nextAngle) * resolution.getScaledHeight_double() / resolution.getScaledWidth_double() * rad1, Math.sin(nextAngle) * rad1, zLev);
						}

						tessellator.draw();
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);


//					for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
//						double anglePer = 360F / RadialMenu.MAX_ITEMS;
//						boolean mouseIn = mouse > anglePer * i && mouse < anglePer * (i + 1);
//
//						if (mouseIn) {
//							FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
//							MenuItem item = RadialMenu.menuItems[i];
//							String string = item != null ? item.title : "Add";
//
//							GL11.glEnable(GL11.GL_TEXTURE_2D);
//							fontRenderer.drawString(string, (resolution.getScaledWidth() / 2) - fontRenderer.getStringWidth(string), resolution.getScaledHeight() / 2, 0xFFFFFF);
//							GL11.glDisable(GL11.GL_TEXTURE_2D);
//						}
//					}

					GL11.glPopMatrix();
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glPopMatrix();

					GL11.glDisable(GL11.GL_BLEND);

					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				// ITEM RENDER
				GL11.glPushMatrix();

				net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

				float outDistance = 80F;
				float anglePer = 360F / (float) RadialMenu.MAX_ITEMS;

				mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

				for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
					// ITEM RENDER
					Point2D renderPoint = new Point2D(0, outDistance);
					renderPoint.setTheta((anglePer * i) - anglePer * 2.5F);
					renderPoint.sub(8, 8);

					MenuItem item = RadialMenu.menuItems[i];
					ItemStack stack = item != null ? item.icon : new ItemStack(Blocks.stone);

					float drawX = resolution.getScaledWidth() / 2 + renderPoint.getX();
					float drawY = resolution.getScaledHeight() / 2 + renderPoint.getY();

					RenderHelper.renderItem(drawX, drawY, 0.05F, stack);
				}

				net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

				GL11.glPopMatrix();
			}
		}
	}
}