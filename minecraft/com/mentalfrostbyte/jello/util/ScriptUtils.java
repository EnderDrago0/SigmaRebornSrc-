package com.mentalfrostbyte.jello.util;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.mentalfrostbyte.jello.hud.NotificationManager;
import com.mentalfrostbyte.jello.main.Jello;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class ScriptUtils {

	public static HashMap<File, Boolean> ssc = new HashMap<File, Boolean>();
	
	public static void load() {
		for(File f : SettingsFile.scriptFolder.listFiles()) {
			Jello.sc.put(f, false);
		}
		for(File f : SettingsFile.scriptFolder.listFiles()) {
			ssc.put(f, false);
		}
		
		for(File f : SettingsFile.scriptFolder.listFiles()) {
			Jello.scripts.add(f);
		}
	}
	public static void toggle(File n) {
		if(Jello.sc.get(n).booleanValue() == true) {
			Jello.sc.remove(n);
			Jello.sc.put(n, false);
		}else {
			Jello.sc.remove(n);
			Jello.sc.put(n, true);
		}
	}
	public static boolean get(File e) {
		if(Jello.sc.get(e).booleanValue() == true)
			return true;
		else
			return false;
	}
	public static void onUpdate() {
		
	}
	public static void onEnable() {
		
	}
	@SuppressWarnings("resource")
	public static void script() {
		for(File f : Jello.scripts) {
			Minecraft mc = Minecraft.getMinecraft();
			ArrayList<Integer> ints = new ArrayList<Integer>();
			ArrayList<Double> doubles = new ArrayList<Double>();
			ArrayList<String> strings = new ArrayList<String>();
			if(!get(f))
				return;
			try {
				String line;
				BufferedReader variable9 = new BufferedReader(new FileReader(f));
				while ((line = variable9.readLine()) != null) {
					String[] stuff = line.split(":");
					
					if(stuff[0].contains("Jello")) {
						if(!stuff[1].isEmpty()) {
							if(stuff[1].contains("AddMessage")) {
								if(!stuff[2].isEmpty()) {
									Jello.addChatMessage(stuff[2]);
								}
							}
							if(stuff[1].contains("Notify")) {
								if(!stuff[2].isEmpty()) {
									NotificationManager.notify(stuff[2], f.getName(), 700);
								}
							}
						}
					}
					if(stuff[0].contains("mc")) {
						if(!stuff[1].isEmpty()) {
							if(stuff[1].contains("thePlayer")) {
								if(!stuff[2].isEmpty()) {
									if(stuff[2].contains("motionX")) {
										if(!stuff[3].isEmpty()) {
											mc.thePlayer.motionX = Double.valueOf(stuff[3]);
										}
									}else if(stuff[2].contains("motionZ")){
										if(!stuff[3].isEmpty()) {
											mc.thePlayer.motionZ = Double.valueOf(stuff[3]);
										}
									}
									else if(stuff[2].contains("motionY")){
										if(!stuff[3].isEmpty()) {
											mc.thePlayer.motionY = Double.valueOf(stuff[3]);
										}
									}
									else if(stuff[2].contains("strafe")){
										if(!stuff[3].isEmpty()) {
											MovementUtil movementUtil = new MovementUtil();
											movementUtil.strafe(Double.valueOf(stuff[3]), 0);
										}
									}
								}
							}
						}
					}if(stuff[0].contains("var")) {
						if(!stuff[1].isEmpty()) {
							switch (stuff[1]) {
							case "int":
								if(!stuff[2].isEmpty()) {
									ints.add(Integer.valueOf(stuff[2]));
								}
								break;

							case "double":
								if(!stuff[2].isEmpty()) {
									doubles.add(Double.valueOf(stuff[2]));
								}
								break;
								
							case "String":
								if(!stuff[2].isEmpty()) {
									strings.add(stuff[2]);
								}
								break;
							}
						}
					}if(stuff[0].contains("if")) {
						if(!stuff[1].isEmpty()) {
							
						}
					}
				}
				variable9.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
