package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;

import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {
	
	public ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla");
	public NumberValue packetAmmount = new NumberValue("RepeatTimes", 1, 1, 60, 1);
	public Regen() {
        super("Regen", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        this.addValue(mode);
        this.addValue(packetAmmount);
    }
   
	public void onEnable(){
		
	}
	
	public void onDisable(){
		
	}
	
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
		switch (mode.getMode()) {
		case "Vanilla":
			if(mc.thePlayer.getHealth() < 20) {
				repeat((int) packetAmmount.getValue());
				//Jello.addChatMessage("R");
			}
			break;
		}
	}
	private void repeat(int times) {
		for(int i = 0; i <= times; i++) {
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer());
		}
	}
}
