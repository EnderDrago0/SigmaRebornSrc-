package com.mentalfrostbyte.jello.modules;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.event.events.EventRenderNameTag;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ColorUtil;
import com.mentalfrostbyte.jello.util.EntityUtils;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.RenderingUtil;
import com.mentalfrostbyte.jello.util.Stencil;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ESP extends Module {
	//public ModeValue mode;
	public static ModeValue mode = new ModeValue("Mode", "Fill", "Fill", "Box", "Outline", "2D", "CSGO");
	public static ModeValue colormode = new ModeValue("ColorMode", "Health", "Health", "Team", "Custom", "Rainbow", "SigmaTest");
	private BooleanValue armor = new BooleanValue("Armor", true);
	public ESP() {
        super("ESP", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(4);
        this.addValue(mode, colormode, armor);
    }
   
	public void onEnable(){
		EventManager.register(this);
	//	en = true;
	}
	
	public void onDisable(){
		EventManager.unregister(this);
		//  en = false;
	}
	
    
	public void onUpdate()
	{
		
	}
    public static float h = 0;
    private double gradualFOVModifier;
    public static Map<EntityLivingBase, double[]> entityPositionstop = new HashMap();
    public static Map<EntityLivingBase, double[]> entityPositionsbottom = new HashMap();
	
	
	
	@EventTarget
	public void rende(EventRender3D e) {
		if (h > 255) {
			h = 0;
		}

		h+= 0.1;
		int renderColor = 0;
		int list = GL11.glGenLists(1);
		
		if(mode.is("Outline")){
			
			checkSetupFBO();
            
            Stencil.getInstance().startLayer();
            GL11.glPushMatrix();
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            Stencil.getInstance().setBuffer(true);
            GL11.glNewList(list, GL11.GL_COMPILE);
            GlStateManager.enableLighting();
            
		}else{
		GL11.glPushMatrix();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
		}
	
		for(Object obj : mc.theWorld.loadedEntityList){
			Entity entity = (Entity)obj;
			 if (isValid(entity)) {

				mc.entityRenderer.func_175072_h();
				 switch(colormode.getMode()){
                    case"Rainbow":{
                    	final Color color = Color.getHSBColor(h / 255.0f, 0.6f, 1.0f);
            			final int c = color.getRGB();
                    	renderColor = c;
                    }
                    break;
                    case"Team":{
                    String text = entity.getDisplayName().getFormattedText();
                	if(Character.toLowerCase(text.charAt(0)) == '§'){
                		
                    	char oneMore = Character.toLowerCase(text.charAt(1));
                    	int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                    	
                    	if (colorCode < 16) {
                            try {
                                int newColor = mc.fontRendererObj.colorCode[colorCode];   
                                 renderColor = ColorUtil.getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                        }
                	}else{
                		renderColor = ColorUtil.getColor(255, 255, 255, 255);
                	}
                    }
                    break;
                    case"Health":{
                    	float health = ((EntityLivingBase)entity).getHealth();
                    

                        if (health > 20) {
                            health = 20;
                        }
                        float[] fractions = new float[]{0f, 0.5f, 1f};
                        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                        float progress = (health * 5) * 0.01f;
                        Color customColor = blendColors(fractions, colors, progress).brighter();
                        renderColor = customColor.getRGB();
                    }
                    break;
                    case"SigmaTest":{
	                    	final Color color = Color.getHSBColor(255.0f,255.0f, 1.0f);
	            			final int c = color.getRGB();
	                    	renderColor = ColorUtil.getColor(255, 255, 255, 255);
                    }
                    break;
                    case"Custom"://03dffc
                    	renderColor = 0xff03dffc;//renderColor = Client.cm.getESPColor().getColorInt();
                    	break;
                    }        
                    if(entity.hurtResistantTime > 15 && colormode.is("Fill")){
                    	renderColor = ColorUtil.getColor(1, 0, 0, 1);
                    }
                    if(AntiBot.bot.contains(entity)){
                    	renderColor = ColorUtil.getColor(100,100,100,255);
                    }
                   // if(FriendManager.isFriend(entity.getName()) && !(entity instanceof EntityPlayerSP)){
                  //  	renderColor = Colors.getColor(0,195,255,255);
                 //   }
                    

            double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
            double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
            double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
            Render entityRender = mc.getRenderManager().getEntityRenderObject(entity);
			switch(mode.getMode()){
			case"Outline":
				RenderingUtil.glColor(renderColor);
				RenderingUtil.pre3D();
                GL11.glLineWidth(3.5f);
              //  GL11.glTranslated(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ);
                if (entityRender != null) {
                    float distance = mc.thePlayer.getDistanceToEntity(entity);
                    if (entity instanceof EntityLivingBase) {
                        GlStateManager.disableLighting();
                        RendererLivingEntity.renderLayers = false;
                        RendererLivingEntity.rendername = false;
                        //ChatUtil.printChat("" + entity);
                        entityRender.doRender(entity, posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ, mc.timer.renderPartialTicks, mc.timer.renderPartialTicks);
                        RendererLivingEntity.renderLayers = true;
                        RendererLivingEntity.rendername = true;
                        GlStateManager.enableLighting();

                    }
                }
                RenderingUtil.post3D();
                
				break;
			case"Box":

        		double x = entity.lastTickPosX
        				+ (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;

        		double y = entity.lastTickPosY
        				+ (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;

        		double z = entity.lastTickPosZ
        				+ (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;   
        		EntityLivingBase base = (EntityLivingBase)entity;
        		double widthX = (entity.boundingBox.maxX - entity.boundingBox.minX) / 2 + 0.05;
        		double widthZ = (entity.boundingBox.maxZ - entity.boundingBox.minZ) / 2 + 0.05;
            	double height = (entity.boundingBox.maxY - entity.boundingBox.minY);
            	if(entity instanceof EntityPlayer)
            		height *= 1.1;
            	RenderingUtil.pre3D();
            	RenderingUtil.glColor(renderColor);
                for(int i = 0; i < 2; i++){
                	if(i == 1)
                	RenderingUtil.glColor(0xff034efc);
                    GL11.glLineWidth(3 - i*2);
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    GL11.glVertex3d(x-widthX, y,z-widthZ);
                    GL11.glVertex3d(x-widthX, y,z-widthZ);
                    GL11.glVertex3d(x-widthX, y + height,z-widthZ);
                    GL11.glVertex3d(x+widthX, y + height,z-widthZ);
                    GL11.glVertex3d(x+widthX, y,z-widthZ);
                    GL11.glVertex3d(x-widthX, y,z-widthZ);
                    GL11.glVertex3d(x-widthX, y,z+widthZ);
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    GL11.glVertex3d(x+widthX, y,z+widthZ);
                    GL11.glVertex3d(x+widthX, y + height,z+widthZ);
                    GL11.glVertex3d(x-widthX, y + height,z+widthZ);
                    GL11.glVertex3d(x-widthX, y,z+widthZ);
                    GL11.glVertex3d(x+widthX, y,z+widthZ);
                    GL11.glVertex3d(x+widthX, y,z-widthZ);
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    GL11.glVertex3d(x+widthX, y + height,z+widthZ);
                    GL11.glVertex3d(x+widthX, y + height,z-widthZ);
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    GL11.glVertex3d(x-widthX, y + height,z+widthZ);
                    GL11.glVertex3d(x-widthX, y + height,z-widthZ);
                    GL11.glEnd();
                }
                
                RenderingUtil.post3D();
				 
				break;
				default:
				break;
				}
			}
		}
		if(mode.is("Outline")){
            GL11.glEndList();
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glCallList(list);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_POINT);
            GL11.glCallList(list);
            Stencil.getInstance().setBuffer(false);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GL11.glCallList(list);
            Stencil.getInstance().cropInside();
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glCallList(list);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_POINT);
            GL11.glCallList(list);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            Stencil.getInstance().stopLayer();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDeleteLists(list, 1);
            GL11.glPopMatrix();
		}else
		RenderingUtil.post3D();
    	if(mode.is("2D")){
    		try {
    			updatePositions();
    		} catch (Exception se) {
    		}        
    	}
    	mc.entityRenderer.func_175072_h();
    	RenderHelper.disableStandardItemLighting(); 
	}
	@EventTarget
	public void r(EventRender2D e) {
		if(mode.is("2D")){
            GlStateManager.pushMatrix();
            ScaledResolution scaledRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);
            GlStateManager.scale(twoDscale, twoDscale, twoDscale);
            for (Entity ent : entityPositionstop.keySet()) {
                double[] renderPositions = entityPositionstop.get(ent);
                double[] renderPositionsBottom = entityPositionsbottom.get(ent);
                if ((renderPositions[3] > 0.0D) || (renderPositions[3] <= 1.0D)) {
                    GlStateManager.pushMatrix();
                    if (isValid(ent)) {
                        scale(ent);
                        try {
                            float y = (float) renderPositions[1];
                            float endy = (float) renderPositionsBottom[1];
                            float meme = endy - y;
                            float x = (float) renderPositions[0] - (meme / 4f);
                            float endx = (float) renderPositionsBottom[0] + (meme / 4f);
                            if (x > endx) {
                                endx = x;
                                x = (float) renderPositionsBottom[0] + (meme / 4f);
                            }
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(2, 2, 2);
                            GlStateManager.popMatrix();
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            int color = 0;
                            switch(colormode.getMode()){
    	                    case"Rainbow":{
    	                    	final Color colorz = Color.getHSBColor(h / 255.0f, 0.6f, 1.0f);
    	            			final int c = colorz.getRGB();
    	                    	color = c;
    	                    }
    	                    break;
    	                    case"Team":{
    	                    String text = ent.getDisplayName().getFormattedText();
    	                	if(Character.toLowerCase(text.charAt(0)) == '§'){
    	                		
    	                    	char oneMore = Character.toLowerCase(text.charAt(1));
    	                    	int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
    	                    	
    	                    	if (colorCode < 16) {
    	                            try {
    	                                int newColor = mc.fontRendererObj.colorCode[colorCode];   
    	                                 color = ColorUtil.getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
    	                            } catch (ArrayIndexOutOfBoundsException ignored) {
    	                            }
    	                        }else{
    	                        }
    	                	}else{
    	                		color = ColorUtil.getColor(255, 255, 255, 255);
    	                	}
    	                    }
    	                    break;
    	                    case"Health":{
    	                    	float health = ((EntityLivingBase)ent).getHealth();
    	                        if (health > 20) {
    	                            health = 20;
    	                        }
    	                        float[] fractions = new float[]{0f, 0.5f, 1f};
    	                        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
    	                        float progress = (health * 5) * 0.01f;
    	                        Color customColor = blendColors(fractions, colors, progress).brighter();
    	                        color = customColor.getRGB();
    	                    }
    	                    break;
    	                    case"Custom":
    	                    	color = 0xff03dffc;//color = Client.cm.getESPColor().getColorInt();
    	                    	break;
    	                    }
    	                    if(AntiBot.bot.contains(ent)){
    	                    	color = ColorUtil.getColor(100,100,100,255);
    	                    }
    	                    
                            RenderingUtil.rectangleBordered(x, y, endx, endy, 2.25, ColorUtil.getColor(0, 0, 0, 0), color);
                            RenderingUtil.rectangleBordered(x - 0.5, y - 0.5, endx + 0.5, endy + 0.5, 0.9, ColorUtil.getColor(0, 0), ColorUtil.getColor(0));
                            RenderingUtil.rectangleBordered(x + 2.5, y + 2.5, endx - 2.5, endy - 2.5, 0.9, ColorUtil.getColor(0, 0), ColorUtil.getColor(0));
                            RenderingUtil.rectangleBordered(x - 5, y - 1, x - 1, endy, 1, ColorUtil.getColor(0, 100), ColorUtil.getColor(0, 255));
                            if(ent instanceof EntityPlayer){
                            	/*
                            	 if (!Client.getModuleManager().get(Nametags.class).isEnabled() && ((Boolean) settings.get(NAME).getValue())) {
                                     GlStateManager.pushMatrix();
                                     String renderName = FriendManager.isFriend(ent.getName()) ? FriendManager.getAlias(ent.getName()) : ent.getName();
                                     TTFFontRenderer font = Client.fm.getFont("Verdana Bold 16");
                                     float meme2 = ((endx - x) / 2 - (font.getWidth(renderName) / 2f));
                                     font.drawStringWithShadow(renderName + " " + (int) mc.thePlayer.getDistanceToEntity(ent) + "m", (x + meme2), (y - font.getHeight(renderName) - 5), FriendManager.isFriend(ent.getName()) ? Colors.getColor(192, 80, 64) : -1);
                                     GlStateManager.popMatrix();
                                 }
                                 if (((EntityPlayer) ent).getCurrentEquippedItem() != null && ((Boolean) settings.get(ITEMS).getValue())) {
                                     GlStateManager.pushMatrix();
                                     GlStateManager.scale(2, 2, 2);
                                     ItemStack stack = ((EntityPlayer) ent).getCurrentEquippedItem();
                                     String customName =((EntityPlayer) ent).getCurrentEquippedItem().getItem().getItemStackDisplayName(stack);
                                     TTFFontRenderer font = Client.fm.getFont("Verdana 12");
                                     float meme2 = ((endx - x) / 2 - (font.getWidth(customName) / 1f));
                                     font.drawStringWithShadow(customName, (x + meme2) / 2f, (endy + font.getHeight(customName) / 2 * 2f) / 2f, -1);
                                     GlStateManager.popMatrix();
                                 }
                                 if ((Boolean) settings.get(ARMOR).getValue()) {
                                     float var1 = (endy - y) / 4;
                                     ItemStack stack = ((EntityPlayer) ent).getEquipmentInSlot(4);
                                     if (stack != null) {
                                         RenderingUtil.rectangleBordered(endx + 1, y + 1, endx + 6, y + var1, 1, ColorUtil.getColor(28, 156, 179, 100), ColorUtil.getColor(0, 255));
                                         float diff1 = (y + var1 - 1) - (y + 2);
                                         double percent = 1 - (double) stack.getItemDamage() / (double) stack.getMaxDamage();
                                         RenderingUtil.rectangle(endx + 2, y + var1 - 1, endx + 5, y + var1 - 1 - (diff1 * percent), ColorUtil.getColor(78, 206, 229));
                                         mc.fontRendererObj.drawStringWithShadow(stack.getMaxDamage() - stack.getItemDamage() + "", endx + 7, (y + var1 - 1 - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
                                     }
                                     ItemStack stack2 = ((EntityPlayer) ent).getEquipmentInSlot(3);
                                     if (stack2 != null) {
                                         RenderingUtil.rectangleBordered(endx + 1, y + var1, endx + 6, y + var1 * 2, 1, ColorUtil.getColor(28, 156, 179, 100), ColorUtil.getColor(0, 255));
                                         float diff1 = (y + var1 * 2) - (y + var1 + 2);
                                         double percent = 1 - (double) stack2.getItemDamage() * 1 / (double) stack2.getMaxDamage();
                                         RenderingUtil.rectangle(endx + 2, (y + var1 * 2), endx + 5, (y + var1 * 2) - (diff1 * percent), ColorUtil.getColor(78, 206, 229));
                                         mc.fontRendererObj.drawStringWithShadow(stack2.getMaxDamage() - stack2.getItemDamage() + "", endx + 7, ((y + var1 * 2) - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
                                     }
                                     ItemStack stack3 = ((EntityPlayer) ent).getEquipmentInSlot(2);
                                     if (stack3 != null) {
                                         RenderingUtil.rectangleBordered(endx + 1, y + var1 * 2, endx + 6, y + var1 * 3, 1, ColorUtil.getColor(28, 156, 179, 100), ColorUtil.getColor(0, 255));
                                         float diff1 = (y + var1 * 3) - (y + var1 * 2 + 2);
                                         double percent = 1 - (double) stack3.getItemDamage() * 1 / (double) stack3.getMaxDamage();
                                         RenderingUtil.rectangle(endx + 2, (y + var1 * 3), endx + 5, (y + var1 * 3) - (diff1 * percent), ColorUtil.getColor(78, 206, 229));
                                         mc.fontRendererObj.drawStringWithShadow(stack3.getMaxDamage() - stack3.getItemDamage() + "", endx + 7, ((y + var1 * 3) - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
                                     }
                                     ItemStack stack4 = ((EntityPlayer) ent).getEquipmentInSlot(1);
                                     if (stack4 != null) {
                                         RenderingUtil.rectangleBordered(endx + 1, y + var1 * 3, endx + 6, y + var1 * 4, 1, ColorUtil.getColor(28, 156, 179, 100), ColorUtil.getColor(0, 255));
                                         float diff1 = (y + var1 * 4) - (y + var1 * 3 + 2);
                                         double percent = 1 - (double) stack4.getItemDamage() * 1 / (double) stack4.getMaxDamage();
                                         RenderingUtil.rectangle(endx + 2, (y + var1 * 4) - 1, endx + 5, (y + var1 * 4) - (diff1 * percent), ColorUtil.getColor(78, 206, 229));
                                         mc.fontRendererObj.drawStringWithShadow(stack4.getMaxDamage() - stack4.getItemDamage() + "", endx + 7, ((y + var1 * 4) - (diff1 / 2)) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
                                     }
                                 }*/
                            }
                           
                            float health = ((EntityLivingBase) ent).getHealth();
                            if(health > 20)
                            	health = 20;
                            float[] fractions = new float[]{0f, 0.5f, 1f};
                            Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                            float progress = (health * 5) * 0.01f;
                            Color customColor = blendColors(fractions, colors, progress).brighter();
                            double healthLocation = endy + (y - endy) * ((health * 5) * 0.01f);
                            RenderingUtil.rectangle(x - 4, endy - 1, x - 2, healthLocation, customColor.getRGB());
                        } catch (Exception es) {
                        	//ChatUtil.printChat("§c" + ent);
                        }
                    }
                    GlStateManager.popMatrix();
                    GL11.glColor4f(1, 1, 1, 1);
                }
            }
            GL11.glScalef(1, 1, 1);
            GL11.glColor4f(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
	}
	public static boolean isValid(Entity entity){
      //  Module espMod =  Client.getModuleManager().get(ESP.class);
     //   boolean players = (Boolean) espMod.getSetting(ESP.PLAYERS).getValue();
       // boolean invis = (Boolean) espMod.getSetting(ESP.INVISIBLES).getValue();
    //	boolean others = (Boolean) espMod.getSetting(ESP.ANIMALS).getValue();
    	boolean valid = entity instanceof EntityMob || entity instanceof EntityIronGolem ||
				entity instanceof EntityAnimal || entity instanceof EntityVillager || entity instanceof EntityPlayer;
    	if(entity.isInvisible() && !true){

    		return false;
    	}
    	if((true && entity instanceof EntityPlayer) || (true && (entity instanceof EntityMob || entity instanceof EntityAnimal || entity instanceof EntityVillager || entity instanceof EntityIronGolem))){
    		if(entity instanceof EntityPlayerSP){
    			
    			return  mc.gameSettings.thirdPersonView != 0;
    		}else{
    		
    			return true;
    		}
    	
    	}else{
    		return false;
    	}
    }
    

public static Color blendColors(float[] fractions, Color[] colors, float progress) {
    Color color = null;
    if (fractions != null) {
        if (colors != null) {
            if (fractions.length == colors.length) {
                int[] indicies = getFractionIndicies(fractions, progress);

                if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length) {
                    return colors[0];
                }
                float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
                Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};

                float max = range[1] - range[0];
                float value = progress - range[0];
                float weight = value / max;

                color = blend(colorRange[0], colorRange[1], 1f - weight);
            } else {
                throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
            }
        } else {
            throw new IllegalArgumentException("Colours can't be null");
        }
    } else {
        throw new IllegalArgumentException("Fractions can't be null");
    }
    return color;
}

public static int[] getFractionIndicies(float[] fractions, float progress) {
    int[] range = new int[2];

    int startPoint = 0;
    while (startPoint < fractions.length && fractions[startPoint] <= progress) {
        startPoint++;
    }

    if (startPoint >= fractions.length) {
        startPoint = fractions.length - 1;
    }

    range[0] = startPoint - 1;
    range[1] = startPoint;

    return range;
}

public static Color blend(Color color1, Color color2, double ratio) {
    float r = (float) ratio;
    float ir = (float) 1.0 - r;

    float rgb1[] = new float[3];
    float rgb2[] = new float[3];

    color1.getColorComponents(rgb1);
    color2.getColorComponents(rgb2);

    float red = rgb1[0] * r + rgb2[0] * ir;
    float green = rgb1[1] * r + rgb2[1] * ir;
    float blue = rgb1[2] * r + rgb2[2] * ir;

    if (red < 0) {
        red = 0;
    } else if (red > 255) {
        red = 255;
    }
    if (green < 0) {
        green = 0;
    } else if (green > 255) {
        green = 255;
    }
    if (blue < 0) {
        blue = 0;
    } else if (blue > 255) {
        blue = 255;
    }

    Color color = null;
    try {
        color = new Color(red, green, blue);
    } catch (IllegalArgumentException exp) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
        exp.printStackTrace();
    }
    return color;
}

private void updatePositions() {
    entityPositionstop.clear();
    entityPositionsbottom.clear();
    float pTicks = mc.timer.renderPartialTicks;
    for (Object o : mc.theWorld.getLoadedEntityList()) {
  
        if (o instanceof EntityLivingBase && o != mc.thePlayer) {
          	EntityLivingBase ent = (EntityLivingBase)o;
            double x;
            double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
            double z;
            x = ent.lastTickPosX + ((ent.posX + 10) - (ent.lastTickPosX + 10)) * pTicks - mc.getRenderManager().viewerPosX;
            z = ent.lastTickPosZ + ((ent.posZ + 10) - (ent.lastTickPosZ + 10)) * pTicks - mc.getRenderManager().viewerPosZ;
            y += ent.height + 0.2D;
            double[] convertedPoints = convertTo2D(x, y, z);
            double xd = Math.abs(convertTo2D(x, y + 1.0D, z, ent)[1] - convertTo2D(x, y, z, ent)[1]);
            assert convertedPoints != null;
            if ((convertedPoints[2] >= 0.0D) && (convertedPoints[2] < 1.0D)) {
                entityPositionstop.put(ent, new double[]{convertedPoints[0], convertedPoints[1], xd, convertedPoints[2]});
                y = ent.lastTickPosY + ((ent.posY - 2.2) - (ent.lastTickPosY - 2.2)) * pTicks - mc.getRenderManager().viewerPosY;
                entityPositionsbottom.put(ent, new double[]{convertTo2D(x, y, z)[0], convertTo2D(x, y, z)[1], xd, convertTo2D(x, y, z)[2]});
            }
        }
    }
}

private double[] convertTo2D(double x, double y, double z, Entity ent) {
    return convertTo2D(x, y, z);
}

private void scale(Entity ent) {
    float scale = (float) 1;
    float target = scale * (mc.gameSettings.fovSetting
            / (mc.gameSettings.fovSetting/*
     * *
     * mc.thePlayer.getFovModifier()
     *//* .func_175156_o() */));
    if ((this.gradualFOVModifier == 0.0D) || (Double.isNaN(this.gradualFOVModifier))) {
        this.gradualFOVModifier = target;
    }
    this.gradualFOVModifier += (target - this.gradualFOVModifier) / (Minecraft.getDebugFps() * 0.7D);

    scale = (float) (scale * this.gradualFOVModifier);

    GlStateManager.scale(scale, scale, scale);
}

private double[] convertTo2D(double x, double y, double z) {
    FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    IntBuffer viewport = BufferUtils.createIntBuffer(16);
    FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
    FloatBuffer projection = BufferUtils.createFloatBuffer(16);
    GL11.glGetFloat(2982, modelView);
    GL11.glGetFloat(2983, projection);
    GL11.glGetInteger(2978, viewport);
    boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
    if (result) {
        return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
    }
    return null;
}
public static void checkSetupFBO() {
    //Gets the FBO of Minecraft
    Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
    //Check if FBO isn't null
    if (fbo != null) {
        //Checks if screen has been resized or new FBO has been created
        if (fbo.depthBuffer > -1) {
            //Sets up the FBO with depth and stencil extensions (24/8 bit)
            setupFBO(fbo);
            //Reset the ID to prevent multiple FBO's
            fbo.depthBuffer = -1;
        }
    }
}

/**
 * Sets up the FBO with depth and stencil
 *
 * @param fbo Framebuffer
 */
public static void setupFBO(Framebuffer fbo) {
    //Deletes old render buffer extensions such as depth
    //Args: Render Buffer ID
    EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
    //Generates a new render buffer ID for the depth and stencil extension
    int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
    //Binds new render buffer by ID
    //Args: Target (GL_RENDERBUFFER_EXT), ID
    EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
    //Adds the depth and stencil extension
    //Args: Target (GL_RENDERBUFFER_EXT), Extension (GL_DEPTH_STENCIL_EXT), Width, Height
    EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    //Adds the stencil attachment
    //Args: Target (GL_FRAMEBUFFER_EXT), Attachment (GL_STENCIL_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
    EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
    //Adds the depth attachment
    //Args: Target (GL_FRAMEBUFFER_EXT), Attachment (GL_DEPTH_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
    EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
}
	
}
