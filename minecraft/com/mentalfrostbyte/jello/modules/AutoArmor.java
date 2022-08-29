package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventPreMotionUpdates;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.ArmorUtil;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.MathHelper;

public class AutoArmor extends Module {

	public Random rand = new Random();
	private final int[] chestplate = new int[]{311, 307, 315, 303, 299};
    private final int[] leggings = new int[]{312, 308, 316, 304, 300};
    private final int[] boots = new int[]{313, 309, 317, 305, 301};
    private final int[] helmet = new int[]{310, 306, 314, 302, 298};
    int delay = 0;
    public boolean bestarmor = true;
	public TimerUtil timer = new TimerUtil();
	private TimerUtil d = new TimerUtil();
	public ModeValue mode;
	public NumberValue speed;
	private boolean canstart;
	public AutoArmor() {
        super("AutoArmor", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(3);
        mode = new ModeValue("Mode", "1", "1", "2");
      //  this.addValue(mode);
        speed = new NumberValue("Delay", 40, 1, 500, 1);
        this.addValue(speed);
    }
   
	public void onEnable(){
		d.reset();
		canstart=false;
	}
	
	public void onDisable(){
		canstart=false;
		d.reset();
	}
	
	public int range(int min, int max) {
        return min + (new Random().nextInt() * (max - min));
    }
    public void AutoArmoring() {
        if (this.bestarmor) {
            return;
        }
        int item = -1;
        ++this.delay;
        if (this.delay >= 10) {
            int id;
            if (Jello.core.player().inventory.armorInventory[0] == null) {
                int[] boots = this.boots;
                int length = boots.length;
                int i = 0;
                //int slotID = -1;
                while (i < length) {
                    id = boots[i];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++i;
                }
            }
            if (Jello.core.player().inventory.armorInventory[1] == null) {
                int[] leggings = this.leggings;
                int length2 = leggings.length;
                int j = 0;
                while (j < length2) {
                    id = leggings[j];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++j;
                }
            }
            if (Jello.core.player().inventory.armorInventory[2] == null) {
                int[] chestplate = this.chestplate;
                int length3 = chestplate.length;
                int k = 0;
                while (k < length3) {
                    id = chestplate[k];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++k;
                }
            }
            if (Jello.core.player().inventory.armorInventory[3] == null) {
                int[] helmet = this.helmet;
                int length4 = helmet.length;
                int l = 0;
                while (l < length4) {
                    id = helmet[l];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++l;
                }
            }
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, 1, Jello.core.player());
                this.delay = 0;
            }
        }
    }

    public void SwitchToBetterArmor() {
        if (!this.bestarmor) {
            return;
        }
        ++this.delay;
        if (this.delay >= 10 && (Jello.core.player().openContainer == null || Jello.core.player().openContainer.windowId == 0)) {
            int n;
            int n2;
            int[] arrn;
            int id;
            boolean switcharmor = false;
            int item = -1;
            if (Jello.core.player().inventory.armorInventory[0] == null) {
                arrn = this.boots;
                n2 = arrn.length;
                n = 0;
                while (n < n2) {
                    id = arrn[n];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++n;
                }
            }
            if (ArmorUtil.IsBetterArmor(0, this.boots)) {
                item = 8;
                switcharmor = true;
            }
            if (Jello.core.player().inventory.armorInventory[3] == null) {
                arrn = this.helmet;
                n2 = arrn.length;
                n = 0;
                while (n < n2) {
                    id = arrn[n];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++n;
                }
            }
            if (ArmorUtil.IsBetterArmor(3, this.helmet)) {
                item = 5;
                switcharmor = true;
            }
            if (Jello.core.player().inventory.armorInventory[1] == null) {
                arrn = this.leggings;
                n2 = arrn.length;
                n = 0;
                while (n < n2) {
                    id = arrn[n];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++n;
                }
            }
            if (ArmorUtil.IsBetterArmor(1, this.leggings)) {
                item = 7;
                switcharmor = true;
            }
            if (Jello.core.player().inventory.armorInventory[2] == null) {
                arrn = this.chestplate;
                n2 = arrn.length;
                n = 0;
                while (n < n2) {
                    id = arrn[n];
                    if (ArmorUtil.getItem(id) != -1) {
                        item = ArmorUtil.getItem(id);
                        break;
                    }
                    ++n;
                }
            }
            if (ArmorUtil.IsBetterArmor(2, this.chestplate)) {
                item = 6;
                switcharmor = true;
            }
            boolean var7 = false;
            ItemStack[] arritemStack = Jello.core.player().inventory.mainInventory;
            int n3 = arritemStack.length;
            n2 = 0;
            while (n2 < n3) {
                ItemStack stack = arritemStack[n2];
                if (stack == null) {
                    var7 = true;
                    break;
                }
                ++n2;
            }
            boolean bl = switcharmor = switcharmor && !var7;
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, switcharmor ? 4 : 1, Jello.core.player());
                this.delay = 0;
            }
        }
    }
    private double getProtectionValues(final ItemStack stack) {
        return ((ItemArmor)stack.getItem()).damageReduceAmount + (100 - ((ItemArmor)stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180310_c.effectId, stack) * 4 * 0.0075;
    }
    private TimerUtil del = new TimerUtil();
    public void onUpdate() {
    	if(!this.isToggled())
    		return;
    	if(mc.currentScreen instanceof GuiInventory) {
	    	if(d.hasTimeElapsed(400, true)) {
	    		canstart = true;
	    	}
    	}else {
    		d.reset();
    		canstart = false;
    	}
    	if(mc.currentScreen instanceof GuiInventory && canstart){
    		if(mode.is("2")) {
		        this.AutoArmoring();
		        this.SwitchToBetterArmor();
    		}else if(mode.is("1")) {
    			int slotID = -1;
                double maxProt = -1;
                int switchArmor = -1;

                for (int i = 9; i < 45; ++i) {
                    ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (stack != null && (this.canEquip(stack) || this.betterCheck(stack) && !this.canEquip(stack))) {
                        if (this.betterCheck(stack) && switchArmor == -1) {
                            switchArmor = this.betterSwap(stack);
                        }

                        double protValue = getProtectionValue(stack);
                        if (protValue >= maxProt) {
                            slotID = i;
                            maxProt = protValue;
                        }
                    }
                }

                if (slotID != -1 && timer.hasTimeElapsed((long) (speed.getValue() + this.range(0, 30)), true)) {
                    if (!(mc.currentScreen instanceof GuiInventory)) {
                        mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    }
                    if (switchArmor != -1) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 4 + switchArmor, 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
                    }

                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotID, 0, 1, mc.thePlayer);
                    if (!(mc.currentScreen instanceof GuiInventory)) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.openContainer.windowId));
                    }
                    timer.hasTimeElapsed(0, true);
                }
            }
    	}
    		}
    		

    public static double getProtectionValue(ItemStack stack) {
        return !(stack.getItem() instanceof ItemArmor) ? 0.0D : (double) ((ItemArmor) stack.getItem()).damageReduceAmount + (double) ((100 - ((ItemArmor) stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180310_c.effectId, stack) * 4) * 0.0075D;
    }
    
    public boolean betterCheck(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                return true;
            }

            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                return true;
            }

            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                return true;
            }

            return mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount;
        }

        return false;
    }

    private int betterSwap(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount) {
                return 1;
            }

            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                return 2;
            }

            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                return 3;
            }

            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                return 4;
            }
        }

        return -1;
    }

    private boolean canEquip(ItemStack stack) {
        return mc.thePlayer.getEquipmentInSlot(1) == null && stack.getUnlocalizedName().contains("boots") || mc.thePlayer.getEquipmentInSlot(2) == null && stack.getUnlocalizedName().contains("leggings") || mc.thePlayer.getEquipmentInSlot(3) == null && stack.getUnlocalizedName().contains("chestplate") || mc.thePlayer.getEquipmentInSlot(4) == null && stack.getUnlocalizedName().contains("helmet");
    }
}
