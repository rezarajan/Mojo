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
    private ArrayList<String> restaurantQuantity = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_cart);

        DatabaseHelper myDb = new DatabaseHelper(getApplicationContext());
        recyclerView = findViewById(R.id.mainRecycler);


        Cursor data = myDb.orderAlpha();
        String previousRestaurant = "";
        int index = -1; //set to -1 since on the first iteration for item quantity there is a ++ to make it 0
        int specificItemQuantity = 1;


        if(data != null){
            if(data.moveToFirst()){
                do{

                    if(!data.getString(1).equals(previousRestaurant)){
                        specificItemQuantity = 1;

                        restaurantNames.add(data.getString(1));
                        Log.d("Database Data", data.getString(1));
                        previousRestaurant = data.getString(1);

                        //index is set to -1 so for the first iteration this operation sets the index to 0
                        index++;
                        restaurantQuantity.add(index, String.valueOf(specificItemQuantity));    //using the add operation since this is a new index

                        Log.d("Index : Quantity", String.valueOf(index) + ":" + String.valueOf(specificItemQuantity));

                    }
                    else {
                        specificItemQuantity++;
                        restaurantQuantity.set(index, String.valueOf(specificItemQuantity));    //using the set operation to overwrite existing data at the index
                        Log.d("Index : Quantity", String.valueOf(index) + ":" + String.valueOf(specificItemQuantity));


                    }
                } while (data.moveToNext());
            }
        }

        Log.d("Quantity list", restaurantQuantity.toString());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        shoppingCartAdapter adapter = new shoppingCartAdapter(getApplicationContext(), restaurantNames, restaurantQuantity);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
