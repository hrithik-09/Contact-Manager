package com.example.contacts;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "contact_table")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String profileImageUri; // Store URI as a string
    private String prefix;
    private String firstName;
    private String surname;
    private String email;
    private String birthday;
    private String address;

    private String mobileNumber;

    public Contact(int id, String profileImageUri, String prefix, String firstName, String surname, String email, String birthday, String address, String mobileNumber) {
        this.id = id;
        this.profileImageUri = profileImageUri;
        this.prefix = prefix;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
        this.address = address;
        this.mobileNumber = mobileNumber;
    }

    public Contact(String profileImageUri, String prefix, String firstName, String surname,
                   String email, String birthday, String address, String mobileNumber) {
        this.profileImageUri = profileImageUri;
        this.prefix = prefix;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
        this.address = address;
        this.mobileNumber = mobileNumber;
    }

    public Contact() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProfileImageUri() { return profileImageUri; }
    public void setProfileImageUri(String profileImageUri) { this.profileImageUri = profileImageUri; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

}
