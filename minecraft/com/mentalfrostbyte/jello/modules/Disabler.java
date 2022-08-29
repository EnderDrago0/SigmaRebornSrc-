package com.mentalfrostbyte.jello.modules;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.omg.PortableServer.THREAD_POLICY_ID;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.event.types.EventType2;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Disabler extends Module {
	//private ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Matrix", "Ghostly");
//	private BooleanValue verusSlientFlagApplyValue = new BooleanValue("VerusSilentFlagApply", false);
//	private NumberValue verusBufferSizeValue = new NumberValue("VerusBufferSize", 300, 0, 1000, 1);
//	private NumberValue verusRepeatTimesValue = new NumberValue("VerusRepeatTimes", 1, 1, 5 ,1);
//	private NumberValue verusRepeatTimesFightingValue = new NumberValue("VerusRepeatTimesFighting", 1, 1, 5, 1);
//	private NumberValue verusFlagDelayValue = new NumberValue("VerusFlagDelay", 40, 35, 60, 1);
	public Disabler() {
        super("Disabler", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
      //  this.addValue(mode);
     //   this.addValue(verusRepeatTimesValue);
     //   this.addValue(verusSlientFlagApplyValue);
    //    this.addValue(verusBufferSizeValue);
    //    this.addValue(verusRepeatTimesFightingValue);
    ////    this.addValue(verusFlagDelayValue);
      //  this.addValue(matrixNoCheck, matrixMoveFix, matrixMoveOnly, matrixNoMovePacket, matrixHotbarChange);
    }
	public void onEnable(){
	}
	
	public void onDisable(){
	}
	public void onUpdate()
	{
		if(!this.isToggled())
			return; 	
		
		
	}
	private boolean changed;
	@EventTarget
	public void e(EventMotion evv) {
		
	}
	@EventTarget
	public void onPs(EventPacketSent e) {
		
	}
	@EventTarget
	public void onPr(EventReceivePacket e) {
		
	}
}
