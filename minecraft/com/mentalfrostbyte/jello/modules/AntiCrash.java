package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class AntiCrash extends Module {
	
	public AntiCrash() {
        super("AntiCrash", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(6);
    }
   
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	@EventTarget
    public void on(EventReceivePacket e) {
    	if(e.getPacket() instanceof S08PacketPlayerPosLook) {
    		S08PacketPlayerPosLook s = (S08PacketPlayerPosLook)e.getPacket();
    		float t = 500;
    		if(s.yaw > 180 + t || s.yaw < -180 + -t|| s.pitch < -90 + -t|| s.pitch > 90+ t)
    			e.setCancelled(true);
    	}
    }
}
