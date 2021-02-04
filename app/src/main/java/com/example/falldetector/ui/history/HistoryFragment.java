package com.example.falldetector.ui.history;

import android.content.Context;
import android.os.Bundle;
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
import com.example.falldetector.database.Ride;
import com.example.falldetector.viewAdapter.HistoryRecyclerViewAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryRecyclerViewAdapter adapter;
    private HistoryViewModel historyViewModel;
    private List<Ride> adapterRides;
    private DatabaseHandler db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        db = new DatabaseHandler(getContext());

        historyViewModel = new HistoryViewModel(getContext());

        adapterRides = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryRecyclerViewAdapter(getContext(), adapterRides);
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
                    Ride removeRide = adapterRides.get(viewHolder.getAdapterPosition());

                    db.deleteRide(removeRide);

                    adapterRides.remove(viewHolder.getAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                }
            }



        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Observer for rides
        final Observer<List<Ride>> rideListObserver = new Observer<List<Ride>>() {
            @Override
            public void onChanged(List<Ride> rideList) {
                adapterRides.clear();
                adapterRides.addAll(rideList);
                adapter.notifyItemRangeChanged(0, rideList.size());
            }
        };
        historyViewModel.getRides().observe(getViewLifecycleOwner(), rideListObserver);

        return root;
    }

    public static void printHistory(Context context, DatabaseHandler db) {
        List<Ride> rides = db.getAllRides();

        for (Ride rd : rides) {
            String log = "Id: " + rd.getId() + ", How long? " + rd.getHowLongWasRide() + ", How was ride? " + rd.getHowWasRide() + ", Weather? " + rd.getHowWasWeather();
            System.out.println(log);
        }
    }
}