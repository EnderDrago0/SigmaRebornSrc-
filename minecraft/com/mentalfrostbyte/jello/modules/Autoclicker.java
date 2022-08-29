package com.mentalfrostbyte.jello.modules;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.MathUtil;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class Autoclicker extends Module {

	public Random rand = new Random();
	
	private TimerUtil timer = new TimerUtil();
	private NumberValue min = new NumberValue("MinimumCPS", 6, 1, 20, 1);
	private NumberValue max = new NumberValue("MaximumCPS", 8, 1, 20, 1);
	private BooleanValue clickOnGui = new BooleanValue("ClickOnGUI", false);
	public Autoclicker() {
        super("AutoClicker", Keyboard.KEY_NONE);
        this.jelloCat = Jello.tabgui.cats.get(2);
        this.addValue(min, max, clickOnGui);
    }
   
	public void onEnable() {
		EventManager.register(this);
	}
	
	public void onDisable(){
		EventManager.unregister(this);
	}
	
    @EventTarget
	  public void onRender(EventRender2D e)
	  {
		  if(!this.isToggled() || Jello.getModule("KillAura").isToggled())
			  return;
		  if (Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
	            mc.gameSettings.keyBindAttack.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode() ,true);
	            return;
	        }

	        if (!mc.thePlayer.isBlocking()) {
	        	
	        	if(!clickOnGui.isEnabled() && mc.currentScreen != null)
	        		return;
	            Mouse.poll();

	            if (Mouse.isButtonDown(0) && Math.random() * 50 <= min.getValue() + (MathUtil.RANDOM.nextDouble() * (max.getValue() - min.getValue()))) {
	                sendClick(0, true);
	                sendClick(0, false);
	            }
	        }
		  
	  }
    public void sendClick(final int button, final boolean state) {
        final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode();

        KeyBinding.setKeyBindState(button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode(), state);

        if (state) {
            KeyBinding.onTick(keyBind);
        }
    }
	
}
