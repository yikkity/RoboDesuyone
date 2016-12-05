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
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;

/*
 * Will have to eventually refactor this into a new command class to handle all the commands
 * 
 */
public class MessageEventListener {

    // TODO possibly refactor all methods so that there is one method that grabs
    // the command then goes into a switch statement for the correct command
    // then it calls the appropriate method for the command

    // TODO write a help command that lists all readable permutations of
    // commands

    public void commandOperator(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String channelId = message.getChannel().getID();
        IChannel channel = event.getClient().getChannelByID(channelId);
        IDiscordClient client = event.getClient();

        String messageContent = message.getContent();
        String[] messageSplit = messageContent.split(" ");

        switch (messageSplit[0]) {
        case "!test":
            testEvent(client, channelId);
            break;
        case "!quotethat":
            quotePreviousMessageCommand(client, channelId, channel);
            break;
        case "!quote":
            quoteCommand(client, messageSplit, channelId);
            break;
        default:
            try {
                client.getChannelByID(channelId).sendMessage("That is not a command");
            } catch (MissingPermissionsException | HTTP429Exception | DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    @EventSubscriber
    public void testEvent(IDiscordClient dClient, String channelId) {

        try {
            dClient.getChannelByID(channelId).sendMessage("Bye Nyx");
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        } catch (HTTP429Exception e) {
            e.printStackTrace();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Quote commands
    // !quotethat
    // !quote
    // !quote username
    // !quote <quoteId>
    // !quote username message
    // !quotedump

    // Still need to test

    // takes in and saves the message to be quoted
    @EventSubscriber
    public void quotePreviousMessageCommand(IDiscordClient dClient, String channelId, IChannel channel) {

        // get the message before the command
        MessageList messages = channel.getMessages();
        IMessage toBeQuoted = messages.get(messages.size() - 1);
        IUser author = toBeQuoted.getAuthor();

        // create the quote string to be saved
        String messageToSave = author.getName() + " - " + toBeQuoted.getContent();

        try {
            writeToFileHelper(messageToSave, "quotes.txt");
            dClient.getChannelByID(channelId).sendMessage("Message Quoted");

        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        } catch (HTTP429Exception e) {
            e.printStackTrace();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    // saves quotes manually
    // gets a random quote from a specific user
    // gets a specific quote
    // gets a random quote
    @EventSubscriber
    public void quoteCommand(IDiscordClient dClient, String[] messageSplit, String channelId) {
        String user = "";
        String chosenQuote;
        int chosenQuoteIndex = -1;

        // if the message is equal to the desired command word, then execute
        if (messageSplit[0].equals("!quote")) {
            try {

                // if the user wants to manually quote someone
                // TODO: create a check against user list to check if actual
                // user is given
                if (messageSplit.length == 3) {
                    user = messageSplit[1];
                    String toBeQuoted = messageSplit[2];

                    String messageToSave = user + " - \"" + toBeQuoted + "\"";
                    writeToFileHelper(messageToSave, "quotes.txt");

                    dClient.getChannelByID(channelId).sendMessage("Quote Added");

                    // if the user gave a username or quote id
                } else if (messageSplit.length == 2) {

                    // check if the param is a numerical
                    // should be the quoteid
                    if (NumberUtils.isNumber(messageSplit[1])) {
                        chosenQuoteIndex = Integer.valueOf(messageSplit[1]);

                        // if it wasn't a number
                    } else {
                        user = messageSplit[1];
                    }

                    chosenQuote = getQuoteHelper(user, chosenQuoteIndex);
                    dClient.getChannelByID(channelId).sendMessage(chosenQuote);

                    // if the user didn't give any params
                } else if (messageSplit.length == 1) {
                    chosenQuote = getQuoteHelper(user, chosenQuoteIndex);
                    dClient.getChannelByID(channelId).sendMessage(chosenQuote);

                }
                // else {
                // TODO move this string to help command
                // event.getClient().getChannelByID(message.getChannel().getID()).sendMessage(
                // "Wrong quote input - correct inputs = !quote | !quote
                // username | !quote 1 | !quote username message");
                // }

            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            } catch (HTTP429Exception e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO this
    // read all quotes from file and send to user
    // send as a PM? <-- seems likely - less work lol
    @EventSubscriber
    public void dumpQuotes(MessageReceivedEvent event) {

    }

    // -----------------------------------------------------------------------------------------------------------------

    // Queue commands
    // !lineup
    // !done
    // !showqueue

    // confirmed:
    // user will say !lineup
    // bot sees message
    // writes user's name to file

    // not confirmed
    // remove user's name from list
    // - command !done
    // -- will remove user from queue list
    // -- will pm next user #1 in line

    @EventSubscriber
    public void queuePersonCommand(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();

        if (message.getContent().equals("!lineup")) {
            // write to queue file
            try {
                writeToFileHelper(user.getName(), "queue.txt");

                event.getClient().getChannelByID(message.getChannel().getID())
                        .sendMessage(user.getName() + " was added to the queue.");
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            } catch (HTTP429Exception e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * will print out queue as a message in the chat the command was typed in
     * might change to pm to that person if determined too spammy
     */
    @EventSubscriber
    public void showQueueCommand(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        List<String> queue = new ArrayList<String>();
        StringBuilder queueString = new StringBuilder();
        if (message.getContent().equals("!showqueue")) {
            // open the quotes file
            try {
                queue = getQueueFromFile();

                for (String person : queue) {
                    queueString.append(person + "\n");
                }

                event.getClient().getChannelByID(message.getChannel().getID()).sendMessage(queueString.toString());
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            } catch (HTTP429Exception e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Helper and Misc methods
    
    private void writeToFileHelper(String messageToSave, String filename) {
        // open the quotes file
        try (PrintWriter output = new PrintWriter(filename)) {
            // write the new quote in
            output.println(messageToSave);

            // close the file
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // TODO: write a user not found response
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

    private List<String> getQueueFromFile() {
        String fromFile;
        int count = 0;
        List<String> queue = new ArrayList<String>();

        // open the quotes file
        try (BufferedReader input = new BufferedReader(new FileReader("queue.txt"))) {

            // stores quotes into the array list to be chosen
            // reads in quotes of a specific person if username was
            // given
            // otherwise it will populate all quotes
            while ((fromFile = input.readLine()) != null) {
                count++;
                queue.add(Integer.toString(count) + ". " + fromFile + "\n");
            }

            // close the file
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return queue;
    }
}
