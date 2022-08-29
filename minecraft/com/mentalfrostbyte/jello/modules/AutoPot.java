package com.mentalfrostbyte.jello.modules;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtils;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLadder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoPot extends Module {
	
	private final NumberValue minHealth = new NumberValue("Health", 15, 1, 20, 1);
	private final BooleanValue randomiseRots = new BooleanValue("Randomise Rotations", true);
	
	public AutoPot() {
        super("AutoPot", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(1);
        this.addValue(randomiseRots, minHealth);
    }
	private int ticksSinceLastSplash, ticksSinceCanSplash, oldSlot;
    private boolean needSplash, switchBack;

    private final ArrayList<Integer> acceptedPotions = new ArrayList() {{
        add(6);
        add(1);
        add(5);
        add(8);
        add(14);
        add(12);
        add(10);
        add(16);
    }};
	public void onEnable(){
		EventManager.register(this);
		
	}
	
	public void onDisable(){
		EventManager.unregister(this);
		needSplash = switchBack = false;
	}
	
    @SuppressWarnings("unused")
	@EventTarget
	public void mot(EventMotion e)
	{
    	ticksSinceLastSplash++;
    	 if (Objects.requireNonNull(Jello.getModule("BlockFly").isToggled() || KillAura.attacking|| mc.thePlayer.isInLiquid() || (PlayerUtils.getBlockRelativeToPlayer(0, -1, 0) instanceof BlockAir || PlayerUtils.getBlockRelativeToPlayer(0, -1, 0) instanceof BlockLadder)))
             ticksSinceCanSplash = 0;
         else
             ticksSinceCanSplash++;
    	 
    	 if (switchBack) {
             mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
             mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
           //  mc.thePlayer.inventory.currentItem = oldSlot;
             switchBack = false;
             return;
         }
    	 
    	 if (ticksSinceCanSplash <= 1 || !mc.thePlayer.onGround)
             return;
    	 oldSlot = mc.thePlayer.inventory.currentItem;
         for (int i = 36; i < 45; ++i) {
             final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
             if (itemStack != null && mc.currentScreen == null) {
                 final Item item = itemStack.getItem();
                 if (item instanceof ItemPotion) {
                     final ItemPotion p = (ItemPotion) item;
                     if (ItemPotion.isSplash(itemStack.getMetadata())) {
                         if (p.getEffects(itemStack.getMetadata()) != null) {
                             final int potionID = (p.getEffects(itemStack.getMetadata()).get(0)).getPotionID();
                             boolean hasPotionIDActive = false;

                             for (final PotionEffect potion : mc.thePlayer.getActivePotionEffects()) {
                                 if (potion.getPotionID() == potionID && potion.getDuration() > 0) {
                                     hasPotionIDActive = true;
                                     break;
                                 }
                             }

                             if (acceptedPotions.contains(potionID) && !hasPotionIDActive && ticksSinceLastSplash > 20) {
                                 final String effectName = p.getEffects(itemStack.getMetadata()).get(0).getEffectName();

                                 if ((effectName.contains("regeneration") || effectName.contains("heal")) && mc.thePlayer.getHealth() > minHealth.getValue())
                                     continue;

                                 if (false) {//jump.isEnabled();
                                     e.setPitch(randomiseRots.isEnabled() ? -RandomUtils.nextFloat(89, 90) : -90);
                                     mc.thePlayer.rotationPitchHead = 90;
                                     if (!needSplash) {
                                         mc.thePlayer.jump();
                                         needSplash = true;

                                         new Thread(() -> {
                                             try {
                                                 Thread.sleep(300L); // TODO: fix
                                             } catch (final InterruptedException e1) {
                                                 e1.printStackTrace();
                                             }

                                             if (false)//if (packet.isEnabled())
                                                 mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, randomiseRots.isEnabled() ? -RandomUtils.nextFloat(89, 90) : -90, mc.thePlayer.onGround));

                                             needSplash = false;
                                         }).start();
                                     } else {
                                    	// mc.thePlayer.inventory.currentItem = i-36;
                                    	 mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i - 36));
                                    	 mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(itemStack));
                                         switchBack = true;

                                         ticksSinceLastSplash = 0;
                                         needSplash = false;
                                     }
                                 } else {
                                     e.setPitch(randomiseRots.isEnabled() ? RandomUtils.nextFloat(89, 90) : 90);
                                     mc.thePlayer.rotationPitchHead = 90;
                                     if (!needSplash) {
                                         if (false)//if (packet.isEnabled())
                                            mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, randomiseRots.isEnabled() ? RandomUtils.nextFloat(89, 90) : 90, mc.thePlayer.onGround));

                                         needSplash = true;
                                     } else {
                                    	 //mc.thePlayer.inventory.currentItem = i - 36;
                                    	 mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i - 36));
                                    	 mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(itemStack));
                                         switchBack = true;

                                         ticksSinceLastSplash = 0;
                                         needSplash = false;
                                     }
                                 }
                                 return;
                             }
                         }
                     }
                 }
             }
         }
    	 
	}
}
