package com.example.falldetector.viewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.falldetector.R;
import com.example.falldetector.database.Ride;

import org.w3c.dom.Text;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryViewHolder> {
    private List<Ride> mData;
    private LayoutInflater mInflater;

    public HistoryRecyclerViewAdapter(Context context, List<Ride> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public HistoryRecyclerViewAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_history_row, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.HistoryViewHolder holder, int position) {
        Ride ride = mData.get(position);
        holder.textHowWasRide.setText(ride.getHowWasRide());
        holder.textHowWasWeather.setText(ride.getHowWasWeather());
        holder.textHowLong.setText(ride.getHowLongWasRide());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textHowWasRide;
        TextView textHowWasWeather;
        TextView textHowLong;

        HistoryViewHolder(View itemView) {
            super(itemView);
            textHowWasRide = itemView.findViewById(R.id.howWasRideData);
            textHowWasWeather = itemView.findViewById(R.id.howWasWeatherData);
            textHowLong = itemView.findViewById(R.id.rideTime);
        }
    }
}
