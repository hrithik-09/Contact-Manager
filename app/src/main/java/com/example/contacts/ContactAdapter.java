package com.example.contacts;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

        Glide.with(holder.itemView.getContext()).clear(holder.contactListBinding.contactImage);
        if (currentContact.getProfileImageUri() != null) {
            // Load contact image
            holder.contactListBinding.contactImage.setVisibility(View.VISIBLE);
            holder.contactListBinding.contactInitial.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext())
                    .load(currentContact.getProfileImageUri())
                    .circleCrop()
                    .into(holder.contactListBinding.contactImage);
        } else {
            // Show initial instead
            holder.contactListBinding.contactImage.setVisibility(View.GONE);
            holder.contactListBinding.contactInitial.setVisibility(View.VISIBLE);
            String firstName = currentContact.getFirstName();
            if (firstName != null && !firstName.trim().isEmpty()) {
                String firstLetter = String.valueOf(firstName.trim().charAt(0)).toUpperCase();
                holder.contactListBinding.contactInitial.setText(firstLetter);
            } else {
                // Handle cases where no valid name exists
                holder.contactListBinding.contactInitial.setText("#"); // Default or placeholder
            }
        }

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
