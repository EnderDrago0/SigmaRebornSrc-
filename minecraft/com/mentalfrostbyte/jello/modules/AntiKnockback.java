package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3CPacketUpdateScore;

public class AntiKnockback extends Module {

	private Random rand = new Random();
	private TimerUtil timer = new TimerUtil();
	public static ModeValue mode = new ModeValue("Mode", "Reverse", "Cancel", "Reverse", "MatrixSimple", "Spoof");
	public AntiKnockback() {
        super("Velocity", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
        this.addValue(mode);
    }
    public void onEnable(){
		EventManager.register(this);
		timer.reset();
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	@Override
	public void onUpdate() {
		if(!this.isToggled())
			return;
		switch (mode.getMode()) {
		case "MatrixSimple":
			if (mc.thePlayer.hurtTime <= 0) {
	            return;
	        } 
	        if (mc.thePlayer.onGround) {
	            if (mc.thePlayer.hurtTime == 7) {
	                mc.thePlayer.motionX = 0;
	                mc.thePlayer.motionZ = 0;
	            } 
	            
	        } else if (mc.thePlayer.hurtTime == 7) {
	            mc.thePlayer.motionX = 0;
	            mc.thePlayer.motionZ = 0;
	       }
			break;

		default:
			break;
		}
    }
    @EventTarget
    public void onKb(EventReceivePacket e) {
    	switch (mode.getMode()) {
		case "Cancel":
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity v = (S12PacketEntityVelocity) e.getPacket();
				if(v.func_149412_c() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
				}
			}
			else if(e.getPacket() instanceof S27PacketExplosion) {
				e.setCancelled(true);
			}
			break;
		case"MatrixSimple":
			S12PacketEntityVelocity c = (S12PacketEntityVelocity) e.getPacket();
			
			break;
		case"Reverse":
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity v = (S12PacketEntityVelocity) e.getPacket();
				if(v.func_149412_c() == mc.thePlayer.getEntityId()) {
					mc.thePlayer.setVelocity(v.func_149411_d() / -8000.0D, v.func_149410_e() / 8000.0D, v.func_149409_f() / -8000.0D);
				}
			}
			break;
    	case"Spoof":
    		S12PacketEntityVelocity v = (S12PacketEntityVelocity) e.getPacket();
    		if(v.func_149412_c() == mc.thePlayer.getEntityId()) {
	    		e.setCancelled(true);
	    		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + v.field_149415_b / 8000.0, mc.thePlayer.posY + v.field_149416_c / 8000.0, mc.thePlayer.posZ + v.field_149414_d / 8000.0, false));
	    		break;
    		}
		}
	}
}
