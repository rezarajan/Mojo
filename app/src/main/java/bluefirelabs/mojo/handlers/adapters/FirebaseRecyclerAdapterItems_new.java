package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class FirebaseRecyclerAdapterItems_new {

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        public TextView item_details;
        public TextView item_cost;
        public TextView item_quantity;

        public Context getContext() {
            return context;
        }

        DatabaseHelper myDb;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            item_details = (TextView) itemView.findViewById(R.id.item_dets);
            item_cost = (TextView) itemView.findViewById(R.id.item_cost);
            item_quantity = (TextView) itemView.findViewById(R.id.item_quantity);
            context = itemView.getContext();
            myDb = new DatabaseHelper(this.getContext()); //calls constructor from the database helper class


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                    boolean isInserted = myDb.insertData("Starbucks",       //TODO: Change this to the actual restaurant name
                            itemTitle.getText().toString(),     //The item name
                            itemDescription.getText().toString().replace("$",""),       //The item cost
                            "1");                                //Adds the item at at the specific position to the database
                                                                //Default Quantity is 1

                    if (isInserted == true) {
                        Snackbar.make(v, "Data Inserted",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(v, "Data not Inserted",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } */

                    //Intent intent = new Intent(context, Item_Menu.class);
                    //context.startActivity(intent);

                    /*
                    final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("menu");
                    reference1.orderByValue().addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                            //hopperValues.put("key", dataSnapshot.getKey().toString());
                            //Log.d("Values", dataSnapshot.getKey().toString());
                            Log.d("Values", dataSnapshot.getValue().toString());


                            //Intent intent = new Intent(context, Drinks_Menu.class);
                            //intent.putExtra("Restaurant", itemTitle.getText().toString());
                            //intent.putExtra("Icon", hopperValues.get("icon").toString());
                            //Log.d("icon", hopperValues.get("icon").toString());
                            //context.startActivity(intent);
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
                    }); */
                }
            });
        }
    }
}
