/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.chatapp;

/**
 *
 * @author SevenC
 */

import com.chatapp.model.User;
import com.chatapp.service.impl.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidatorTest {

    private UserValidator v;

    @BeforeEach
    public void setUp() {
        v = new UserValidator();
    }

    // ---------------- USERNAME ----------------
    @Test
    @DisplayName("Username with underscore & 5 or fewer chars passes")
    public void usernamePasses() {
        assertEquals(UserValidator.USERNAME_OK, v.validateUsername("k_yl"));
        assertEquals(UserValidator.USERNAME_OK, v.validateUsername("A_b"));
    }

    @Test
    @DisplayName("Username without underscore fails")
    public void usernameNoUnderscore() {
        assertEquals(UserValidator.USERNAME_FAIL, v.validateUsername("kyle"));
    }

    @Test
    @DisplayName("Username longer than 5 chars fails")
    public void usernameTooLong() {
        assertEquals(UserValidator.USERNAME_FAIL, v.validateUsername("k_yle1"));
    }

    // ---------------- PASSWORD ----------------
    @Test
    @DisplayName("Valid password passes")
    public void passwordPasses() {
        assertEquals(UserValidator.PASSWORD_OK, v.validatePassword("Passw0rd!"));
    }

    @Test
    @DisplayName("Short password fails")
    public void passwordTooShort() {
        assertEquals(UserValidator.PASSWORD_FAIL, v.validatePassword("Ab1!"));
    }

    @Test
    @DisplayName("Password without capital fails")
    public void passwordNoCapital() {
        assertEquals(UserValidator.PASSWORD_FAIL, v.validatePassword("passw0rd!"));
    }

    @Test
    @DisplayName("Password without digit fails")
    public void passwordNoDigit() {
        assertEquals(UserValidator.PASSWORD_FAIL, v.validatePassword("Password!"));
    }

    @Test
    @DisplayName("Password without special char fails")
    public void passwordNoSpecial() {
        assertEquals(UserValidator.PASSWORD_FAIL, v.validatePassword("Password1"));
    }

    // ---------------- CELLPHONE ----------------
    @Test
    @DisplayName("Valid SA numbers pass")
    public void cellphonePasses() {
        assertEquals(UserValidator.CELL_OK, v.validateCellphone("0821234567"));
        assertEquals(UserValidator.CELL_OK, v.validateCellphone("+27821234567"));
        assertEquals(UserValidator.CELL_OK, v.validateCellphone("0721234567"));
    }

    @Test
    @DisplayName("Invalid cellphone fails")
    public void cellphoneFails() {
        assertEquals(UserValidator.CELL_FAIL, v.validateCellphone("12345"));

        assertEquals(UserValidator.CELL_FAIL, v.validateCellphone("0991234567"));

        assertEquals(UserValidator.CELL_FAIL, v.validateCellphone("082123456"));

        assertEquals(UserValidator.CELL_FAIL, v.validateCellphone("08212345678"));
    }

    // ---------------- LOGIN ----------------
    @Test
    @DisplayName("Correct login returns welcome message")
    public void loginSuccess() {
        User u = new User("Kyle", "Smith", "k_yl", "Passw0rd!", "0821234567");
        assertEquals(
            "Welcome Kyle Smith, It is great to see you again.",
            v.validateLogin("k_yl", "Passw0rd!", u));
    }

    @Test
    @DisplayName("Wrong password returns error")
    public void loginWrongPassword() {
        User u = new User("Kyle", "Smith", "k_yl", "Passw0rd!", "0821234567");
        assertEquals(UserValidator.LOGIN_FAIL, v.validateLogin("k_yl", "wrong", u));
    }

    @Test
    @DisplayName("Wrong username returns error")
    public void loginWrongUsername() {
        User u = new User("Kyle", "Smith", "k_yl", "Passw0rd!", "0821234567");
        assertEquals(UserValidator.LOGIN_FAIL, v.validateLogin("nope", "Passw0rd!", u));
    }
}
