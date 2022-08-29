package com.mentalfrostbyte.jello.modules;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.client.renderer.GlStateManager;

public class RenderModule extends Module {
	
	public BooleanValue firstPerson = new BooleanValue("ShowInFirstPerson", false); 
	public static long lastFrame = 0;
    private int ticks;
	
	public RenderModule() {
        super("ChinaHat", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(4);
        this.addValue(firstPerson);
    }
   
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	
    @EventTarget
	public void rend(EventRender3D e) {
    	if(mc.gameSettings.thirdPersonView == 0)
    		return;
    	ticks += .004 * (System.currentTimeMillis() - lastFrame);
        lastFrame = System.currentTimeMillis();
    	
    	 GL11.glPushMatrix();
         GL11.glDisable(3553);
         GL11.glEnable(2848);
         GL11.glEnable(2832);
         GL11.glEnable(3042);
         GL11.glShadeModel(GL11.GL_SMOOTH);
         GlStateManager.disableCull();
         GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
         
         
         final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
         final double y = (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.thePlayer.getEyeHeight() + 0.5 + (mc.thePlayer.isSneaking() ? -0.2 : 0);
         final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;
         
         
         Color c;

         final double rad = 0.65f;

         int q = 64;

         boolean increaseCount = false;
         
         q = 128;
         
         
         final double rotations = 0;
         
         
         
         
         for (float i = 0; i < Math.PI * 2 + (increaseCount ? 0.01 : 0); i += Math.PI * 4 / q) {
             final double vecX = x + rad * Math.cos(i + rotations);
             final double vecZ = z + rad * Math.sin(i + rotations);

             c = new Color(1,255,234);

             GL11.glColor4f(c.getRed() / 255.F,
                     c.getGreen() / 255.F,
                     c.getBlue() / 255.F,
                     0.8f
             );

             GL11.glVertex3d(vecX, y - 0.25, vecZ);

             GL11.glColor4f(c.getRed() / 255.F,
                     c.getGreen() / 255.F,
                     c.getBlue() / 255.F,
                     0.8f
             );

             GL11.glVertex3d(x, y, z);

         }
         
         
         
         GL11.glEnd();
         GL11.glShadeModel(GL11.GL_FLAT);
         GL11.glDepthMask(true);
         GL11.glEnable(2929);
         GlStateManager.enableCull();
         GL11.glDisable(2848);
         GL11.glEnable(2832);
         GL11.glEnable(3553);
         GL11.glPopMatrix();

         GL11.glColor3f(255, 255, 255);
         
	}
	
	public void onUpdate()
	{

	}
}
