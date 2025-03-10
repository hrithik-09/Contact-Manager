package com.example.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditContact extends AppCompatActivity {
    private MyViewModel myViewModel;
    private Contact contact;

    private ImageView contactImage, addImageIcon,goBack;
    private TextInputEditText firstName, surname, prefix, email, address, birthday,mobileNumber;
    private TextInputLayout firstNameInputLayout,mobileNumberInputLayout,emailInputLayout,birthdayInputLayout;
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
        setContentView(R.layout.activity_edit_contact);


        // Initialize Views
        firstNameInputLayout=findViewById(R.id.firstNameInputLayout);
        emailInputLayout=findViewById(R.id.emailInputLayout);
        mobileNumberInputLayout=findViewById(R.id.mobileNumberInputLayout);
        contactImage = findViewById(R.id.contactImage);
        addImageIcon = findViewById(R.id.addImageIcon);
        firstName = findViewById(R.id.firstName);
        surname = findViewById(R.id.surname);
        prefix = findViewById(R.id.prefix);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        birthdayInputLayout=findViewById(R.id.birthdayInputLayout);
        birthday = findViewById(R.id.birthday);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        mobileNumber = findViewById(R.id.mobileNumber);
        goBack=findViewById(R.id.goBack);
        contact = new Contact();
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Click listeners
        addImageIcon.setOnClickListener(v -> openImagePicker());
        birthday.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveContact());
        cancelButton.setOnClickListener(v -> finish()); // Simply close the activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        addTextWatchers();
        findViewById(R.id.rootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return false;
        });



        goBack.setOnClickListener(v1 -> {
            showBottomSheetDialog();
        });
        birthdayInputLayout.setEndIconOnClickListener(v -> {
            showDatePicker();
            birthday.requestFocus();
        });

        birthdayInputLayout.setOnClickListener(v -> {
            showDatePicker();
            birthday.requestFocus();
        });



        birthday.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                birthday.clearFocus();
                showDatePicker();
            }
        });

        birthday.setOnClickListener(v -> {
            birthday.clearFocus();
            showDatePicker();
        });

        birthdayInputLayout.setEndIconOnClickListener(v -> showDatePicker());



    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long today = calendar.getTimeInMillis();

        // Define start date if you want to restrict past dates as well
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, 1900); // Example: Year 1900
        long startDate = startCalendar.getTimeInMillis();

        // Custom DateValidator to allow today but restrict future dates
        CalendarConstraints.DateValidator dateValidator = new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long date) {
                return date <= today || isSameDay(date, today);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
            }

            // Utility method to compare two dates without time consideration
            private boolean isSameDay(long date1, long date2) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTimeInMillis(date1);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(date2);

                return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            }
        };

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(startDate) // Optional: Restrict past dates before year 1900
                .setEnd(today)       // Allow today and restrict future dates
                .setValidator(dateValidator);  // Restrict future dates properly

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(today)  // Defaults selection to today's date
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDate = sdf.format(new Date(selection));
            birthday.setText(selectedDate);
        });
    }




    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(pickIntent);
    }

    private void addTextWatchers() {
        email.addTextChangedListener(new EditContact.ValidationTextWatcher(email));
        mobileNumber.addTextChangedListener(new EditContact.ValidationTextWatcher(mobileNumber));
    }
    private void saveContact() {
        String firstNameText = firstName.getText().toString().trim();
        String surnameText = surname.getText().toString().trim();
        String prefixText = prefix.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String mobileNumberText=mobileNumber.getText().toString().trim();

        if (firstNameText.isEmpty()) {
            firstNameInputLayout.setError("First Name is required");
            return;
        } else {
            firstNameInputLayout.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailInputLayout.setError("Invalid email format");
            return;
        } else {
            emailInputLayout.setError(null);
        }

        if (mobileNumber.length() !=10 ) {
            mobileNumberInputLayout.setError("Mobile no. must be 10 digits");
            return;
        } else {
            mobileNumberInputLayout.setError(null);
        }


        contact.setFirstName(firstNameText);
        contact.setSurname(surnameText);
        contact.setPrefix(prefixText);
        contact.setEmail(emailText);
        contact.setAddress(addressText);
        contact.setMobileNumber(mobileNumberText);


        myViewModel.addNewContact(contact);
        Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show();


        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    private class ValidationTextWatcher implements TextWatcher {
        private final View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!firstName.getText().toString().trim().isEmpty())
            {
                firstNameInputLayout.setError(null);
            }

            if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
            {
                emailInputLayout.setError(null);
            }

            if (mobileNumber.getText().toString().trim().length() ==10 )  {
                mobileNumberInputLayout.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
    @Override
    public void onBackPressed() {
        showBottomSheetDialog();
//        super.onBackPressed();
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.go_back_popup_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetView.setBackground(new ColorDrawable(Color.TRANSPARENT));

        TextView cancelButton = bottomSheetView.findViewById(R.id.cancelButton);
        TextView discardButton = bottomSheetView.findViewById(R.id.discardButton);
        TextView saveButton = bottomSheetView.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(view -> bottomSheetDialog.dismiss());
        discardButton.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        saveButton.setOnClickListener(view -> {
            // Add your save logic here
            saveContact();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}