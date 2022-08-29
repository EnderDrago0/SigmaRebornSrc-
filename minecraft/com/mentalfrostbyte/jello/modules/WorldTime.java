package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.NumberValue;

public class WorldTime extends Module {
	public NumberValue time = new NumberValue("Time", 24000, 1, 24000, 1);
	public WorldTime() {
        super("WorldTime", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(4);
        this.addValue(time);
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
		mc.theWorld.setWorldTime((long)time.getValue());
	}
}
