package com.example.contacts;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.contacts.databinding.ActivityAddNewContactBinding;

public class AddNewContact extends AppCompatActivity {
    private ActivityAddNewContactBinding binding;
    private AddNewContactClickHandler handler;
    private Contact contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        contacts = new Contact();

        binding= DataBindingUtil.setContentView(
                this,
                R.layout.activity_add_new_contact
        );

        // VIew Model
        MyViewModel myViewModel = new ViewModelProvider(this)
                .get(MyViewModel.class);


        handler = new AddNewContactClickHandler(
                contacts,
                this,
                myViewModel
        );

        binding.setContact(contacts);
        binding.setClickHandler(handler);
    }
}