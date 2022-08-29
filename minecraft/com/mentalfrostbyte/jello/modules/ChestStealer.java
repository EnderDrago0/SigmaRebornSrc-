package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.WorldChangeEvent;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.RotationUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChestStealer extends Module {

	private Random rand = new Random();
	private TimerUtil timer = new TimerUtil();
	//public static BooleanValue silent = new BooleanValue("NoGui", true);
	private BooleanValue aura = new BooleanValue("Aura", false);
	private TileEntityChest chestToOpen;
	private final ArrayList<BlockPos> clickedChests = new ArrayList<>();
	private final NumberValue range = new NumberValue("AuraRange", 3, 1, 10 ,1);
	public ChestStealer() {
        super("ChestStealer", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
        this.addValue(aura, range);
    }
   
	public void onEnable(){
		clickedChests.clear();
	}
	
	public void onDisable(){
		
	}
	
    public void onUpdate(){
    	if(!this.isToggled())
    		return;
    	GuiChest chest;
        if (this.mc.currentScreen instanceof GuiChest && !Jello.inventoryUtil.inventoryIsFull() && !Jello.chestUtil.chestIsEmpty(chest = (GuiChest)this.mc.currentScreen)) {
        	Jello.chestUtil.chestSlots.clear();
        	Jello.chestUtil.findChestSlots(chest);
        	if (!Jello.chestUtil.chestSlots.isEmpty() && (chest.lowerChestInventory.getDisplayName().getUnformattedText().equals("Large Chest") || chest.lowerChestInventory.getDisplayName().getUnformattedText().equals("Chest") || chest.lowerChestInventory.getDisplayName().getUnformattedText().equals("LOW"))) {
            	
            	Random random = new Random();
                int randomSlot = random.nextInt(Jello.chestUtil.chestSlots.size());
                if (true) {
                    this.mc.playerController.windowClick(chest.inventorySlots.windowId, Jello.chestUtil.chestSlots.get(randomSlot), 0, 1, this.mc.thePlayer);
                }
               
            }
        }else if(this.mc.currentScreen instanceof GuiChest){
        	mc.gameSettings.keyBindForward.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            mc.gameSettings.keyBindBack.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            mc.gameSettings.keyBindRight.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            mc.gameSettings.keyBindLeft.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));  
            mc.gameSettings.keyBindJump.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));  
            mc.gameSettings.keyBindSprint.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
		    mc.thePlayer.closeScreen();
        }

    	if(aura.isEnabled()) {
	    	if (mc.currentScreen instanceof GuiContainer)
	    		return;
	        if (chestToOpen != null) {
	            if (mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), chestToOpen.getPos(), EnumFacing.DOWN, new Vec3(chestToOpen.getPos()))) {
	                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
	                mc.thePlayer.rotationYawHead = RotationUtil.faceTileEntity(chestToOpen, animation, animHeight)[0];
	                mc.thePlayer.renderYawOffset = RotationUtil.faceTileEntity(chestToOpen, animation, animHeight)[0];
	                mc.thePlayer.renderPitchRotation = RotationUtil.faceTileEntity(chestToOpen, animation, animHeight)[1];
	                chestToOpen = null;
	                return;
	            }
	        }
	
	        for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
	            if (tileEntity instanceof TileEntityChest && mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) < range.getValue()) {
	                if (clickedChests.contains(tileEntity.getPos())) continue;
	                final TileEntityChest chests = (TileEntityChest) tileEntity;
	                clickedChests.add(chests.getPos());
	                chestToOpen = chests;
	            }
	        }
    	}
    }
    @EventTarget
    public void omn(WorldChangeEvent e) {
    	clickedChests.clear();
    }	
}
