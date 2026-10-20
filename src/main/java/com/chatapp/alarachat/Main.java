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
import com.chatapp.service.impl.MessageManagerImpl;
import com.chatapp.service.impl.UserValidator;

import java.util.NoSuchElementException;
import java.util.Scanner;


 //Part 3 (stored messages menu) has been removed.

public class Main {

    //Shared state (static = one instance for the whole app)
    private static final Scanner sc = new Scanner(System.in);
    private static final UserValidator validator = new UserValidator();

    private static User currentUser;
    private static MessageManagerImpl manager;


    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println("        WELCOME TO ALARA");
        System.out.println("=========================================");

        try {
            //Registration
            currentUser = registerFlow();
            if (currentUser == null) {
                System.out.println("Registration aborted. Goodbye.");
                pauseBeforeExit();
                return;
            }

            //Login
            if (!loginFlow(currentUser)) {
                System.out.println("Login failed 3 times. Goodbye.");
                pauseBeforeExit();
                return;
            }

            //How many messages this session
            System.out.print("\nHow many messages do you wish to send in this session? ");
            int count = readIntSafely();
            manager = new MessageManagerImpl(count);

            //Main menu — runs until Quit
            boolean running = true;
            while (running) {                                    // Loop construct
                printMainMenu();
                int choice = readIntSafely();                    // Data type: int
                switch (choice) {                                // Decision construct
                    case 1: sendMessagesFlow(count);  break;
                    case 2: System.out.println("Coming Soon"); break;
                    case 3: running = false;           break;    // quit
                    default: System.out.println("Invalid option. Try again.");
                }
            }
            System.out.println("\nGoodbye!");

        } catch (NoSuchElementException e) {
            // Common when running inside an IDE whose Output pane is not
            // connected to standard input. Gives the user a recoverable hint.
            System.out.println("\n\n[!] No input detected on standard input.");
            System.out.println("    Re-run from an external terminal:");
            System.out.println("        java -jar target/AlaraChat-1.0-SNAPSHOT.jar");
        } finally {
            pauseBeforeExit();
        }
    }

    
    /**
     * Asks for the five registration fields.
     * The three validators loop until the user supplies a valid value —
     * the app never accepts invalid input into the User object.
     *
     * Constructs: Loops (while-true), Decisions (equals check on the message),
     *             Variables (local first, last, username, password, cell).
     */
    private static User registerFlow() {
        System.out.println("\n--- REGISTRATION ---");

        System.out.print("First name: ");
        String first = sc.nextLine().trim();

        System.out.print("Last name: ");
        String last = sc.nextLine().trim();

        //Username: must contain '_' and be <= 5 chars
        String username;
        while (true) {
            System.out.print("Username (must contain '_', max 5 chars): ");
            username = sc.nextLine().trim();
            String msg = validator.validateUsername(username);
            System.out.println(msg);
            if (msg.equals(UserValidator.USERNAME_OK)) break;
        }

        //Password: >=8 chars, capital, digit, special
        String password;
        while (true) {
            System.out.print("Password (>=8, capital, digit, special): ");
            password = sc.nextLine();
            String msg = validator.validatePassword(password);
            System.out.println(msg);
            if (msg.equals(UserValidator.PASSWORD_OK)) break;
        }

        //Cellphone: SA format (0[6-8]... or +27[6-8]...)
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


    /**
     * Gives the user three chances to enter the correct credentials.
     * Returns true on success, false after the third failure.
     *
     * Constructs: For loop (bounded attempts), Decision (startsWith check),
     *             Method call chain (validator.validateLogin).
     */
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
        }
        return false;
    }


    /**
     * Prints the top-level menu. Called inside the while loop in main().
     * Part 3's "Stored Messages" option has been removed.
     */
    private static void printMainMenu() {
        System.out.println("\n============ MAIN MENU ============");
        System.out.println(" 1) Send message");
        System.out.println(" 2) Show recently sent messages");
        System.out.println(" 3) Quit");
        System.out.print("Choice: ");
    }


    /**
     * Runs the send-message flow {@code allowed} times.
     *
     * Two inner re-prompt rules:
     *   - invalid recipient → i-- (slot not consumed)
     *   - body too long     → i-- (slot not consumed)
     * This satisfies the brief's "app should allow only the set number
     * of messages" while still letting the user correct mistakes.
     *
     * Constructs: For loop, Decisions (if/else, switch),
     *             Operators (i--, i++, !, ==), Arrays (getSentMessages().length).
     */
    private static void sendMessagesFlow(int allowed) {

        // Guard: no messages configured
        if (allowed <= 0) {
            System.out.println("No messages allowed this session.");
            return;
        }

        for (int i = 0; i < allowed; i++) {                       // Loop construct
            System.out.println("\n--- Message " + (i + 1) + " of " + allowed + " ---");

            //Step 1: Recipient 
            System.out.print("Recipient cell number: ");
            String recipient = sc.nextLine().trim();

            if (!"Recipient accepted".equals(manager.validateRecipient(recipient))) {
                System.out.println("Recipient number incorrectly formatted.");
                i--;                                             // do not consume slot
                continue;                                        // restart this iteration
            }

            //Step 2: Body
            System.out.print("Message (max 250 chars): ");
            String body = sc.nextLine();

            String bodyCheck = manager.validateMessageBody(body);
            if (!"Message Sent".equals(bodyCheck)) {
                System.out.println(bodyCheck);                   // exact brief message
                i--;                                             // do not consume slot
                continue;
            }

            //Step 3: Compose (ID + number + hash generated automatically)
            Message m = manager.composeMessage(recipient, body);

            //Step 4: Preview (uses Message.toString())
            System.out.println("\n--- Preview ---");
            System.out.println(m);

            //Step 5: Choose what to do
            System.out.println("\nWhat would you like to do?");
            System.out.println("  1) Send Message");
            System.out.println("  2) Store Message");
            System.out.println("  3) Disregard Message");
            System.out.print("Choice: ");
            int action = readIntSafely();

            switch (action) {                                    // Decision construct
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
                    // Unknown choice: safest is to disregard, so the slot is
                    // consumed but nothing enters the sent or stored arrays.
                    System.out.println("Unknown action – treating as disregard");
                    manager.disregardMessage(m);
                    System.out.println("Press 0 to delete the message");
            }
        }

        // Running total (array length gives the sent count)
        System.out.println("\nTotal messages sent this session: "
                         + manager.getSentMessages().length);
    }


    /**
     * Keeps re-prompting until the user types a whole number.
     */
    private static int readIntSafely() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a whole number: ");
            }
        }
    }

    /** Pauses before exit so the user can read the final output. */
    private static void pauseBeforeExit() {
        System.out.println("\nPress Enter to close...");
        try { sc.nextLine(); } catch (Exception ignored) { }
    }
}