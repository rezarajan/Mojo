package bluefirelabs.mojo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import bluefirelabs.mojo.handlers.RecyclerAdapter;

/**
 * Created by rezarajan on 19/05/2017.
 */

public class Restaurant_Cards extends AppCompatActivity implements bluefirelabs.mojo.fragments.currentinfo_fragment.currentinfoListener, bluefirelabs.mojo.fragments.restaurantlist_fragmnet.restaurantlistListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hub_layout);


        //TextView smalldesc = (TextView)findViewById(R.id.small_description_location);

        //smalldesc.setText("Hello");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.layout.menu_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
