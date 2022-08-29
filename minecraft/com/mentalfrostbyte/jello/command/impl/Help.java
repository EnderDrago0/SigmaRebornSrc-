package com.mentalfrostbyte.jello.command.impl;

import com.mentalfrostbyte.jello.command.Command;
import com.mentalfrostbyte.jello.command.CommandManager;
import com.mentalfrostbyte.jello.main.Jello;

public class Help extends Command{

	public Help() {
		super("Help", "Displays all the commands", "help", "h");
	}

	@Override
	public void onCommand(String[] args, String command) {
		for(Command com : CommandManager.commands) {
			Jello.addChatMessage(com.getName() + "-" + com.getDescription() + " ." + com.getSyntax());
		}
	}
}
