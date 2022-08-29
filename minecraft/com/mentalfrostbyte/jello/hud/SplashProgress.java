package com.mentalfrostbyte.jello.hud;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.util.FontUtil;
import com.mentalfrostbyte.jello.util.RenderingUtil;

import gnu.trove.map.TMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class SplashProgress {
	private static final int MAX = 10;
	private static int PROGRESS = 0;
	private static String CURRENT = "";
	private static ResourceLocation splash;
	private static FontUtil fUtil;
	
	public static void update() {
		if(Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getLanguageManager() == null)
			return;
		
		drawSpash(Minecraft.getMinecraft().getTextureManager());
	}
	
	public static void setProgress(int givenProgress, String givenText) {
		PROGRESS = givenProgress;
		CURRENT = givenText;
		update();
	}
	
	public static void drawSpash(TextureManager em) {
		
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		int scaleFactor = scaledResolution.getScaleFactor();
		Framebuffer framebuffer = new Framebuffer(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor, true);
		framebuffer.bindFramebuffer(false);
		
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0d, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0d, 1000.0d, 3000.0d);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0f, 0.0f, -2000f);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();
		if(splash == null) {
			splash = new ResourceLocation("Jello/blursplash.jpeg");
		}
		em.bindTexture(splash);
		GlStateManager.func_179117_G();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 1920, 1080);
		drawProgress();
		framebuffer.unbindFramebuffer();
		framebuffer.framebufferRender(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);
		
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1f);
		Minecraft.getMinecraft().func_175601_h();
		
	}
	
	public static void drawProgress() {
		if(Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null)
			return;
		
		if(fUtil == null) {
			
		}
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		
		double nProgress = (double)PROGRESS;
		double calc = (nProgress / MAX) * sr.getScaledWidth();
		
		Gui.drawRect(0, sr.getScaledHeight() - 35, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0,0,0,50).getRGB());
		GlStateManager.func_179117_G();
		resetTextureState();
		
		fUtil.san.drawString(CURRENT, 20, sr.getScaledHeight() - 25, 0xffffffff);
		
		String s = PROGRESS + "/" + MAX;
		fUtil.san.drawString(s, sr.getScaledWidth() - 20 - fUtil.san.getStringWidth(s), sr.getScaledHeight() - 25, 0xe1e1e1ff);
		GlStateManager.func_179117_G();
		resetTextureState();
		Gui.drawRect(0, sr.getScaledHeight() - 2, (int)calc, sr.getScaledHeight(), new Color(149, 201, 144).getRGB());
	//	RenderingUtil.color(new Color(149, 201, 144).getRGB());
		//RenderingUtil.drawRoundedRectz(0, sr.getScaledHeight() - 200, (int)calc, 7, 2);
		Gui.drawRect(0, sr.getScaledHeight() - 2, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0,0,0,10).getRGB());
		
	}
	
	public static void resetTextureState() {
		GlStateManager.field_179174_p[GlStateManager.field_179162_o].field_179059_b = -1;
	}
	
	
}
