package com.mentalfrostbyte.jello.main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.lwjgl.opengl.Display;

import com.ibm.icu.util.ULocale.Category;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMotion;
import com.mentalfrostbyte.jello.hud.JelloHud;
import com.mentalfrostbyte.jello.hud.Notification;
import com.mentalfrostbyte.jello.hud.NotificationManager;
import com.mentalfrostbyte.jello.tabgui.TabGUI;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.ScriptUtils;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mentalfrostbyte.jello.util.Value;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ResourceLocation;
import optifine.Config;

public class Module {
	
	protected static Minecraft mc = Minecraft.getMinecraft();
	public TimerUtil time = new TimerUtil();
	private String name;
	private String displayname;
	public int keyCode;
	private boolean toggled;
	 public ArrayList<Value> values;
	 private Category category;
	 public TabGUI.Cat jelloCat;
	 private double toggleAnimation;
		public float animation;
		public float animHeight;
		
		public float hoverPercent;
		public float lastHoverPercent;
	 
	public Module(String name, int keyCode) {
		this.name = name;
		this.displayname = name;
		this.keyCode = keyCode;
		this.toggled = false;
		this.values = new ArrayList();
	}
	public void toggle() {
		this.toggled = !this.toggled;
		if(this.toggled) {
			onEnable();
			enSound();
			
		}else {
			onDisable();
			if(!(this.getName() == "Menu"))
				disSound();
			
			
		}

				if(mc.thePlayer != null){
		NotificationManager.notify(toggled ? "Enabled":"Disabled", this.getDisplayName(), 250);
				}
	}
	
	private void enSound() {
		try {
			URL s = this.getClass().getClassLoader().getResource("assets\\minecraft\\Jello\\enable.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(s);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
	        clip.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private void disSound() {
		try {
			URL s = this.getClass().getClassLoader().getResource("assets\\minecraft\\Jello\\disable.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(s);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
	        clip.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private void stop(Clip e) {
		if(time.hasTimeElapsed(300, true)){
			e.stop();
		}
	}
	public String getDisplayName() {
		return displayname;
	}
	
	public void addValue(Value value)
    {
      this.values.add(value);
    }
	
	public void addValue(Value... value)
    {
		Collections.addAll(this.values, value);
    }
	public void checkForHidens() {
		
	}
	public void removeValue(Value value) {
		this.values.remove(value);
	}
	public void onEnable() { }
	
	public void onDisable() { }
	
	public void onUpdate() {
		ScriptUtils.script();
	}
	
	public void onRender() { 
		/*
		Configuration config = new Configuration();
		
		config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		config.setDictionaryPath("src\\assets\\minecraft\\Jello\\3801.dic");
		config.setLanguageModelPath("src\\assets\\minecraft\\Jello\\3801.lm");
		
		
		try {
			LiveSpeechRecognizer s = new LiveSpeechRecognizer(config);
			s.startRecognition(true);
			
			SpeechResult speechResult = null;
			
			while ((speechResult = s.getResult()) != null) {
				String voiceCommand = speechResult.getHypothesis();
				System.out.println(voiceCommand);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getToggleAnimation() {
		return toggleAnimation;
	}
	public void setToggleAnimation(double toggleAnimation) {
		this.toggleAnimation = toggleAnimation;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getKeyCode() {
		return this.keyCode;
	}
	
	public void setKeyCode(int keycode) {
		this.keyCode = keycode;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	
	}
	
	public boolean isToggled() {
		return this.toggled;
	}
	
	public boolean onSendChatMessage(String s){
		return true;
	}
	
	public boolean onRecieveChatMessage(S02PacketChat packet){
		return true;
	}
	
	public Category getCategory() {
        return this.category;
    }
    
    public void setCategory(final Category category) {
        this.category = category;
    }
   


    
    public ArrayList<Value> getValues()
    {
      return values;
    }
    

    public Value getValue(String label) {
        for (Value value : this.getValues()) {
            if (!value.getName().equalsIgnoreCase(label)) continue;
            return value;
        }
        return null;
    }
	public void setDisplayName(String string) {
		displayname = string;
		
	}
    
    
    

}
