package com.mentalfrostbyte.jello.command.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.command.Command;
import com.mentalfrostbyte.jello.main.Jello;
import com.mentalfrostbyte.jello.main.Module;
import com.mentalfrostbyte.jello.util.BooleanValue;
import com.mentalfrostbyte.jello.util.ModeValue;
import com.mentalfrostbyte.jello.util.NumberValue;
import com.mentalfrostbyte.jello.util.SettingsFile;
import com.mentalfrostbyte.jello.util.Value;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

public class ConfigLoad extends Command{

	public ConfigLoad() {
		super("Config", "Loads a config", "config <action> <name>", "e");
	}
	private File cf = SettingsFile.configFolder;
	@SuppressWarnings("resource")
	@Override
	public void onCommand(String[] args, String command) {
		if(args.length == 1) {
			String configAction = args[0];
			String ooo = args[0];
			if(ooo.equalsIgnoreCase("list")) {
				for(File cfiles : cf.listFiles()) {
					String cname = cfiles.getName();
					cname = cname.replaceAll(".jello", " ");
					Jello.addChatMessage(cname);
				}
			}
		}
		if(args.length == 2) {//config load name
			String configAction = args[0];
			String configName = args[1];
			if(configAction.equalsIgnoreCase("load")) {
				boolean loaded = false;
				
				for(File cfiles : cf.listFiles()) {//loading part
					if(cfiles.getName().equalsIgnoreCase(configName + ".jello")) {
						Jello.addChatMessage("Loading config " + configName);
						loaded = true;
						try {
							String line;
							BufferedReader variable9 = new BufferedReader(new FileReader(cfiles));
							try {
								while ((line = variable9.readLine()) != null) {
								    String[] stuff = line.split(":");
								    Module module = Jello.getModule(stuff[0]);
								    
								    Value val = module.getValue(stuff[1]);
								    if (val instanceof BooleanValue) {
								        BooleanValue value = (BooleanValue)val;
								        value.set(Boolean.valueOf(stuff[2]));
								        continue;
								    }
								    if(val instanceof NumberValue) {
								        NumberValue value = (NumberValue)val;
								        value.setVal(Double.valueOf(stuff[2]));
								    }
								    if(val instanceof ModeValue) {
								    	ModeValue value = (ModeValue)val;
								    	value.setMode(String.valueOf(stuff[2]));
								    }
								}
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					        try {
								variable9.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						
						break;
					}
				}
				if(!loaded) {
					Jello.addChatMessage("Could not find config " + configName);
				}
			}
			if(configAction.equalsIgnoreCase("save")) {
				String sconfigName = args[1];
				if(configName.equalsIgnoreCase("con") && Util.getOSType() != Util.EnumOS.LINUX) {
					Jello.addChatMessage("NOOOOO DONT DO THAT, IT WILL CRASH YOUR PC");
					return;
				}
				boolean saved = false;
				File configFile = new File(Minecraft.getMinecraft().mcDataDir, "\\Jello\\configs\\" + configName + ".jello" + "\\");
		        PrintWriter variable9 = null;
				try {
					variable9 = new PrintWriter(new FileWriter(configFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
		        for (Module m2 : Jello.getModules()) {
		        	
		            for (Value val : m2.getValues()) {
		                
		                if (val instanceof BooleanValue) {
		                	BooleanValue value = (BooleanValue)val;
		                    variable9.println(String.valueOf(m2.getName()) + ":" + value.getName() + ":" + value.isEnabled());
		                }
		                
		                
		                if (val instanceof NumberValue) {
		                	NumberValue value = (NumberValue)val;
		                	variable9.println(String.valueOf(m2.getName()) + ":" + value.getName() + ":" + value.getValue());
		                }
		                	

		                if(val instanceof ModeValue) {
		                	ModeValue value = (ModeValue)val;
		                	variable9.println(String.valueOf(m2.getName()) + ":" + val.getName() + ":" + value.getMode());
		                }
		                saved = true;
		            }
		            
		        }
		        variable9.close();
				if(!saved) {
					Jello.addChatMessage("Could not save config");
				}else {
					Jello.addChatMessage("Saved config as " + sconfigName);
				}
			}
		}
	}
}
