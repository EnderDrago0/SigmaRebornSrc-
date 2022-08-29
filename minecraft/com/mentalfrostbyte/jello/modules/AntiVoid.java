package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtils;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class AntiVoid extends Module {

	public Random rand = new Random();
	
	private TimerUtil timer = new TimerUtil();
	private NumberValue dis;
	private ModeValue mode = new ModeValue("Mode", "TPBack", "TPBack", "MovementFlag");
	private BooleanValue checkVoid = new BooleanValue("VoidCheck", true);
	public AntiVoid() {
        super("AntiVoid", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
        dis = new NumberValue("Distance", 10, 1, 20, 1);
        this.addValue(mode, dis,checkVoid);
    }
	private double x;
	private double y;
	private double z;
	
   
	public void onEnable(){
		currentBlockCheck = 0;
	}
	
	public void onDisable(){
		currentBlockCheck = 0;
	}
	
    private int currentBlockCheck;
    private boolean noBlock;
	  public void onUpdate()
	  {
		  if(!this.isToggled()) 
			  return;
		  if(checkVoid.isEnabled()) {
			  BlockPos blockBelowPlayer = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - currentBlockCheck, mc.thePlayer.posZ);	
				for(int i = 0; i <= 30; i++) {
					currentBlockCheck++;
					noBlock = true;
					if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - currentBlockCheck, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
						noBlock = false;
					}
				}
		  }
		  
		  switch(mode.getMode()) {
		  
		  case "MovementFlag":
			  if(PlayerUtils.isBlockUnder()&& checkVoid.isEnabled())
				  return;
			  if(mc.thePlayer.fallDistance > dis.getValue() && mc.thePlayer.onGround == false && !PlayerUtils.isBlockUnder()) {
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
				  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ, true));
			  }
			  break;
			  
		  case "TPBack":
			  if(PlayerUtils.isBlockUnder() && checkVoid.isEnabled())
				  return;
			  if(mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
				  x = mc.thePlayer.posX;
				  y = mc.thePlayer.posY;
				  z = mc.thePlayer.posZ;
			  }
			  if(mc.thePlayer.fallDistance > dis.getValue() && mc.thePlayer.onGround == false && mc.thePlayer.isCollidedVertically == false) {
				  mc.thePlayer.setPosition(x, y, z);
				  mc.thePlayer.onGround = true;
			  }
			  break;
			  
		  }
	  }
	  private boolean checkForVoid() {
		  int i = (int) (-(mc.thePlayer.posY-1.4857625));
		  boolean dangerous = true;
		  while (i <= 0) {
			  dangerous = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() == Blocks.air;
			  i++;
			  if (!dangerous)
				  break;
		  }
		  return dangerous;
	  }
}
