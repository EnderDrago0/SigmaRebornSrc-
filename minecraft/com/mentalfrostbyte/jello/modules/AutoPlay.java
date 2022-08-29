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
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.MathHelper;

public class AutoPlay extends Module {

	public Random rand = new Random();
	
	public TimerUtil timer = new TimerUtil();
	public static boolean en;
	public ModeValue mode;
	public boolean clicks;
	public AutoPlay() {
        super("AutoPlay", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
        mode = new ModeValue("Mode", "Outline", "Outline", "2d");
        addValue(mode);
    }
   
	public void onEnable(){
		en = true;
	}
	
	public void onDisable(){
		  en = false;
	}
	
    
	public void onUpdate()
	{
		  //en = false;
	}
	@EventTarget
    public void onKb(EventReceivePacket e) {
	 	 if (e.getPacket() instanceof S2FPacketSetSlot) {
	 		// e.setCancelled(true);
	 		 System.out.println("ss");
	 	 }
	}
  @EventTarget
  public void on(EventPacketSent ev) {
	  
  }
	
	
}
