package com.example.contacts;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactDatabase contactDatabase;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private ActivityMainBinding mainBinding;
    private MainActivityClickHandlers handlers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Data binding
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        handlers = new MainActivityClickHandlers(this);
        mainBinding.setClickHandler(handlers);

        //RecyclerView
        RecyclerView recyclerView = mainBinding.recyclerViewContactList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //Adapter
        contactAdapter = new ContactAdapter(contacts);

        //Database
        contactDatabase =ContactDatabase.getInstance(this);

        //ViewModel
        MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        Contact c1 = new Contact(1,"hr@gmail.com","HR");
        viewModel.addNewContact(c1);

        viewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {

            }
        });
        viewModel.getAllContacts();

        recyclerView.setAdapter(contactAdapter);
    }
}