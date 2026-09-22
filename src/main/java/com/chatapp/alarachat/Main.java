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
import com.chatapp.model.User;
import com.chatapp.service.impl.MessagerManagerImpl;
import com.chatapp.service.impl.UserValidator;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserValidator validator = new UserValidator();

    private static User currentUser;
    private static MessagerManagerImpl manager;

    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println("        WELCOME TO ALARA CHAT");
        System.out.println("=========================================");

        try {
            currentUser = registerFlow();
            if (currentUser == null) {
                System.out.println("Registration aborted. Goodbye.");
                pauseBeforeExit();
                return;
            }

            if (!loginFlow(currentUser)) {
                System.out.println("Login failed 3 times. Goodbye.");
                pauseBeforeExit();
                return;
            }

            System.out.print("\nHow many messages do you wish to send in this session? ");
            int count = readIntSafely();
            manager = new MessagerManagerImpl(count);
            manager.loadStoredFromJson();

            boolean running = true;
            while (running) {
                printMainMenu();
                int choice = readIntSafely();
                switch (choice) {
                    case 1: sendMessagesFlow(count);  break;
                    case 2: System.out.println("Coming Soon"); break;
                    case 3: running = false;           break;
                    case 4: storedMessagesMenu();      break;
                    default: System.out.println("Invalid option. Try again.");
                }
            }
            System.out.println("\nGoodbye!");

        } catch (NoSuchElementException e) {
            System.out.println("\n\n[!] No input detected on standard input.");
            System.out.println("    If you are running inside NetBeans, the Output");
            System.out.println("    pane did not capture your keystrokes.");
            System.out.println("    Re-run with:");
            System.out.println("        java -jar target/AlaraChat-1.0-SNAPSHOT.jar");
            System.out.println("    from an external Command Prompt / Terminal.");
        } finally {
            pauseBeforeExit();
        }
    }

    private static User registerFlow() {
        System.out.println("\n--- REGISTRATION ---");

        System.out.print("First name: ");
        String first = sc.nextLine().trim();

        System.out.print("Last name: ");
        String last = sc.nextLine().trim();

        String username;
        while (true) {
            System.out.print("Username (must contain '_', max 5 chars): ");
            username = sc.nextLine().trim();
            String msg = validator.validateUsername(username);
            System.out.println(msg);
            if (msg.equals(UserValidator.USERNAME_OK)) break;
        }

        String password;
        while (true) {
            System.out.print("Password (>=8, capital, digit, special): ");
            password = sc.nextLine();
            String msg = validator.validatePassword(password);
            System.out.println(msg);
            if (msg.equals(UserValidator.PASSWORD_OK)) break;
        }

        String cell;
        while (true) {
            System.out.print("Cellphone (e.g. +27821234567 or 0821234567): ");
            cell = sc.nextLine().trim();
            String msg = validator.validateCellphone(cell);
            System.out.println(msg);
            if (msg.equals(UserValidator.CELL_OK)) break;
        }

        return new User(first, last, username, password, cell);
    }


    private static boolean loginFlow(User stored) {
        System.out.println("\n--- LOGIN ---");
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Username (attempt " + attempt + "/3): ");
            String u = sc.nextLine().trim();
            System.out.print("Password: ");
            String p = sc.nextLine();

            String msg = validator.validateLogin(u, p, stored);
            System.out.println(msg);
            if (msg.startsWith(UserValidator.LOGIN_OK_PREFIX)) return true;

            System.out.println("    [debug] stored username = '" + stored.getUsername() + "'");
            System.out.println("    [debug] stored password = '" + stored.getPassword() + "'");
            System.out.println("    [debug] typed  username = '" + u + "'");
            System.out.println("    [debug] typed  password = '" + p + "'");
        }
        return false;
    }
    private static void printMainMenu() {
        System.out.println("\n============ MAIN MENU ============");
        System.out.println(" 1) Send message");
        System.out.println(" 2) Show recently sent messages");
        System.out.println(" 3) Quit");
        System.out.println(" 4) Stored Messages");
        System.out.print("Choice: ");
    }

    private static void sendMessagesFlow(int allowed) {
        if (allowed <= 0) {
            System.out.println("No messages allowed this session.");
            return;
        }
        for (int i = 0; i < allowed; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + allowed + " ---");

            System.out.print("Recipient cell number: ");
            String recipient = sc.nextLine().trim();
            if (!"Recipient accepted".equals(manager.validateRecipient(recipient))) {
                System.out.println("Recipient number incorrectly formatted.");
                i--;
                continue;
            }

            System.out.print("Message (max 250 chars): ");
            String body = sc.nextLine();
            String bodyCheck = manager.validateMessageBody(body);
            if (!"Message Sent".equals(bodyCheck)) {
                System.out.println(bodyCheck);
                i--;
                continue;
            }

            Message m = manager.composeMessage(recipient, body);
            System.out.println("\n--- Preview ---");
            System.out.println(m);

            System.out.println("\nWhat would you like to do?");
            System.out.println("  1) Send Message");
            System.out.println("  2) Store Message");
            System.out.println("  3) Disregard Message");
            System.out.print("Choice: ");
            int action = readIntSafely();

            switch (action) {
                case 1:
                    manager.sendMessage(m);
                    System.out.println("Message successfully sent");
                    break;
                case 2:
                    manager.storeMessage(m);
                    System.out.println("Message successfully stored");
                    break;
                case 3:
                    manager.disregardMessage(m);
                    System.out.println("Press 0 to delete the message");
                    break;
                default:
                    System.out.println("Unknown action – treating as disregard");
                    manager.disregardMessage(m);
                    System.out.println("Press 0 to delete the message");
            }
        }
        System.out.println("\nTotal messages sent this session: "
                         + manager.getSentMessages().length);
    }

    private static void storedMessagesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== STORED MESSAGES =====");
            System.out.println(" 1) Show sender and recipient of all stored messages");
            System.out.println(" 2) Show longest stored message");
            System.out.println(" 3) Search by message ID");
            System.out.println(" 4) Search all messages for a recipient");
            System.out.println(" 5) Delete a message using hash");
            System.out.println(" 6) Full report of stored messages");
            System.out.println(" 7) Disregard message by hash");
            System.out.println(" 0) Back");
            System.out.print("Choice: ");
            int c = readIntSafely();

            switch (c) {
                case 1: System.out.println(manager.displayStoredSenderRecipient()); break;
                case 2: System.out.println(manager.findLongestStoredMessage());     break;
                case 3:
                    System.out.print("Message ID: ");
                    String id = sc.nextLine().trim();
                    Message found = manager.searchByMessageId(id);
                    System.out.println(found == null
                        ? "No message with that ID."
                        : "Recipient: " + found.getRecipient()
                        + "\nBody: "     + found.getMessageBody());
                    break;
                case 4:
                    System.out.print("Recipient: ");
                    String rec = sc.nextLine().trim();
                    Message[] list = manager.searchByRecipient(rec);
                    if (list.length == 0) System.out.println("None found.");
                    else for (Message m : list) System.out.println(m);
                    break;
                case 5:
                    System.out.print("Message hash: ");
                    String h = sc.nextLine().trim();
                    System.out.println(manager.deleteByHash(h)
                        ? "Message deleted." : "Hash not found.");
                    break;
                case 6: System.out.println(manager.generateReport()); break;
                case 7:
    Message[] sent = manager.getSentMessages();
    if (sent.length == 0) {
        System.out.println("No sent messages to disregard.");
        break;
    }
    System.out.println("Sent messages available:");
    for (Message m : sent) System.out.println("  " + m.getMessageHash());
    System.out.print("Hash to disregard: ");
    String dh = sc.nextLine().trim();
    System.out.println(manager.disregardSentByHash(dh)
        ? "Message moved to disregarded."
        : "Hash not found in sent messages.");
    break;
                case 0: back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static int readIntSafely() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a whole number: ");
            }
        }
    }

    private static void pauseBeforeExit() {
        System.out.println("\nPress Enter to close...");
        try {
            sc.nextLine();
        } catch (Exception ignored) { }
    }
}
