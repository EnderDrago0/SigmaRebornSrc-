package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventAttack;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
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

public class Criticals extends Module {

	private Random rand = new Random();
	private float d;
	private boolean di;
	private TimerUtil timer = new TimerUtil();
	private ModeValue mode;
	private MovementUtil mu = new MovementUtil();
	public Criticals() {
        super("Criticals", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        mode = new ModeValue("Mode", "Packet", "Packet", "IDK");
        addValue(mode);
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
		  switch(mode.getMode()) {
		  case"Packet":
			  break;
		  case"IDK":
			  
			  break;
		  }
	  }	
	  @EventTarget
	  public void onA(EventAttack e) {
		  switch(mode.getMode()) {
		  case"Packet":
              if (mc.thePlayer.onGround) {
            	  double off = 0.0626;
          		double x = mc.thePlayer.posX; double y = mc.thePlayer.posY; double z = mc.thePlayer.posZ;
          		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y+off, z, false));
          		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
              }
              break;
		  case"IDK":
			  
			  break;
		  }
	  }
}
