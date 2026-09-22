/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp.service.impl;

/**
 *
 * @author SevenC
 */

import com.chatapp.service.Validator;


public class UserValidator implements Validator {

    public static final String USERNAME_OK =
        "Username successfully captured.";
    public static final String USERNAME_FAIL =
        "Username is not correctly formatted; please ensure that your username "
      + "contains an underscore and is no longer than 5 characters.";

    public static final String PASSWORD_OK  = "Password successfully captured";
    public static final String PASSWORD_FAIL =
        "Password is not correctly formatted please ensure that the password "
      + "is at least eight characters long , contains a capital letter, a "
      + "number and a special character";

    public static final String CELL_OK   = "Cellphone number successfully added";
    public static final String CELL_FAIL =
        "Cellphone number incorrectly formatted or does not contain international code";

    public static final String LOGIN_OK_PREFIX = "Welcome ";
    public static final String LOGIN_FAIL =
        "Username or password incorrect please try again";

    public static final String SA_PHONE_REGEX = "^(\\+27|0)[6-8][0-9]{8}$";

    
    @Override
    public String validateUsername(String username) {
        if (username == null) return USERNAME_FAIL;
        if (username.length() > 5) return USERNAME_FAIL;
        if (!username.contains("_")) return USERNAME_FAIL;
        return USERNAME_OK;
    }


    @Override
    public String validatePassword(String password) {
        if (password == null || password.length() < 8) return PASSWORD_FAIL;

        boolean hasUpper   = false;
        boolean hasDigit   = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c))       hasUpper   = true;
            else if (Character.isDigit(c))      hasDigit   = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        if (hasUpper && hasDigit && hasSpecial) return PASSWORD_OK;
        return PASSWORD_FAIL;
    }


    @Override
    public String validateCellphone(String cellphone) {
        if (cellphone == null) return CELL_FAIL;
        return cellphone.matches(SA_PHONE_REGEX) ? CELL_OK : CELL_FAIL;
    }


    public String validateLogin(String enteredUser, String enteredPass,
                                com.chatapp.model.User stored) {
        if (stored == null) return LOGIN_FAIL;
        if (!stored.getUsername().equals(enteredUser)) return LOGIN_FAIL;
        if (!stored.getPassword().equals(enteredPass)) return LOGIN_FAIL;

        return LOGIN_OK_PREFIX + stored.getFirstName() + " "
             + stored.getLastName() + ", It is great to see you again.";
    }
}