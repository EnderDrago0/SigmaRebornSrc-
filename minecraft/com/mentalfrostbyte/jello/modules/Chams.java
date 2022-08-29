package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRenderEntity;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;

public class Chams extends Module {
	private ModeValue mode = new ModeValue("Mode", "Normal", "Normal","CSGO");
	public Chams() {
        super("Chams", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(4);
        this.addValue(mode);
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
	public void ere(EventRenderEntity e) {
		GL11.glEnable(32823);
		GL11.glPolygonOffset(1.0f, -1100000.0f);
	}
}
