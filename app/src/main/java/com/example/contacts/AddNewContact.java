package com.example.contacts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.contacts.databinding.ActivityAddNewContactBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewContact extends AppCompatActivity {
    private MyViewModel myViewModel;
    private Contact contact;

    private ImageView contactImage, addImageIcon;
    private EditText firstName, surname, prefix, email, address, birthday,mobileNumber;
    private TextView saveButton, cancelButton;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        contactImage.setImageURI(selectedImageUri);
                        contact.setProfileImageUri(selectedImageUri.toString());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        // Initialize Views
        contactImage = findViewById(R.id.contactImage);
        addImageIcon = findViewById(R.id.addImageIcon);
        firstName = findViewById(R.id.firstName);
        surname = findViewById(R.id.surname);
        prefix = findViewById(R.id.prefix);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        birthday = findViewById(R.id.birthday);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        mobileNumber = findViewById(R.id.mobileNumber);
        contact = new Contact();
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Click listeners
        addImageIcon.setOnClickListener(v -> openImagePicker());
        birthday.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveContact());
        cancelButton.setOnClickListener(v -> finish()); // Simply close the activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        findViewById(R.id.rootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return false;
        });

    }

    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(pickIntent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            birthday.setText(selectedDate);
            contact.setBirthday(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveContact() {
        String firstNameText = firstName.getText().toString().trim();
        String surnameText = surname.getText().toString().trim();
        String prefixText = prefix.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String mobileNumberText=mobileNumber.getText().toString().trim();

        if (firstNameText.isEmpty() || mobileNumberText.isEmpty()) {
            Toast.makeText(this, "Fill the necessary fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Populate Contact Object
        contact.setFirstName(firstNameText);
        contact.setSurname(surnameText);
        contact.setPrefix(prefixText);
        contact.setEmail(emailText);
        contact.setAddress(addressText);
        contact.setMobileNumber(mobileNumberText);

        // Save contact using ViewModel
        myViewModel.addNewContact(contact);
        Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show();

        // Navigate back to MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}