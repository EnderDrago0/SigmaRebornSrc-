package com.mentalfrostbyte.jello.modules;

import java.util.Arrays;
import static java.lang.Double.isNaN;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.event.events.EventRender3D;
import com.mentalfrostbyte.jello.event.types.EventType2;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.MovementUtil;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockFly extends Module {


	private float p;
	private TimerUtil tieeg = new TimerUtil();
	private TimerUtil timer = new TimerUtil();
	private  TimerUtil timerMotion = new TimerUtil();
	private final BooleanValue eagle = new BooleanValue("Eagle", false);
	private final NumberValue delay = new NumberValue("Delay", 0, 0, 20, 1);
	private final BooleanValue keepY = new BooleanValue("KeepY", false);
	private final ModeValue towerMode = new ModeValue("TowerMode", "None", "None", "Matrix");
	public float yaw;
	public float pitch;
	public int startSlot;
	BlockPos currentBlockPos = new BlockPos(-1, -1, -1);
	public static boolean ena;
	private static List<Block> invalid = Arrays.asList(new Block[] { Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
            Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
            Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox,
            Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever });
	private float rotation = 999.0F;
	int itemStackSize;
	  int currentSlot;
	  int currentItem;
	  //private BlockData blockData;
	  boolean placing;
	  private int slot;
	  public static float progressYaw;
		public static float progressPitch;
		public boolean grounded;
		public boolean headTurned;
		public static boolean safewalk = false;
		private float lastY;
		private float lastX;
		private float needY;
		private float needX;
		
		private ModeValue rotationsType;
		//private ModeValue mode;
		private static BooleanValue sprint;
		public static BooleanValue safeWalk = new BooleanValue("SafeWalk", true);
	//	public BooleanValue keepY = new BooleanValue("KeepY", true);
		private int blockstile;
		private float[] facerots;
	public BlockFly() {
        super("BlockFly", Keyboard.KEY_C);
        this.jelloCat = Jello.tabgui.cats.get(0);
      //  mode = new ModeValue("Mode", "NewTest", "Jello" ,"NewTest");
       // this.addValue(mode);
        rotationsType = new ModeValue("RotationsType", "Keep", "Keep", "Snap", "Down", "FaceBlock");
        this.addValue(rotationsType, towerMode);
        sprint = new BooleanValue("Sprint", false);
        this.addValue(delay);
        this.addValue(sprint);
        this.addValue(keepY);
        this.addValue(safeWalk);
        this.addValue(eagle);
       }
	double normalise( double value,  double start,  double end ) 
	{
	   double width       = end - start   ;   // 
	   double offsetValue = value - start ;   // value relative to 0

	  return ( offsetValue - ( Math.floor( offsetValue / width ) * width ) ) + start ;
	}
	public void onEnable(){
		slot = mc.thePlayer.inventory.currentItem;
        didChange = false;
		blockstile = 0;
		tieeg.reset();
		startY = mc.thePlayer.posY;
		grounded = mc.thePlayer.onGround;
		headTurned = false;
        yaw = mc.thePlayer.rotationYaw;
          pitch = mc.thePlayer.rotationPitch;
		//progressYaw = mc.thePlayer.rotationYaw;
		//yaw = mc.thePlayer.rotationYaw;
		//progressYaw = (float)this.normalise(mc.thePlayer.rotationYaw, -180, 180);
		//yaw = (float)this.normalise(mc.thePlayer.rotationYaw, -180, 180);
		//startSlot = Jello.core.player().inventory.currentItem;
		//Jello.core.player().inventory.currentItem = this.getBlockSlot();
          if(safeWalk.isEnabled())
        	  safewalk = true;
		ena = true;
		EventManager.register(this);
		this.rotation = 999.0F;
		//String server = mc.isSingleplayer() == true ? "":mc.getCurrentServerData().serverIP;
		//Jello.addChatMessage(server);
	}
	public MovementUtil mu = new MovementUtil();
	public void onDisable(){
		 didChange = false;
        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
		mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		safewalk = false;
		  mc.gameSettings.keyBindSneak.pressed = false;
		grounded = mc.thePlayer.onGround;
		headTurned = false;
        yaw = mc.thePlayer.rotationYaw;
          pitch = mc.thePlayer.rotationPitch;
		//Jello.core.player().inventory.currentItem = startSlot;
		EventManager.unregister(this);
		//timer.reset();
		ena = false;
		mc.timer.timerSpeed = 1.0F;
	    if (mc.thePlayer.isSwingInProgress)
	    {
	      mc.thePlayer.swingProgress = 0.0F;
	      mc.thePlayer.swingProgressInt = 0;
	      mc.thePlayer.isSwingInProgress = false;
	    }
	    mc.thePlayer.inventory.currentItem = slot;
	}
	//for new test
	private BlockData target;

    private int ticker;
    private int lastPlacedY;
    
    private boolean is;
    
    private int slota;
    
    private float x;
    private float y;
    private double startY;
    private float placeY;
    private boolean didChange;
	@EventTarget
	public void onMotion(EventMotion e){
		if(false) {
		int k = 0;
		if (hasBlocksInHotbar())
	      {
	        ItemStack localObject1 = new ItemStack(Item.getItemById(261));
	        for (int i = 9; i < 36; i++) {
	          if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
	          {
	            
	            if (((mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock)) && (isValidItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())))
	            {
	              for (int m = 36; m < 45; m++) {
	                if (Container.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(m), (ItemStack)localObject1, true))
	                {
	                  swap(i, m - 36);
	                  k++;
	                  break;
	                }
	              }
	              if (k != 0) {
	                break;
	              }
	              swap(i, 7); break;
	            }
	          }
	        }
	      }
	      boolean bool1 = true;
	      boolean bool2 = true;
	      
	      mc.thePlayer.field_175165_bM = 999.0F;
	      if (!hasBlocks()) {
	        return;
	      }
	      double d1 = mc.thePlayer.posX;double d2 = mc.thePlayer.posZ;
	      double d3 = mc.thePlayer.movementInput.moveForward;
	      double d4 = mc.thePlayer.movementInput.moveStrafe;
	      float f = mc.thePlayer.rotationYaw;
	      if (!mc.thePlayer.isCollidedHorizontally) {
	        
	          d1 += (d3 * 0.45D * Math.cos(Math.toRadians(f + 90.0F)) + d4 * 0.45D * Math.sin(Math.toRadians(f + 90.0F))) * 1.0D;
	          d2 += (d3 * 0.45D * Math.sin(Math.toRadians(f + 90.0F)) - d4 * 0.45D * Math.cos(Math.toRadians(f + 90.0F))) * 1.0D;
	        
	      }
	      BlockPos localBlockPos = new BlockPos(d1, mc.thePlayer.posY - 1.0D, d2);
	      Block localBlock = mc.theWorld.getBlockState(localBlockPos).getBlock();
	      BlockData localBlockData = getBlockData(localBlockPos);
	      if (e.getType() == EventType2.PRE)
	      {  
	        eventMove(e);
	        if ((mc.gameSettings.keyBindJump.getIsKeyPressed()) && (bool1) && (mc.thePlayer.moveForward == 0.0F) && (mc.thePlayer.moveStrafing == 0.0F)) {
	          eventMotion(e);
	        }
	      }
	      if ((isBlockAccessible(localBlock)) && (localBlockData != null))
	      {
	        this.currentBlockPos = localBlockData.pos;
	        if (e.getType() == EventType2.PRE)
	        {
	          float[] arrayOfFloat = getRotations(localBlockData.pos, localBlockData.facing);
	          headTurned = true;
	          yaw = arrayOfFloat[0];
	          pitch = arrayOfFloat[1];
	          (e).setYaw(arrayOfFloat[0]);
	          (e).setPitch(arrayOfFloat[1]);
	          f = arrayOfFloat[0];
	          this.rotation = arrayOfFloat[1];
	          if ((!mc.gameSettings.keyBindJump.getIsKeyPressed()) && (mc.thePlayer.onGround) && (isNotCollidingBelow(0.001D)) && (mc.thePlayer.isCollidedVertically)) {
	            e.setOnGround(false);
	            grounded = false;
	          }else{
	        	  grounded = mc.thePlayer.onGround;
	          }
	        }
	        else
	        {
	         
	          this.timerMotion.reset();
	          int n = mc.thePlayer.inventory.currentItem;
	          updateHotbar();
	          p = getRotations(localBlockData.pos, localBlockData.facing)[0];
	          mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), localBlockData.pos, localBlockData.facing, blockDataToVec3(localBlockData.pos, localBlockData.facing));
	          mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
	          mc.thePlayer.inventory.currentItem = n;
	          mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(n));
	          mc.playerController.updateController();
	          headTurned = false;
	          yaw = mc.thePlayer.rotationYaw;
	          pitch = mc.thePlayer.rotationPitch;
	          (e).setYaw(p);
	          (e).setPitch(p);
	          this.timer.reset();
	        }
	      }
          mc.thePlayer.renderPitchRotation = getRotations(localBlockData.pos, localBlockData.facing)[1];
          mc.thePlayer.rotationYawHead = getRotations(localBlockData.pos, localBlockData.facing)[0];
          mc.thePlayer.prevRenderYawOffset = p;
		}else if(true) {
			if(eagle.isEnabled())
				mc.gameSettings.keyBindSneak.pressed = false;
			if(!sprint.isEnabled())
				mc.thePlayer.setSprinting(false);
			//mc.thePlayer.setSprinting(false);
            slot = 0;
            

                int total = 0;

                for (int index = 36; index < 45; index++) {
                    ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
                    if (itemStack != null) {
                        if (itemStack.getItem() instanceof ItemBlock) {
                            if (((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockFalling)
                                continue;

                            total += itemStack.stackSize;
                        }
                    }
                }
            
            

            target = null;
            boolean blockInHand = false;
            slot = mc.thePlayer.inventory.currentItem;

            if (mc.thePlayer.getHeldItem() != null)
                blockInHand = mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && mc.thePlayer.getHeldItem().stackSize > 0;

            boolean canAutoSelect = getHotbarBlock() != -1;
            if (blockInHand || canAutoSelect) {

                BlockPos blockBelowPlayer = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ);
                BlockPos b1 = new BlockPos(mc.thePlayer.posX + 1, placeY, mc.thePlayer.posZ);
                BlockPos b2 = new BlockPos(mc.thePlayer.posX - 1, placeY, mc.thePlayer.posZ);
                BlockPos b3 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ - 1);
                BlockPos b4 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ + 1);
                
                BlockPos g1 = new BlockPos(mc.thePlayer.posX + 2, placeY, mc.thePlayer.posZ);
                BlockPos g2 = new BlockPos(mc.thePlayer.posX - 2, placeY, mc.thePlayer.posZ);
                BlockPos g3 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ - 2);
                BlockPos g4 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ + 2);
