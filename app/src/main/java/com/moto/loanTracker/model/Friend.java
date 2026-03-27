package com.moto.loanTracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friends")
public class Friend {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phoneNumber;
    private String profileImageUri;
    private double balance;

    public Friend(String name, String phoneNumber, String profileImageUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImageUri = profileImageUri;
        this.balance = 0.0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfileImageUri() { return profileImageUri; }
    public void setProfileImageUri(String profileImageUri) { this.profileImageUri = profileImageUri; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
