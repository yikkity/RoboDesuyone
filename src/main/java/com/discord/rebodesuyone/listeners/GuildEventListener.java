package com.discord.rebodesuyone.listeners;

import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

public class GuildEventListener implements IListener<GuildCreateEvent>{

	/* 
	 * Method that's ran when the guild and channels populate for the bot
	 * 
	 */
	@Override
	public void handle(GuildCreateEvent event) {
		try {
			//general channel id: 173441141323071488
			event.getClient().getChannelByID("173441141323071488").sendMessage("I'm awake");
		} catch (MissingPermissionsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTP429Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
