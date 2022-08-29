package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;

public class NoRotateSet extends Module {
	
	public NoRotateSet() {
        super("NoRotateSet", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
    }
	private Float yaw, pitch;
    private Float startingYaw, startingPitch;
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}

	@EventTarget
	public void p(EventReceivePacket p) {
		if(p.getPacket() instanceof S08PacketPlayerPosLook) {
			Jello.addChatMessage("Canceling rotation set");
			S08PacketPlayerPosLook pa = (S08PacketPlayerPosLook) p.getPacket();
			if(pa.pitch == 0 && pa.yaw == 0 || mc.getNetHandler().doneLoadingTerrain)
				return;
				yaw = pa.yaw;
                pitch = pa.pitch;

                pa.yaw = mc.thePlayer.rotationYaw;
                pa.pitch = mc.thePlayer.rotationPitch;
			
		}
	}
}
