package bluefirelabs.mojo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class Checkout extends AppCompatActivity{

    private static final String TAG = "ListDataActivity";
    DatabaseHelper myDb;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_layout);
        mListView = (ListView)findViewById(R.id.listview_checkout);
        myDb = new DatabaseHelper(this);

        populateListView();
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
                    Snackbar.make(view, "No ID associated with that name",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
}
