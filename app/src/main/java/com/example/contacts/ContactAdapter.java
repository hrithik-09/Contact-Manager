package com.example.contacts;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.databinding.ContactListBinding;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> contacts;

    public ContactAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactListBinding contactListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.contact_list,
                parent,
                false
        );
        return new ContactViewHolder(contactListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact currentContact=contacts.get(position);
        holder.contactListBinding.setContact(currentContact);
    }

    @Override
    public int getItemCount() {
        if (contacts!=null){
            return contacts.size();
        }
        else {
            return 0;
        }
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{
        private ContactListBinding contactListBinding;

        public ContactViewHolder(@NonNull ContactListBinding contactListBinding) {
            super(contactListBinding.getRoot());
            this.contactListBinding = contactListBinding;
        }
    }
}
