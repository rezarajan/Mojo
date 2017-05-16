package bluefirelabs.mojo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Reza Rajan on 2017-05-15.
 */

public class xmlAdapter extends AppCompatActivity{
    private ArrayList<String> mArray;
    private LayoutInflater mInflater;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayAdapter<String> mArrayAdapter_1;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view);
        ListView listView = (ListView) findViewById(R.id.listView);

        String[] items = {"One", "Two", "Three", "Four", "Five"};
        mArray = new ArrayList<>(Arrays.asList(items));
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtV, mArray);
        listView.setAdapter(mArrayAdapter);

    }
}
