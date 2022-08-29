package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.RotationUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class HvHAura extends Module {
	
	public HvHAura() {
        super("HvHAura", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
    }
   
	public void onEnable(){
		EventManager.register(this);
	}
	
	public void onDisable(){
		mc.thePlayer.movementYaw = null;
		EventManager.unregister(this);
	}
	private List<EntityLivingBase> targetList = new ArrayList<EntityLivingBase>();
    
	public void onUpdate()
	{
		if(!this.isToggled())
			return;
		targetList.clear();
        for(Entity target : mc.thePlayer.getEntityWorld().loadedEntityList) {
        	 if (target instanceof EntityLivingBase) {
	        	if(target.getDistanceToEntity(mc.thePlayer) < 10) {
	        		EntityLivingBase entityLivingBase = (EntityLivingBase) target;
	        		if (entityLivingBase instanceof EntityPlayer && entityLivingBase != mc.thePlayer)
	        			targetList.add(entityLivingBase);
	        	}
        	 }
        }
		
	}
	@EventTarget
	public void render(EventRender3D rend) {
		
		if(!targetList.isEmpty()) {
			mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(targetList.get(0), Action.ATTACK));
			mc.thePlayer.swingItem();
			mc.thePlayer.renderYawOffset = RotationUtil.faceEntity(targetList.get(0), 2, 2)[0];
			mc.thePlayer.renderPitchRotation = RotationUtil.faceEntity(targetList.get(0), 2, 2)[1];
			//mc.thePlayer.rotationYawHead = RotationUtil.faceEntity(targetList.get(0), 2, 2)[0];
			mc.thePlayer.movementYaw = RotationUtil.faceEntity(targetList.get(0), 2, 2)[0];
		}else {
			mc.thePlayer.movementYaw = null;
		}
	}
}
