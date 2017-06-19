package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class Receipt extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";
    DatabaseHelper myDb;
    private ListView mListView;
    private Button placeorder;
    private TextView noitems;
    private String orderId;
    Set keys, mainkey;
    public String restaurantName = "";
    public String RESTAURANT = "";
    private String UID = "";
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_checkout_layout);

        Intent receivedIntent = getIntent();

        //now get the orderID passed as an extra
        orderId = receivedIntent.getExtras().getString("ID"); //NOTE: -1 is just the default value
        Log.d("Order ID Received", orderId);

        mListView = (ListView) findViewById(R.id.listview_checkout);
        placeorder = (Button) findViewById(R.id.button_place_order);
        noitems = (TextView) findViewById(R.id.content_available_indicator);

        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        final ArrayList<String> listData = new ArrayList<>();
        listData.clear();
        //create the list adapter and set the adapter
        final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);

        final MyCallback myCallback = new MyCallback() {
            @Override
            public void callbackCall(final String restaurant) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant + "/items/");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();

                        final Map<String, Object> singleitemCost = (Map<String, Object>) dataSnapshot.getValue();
                        final Map<String, Object> finalitemCost = (Map<String, Object>) dataSnapshot.getValue();

                        if (hopperValues != null) {


                            keys = hopperValues.keySet();

                            for (String s : hopperValues.keySet()) {
                                Log.d("Receipt Items", "Key: " + s);

                                listData.add(s);
                                listData.add("Quantity: " + hopperValues.get(s));
                                //itemTitle.setText(key.toString());
                                //mListView.setAdapter(adapter);
                            }
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant + "/cost/");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Map<String, Object> itemCost = (Map<String, Object>) dataSnapshot.getValue();

                                    if (itemCost != null) {

                                        keys = itemCost.keySet();
                                        int total_cost = 0;


                                        for (String s : itemCost.keySet()) {
                                            singleitemCost.put(s, itemCost.get(s));
                                            listData.add("(" + s + ") Cost: " + singleitemCost.get(s));        //TODO: Fix this to display this once after each item
                                            //Log.d("Item Cost", String.valueOf(itemCost.get(s)));
                                            //Log.d("Single Item Cost", String.valueOf(singleitemCost.get(s)));
                                            finalitemCost.put(s, String.valueOf(Integer.parseInt(String.valueOf(itemCost.get(s))) * Integer.parseInt(String.valueOf(hopperValues.get(s)))));
                                            //Log.d("Final Item Cost", String.valueOf(finalitemCost.get(s)));
                                            listData.add("(" + s + ") Total: " + finalitemCost.get(s));
                                            total_cost += Integer.parseInt(String.valueOf(finalitemCost.get(s)));
                                        }
                                        listData.add(String.valueOf("Total Cost: " + total_cost));
                                    }
                                    mListView.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        UID = user.getUid();

        final DatabaseReference reference_restaurantName = FirebaseDatabase.getInstance().getReference("uid/" + UID);
        reference_restaurantName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                //Log.d("Values", dataSnapshot.getValue().toString());
                //restaurantName = (String) hopperValues.get("name"); //this directory only contains one item so it should not be a problem
                RESTAURANT = "uid/" + UID + "/" + orderId;
                Log.d("Values from Checkout", RESTAURANT);
                myCallback.callbackCall(RESTAURANT);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listData.clear();

    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.vendor_checkout_layout);
        mListView = (ListView) findViewById(R.id.listview_checkout);
        placeorder = (Button) findViewById(R.id.button_place_order);
        noitems = (TextView) findViewById(R.id.content_available_indicator);
        populateListView();
    }
}