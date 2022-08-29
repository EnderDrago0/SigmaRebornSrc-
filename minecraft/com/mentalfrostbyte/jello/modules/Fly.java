package com.mentalfrostbyte.jello.modules;

import java.awt.ImageCapabilities;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.event.types.EventType;
import com.mentalfrostbyte.jello.event.types.EventType2;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.modules.BlockFly.BlockData;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.ScriptUtils;
import com.mentalfrostbyte.jello.util.SettingsFile;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlime;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Fly extends Module {

	public Random rand = new Random();
	
	//private List packetBuffer = new LinkedList<Packet<INetHandlerPlayServer>>();
	private ArrayList<Packet> packets = new ArrayList<>();
	//minemora
	private ArrayList<Packet> mp = new ArrayList<>();
	public TimerUtil timer = new TimerUtil();
	private NumberValue speed;
	public static ModeValue mode;
	
	private NumberValue BoostSp = new NumberValue("SpeedBoost", 1.15, 0, 3, 0.1);
	private NumberValue boostTicks = new NumberValue("BoostTicks", 27,10,40, 1);
	public static boolean e;
	private BooleanValue smoothCam = new BooleanValue("SmoothCamera" ,true);
	private BooleanValue fakeDamage = new BooleanValue("FakeDamage", true);
	public Fly() {
        super("Fly", Keyboard.KEY_F);
        this.jelloCat = Jello.tabgui.cats.get(0);
        speed = new NumberValue("Speed", 1, 0.1, 8, 0.1);
        addValue(speed);
        mode = new ModeValue("Mode", "Blocksmc", "Vanilla", "Verus", "Blocksmc", "Matrix");
        addValue(mode);
        this.addValue(BoostSp);
        this.addValue(boostTicks, smoothCam, fakeDamage);
    }
	

	
	private double startY;
	
	private int bmcstage;
	public float sp;
	private double bmcy;
	public void onEnable(){
		bmcTimer = 0;
		boostMotion = 0;
		if(fakeDamage.isEnabled())
			mc.thePlayer.performHurtAnimation();
		startY = mc.thePlayer.posY;
		if(smoothCam.isEnabled())
			EntityPlayer.useCustomY(true);
		bmcslot = 0;
		packetss = 0;
		//mc.thePlayer.motionY = 0.7;
		timer.reset();
		bmcstage = 0;
		bmcy = mc.thePlayer.posY;
		//mc.thePlayer.jump();
		if(mode.is("Blocksmc")) {
			sp = (float) mc.thePlayer.posY;
			mc.thePlayer.jump();
		}
		e = true;
		EventManager.register(this);
		packets.clear();
		sp = 1;	
		tom.reset();
		tome.reset();
		//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.25, mc.thePlayer.posZ);
		//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.25, mc.thePlayer.posZ);
	}
	
	public void onDisable(){
		bmcTimer=0;
		bmcslot = 0;
		boostMotion =0;
		c03 = false;
		if(!mode.is("Matrix"))
			mc.thePlayer.motionY = 0;
		  mc.thePlayer.motionX = 0;
		  mc.thePlayer.motionZ = 0;
		
		startY = mc.thePlayer.posY;
		EntityPlayer.useCustomY(false);
		timer.reset();
		if(mode.is("Minemora")) {
			//packets.forEach(mc.thePlayer.sendQueue.getNetworkManager()::sendPacket);
			packets.clear();
			
		}
		e = false;
		sp = 1;
		  mc.timer.timerSpeed = 1f;
		  EventManager.unregister(this);
//		  mc.thePlayer.motionX = 0;
	//	  mc.thePlayer.motionZ = 0;
		//  mc.thePlayer.motionY = 0;
		  tom.reset();
		  tome.reset();
		  
	}
	private int bmcslot;
	TimerUtil tom = new TimerUtil();
	private BlockData target;
	public BlockData getTarget(BlockPos pos) {
        EnumFacing[] orderedFacingValues = new EnumFacing[] {
                EnumFacing.UP,
                EnumFacing.EAST,
                EnumFacing.NORTH,
                EnumFacing.WEST,
                EnumFacing.SOUTH,
                EnumFacing.DOWN
                
        };
        for (EnumFacing facing : orderedFacingValues) {
            BlockPos alteredPos = pos.add(facing.getOpposite().getDirectionVec());

            if (!mc.theWorld.getBlockState(alteredPos).getBlock().isReplaceable(mc.theWorld, alteredPos) && !(mc.theWorld.getBlockState(alteredPos).getBlock() instanceof BlockLiquid) && !(mc.theWorld.getBlockState(alteredPos).getBlock() instanceof BlockAir)) {
                return new BlockData(alteredPos, facing);
            }
        }

        return null;
	}
	TimerUtil tome = new TimerUtil();
	private double bmcTimer;
	@EventTarget
	  public void gg(EventMotion e)
	  {
		
		  EntityPlayer.setCameraY((float) startY);
		//System.sfout.println(BoostSp.getValue() + "s" + boostTicks.getValue());
		  MovementUtil mo = new MovementUtil();
		  TimerUtil toms = new TimerUtil();
		  if(!this.isToggled())
			  return;
		  if(e.getType() == EventType2.POST)
			  return;
		  switch(mode.getMode()) {
		  case"Minemora":
			  if(mc.thePlayer.fallDistance > 1) {
				  mc.thePlayer.fallDistance = 0;
				  mc.thePlayer.motionY = 0;
				  mc.thePlayer.motionX = 0;
				  mc.thePlayer.motionZ = 0;
			  }
			  break;
		  case"Matrix":
			  if (this.boostMotion == 0.0F) {
		          double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
		          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
		        //  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + Math.sin(-yaw) * 1.5D, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ + Math.cos(yaw) * 1.5D, false));
		          this.boostMotion = 1.0F;
		          mc.timer.timerSpeed = 0.1F;
		          break;
		        } 
		        if (this.boostMotion == 2.0F) {
		          mo.strafe(3.4, mc.thePlayer.rotationYaw);
		          mc.thePlayer.motionY = 1;
		          this.boostMotion = 3.0F;
		          break;
		        } 
		        if (this.boostMotion < 5.0F) {
		          this.boostMotion++;
		          break;
		        } 
		        if (this.boostMotion >= 5.0F)
		          mc.timer.timerSpeed = 1.0F; 
			  
			  break;
		  case"Blocksmc":
			  e.setPitch(90);
			  mc.thePlayer.renderPitchRotation = 90;
			  BlockPos blockBelowPlayer = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			  if(getTarget(blockBelowPlayer) != null)
				  target = getTarget(blockBelowPlayer);
			  if(bmcstage == 0 && target!=null) {
				//  Jello.addChatMessage("in");
				  mc.thePlayer.inventory.currentItem = getHotbarBlock();
				 // mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(getHotbarBlock()));
				  
				//  mc.timer.timerSpeed = 0.3f;
				  
				  if (mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld,  mc.thePlayer.getHeldItem(), target.pos, target.facing, new Vec3(target.pos.getX(), target.pos.getY(), target.pos.getZ()))) {
					 // Jello.addChatMessage("Place");
					  mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
					  bmcstage = 1;
				  }
				 // mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ))), 0, 0.94f, 0));
			  }
			  mo.strafe(0.27, mc.thePlayer.rotationYaw);
			  	if(mc.thePlayer.onGround && bmcstage == 1 || bmcstage == 2) {
			  		if(bmcslot == 0) {
			  			 mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(0));
				  		bmcslot =1;
			  		}
			  		mc.thePlayer.onGround = false;
			 // 	mc.timer.timerSpeed = 1.3f;
                  TimerUtil time = new TimerUtil();
                  if(tom.hasTimeElapsed(600, true)) {
                		bmcstage = 2;
                  }
                 // mc.thePlayer.onGround = false;
              }
              if(bmcstage == 2) {
                  mc.thePlayer.motionY = 0;
              //    bmcstage = 3;
                  //float
              }else {
            	  mc.thePlayer.motionX = 0;
				  mc.thePlayer.motionZ = 0;
              }
              if(mc.gameSettings.keyBindSprint.pressed) {
                  bmcy = 1;
              }else {
                  bmcy = 0;
              }
              if(mc.gameSettings.keyBindPlayerList.pressed) {
                  mc.thePlayer.motionY = 0;
              }
              if(bmcstage == 3) {
                  mc.timer.timerSpeed = 1f;
              }
             // mc.thePlayer.motionY = 0;
              if(time.hasTimeElapsed(200, true)) {
            	//  mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            	  mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            	  mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
              }// mc.thePlayer.motionX = 0;
              if(bmcTimer < 40) {
            	  bmcTimer+=1;
            	//  mc.timer.timerSpeed = 1f;
              }else {
            	 // mc.timer.timerSpeed = 1;
              }
              break;
		  case"Verus":
			  if(mc.thePlayer.isMoving()) {
				  mc.thePlayer.motionY = 0;
				 mo.strafe(mo.getBaseMoveSpeed(), mc.thePlayer.rotationYaw);
			  }else {
				  mc.thePlayer.motionY = 0;
				  mc.thePlayer.motionX = 0;
				  mc.thePlayer.motionZ = 0;
			  }
			  
			 
			  mc.thePlayer.motionY = 0;
			  if(mc.gameSettings.keyBindJump.pressed)
					 mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.8, mc.thePlayer.posZ);
				 if(mc.gameSettings.keyBindSneak.pressed)
					 mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.8, mc.thePlayer.posZ);
			 // if(mode.is("Vulcan")) {
				//  mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 4, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
			//	  mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 3, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
			//  }
			  mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
		  
			  break;
			  
		  case"Vanilla":
			  mc.thePlayer.motionX = 0;
			  mc.thePlayer.motionZ = 0;
			  mc.thePlayer.motionY = 0;
			  mo.strafe(speed.getValue(),mc.thePlayer.rotationYaw);
			 if(mc.gameSettings.keyBindJump.pressed)
				 mc.thePlayer.motionY = 2;
			 if(mc.gameSettings.keyBindSneak.pressed)
				 mc.thePlayer.motionY = -2;
			  break;
		  }
		
	  }
    private boolean lastValue;
    private boolean teleport;

	private float boostMotion = 0;
	  private MovementUtil mout = new MovementUtil();
	  private int packetss;
	  public double lastX;
	  public double lastY;
	  public double lastZ;
	  private boolean pendingFlagApplyPacket;
	  	@EventTarget
	    public void onKb(EventReceivePacket e) {
	  		MovementUtil mo = new MovementUtil();
	  		switch(mode.getMode()) {
	  		case"Matrix":
	  			if (e.getPacket() instanceof S08PacketPlayerPosLook) {
	  	          S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)e.getPacket();
	  	          mc.thePlayer.setPosition(packet.x, packet.y, packet.z);
	  	          mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, false));
	  	          if (this.boostMotion == 1.0F)
	  	            this.boostMotion = 2.0F; 
	  	          e.setCancelled(true);
	  	        } 
	  		}
		}
	  	private boolean c03;
	  	private boolean c04;
	  	private boolean c05;
	  	private boolean c06;
	  	
	  @EventTarget
	  public void on(EventPacketSent ev) {
		  switch (mode.getMode()) {
		case "Verus":
			break;
		}
	  }
	  
	  
	  private float groundTimer = 900;
	  private void handleVanillaKickBypas() {
	        if (System.currentTimeMillis() - groundTimer < 1000) return;

	        final double x = mc.thePlayer.posX;
	        final double y = mc.thePlayer.posY;
	        final double z = mc.thePlayer.posZ;

	        final double ground = calculateGround();

	        for (double posY = y; posY > ground; posY -= 8D) {
	            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, posY, z, true));

	            if (posY - 8D < ground) break; // Prevent next step
	        }

	        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, ground, z, true));


	        for (double posY = ground; posY < y; posY += 8D) {
	            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, posY, z, true));

	            if (posY + 8D > y) break; // Prevent next step
	        }

	        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));

	        groundTimer = System.currentTimeMillis();
	    }
	  public class BlockData
	  {
	    public BlockPos pos;
	    public EnumFacing facing;
	    
	    private BlockData(BlockPos paramBlockPos, EnumFacing paramEnumFacing)
	    {
	      this.pos = paramBlockPos;
	      this.facing = paramEnumFacing;
	    }
	  }
	    public double calculateGround() {
	        final double y = mc.thePlayer.posY;

	        final AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
	        double blockHeight = 1D;

	        for (double ground = y; ground > 0D; ground -= blockHeight) {
	            final AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);

	            if (mc.theWorld.checkBlockCollision(customBox)) {
	                if (blockHeight <= 0.05D)
	                    return ground + blockHeight;

	                ground += blockHeight;
	                blockHeight = 0.05D;
	            }
	        }

	        return 0F;
	    }
	    private int getHotbarBlock() {
	        for (int index = 36; index < 45; index++) {
	            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
	            if (itemStack != null) {
	                if (itemStack.getItem() instanceof ItemBlock) {
	                    if (((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockSlime) {
	                    	
	                    }else {
	                    	continue;
	                    }
	                    if (itemStack.stackSize >= 1) {
	                        return index - 36;
	                    }
	                }
	            }
	        }

	        return 0;
	    }
}
