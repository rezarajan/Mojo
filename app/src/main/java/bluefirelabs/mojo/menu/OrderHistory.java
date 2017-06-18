package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Map;
import java.util.Set;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class OrderHistory extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";
    DatabaseHelper myDb;
    private ListView mListView;
    private Button placeorder;
    private TextView noitems, txtView;
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

        //Intent receivedIntent = getIntent();

        //now get the orderID passed as an extra
        //orderId = receivedIntent.getExtras().getString("ID"); //NOTE: -1 is just the default value
        //Log.d("Order ID Received", orderId);

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
            public void callbackCall(String restaurant) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                        //hopperValues.put("key", dataSnapshot.getKey().toString());
                        //Log.d("Values", dataSnapshot.getKey().toString());
                        //Log.d("Values from Checkout", dataSnapshot.getValue().toString());


                        int total_cost = 0;

                        if (hopperValues != null) {
                            //Map<String, Object> orderID = (Map<String, Object>) hopperValues.get("items");
                            //Log.d("orderID", orderID.toString());

                            //Map<String, Object> itemValues = (Map<String, Object>) hopperValues.get("items");

                            keys = hopperValues.keySet();

                            for (String s : hopperValues.keySet()) {
                                Log.d("hopperValues", "Key: " + s);
                                listData.add(s);
                                //itemTitle.setText(key.toString());
                                mListView.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderHistory.this, Receipt.class);
                intent.putExtra("ID", mListView.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        UID = user.getUid();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("uid/" + UID + "/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                keys = hopperValues.keySet();
                //hopperValues.put("key", dataSnapshot.getKey().toString());
                //Log.d("Values", dataSnapshot.getKey().toString());
                Log.d("Values", keys.toString());
                RESTAURANT = "uid/" + UID;
                myCallback.callbackCall(RESTAURANT);
                /*for (String s : hopperValues.keySet()) {
                    Log.d("Values", s);
                    //DatabaseReference reference_order = FirebaseDatabase.getInstance().getReference("uid/"+UID+"/"+s);
                    //RESTAURANT = "uid/"+UID+"/"+s;
                    RESTAURANT = "uid/"+UID;
                    myCallback.callbackCall(RESTAURANT);
                } */
                //Map<String, Object> itemValues = (Map<String, Object>) hopperValues.get("items");
                //Log.d("items", itemValues.toString());
                //restaurantName = (String) hopperValues.get("items"); //this directory only contains one item so it should not be a problem
                //RESTAURANT  = "uid/"+restaurantName+"/requests/";
                //Log.d("Restaurant", RESTAURANT);
                //myCallback.callbackCall(RESTAURANT);
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
