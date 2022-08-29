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
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

public class FastUse extends Module {
	
	public TimerUtil timer = new TimerUtil();
	public ModeValue mode;
	public FastUse() {
        super("FastUse", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
        mode = new ModeValue("Mode", "Instant", "Instant", "Matrix");
        addValue(mode);
    }
   
	public void onEnable(){
	}
	
	public void onDisable(){
	}
	
    
	  public void onUpdate()
	  {
		  if(!this.isToggled())
			  return;
		  switch(mode.getMode()) {
		  case"Instant":
			  if(mc.thePlayer.isUsingItem() && (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion)) {
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
				  mc.playerController.onStoppedUsingItem(mc.thePlayer);
			  }
		  case"Matrix":
			  if(mc.thePlayer.isUsingItem() && (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion)) {
				  if(!Jello.getModule("Fly").isToggled() && !Jello.getModule("Speed").isToggled() && !Jello.getModule("LongJump").isToggled())
					  mc.timer.timerSpeed = 0.3f;
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				//  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				//  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				  //mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				  //mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				 // mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				  
			  }else {
				  if(!Jello.getModule("Fly").isToggled() && !Jello.getModule("Speed").isToggled() && !Jello.getModule("LongJump").isToggled())
					  mc.timer.timerSpeed = 1;
			  }
		  }
	  }
}
