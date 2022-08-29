package com.mentalfrostbyte.jello.discord;

import com.mentalfrostbyte.jello.main.Jello;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Client {
	
	private long startTime = 0;
	//public static DiscordRP discordRPC = new DiscordRP();
	
	
    public void init() {
        this.startTime = System.currentTimeMillis();

        final DiscordEventHandlers discordEventHandlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(discordUser -> {
                    final String firstLine = "Launching Sigma Jello Reborn";

                    this.updateStatus(firstLine, "...Yep, there's RPC now.");
                }).build();

        DiscordRPC.discordInitialize("998960027073261600", discordEventHandlers, true);

        new Thread("Discord RPC Callback") {
            @Override
            public void run() {
                DiscordRPC.discordRunCallbacks();
            }
        }.start();
    }
    public void updateStatus(String lineOne, String lineTwo) {
      final DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(lineTwo)
              .setBigImage("large", "")
              .setDetails(lineOne)
              .setStartTimestamps(this.startTime);

      DiscordRPC.discordUpdatePresence(builder.build());
  }
	public static void shutdown() {
		DiscordRPC.discordShutdown();
	}
	
}
