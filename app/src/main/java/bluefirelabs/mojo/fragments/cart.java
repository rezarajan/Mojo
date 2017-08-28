package bluefirelabs.mojo.fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.shoppingCartAdapter;
import bluefirelabs.mojo.handlers.adapters.subItemAdapter;
import database.DatabaseHelper;
import database.DatabaseHelperExtras;

public class cart extends FragmentActivity {

    private ArrayList<String> restaurantNames = new ArrayList<>();
    private ArrayList<String> restaurantQuantity = new ArrayList<>();
    //private ArrayList<String> itemName = new ArrayList<>();/**/

    private Map<String, String> itemName = new HashMap<String, String>();
    private Map<String, String> itemCost = new HashMap<String, String>();
    private Map<String, String> itemCount = new HashMap<String, String>();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_cart);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            View view = window.getDecorView();
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);

        }


        DatabaseHelper myDb = new DatabaseHelper(getApplicationContext());
        DatabaseHelperExtras myDbExtras = new DatabaseHelperExtras(getApplicationContext());
        recyclerView = findViewById(R.id.mainRecycler);

        Cursor data = myDb.orderAlpha();
        Cursor dataItems = myDb.orderAlpha();
        String previousRestaurant = "";
        int index = -1; //set to -1 since on the first iteration for item quantity there is a ++ to make it 0
        int indexItems = 0;
        int specificItemQuantity = 1;


        if(data != null){
            if(data.moveToFirst()){
                do{

                    if(!data.getString(1).equals(previousRestaurant)){
                        specificItemQuantity = 1;
                        indexItems = 0;

                        myDbExtras.orderExtras(data.getString(2) + "_0", data.getString(1));

                        itemName.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(2));   //restaurant_0, itemName
                        itemCost.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(3));   //restaurant_0, itemCost
                        itemCount.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(4));   //restaurant_0, itemQuantity
                        indexItems++;

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

                        itemName.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(2));   //restaurant_1, itemName
                        itemCost.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(3));   //restaurant_1, itemCost
                        itemCount.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(4));   //restaurant_1, itemQuantity
                        indexItems++;


                    }
                } while (data.moveToNext());
            }
        }

/*        Log.d("Quantity list", restaurantQuantity.toString());
        Log.d("Item list", itemName.toString());*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        shoppingCartAdapter adapter = new shoppingCartAdapter(getApplicationContext(), restaurantNames, restaurantQuantity, itemName, itemCost, itemCount);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
