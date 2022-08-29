package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.muffin.Muffin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.modules.BlockFly.BlockData;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class NoFall extends Module {
	
	public TimerUtil timer = new TimerUtil();
	public ModeValue mode;
	
	public NoFall() {
        super("NoFall", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
        mode = new ModeValue("Mode", "Matrix", "Matrix");
        addValue(mode);
    }
	public void onEnable(){
		EventManager.register(this);
		needSpoof = false;
	}
	
	public void onDisable(){
		needSpoof = false;
		EventManager.unregister(this);
	}
	private boolean packetModify;
	private boolean needSpoof;
	private int packet1Count;
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 
		switch (mode.getMode()) {
		case "Matrix":
			 if (mc.thePlayer.fallDistance > 4) {
              //   mc.thePlayer.motionY = -0.5;
                 mc.thePlayer.fallDistance = 0.0f;
                 mc.thePlayer.motionX = 0;
                 mc.thePlayer.motionZ= 0;
                 needSpoof = true;
             }

             if (mc.thePlayer.fallDistance / 3 > packet1Count) {
                 packet1Count = (int) (mc.thePlayer.fallDistance / 3);
                 packetModify = true;
             }
             if (mc.thePlayer.onGround) {
                 packet1Count = 0;
             }
			break;

		default:
			break;
		}
	}
	 @EventTarget
    public void onKb(EventReceivePacket e) {
	 	switch (mode.getMode()) {
		case "Matrix":
			
			break;

		default:
			break;
		}
	}
    @EventTarget
    public void on(EventPacketSent e) {
	 	switch (mode.getMode()) {
		case "Matrix":
			if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 4) {
                mc.thePlayer.motionY = 0.0;
                mc.thePlayer.fallDistance = 0.0f;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ= 0;
                needSpoof = true;
            }

            if (mc.thePlayer.fallDistance / 3 > packet1Count) {
                packet1Count = (int) (mc.thePlayer.fallDistance / 3);
                packetModify = true;
            }
            if (mc.thePlayer.onGround) {
                packet1Count = 0;
            }
			if(e.getPacket() instanceof C03PacketPlayer) {
				C03PacketPlayer c3 = (C03PacketPlayer) e.getPacket();
				if(needSpoof) {
					needSpoof = false;
					c3.setOnGround(true);
				}
			}
			break;
		}
    }
}
