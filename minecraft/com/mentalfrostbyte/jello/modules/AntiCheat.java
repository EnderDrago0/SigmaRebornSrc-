package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.RotationUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class AntiCheat extends Module {
	
	public AntiCheat() {
        super("AntiCheat", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
    }
	private TimerUtil timerFly = new TimerUtil();
   
	public void onEnable(){
		ticksig = 0;
	}
	
	public void onDisable(){
		time.reset();
	}
	
    private int ticksig;
    private int shitidk;
    private float yaw;
    private float pitch;
    private float n;
    
	private boolean haslockedaway;
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
		ticksig++;
		//if(mc.thePlayer.motionY != 0)
		//Jello.addChatMessage(String.valueOf(mc.thePlayer.motionY));
		TimerUtil time = new TimerUtil();
		RotationUtil rot = new RotationUtil();
		MovementUtil mu = new MovementUtil();
		for(Entity e : mc.thePlayer.getEntityWorld().loadedEntityList) {
			if(e instanceof EntityPlayer) {
				EntityPlayer entity = (EntityPlayer) e;
				if(entity == mc.thePlayer)
					return;
				//if(entity.getName() == mc.thePlayer.getName())
				//	return;
			//	mc.thePlayer.sendChatMessage("/help");
				
				if(entity.onGround) {
					if(mu.getSpeedOfEntity(entity) > 0.40) {
						Jello.addChatMessage(entity.getName() + " failed speed A");
					}
				}
				if(entity.ticksExisted > 0) {
					if(mu.getSpeedOfEntity(entity) > 0.2873) {
						if(entity.onGround) {
							shitidk = 0;
							ticksig = 1;
						}
						if(ticksig < 4) {
							
						}else {
						//	Jello.addChatMessage(entity.getName() + " failed speed B");
						}
					}
				}
				if(mc.thePlayer.getLastAttacker() != null) {
					//mc.thePlayer.getLastAttacker().renderYawOffset = RotationUtil.faceEntityAc(mc.thePlayer, mc.thePlayer.getLastAttacker(), 1, 1)[0];
				}
			}
		}
	}
}
