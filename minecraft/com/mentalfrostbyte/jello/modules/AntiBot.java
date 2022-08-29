package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.WorldChangeEvent;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class AntiBot extends Module {
	public static List<EntityLivingBase> bot = new ArrayList<EntityLivingBase>();
	private BooleanValue ticks = new BooleanValue("TicksExisted", true);
	private BooleanValue clearOnWorldChange = new BooleanValue("ClearOnWorldChange", true);
	public AntiBot() {
        super("AntiBot", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        this.addValue(ticks, clearOnWorldChange);
    }
   
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
		bot.clear();
	}
	
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
		  for(Entity bots : mc.thePlayer.getEntityWorld().loadedEntityList) {
	       	 if (bots instanceof EntityLivingBase) {
	       		EntityLivingBase bote = (EntityLivingBase) bots;
	       		double dis = bote.getDistanceToEntity(mc.thePlayer);
	       		if(bote.ticksExisted < 20 && bote.getDistanceToEntity(mc.thePlayer) > 0.1 && bote.getDistanceToEntity(mc.thePlayer) <  10) {
	       			if(!bot.contains(bote)) {
	       				bot.add(bote);
	       			}
	       		}
	       	 }
		  }
	   }
	
	@EventTarget
	public void worldChange(WorldChangeEvent e) {
		if(clearOnWorldChange.isEnabled()) {
			bot.clear();
		}
	}
	}
