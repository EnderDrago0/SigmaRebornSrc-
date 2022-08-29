package com.mentalfrostbyte.jello.modules;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.plaf.FontUIResource;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.font.JelloFontRenderer;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.FontUtil;
import com.mentalfrostbyte.jello.util.MathUtil;
import com.mentalfrostbyte.jello.util.Vec2f;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class NameTags extends Module {
	
	public NameTags() {
        super("NameTags", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
    }
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
	}
	@EventTarget
	public void onre(EventRender3D e) {
		
		
		
	}
	@EventTarget
	public void onre(EventRender2D e) {
		
	}
	
}
