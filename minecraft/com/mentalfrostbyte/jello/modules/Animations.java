package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

public class Animations extends Module {

	public Random rand = new Random();
	
	public TimerUtil timer = new TimerUtil();
	public static boolean en;
	public static ModeValue mode;
	public Animations() {
        super("Animations", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(4);
        mode = new ModeValue("Mode", "Leaked", "Jello", "Exhibition", "Slide", "OldSigma", "Leaked", "Exhibition2", "Slide2", "Swang");
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
	
	
}
