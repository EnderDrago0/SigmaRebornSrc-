package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventEntityStep;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventPreMotionUpdates;
import com.mentalfrostbyte.jello.event.events.EventSlowDown;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.event.types.EventType;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class NoSlow extends Module {

	
	public static double moveSpeed;
	private TimerUtil timer = new TimerUtil();
	private long delay;
	private boolean s, blocking;
	private ModeValue mode = new ModeValue("Mode", "Delay", "Delay", "NCP", "Matrix");
    private boolean m;
    private ArrayList<Packet> packets = new ArrayList<>();
    private boolean cana;
    private boolean lastBlockingStat = false;
	public NoSlow() {
        super("NoSlow", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
        this.addValue(mode);
    }
   
	public void onEnable(){
		EventManager.register(this);
		timer.reset();
	}
	
	public void onDisable(){
		EventManager.unregister(this);
		timer.reset();
	}
	
	//BRAKES
	@Override
	public void onUpdate() {
		switch(mode.getMode()) {
		case"Delay":
			if (!mc.thePlayer.isBlocking()) s = false;

            if (mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 5 == 0 && s) {
                mc.playerController.syncCurrentPlayItem();
                mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

                s = false;
            }

            if (mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 5 == 1 && !s) {
                mc.playerController.syncCurrentPlayItem();
                mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                s = true;
            }
            break;
		case"Matrix":
			if(mc.thePlayer.isBlocking() || lastBlockingStat) {
			if(timer.hasTimeElapsed(230, false) && m) {
				m = false;
				 mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
				 if(!packets.isEmpty()) {
					 boolean canA = false;
					 for(Packet p : packets) {
	                	if(p instanceof C03PacketPlayer) {
	                		cana = true;
	                	}
	                	if(!((p instanceof C02PacketUseEntity || p instanceof C0APacketAnimation) && !cana)) {
	                		mc.thePlayer.sendQueue.addToSendQueueNoEvent(p);
	                	}
	                }
					 packets.clear();
				 }
			}
			if(!m) {
				lastBlockingStat = mc.thePlayer.isBlocking();
				if(!mc.thePlayer.isBlocking()) 
					return;
				   m = true;
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
				timer.reset();
			}
			}
			
			break;
		}
	}
	@EventTarget
	public void p(EventPacketSent e) {
		switch(mode.getMode()) {
		case"Matrix":
			if(!m)
				return;
			if((e.getPacket() instanceof C07PacketPlayerDigging || e.getPacket() instanceof C08PacketPlayerBlockPlacement) && mc.thePlayer.isBlocking())
				e.setCancelled(true);
			else if(e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C0APacketAnimation || e.getPacket() instanceof C0BPacketEntityAction || e.getPacket() instanceof C02PacketUseEntity || e.getPacket() instanceof C07PacketPlayerDigging || e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
				packets.add(e.getPacket());
				e.setCancelled(true);
			}
			
			break;
		}
	}
	
	@EventTarget
    public void onSlowDown(EventSlowDown e) {
		
           // if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
             //   e.setCancelled(false);
           //     return;
           // }
        e.setCancelled(true);
        
    }
    
	
}
