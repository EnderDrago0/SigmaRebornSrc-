package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.RenderingUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MathHelper;

public class Blink extends Module {
	
	private ArrayList<Packet> packets = new ArrayList<>();
	private TimerUtil timer = new TimerUtil();
	private BooleanValue pulse = new BooleanValue("Pulse", false);
	private NumberValue pulseDelay = new NumberValue("PulseDelay", 300, 10, 1000, 1);
	public Blink() {
        super("Blink", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        this.addValue(pulse);
        this.addValue(pulseDelay);
    }
   
	public void onEnable(){
		EventManager.register(this);
		packets.clear();
	}
	
	public void onDisable(){
		EventManager.unregister(this);
		if(mc.theWorld != null) {
			packets.forEach(mc.thePlayer.sendQueue.getNetworkManager()::sendPacket);
			packets.clear();
		  }
	}
	@Override
	public void onUpdate(){
		if(mc.theWorld != null) {
			 if(timer.hasTimeElapsed((long) pulseDelay.getValue(), true) && pulse.isEnabled() && !packets.isEmpty()) {
				  packets.forEach(mc.thePlayer.sendQueue.getNetworkManager()::sendPacketSilent);
				  packets.clear();
				  timer.reset();
			  }
		}
	}
	
		@EventTarget
	  public void onpos(EventPacketSent ev) {
		  if (ev.getPacket() instanceof C03PacketPlayer || ev.getPacket() instanceof C04PacketPlayerPosition || ev.getPacket() instanceof C08PacketPlayerBlockPlacement || ev.getPacket() instanceof C06PacketPlayerPosLook || ev.getPacket() instanceof C05PacketPlayerLook) {
		 		 packets.add(ev.getPacket());
		 		 ev.setCancelled(true);
		 }
	  }
}
