package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class AddNewContactClickHandler {
    Contact contact;
    Context context;
    MyViewModel myViewModel;

    public AddNewContactClickHandler(Contact contact, Context context, MyViewModel myViewModel) {
        this.contact = contact;
        this.context = context;
        this.myViewModel = myViewModel;
    }
    public void onSubmitBtnClicked(View view){
        if (contact.getName() == null || contact.getEmail() ==null){
            Toast.makeText(context, "Fields Cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent i = new Intent(context,MainActivity.class);

            Contact c = new Contact(
                    contact.getName(),
                    contact.getEmail()
            );

            myViewModel.addNewContact(c);

            context.startActivity(i);

        }
    }

}
