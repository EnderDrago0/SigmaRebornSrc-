package com.mentalfrostbyte.jello.hud;

import java.awt.Color;
import java.awt.Robot;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mentalfrostbyte.jello.hahaNoBugs;
import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventKeyPressed;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.main.JCore;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.modules.ActiveMods;
import com.mentalfrostbyte.jello.modules.KillAura;
import com.mentalfrostbyte.jello.modules.TargetHUD;
import com.mentalfrostbyte.jello.tabgui.TabGUI.Cat;
import com.mentalfrostbyte.jello.util.BloomUtils;
import com.mentalfrostbyte.jello.util.BlurUtil;
import com.mentalfrostbyte.jello.util.CircleManager;
import com.mentalfrostbyte.jello.util.ColorUtil;
import com.mentalfrostbyte.jello.util.FontUtil;
import com.mentalfrostbyte.jello.util.RenderingUtil;
import com.mentalfrostbyte.jello.util.Stencil;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mentalfrostbyte.jello.util.Value;
import com.minimap.XaeroMinimap;
import com.minimap.animation.Animation;

import fr.lavache.anime.Animate;
import fr.lavache.anime.Easing;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class JelloHud extends GuiScreen {

	public Timer timer;
	Animate anim = new Animate();
	Animate anim1 = new Animate();
	public float current;
	public float current1;
	public TimerUtil timer2 = new TimerUtil();
	
    public Value fadeMode;
    public Value customColor;

    public Value boxes;
    public Value connect;

    public Value offset;
    public Value boxSize;
    public Value red;
    public Value green;
    public Value blue;
    Robot r;
	public static FontRenderer fr;
	public static Notification notification;
	public double arrayTrans;
	public double leftHudTrans;
	public boolean transOver;
	public boolean timerStarted;
	public Random rand = new Random();
	FBO fbo = new FBO();
	FrameBufferProxy proxy = new FrameBufferProxy();
	public int tRed, tGreen, tBlue;
	public int lasttRed, lasttGreen, lasttBlue;
	
	public int nRed, nGreen, nBlue;
	public int lastnRed, lastnGreen, lastnBlue;
	

	public int nbRed, nbGreen, nbBlue;
	public int lastnbRed, lastnbGreen, lastnbBlue;
	
	public int bRed, bGreen, bBlue;
	public int lastbRed, lastbGreen, lastbBlue;
	public Color top = new Color(255,255,255,255);
	public Color bottom = new Color(255,255,255,255);
	public Color notif = new Color(255,255,255,255);
	Thread colorThread;
	
	 public static CircleManager Wcircles = new CircleManager();
	 public static CircleManager Acircles = new CircleManager();
	 public static CircleManager Scircles = new CircleManager();
	 public static CircleManager Dcircles = new CircleManager();
	 public static CircleManager Lcircles = new CircleManager();
	 public static CircleManager Rcircles = new CircleManager();
	
	//public Minimap minimap = new Minimap(new InterfaceHandler.InterfaceHandler$5("gui.xaero_minimap", 128, 128, ModOptions.MINIMAP));
	public XaeroMinimap xaero = new XaeroMinimap();
	int colorTop, colorTopRight, colorBottom, colorBottomRight, colorNotification = 0, colorNotificationBottom = 0;
	
	public Compass compass = new Compass(325, 325, 1, 2, true);
	
	public JelloHud() {
		try {
			xaero.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mc = JCore.mc;
		this.fr = mc.fontRendererObj;
		EventManager.register(this);
		this.timer = new Timer();
       
	}
	
	@EventTarget
	public void onKey(EventKeyPressed e){
		int x = 5;
		int y = 0 + ( Jello.getModule("MiniMap").isToggled() ? 75 + 8 : 0);
		if(e.getEventKey() == mc.gameSettings.keyBindForward.getKeyCode()){
			Wcircles.addCircle(85/2f, 284/2f + y, 26, 5, mc.gameSettings.keyBindForward.getKeyCode());
		}
		if(e.getEventKey() == mc.gameSettings.keyBindLeft.getKeyCode()){
			Acircles.addCircle(34/2f, 334/2f + y, 26, 5, mc.gameSettings.keyBindLeft.getKeyCode());
		}
		if(e.getEventKey() == mc.gameSettings.keyBindBack.getKeyCode()){
			Scircles.addCircle(85/2f, 334/2f + y, 26, 5, mc.gameSettings.keyBindBack.getKeyCode());
		}
		if(e.getEventKey() == mc.gameSettings.keyBindRight.getKeyCode()){
			Dcircles.addCircle(136/2f, 334/2f + y, 26, 5, mc.gameSettings.keyBindRight.getKeyCode());
		}
		if(e.getEventKey() == mc.gameSettings.keyBindAttack.getKeyCode()){
			Lcircles.addCircle(47/2f, 386/2f + y, 35, 5, mc.gameSettings.keyBindAttack.getKeyCode());
		}
		if(e.getEventKey() == mc.gameSettings.keyBindUseItem.getKeyCode()){
			Rcircles.addCircle(124/2f, 386/2f + y, 35, 5, mc.gameSettings.keyBindUseItem.getKeyCode());
		}
	}
	
	
	
	public void renderScreen() {
		
		
		
		
	this.calculateTransitions();
	
	if(!this.transOver){
		GL11.glPushMatrix();
		//GL11.glTranslated(0-this.leftHudTrans, 0, 0);
	}
	this.renderHud();
	if(!this.transOver){
		GL11.glPopMatrix();
		GL11.glPushMatrix();
	}
	if(!this.transOver){
		//GL11.glTranslated(this.arrayTrans, 0, 0);
	}
	if(ActiveMods.enabled){
	this.renderArraylist();
	}
	if(!this.transOver){
		GL11.glPopMatrix();
	}
	
	this.renderNotificationBar();
	ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
	
	GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    
    if(Jello.getModule("Compass").isToggled())
    	renderCompass(sr);
    
        
	
	}
	public void renderNotificationBar(){
		if(notification != null){
			notification.renderNotification();
			}
	}
	public void calculateTransitions(){
		if(arrayTrans > 0 && timerStarted && timer2.hasTimeElapsed(1100, false)){
			arrayTrans -= (arrayTrans/(8*Minecraft.getMinecraft().getDebugFps()*0.04))+0.01;
		}else if(arrayTrans <= 1 && leftHudTrans <= 1){
			transOver = true;
			timerStarted = false;
		}
		if(leftHudTrans > 0 && timerStarted && timer2.hasTimeElapsed(1100, false)){
			leftHudTrans -= (leftHudTrans/(8*Minecraft.getMinecraft().getDebugFps()*0.04))+0.01;
		}else if(leftHudTrans <= 1 && arrayTrans <= 1){
			transOver = true;
			timerStarted = false;
		}
	}

	@EventTarget
	public void onTick(EventTick e){
		
		lasttRed = tRed;
		lasttGreen = tGreen;
		lasttBlue = tBlue;

		lastnRed = nRed;
		lastnGreen = nGreen;
		lastnBlue = nBlue;

		lastnbRed = nbRed;
		lastnbGreen = nbGreen;
		lastnbBlue = nbBlue;
	       
		lastbRed = bRed;
		lastbGreen = bGreen;
	    lastbBlue = bBlue;
	    
	    if(notification != null){
			notification.onTick();
			}
	    
	    Wcircles.runCircles();
		Acircles.runCircles();
		Scircles.runCircles();
		Dcircles.runCircles();
		Lcircles.runCircles();
		Rcircles.runCircles();
		if(Jello.tabgui.showModules){
    		int categoryIndex = Jello.tabgui.currentCategory;
    		Cat category = Jello.tabgui.cats.get(categoryIndex);
    		List<Module> modules = Jello.getModulesInCategory(category);
    		int size = modules.size();
    		int currentModule = category.selectedIndex;
    		
    		category.lastSelectedTrans = category.selectedTrans;

    		category.selectedTrans += (((currentModule*15)-category.selectedTrans)/(2.5f))+0.01;
    		
    		
    	}
		
	    
		Color top = ColorUtil.blend(ColorUtil.colorFromInt(colorTop), ColorUtil.colorFromInt(colorTopRight));
		Color bottom = ColorUtil.blend(ColorUtil.colorFromInt(colorBottom), ColorUtil.colorFromInt(colorBottomRight));
		
		bRed += ((bottom.getRed()-bRed)/(5))+0.1;
		bGreen += ((bottom.getGreen()-bGreen)/(5))+0.1;
		bBlue += ((bottom.getBlue()-bBlue)/(5))+0.1;

		tRed += ((top.getRed()-tRed)/(5))+0.1;
		tGreen += ((top.getGreen()-tGreen)/(5))+0.1;
		tBlue += ((top.getBlue()-tBlue)/(5))+0.1;
		
		nRed += ((ColorUtil.colorFromInt(colorNotification).getRed()-nRed)/(5))+0.1;
		nGreen += ((ColorUtil.colorFromInt(colorNotification).getGreen()-nGreen)/(5))+0.1;
		nBlue += ((ColorUtil.colorFromInt(colorNotification).getBlue()-nBlue)/(5))+0.1;

		nbRed += ((ColorUtil.colorFromInt(colorNotificationBottom).getRed()-nbRed)/(5))+0.1;
		nbGreen += ((ColorUtil.colorFromInt(colorNotificationBottom).getGreen()-nbGreen)/(5))+0.1;
		nbBlue += ((ColorUtil.colorFromInt(colorNotificationBottom).getBlue()-nbBlue)/(5))+0.1;
	       
	       tRed = Math.min((int)tRed, 255);
	       tGreen = Math.min((int)tGreen, 255);
	       tBlue = Math.min((int)tBlue, 255);
	       tRed = Math.max((int)tRed, 0);
	       tGreen = Math.max((int)tGreen, 0);
	       tBlue = Math.max((int)tBlue, 0);

	       nRed = Math.min((int)nRed, 255);
	       nGreen = Math.min((int)nGreen, 255);
	       nBlue = Math.min((int)nBlue, 255);
	       nRed = Math.max((int)nRed, 0);
	       nGreen = Math.max((int)nGreen, 0);
	       nBlue = Math.max((int)nBlue, 0);
	       
	       nbRed = Math.min((int)nbRed, 255);
	       nbGreen = Math.min((int)nbGreen, 255);
	       nbBlue = Math.min((int)nbBlue, 255);
	       nbRed = Math.max((int)nbRed, 0);
	       nbGreen = Math.max((int)nbGreen, 0);
	       nbBlue = Math.max((int)nbBlue, 0);
	       
	       
	       bRed = Math.min((int)bRed, 255);
	       bGreen = Math.min((int)bGreen, 255);
	       bBlue = Math.min((int)bBlue, 255);
	       bRed = Math.max((int)bRed, 0);
	       bGreen = Math.max((int)bGreen, 0);
	       bBlue = Math.max((int)bBlue, 0);
	}
	public static boolean h;
	Gui gui = new Gui();
	ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	public void renderHud(){
		if(hahaNoBugs.hasReach(4000)) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        

       if(timer.hasReach(50)){
        int p_148259_2_ = 0, p_148259_3_ = 0;
        IntBuffer pixelBuffer = null;
        int[] pixelValues = null;
        
            if (OpenGlHelper.isFramebufferEnabled())
            {
                p_148259_2_ = 180;
                p_148259_3_ = 280;
            }

            int var6 = p_148259_2_ * p_148259_3_;

            if (pixelBuffer == null || pixelBuffer.capacity() < var6)
            {
                pixelBuffer = BufferUtils.createIntBuffer(var6);
                pixelValues = new int[var6];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();
          
                GL11.glReadPixels(0, sr.getScaledHeight()- (p_148259_3_-sr.getScaledHeight())/*728*/, p_148259_2_, p_148259_3_, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
                
            pixelBuffer.get(pixelValues);
            TextureUtil.func_147953_a(pixelValues, p_148259_2_, p_148259_3_);
            
          //  System.out.println(mc.gameSettings.guiScale);
            	if(!(mc.currentScreen instanceof GuiGameOver) && mc.gameSettings.guiScale == 2){
            		if(pixelValues.length > 1000) {
            		colorTop = pixelValues[(45*sr.getScaleFactor()) * p_148259_2_ + 10];
                    colorTopRight = pixelValues[(45*sr.getScaleFactor()) * p_148259_2_ + 130];
                    
                    colorBottom = pixelValues[((45 + 77)*sr.getScaleFactor()) * p_148259_2_ + 10];
                    colorBottomRight = pixelValues[((45 + 77)*sr.getScaleFactor()) * p_148259_2_ + 130];
            	}}
            	
            	
            	p_148259_2_ = 0;
            	p_148259_3_ = 0;
                pixelBuffer = null;
                pixelValues = null;
                
                    if (OpenGlHelper.isFramebufferEnabled())
                    {
                        p_148259_2_ = 280;
                        p_148259_3_ = 150;
                    }

                    var6 = p_148259_2_ * p_148259_3_;

                    if (pixelBuffer == null || pixelBuffer.capacity() < var6)
                    {
                        pixelBuffer = BufferUtils.createIntBuffer(var6);
                        pixelValues = new int[var6];
                    }

                    GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
                    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                    pixelBuffer.clear();
                  
                        GL11.glReadPixels(sr.getScaledWidth()- (p_148259_2_-sr.getScaledWidth()), sr.getScaledHeight()- (p_148259_3_-sr.getScaledHeight())/*728*/, p_148259_2_, p_148259_3_, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
                        
                    pixelBuffer.get(pixelValues);
                    TextureUtil.func_147953_a(pixelValues, p_148259_2_, p_148259_3_);
                    if(pixelValues.length > 1000) {
                    		colorNotification = pixelValues[(10) * p_148259_2_ + 270];
                    		colorNotificationBottom = pixelValues[(77) * p_148259_2_ + 270];
                    }

            	timer.reset();
       }
            	
       EventRender2D e = new EventRender2D();
       EventManager.call(e);
       
       GlStateManager.disableAlpha();
       
       this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/Jello.png"));
       this.drawModalRectWithCustomSizedTexture(0.5f - 1, 2 - 2.5f, 0, 0, 86, 49, 86, 49);
       
     		if(Jello.getModule("TabGUi").isToggled()){
     			
     		
       this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/TabGUIShadow.png"));
       this.drawModalRectWithCustomSizedTexture(0.5f, 40.5f, 0, 0, 84, 86, 84, 86);
       
	}
       
       int yOff = 0;
      
       GlStateManager.disableAlpha();
       GlStateManager.enableBlend();
   		if(Jello.getModule("MiniMap").isToggled()){
   			
       
       this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/MiniMap.jpg"));
       this.drawModalRectWithCustomSizedTexture(0.5f, 251/2f, 0, 0, 168/2f, 168/2f, 168/2f, 168/2f);
       yOff += 75 + 8;
   		}
       
      		if(Jello.getModule("KeyStrokes").isToggled()){
      			
      		
       this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/keystrokes.png"));
       this.drawModalRectWithCustomSizedTexture(0.5f  - 1, 251/2f  - 1 + yOff, 0, 0, 172/2f, 172/2f, 172/2f, 172/2f);

       GlStateManager.disableAlpha();
       GlStateManager.enableBlend();
       int keyStrokeX = 5;
       int keyStrokeY = 130;
       
       Stencil.write(false);
		Gui.drawFloatRect(keyStrokeX + 26.5f - 1, keyStrokeY + yOff, keyStrokeX + 35 + 15.5f - 1, keyStrokeY + 25 - 1 + yOff, 0xb2000000);
		Stencil.erase(true);
		GlStateManager.enableBlend();
		Wcircles.drawCircles();
		Stencil.dispose();
		
		Stencil.write(false);
		Gui.drawFloatRect(keyStrokeX, keyStrokeY + 26.5f - 1 + yOff, keyStrokeX + 25 - 1, keyStrokeY + 30 + 5 + 15.5f - 1 + yOff, 0xb2000000);
		Stencil.erase(true);
		GlStateManager.enableBlend();
		Acircles.drawCircles();
		Stencil.dispose();
		
		Stencil.write(false);
		Gui.drawFloatRect(keyStrokeX + 51/2f, keyStrokeY + 26.5f - 1 + yOff, keyStrokeX + 25 + 51/2f - 1, keyStrokeY + 30 + 5 + 15.5f - 1 + yOff, 0xb2000000);
		Stencil.erase(true);
		GlStateManager.enableBlend();
		Scircles.drawCircles();
		Stencil.dispose();
		
		Stencil.write(false);
		Gui.drawFloatRect(keyStrokeX + 51/2f+ 51/2f, keyStrokeY + 26.5f - 1 + yOff, keyStrokeX + 25 + 51/2f+ 51/2f - 1, keyStrokeY + 30 + 5 + 15.5f - 1 + yOff, 0xb2000000);
		Stencil.erase(true);
		GlStateManager.enableBlend();
		Dcircles.drawCircles();
		Stencil.dispose();
		
		Stencil.write(false);
		Gui.drawFloatRect(keyStrokeX, keyStrokeY + 26.5f + 51/2f - 1 + yOff, keyStrokeX + 74/2f, keyStrokeY + 26.5f + 51/2f + 24 - 1 + yOff, 0xb2000000);
		Stencil.erase(true);
		GlStateManager.enableBlend();
		Lcircles.drawCircles();
		Stencil.dispose();
		
		Stencil.write(false);
		Gui.drawFloatRect(keyStrokeX + 77/2f, keyStrokeY + 26.5f + 51/2f - 1 + yOff, keyStrokeX + 74/2f + 76/2f, keyStrokeY + 26.5f + 51/2f + 24 - 1 + yOff, 0xb2000000);
		Stencil.erase(true);
		GlStateManager.enableBlend();
		Rcircles.drawCircles();
		Stencil.dispose();
		
		yOff += 100;
       }
	
       

       
       int tR = smoothAnimation(tRed, lasttRed);
       int tG = smoothAnimation(tGreen, lasttGreen);
       int tB = smoothAnimation(tBlue, lasttBlue);
       
       int bR = smoothAnimation(bRed, lastbRed);
       int bG = smoothAnimation(bGreen, lastbGreen);
       int bB = smoothAnimation(bBlue, lastbBlue);
       
    		if(Jello.getModule("TabGUi").isToggled()){
    			Gui.INSTANCE.drawGradientRect(5, 45, 5 + 75, 45 + 77,/*415277420*/(new Color(tR, tG, tB, 255)).getRGB(), (new Color(bR, bG, bB, 255)).getRGB());
       
   	
       GlStateManager.disableAlpha();
    	GlStateManager.enableBlend();
    	GL11.glEnable(3042);
    	GL11.glColor4f(1, 1, 1, 1);
    	
    	if(Jello.tabgui.showModules){
    		

    		int categoryIndex = Jello.tabgui.currentCategory;
    		Cat category = Jello.tabgui.cats.get(categoryIndex);
    		List<Module> modules = Jello.getModulesInCategory(category);
    		int size = modules.size();
    		int currentModule = category.selectedIndex;
    		
    		float trans = category.selectedTrans;
    		float lastTrans = category.lastSelectedTrans;
    		
    		float smoothTrans = smoothTrans(trans, lastTrans);
    		
    		if(size != 0){
    	       Gui.INSTANCE.drawGradientRect(85, 45, 85 + 75 + 10, 45 + 15*size + 2.5f, (new Color(tR, tG, tB, 255)).getRGB(), (new Color(bR, bG, bB, 255)).getRGB());
    		
    	       GlStateManager.disableAlpha();
    	    	GlStateManager.enableBlend();
    	    	GL11.glEnable(3042);
    	    	GL11.glColor4f(1, 1, 1, 1);

    	    		this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/TabGUISelector.png"));
                	this.drawModalRectWithCustomSizedTexture(85, 45+ smoothTrans, 0, 0, 75 + 10, 17.5f, 75, 17.5f);
    	    	}
                int y = 0;
                for(Module m : modules){
                	if(y == 0){
                	this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/TabGUIShadow2.png"));
             	       this.drawModalRectWithCustomSizedTexture(0.5f +  75 + 5, 40.5f, 0, 0, 84 + 10, 20, 84 + 10, 86);
                	}else
                	if(y == size-1){
                		this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/TabGUIShadow2.png"));
              	       this.drawModalRectWithCustomSizedTexture(0.5f +  75 + 5, 40.5f + 15*y + 5f, 0, 64.5f, 84 + 10, 20, 84 + 10, 86);
                	}else{
                		this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/TabGUIShadow2.png"));
               	       this.drawModalRectWithCustomSizedTexture(0.5f +  75 + 5, 40.5f + 15*y + 5f, 0, 30, 84 + 10, 15, 84 + 10, 86);
                 	
                	}
                if(m.isToggled()){
                FontUtil.jelloFontBoldSmall.drawString(m.getName(), 85 + 11/2f, 45+ 15*y + 5, -1);  	
                }else{
                FontUtil.jelloFontMarker.drawString(m.getName(), 85 + 11/2f, 45+ 15*y + 5, -1);
                }
                y++;
                }
    	}
    	
    	int y = 0;
        for(Cat c : Jello.tabgui.cats){
        	boolean selected = c.equals(Jello.tabgui.cats.get(Jello.tabgui.currentCategory));
        	if(selected){
        		if(!Float.isFinite(Jello.tabgui.seenTrans)){
        			Jello.tabgui.seenTrans = 0;
            	}
        		Jello.tabgui.seenTrans += (((Jello.tabgui.currentCategory*15)-Jello.tabgui.seenTrans)/(5*Minecraft.getMinecraft().getDebugFps()*0.04))-0.001;
        		Jello.tabgui.seenTrans += (((Jello.tabgui.currentCategory*15)-Jello.tabgui.seenTrans)/(5*Minecraft.getMinecraft().getDebugFps()*0.04))-0.001;
        		this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/TabGUISelector.png"));
                this.drawModalRectWithCustomSizedTexture(5, 45 + Jello.tabgui.seenTrans, 0, 0, 75, 17, 75, 17);
            	
        	}
            y++;
        }
    	
    	int x = 0;
        for(Cat c : Jello.tabgui.cats){
        	boolean selected = c.equals(Jello.tabgui.cats.get(Jello.tabgui.currentCategory));
        	
        	if(!Float.isFinite(c.seenTrans)){
        		c.seenTrans = 0;
        	}
        	c.seenTrans += (((selected ? 7 : 0)-c.seenTrans)/(5*Minecraft.getMinecraft().getDebugFps()*0.04))+0.001;
        	c.seenTrans += (((selected ? 7 : 0)-c.seenTrans)/(5*Minecraft.getMinecraft().getDebugFps()*0.04))+0.001;
        	if(x < 5) {
	    		this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/"+c.location));
	            this.drawModalRectWithCustomSizedTexture(5 + c.seenTrans, 45 + x*15, 0, 0, 75, 17, 75, 17);
        	}
            x++;
        }
    }
        GlStateManager.disableBlend();
		  GlStateManager.popMatrix();
		}
		if(KillAura.attacking && TargetHUD.enabled && mc.thePlayer.getLastAttacker() != null) {
			anim.setEase(Easing.BACK_OUT);
			anim.setMin(-40);   //-80 
			anim.setMax(100);
			anim.setReversed(false);
			anim.setSpeed(200);//200
			anim.update();
			current = -anim.getValue();
			BlurUtil.blurAreaBoarder((int) TargetHUD.x.getValue() - 78, (int) TargetHUD.y.getValue() + 65 - (int)anim.getValue(), 130, 50, 20);
			
			EntityLivingBase LastAtackedTarget = (EntityLivingBase) mc.thePlayer.getLastAttacker();
			//if(LastAtackedTarget != null) {	//1000
				GlStateManager.pushMatrix();//-200     //600
				GlStateManager.translate(TargetHUD.x.getValue(), TargetHUD.y.getValue() - anim.getValue(), 0);
		//		GlStateManager.scale(Animation.animate(1, 1.07), Animation.animate(1, 1.07), 1);
				//Gui.drawRect(sr.getScaledWidth_double() - 505, sr.getScaledHeight_double() - 175 + 0, sr.getScaledWidth_double() - 375, sr.getScaledHeight_double() - 125, 0x90000000);
				
				//RenderUtil.color(RenderUtil.getRainbow(10, 0.5f, 1, 1));
				RenderingUtil.color(0x60000000);
				
				
				//RenderingUtil.drawRoundedRectz(-78, 65, 130, 50, 1);
				
				
				GlStateManager.pushMatrix();
				mc.getTextureManager().bindTexture(new ResourceLocation("Jello/jellotarget.png"));
		        GL11.glEnable(3042);
		        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
		        Gui.drawModalRectWithCustomSizedTexture(-113, 40, 200, 100, 200, 100, 200, 100);
		        GL11.glDisable(3042);
				GlStateManager.popMatrix();
		       // System.out.println(anim.getValue());
				FontUtil.san.drawString(LastAtackedTarget.getName(), -33, 75 , -1);
				FontUtil.san.drawString((int)LastAtackedTarget.getHealth() + " Health", -33, 90, -1);
				if(LastAtackedTarget instanceof EntityPlayer)
					renderPlayer2d(sr.getScaledWidth_double() - 500, sr.getScaledHeight_double() - 170, 25,25, 25, 25, 35, 35, 200, 200, (AbstractClientPlayer) LastAtackedTarget, LastAtackedTarget);
				
				
				
				if(LastAtackedTarget.getHealth() >= 13)
					//GL11.glColor4d(0, 255, 0, 1);
					GlStateManager.color(0,255,0, 0.4f);
				else if(LastAtackedTarget.getHealth() < 13 && LastAtackedTarget.getHealth() > 6.5)
					//GL11.glColor4d(252, 173, 3, 1);
					GlStateManager.color(252,173,0,0.4f);
				else
				//	GL11.glColor4d(255, 0, 0, 1);
					GlStateManager.color(255,0,0,0.4f);
				
				
				
				RenderingUtil.drawRoundedRectz(-73, 107, 500 - (-((int)LastAtackedTarget.getHealth() * 6) + 500), 4, 0);
			//	gui.drawRect(sr.getScaledWidth() - 500, sr.getScaledHeight() - 131, sr.getScaledWidth() - (-((int)LastAtackedTarget.getHealth() * 6) + 500), sr.getScaledHeight() - 129, 0xff00ff22);//0xff03fc1c
				GlStateManager.popMatrix();
				anim1.reset();
				
			}else {
				anim.reset();
				anim1.setEase(Easing.BACK_IN);
				anim1.setMin(current + 20);//+ 10 wen linear//  + 20    // + 30 linear
				anim1.setMax(200);//230
				anim1.setReversed(false);
				anim1.setSpeed(300);
				anim1.update();
				current1 = anim1.getValue();
			//	System.out.println(anim1.getValue());
				BlurUtil.blurAreaBoarder((int) TargetHUD.x.getValue() - 78, (int) TargetHUD.y.getValue() + 65 + (int)anim1.getValue(), 130, 50, 20);
				
				EntityLivingBase LastAtackedTarget = (EntityLivingBase) mc.thePlayer.getLastAttacker();
				//if(LastAtackedTarget != null) {	//1000
					GlStateManager.pushMatrix();//-200     //600
					GlStateManager.translate(TargetHUD.x.getValue(), TargetHUD.y.getValue() + anim1.getValue(), 0);
			//		GlStateManager.scale(Animation.animate(1, 1.07), Animation.animate(1, 1.07), 1);
					//Gui.drawRect(sr.getScaledWidth_double() - 505, sr.getScaledHeight_double() - 175 + 0, sr.getScaledWidth_double() - 375, sr.getScaledHeight_double() - 125, 0x90000000);
					
					//RenderUtil.color(RenderUtil.getRainbow(10, 0.5f, 1, 1));
					RenderingUtil.color(0x60000000);
					
					
					//RenderingUtil.drawRoundedRectz(-78, 65, 130, 50, 1);
					
					
					GlStateManager.pushMatrix();
					mc.getTextureManager().bindTexture(new ResourceLocation("Jello/jellotarget.png"));
			        GL11.glEnable(3042);
			        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
			        Gui.drawModalRectWithCustomSizedTexture(-113, 40, 200, 100, 200, 100, 200, 100);
			        GL11.glDisable(3042);
					GlStateManager.popMatrix();
			        
					FontUtil.san.drawString("JelloSigma", -33, 75 , -1);
					FontUtil.san.drawString((int)mc.thePlayer.getHealth() + " Health", -33, 90, -1);
					if(mc.thePlayer instanceof EntityPlayer)
						renderPlayer2d(sr.getScaledWidth_double() - 500, sr.getScaledHeight_double() - 170, 25,25, 25, 25, 35, 35, 200, 200, (AbstractClientPlayer) mc.thePlayer, mc.thePlayer);
					
					
					
					if(mc.thePlayer.getHealth() >= 13)
						//GL11.glColor4d(0, 255, 0, 1);
						GlStateManager.color(0,255,0, 0.4f);
					else if(mc.thePlayer.getHealth() < 13 && mc.thePlayer.getHealth() > 6.5)
						//GL11.glColor4d(252, 173, 3, 1);
						GlStateManager.color(252,173,0,0.4f);
					else
					//	GL11.glColor4d(255, 0, 0, 1);
						GlStateManager.color(255,0,0,0.4f);
					
					
					
					RenderingUtil.drawRoundedRectz(-73, 107, 500 - (-((int)mc.thePlayer.getHealth() * 6) + 500), 4, 0);
				//	gui.drawRect(sr.getScaledWidth() - 500, sr.getScaledHeight() - 131, sr.getScaledWidth() - (-((int)LastAtackedTarget.getHealth() * 6) + 500), sr.getScaledHeight() - 129, 0xff00ff22);//0xff03fc1c
					GlStateManager.popMatrix();
			}
			
		//}//else{
			//istargeting = false;
		/*	GlStateManager.pushMatrix();
			anim.resetAnim(180);
			GlStateManager.translate(500, 300, 0);
			//Gui.drawRect(sr.getScaledWidth_double() - 505, sr.getScaledHeight_double() - 175 + 0, sr.getScaledWidth_double() - 375, sr.getScaledHeight_double() - 125, 0x90000000);
			
			RenderingUtil.color(0x90000000);
			RenderingUtil.drawRoundedRectz(sr.getScaledWidth_double() - 505, sr.getScaledHeight_double() - 175, 130, 50, 8);
			
			fr.drawString(mc.thePlayer.getName(),sr.getScaledWidth() - 460, sr.getScaledHeight() - 165 , -1);
			fr.drawString( (int) mc.thePlayer.getHealth() + " Health", sr.getScaledWidth() - 460, sr.getScaledHeight() - 150, -1);
			renderPlayer2d(460, 338, 25,25, 25, 25, 35, 35, 200, 200, (AbstractClientPlayer) mc.thePlayer, mc.thePlayer);
			gui.drawRect(sr.getScaledWidth() - 500, sr.getScaledHeight() - 131, sr.getScaledWidth() - (-((int)mc.thePlayer.getHealth() * 6) + 500), sr.getScaledHeight() - 129, 0xff03fc1c);
			GlStateManager.popMatrix();*/
		//}
	}
	public float hurt(EntityLivingBase target, float plus) {
		switch(target.hurtTime) {
		case 0:
			return 1;
		//case 1:
		//	return (float) 0.90;
		//case 2:
		//	return 0.80F + plus;
		//case 3:
		//	return 0.70f + plus;
		//case 4:
		//	return 0.60f + plus;
		case 5:
			return 0.90f + plus;
		case 6:
			return 0.80f + plus;
		case 7:
			return 0.70f + plus;
		case 8:
			return 0.60f + plus;
		case 9:
			return 0.50f + plus;
		case 10:
			return 0.40f+ plus;
		}
		return 1;
	}
	private void renderPlayer2d(final double n, final double n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8, final float n9, final float n10, final AbstractClientPlayer abstractClientPlayer, EntityLivingBase target) {
		mc.getTextureManager().bindTexture(abstractClientPlayer.getLocationSkin());
        GL11.glEnable(3042);
        GL11.glColor4f(1.0f, hurt(target, 0), 1.0f, 1.0f);
        Gui.drawScaledCustomSizeModalRect((int)n, (int)n2, n3, n4, n5, n6, n7, n8, n9, n10);
        GL11.glDisable(3042);
    }
	public int smoothAnimation(double current, double last){
		return (int) (current * mc.timer.renderPartialTicks + (last * (1.0f - mc.timer.renderPartialTicks)));
	}
	public float smoothTrans(double current, double last){
		return (float) (current * mc.timer.renderPartialTicks + (last * (1.0f - mc.timer.renderPartialTicks)));
	}
	
	
	public void renderArraylist(){
		ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		//int count = 0;
		 float yStart = 1;
	        float xStart = 0;
	        int colorFade = 0;

		Collections.sort(Jello.mods, new ModuleComparator());
for(Module module : Jello.mods) {
	if(!module.isToggled())
		continue;
			xStart = (float) (sr.getScaledWidth() - FontUtil.jelloFont.getStringWidth(module.getDisplayName()) - 5);
            int color = Color.green.getRGB() + 00;
            GL11.glPushMatrix();
            if(notification != null && Jello.getModule("Notifications").isToggled()){
            	GL11.glTranslated(0, notification.getProgress(), 0);
            	}
            GL11.glColor4f(1, 1, 1, 1);
           // this.handleAnimations(module);
            //this.drawPrefrences(xStart, yStart, color);
            GL11.glPopMatrix();
            if(notification != null && Jello.getModule("Notifications").isToggled()){
            	GL11.glPushMatrix();
            	GL11.glTranslated(0, notification.getProgress(), 0);
            	}
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, 1);
            //if(module.animation >= 0){
            this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/arraylistshadow.png"));
           //GlStateManager.disableBlend();
            // Gui.drawFloatRect(xStart - (float)FontUtil.jelloFont.getStringWidth(module.getDisplayName())/2.5f + 5, 0, sr.getScaledWidth(), 100, -1);
            //if(yStart < 10)
            GlStateManager.color(1, 1, 1, 0.7f);
            this.drawModalRectWithCustomSizedTexture(xStart - 8 -2 - 1, yStart + 2 - 2.5f - 1.5f - 1.5f - 1.5f - 6 - 1, 0, 0, FontUtil.jelloFont.getStringWidth(module.getDisplayName())*1 + 20 + 10, 18.5 + 6 + 12 + 2, FontUtil.jelloFont.getStringWidth(module.getDisplayName())*1 + 20 + 10, 18.5 + 6 + 12 + 2);
            //FontUtil.jelloFont.drawString(module.getDisplayName(), xStart, yStart + 7.5f, 0xffffffff);
            //}
            if(notification != null && Jello.getModule("Notifications").isToggled()){
            	GL11.glPopMatrix();
            	}
            yStart +=  7.5f  +5.25f;
            if ((module.animation != -5)) {
                colorFade++;
                if (colorFade > 50) {
                    colorFade = 0;
                }
            }
        }	
 yStart = 1;
 xStart = 0;
 colorFade = 0;
		for(Module module : Jello.mods) {
			if(!module.isToggled())
				continue;
				
			xStart = (float) (sr.getScaledWidth() - FontUtil.jelloFont.getStringWidth(module.getDisplayName()) - 5);
            int color = Color.green.getRGB() + 00;
            GL11.glPushMatrix();
            if(notification != null && Jello.getModule("Notifications").isToggled()){
            	GL11.glTranslated(0, notification.getProgress(), 0);
            	}
            GL11.glColor4f(1, 1, 1, 1);
           // this.handleAnimations(module);
            //this.drawPrefrences(xStart, yStart, color);
            GL11.glPopMatrix();
            if(notification != null && Jello.getModule("Notifications").isToggled()){
            	GL11.glPushMatrix();
            	GL11.glTranslated(0, notification.getProgress(), 0);
            	}
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, 1);
            if(module.animation >= 0){
           // this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/arraylistshadow.png"));
           //GlStateManager.disableBlend();
            // Gui.drawFloatRect(xStart - (float)FontUtil.jelloFont.getStringWidth(module.getDisplayName())/2.5f + 5, 0, sr.getScaledWidth(), 100, -1);
           // this.drawModalRectWithCustomSizedTexture(xStart - 8, yStart + 2 - 2.5f - 1.5f - 1.5f - 0.5f, 0, 0, FontUtil.jelloFont.getStringWidth(module.getDisplayName())*1.8f + 5, 18.5 + 10, FontUtil.jelloFont.getStringWidth(module.getDisplayName())*1.8f + 5, 18.5 + 10);
            FontUtil.jelloFont.drawString(module.getDisplayName(), xStart, yStart + 7.5f, 0xffffffff);
            }
            if(notification != null && Jello.getModule("Notifications").isToggled()){
            	GL11.glPopMatrix();
            	}
            yStart +=  7.5f  +5.25f;//module.animHeight;
            if ((module.animation != -5)) {
                colorFade++;
                if (colorFade > 50) {
                    colorFade = 0;
                }
            }
        }		
	}
	
	public static class ModuleComparator
    implements Comparator<Module> {
        @Override
        public int compare(Module o1, Module o2) {
            
            	if (FontUtil.jelloFont.getStringWidth(o1.getDisplayName()) < FontUtil.jelloFont.getStringWidth(o2.getDisplayName())) {
                    return 1;
                }
                if (FontUtil.jelloFont.getStringWidth(o1.getDisplayName()) > FontUtil.jelloFont.getStringWidth(o2.getDisplayName())) {
                    return -1;
                }
            
            return 0;
        }
    }
	
	public class Timer {

	    private long lastCheck = getSystemTime();

	    public boolean hasReach(float mil) {
	        return getTimePassed() >= (mil);
	    }

	    public boolean hasReach(double mil) {
	        return getTimePassed() >= (mil);
	    }

	    public void reset() {
	        lastCheck = getSystemTime();
	    }

	    private long getTimePassed() {
	        return getSystemTime() - lastCheck;
	    }

	    private long getSystemTime() {
	        return System.nanoTime() / (long) (1E6);
	    }

	}
	protected void clearScreen(int displayWidth, int displayHeight, float zDepth)
	  {
	    GL11.glClearDepth(999.0D);
	    GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
	    GL11.glClear(16640);
	    
	    GL11.glDisable(3553);
	    GL11.glEnable(3008);
	    GL11.glAlphaFunc(518, 0.0F);
	    GL11.glBlendFunc(1, 0);
	    GL11.glShadeModel(7424);
	    GL11.glEnable(2929);
	    GL11.glDepthMask(true);
	    GL11.glDepthFunc(519);
	    Tessellator tessellator = Tessellator.instance;
	    WorldRenderer wr = tessellator.getWorldRenderer();
	    wr.startDrawingQuads();
	    wr.func_178960_a(0.0F, 0.0F, 0.0F, 1.0F);
	    wr.addVertex(displayWidth, 0.0D, -zDepth);
	    wr.addVertex(0.0D, 0.0D, -zDepth);
	    wr.addVertex(0.0D, displayHeight, -zDepth);
	    wr.addVertex(displayWidth, displayHeight, -zDepth);
	    wr.draw();
	    GL11.glDepthFunc(515);
	    GL11.glEnable(3008);
	    GL11.glAlphaFunc(516, 0.1F);
	    GL11.glEnable(3553);
	    GL11.glDepthMask(true);
	  }
	
	public void renderCompass(ScaledResolution sr)
	  {
		compass.draw(sr);
	  }
	 
	
	  public float getNormalYaw(int offset)
	  {
	    return Jello.core.normalise(this.mc.thePlayer.rotationYaw, -90 + offset, 90 + offset) * 2.0F;
	  }
	  
	  private int getColorForCompass(String offset)
	  {
	    float normalYaw = getNormalYaw(Integer.valueOf(offset).intValue());
	    
	    int color = (int)Math.min(255.0F, Math.max(1.0F, Math.abs(-255.0F + (!String.valueOf(offset).contains("-") ? (normalYaw + Float.valueOf(offset).floatValue() * 2.0F) / 2.0F / 90.0F * 255.0F : -((normalYaw + Float.valueOf(offset).floatValue() * 2.0F) / 2.0F / 90.0F * 255.0F))) + 4.0F));
	    if (color == 32) {
	      color = 31;
	    }
	    if (color == 66) {
	      color = 64;
	    }
	    if (color == 67) {
	      color = 65;
	    }
	    if (color == 35) {
	      color = 34;
	    }
	    return new Color(255, 255, 255, color).getRGB();
	  }
	  
	  public int getColorForCompass(float offset, float otherOffset)
	  {
	    float normalYaw = getNormalYaw((int)offset + (int)otherOffset);
	    
	    int color = (int)Math.min(255.0F, Math.max(1.0F, Math.abs(-255.0F + (!String.valueOf(offset).contains("-") ? (normalYaw + offset * 2.0F) / 2.0F / 90.0F * 255.0F : -((normalYaw + offset * 2.0F) / 2.0F / 90.0F * 255.0F))) + 4.0F));
	    if (color == 32) {
	      color = 31;
	    }
	    if (color == 66) {
	      color = 65;
	    }
	    if (color == 67) {
	      color = 66;
	    }
	    if (color == 35) {
	      color = 34;
	    }
	    return new Color(255, 255, 255, color).getRGB();
	  }
	
	
	private void drawPrefrences(float xStart, float yStart, int color) {
		ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        /** Drawing the real background **/
        //Gui.drawFloatRect(((xStart - 3)), ((yStart + 12)), (sr.getScaledWidth()), (int) ((yStart - 2)), 0xff000000);
		Gui.drawFloatRect( ((xStart - 0)),((yStart + 9)),  (sr.getScaledWidth()), ((yStart - 1)), new Color(15, 15, 15, 255).getRGB() + 00);
        //if (this.boxes.getBooleanValue())
        //    Gui.drawRect((int) ((sr.getScaledWidth() - 2)), (int) (((yStart - 16))), (int) ((sr.getScaledWidth())), (int) ((yStart - 2)), color);
       // if (this.connect.getBooleanValue()) {
          Gui.drawHLine( ((xStart - 0)), ((sr.getScaledWidth())), ((yStart + 9)), 0xffffffff);
            Gui.drawVLine( ((xStart - 0)), ((yStart - 2)), ((yStart + 9)), 0xffffffff);
     //   }

    }
	
	public void onWorldLoad(){
		timer2.reset();
		ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		this.arrayTrans = 120;
		this.leftHudTrans = 170;
		this.timerStarted = true;
		this.transOver = false;
	}
}
