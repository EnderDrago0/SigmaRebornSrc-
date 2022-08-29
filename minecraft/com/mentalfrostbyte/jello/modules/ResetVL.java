package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.sun.jna.ptr.DoubleByReference;

public class ResetVL extends Module {
	private int jumps;
	private double y;
	public ResetVL() {
        super("ResetVL", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
    }
   
	public void onEnable(){
		y = mc.thePlayer.posY;
		jumps = 0;
	}
	
	public void onDisable(){
		mc.timer.timerSpeed = 1F;
	}
	
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
	 if (jumps <= 25) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.11;
                jumps++;
            }
            mc.thePlayer.posY = y;
            mc.timer.timerSpeed = 2.25f;
        } else
            this.toggle();
	    
	}
}
