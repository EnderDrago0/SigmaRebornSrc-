package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

public class Sprint extends Module {
	
	public static BooleanValue multi;
	public Sprint() {
        super("Sprint", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
        multi = new BooleanValue("OmniSprint", false);
        this.addValue(multi);
    }
   
	public void onEnable(){
		
	}
	
	public void onDisable(){
		
	}
	
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
		if(!mc.thePlayer.isUsingItem() && mc.thePlayer.isMoving())
			mc.thePlayer.setSprinting(true);
	}
}
