package com.mentalfrostbyte.jello.modules;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventPreMotionUpdates;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.PosLookInstance;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class LongJump extends Module {

	private PosLookInstance posLookInstance = new PosLookInstance();
	public TimerUtil timer = new TimerUtil();
	public static boolean enabled;
    public static float stage;
    
    public boolean wasOnGround;
    public boolean wasNotOnGround;
    private ModeValue mode = new ModeValue("Mode", "Matrix", "Matrix");
    private ModeValue t = new ModeValue("BypassMode", "None","None", "Motion", "Clip");
    private boolean hasFell;
    private boolean flagged;
    public LongJump() {
        super("LongJump", Keyboard.KEY_B);
        this.jelloCat = Jello.tabgui.cats.get(0);
        this.addValue(mode);
    }
    private float X;
    private float Y;
    private float Z;
	public void onDisable() {
		wasOnGround = false;
		timer.reset();
		flagged = false;
		hasFell = false;
		EventManager.unregister(this);
		mc.timer.timerSpeed = 1f;
	}
	private double lastMotX, lastMotY, lastMotZ;
	MovementUtil mu = new MovementUtil();
	private int flags;
	public void onEnable() {
		flags = 0;
		timer.reset();
		mc.timer.timerSpeed = 0.2f;
		posLookInstance.reset();
		EventManager.register(this);
		switch (mode.getMode()) {
		case "Matrix":
		//	mc.thePlayer.posZ = mc.thePlayer.posZ;
		//	mc.thePlayer.posY = mc.thePlayer.posY;
			//mc.thePlayer.posX = mc.thePlayer.posX;
			if(mc.thePlayer.onGround) {
				hasFell = true;
			    //mc.thePlayer.jump();
				//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ);
			}else if(mc.thePlayer.fallDistance > 0) {
				//hasFell = true;
			}
			
			break;
		}
	}
	public void onUpdate(){
		MovementUtil mo = new MovementUtil();
		if(!this.isToggled())
			return;
		if(flags == 2) {
			if(timer.hasTimeElapsed(500, true)) {
				mc.timer.timerSpeed = 1;
				//wasOnGround = true;
			}
			if(flags == 2) {
				wasOnGround = true;
			}
		}else {
			timer.reset();
		}
		
		if(wasOnGround && mc.thePlayer.onGround) {
			this.toggle();
		}
		switch (mode.getMode()) {
		case "Matrix":
			if(hasFell) {
				if(!flagged) {
					mo.strafe(3.4, 5);
					mc.thePlayer.motionY = 1.2;
				}
			}else {
				hasFell = true;
				if(t.is("Motion")) {
				//	mc.thePlayer.motionX *= 0.2;
               //    mc.thePlayer.motionZ *= 0.2;
                 //   if (mc.thePlayer.fallDistance > 0) {
                        hasFell = true;
                  //  }
				}
				if(t.is("Clip") && mc.thePlayer.motionY < 0) {
					hasFell = true;
				}
			}
			break;
		}
	}

	@EventTarget
	public void onpos(EventPacketSent ev) {
		switch (mode.getMode()) {
		case "Matrix":
			if(ev.getPacket() instanceof C06PacketPlayerPosLook && posLookInstance.equalFlag((C06PacketPlayerPosLook) ev.getPacket())) {
				posLookInstance.reset();
				MovementUtil mo = new MovementUtil();
				
                mc.thePlayer.motionX = lastMotX;
                mc.thePlayer.motionY = lastMotY;
                mc.thePlayer.motionZ = lastMotZ;
              //  mo.strafe(3.4, 5);
				//mc.thePlayer.motionY = 1;
      //          this.toggle();
			}
			break;

		default:
			break;
		}
	}

	@EventTarget
	public void S(EventReceivePacket ev) {
		switch (mode.getMode()) {
		case "Matrix":
			if(ev.getPacket() instanceof S08PacketPlayerPosLook && hasFell) {
				flagged = true;
				flags++;
				posLookInstance.set((S08PacketPlayerPosLook) ev.getPacket());
				MovementUtil mo = new MovementUtil();
				lastMotX = mc.thePlayer.motionX;
                lastMotY = mc.thePlayer.motionY;
                lastMotZ = mc.thePlayer.motionZ;
			//	mo.strafe(3.4, 5);
				//mc.thePlayer.motionY = 1;
			}
			break;
		}
	}
	
}
