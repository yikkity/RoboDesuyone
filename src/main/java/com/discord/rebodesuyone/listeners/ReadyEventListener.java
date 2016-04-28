package com.discord.rebodesuyone.listeners;

import java.util.ArrayList;

import com.discord.robodesuyone.RoboDesuyoneMain;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

public class ReadyEventListener implements IListener<ReadyEvent> {

	@Override
	public void handle(ReadyEvent event) {
		
	}

}
