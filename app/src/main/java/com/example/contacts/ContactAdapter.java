package com.example.contacts;



import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact currentContact=contacts.get(position);
        holder.contactPName.setText(currentContact.getPrefix());
        holder.contactFName.setText(currentContact.getFirstName());
        holder.contactSName.setText(currentContact.getSurname());
        Glide.with(holder.itemView.getContext()).clear(holder.contactImage);;
        if (currentContact.getProfileImageUri() != null) {

            holder.contactImage.setVisibility(View.VISIBLE);
            holder.contactInitial.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext())
                    .load(currentContact.getProfileImageUri())
                    .circleCrop()
                    .into(holder.contactImage);
        } else {

            holder.contactImage.setVisibility(View.GONE);
            holder.contactInitial.setVisibility(View.VISIBLE);

            String firstName = currentContact.getFirstName();
            if (firstName != null && !firstName.trim().isEmpty()) {
                String firstLetter = String.valueOf(firstName.trim().charAt(0)).toUpperCase();
                holder.contactInitial.setText(firstLetter);
            } else {
                holder.contactInitial.setText("#");
            }
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditContact.class);
            intent.putExtra("CONTACT_ID", currentContact.getId());
            holder.itemView.getContext().startActivity(intent);
        });

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

    class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView contactInitial,contactPName,contactFName,contactSName;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactInitial = itemView.findViewById(R.id.contactInitial);
            contactFName=itemView.findViewById(R.id.contactFName);
            contactPName=itemView.findViewById(R.id.contactPName);
            contactSName=itemView.findViewById(R.id.contactSName);
        }
    }
}
