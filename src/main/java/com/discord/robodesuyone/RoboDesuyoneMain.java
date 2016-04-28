package com.discord.robodesuyone;

import com.discord.rebodesuyone.listeners.GuildEventListener;
import com.discord.rebodesuyone.listeners.ReadyEventListener;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 * Hello world!
 *
 */
public class RoboDesuyoneMain {
	static IDiscordClient client;

	public static IDiscordClient getClient() {
		return client;
	}

	public static void setClient(IDiscordClient client) {
		RoboDesuyoneMain.client = client;
	}

	public static void main(String[] args) throws DiscordException {
		// logs the bot into the discord server
		client = new ClientBuilder().withToken("MTc1MDQzMzAyODc2MzgxMTg0.CgLtRw.liF3WTv1e6IbEHb9ywJK0fEgd5s").login();

		// register listeners
		client.getDispatcher().registerListener(new ReadyEventListener());
		client.getDispatcher().registerListener(new GuildEventListener());
	}
}
