package com.example.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MainActivityClickHandlers {
    Context context;

    public MainActivityClickHandlers(Context context) {
        this.context = context;
    }

    public void addContactIconClicked(View view){
        Intent i = new Intent(view.getContext(),AddNewContact.class);
        context.startActivity(i);
    }
}
