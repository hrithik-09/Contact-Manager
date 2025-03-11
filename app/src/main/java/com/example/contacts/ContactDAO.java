package com.example.contacts;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {

    @Insert
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);

    @Query("SELECT * FROM contact_table ORDER BY firstName ASC,surname ASC")
    LiveData<List<Contact>> getAllContact();

    @Query("SELECT * FROM contact_table WHERE id = :contactId")
    LiveData<Contact> getContactById(int contactId);
}
