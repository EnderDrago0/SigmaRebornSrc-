package com.mentalfrostbyte.jello.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mentalfrostbyte.jello.command.impl.Bind;
import com.mentalfrostbyte.jello.command.impl.ConfigLoad;
import com.mentalfrostbyte.jello.command.impl.Help;
import com.mentalfrostbyte.jello.command.impl.Info;
import com.mentalfrostbyte.jello.command.impl.Toggle;
import com.mentalfrostbyte.jello.event.events.EventChat;
import com.mentalfrostbyte.jello.main.Jello;

public class CommandManager {

	public static List<Command> commands = new ArrayList<Command>();
	public String prefix = ".";
	
	public CommandManager() {
		setup();
	}
	
	public void setup() {
		commands.add(new Toggle());
		commands.add(new Bind());
		commands.add(new Info());
		commands.add(new ConfigLoad());
		commands.add(new Help());
	}
	public void handleChat(EventChat event) {
		String message = event.getMessage();
		
		if(!message.startsWith(prefix))
			return;
		
		event.setCancelled(true);
		
		message = message.substring(prefix.length());
		boolean foundCom = false;
		if(message.split(" ").length > 0) {
			String commandName = message.split(" ")[0];
			
			for(Command c : commands) {
				if(c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
					c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
					foundCom = true;
					break;
				}
			}
		}
		if(!foundCom) {
			Jello.addChatMessage("Could not find command");
		}
	}
}
