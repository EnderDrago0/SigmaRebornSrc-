package com.mentalfrostbyte.jello.command.impl;

import com.mentalfrostbyte.jello.command.Command;
import com.mentalfrostbyte.jello.main.Jello;

public class Info extends Command{

	public Info() {
		super("Info", "Displays client info", "info", "i");
	}

	@Override
	public void onCommand(String[] args, String command) {
		Jello.addChatMessage(Jello.version + "By Cancelled");
	}
}
