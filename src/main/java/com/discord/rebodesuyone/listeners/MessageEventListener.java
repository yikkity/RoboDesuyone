package com.discord.rebodesuyone.listeners;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;

public class MessageEventListener {

	@EventSubscriber
	public void testEvent(MessageReceivedEvent event) {
		IMessage message = event.getMessage();

		// if the message is equal to the desired command word, then execute
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

	// takes in and saves the message to be quoted
	@EventSubscriber
	public void quotePreviousMessageCommand(MessageReceivedEvent event) {
		IMessage message = event.getMessage();

		// get the message before the command
		IChannel channel = event.getClient().getChannelByID(message.getChannel().getID());
		MessageList messages = channel.getMessages();
		IMessage toBeQuoted = messages.get(messages.size() - 1); // get the
																	// message
																	// before
																	// the
																	// command
		IUser author = toBeQuoted.getAuthor();

		String messageToSave = author.getName() + " - " + toBeQuoted.getContent();

		// if the message is equal to the desired command word, then execute
		if (message.getContent().equals("!quotethat")) {
			try {
				// open the quotes file
				try (PrintWriter output = new PrintWriter("quotes.txt")) {
					// write the new quote in
					output.println(messageToSave);

					// close the file
					output.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				event.getClient().getChannelByID(message.getChannel().getID()).sendMessage("Message Quoted");

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
	public void sayAQuoteCommand(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		String[] messageSplit = message.getContent().split(" ");
		String user = "";
		String chosenQuote;
		int chosenQuoteIndex = -1;

		// if the message is equal to the desired command word, then execute
		if (messageSplit[0].equals("!quote")) {
			try {

				// if the user gave a username or quote id
				if (messageSplit.length == 2) {

					// check if the param is a quote id
					if (NumberUtils.isNumber(messageSplit[1])) {
						chosenQuoteIndex = Integer.valueOf(messageSplit[1]);

						// if it wasn't a number
					} else {
						user = messageSplit[1];
					}

					chosenQuote = getQuoteHelper(user, chosenQuoteIndex);
					event.getClient().getChannelByID(message.getChannel().getID()).sendMessage(chosenQuote);

					// if the user didn't give any params
				} else if (messageSplit.length == 1) {
					chosenQuote = getQuoteHelper(user, chosenQuoteIndex);
					event.getClient().getChannelByID(message.getChannel().getID()).sendMessage(chosenQuote);

					// shame user here (or just tell them what the correct input
					// is)
				} else {
					event.getClient().getChannelByID(message.getChannel().getID())
							.sendMessage("Wrong quote input - correct input !quote <|username|quoteID>");
				}

			} catch (MissingPermissionsException e) {
				e.printStackTrace();
			} catch (HTTP429Exception e) {
				e.printStackTrace();
			} catch (DiscordException e) {
				e.printStackTrace();
			}
		}
	}

	private String getQuoteHelper(String user, int chosenQuoteIndex) {
		String quote;
		List<String> quotesList = new ArrayList<String>();

		// open the quotes file
		try (BufferedReader input = new BufferedReader(new FileReader("quotes.txt"))) {

			// stores quotes into the array list to be chosen
			// reads in quotes of a specific person if username was
			// given
			// otherwise it will populate all quotes
			while ((quote = input.readLine()) != null) {
				if (!user.isEmpty()) {
					if (quote.contains(user)) {
						quotesList.add(quote);
					}
				} else {
					quotesList.add(quote);
				}
			}

			// close the file
			input.close();

			// if the user didn't give a quote id, then it will randomly choose
			// a quote
			if (chosenQuoteIndex == -1) {
				// randomly pick a quote
				int maxRandomRange = quotesList.size();

				Random random = new Random();
				chosenQuoteIndex = random.nextInt(maxRandomRange);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return quotesList.get(chosenQuoteIndex);
	}

	// read all quotes from file and send user a PM with a link to text dump
	// somehow
	// pastebin api?
	@EventSubscriber
	public void dumpQuotes(MessageReceivedEvent event) {

	}

	/*
	 * confirmed: user will say !qup 
	 * bot sees message 
	 * writes user's name
	 * to file
	 * 
	 * not confirmed 
	 * set timer? 
	 * when timer resolves pm user
	 * when timer resolves remove user's name from list
	 */
	@EventSubscriber
	public void queuePersonCommand(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		IUser user = message.getAuthor();
		
		if (message.getContent().equals("!qup")) {
			
		}
	}
	
	/*
	 * will print out queue as a message in the chat the command was typed in
	 */
	@EventSubscriber
	public void showQueueCommand(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		
		if (message.getContent().equals("!qup")) {
			
		}
	}
}
