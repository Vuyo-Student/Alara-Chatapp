/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp.model;

/**
 *
 * @author SevenC
 */


public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellphone;

    public User() { }

    public User(String firstName, String lastName,
                String username, String password, String cellphone) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.username  = username;
        this.password  = password;
        this.cellphone = cellphone;
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName;  }
    public String getUsername()  { return username;  }
    public String getPassword()  { return password;  }
    public String getCellphone() { return cellphone; }

    public void setFirstName(String v) { this.firstName = v; }
    public void setLastName(String v)  { this.lastName  = v; }
    public void setUsername(String v)  { this.username  = v; }
    public void setPassword(String v)  { this.password  = v; }
    public void setCellphone(String v) { this.cellphone = v; }
}
