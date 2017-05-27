package bluefirelabs.mojo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


    }
}
