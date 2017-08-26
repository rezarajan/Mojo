package bluefirelabs.mojo.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.shoppingCartAdapter;
import database.DatabaseHelper;

public class cart extends FragmentActivity {

    private ArrayList<String> restaurantNames = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_cart);

        DatabaseHelper myDb = new DatabaseHelper(getApplicationContext());
        recyclerView = findViewById(R.id.mainRecycler);


        Cursor data = myDb.orderAlpha();
        String previousRestaurant = "";

        if(data != null){
            if(data.moveToFirst()){
                do{

                    if(!data.getString(1).equals(previousRestaurant)){
                        restaurantNames.add(data.getString(1));
                        Log.d("Database Data", data.getString(1));
                        previousRestaurant = data.getString(1);
                    }
                } while (data.moveToNext());
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        shoppingCartAdapter adapter = new shoppingCartAdapter(getApplicationContext(), restaurantNames);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
