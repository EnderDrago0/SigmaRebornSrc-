package com.mentalfrostbyte.jello.modules;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventPreMotionUpdates;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.event.types.EventType2;
import com.mentalfrostbyte.jello.hud.NotificationManager;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Speed extends Module {
//serxphine
	//sakuralamb
	//Yorukaa
	//Orageuse
	private BooleanValue motReset = new BooleanValue("Motion Reset", true);
	public TimerUtil timer = new TimerUtil();
	public static boolean enabled;
	
	private double moveSpeed;
	  private double lastDist;
	  public static int stage;
	  private BooleanValue t = new BooleanValue("TimerBalance", false);
	  private BooleanValue autoDisable = new BooleanValue("AutoDisable", true);
	  public NumberValue speed;
	  public ModeValue mode;
	
	public Speed() {
        super("Speed", Keyboard.KEY_V);
        this.jelloCat = Jello.tabgui.cats.get(0);
        speed = new NumberValue("Speed", 1, 0.1, 20, 0.1);
        addValue(speed);
        mode = new ModeValue("Mode", "Vanilla", "Vanilla", "MatrixSemiStrafe", "MatrixTimerBalance", "MatrixMultiply", "MatrixDynamic", "Strafe");
        addValue(mode, motReset, t, autoDisable);
    }
	MovementUtil mu = new MovementUtil();
	
	public void onEnable(){
		mc.thePlayer.jumpMovementFactor = 0.02f;
		EventManager.register(this);
		enabled = true;
		if (mc.thePlayer != null) {
	      this.moveSpeed = mu.getBaseMoveSpeed();
	    }
	    this.lastDist = 0.0D;
	    stage = 2;
	    mc.timer.timerSpeed = 1.0F;
	}
	
	public void onDisable(){
		mc.thePlayer.jumpMovementFactor = 0.02f;
		EventManager.unregister(this);
		enabled = false;
		 mc.timer.timerSpeed = 1.0f;
		 if(motReset.isEnabled()) {
			 mc.thePlayer.motionX = 0;
			 mc.thePlayer.motionZ = 0;
		 }
	}
	@EventTarget
	public void M(EventMotion e)
	{
	  if(!this.isToggled())
		  return; 
	  
	  if(e.getType() == EventType2.POST)
		  return;
	  	switch (mode.getMode()) {
		case "Vanilla":
			mu.strafe((float) speed.getValue(), mc.thePlayer.rotationYaw);
			if(mc.thePlayer.onGround && mc.thePlayer.isMoving()) 
				mc.thePlayer.jump();
			break;

		case"MatrixSemiStrafe":
			
			if(mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				mu.strafe(0.30, mc.thePlayer.rotationYaw);
			}
			if(mc.thePlayer.fallDistance > 0.1) {
				mu.strafe(0.22, mc.thePlayer.rotationYaw);
			}else {
				
			}
			break;
			
		case"MatrixTimerBalance":
			if(mc.thePlayer.isMoving()) {
			 if (mc.thePlayer.onGround)
				 mc.thePlayer.jump();
		        else {
		            if (mc.thePlayer.fallDistance <= 0.1)
		                mc.timer.timerSpeed = 1.9f;
		            else if (mc.thePlayer.fallDistance < 1.3)
		                mc.timer.timerSpeed = 0.6f;
		            else
		                mc.timer.timerSpeed = 1f;
		        }
			}else {
				mc.timer.timerSpeed =1;
			}
			break;
		case"MatrixMultiply":
			mc.timer.timerSpeed = 1.2f;
	        if (mc.thePlayer.isMoving()) {
	            if (mc.thePlayer.onGround) {
	                mc.timer.timerSpeed = 1.0f;
	                mc.thePlayer.jump();
	            }
	            
	             if (mc.thePlayer.motionY > 0.003) {
	                mc.thePlayer.motionX *= 1.0012;
	                mc.thePlayer.motionZ *= 1.0012;
	                mc.timer.timerSpeed = 1.05f;
	             }
	             
	        }
	        break;
		case"Strafe":
			mu.strafe(mu.getBaseMoveSpeed(), mc.thePlayer.rotationYaw);
			if(mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
				mc.thePlayer.jump();
			}
			break;
		case"MatrixDynamic":
			if(!KillAura.attacking) {
				 if (mc.thePlayer.isMoving()) {
		            if (mc.thePlayer.onGround) {
		                mc.timer.timerSpeed = 1.0f;
		                mc.thePlayer.jump();
		            }
		            
		             if (mc.thePlayer.motionY > 0.003) {
		                mc.thePlayer.motionX *= 1.0012;
		                mc.thePlayer.motionZ *= 1.0012;
		                mc.timer.timerSpeed = 1.05f;
		             }
		             
			     }
			}else {
				
				if(mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
					mc.thePlayer.jump();
					mu.strafe(0.30, mc.thePlayer.rotationYaw);
				}
				if(mc.thePlayer.fallDistance > 0.1) {
					mu.strafe(0.22, mc.thePlayer.rotationYaw);
				}else {
					
				}
			}
			break;
		}
	  }
	@EventTarget
	public void onre(EventReceivePacket e) {
		if(autoDisable.isEnabled()) {
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				NotificationManager.notify("Speed disabled due to flags", this.getDisplayName(), 700);
				this.toggle();
			}
		}
	}
}
