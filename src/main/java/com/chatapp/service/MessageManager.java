/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp.service;

/**
 *
 * @author SevenC
 */

import com.chatapp.model.Message;


public interface MessageManager {

    /**
     * Validates a recipient cell number.
     * Returns "Recipient accepted" or the exact error string from the brief.
     */
    String validateRecipient(String recipient);

    /**
     * Validates body length (max 250 characters).
     * Returns "Message Sent" or "Please enter a message of less than 250 characters".
     */
    String validateMessageBody(String body);

    /**
     * Composes a new message:
     *   - generates a 10-digit random ID,
     *   - increments the message number,
     *   - builds the message hash.
     */
    Message composeMessage(String recipient, String body);

    /** Choice 1: "Send Message" — marks the message as SENT. */
    void sendMessage(Message m);

    /** Choice 3: "Disregard Message" — marks the message as DISREGARDED. */
    void disregardMessage(Message m);

    /** Choice 2: "Store Message" — marks the message as STORED and writes to JSON. */
    void storeMessage(Message m);

    /** Returns only the messages that were actually sent. */
    Message[] getSentMessages();

    /** Writes all currently stored messages to the JSON file. */
    void saveStoredToJson();
}