package com.example.adityapatel.nearbycafe;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adityapatel.nearbycafe.models.PlaceDataStore;
import com.example.adityapatel.nearbycafe.models.PlaceDataStoreImpl;
import com.google.android.gms.location.places.Place;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private final PlaceDataStore cafeStore;


    public RecyclerViewAdapter() {
        // note = mnote_name;
        cafeStore = PlaceDataStoreImpl.sharedInstance();

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int i) {
        Log.d("recycle", "in recycleview");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        try {
            //dataStore.storeNotes(mcontext);
            String rating = String.valueOf(cafeStore.getPlaces().get(position).getRating());
            holder.CafeName.setText(cafeStore.getPlaces().get(position).getName());
            holder.CafeAddress.setText(cafeStore.getPlaces().get(position).getAddress());
            holder.CafeRating.setText("Rating "+rating+"/5");

        } catch (NullPointerException e) {
            Log.d("nullpointer", e.getMessage());
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView CafeName;
        TextView CafeRating;
        TextView CafeAddress;
        ConstraintLayout parent_Layout;

        public ViewHolder(View itemView) {
            super(itemView);
            CafeName = (TextView)itemView.findViewById(R.id.cafeName);
            CafeRating = (TextView)itemView.findViewById(R.id.cafe_rating);
            CafeAddress =(TextView)itemView.findViewById(R.id.cafe_address);

        }
    }

    @Override
    public int getItemCount() {
        // Log.d(TAG, Double.toString(dataStore.getNotes().get(0).getLatitude()));

        Log.d(TAG, "getItemCount: ");

        return cafeStore.getPlaces().size() ;
    }

}
