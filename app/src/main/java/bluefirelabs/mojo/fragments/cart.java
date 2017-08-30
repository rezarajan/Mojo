package bluefirelabs.mojo.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.shoppingCartAdapter;
import bluefirelabs.mojo.handlers.adapters.subItemAdapter;
import bluefirelabs.mojo.main.ui.checkout.Checkout;
import database.DatabaseHelper;
import database.DatabaseHelperExtras;

public class cart extends FragmentActivity {

    private ArrayList<String> restaurantNames = new ArrayList<>();
    private ArrayList<String> restaurantQuantity = new ArrayList<>();
    //private ArrayList<String> itemName = new ArrayList<>();/**/

    private Map<String, String> itemName = new HashMap<String, String>();
    private Map<String, String> itemCost = new HashMap<String, String>();
    private Map<String, String> itemCount = new HashMap<String, String>();

    public RecyclerView recyclerView;

    private shoppingCartAdapter adapter;

    private String pushId, itemRef;

    private DatabaseHelper myDb;
    private DatabaseHelperExtras myDbExtras;

    private Cursor dataExtras;

    private CardView pay;

    private Integer identifier = 0;

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

        //View view = getWindow().getDecorView().getRootView();

        //recyclerView = findViewById(R.id.mainRecycler);

        recyclerView = findViewById(R.id.mainRecycler);

