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

    Message composeMessage(String recipient, String body);
    String  validateRecipient(String recipient);
    String  validateMessageBody(String body);

    void storeMessage(Message m);
    void sendMessage(Message m);
    void disregardMessage(Message m);

    Message[] getSentMessages();
    Message[] getStoredMessages();
    Message[] getDisregardedMessages();
    String[]  getMessageIds();
    String[]  getMessageHashes();

    String displayStoredSenderRecipient();
    String findLongestStoredMessage();
    Message searchByMessageId(String id);
    Message[] searchByRecipient(String recipient);
    boolean deleteByHash(String hash);
    boolean disregardSentByHash(String hash);
    String generateReport();

    void saveStoredToJson();
    void loadStoredFromJson();
}
