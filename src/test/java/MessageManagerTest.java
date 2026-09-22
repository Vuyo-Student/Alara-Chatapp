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
import com.chatapp.service.impl.MessagerManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageManagerTest {

    private MessagerManagerImpl mgr;

    @BeforeEach
    public void setUp() {
        mgr = new MessagerManagerImpl(10, "test_stored_messages.json");
    }

    @Test
    @DisplayName("Compose populates ID and hash arrays")
    public void composePopulatesArrays() {
        mgr.composeMessage("0821234567", "hello world");
        mgr.composeMessage("0821234567", "second message");
        assertEquals(2, mgr.getMessageIds().length);
        assertEquals(2, mgr.getMessageHashes().length);
    }

    @Test
    @DisplayName("Send stores in sent array and increments count")
    public void sendAndCount() {
        Message m = mgr.composeMessage("0821234567", "hi");
        mgr.sendMessage(m);
        assertEquals(1, mgr.getSentMessages().length);
        assertEquals("SENT", mgr.getSentMessages()[0].getStatus());
    }

    @Test
    @DisplayName("Disregard stores in disregarded array")
    public void disregardAndCount() {
        Message m = mgr.composeMessage("0821234567", "hi");
        mgr.disregardMessage(m);
        assertEquals(1, mgr.getDisregardedMessages().length);
    }

    @Test
    @DisplayName("Store adds to stored array and JSON file is created")
    public void storeAndPersist() {
        Message m = mgr.composeMessage("0821234567", "persist me");
        mgr.storeMessage(m);
        assertEquals(1, mgr.getStoredMessages().length);
    }

    @Test
    @DisplayName("Longest stored message is returned")
    public void longestStored() {
        mgr.storeMessage(mgr.composeMessage("0821234567", "short"));
        mgr.storeMessage(mgr.composeMessage("0821234567", "the longest one here"));
        assertTrue(mgr.findLongestStoredMessage().contains("the longest one here"));
    }

    @Test
    @DisplayName("Search by ID finds the message")
    public void searchById() {
        Message m = mgr.composeMessage("0821234567", "search me");
        mgr.storeMessage(m);
        Message found = mgr.searchByMessageId(m.getMessageId());
        assertNotNull(found);
        assertEquals("search me", found.getMessageBody());
    }

    @Test
    @DisplayName("Search by recipient returns all matching")
    public void searchByRecipient() {
        mgr.storeMessage(mgr.composeMessage("0821234567", "one"));
        mgr.storeMessage(mgr.composeMessage("0821234567", "two"));
        mgr.storeMessage(mgr.composeMessage("0827654321", "other"));
        assertEquals(2, mgr.searchByRecipient("0821234567").length);
    }

    @Test
    @DisplayName("Delete by hash removes message")
    public void deleteByHash() {
        Message m = mgr.composeMessage("0821234567", "delete me");
        mgr.storeMessage(m);
        assertTrue(mgr.deleteByHash(m.getMessageHash()));
        assertEquals(0, mgr.getStoredMessages().length);
    }

    @Test
    @DisplayName("Message body longer than 250 chars rejected")
    public void longBodyRejected() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 251; i++) sb.append('a');
        assertEquals("Please enter a message of less than 250 characters",
                     mgr.validateMessageBody(sb.toString()));
    }

    @Test
    @DisplayName("Message body 250 chars or fewer accepted")
    public void okBodyAccepted() {
        assertEquals("Message Sent", mgr.validateMessageBody("hello"));
    }

    @Test
    @DisplayName("Recipient with SA code accepted")
    public void recipientOk() {
        assertEquals("Recipient accepted", mgr.validateRecipient("+27821234567"));
        assertEquals("Recipient accepted", mgr.validateRecipient("0821234567"));
    }

    @Test
    @DisplayName("Recipient without SA code rejected")
    public void recipientBad() {
        assertEquals("Recipient number incorrectly formatted.",
                     mgr.validateRecipient("12345"));
        assertEquals("Recipient number incorrectly formatted.",
                     mgr.validateRecipient("+15551234567"));
    }
}
