package com.discord.rebodesuyone.listeners;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class GuildEventListener implements IListener<GuildCreateEvent>{

	/* 
	 * Method that's ran when the guild and channels populate for the bot
	 * Basically a start up message is sent to the #general chat
	 * 
	 */
	@Override
	public void handle(GuildCreateEvent event) {
			//general channel id: 173441141323071488
			try {
                event.getClient().getChannelByID("173441141323071488").sendMessage("I'm awake");
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
		
		
	}

}
