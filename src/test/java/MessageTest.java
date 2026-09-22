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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

    @Test
    @DisplayName("Message ID has exactly 10 characters, all digits")
    public void idFormat() {
        for (int i = 0; i < 20; i++) {
            String id = Message.generateMessageId();
            assertEquals(10, id.length());
            assertTrue(id.matches("[0-9]{10}"));
        }
    }

    @Test
    @DisplayName("Hash format = first2(ID):num:FIRSTWORDLASTWORD (uppercase)")
    public void hashFormat() {
        Message m = new Message("1234567890", 3, "0821234567",
                                "hello world this is goodbye");
        assertEquals("12:3:HELLOGOODBYE", m.getMessageHash());
    }

    @Test
    @DisplayName("Hash handles single-word body")
    public void hashSingleWord() {
        Message m = new Message("9876543210", 1, "0821234567", "hi");
        assertEquals("98:1:HIHI", m.getMessageHash());
    }

    @Test
    @DisplayName("Hash handles empty body")
    public void hashEmpty() {
        Message m = new Message("5555555555", 2, "0821234567", "");
        assertEquals("55:2:", m.getMessageHash());
    }
}
