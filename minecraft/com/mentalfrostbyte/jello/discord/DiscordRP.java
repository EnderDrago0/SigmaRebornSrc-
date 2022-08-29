package com.mentalfrostbyte.jello.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {

	private static boolean running = true;
	private static long created = 0;
	
	
	public static void start() {
	/*	created = System.currentTimeMillis();
		
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback(){
			@Override
			public void apply(DiscordUser user) {
				System.out.println(user.username);
				System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
				update("Booting up", "");
			}
			
		}).build();
		
		
		DiscordRPC.discordInitialize("998960027073261600", handlers, true);
		new Thread("Discord RPC Callback") {
			@Override
			public void run() {
				while(running) {
					DiscordRPC.discordRunCallbacks();
				}
			}
		}.start();*/
		
	}
	public static void shutdown() {
		/*running = false;
		DiscordRPC.discordShutdown();*/
	}
	public static void update(String firstLine, String secondLine) {
	/*	DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondLine);
		b.setBigImage("large", "");
		b.setDetails(firstLine);
		b.setStartTimestamps(created);
		
		DiscordRPC.discordUpdatePresence(b.build());*/
	}
	
}
