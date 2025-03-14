package com.example.contacts;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import android.os.Parcel;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewContact extends AppCompatActivity {
    private MyViewModel myViewModel;
    private static final int REQUEST_PERMISSION_CODE = 101;

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
                        String savedImagePath = saveImageToInternalStorage(selectedImageUri);
                        if (savedImagePath != null) {
                            contact.setProfileImageUri(savedImagePath); // Save the file path
                        }
                    }
                }
            });

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Correct the image orientation
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                ExifInterface exif = new ExifInterface(inputStream);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = rotateBitmap(bitmap, orientation);
            }

            // Save the corrected image
            File directory = new File(getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "contact_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(directory, fileName);

            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }

            return imageFile.getAbsolutePath(); // Return the saved image path
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle errors properly
        }
    }

    // Utility method to rotate bitmap based on EXIF data
    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap; // No rotation needed
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

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
        addImageIcon.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissions();
            }
        });

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
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_PERMISSION_CODE);
        } else {
            // For Android 6.0 to 12
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker(); // Call the method to open the image picker
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
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
        email.addTextChangedListener(new ValidationTextWatcher(email));
        mobileNumber.addTextChangedListener(new ValidationTextWatcher(mobileNumber));
    }
    private void saveContact() {
        String firstNameText = firstName.getText().toString().trim();
        String surnameText = surname.getText().toString().trim();
        String prefixText = prefix.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String mobileNumberText=mobileNumber.getText().toString().trim();
        String birthdayText=birthday.getText().toString().trim();

        if (firstNameText.isEmpty()) {
            firstNameInputLayout.setError("First Name is required");
            return;
        } else {
            firstNameInputLayout.setError(null);
        }

        if (!emailText.isEmpty()){

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailInputLayout.setError("Invalid email format");
            return;
        } else {
            emailInputLayout.setError(null);
        }
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
        contact.setBirthday(birthdayText);


        myViewModel.addNewContact(contact);
        Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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