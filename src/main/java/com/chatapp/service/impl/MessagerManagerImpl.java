/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp.service.impl;

/**
 *
 * @author SevenC
 */

import com.chatapp.model.Message;
import com.chatapp.service.MessageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MessagerManagerImpl implements MessageManager {

    private final String jsonFile;

    private Message[] sentMessages;
    private Message[] disregardedMessages;
    private Message[] storedMessages;
    private String[]  messageIds;
    private String[]  messageHashes;

    private int sentCount;
    private int disregardedCount;
    private int storedCount;
    private int idCount;
    private int hashCount;

    private int messageCounter = 0;


    public MessagerManagerImpl(int capacity) {
        this(capacity, "stored_messages.json");
    }

    public MessagerManagerImpl(int capacity, String jsonFile) {
        this.jsonFile = jsonFile;
        int cap = Math.max(capacity, 10);
        sentMessages        = new Message[cap];
        disregardedMessages = new Message[cap];
        storedMessages      = new Message[cap];
        messageIds          = new String [cap];
        messageHashes       = new String [cap];
    }

    @Override
public boolean disregardSentByHash(String hash) {
    if (hash == null) return false;
    String target = hash.trim().toUpperCase();

    for (int i = 0; i < sentCount; i++) {
        if (sentMessages[i].getMessageHash().equalsIgnoreCase(target)) {
            Message m = sentMessages[i];

            for (int j = i; j < sentCount - 1; j++) {
                sentMessages[j] = sentMessages[j + 1];
            }
            sentMessages[--sentCount] = null;

            m.setStatus("DISREGARDED");
            if (disregardedCount < disregardedMessages.length) {
                disregardedMessages[disregardedCount++] = m;
            }
            return true;
        }
    }
    return false;
}
    
    @Override
    public String validateRecipient(String recipient) {
        if (recipient == null) return "Recipient number incorrectly formatted.";
        if (!recipient.matches("^(\\+27|0)[6-8][0-9]{8}$"))
            return "Recipient number incorrectly formatted.";
        return "Recipient accepted";
    }

    @Override
    public String validateMessageBody(String body) {
        if (body == null) return "Please enter a message of less than 250 characters";
        if (body.length() > 250)
            return "Please enter a message of less than 250 characters";
        return "Message Sent";
    }

    @Override
    public Message composeMessage(String recipient, String body) {
        messageCounter++;
        String id = Message.generateMessageId();
        Message m = new Message(id, messageCounter, recipient, body);

        if (idCount    < messageIds.length)    messageIds[idCount++]       = id;
        if (hashCount  < messageHashes.length) messageHashes[hashCount++]  = m.getMessageHash();

        return m;
    }


    @Override
    public void sendMessage(Message m) {
        m.setStatus("SENT");
        if (sentCount < sentMessages.length) sentMessages[sentCount++] = m;
    }

    @Override
    public void disregardMessage(Message m) {
        m.setStatus("DISREGARDED");
        if (disregardedCount < disregardedMessages.length)
            disregardedMessages[disregardedCount++] = m;
    }

    @Override
    public void storeMessage(Message m) {
        m.setStatus("STORED");
        if (storedCount < storedMessages.length)
            storedMessages[storedCount++] = m;
        saveStoredToJson();
    }


    @Override public Message[] getSentMessages()        { return trim(sentMessages, sentCount); }
    @Override public Message[] getStoredMessages()      { return trim(storedMessages, storedCount); }
    @Override public Message[] getDisregardedMessages() { return trim(disregardedMessages, disregardedCount); }
    @Override public String[]  getMessageIds()          { return Arrays.copyOf(messageIds, idCount); }
    @Override public String[]  getMessageHashes()       { return Arrays.copyOf(messageHashes, hashCount); }


    @Override
    public String displayStoredSenderRecipient() {
        if (storedCount == 0) return "No stored messages.";
        StringBuilder sb = new StringBuilder("Sender -> Recipient:\n");
        for (int i = 0; i < storedCount; i++) {
            Message m = storedMessages[i];
            sb.append("  ").append(m.getRecipient())
              .append(" <- ").append("You")
              .append("  [hash=").append(m.getMessageHash()).append("]\n");
        }
        return sb.toString();
    }

    @Override
    public String findLongestStoredMessage() {
        if (storedCount == 0) return "No stored messages.";
        Message longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].getMessageBody().length()
                    > longest.getMessageBody().length()) {
                longest = storedMessages[i];
            }
        }
        return "Longest stored message: " + longest.getMessageBody();
    }

    @Override
    public Message searchByMessageId(String id) {
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].getMessageId().equals(id)) return storedMessages[i];
        }
        return null;
    }

    @Override
    public Message[] searchByRecipient(String recipient) {
        Message[] buffer = new Message[storedCount];
        int n = 0;
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].getRecipient().equals(recipient)) {
                buffer[n++] = storedMessages[i];
            }
        }
        return Arrays.copyOf(buffer, n);
    }

    @Override
    public boolean deleteByHash(String hash) {
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].getMessageHash().equalsIgnoreCase(hash)) {
                for (int j = i; j < storedCount - 1; j++) {
                    storedMessages[j] = storedMessages[j + 1];
                }
                storedMessages[--storedCount] = null;
                saveStoredToJson();
                return true;
            }
        }
        return false;
    }

    @Override
    public String generateReport() {
        if (storedCount == 0) return "=== Stored Messages Report ===\n(empty)";
        StringBuilder sb = new StringBuilder("=== Stored Messages Report ===\n");
        for (int i = 0; i < storedCount; i++) {
            sb.append(i + 1).append(". ").append(storedMessages[i]).append("\n");
        }
        return sb.toString();
    }


    @Override
    public void saveStoredToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Message[] live = trim(storedMessages, storedCount);
        try (FileWriter fw = new FileWriter(jsonFile)) {
            gson.toJson(live, fw);
        } catch (IOException e) {
            System.err.println("Could not save JSON: " + e.getMessage());
        }
    }

    @Override
    public void loadStoredFromJson() {
        Gson gson = new Gson();
        try (FileReader fr = new FileReader(jsonFile)) {
            Message[] loaded = gson.fromJson(fr, Message[].class);
            if (loaded == null) return;

            if (loaded.length > storedMessages.length) {
                storedMessages = new Message[loaded.length * 2];
            }
            storedCount = 0;
            for (Message m : loaded) {
                storedMessages[storedCount++] = m;
                if (m.getMessageNumber() > messageCounter)
                    messageCounter = m.getMessageNumber();
            }
        } catch (IOException e) {
        }
    }


    private Message[] trim(Message[] arr, int n) {
        return Arrays.copyOf(arr, n);
    }
}