package com.example.contacts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;
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

    public void updateExistingContact(Contact contact){
        myRepository.updateContact(contact);
    }

    public void deleteContact(Contact contact){
        if (contact.getProfileImageUri() != null) {
            deleteImage(contact.getProfileImageUri());
        }
        myRepository.deleteContact(contact);
    }

    public LiveData<Contact> getContactById(int contactId) {
        return myRepository.getContactById(contactId);
    }

    private void deleteImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }
}
