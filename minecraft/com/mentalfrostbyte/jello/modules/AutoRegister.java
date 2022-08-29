package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.network.play.server.S02PacketChat;

public class AutoRegister extends Module {
	
	public AutoRegister() {
        super("AutoRegister", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
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
	public void onP(EventReceivePacket e) {
		//Jello.addChatMessage("rechived");
		if(e.getPacket() instanceof S02PacketChat) {
			S02PacketChat p = (S02PacketChat) e.getPacket();
			if(p.func_148915_c().getUnformattedText().contains("register") || p.func_148915_c().getUnformattedText().contains("reg")) {
				Jello.addChatMessage("Attepting to register");
				mc.thePlayer.sendChatMessage("/register 4444aa 4444aa");
			}
			if(p.func_148915_c().getUnformattedText().contains("login")) {
				Jello.addChatMessage("Attepting to login");
				mc.thePlayer.sendChatMessage("/login 4444aa");
			}
		}
	}
}
