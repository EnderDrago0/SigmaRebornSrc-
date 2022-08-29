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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Tracers extends Module {
	
	public Tracers() {
        super("Tracers", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(4);
    }
   
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	
	
	@EventTarget
	public void rend(EventRender3D event) {
		 for (final Entity playerEntity : mc.thePlayer.getEntityWorld().loadedEntityList) {
	            if (playerEntity != mc.thePlayer && !playerEntity.isDead && !playerEntity.isInvisible() && playerEntity instanceof EntityPlayer) // Distance check to fix a bug where it renders players far away that have been rendered before
	                drawToPlayer(playerEntity);
	        }
	        this.color(Color.WHITE);
	}
	
	public void color(Color color) {
        if (color == null)
            color = Color.white;
        GL11.glColor4d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }
    
	public void drawToPlayer(final Entity entity) {
        final Color color = new Color(255, 255, 222);

        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;

        final double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().viewerPosX;
        final double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().viewerPosY;
        final double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().viewerPosZ;

        render(red, green, blue, xPos, yPos, zPos);
    }
	public void render(final float red, final float green, final float blue,
            final double x, final double y, final double z) {
drawTracerLine(x, y, z, red, green, blue, 0.5F, 1.5F);
}
	public static void drawTracerLine(final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha, final float lineWidth) {
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(lineWidth);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(2);
		GL11.glVertex3d(0.0D, mc.thePlayer.getEyeHeight(), 0.0D);
		GL11.glVertex3d(x, y, z);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_BLEND);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
