package com.mentalfrostbyte.jello.command.impl;

import com.mentalfrostbyte.jello.command.Command;
import com.mentalfrostbyte.jello.main.Jello;

public class Toggle extends Command{

	public Toggle() {
		super("Toggle", "Toggles a module by name", "toggle <name>", "t");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length > 0) {
			String moduleName = args[0];
			
			boolean foundModule = false;
			
			for(com.mentalfrostbyte.jello.main.Module module : Jello.mods) {
				if(module.getName().equalsIgnoreCase(moduleName)) {
					module.toggle();
					
					Jello.addChatMessage((module.isToggled() ? "Enabled" : "Disabled") + " " + module.getName());
					
					foundModule = true;
					break;
				}
			}
			if(!foundModule) {
				Jello.addChatMessage("Could not found module");
			}
		}
	}
}
