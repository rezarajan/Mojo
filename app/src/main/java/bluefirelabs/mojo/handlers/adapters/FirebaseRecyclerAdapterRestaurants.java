package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.menu.Restaurant_Menu;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class FirebaseRecyclerAdapterRestaurants {

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        public TextView itemTitle;
        public TextView itemDescription;
        public ImageView itemIcon;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDescription = (TextView) itemView.findViewById(R.id.item_description);
            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    getter_restaurants();
                }
            });

        }
        public void getter_restaurants(){
            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("listing");
            reference1.orderByChild("restaurant").equalTo(itemTitle.getText().toString()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                    //hopperValues.put("key", dataSnapshot.getKey().toString());
                    //Log.d("Values", dataSnapshot.getKey().toString());
                    //Log.d("Values", dataSnapshot.getValue().toString());


                    Intent intent = new Intent(context, Restaurant_Menu.class);
                    intent.putExtra("Restaurant", itemTitle.getText().toString());
                    intent.putExtra("Icon", hopperValues.get("icon").toString());
                    Log.d("icon", hopperValues.get("icon").toString());
                    context.startActivity(intent);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
