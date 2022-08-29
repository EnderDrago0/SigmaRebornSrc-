package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.NumberValue;

public class AutoBlock extends Module {
	private NumberValue blockTick = new NumberValue("BlockTick", 9 ,1, 10 ,1);
	private NumberValue unblockTick = new NumberValue("UnblockTick" , 8 , 1, 10 ,1);
	public AutoBlock() {
        super("AutoBlock", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
        this.addValue(blockTick, unblockTick);
        
    }
   
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	@EventTarget
	public void onRender(EventRender2D event) {
		if(!this.isToggled() || Jello.getModule("KillAura").isToggled())
			return; 
		Autoclicker autoclicker = new Autoclicker();
        if(mc.thePlayer.getLastAttacker() != null) {
        	if(mc.thePlayer.getLastAttacker().hurtTime == 9 && mc.thePlayer.getDistanceToEntity(mc.thePlayer.getLastAttacker()) < 7) {
        		autoclicker.sendClick(1, true);
        	}
        	if(mc.thePlayer.getLastAttacker().hurtTime == 9)
        		autoclicker.sendClick(1, false);
        }
	}
    
	public void onUpdate()
	{
		
	}
}
