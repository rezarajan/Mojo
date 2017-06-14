package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class Vendor_Checkout extends AppCompatActivity{

    private static final String TAG = "ListDataActivity";
    DatabaseHelper myDb;
    private ListView mListView;
    private Button placeorder;
    private TextView noitems;
    private String orderId;
    Set keys, mainkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_checkout_layout);

        Intent receivedIntent = getIntent();

        //now get the orderID passed as an extra
        orderId = receivedIntent.getExtras().getString("ID"); //NOTE: -1 is just the default value
        Log.d("Order ID Received", orderId);

        mListView = (ListView)findViewById(R.id.listview_checkout);
        placeorder = (Button)findViewById(R.id.button_place_order);
        noitems = (TextView)findViewById(R.id.content_available_indicator);
        /*if(data.getCount() > 0){
            placeorder.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.INVISIBLE);
        } else {
            placeorder.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            noitems.setVisibility(View.VISIBLE);
        } */

        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        final ArrayList<String> listData = new ArrayList<>();
        listData.clear();




        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("uid/Starbucks/requests/" + orderId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                //hopperValues.put("key", dataSnapshot.getKey().toString());
                //Log.d("Values", dataSnapshot.getKey().toString());
                Log.d("Values", dataSnapshot.getValue().toString());



                int total_cost = 0;

                if (hopperValues != null) {
                    //Map<String, Object> orderID = (Map<String, Object>) hopperValues.get("items");
                    //Log.d("orderID", orderID.toString());

                    Map<String, Object> itemValues = (Map<String, Object>) hopperValues.get("items");

                    keys = itemValues.keySet();

                    for (Iterator i = keys.iterator(); i.hasNext(); ) {
                        String key = (String) i.next();
                        String value = (String) itemValues.get(key);
                        //textview.setText(key + " = " + value);
                        Log.d("itemValues", "Key: " + key + ", Value: " + value);
                        listData.add(key + "    Amount: " + value);
                        //itemTitle.setText(key.toString());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the data and append to a list
        //Cursor data = myDb.getAllData();


        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
        listData.clear();


        //set a listener for item clicks
        //this leads to the edit menu
       /* mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemClicked: You clicked on: " + name);

                Cursor data = myDb.getItemID(name);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > 0){
                    Snackbar.make(view, "The ID is: " + itemID,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    Snackbar.make(view, "No ID associated with that name",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }); */
    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.vendor_checkout_layout);
        //myDb = new DatabaseHelper(this);
        mListView = (ListView)findViewById(R.id.listview_checkout);
        placeorder = (Button)findViewById(R.id.button_place_order);
        noitems = (TextView)findViewById(R.id.content_available_indicator);

        //Intent receivedIntent = getIntent();

        //now get the orderID passed as an extra
        //orderId = receivedIntent.getExtras().getString("ID");

        //Cursor data = myDb.getAllData();
        /*
        if(data.getCount() > 0){
            placeorder.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.INVISIBLE);
        } else {
            placeorder.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            noitems.setVisibility(View.VISIBLE);
        } */
        populateListView();
        /*
        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button clicked", "Placing order");

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
                FirebaseUser user = firebaseAuth.getCurrentUser();


                Map notification = new HashMap<>();
                Map itemListing = new HashMap<>();

                notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                notification.put("customeruid", user.getUid());
                notification.put("vendoruid", "Starbucks");
                //notification.put("postid", pushId);

                //get the data and append to a list
                Cursor data = myDb.getAllData();

                ArrayList<String> listData = new ArrayList<>();
                while(data.moveToNext()){
                    //get the value from the database in column
                    //then add it to the ArrayList
                    listData.add(data.getString(2));
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                }

                //reference.child(pushId).setValue(notification);
                //reference.child(pushId).child("items").setValue(itemListing);
            }
        }); */
    }
}