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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MessageManagerImpl implements MessageManager {


    /**
     * Filename for JSON persistence.
     * Declared "final" because it is set once in the constructor.
     * Two constructors allow Main and tests to use different files
     * (so automated tests never pollute the production data).
     */
    private final String jsonFile;


    /** Messages the user chose "Send Message" on. */
    private Message[] sentMessages;

    /** Messages the user chose "Disregard Message" on. */
    private Message[] disregardedMessages;

    /** Messages the user chose "Store Message" on (also persisted to JSON). */
    private Message[] storedMessages;


    private int sentCount;
    private int disregardedCount;
    private int storedCount;

    /** Global auto-incrementing message number — starts at 0, becomes 1, 2, 3... */
    private int messageCounter = 0;


    /** Production constructor (used by Main). Writes to the default JSON file. */
    public MessageManagerImpl(int capacity) {
        this(capacity, "stored_messages.json");
    }

    /**
     * Test constructor — allows a separate JSON file per test run.
     * Uses Math.max to enforce a floor of 10 slots regardless of caller input.
     */
    public MessageManagerImpl(int capacity, String jsonFile) {
        this.jsonFile = jsonFile;
        int cap = Math.max(capacity, 10);          // Math.max = operator/method call
        sentMessages        = new Message[cap];    // array allocation
        disregardedMessages = new Message[cap];
        storedMessages      = new Message[cap];
    }


    /**
     * Validates the recipient cell number.
     *
     * Regex `^(\+27|0)[6-8][0-9]{8}$` accepts:
     *   - 0821234567    (SA national, 0 + 6/7/8 + 8 digits)
     *   - +27821234567  (SA international, +27 + 6/7/8 + 8 digits)
     *
     * Returns exactly "Recipient accepted" or the brief's error string.
     */
    @Override
    public String validateRecipient(String recipient) {
        if (recipient == null) return "Recipient number incorrectly formatted.";
        if (!recipient.matches("^(\\+27|0)[6-8][0-9]{8}$"))
            return "Recipient number incorrectly formatted.";
        return "Recipient accepted";
    }

    /**
     * Validates the message body length.
     * The brief requires a strict limit of 250 characters (inclusive).
     * The reply strings must match the brief exactly — the tests rely on them.
     */
    @Override
    public String validateMessageBody(String body) {
        if (body == null) return "Please enter a message of less than 250 characters";
        if (body.length() > 250)
            return "Please enter a message of less than 250 characters";
        return "Message Sent";
    }


    /**
     * Builds a new Message and returns it.
     * The ID, number and hash are all generated inside Message / here.
     *
     * Operators used:
     *   messageCounter++   pre-increment the running counter
     *
     * The returned Message is NOT filed yet — the caller decides
     * (sendMessage / storeMessage / disregardMessage).
     */
    @Override
    public Message composeMessage(String recipient, String body) {
        messageCounter++;                                   // 1, 2, 3, ...
        String id = Message.generateMessageId();            // 10 random digits
        return new Message(id, messageCounter, recipient, body);
    }


    /**
     * "Send Message": status SENT, then file in sentMessages[].
     * Uses a guard check so we never write past the array end.
     */
    @Override
    public void sendMessage(Message m) {
        m.setStatus("SENT");
        if (sentCount < sentMessages.length) sentMessages[sentCount++] = m;
    }

    /**
     * "Disregard Message": status DISREGARDED, file in disregardedMessages[].
     */
    @Override
    public void disregardMessage(Message m) {
        m.setStatus("DISREGARDED");
        if (disregardedCount < disregardedMessages.length)
            disregardedMessages[disregardedCount++] = m;
    }

    /**
     * "Store Message": status STORED, file in storedMessages[], persist to JSON.
     * Persistence is immediate — the JSON file is updated on every store, so
     * a crash after this call cannot lose the message.
     */
    @Override
    public void storeMessage(Message m) {
        m.setStatus("STORED");
        if (storedCount < storedMessages.length)
            storedMessages[storedCount++] = m;
        saveStoredToJson();                                 // write-through
    }


    /**
     * Returns a trimmed copy of the sent messages.
     * Arrays.copyOf returns only the filled portion, so the caller does
     * not see trailing nulls.
     */
    @Override
    public Message[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentCount);
    }


    /**
     * Writes all stored messages to disk.
     * Gson handles the Message[] -> JSON conversion automatically.
     * Uses try-with-resources so the FileWriter closes even on error.
     */
    @Override
    public void saveStoredToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Message[] live = Arrays.copyOf(storedMessages, storedCount);
        try (FileWriter fw = new FileWriter(jsonFile)) {
            gson.toJson(live, fw);
        } catch (IOException e) {
            System.err.println("Could not save JSON: " + e.getMessage());
        }
    }
}