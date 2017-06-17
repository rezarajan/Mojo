package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class Checkout extends AppCompatActivity{

    private static final String TAG = "ListDataActivity";
    DatabaseHelper myDb;
    private ListView mListView;
    private Button placeorder;
    private TextView noitems;
    private String pushId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_layout);
        mListView = (ListView)findViewById(R.id.listview_checkout);
        placeorder = (Button)findViewById(R.id.button_place_order);
        noitems = (TextView)findViewById(R.id.content_available_indicator);
        myDb = new DatabaseHelper(this);
        Cursor data = myDb.getAllData();
        if(data.getCount() > 0){
            placeorder.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.INVISIBLE);
        } else {
            placeorder.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            noitems.setVisibility(View.VISIBLE);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pushId = reference.push().getKey();     //String

        //populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        //get the data and append to a list
        Cursor data = myDb.getAllData();

        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column
            //then add it to the ArrayList
            listData.add(data.getString(2));
        }

        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);


        //set a listener for item clicks
        //this leads to the edit menu
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                    Intent editCheckoutintent = new Intent(Checkout.this, editcheckout.class);
                    editCheckoutintent.putExtra("ID", itemID);
                    editCheckoutintent.putExtra("name", name);
                    startActivity(editCheckoutintent);

                } else {
                    Snackbar.make(view, "No ID associated with that name",      //TODO: Change this to suit order quantity
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.checkout_layout);
        myDb = new DatabaseHelper(this);
        mListView = (ListView)findViewById(R.id.listview_checkout);
        placeorder = (Button)findViewById(R.id.button_place_order);
        noitems = (TextView)findViewById(R.id.content_available_indicator);
        Cursor data = myDb.getAllData();
        if(data.getCount() > 0){
            placeorder.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.INVISIBLE);
        } else {
            placeorder.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            noitems.setVisibility(View.VISIBLE);
        }
        populateListView();
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
                //notification.put("runneruid", "Runner");
                notification.put("postid", pushId);

                //get the data and append to a list
                Cursor data = myDb.getAllData();

                //order data alphabetically by restaurant name and append to a list
                //Cursor data = myDb.orderAlpha();

                ArrayList<String> listData = new ArrayList<>();
                while(data.moveToNext()){
                    //get the value from the database in column
                    //then add it to the ArrayList
                    listData.add(data.getString(2));
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                }

                reference.child(pushId).setValue(notification);
                reference.child(pushId).child("items").setValue(itemListing);
            }
        });
    }
}
