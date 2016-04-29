package com.discord.rebodesuyone.listeners;

import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

public class MessageEventListener{

	@EventSubscriber
	public void testEvent(MessageReceivedEvent event) {
		IMessage message = event.getMessage();

		if (message.getContent().equals("!test")) {
			try {
				event.getClient().getChannelByID(message.getChannel().getID()).sendMessage("Mic check");
			} catch (MissingPermissionsException e) {
				e.printStackTrace();
			} catch (HTTP429Exception e) {
				e.printStackTrace();
			} catch (DiscordException e) {
				e.printStackTrace();
			}
		}
	}

	@EventSubscriber
	public void yoEvent(MessageReceivedEvent event){
		IMessage message = event.getMessage();
		
		if (message.getContent().equals("!yo")) {
			try {
				event.getClient().getChannelByID(message.getChannel().getID()).sendMessage("Yo!");
			} catch (MissingPermissionsException e) {
				e.printStackTrace();
			} catch (HTTP429Exception e) {
				e.printStackTrace();
			} catch (DiscordException e) {
				e.printStackTrace();
			}
		}
	}
}
