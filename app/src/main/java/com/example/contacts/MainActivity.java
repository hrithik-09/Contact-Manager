package com.example.contacts;
import com.example.contacts.BuildConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
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
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact c = contactsArrayList.get(viewHolder.getAdapterPosition());

                if (direction == ItemTouchHelper.LEFT) {
                    new AlertDialog.Builder(viewHolder.itemView.getContext())
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this contact?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                viewModel.deleteContact(c); // Proceed with deletion
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                            })
                            .setCancelable(false)
                            .show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + c.getMobileNumber()));
                    viewHolder.itemView.getContext().startActivity(callIntent);
                    recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isCurrentlyActive) {
                    c.drawColor(Color.TRANSPARENT);
                    super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, false);
                    return;
                }

                Paint paint = new Paint();
                Drawable icon;
                float iconMargin = 32f;

                if (dX > 0) {
                    // Right swipe → CALL
                    paint.setColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.green));
                    c.drawRect((float) viewHolder.itemView.getLeft(), (float) viewHolder.itemView.getTop(),
                            dX, (float) viewHolder.itemView.getBottom(), paint);

                    icon = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.ic_call);
                } else {
                    // Left swipe → DELETE
                    paint.setColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.red));
                    c.drawRect((float) viewHolder.itemView.getRight() + dX, (float) viewHolder.itemView.getTop(),
                            (float) viewHolder.itemView.getRight(), (float) viewHolder.itemView.getBottom(), paint);

                    icon = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.ic_delete);
                }

                if (icon != null) {
                    int iconTop = viewHolder.itemView.getTop() +
                            (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconLeft, iconRight;

                    if (dX > 0) {
                        iconLeft = viewHolder.itemView.getLeft() + (int) iconMargin;
                        iconRight = iconLeft + icon.getIntrinsicWidth();
                    } else {
                        iconRight = viewHolder.itemView.getRight() - (int) iconMargin;
                        iconLeft = iconRight - icon.getIntrinsicWidth();
                    }

                    icon.setBounds(iconLeft, iconTop, iconRight, iconTop + icon.getIntrinsicHeight());
                    icon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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