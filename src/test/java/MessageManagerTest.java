/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.chatapp;

/**
 *
 * @author SevenC
 */

import com.chatapp.model.Message;
import com.chatapp.service.impl.MessageManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MessageManagerTest {

    private MessageManagerImpl mgr;

    /**
     * Runs before every test.
     * Uses a separate JSON file so tests never touch the production data
     * or leave stale entries behind for the next test run.
     */
    @BeforeEach
    public void setUp() {
        mgr = new MessageManagerImpl(10, "test_stored_messages.json");
    }



    @Test
    @DisplayName("Compose generates a 10-digit ID and increments the number")
    public void composeGeneratesIdAndNumber() {
        Message m1 = mgr.composeMessage("0821234567", "hello world");
        Message m2 = mgr.composeMessage("0821234567", "second message");

        // ID is 10 characters long
        assertEquals(10, m1.getMessageId().length());
        // Message numbers auto-increment: 1, then 2
        assertEquals(1,  m1.getMessageNumber());
        assertEquals(2,  m2.getMessageNumber());
    }



    @Test
    @DisplayName("Send sets status SENT and adds the message to the sent array")
    public void sendAndCount() {
        Message m = mgr.composeMessage("0821234567", "hi");
        mgr.sendMessage(m);

        // One message in the sent array
        assertEquals(1, mgr.getSentMessages().length);
        // Status was updated to SENT
        assertEquals("SENT", m.getStatus());
    }

    @Test
    @DisplayName("Disregard sets status DISREGARDED")
    public void disregardAndCount() {
        Message m = mgr.composeMessage("0821234567", "hi");
        mgr.disregardMessage(m);
        assertEquals("DISREGARDED", m.getStatus());
    }

    @Test
    @DisplayName("Store sets status STORED")
    public void storeAndCount() {
        Message m = mgr.composeMessage("0821234567", "persist me");
        mgr.storeMessage(m);
        assertEquals("STORED", m.getStatus());
    }



    @Test
    @DisplayName("Message body over 250 chars is rejected")
    public void longBodyRejected() {
        // Build a 251-character string (one over the limit)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 251; i++) sb.append('a');

        // The exact error string from the brief
        assertEquals("Please enter a message of less than 250 characters",
                     mgr.validateMessageBody(sb.toString()));
    }

    @Test
    @DisplayName("Message body 250 chars or fewer is accepted")
    public void okBodyAccepted() {
        assertEquals("Message Sent", mgr.validateMessageBody("hello"));
    }



    @Test
    @DisplayName("Recipient in SA format is accepted")
    public void recipientOk() {
        // International format +27…
        assertEquals("Recipient accepted", mgr.validateRecipient("+27821234567"));
        // National format 08…
        assertEquals("Recipient accepted", mgr.validateRecipient("0821234567"));
    }

    @Test
    @DisplayName("Recipient outside SA format is rejected")
    public void recipientBad() {
        // Too short
        assertEquals("Recipient number incorrectly formatted.",
                     mgr.validateRecipient("12345"));
        // Non-SA international prefix
        assertEquals("Recipient number incorrectly formatted.",
                     mgr.validateRecipient("+15551234567"));
    }
}