        pay = findViewById(R.id.proceedButton);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Button", "clicked");
                getPaymentsReady();

            }
        });

        swapData(getApplicationContext());


    }

    public void swapData(Context context) {
        myDb = new DatabaseHelper(context);
        myDbExtras = new DatabaseHelperExtras(context);

        Cursor data = myDb.orderAlpha();
        Cursor dataItems = myDb.orderAlpha();
        String previousRestaurant = "";
        int index = -1; //set to -1 since on the first iteration for item quantity there is a ++ to make it 0
        int indexItems = 0;
        int specificItemQuantity = 1;


        if (data != null) {
            if (data.moveToFirst()) {

                do {

                    if (!data.getString(1).equals(previousRestaurant)) {
                        specificItemQuantity = 1;
                        indexItems = 0;

                        myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(indexItems), data.getString(1));

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

                    } else {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setAutoMeasureEnabled(false);
        adapter = new shoppingCartAdapter(context, restaurantNames, restaurantQuantity, itemName, itemCost, itemCount);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void getPaymentsReady() {


        //for(int i = 0; i<=99; i++){       //this is a test loop to place 100 orders
        Log.d("Button clicked", "Placing order");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pushId = reference.push().getKey();     //String

        Integer indexItems = 0;



        Map notification = new HashMap<>();
        Map itemListing = new HashMap<>();
        Map costListing = new HashMap<>();
        Map extrasListing = new HashMap<>();

        Double total_cost = 0.00;


        //get the data and append to a list
        //Cursor data = myDb.getAllData();

        //order data alphabetically by restaurant name and append to a list
        Cursor data = myDb.orderAlpha();
        Cursor dataExtras = myDbExtras.getAllData();

        for(int i = 0; i < dataExtras.getCount(); i++){
            dataExtras.moveToPosition(i);
            Log.d("dataExtras", dataExtras.getString(0));
            Log.d("dataExtras", dataExtras.getString(1));
            Log.d("dataExtras", dataExtras.getString(2));
            Log.d("dataExtras", dataExtras.getString(3));
            Log.d("dataExtras", dataExtras.getString(4));
            Log.d("dataExtras", dataExtras.getString(5));
            Log.d("dataExtras", dataExtras.getString(6));
            Log.d("dataExtras", dataExtras.getString(7));

        }


        ArrayList<String> listData = new ArrayList<>();
        //ArrayList<String> listData_comparison = new ArrayList<>();
        String restaurant, next_restaurant;

        int count = data.getCount();
        for (int position = 0; position < count; position++) {
            data.moveToPosition(position);
            Log.d("Database", data.getString(1));
            Log.d("Cost", data.getString(3));
            total_cost += Double.parseDouble(data.getString(3)) * Double.parseDouble(data.getString(4)) * 1.00; //adding the cost of each item multiplied by the quantity and *1.00 to keep it a double

        }

        for (int position = 0; position < count; position++) {
            data.moveToPosition(position);
            restaurant = data.getString(1);     //gets the restaurant name
            //data.moveToPosition(position + 1);
            if (data.moveToNext() != false) {
                next_restaurant = data.getString(1);       //comparing the next restaurant
            } else {
                next_restaurant = restaurant;       //case for the last item in the list
            }
            Log.d("Comparison Parameters", restaurant + " : " + next_restaurant);

            if (restaurant.equals(next_restaurant)) {
                Log.d("Outcome", "same");
                //itemListing.clear();
                //notification.clear();
                //this is done twice since for the case when the restaurants are different
                //the former restaurant will not be added since it resorts to the else case
                data.moveToPosition(position);
                Log.d("Using pushID", String.valueOf(pushId));
                //get the value from the database in column
                //then add it to the ArrayList
                //listData.add(data.getString(2));
                notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                notification.put("customeruid_to", user.getUid());
                notification.put("customeruid_from", "none");
                notification.put("vendoruid", data.getString(1));
                //notification.put("runneruid", "Runner");
                notification.put("postid", pushId);
                itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                costListing.put(data.getString(2), data.getString(3));    //itemId, cost


                identifier = -1;
                do {
                    //TODO: Change this to an actual number when the user selects the number of items of a particular kind
                    identifier++;
                    dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(identifier), data.getString(1));    //uniquetag = itemId + (_0), restaurant;

                } while (dataExtras.getCount() > 0);

                identifier--;       //to normalize it since the previous loop checks if the next value is 0 or not



                Log.d("Identifier", String.valueOf(identifier));


                for(int idCount = 0; idCount <= identifier; idCount++){

                    itemRef = data.getString(2) + "_" + String.valueOf(idCount);     //uniquetag = itemId + (_0)
                    Log.d("Item Ref", itemRef);
                    dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(idCount), data.getString(1));    //uniquetag = itemId + (_0), restaurant


                    if(dataExtras == null){
                        Log.d("dataExtras", "null");
                    }
                    else {
                        Log.d("dataExtras", "not null");
                        Log.d("dataExtras", "Size: " + dataExtras.getCount());

                        if(dataExtras.getCount() == 0){
                            reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                            reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                            reference.child(pushId).child("customeruid_from").setValue("none");
                            reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                            reference.child(pushId).child("postid").setValue(pushId);
                            //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                            reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                            reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                            reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));
                        }
                        else {
                            for(int i = 0; i < dataExtras.getCount(); i++){
                                dataExtras.moveToPosition(i);
                                Log.d("dataExtra", dataExtras.getString(0));


                                //checking for any extra reading 0 quantity and excluding it
                                if (Integer.parseInt(dataExtras.getString(2)) > 0) {
                                    Log.d("Extra for Upload", dataExtras.getString(0));
                                    extrasListing.put("name", dataExtras.getString(0));    //name, extraName
                                    extrasListing.put("cost", dataExtras.getString(1));    //cost, extraCost
                                    extrasListing.put("quantity", dataExtras.getString(2));    //cost, extraQuantity
                                    extrasListing.put("parent", dataExtras.getString(3));    //parent(type) of extra, type
                                }

                                Log.d("ExtraListing", extrasListing.toString());

                                reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                reference.child(pushId).child("customeruid_from").setValue("none");
                                reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                reference.child(pushId).child("postid").setValue(pushId);
                                //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);
                                reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));


                                Log.d("Location_0", data.getString(2) + "_" + String.valueOf(indexItems));
                                extrasListing.clear();

                            }
                        }



                    }

                }


                //data.moveToPosition(position + 1);
                if (data.moveToNext()) {
                    indexItems++;

                    //get the value from the database in column
                    //then add it to the ArrayList
                    //listData.add(data.getString(2));
                    notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                    notification.put("customeruid_to", user.getUid());
                    notification.put("customeruid_from", "none");
                    notification.put("vendoruid", data.getString(1));
                    //notification.put("runneruid", "Runner");
                    notification.put("postid", pushId);
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                    costListing.put(data.getString(2), data.getString(3));    //itemId, cost

                    //itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)
                    Log.d("Location_1", data.getString(2) + "_" + String.valueOf(indexItems));

                    //dataExtras = myDbExtras.orderExtras(data.getString(2), data.getString(1));    //uniquetag = itemId + (_0), restaurant
                    //dataExtras = myDbExtras.orderExtras(data.getString(2) + "_0", data.getString(1));    //uniquetag = itemId + (_0), restaurant

                    identifier = -1;
                    do {
                        //TODO: Change this to an actual number when the user selects the number of items of a particular kind
                        identifier++;
                        dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(identifier), data.getString(1));    //uniquetag = itemId + (_0), restaurant;

                    } while (dataExtras.getCount() > 0);

                    identifier--;       //to normalize it since the previous loop checks if the next value is 0 or not

                    for(int idCount = 0; idCount <= identifier; idCount++){

                        itemRef = data.getString(2) + "_" + String.valueOf(idCount);     //uniquetag = itemId + (_0)
                        Log.d("Item Ref", itemRef);
                        dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(idCount), data.getString(1));    //uniquetag = itemId + (_0), restaurant


                        if(dataExtras == null){
                            Log.d("dataExtras", "null");
                        }
                        else {
                            Log.d("dataExtras", "not null");
                            Log.d("dataExtras", "Size: " + dataExtras.getCount());

                            if(dataExtras.getCount() == 0){
                                reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                reference.child(pushId).child("customeruid_from").setValue("none");
                                reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                reference.child(pushId).child("postid").setValue(pushId);
                                //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));
                            }
                            else {
                                for(int i = 0; i < dataExtras.getCount(); i++){
                                    dataExtras.moveToPosition(i);
                                    Log.d("dataExtra", dataExtras.getString(0));


                                    //checking for any extra reading 0 quantity and excluding it
                                    if (Integer.parseInt(dataExtras.getString(2)) > 0) {
                                        Log.d("Extra for Upload", dataExtras.getString(0));
                                        extrasListing.put("name", dataExtras.getString(0));    //name, extraName
                                        extrasListing.put("cost", dataExtras.getString(1));    //cost, extraCost
                                        extrasListing.put("quantity", dataExtras.getString(2));    //cost, extraQuantity
                                        extrasListing.put("parent", dataExtras.getString(3));    //parent(type) of extra, type
                                    }

                                    Log.d("ExtraListing", extrasListing.toString());

                                    reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                    reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                    reference.child(pushId).child("customeruid_from").setValue("none");
                                    reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                    reference.child(pushId).child("postid").setValue(pushId);
                                    //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                    reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                    reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);
                                    reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                    reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));


                                    Log.d("Location_0", data.getString(2) + "_" + String.valueOf(indexItems));
                                    extrasListing.clear();

                                }
                            }



                        }

                    }

                }

            } else {
                Log.d("Position", String.valueOf(position));
                if (position == 0) {        //for the initial case
                    data.moveToPosition(position);
                    Log.d("Initial set", data.getString(1) + " with pushID: " + String.valueOf(pushId));
                    //get the value from the database in column
                    //then add it to the ArrayList
                    //listData.add(data.getString(2));
                    notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                    notification.put("customeruid_to", user.getUid());
                    notification.put("customeruid_from", "none");
                    notification.put("vendoruid", data.getString(1));
                    //notification.put("runneruid", "Runner");
                    notification.put("postid", pushId);
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                    costListing.put(data.getString(2), data.getString(3));    //itemId, cost

                    //itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)

                    Log.d("Location_3", data.getString(2) + "_" + String.valueOf(indexItems));


                    //dataExtras = myDbExtras.orderExtras(data.getString(2), data.getString(1));    //uniquetag = itemId + (_0), restaurant
                    //dataExtras = myDbExtras.orderExtras(data.getString(2) + "_0", data.getString(1));    //uniquetag = itemId + (_0), restaurant

                    identifier = -1;
                    do {
                        //TODO: Change this to an actual number when the user selects the number of items of a particular kind
                        identifier++;
                        dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(identifier), data.getString(1));    //uniquetag = itemId + (_0), restaurant;

                    } while (dataExtras.getCount() > 0);

                    identifier--;       //to normalize it since the previous loop checks if the next value is 0 or not

                    for(int idCount = 0; idCount <= identifier; idCount++){

                        itemRef = data.getString(2) + "_" + String.valueOf(idCount);     //uniquetag = itemId + (_0)
                        Log.d("Item Ref", itemRef);
                        dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(idCount), data.getString(1));    //uniquetag = itemId + (_0), restaurant


                        if(dataExtras == null){
                            Log.d("dataExtras", "null");
                        }
                        else {
                            Log.d("dataExtras", "not null");
                            Log.d("dataExtras", "Size: " + dataExtras.getCount());

                            if(dataExtras.getCount() == 0){
                                reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                reference.child(pushId).child("customeruid_from").setValue("none");
                                reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                reference.child(pushId).child("postid").setValue(pushId);
                                //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));
                            }
                            else {
                                for(int i = 0; i < dataExtras.getCount(); i++){
                                    dataExtras.moveToPosition(i);
                                    Log.d("dataExtra", dataExtras.getString(0));


                                    //checking for any extra reading 0 quantity and excluding it
                                    if (Integer.parseInt(dataExtras.getString(2)) > 0) {
                                        Log.d("Extra for Upload", dataExtras.getString(0));
                                        extrasListing.put("name", dataExtras.getString(0));    //name, extraName
                                        extrasListing.put("cost", dataExtras.getString(1));    //cost, extraCost
                                        extrasListing.put("quantity", dataExtras.getString(2));    //cost, extraQuantity
                                        extrasListing.put("parent", dataExtras.getString(3));    //parent(type) of extra, type
                                    }

                                    Log.d("ExtraListing", extrasListing.toString());

                                    reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                    reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                    reference.child(pushId).child("customeruid_from").setValue("none");
                                    reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                    reference.child(pushId).child("postid").setValue(pushId);
                                    //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                    reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                    reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);
                                    reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                    reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));


                                    Log.d("Location_0", data.getString(2) + "_" + String.valueOf(indexItems));
                                    extrasListing.clear();

                                }
                            }



                        }

                    }

                    indexItems++;
                    //Log.d("Push ID", pushId.toString());
                    pushId = reference.push().getKey();     //sets a new push id for the different restaurant
                    Log.d("Refreshing Push", pushId);
                    Log.d("The items pushed for " + restaurant, itemListing.toString());
                    itemListing.clear();
                    costListing.clear();
                    notification.clear();
                    extrasListing.clear();
                } else {

                    indexItems = 0;
                    Log.d("Outcome", "different");
                    pushId = reference.push().getKey();     //sets a new push id for the different restaurant
                    Log.d("Setting new pushID", pushId);
                    itemListing.clear();
                    costListing.clear();
                    notification.clear();
                    Log.d("New info set", data.getString(1));
                    //get the value from the database in column
                    //then add it to the ArrayList
                    //listData.add(data.getString(2));
                    notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                    notification.put("customeruid_to", user.getUid());
                    notification.put("customeruid_from", "none");
                    notification.put("vendoruid", data.getString(1));
                    //notification.put("runneruid", "Runner");
                    notification.put("postid", pushId);
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                    costListing.put(data.getString(2), data.getString(3));    //itemId, cost

                    //itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)
                    Log.d("Location_5", data.getString(2) + "_" + String.valueOf(indexItems));


                    //dataExtras = myDbExtras.orderExtras(data.getString(2), data.getString(1));    //uniquetag = itemId + (_0), restaurant
                    //dataExtras = myDbExtras.orderExtras(data.getString(2) + "_0", data.getString(1));    //uniquetag = itemId + (_0), restaurant

                    identifier = -1;
                    do {
                        //TODO: Change this to an actual number when the user selects the number of items of a particular kind
                        identifier++;
                        dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(identifier), data.getString(1));    //uniquetag = itemId + (_0), restaurant;

                    } while (dataExtras.getCount() > 0);

                    identifier--;       //to normalize it since the previous loop checks if the next value is 0 or not

                    for(int idCount = 0; idCount <= identifier; idCount++){

                        itemRef = data.getString(2) + "_" + String.valueOf(idCount);     //uniquetag = itemId + (_0)
                        Log.d("Item Ref", itemRef);
                        dataExtras = myDbExtras.orderExtras(data.getString(2) + "_" + String.valueOf(idCount), data.getString(1));    //uniquetag = itemId + (_0), restaurant


                        if(dataExtras == null){
                            Log.d("dataExtras", "null");
                        }
                        else {
                            Log.d("dataExtras", "not null");
                            Log.d("dataExtras", "Size: " + dataExtras.getCount());

                            if(dataExtras.getCount() == 0){
                                reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                reference.child(pushId).child("customeruid_from").setValue("none");
                                reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                reference.child(pushId).child("postid").setValue(pushId);
                                //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));
                            }
                            else {
                                for(int i = 0; i < dataExtras.getCount(); i++){
                                    dataExtras.moveToPosition(i);
                                    Log.d("dataExtra", dataExtras.getString(0));


                                    //checking for any extra reading 0 quantity and excluding it
                                    if (Integer.parseInt(dataExtras.getString(2)) > 0) {
                                        Log.d("Extra for Upload", dataExtras.getString(0));
                                        extrasListing.put("name", dataExtras.getString(0));    //name, extraName
                                        extrasListing.put("cost", dataExtras.getString(1));    //cost, extraCost
                                        extrasListing.put("quantity", dataExtras.getString(2));    //cost, extraQuantity
                                        extrasListing.put("parent", dataExtras.getString(3));    //parent(type) of extra, type
                                    }

                                    Log.d("ExtraListing", extrasListing.toString());

                                    reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                                    reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                                    reference.child(pushId).child("customeruid_from").setValue("none");
                                    reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                                    reference.child(pushId).child("postid").setValue(pushId);
                                    //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                                    reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                                    reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);
                                    reference.child(pushId).child("items").child(itemRef).child("name").setValue(data.getString(2));
                                    reference.child(pushId).child("items").child(itemRef).child("cost").setValue(data.getString(3));


                                    Log.d("Location_0", data.getString(2) + "_" + String.valueOf(indexItems));
                                    extrasListing.clear();

                                }
                            }



                        }

                    }

                    Log.d("The items pushed for " + next_restaurant, itemListing.toString());
                    //Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show();
                }
            }
        }

        final Map card = new HashMap<>();

        String uid = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("/stripe_customers/" + uid + "/charges");
        String pushId = reference.push().getKey();     //String
        //reference.child(pushId).child("token").setValue(token.getCard());
        //card.put("amount", 100);
        card.put("amount", total_cost * 100); //cost for Stripe is given in cents so multiply by 100 to get the dollar value
        reference.child(pushId).setValue(card);

        myDb.deleteAll();       //clears the database of current items
        myDbExtras.deleteAll(); //clears the database of extra items
        //}

        finish();
    }


    private void processPayments() {
        //final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        final Map card = new HashMap<>();


/*        defaultPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment.setChecked(false);
            }
        });

        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultPayment.setChecked(false);
            }
        });*/

        //Button pay = (Button) findViewById(R.id.pay);
        //Button cancel = (Button) findViewById(R.id.cancel);


/*        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Button", "clicked");
                getPaymentsReady();
            }
        });*/

/*        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {




                //if card data is valid then the receipt popup will appear
                CharSequence options[] = new CharSequence[] {"Place Order", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
                builder.setTitle("Checkout");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){
                            if(addPayment.isChecked()){
                                final Card cardToSave = mCardInputWidget.getCard();
                                if (cardToSave == null) {
                                    //mErrorDialogHandler.showError("Invalid Card Data");
                                    Snackbar.make(view, "Invalid Card Data", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    cardToSave.validateNumber();
                                    cardToSave.validateCVC();
                                    if(!cardToSave.validateCard()) {
                                        Snackbar.make(view, "Card Data Invalid", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    } else {
                                        Snackbar.make(view, "Card Data Valid", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        //Accept payment and receipt
                                        Stripe stripe = new Stripe(view.getContext(), getResources().getString(R.string.stripe_key));      //TODO: Change this to the official product public key
                                        stripe.createToken(
                                                cardToSave,
                                                new TokenCallback() {
                                                    public void onSuccess(Token token) {
                                                        // Send token to your server
                                                        Log.d("token", token.getCard().toString());

                                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                                        String uid = user.getUid();
                                                        Log.d("UID for Payment", uid);
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/stripe_customers/" + uid + "/sources");
                                                        String pushId = reference.push().getKey();     //String
                                                        //reference.child(pushId).child("token").setValue(token.getCard());
                                                        card.put("object", "card");
                                                        card.put("exp_month", cardToSave.getExpMonth());
                                                        card.put("exp_year", cardToSave.getExpYear());
                                                        card.put("number", cardToSave.getNumber());
                                                        card.put("cvc", cardToSave.getCVC());


                                                        //creates a new source before payment
                                                        reference.child(pushId).child("token").updateChildren(card);


                                                        getPaymentsReady();

                                                        Snackbar.make(view, "Payment Successful", Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();

                                                    }
                                                    public void onError(Exception error) {
                                                        // Show localized error message
                                                        Snackbar.make(view, error.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();
                                                    }
                                                }
                                        );

                                    }
                                }

                            }

                            else {
                                getPaymentsReady();
                            }
                        }

                        else {
                            //Go back to receipt
                        }
                    }
                });

                builder.show();     //shows the dialog box for the receipt click choice

            }
        });*/


/*        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[] {"Clear Cart", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
                builder.setTitle("Clear Cart");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //Clear the cart
                            myDb.deleteAll();
                            myDbExtras.deleteAll();
                            finish();
                        }
                        else {
                            //Go back to receipt
                        }
                    }
                });
                builder.show();     //shows the dialog box for the cancel click choice
            }
        });
    }*/
    }
}
