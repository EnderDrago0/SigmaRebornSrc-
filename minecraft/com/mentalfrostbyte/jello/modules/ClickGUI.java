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
import com.mentalfrostbyte.jello.util.TimerUtil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

public class ClickGUI extends Module {

	public Random rand = new Random();
	
	public TimerUtil timer = new TimerUtil();
	public static ModeValue theme = new ModeValue("Theme", "Light", "Light", "Dark");
	public static final BooleanValue blur = new BooleanValue("Blur", false);
	public static BooleanValue particles = new BooleanValue("Particles", true);
	public ClickGUI() {
        super("ClickGUI", Keyboard.KEY_RSHIFT);
        this.addValue(theme, blur, particles);
    }
   
	public void onEnable(){
		if (mc.theWorld != null) {
             this.mc.displayGuiScreen(Jello.jgui);
            this.setToggled(false);
            this.jelloCat = Jello.tabgui.cats.get(4);
        }

	}
	
	public void onDisable(){
		
	}
	
    
	
	
}
