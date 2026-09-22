/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp.model;

/**
 *
 * @author SevenC
 */

import java.io.Serializable;
import java.util.Random;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String messageId;
    private int    messageNumber;
    private String recipient;
    private String messageBody;
    private String messageHash;
    private String status;

    public Message() { }

    public Message(String messageId, int messageNumber,
                   String recipient, String messageBody) {
        this.messageId     = messageId;
        this.messageNumber = messageNumber;
        this.recipient     = recipient;
        this.messageBody   = messageBody;
        this.messageHash   = generateHash();
    }

    
    public static String generateMessageId() {
        StringBuilder sb = new StringBuilder(10);
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }

    
    private String generateHash() {
        String id = (messageId == null || messageId.length() < 2)
                  ? "00" : messageId.substring(0, 2);

        String body = (messageBody == null) ? "" : messageBody.trim();
        String firstWord, lastWord;

        if (body.isEmpty()) {
            firstWord = "";
            lastWord  = "";
        } else {

            
            String[] parts = body.split("\\s+");
            firstWord = parts[0];
            lastWord  = parts[parts.length - 1];
        }
        return (id + ":" + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase();
    }


    public String getMessageId()     { return messageId; }
    public int    getMessageNumber() { return messageNumber; }
    public String getRecipient()     { return recipient; }
    public String getMessageBody()   { return messageBody; }
    public String getMessageHash()   { return messageHash; }
    public String getStatus()        { return status; }

    public void setMessageId(String v)     { this.messageId = v; }
    public void setMessageNumber(int v)    { this.messageNumber = v; }
    public void setRecipient(String v)     { this.recipient = v; }
    public void setMessageBody(String v)   { this.messageBody = v; }
    public void setMessageHash(String v)   { this.messageHash = v; }
    public void setStatus(String v)        { this.status = v; }

    @Override
    public String toString() {
        return String.format(
            "ID: %s | #%d | To: %s | Status: %s | Hash: %s | Body: %s",
            messageId, messageNumber, recipient, status, messageHash, messageBody);
    }
}