/*
                    if (!mc.thePlayer.onGround && (Math.abs(mc.thePlayer.motionX) > 0.05 || Math.abs(mc.thePlayer.motionZ) > 0.05)) {
                        if (lastPlacedY < mc.thePlayer.posY && lastPlacedY > mc.thePlayer.posY - 3)
                            blockBelowPlayer = new BlockPos(mc.thePlayer.posX, lastPlacedY, mc.thePlayer.posZ);
                    } else if (mc.thePlayer.onGround) {
                        lastPlacedY = (int) Math.floor(mc.thePlayer.posY - 1);
                    }
                
*/
                if(!keepY.isEnabled())
                    placeY = (float) (mc.thePlayer.posY - 1);
                else {
                	placeY = (float) startY - 1;
                	if(mc.thePlayer.posY < startY) {
                		startY = mc.thePlayer.posY;
                	}
                }
                
                Block block = mc.theWorld.getBlockState(blockBelowPlayer).getBlock();
                if ( mc.theWorld.getBlockState(blockBelowPlayer).getBlock().isReplaceable(mc.theWorld, blockBelowPlayer) ||  block instanceof BlockLiquid || block instanceof BlockAir) {
                    //target = getTarget(blockBelowPlayer);
                	
                	if(eagle.isEnabled()) {
                	 if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5D, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
                		 blockstile = 0;
                		 MovementUtil mu = new MovementUtil();
             		//	mu.strafe(0.04, 5);
     		            mc.gameSettings.keyBindSneak.pressed = true;
                		 
     		            if(tieeg.hasTimeElapsed((long) (260+ (Math.random() * 20)), true)){
	     		            if(getTarget(blockBelowPlayer) != null)
	                         	target = getTarget(blockBelowPlayer);
	     		           else if(getTarget(b1) != null)
	   	                  	target = getTarget(b1);
	   	                  else if(getTarget(b2) != null)
	   	                  	target = getTarget(b2);
	   	                  else if(getTarget(b3) != null)
	   	                  	target = getTarget(b3);
	   	                  else if(getTarget(b4) != null)
	   	                  	target = getTarget(b4);
	     		            
	     		            
	   	               else if(getTarget(g1) != null)
		                  	target = getTarget(g1);
		                  else if(getTarget(g2) != null)
		                  	target = getTarget(g2);
		                  else if(getTarget(g3) != null)
		                  	target = getTarget(g3);
		                  else if(getTarget(g4) != null)
		                  	target = getTarget(g4);
     		            }
                	 }else {
                		 tieeg.reset();
                	 }
                	}else {
                		 if(getTarget(blockBelowPlayer) != null)
                           	target = getTarget(blockBelowPlayer);
                           else if(getTarget(b1) != null)
                           	target = getTarget(b1);
                           else if(getTarget(b2) != null)
                           	target = getTarget(b2);
                           else if(getTarget(b3) != null)
                           	target = getTarget(b3);
                           else if(getTarget(b4) != null)
                           	target = getTarget(b4);
                           
                           else if(getTarget(g1) != null)
                           	target = getTarget(g1);
                           else if(getTarget(g2) != null)
                           	target = getTarget(g2);
                           else if(getTarget(g3) != null)
                           	target = getTarget(g3);
                           else if(getTarget(g4) != null)
                           	target = getTarget(g4);
                         
	                  
                	}
                	// mc.gameSettings.keyBindSneak.pressed = false;
             //       if(keepY.isEnabled()) {
                //    	placeY = z- 1;
               //     }else {
                    //	placeY = (float) (mc.thePlayer.posY - 1);
             //       }
                 //   if(mc.gameSettings.keyBindPlayerList.pressed) {
                 //   	placeY -= 1;
                //    	safewalk = false;
                //    	mc.thePlayer.setSneaking(false);
                //    }else {
               //     	safewalk = true;
               //     }
                    	
                    if (target != null) {
                        float[] values = BlockHelper.getFacingRotations(target.pos.getX(), target.pos.getY(), target.pos.getZ(), target.facing);
                        facerots = getRotations(target.pos, target.facing);
//                        if(rotationsType.is("Keep")) {
//	                        x = values[0];
	                        y = values[1];
//                        }
                      //  if(rotationsType.is("Smooth")) {
                        	switch(target.facing) {
                        	case SOUTH:
                        		x = 180;
                        		break;
                        		
                        	case EAST:
                        		x = 90;
                        		break;
                        	case WEST:
                        		x = -90;
                        		break;
            				case DOWN:
            					
            					break;
            				case NORTH:
            					x = 0;
            					break;
            				case UP:
            					
            					break;
                        	//}
                        }
                        if(rotationsType.is("Snap")) {
                            e.setYaw((float) facerots[0]);
                            e.setPitch((float) facerots[1]);
                            
                            
                            mc.thePlayer.rotationYawHead = (float) (facerots[0]);
                            mc.thePlayer.renderYawOffset = (float) (facerots[0]);
                            mc.thePlayer.renderPitchRotation = (float) (facerots[1]);
                        }
                        
                        Entity c = this.mc.getRenderViewEntity();

                        if (!blockInHand) {
                        	 didChange = true;
                        	//Jello.addChatMessage("e");
                          //  slot = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = getHotbarBlock();
                          //  mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(getHotbarBlock()));
                            
                        }
                    }
                }
            }
            
            switch (rotationsType.getMode()) {
			case "Keep":
                mc.thePlayer.rotationYawHead = x;
                mc.thePlayer.renderYawOffset = x;
                mc.thePlayer.renderPitchRotation = y;
                e.setYaw(x);
                e.setPitch(y);
				break;

			case "Down":
            	mc.thePlayer.rotationYawHead = x;
                mc.thePlayer.renderYawOffset = x;
                mc.thePlayer.renderPitchRotation = 90;
                e.setPitch(90);
				break;
			case"FaceBlock":
		//		float[] v = getRotations(target.pos, target.facing);
            	mc.thePlayer.rotationYawHead = facerots[0];
                mc.thePlayer.renderYawOffset = facerots[0];
                mc.thePlayer.renderPitchRotation = facerots[1];
                e.setPitch(facerots[1]);
                e.setYaw(facerots[0]);
				break;
            }
            
         }
		if(e.getType() == EventType2.PRE) {
			//mu.strafe(0.2, 1);
			TimerUtil timer = new TimerUtil();
			if (ticker++ >= delay.getValue() + Math.random()) {
                if (target != null) {
                    ticker = 0;
                    blockstile ++;
                    mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                   //mc.thePlayer.swingItem();
                    if (mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld,  mc.thePlayer.getHeldItem(), target.pos, target.facing, new Vec3(target.pos.getX(), target.pos.getY(), target.pos.getZ()))) {
                    	//lastPlacedY = target.pos.getY();
                   //     if (!mc.thePlayer.onGround && mc.thePlayer.motionY > 0 && !(Math.abs(mc.thePlayer.motionX) > 0.05 || Math.abs(mc.thePlayer.motionZ) > 0.05)) {
                           // mc.thePlayer.setEntityBoundingBox(mc.thePlayer.getEntityBoundingBox().offset(0, mc.thePlayer.posY - Math.floor(mc.thePlayer.posY), 0));
                         //   mc.thePlayer.motionY = 1;
                            //((EntityLivingBaseExtension) mc.thePlayer).setJumpTicks(0);
		                	switch (towerMode.getMode()) {
							case "Matrix":
								if(!mc.thePlayer.onGround&&mc.thePlayer.movementInput.jump) {
		                    		//mc.timer.timerSpeed = 1.3f;
		            				mc.thePlayer.motionY = -1;
		                    	}
								break;
		
							case"Motion":
								
								break;
							}
                        }
                    }
                }
				if (mc.thePlayer.movementInput.jump) {
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
					//	mc.timer.timerSpeed = 0.9f;
					}
				}
            }
		if(e.getType() == EventType2.POST) {
			
		}
			
			

        //    if (slot != -1) {
               // mc.thePlayer.inventory.currentItem = slot;
           //     slot = -1;
         //   }
		
		
		
	}
	
	@EventTarget
	public void onPacket(EventReceivePacket e){
		      if(false) {
		      Packet packet = e.getPacket();
		      int k;
		      if (packet instanceof S2FPacketSetSlot)
		      {

		          e.setCancelled(true);
		        
		        S2FPacketSetSlot localS2FPacketSetSlot = (S2FPacketSetSlot)packet;
		        k = localS2FPacketSetSlot.getSlot();
		        ItemStack localItemStack1 = localS2FPacketSetSlot.getItem();
		        if (k != -1)
		        {
		          this.currentSlot = k;
		          if (localItemStack1 != null)
		          {
		            this.itemStackSize = localItemStack1.stackSize;
		          }
		          else
		          {
		            if (mc.thePlayer.inventoryContainer.getSlot(k).getStack() != null)
		            {
		              ItemStack localItemStack2 = new ItemStack(Item.getItemById(261));
		              mc.thePlayer.inventoryContainer.getSlot(k).putStack(null);
		            }
		            this.itemStackSize = 0;
		          }
		          mc.playerController.updateController();
		        }
		      }
		      }
	}
	
	@EventTarget
	public void onSend(EventPacketSent e){
		//if(headTurned){
	//	if(e.getPacket() instanceof C05PacketPlayerLook || e.getPacket() instanceof C06PacketPlayerPosLook) {
			//if(mode.is("Jello"))
	//		if(e.getPacket() instanceof C06PacketPlayerPosLook) {
	//			C06PacketPlayerPosLook lok = (C06PacketPlayerPosLook) e.getPacket();
	//			System.out.println(e.getPacket());
	//		}
	   	  		//e.setPacket(new C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
	   	  		
	//		}
	//	}
	}
	
	private BlockData getBlockData(BlockPos paramBlockPos)
	  {
	    if (isValidBlock(paramBlockPos.add(0, -1, 0))) {
	      return new BlockData(paramBlockPos.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(paramBlockPos.add(-1, 0, 0))) {
	      return new BlockData(paramBlockPos.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(paramBlockPos.add(1, 0, 0))) {
	      return new BlockData(paramBlockPos.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(paramBlockPos.add(0, 0, 1))) {
	      return new BlockData(paramBlockPos.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(paramBlockPos.add(0, 0, -1))) {
	      return new BlockData(paramBlockPos.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos1 = paramBlockPos.add(-1, 0, 0);
	    if (isValidBlock(localBlockPos1.add(0, -1, 0))) {
	      return new BlockData(localBlockPos1.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos1.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos1.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos1.add(1, 0, 0))) {
	      return new BlockData(localBlockPos1.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos1.add(0, 0, 1))) {
	      return new BlockData(localBlockPos1.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos1.add(0, 0, -1))) {
	      return new BlockData(localBlockPos1.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos2 = paramBlockPos.add(1, 0, 0);
	    if (isValidBlock(localBlockPos2.add(0, -1, 0))) {
	      return new BlockData(localBlockPos2.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos2.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos2.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos2.add(1, 0, 0))) {
	      return new BlockData(localBlockPos2.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos2.add(0, 0, 1))) {
	      return new BlockData(localBlockPos2.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos2.add(0, 0, -1))) {
	      return new BlockData(localBlockPos2.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos3 = paramBlockPos.add(0, 0, 1);
	    if (isValidBlock(localBlockPos3.add(0, -1, 0))) {
	      return new BlockData(localBlockPos3.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos3.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos3.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos3.add(1, 0, 0))) {
	      return new BlockData(localBlockPos3.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos3.add(0, 0, 1))) {
	      return new BlockData(localBlockPos3.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos3.add(0, 0, -1))) {
	      return new BlockData(localBlockPos3.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos4 = paramBlockPos.add(0, 0, -1);
	    if (isValidBlock(localBlockPos4.add(0, -1, 0))) {
	      return new BlockData(localBlockPos4.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos4.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos4.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos4.add(1, 0, 0))) {
	      return new BlockData(localBlockPos4.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos4.add(0, 0, 1))) {
	      return new BlockData(localBlockPos4.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos4.add(0, 0, -1))) {
	      return new BlockData(localBlockPos4.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos5 = paramBlockPos.add(0, -1, 0);
	    if (isValidBlock(localBlockPos5.add(0, -1, 0))) {
	      return new BlockData(localBlockPos5.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos5.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos5.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos5.add(1, 0, 0))) {
	      return new BlockData(localBlockPos5.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos5.add(0, 0, 1))) {
	      return new BlockData(localBlockPos5.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos5.add(0, 0, -1))) {
	      return new BlockData(localBlockPos5.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos6 = localBlockPos5.add(1, 0, 0);
	    if (isValidBlock(localBlockPos6.add(0, -1, 0))) {
	      return new BlockData(localBlockPos6.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos6.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos6.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos6.add(1, 0, 0))) {
	      return new BlockData(localBlockPos6.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos6.add(0, 0, 1))) {
	      return new BlockData(localBlockPos6.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos6.add(0, 0, -1))) {
	      return new BlockData(localBlockPos6.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos7 = localBlockPos5.add(-1, 0, 0);
	    if (isValidBlock(localBlockPos7.add(0, -1, 0))) {
	      return new BlockData(localBlockPos7.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos7.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos7.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos7.add(1, 0, 0))) {
	      return new BlockData(localBlockPos7.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos7.add(0, 0, 1))) {
	      return new BlockData(localBlockPos7.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos7.add(0, 0, -1))) {
	      return new BlockData(localBlockPos7.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos8 = localBlockPos5.add(0, 0, 1);
	    if (isValidBlock(localBlockPos8.add(0, -1, 0))) {
	      return new BlockData(localBlockPos8.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos8.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos8.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos8.add(1, 0, 0))) {
	      return new BlockData(localBlockPos8.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos8.add(0, 0, 1))) {
	      return new BlockData(localBlockPos8.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos8.add(0, 0, -1))) {
	      return new BlockData(localBlockPos8.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    BlockPos localBlockPos9 = localBlockPos5.add(0, 0, -1);
	    if (isValidBlock(localBlockPos9.add(0, -1, 0))) {
	      return new BlockData(localBlockPos9.add(0, -1, 0), EnumFacing.UP);
	    }
	    if (isValidBlock(localBlockPos9.add(-1, 0, 0))) {
	      return new BlockData(localBlockPos9.add(-1, 0, 0), EnumFacing.EAST);
	    }
	    if (isValidBlock(localBlockPos9.add(1, 0, 0))) {
	      return new BlockData(localBlockPos9.add(1, 0, 0), EnumFacing.WEST);
	    }
	    if (isValidBlock(localBlockPos9.add(0, 0, 1))) {
	      return new BlockData(localBlockPos9.add(0, 0, 1), EnumFacing.NORTH);
	    }
	    if (isValidBlock(localBlockPos9.add(0, 0, -1))) {
	      return new BlockData(localBlockPos9.add(0, 0, -1), EnumFacing.SOUTH);
	    }
	    return null;
	  }
	
	private boolean isValidBlock(BlockPos paramBlockPos)
	  {
	    Block localBlock = mc.theWorld.getBlockState(paramBlockPos).getBlock();
	    if ((localBlock.getMaterial().isSolid()) || (!localBlock.isTranslucent()) || (localBlock.isSolidFullCube()) || ((localBlock instanceof BlockLadder)) || ((localBlock instanceof BlockCarpet)) || ((localBlock instanceof BlockSnow)) || ((localBlock instanceof BlockSkull))) {
	      if (!localBlock.getMaterial().isLiquid()) {
	        return true;
	      }
	    }
	    return false;
	  }
	
	public static void scaffoldMove(double paramDouble)
	  {
	    float f1 = mc.thePlayer.rotationYaw * 0.017453292F;
	    float f2 = mc.thePlayer.rotationYaw * 0.017453292F - 4.712389F;
	    float f3 = mc.thePlayer.rotationYaw * 0.017453292F + 4.712389F;
	    float f4 = mc.thePlayer.rotationYaw * 0.017453292F + 0.5969026F;
	    float f5 = mc.thePlayer.rotationYaw * 0.017453292F + -0.5969026F;
	    float f6 = mc.thePlayer.rotationYaw * 0.017453292F - 2.3876104F;
	    float f7 = mc.thePlayer.rotationYaw * 0.017453292F - -2.3876104F;
	    if ((mc.gameSettings.keyBindForward.pressed) && (!isMoving()))
	    {
	      if ((mc.gameSettings.keyBindLeft.pressed) && (!mc.gameSettings.keyBindRight.pressed))
	      {
	        mc.thePlayer.motionX -= MathHelper.sin(f5) * paramDouble;
	        mc.thePlayer.motionZ += MathHelper.cos(f5) * paramDouble;
	      }
	      else if ((mc.gameSettings.keyBindRight.pressed) && (!mc.gameSettings.keyBindLeft.pressed))
	      {
	        mc.thePlayer.motionX -= MathHelper.sin(f4) * paramDouble;
	        mc.thePlayer.motionZ += MathHelper.cos(f4) * paramDouble;
	      }
	      else
	      {
	        mc.thePlayer.motionX -= MathHelper.sin(f1) * paramDouble;
	        mc.thePlayer.motionZ += MathHelper.cos(f1) * paramDouble;
	      }
	    }
	    else if ((mc.gameSettings.keyBindBack.pressed) && (!isMoving()))
	    {
	      if ((mc.gameSettings.keyBindLeft.pressed) && (!mc.gameSettings.keyBindRight.pressed))
	      {
	        mc.thePlayer.motionX -= MathHelper.sin(f6) * paramDouble;
	        mc.thePlayer.motionZ += MathHelper.cos(f6) * paramDouble;
	      }
	      else if ((mc.gameSettings.keyBindRight.pressed) && (!mc.gameSettings.keyBindLeft.pressed))
	      {
	        mc.thePlayer.motionX -= MathHelper.sin(f7) * paramDouble;
	        mc.thePlayer.motionZ += MathHelper.cos(f7) * paramDouble;
	      }
	      else
	      {
	        mc.thePlayer.motionX += MathHelper.sin(f1) * paramDouble;
	        mc.thePlayer.motionZ -= MathHelper.cos(f1) * paramDouble;
	      }
	    }
	    else if ((mc.gameSettings.keyBindLeft.pressed) && (!mc.gameSettings.keyBindRight.pressed) && (!mc.gameSettings.keyBindForward.pressed) && (!mc.gameSettings.keyBindBack.pressed) && (!isMoving()))
	    {
	      mc.thePlayer.motionX += MathHelper.sin(f2) * paramDouble;
	      mc.thePlayer.motionZ -= MathHelper.cos(f2) * paramDouble;
	    }
	    else if ((mc.gameSettings.keyBindRight.pressed) && (!mc.gameSettings.keyBindLeft.pressed) && (!mc.gameSettings.keyBindForward.pressed) && (!mc.gameSettings.keyBindBack.pressed) && (!isMoving()))
	    {
	      mc.thePlayer.motionX += MathHelper.sin(f3) * paramDouble;
	      mc.thePlayer.motionZ -= MathHelper.cos(f3) * paramDouble;
	    }
	  }
	
	public static boolean isMoving()
	  {
	    if ((mc.gameSettings.keyBindForward.isPressed()) || (mc.gameSettings.keyBindBack.isPressed()) || (mc.gameSettings.keyBindLeft.isPressed()) || (mc.gameSettings.keyBindRight.isPressed())) {
	      return false;
	    }
	    return false;
	  }
	  
	  public static boolean isNotCollidingBelow(double paramDouble)
	  {
	    if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -paramDouble, 0.0D)).isEmpty()) {
	      return true;
	    }
	    return false;
	  }
		    
	  private boolean hasBlocksInHotbar()
	  {
	    for (int i = 36; i < 45; i++) {
	      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
	      {
	        Item localItem = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
	        if (((localItem instanceof ItemBlock)) && (isValidItem(localItem))) {
	          return false;
	        }
	      }
	    }
	    return true;
	  }
	  
	  private boolean isValidItem(Item paramItem)
	  {
	    if (!(paramItem instanceof ItemBlock)) {
	      return false;
	    }
	    ItemBlock localItemBlock = (ItemBlock)paramItem;
	    Block localBlock = localItemBlock.getBlock();
	    if (invalid.contains(localBlock)) {
	      return false;
	    }
	    return true;
	  }
	  
	  protected void swap(int paramInt1, int paramInt2)
	  {
	    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, paramInt1, paramInt2, 2, mc.thePlayer);
	  }
	  
	  private boolean hasBlocks()
	  {
	    int i = 36;
	    while (i < 45) {
	      try
	      {
	        ItemStack localItemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
	        if ((localItemStack == null) || (localItemStack.getItem() == null) || (!(localItemStack.getItem() instanceof ItemBlock)) || (!isValidItem(localItemStack.getItem()))) {
	          i++;
	        } else {
	          return true;
	        }
	      }
	      catch (Exception localException) {}
	    }
	    return false;
	  }
	  
	  public class BlockData
	  {
	    public BlockPos pos;
	    public EnumFacing facing;
	    
	    public BlockData(BlockPos paramBlockPos, EnumFacing paramEnumFacing)
	    {
	      this.pos = paramBlockPos;
	      this.facing = paramEnumFacing;
	    }
	  }
	  
	  public boolean isBlockAccessible(Block paramBlock)
	  {
	    if (paramBlock.getMaterial().isReplaceable())
	    {
	      if (((paramBlock instanceof BlockSnow)) && (paramBlock.getBlockBoundsMaxY() > 0.125D)) {
	        return false;
	      }
	      return true;
	    }
	    return false;
	  }
	  
	  private Vec3 blockDataToVec3(BlockPos paramBlockPos, EnumFacing paramEnumFacing)
	  {
	    double d1 = paramBlockPos.getX() + 0.5D;
	    double d2 = paramBlockPos.getY() + 0.5D;
	    double d3 = paramBlockPos.getZ() + 0.5D;
	    d1 += paramEnumFacing.getFrontOffsetX() / 2.0D;
	    d3 += paramEnumFacing.getFrontOffsetZ() / 2.0D;
	    d2 += paramEnumFacing.getFrontOffsetY() / 2.0D;
	    return new Vec3(d1, d2, d3);
	  }
		  
	  private void updateHotbar()
	  {
	    ItemStack localItemStack = new ItemStack(Item.getItemById(261));
	    try
	    {
	      for (int i = 36; i < 45; i++)
	      {
	        int j = i - 36;
	        if ((!Container.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(i), localItemStack, true)) && 
	          ((mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock)) && (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null) && 
	          (isValidItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())) && (mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize != 0))
	        {
	          if ((this.currentSlot == j) && (this.itemStackSize == 0))
	          {
	            this.itemStackSize = 120;
	            return;
	          }
	          if (mc.thePlayer.inventory.currentItem == j) {
	            break;
	          }
	          mc.thePlayer.inventory.currentItem = j;
	          this.currentItem = j;
	          mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
	          mc.playerController.updateController(); break;
	        }
	      }
	    }
	    catch (Exception localException) {}
	  }
	  
	  public static float[] getRotations(BlockPos paramBlockPos, EnumFacing paramEnumFacing)
	  {
	    double d1 = paramBlockPos.getX() + 0.5D - mc.thePlayer.posX + paramEnumFacing.getFrontOffsetX() / 2.0D;
	    double d2 = paramBlockPos.getZ() + 0.5D - mc.thePlayer.posZ + paramEnumFacing.getFrontOffsetZ() / 2.0D;
	    double d3 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (paramBlockPos.getY() + 0.5D);
	    double d4 = MathHelper.sqrt_double(d1 * d1 + d2 * d2);
	    float f1 = (float)(Math.atan2(d2, d1) * 180.0D / 3.141592653589793D) - 90.0F;
	    float f2 = (float)(Math.atan2(d3, d4) * 180.0D / 3.141592653589793D);
	    if (f1 < 0.0F) {
	      f1 += 360.0F;
	    }
	    return new float[] { f1, f2 };
	  }
	  
	  public void eventMove(EventMotion paramEventMotion)
	  {
	    double d1 = mc.thePlayer.motionX;
	    double d2 = mc.thePlayer.motionZ;
	    if (!Jello.getModule("Fast").isToggled()) {
	        mc.thePlayer.motionX *= 0.0D;
	        mc.thePlayer.motionZ *= 0.0D;
	        if ((mc.thePlayer.onGround) && (mc.thePlayer.isCollidedVertically) && (isNotCollidingBelow(0.01D))) {
	          scaffoldMove(0.14D);
	        } else {
	          scaffoldMove(0.2D);
	        }
	      
	    }
	  }
	  
	  public void eventMotion(EventMotion paramEventMotion)
	  {
	    BlockPos localBlockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
	    Block localBlock = mc.theWorld.getBlockState(localBlockPos).getBlock();
	    BlockData localBlockData = getBlockData(localBlockPos);
	    
	   	if ((isBlockAccessible(localBlock)) && (localBlockData != null))
	    {
	      mc.thePlayer.motionY = 0.4196D;
	      mc.thePlayer.motionX *= 0.0D;
	      mc.thePlayer.motionZ *= 0.0D;
	    }
	  }
	  
	  
	  public float[] getRotationsForPosition(double x, double y, double z, double sourceX, double sourceY, double sourceZ) {
	        double deltaX = x - sourceX;
	        double deltaY = y - sourceY;
	        double deltaZ = z - sourceZ;

	        double yawToEntity;

	        if (deltaZ < 0 && deltaX < 0) { // quadrant 3
	            yawToEntity = 90D + Math.toDegrees(Math.atan(deltaZ / deltaX)); // 90
	            // degrees
	            // forward
	        } else if (deltaZ < 0 && deltaX > 0) { // quadrant 4
	            yawToEntity = -90D + Math.toDegrees(Math.atan(deltaZ / deltaX)); // 90
	            // degrees
	            // back
	        } else { // quadrants one or two
	            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
	        }

	        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ
	                * deltaZ);

	        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));

	        yawToEntity = wrapAngleTo180((float) yawToEntity);
	        pitchToEntity = wrapAngleTo180((float) pitchToEntity);

	        yawToEntity = isNaN(yawToEntity) ? 0 : yawToEntity;
	        pitchToEntity = isNaN(pitchToEntity) ? 0 : pitchToEntity;

	        return new float[] { (float) yawToEntity, (float) pitchToEntity };
	    }
	  private float wrapAngleTo180(float angle) {
	        angle %= 360.0F;

	        while (angle >= 180.0F) {
	            angle -= 360.0F;
	        }
	        while (angle < -180.0F) {
	            angle += 360.0F;
	        }

	        return angle;
	    }
	  //for nes test
	  private class BlockDataa {
	        public BlockPos position;
	        public EnumFacing face;

	        public BlockDataa(BlockPos position, EnumFacing face) {
	            this.position = position;
	            this.face = face;
	        }
	    }
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
		
		private int getHotbarBlock() {
	        for (int index = 36; index < 45; index++) {
	            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
	            if (itemStack != null) {
	                if (itemStack.getItem() instanceof ItemBlock) {
	                    if (((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockFalling)
	                        continue;
	                    if(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockSlime)
							continue;

	                    if (itemStack.stackSize >= 1) {
	                        return index - 36;
	                    }
	                }
	            }
	        }

	        return -1;
	    }
		@EventTarget
		public void onrender(EventRender3D e) {
			/*
            BlockPos blockBelowPlayer = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ);
            BlockPos b1 = new BlockPos(mc.thePlayer.posX + 1, placeY, mc.thePlayer.posZ);
            BlockPos b2 = new BlockPos(mc.thePlayer.posX - 1, placeY, mc.thePlayer.posZ);
            BlockPos b3 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ - 1);
            BlockPos b4 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ + 1);
            
            BlockPos g1 = new BlockPos(mc.thePlayer.posX + 2, placeY, mc.thePlayer.posZ);
            BlockPos g2 = new BlockPos(mc.thePlayer.posX - 2, placeY, mc.thePlayer.posZ);
            BlockPos g3 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ - 2);
            BlockPos g4 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ + 2);
            
            BlockPos f1 = new BlockPos(mc.thePlayer.posX + 3, placeY, mc.thePlayer.posZ);
            BlockPos f2 = new BlockPos(mc.thePlayer.posX - 3, placeY, mc.thePlayer.posZ);
            BlockPos f3 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ - 3);
            BlockPos f4 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ + 3);
            
            BlockPos d1 = new BlockPos(mc.thePlayer.posX + 3, placeY, mc.thePlayer.posZ);
            BlockPos d2 = new BlockPos(mc.thePlayer.posX - 3, placeY, mc.thePlayer.posZ);
            BlockPos d3 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ - 3);
            BlockPos d4 = new BlockPos(mc.thePlayer.posX, placeY, mc.thePlayer.posZ + 3);
            if(getTarget(blockBelowPlayer) != null)
              	target = getTarget(blockBelowPlayer);
              else if(getTarget(b1) != null)
              	target = getTarget(b1);
              else if(getTarget(b2) != null)
              	target = getTarget(b2);
              else if(getTarget(b3) != null)
              	target = getTarget(b3);
              else if(getTarget(b4) != null)
              	target = getTarget(b4);
              
              else if(getTarget(g1) != null)
              	target = getTarget(g1);
              else if(getTarget(g2) != null)
              	target = getTarget(g2);
              else if(getTarget(g3) != null)
              	target = getTarget(g3);
              else if(getTarget(g4) != null)
              	target = getTarget(g4);
            
              else if(getTarget(f1) != null)
            	target = getTarget(f1);
              else if(getTarget(f2) != null)
            	target = getTarget(f2);
              else if(getTarget(f3) != null)
            	target = getTarget(f3);
              else if(getTarget(f4) != null)
            	target = getTarget(f4);
            
              else if(getTarget(d1) != null)
              	target = getTarget(d1);
                else if(getTarget(d2) != null)
              	target = getTarget(d2);
                else if(getTarget(d3) != null)
              	target = getTarget(d3);
                else if(getTarget(d4) != null)
              	target = getTarget(d4);
            if(target!=null)
			if (mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld,  mc.thePlayer.getHeldItem(), target.pos, target.facing, new Vec3(target.pos.getX(), target.pos.getY(), target.pos.getZ()))) {
				mc.thePlayer.renderPitchRotation = BlockHelper.getFacingRotations(target.pos.getX(), target.pos.getY(), target.pos.getZ(), target.facing)[1];
				mc.thePlayer.renderYawOffset = BlockHelper.getFacingRotations(target.pos.getX(), target.pos.getY(), target.pos.getZ(), target.facing)[0];
				mc.thePlayer.rotationYawHead = BlockHelper.getFacingRotations(target.pos.getX(), target.pos.getY(), target.pos.getZ(), target.facing)[0];
			}*/
		}
}
