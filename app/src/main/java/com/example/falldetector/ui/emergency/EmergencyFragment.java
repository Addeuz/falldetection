package com.example.falldetector.ui.emergency;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.falldetector.R;
import com.example.falldetector.database.Contact;
import com.example.falldetector.database.DatabaseHandler;
import com.example.falldetector.viewAdapter.ContactRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactRecyclerViewAdapter adapter;
    private EmergencyViewModel emergencyViewModel;
    private List<Contact> adapterContacts;
    private DatabaseHandler db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = new DatabaseHandler(getContext());


        View root = inflater.inflate(R.layout.fragment_emergency, container, false);

        emergencyViewModel = new EmergencyViewModel(getContext());

        adapterContacts = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactRecyclerViewAdapter(getContext(), adapterContacts);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Remove item from backing list here
                System.out.println(swipeDir);
                System.out.println(viewHolder.getAdapterPosition());
                System.out.println(viewHolder.getItemId());

                if (swipeDir == 4) {
                    System.out.println("delete");
                    Contact removeContact = adapterContacts.get(viewHolder.getAdapterPosition());

                    db.deleteContact(removeContact);

                    adapterContacts.remove(viewHolder.getAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                }
            }



        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        final Observer<List<Contact>> contactListObserver = contactList -> {
            System.out.println("Observer: " + contactList);
            adapterContacts.clear();
            adapterContacts.addAll(contactList);
            System.out.println(adapterContacts.size());
            adapter.notifyDataSetChanged();
        };
        emergencyViewModel.getContacts().observe(getViewLifecycleOwner(), contactListObserver);

        FloatingActionButton fab = root.findViewById(R.id.addContactFab);

        fab.setOnClickListener(view -> {
            // Start picking the contact to add
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, 1);
            adapter.notifyDataSetChanged();
        });

        return root;
    }


    public static void printContacts(Context context, DatabaseHandler db) {

        List<Contact> contacts = db.getAllContacts();

        for (Contact cn : contacts) {
            String log = "Id: " + cn.getId() + " ,Name: " + cn.getName() + " ,Phone: " +
                    cn.getPhoneNumber();
            // Writing Contacts to system out
            System.out.println(log);
        }
    }
}