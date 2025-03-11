package com.example.contacts;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    //available data source:
    //Room db

    private final ContactDAO contactDAO;
    ExecutorService executor;

    Handler handler;
    public Repository(Application application) {
        ContactDatabase contactDatabase=ContactDatabase.getInstance(application);
        this.contactDAO = contactDatabase.getContactDAO();
        //  Used for background database operation
        executor= Executors.newSingleThreadExecutor();

        //  Used for updating the UI
        handler=new Handler(Looper.getMainLooper());
    }

    public void addContact(Contact contact){


        executor.execute(new Runnable() {
            @Override
            public void run() {
            contactDAO.insert(contact);
            }
        });
    }

    public void updateContact(Contact contact){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                contactDAO.update(contact);
            }
        });
    }
    public void deleteContact(Contact contact){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                contactDAO.delete(contact);
            }
        });
    }
    public LiveData<Contact> getContactById(int contactId) {
        return contactDAO.getContactById(contactId);
    }
    public LiveData<List<Contact>>getAllContact(){
        return contactDAO.getAllContact();
    }
}
