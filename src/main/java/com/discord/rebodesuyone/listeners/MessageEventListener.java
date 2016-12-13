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

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class MessageEventListener implements IListener<MessageReceivedEvent> {

    @Override
    public void handle(MessageReceivedEvent event) {
        commandOperator(event);
    }

    // Operator method to handle all command inputs then calls the relevant
    // @EventSubscriber
    public void commandOperator(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String channelId = message.getChannel().getID();
        IChannel channel = event.getClient().getChannelByID(channelId);
        IDiscordClient client = event.getClient();
        IUser user = message.getAuthor();

        String messageContent = message.getContent();
        String[] messageSplit = messageContent.split(" ");

        // makes sure that only messages with '!' as the first char gets checked
        if (messageSplit[0].charAt(0) == '!') {
            switch (messageSplit[0]) {
            case "!test":
                testEvent(client, channelId);
                break;
            case "!quotethat":
                quotePreviousMessageCommand(client, channelId, channel);
                break;
            case "!quote":
                quoteCommand(client, messageSplit, channelId, user.getID());
                break;
            case "!lineup":
                queuePersonCommand(client, user.getName(), channelId);
                break;
            case "!showqueue":
                showQueueCommand(client, channelId);
                break;
            default:
                try {
                    new MessageBuilder(client).withChannel(channel).withContent("That is not a command!").build();
                } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void testEvent(IDiscordClient dClient, String channelId) {

        try {
            new MessageBuilder(dClient).withChannel(channelId).withContent("Bye Nyx").build();
        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
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
    public void quotePreviousMessageCommand(IDiscordClient dClient, String channelId, IChannel channel) {

        // get the message before the command
        MessageList messages = channel.getMessages();
        IMessage toBeQuoted = messages.get(messages.size() - 1);
        IUser author = toBeQuoted.getAuthor();

        // create the quote string to be saved
        String messageToSave = author.getName() + " - " + toBeQuoted.getContent();

        writeToFileHelper(messageToSave, "quotes.txt");

        try {
            new MessageBuilder(dClient).withChannel(channelId).withContent("Quote saved").build();
        } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
            e.printStackTrace();
        }

    }

    // saves quotes manually
    // gets a random quote from a specific user
    // gets a specific quote
    // gets a random quote
    public void quoteCommand(IDiscordClient dClient, String[] messageSplit, String channelId, String userId) {
        String username = "";
        String chosenQuote;
        int chosenQuoteIndex = -1;

        // if the message is equal to the desired command word, then execute
        if (messageSplit[0].equals("!quote")) {
            try {
                // if the user wants to manually quote someone
                // TODO: create a check against user list to check if actual
                // user is given
                if (messageSplit.length == 3) {
                    username = messageSplit[1];
                    String toBeQuoted = messageSplit[2];

                    // check if user exists in list of users
                    if (existingUserCheck(dClient, username) == true) {
                        String messageToSave = username + " - \"" + toBeQuoted + "\"";
                        writeToFileHelper(messageToSave, "quotes.txt");

                        new MessageBuilder(dClient).withChannel(channelId).withContent("Quote Added").build();
                    } else {
                        new MessageBuilder(dClient).withChannel(channelId).withContent("User doesn't exist").build();
                    }

                    // if the user gave a username or quote id
                } else if (messageSplit.length == 2) {

                    // check if the param is a numerical
                    // should be the quoteid
                    if (NumberUtils.isNumber(messageSplit[1])) {
                        chosenQuoteIndex = Integer.valueOf(messageSplit[1]);

                        // if it wasn't a number
                    } else {
                        username = messageSplit[1];
                    }

                    // check if the user exists in list of users
                    // or if a quote index was given
                    if (existingUserCheck(dClient, username) || chosenQuoteIndex != -1) {
                        chosenQuote = getQuoteHelper(username, chosenQuoteIndex);
                        new MessageBuilder(dClient).withChannel(channelId).withContent(chosenQuote);
                    } else {
                        new MessageBuilder(dClient).withChannel(channelId)
                                .withContent("User doesn't exist of invalid quote id").build();
                    }

                    // if the user didn't give any params
                } else if (messageSplit.length == 1) {
                    chosenQuote = getQuoteHelper(username, chosenQuoteIndex);
                    new MessageBuilder(dClient).withChannel(channelId).withContent(chosenQuote).build();

                }
                // else {
                // TODO move this string to help command
                // event.getClient().getChannelByID(message.getChannel().getID()).sendMessage(
                // "Wrong quote input - correct inputs = !quote | !quote
                // username | !quote 1 | !quote username message");
                // }
            } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
                e.printStackTrace();
            }
        }
    }

    // read all quotes from file and send to user
    // send as a PM? <-- seems likely - less work lol
    public void dumpQuotes(IDiscordClient dClient, IUser user) {
        String quote;
        int id = 0;
        StringBuilder quotes = new StringBuilder();
        try (BufferedReader input = new BufferedReader(new FileReader("quotes.txt"))) {
            // stores quotes into the array list to be chosen
            // reads in quotes of a specific person if username was given
            // otherwise it will populate all quotes
            while ((quote = input.readLine()) != null) {
                quotes.append("Quote id: " + id++ + " - " + quote + "\n");
            }

            // close the file
            input.close();

            try {
                IPrivateChannel pm = dClient.getOrCreatePMChannel(user);
                new MessageBuilder(dClient).withChannel(pm.getID()).withQuote(quotes.toString()).build();
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void queuePersonCommand(IDiscordClient dClient, String username, String channelId) {

        // write to queue file
        writeToFileHelper(username, "queue.txt");
        try {
            new MessageBuilder(dClient).withChannel(channelId).withContent(username + " was added to the queue.")
                    .build();
        } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }

    /*
     * will print out queue as a message in the chat the command was typed in
     * might change to pm to that person if determined too spammy
     */
    public void showQueueCommand(IDiscordClient dClient, String channelId) {
        List<String> queue = new ArrayList<String>();
        StringBuilder queueString = new StringBuilder();
        // open the quotes file
        queue = getQueueFromFile();

        for (String person : queue) {
            queueString.append(person + "\n");
        }

        try {
            new MessageBuilder(dClient).withChannel(channelId).withContent(queueString.toString()).build();
        } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Helper and Misc methods

    private boolean existingUserCheck(IDiscordClient dClient, String username) {
        boolean exists = false;
        List<IUser> users = dClient.getUsers();
        for (IUser user : users) {
            if (user.getName().equals(username)) {
                exists = true;
            }
        }
        return exists;
    }

    private void writeToFileHelper(String messageToSave, String filename) {
        // open the quotes file
        try (PrintWriter output = new PrintWriter(filename)) {
            // write the new quote in
            output.append(messageToSave);

            // close the file
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getQuoteHelper(String user, int chosenQuoteIndex) {
        String quote;
        List<String> quotesList = new ArrayList<String>();

        // open the quotes file
        try (BufferedReader input = new BufferedReader(new FileReader("quotes.txt"))) {

            // stores quotes into the array list to be chosen
            // reads in quotes of a specific person if username was given
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = "";
        if (quotesList.isEmpty()) {
            result = "User does not have anything quoted.";
        } else {
            result = quotesList.get(chosenQuoteIndex);
        }

        return result;
    }

    private List<String> getQueueFromFile() {
        String fromFile;
        int count = 0;
        List<String> queue = new ArrayList<String>();

        // open the quotes file
        try (BufferedReader input = new BufferedReader(new FileReader("queue.txt"))) {

            // stores the people in an array list
            while ((fromFile = input.readLine()) != null) {
                count++;
                queue.add(Integer.toString(count) + ". " + fromFile + "\n");
            }

            // close the file
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return queue;
    }
}
