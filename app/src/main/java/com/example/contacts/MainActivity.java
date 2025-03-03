package com.example.contacts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
    private ArrayList<Contact> contactsArrayList = new ArrayList<>();
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



        //Database
        contactDatabase =ContactDatabase.getInstance(this);

        //ViewModel
        MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        Contact c1 = new Contact("hr@gmail.com","HR");
        viewModel.addNewContact(c1);

        viewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                contactsArrayList.clear();
                for (Contact c : contacts){
                    contactsArrayList.add(c);
                }
                contactAdapter.notifyDataSetChanged();
            }
        });
        //Adapter
        contactAdapter = new ContactAdapter(contactsArrayList);

        recyclerView.setAdapter(contactAdapter);
    }
}