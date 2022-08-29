package com.mentalfrostbyte.jello.modules;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.*;
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
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class InvCleaner extends Module {

	public Random rand = new Random();
	
	public TimerUtil timer = new TimerUtil();
	public TimerUtil d = new TimerUtil();
	private int slots;
	private double numberIdkWillfigureout;
	private boolean someboolean;
	public ModeValue mode;
	public BooleanValue spoof;
	public boolean inventoryOpen;
	public NumberValue delay;
	public NumberValue startDelay;
	private boolean canstart;
	 public List<String> junk = Arrays.asList("stick", "string", "cake", "mushroom", "flint", "dyePowder", "feather", "chest", 
	            "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "piston", "snow", "poison");
	
	public InvCleaner() {
        super("InvCleaner", Keyboard.KEY_O);
        this.jelloCat = Jello.tabgui.cats.get(3);
        mode = new ModeValue("Mode", "2", "2", "1");
     //   this.addValue(mode);
        spoof = new BooleanValue("SpoofInv", false);
        this.addValue(spoof);
        delay = new NumberValue("Delay", 200, 1, 1000, 1);
        this.addValue(delay);
        startDelay = new NumberValue("StartDelay",1, 1, 1000, 1);
        this.addValue(startDelay);
    }
   
	public void onEnable(){
		EventManager.register(this);
		timer.reset();
		slots = 9;
		canstart = false;
		d.reset();
		numberIdkWillfigureout = getEnchantmentOnSword(mc.thePlayer.getHeldItem());
	}
	public void onDisable(){
		EventManager.unregister(this);
		d.reset();
		canstart = false;
	}
	
    public void onUpdate(){
    	if(!this.isToggled())
    		return;
    	if(mc.currentScreen instanceof GuiInventory) {
	    	if(d.hasTimeElapsed(700, true)) {
	    		canstart = true;
	    	}
    	}else {
    		d.reset();
    		canstart = false;
    	}
    	//if(d.hasTimeElapsed((long) startDelay.getValue(), true)) {
    	if(mc.currentScreen instanceof GuiInventory && canstart){
    		
    		if(mode.is("1")) {
	    	if ((slots >= 45) && (!someboolean)) {
				setToggled(false);
				return;
			}
			if (someboolean) {
				if (timer.delay(100)) {
					mc.playerController.windowClick(0, -999, 0, 0, mc.thePlayer);
					mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
					mc.playerController.syncCurrentPlayItem();
					someboolean = false;
					
					timer.reset();
				}
				return;
			}
			numberIdkWillfigureout = getEnchantmentOnSword(mc.thePlayer.getHeldItem());
			ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slots).getStack();
			if ((isItemBad(stack)) && (getEnchantmentOnSword(stack) <= numberIdkWillfigureout)
					&& (stack != mc.thePlayer.getHeldItem())) {
				mc.playerController.windowClick(0, slots, 0, 0, mc.thePlayer);
				timer.reset();
				someboolean = true;
			}
			slots += 1;
	    	
	    	}else if(mode.is("2")) {
	    		if ((mc.currentScreen == null || (mc.currentScreen instanceof GuiContainer && ((GuiContainer) mc.currentScreen).inventorySlots == mc.thePlayer.inventoryContainer)) && purgeUnusedArmor() && purgeUnusedTools() && purgeJunk() && manageSword()) {
	                //Logger.ingameInfo("DONE");
	                if (hotbarHasSpace())
	                    manageHotbar();
	            }
	    	}
    	}
    //}
    	
    }
	

	public static boolean isItemBad(ItemStack item) {
		return (item != null) && ((item.getItem().getUnlocalizedName().contains("TNT"))
				|| (item.getItem().getUnlocalizedName().contains("stick"))
				|| (item.getItem().getUnlocalizedName().contains("egg"))
				|| (item.getItem().getUnlocalizedName().contains("string"))
				|| (item.getItem().getUnlocalizedName().contains("flint"))
				|| (item.getItem().getUnlocalizedName().contains("compass"))
				|| (item.getItem().getUnlocalizedName().contains("feather"))
				|| (item.getItem().getUnlocalizedName().contains("bucket"))
				|| (item.getItem().getUnlocalizedName().contains("chest"))
				|| (item.getItem().getUnlocalizedName().contains("snowball"))
				|| (item.getItem().getUnlocalizedName().contains("fish"))
				|| (item.getItem().getUnlocalizedName().contains("enchant"))
				|| (item.getItem().getUnlocalizedName().contains("exp")) || ((item.getItem() instanceof ItemPickaxe))
				|| ((item.getItem() instanceof ItemTool)) || ((item.getItem() instanceof ItemArmor))
				|| ((item.getItem() instanceof ItemSword))
				|| ((item.getItem().getUnlocalizedName().contains("potion")) && (isBadPotion(item))));
	}

	public static boolean isBadPotion(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}
		if (!(itemStack.getItem() instanceof ItemPotion)) {
			return false;
		}
		ItemPotion itemPotion = (ItemPotion) itemStack.getItem();
		Iterator iterator = itemPotion.getEffects(itemStack).iterator();
		PotionEffect potionEffect;
		do {
			if (!iterator.hasNext()) {
				return false;
			}
			Object pObj = iterator.next();
			potionEffect = (PotionEffect) pObj;
			if (potionEffect.getPotionID() == Potion.poison.getId()) {
				return true;
			}
			if (potionEffect.getPotionID() == Potion.moveSlowdown.getId()) {
				return true;
			}
		} while (potionEffect.getPotionID() != Potion.harm.getId());
		return true;
	}

	private static double getEnchantmentOnSword(ItemStack itemStack) {
		if (itemStack == null) {
			return 0.0D;
		}
		if (!(itemStack.getItem() instanceof ItemSword)) {
			return 0.0D;
		}
		ItemSword itemSword = (ItemSword) itemStack.getItem();
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180314_l.effectId, itemStack)
				+ itemSword.field_150934_a;
	}
	
	
	
	//z
	public static float getSwordStrength(ItemStack stack) {
        return (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180314_l.effectId, stack) * 1.25F) + (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack));
    }
	
	private boolean manageSword() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();
                if (!stack.getDisplayName().toLowerCase().contains("(right click)") && item instanceof ItemSword && timer.hasTimeElapsed((long) delay.getValue(), true)) {
                    moveToHotbarSlot1(i);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>purgeUnusedArmor.</p>
     *
     * @return a boolean.
     */
    public boolean purgeUnusedArmor() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                if (item instanceof ItemArmor) {
                    if (!isBestArmor(stack) && timer.hasTimeElapsed((long) delay.getValue(), true)) {
                        purge(i);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>purgeUnusedTools.</p>
     *
     * @return a boolean.
     */
    public boolean purgeUnusedTools() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                if (item instanceof ItemTool) {
                    if (!stack.getDisplayName().toLowerCase().contains("(right click)") && !isBestTool(stack) && timer.hasTimeElapsed((long) delay.getValue(), true)) {
                        purge(i);
                        return false;
                    }
                }
                if (item instanceof ItemSword) {
                    if (!stack.getDisplayName().toLowerCase().contains("(right click)") && !isBestSword(stack) && timer.hasTimeElapsed((long) delay.getValue(), true)) {
                        purge(i);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * <p>purgeJunk.</p>
     *
     * @return a boolean.
     */
    public boolean purgeJunk() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                for (String shortName : junk) {
                    if (!stack.getDisplayName().toLowerCase().contains("(right click)") && item.getUnlocalizedName().contains(shortName) && timer.hasTimeElapsed((long) delay.getValue(), true)) {
                        purge(i);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>manageHotbar.</p>
     */
    public void manageHotbar() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                if (!stack.getDisplayName().toLowerCase().contains("(right click)") &&
                        ((item instanceof ItemPickaxe && hotbarNeedsItem(ItemPickaxe.class)) || (item instanceof ItemAxe && hotbarNeedsItem(ItemAxe.class)) || (item instanceof ItemSword && hotbarNeedsItem(ItemSword.class)) ||
                                (item instanceof ItemAppleGold && hotbarNeedsItem(ItemAppleGold.class)) || (item instanceof ItemEnderPearl && hotbarNeedsItem(ItemEnderPearl.class)) || (item instanceof ItemBlock && (((ItemBlock) item).getBlock().isFullCube()) &&
                                !hotbarHasBlocks())) &&
                        !hotbar && timer.hasTimeElapsed((long) delay.getValue(), true)) {
                    moveToHotbar(i);
                    return;
                }
            }
        }
        //for (int i = 9; i < 45; i++) {

        //}
    }

    /**
     * <p>hotbarHasSpace.</p>
     *
     * @return a boolean.
     */
    public boolean hotbarHasSpace() {
        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (slot.getStack() == null)
                return true;
        }
        return false;
    }

    public boolean hotbarNeedsItem(Class<?> type) {
        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (type.isInstance(slot.getStack()))
                return false;
        }
        return true;
    }
    public boolean hotbarHasBlocks() {
        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (slot.getStack() != null && slot.getStack().getItem() instanceof ItemBlock && ((ItemBlock) slot.getStack().getItem()).getBlock().isFullCube())
                return true;
        }
        return false;
    }

    /**
     * <p>isBestTool.</p>
     *
     * @param compareStack a {@link net.minecraft.item.ItemStack} object.
     * @return a boolean.
     */
    public boolean isBestTool(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemTool) {
                ItemTool item = (ItemTool) stack.getItem();
                ItemTool compare = (ItemTool) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.getStrVsBlock(stack, preferredBlock(item.getClass())) <= item.getStrVsBlock(compareStack, preferredBlock(compare.getClass())))
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * <p>isBestSword.</p>
     *
     * @param compareStack a {@link net.minecraft.item.ItemStack} object.
     * @return a boolean.
     */
    public boolean isBestSword(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemSword) {
                ItemSword item = (ItemSword) stack.getItem();
                ItemSword compare = (ItemSword) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.field_150934_a + getSwordStrength(compareStack) <= item.field_150934_a + getSwordStrength(stack))
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * <p>preferredBlock.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link net.minecraft.block.Block} object.
     */
    public Block preferredBlock(Class clazz) {
        return clazz == ItemPickaxe.class ? Blocks.cobblestone : clazz == ItemAxe.class ? Blocks.log : Blocks.dirt;
    }

    /**
     * <p>isBestArmor.</p>
     *
     * @param compareStack a {@link net.minecraft.item.ItemStack} object.
     * @return a boolean.
     */
    public boolean isBestArmor(ItemStack compareStack) {
        for (int i = 0; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor) stack.getItem();
                ItemArmor compare = (ItemArmor) compareStack.getItem();
                if (item.armorType == compare.armorType) {
                    if (AutoArmor.getProtectionValue(compareStack) <= AutoArmor.getProtectionValue(stack))
                        return false;
                }
            }
        }

        return true;
    }

    public boolean has(Item item, int count) { //WIP
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (slot.getStack() != null && (slot.getStack().getItem().equals(item)))
                count -= slot.getStack().stackSize;
        }
        return count >= 0;
    }

    /**
     * <p>moveToHotbar.</p>
     *
     * @param slot a int.
     */
    public void moveToHotbar(int slot) {
        if (spoof.isEnabled())
            openInvPacket();

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);

        if (spoof.isEnabled())
            closeInvPacket();
    }

    public void moveToHotbarSlot1(int slot) {
        if (spoof.isEnabled())
            openInvPacket();

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 2, mc.thePlayer);

        if (spoof.isEnabled())
            closeInvPacket();
    }

    /**
     * <p>purge.</p>
     *
     * @param slot a int.
     */
    public void purge(int slot) {
        if (spoof.isEnabled())
            openInvPacket();

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
        // Logger.ingameInfo(mc.thePlayer.ticksExisted+"");

        if (spoof.isEnabled())
            closeInvPacket();
    }

    /**
     * <p>openInvPacket.</p>
     */
    public void openInvPacket() {
        if (!inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));

        inventoryOpen = true;
    }

    /**
     * <p>closeInvPacket.</p>
     */
    public void closeInvPacket() {
        if (inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));

        inventoryOpen = false;
    }
}
