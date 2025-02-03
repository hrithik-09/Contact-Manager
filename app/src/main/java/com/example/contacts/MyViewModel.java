package com.example.contacts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private Repository myRepository;

    private LiveData<List<Contact>> allContacts;

    public MyViewModel(@NonNull Application application) {
        super(application);
        this.myRepository = new Repository(application);
    }

    public LiveData<List<Contact>>getAllContacts(){
        allContacts= myRepository.getAllContact();
        return allContacts;
    }

    public void addNewContact(Contact contact){
        myRepository.addContact(contact);
    }

    public void deleteContact(Contact contact){
        myRepository.deleteContact(contact);
    }
}
