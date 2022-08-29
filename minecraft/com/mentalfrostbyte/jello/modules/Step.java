package com.mentalfrostbyte.jello.modules;

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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class Step extends Module {

	
	public static double moveSpeed;
	public TimerUtil timer = new TimerUtil();
	public static boolean enabled;
	private boolean resetNextTick;
    private double preY;
	public ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Matrix");
    
    
	public Step() {
        super("Step", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(0);
    }
   
	public void onEnable(){
		EventManager.register(this);
		enabled = true;
		this.addValue(mode);
		
	}
	
	public void onDisable(){
		EventManager.unregister(this);
		enabled = false;
	}
	
    
    //STEP
    @EventTarget
    public void onStep(EventEntityStep event) {	
		
		
    }
	@EventTarget
    public void onPostTick(EventTick event) {
        if (this.resetNextTick) {
        	 //mc.timer.timerSpeed = 1.0f;
            this.resetNextTick = false;
        }
	
    }
	private boolean canStep() {
        if (this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.onGround && this.mc.thePlayer.motionY < 0.0 && !this.mc.thePlayer.movementInput.jump) {
            return true;
        }
        return false;
    }
    
	
}
