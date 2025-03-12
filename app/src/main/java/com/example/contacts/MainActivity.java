package com.example.contacts;
import com.example.contacts.BuildConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactDatabase contactDatabase;
    private FloatingActionButton addContactIcon;
    private ImageView add,toggle;
    private Switch darkModeSwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DrawerLayout drawerLayout;
    private ArrayList<Contact> contactsArrayList = new ArrayList<>();
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", isSystemDarkModeEnabled());
        darkModeSwitch=findViewById(R.id.dark_mode_switch);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            updateSwitchText();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            updateSwitchText();
        }

        // Set initial switch text based on the current mode
        updateSwitchText();
        darkModeSwitch.setChecked(isDarkMode);

        darkModeSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Save user preference
            editor = sharedPreferences.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();

            recreate();
        });

        //RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewContactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //Database
        contactDatabase =ContactDatabase.getInstance(this);

        //ViewModel
        MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);

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

        // swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // If you swipe the item to the left
                Contact c = contactsArrayList.get(viewHolder.getAdapterPosition());

                viewModel.deleteContact(c);
            }
        }).attachToRecyclerView(recyclerView);

        addContactIcon=findViewById(R.id.addContactIcon);
        add=findViewById(R.id.add);
        toggle=findViewById(R.id.toggle);
        addContactIcon.setOnClickListener(view -> {
            Intent i = new Intent(this,AddNewContact.class);
            startActivity(i);
        });

        add.setOnClickListener(view -> {
            Intent i = new Intent(this,AddNewContact.class);
            startActivity(i);
        });

        drawerLayout=findViewById(R.id.drawer_layout);

        toggle.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);

        });






    // Version details
        TextView versionDetail = findViewById(R.id.version_detail);
        String versionName = BuildConfig.VERSION_NAME;
        versionDetail.setText("Version "+versionName);



    }
    private void updateSwitchText() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkModeSwitch.setText("Dark Mode");
        } else {
            darkModeSwitch.setText("Light Mode");
        }
    }


    private boolean isSystemDarkModeEnabled() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

}