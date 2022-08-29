package com.mentalfrostbyte.jello.command.impl;

import org.lwjgl.input.Keyboard;

import com.mentalfrostbyte.jello.command.Command;
import com.mentalfrostbyte.jello.main.Jello;

public class Bind extends Command{

	public Bind() {
		super("Bind", "Binds a module to a key", "bind <name> <key> | clear", "b");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length == 2) {
			String moduleName = args[0];
			String keyName = args[1];
			
			boolean foundMod = false;
			
			for(com.mentalfrostbyte.jello.main.Module module : Jello.mods) {
				if(module.getName().equalsIgnoreCase(moduleName)) {
					module.setKeyCode(Keyboard.getKeyIndex(keyName.toUpperCase()));
					
					Jello.addChatMessage(String.format("Bound %s to %s", module.getName(), Keyboard.getKeyName(module.getKeyCode())));
					foundMod = true;
					break;
				}
			}
			if(!foundMod) {
				Jello.addChatMessage("Could not find module");
			}
		}
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("clear")) {
				for(com.mentalfrostbyte.jello.main.Module module : Jello.mods) {
					module.setKeyCode(Keyboard.KEY_NONE);
				}
			}
			
			Jello.addChatMessage("Cleared all binds");
			
		}
	}
}
