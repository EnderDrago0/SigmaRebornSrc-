package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import javax.xml.crypto.AlgorithmMethod;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class MatrixShit extends Module {
	
	
	
	public MatrixShit() {
        super("MatrixShit", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
    }
	
	public void onEnable(){
		 pendingFlagApplyPacket = false;
		EventManager.register(this);
	}
	
	public void onDisable(){
		 pendingFlagApplyPacket = false;
		EventManager.unregister(this);
	}
	
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
	}
	public boolean pendingFlagApplyPacket;
	private double lastMotionX = 0.0;
	private double lastMotionY = 0.0;
	private double lastMotionZ = 0.0;
	@EventTarget
	public void onp(EventReceivePacket e) {
		if(mc.thePlayer == null || mc.theWorld == null)
            return;
		
		if(e.getPacket() instanceof S08PacketPlayerPosLook) {
			pendingFlagApplyPacket = true;
			Jello.addChatMessage("FLAGGED");
			lastMotionX = mc.thePlayer.motionX;
		    lastMotionY = mc.thePlayer.motionY;
		    lastMotionZ = mc.thePlayer.motionZ;
		}
		
	}
	@EventTarget
	public void onps(EventPacketSent e) {
		if(mc.thePlayer == null || mc.theWorld == null)
            return;
		
		//System.out.println("ggggggggggggggggg");
		
		if(e.getPacket() instanceof C06PacketPlayerPosLook && pendingFlagApplyPacket) {
			pendingFlagApplyPacket = false;
			Jello.addChatMessage("SETTING MOTION");
			mc.thePlayer.motionX = lastMotionX;
	        mc.thePlayer.motionY = lastMotionY;
	        mc.thePlayer.motionZ = lastMotionZ;
	            
			
		}
	}
}
