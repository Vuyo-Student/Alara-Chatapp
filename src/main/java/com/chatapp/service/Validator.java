/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp.service;

/**
 *
 * @author SevenC
 */

public interface Validator {

    String validateUsername(String username);
    String validatePassword(String password);
    String validateCellphone(String cellphone);
}
