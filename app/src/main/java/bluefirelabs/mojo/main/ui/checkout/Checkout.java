package bluefirelabs.mojo.main.ui.checkout;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

public class Checkout extends FragmentActivity {

    DatabaseHelper myDb;
    private LinearLayout listContainer;
    private RelativeLayout detail_card_layout;

    private TextView restaurant_name, item_quantity, item_dets, item_cost, item_description; //checkout item view
    private TextView item_subtotal, item_tax, item_total;           //base wrapper for the main view
    private TextView item_subtotal_value, item_tax_value, item_total_value;     //extended base wrapper for the main view

    View accentLayout, restaurant_separator;
    private String pushId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

/*        listContainer = (LinearLayout) findViewById(R.id.checkout_list_container);



        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        View childView = layoutInflater.inflate(R.layout.checkout_item, null);

        listContainer.addView(childView);



        //The checkout item contents
        restaurant_name = (TextView) childView.findViewById(R.id.restaurant_name);
        item_quantity = (TextView) childView.findViewById(R.id.item_quantity);
        item_dets = (TextView) childView.findViewById(R.id.item_dets);
        item_cost = (TextView) childView.findViewById(R.id.item_cost);
        item_description = (TextView) childView.findViewById(R.id.item_description);*/


        ////////////////////////////////////////////////////////////////////////////////////

        //The main layout items
        item_subtotal_value = (TextView) findViewById(R.id.item_subtotal_value);
        item_tax_value = (TextView) findViewById(R.id.item_tax_value);
        item_total_value = (TextView) findViewById(R.id.item_total_value);


        /////////////////////////////////////////////////////////////////////////////////////



/*        mListView = (ListView)findViewById(R.id.listview_checkout);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        placeorder = (Button)findViewById(R.id.button_place_order);
        noitems = (TextView)findViewById(R.id.content_available_indicator);*/
        myDb = new DatabaseHelper(this);
        Cursor data = myDb.getAllData();
        if(data.getCount() > 0){
/*            placeorder.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.INVISIBLE);*/
        } else {
/*            placeorder.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            noitems.setVisibility(View.VISIBLE);*/
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //pushId = reference.push().getKey();     //String

        populateListView();
    }

    private void populateListView() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pushId = reference.push().getKey();     //String

        DecimalFormat df = new DecimalFormat("#.00");


        Map notification = new HashMap<>();
        Map itemListing = new HashMap<>();
        Map costListing = new HashMap<>();
        //Map previousPlaceholder = new HashMap<>();
        String previousPlaceholder_Dets = "";
        String previousPlaceholder_Quantity = "";
        String previousPlaceholder_Cost = "";
        String previousPlaceholder_Description = "";

        Double total_cost = 0.00;

        //order data alphabetically by restaurant name and append to a list
        Cursor data = myDb.orderAlpha();

        String restaurant, next_restaurant;     //to compare data for different restaurants

        int count = data.getCount();
        for (int position = 0; position < count; position++) {
            data.moveToPosition(position);
            Log.d("Database", data.getString(1));
            Log.d("Cost", data.getString(3));
            total_cost += Double.parseDouble(data.getString(3)) * Double.parseDouble(data.getString(4)) * 1.00; //adding the cost of each item multiplied by the quantity and *1.00 to keep it a double
        }

        total_cost = Math.round(total_cost*100.0)/100.0;

        String subtotal = "$" + String.valueOf(total_cost);
        item_subtotal_value.setText(subtotal);      //setting the subtotal value on the main layout


        Double finalCost = total_cost + Double.parseDouble(item_tax_value.getText().toString().replace("$", ""));
        String total = "$" + String.valueOf(finalCost);
        item_total_value.setText(total);      //setting the subtotal value on the main layout



        listContainer = (LinearLayout) findViewById(R.id.checkout_list_container);

        Log.d("Checkout", "populating items");

        //get the data and append to a list
        //Cursor data = myDb.getAllData();

/*        ArrayList<String> itemnameData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value of the item name from the database in column
            //then add it to the ArrayList
            itemnameData.add(data.getString(2));
        }*/


        //Iterator<String> iterator = itemnameData.iterator();

/*        for(int i = 0; i < itemnameData.size(); i++){
            final LayoutInflater layoutInflater = LayoutInflater.from(this);
            View childView = layoutInflater.inflate(R.layout.checkout_item, null);
            listContainer.addView(childView);



            //The checkout item contents
            restaurant_name = (TextView) childView.findViewById(R.id.restaurant_name);
            item_quantity = (TextView) childView.findViewById(R.id.item_quantity);
            item_dets = (TextView) childView.findViewById(R.id.item_dets);
            item_cost = (TextView) childView.findViewById(R.id.item_cost);
            item_description = (TextView) childView.findViewById(R.id.item_description);
            item_dets.setText(itemnameData.get(i));
            System.out.println(itemnameData.get(i));
        }*/



        /*Filtering algorithm for different restaurants*/
        for (int position = 0; position < count; position++) {

            /*Setting up the card view for each item */

            final LayoutInflater layoutInflater = LayoutInflater.from(this);
            View childView = layoutInflater.inflate(R.layout.checkout_item, null);



            //The checkout item contents
            restaurant_name = (TextView) childView.findViewById(R.id.restaurant_name);
            item_quantity = (TextView) childView.findViewById(R.id.item_quantity);
            item_dets = (TextView) childView.findViewById(R.id.item_dets);
            item_cost = (TextView) childView.findViewById(R.id.item_cost);
            item_description = (TextView) childView.findViewById(R.id.item_description);

            //////////////////////////////////////////////////////////////////////////////////

/*
            listContainer.addView(childView);

            item_dets.setText(data.getString(2));
            item_quantity.setText(data.getString(4));
            item_cost.setText(data.getString(3));
*/


            data.moveToPosition(position);
            restaurant = data.getString(1);     //gets the restaurant name



            //data.moveToPosition(position + 1);
            if (data.moveToNext()) {
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
                itemListing.put(data.getString(2), data.getString(4));    //itemId (name), quantity
                costListing.put(data.getString(2), data.getString(3));    //itemId (name), cost

                //item_description.setText(data.getString(3));
                //TODO: Make a new column in the SQL Database to accomodate the item description





                /*reference.child(pushId).setValue(notification);
                reference.child(pushId).child("items").setValue(itemListing);
                reference.child(pushId).child("cost").setValue(costListing);*/
                //Log.d("Push ID", pushId.toString());
                pushId = reference.push().getKey();     //sets a new push id for the different restaurant
                Log.d("Refreshing Push", pushId);
                Log.d("The items pushed for " + restaurant + " are", itemListing.toString());



                //System.out.println(data.getString(2));

/*                item_dets.setText(data.getString(2));
                item_quantity.setText(data.getString(4));
                item_cost.setText(data.getString(3));       //TODO: Item cost has to be multiplied by quantity*/


                /*reference.child(pushId).setValue(notification);
                reference.child(pushId).child("items").setValue(itemListing);
                reference.child(pushId).child("cost").setValue(costListing);
*/
                //data.moveToPosition(position + 1);
                if (data.moveToNext()) {
                    //get the value from the database in column
                    //then add it to the ArrayList
                    //listData.add(data.getString(2));
                    data.moveToPosition(position);
                    notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                    notification.put("customeruid_to", user.getUid());
                    notification.put("customeruid_from", "none");
                    notification.put("vendoruid", data.getString(1));
                    //notification.put("runneruid", "Runner");
                    notification.put("postid", pushId);
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                    costListing.put(data.getString(2), data.getString(3));    //itemId, cost

                    listContainer.addView(childView);
                    Log.d("Activated", "1");


                    item_dets.setText(data.getString(2));
                    item_quantity.setText(data.getString(4));
                    item_cost.setText("$" + data.getString(3));

                    reference.child(pushId).setValue(notification);
                    reference.child(pushId).child("items").setValue(itemListing);
                    reference.child(pushId).child("cost").setValue(costListing);
                    Log.d("The items pushed for " + restaurant + " are", itemListing.toString());
                    System.out.println("Moving to next");
                } else {
                    data.moveToPosition(position);

                    listContainer.addView(childView);
                    Log.d("Activated", "0");

                    item_dets.setText(data.getString(2));
                    item_quantity.setText(data.getString(4));
                    item_cost.setText("$" + data.getString(3));
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


                    listContainer.addView(childView);
                    Log.d("Activated", "2");

                    item_dets.setText(data.getString(2));
                    item_quantity.setText(data.getString(4));
                    item_cost.setText("$" + data.getString(3));

                    /*reference.child(pushId).setValue(notification);
                    reference.child(pushId).child("items").setValue(itemListing);
                    reference.child(pushId).child("cost").setValue(costListing);*/
                    //Log.d("Push ID", pushId.toString());
                    pushId = reference.push().getKey();     //sets a new push id for the different restaurant
                    Log.d("Refreshing Push", pushId);
                    Log.d("The items pushed for " + restaurant + " are", itemListing.toString());
                    itemListing.clear();
                    costListing.clear();
                    notification.clear();
                } else {
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

                    data.moveToPosition(position);


                    notification.put("user_token", FirebaseInstanceId.getInstance().getToken());
                    notification.put("customeruid_to", user.getUid());
                    notification.put("customeruid_from", "none");
                    notification.put("vendoruid", data.getString(1));
                    //notification.put("runneruid", "Runner");
                    notification.put("postid", pushId);
                    itemListing.put(data.getString(2), data.getString(4));    //itemId, quantity
                    costListing.put(data.getString(2), data.getString(3));    //itemId, cost
                    //duplicateComparisonParameter.put(data.getString(2), data.getString(4));     //using itemListing to compare for duplicates


                    listContainer.addView(childView);
                    Log.d("Activated", "3");

                    item_dets.setText(data.getString(2));
                    item_quantity.setText(data.getString(4));
                    item_cost.setText("$" + data.getString(3));
                    //System.out.println(data.getString(2));


                    /*reference.child(pushId).setValue(notification);
                    reference.child(pushId).child("items").setValue(itemListing);
                    reference.child(pushId).child("cost").setValue(costListing);*/
                    Log.d("The items pushed for " + next_restaurant + " are", itemListing.toString());
                    Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show();
                }
            }
        }



        //create the list adapter and set the adapter
/*        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);


        //set a listener for item clicks
        //this leads to the edit menu
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                String sanitizedItem = name.replace("'", "''") ; //looks for any "'" in the item name (like S'Mores) so that the DatabaseHelper can properly query it
                Log.d(TAG, "onItemClicked: You clicked on: " + name);

                Cursor data = myDb.getItemID(sanitizedItem);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > 0){
                    Snackbar.make(view, "The ID is: " + itemID,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Intent editCheckoutintent = new Intent(bluefirelabs.mojo.menu.Checkout.this, editcheckout.class);
                    editCheckoutintent.putExtra("ID", itemID);
                    editCheckoutintent.putExtra("name", name);
                    startActivity(editCheckoutintent);

                } else {
                    Snackbar.make(view, "No ID associated with that name",      //TODO: Change this to suit order quantity
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });*/
    }


/*    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.checkout_layout);

        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        View childView = layoutInflater.inflate(R.layout.checkout_item, null);

        listContainer.addView(childView);



        //The checkout item contents
        restaurant_name = (TextView) childView.findViewById(R.id.restaurant_name);
        item_quantity = (TextView) childView.findViewById(R.id.item_quantity);
        item_dets = (TextView) childView.findViewById(R.id.item_dets);
        item_cost = (TextView) childView.findViewById(R.id.item_cost);
        item_description = (TextView) childView.findViewById(R.id.item_description);


        ////////////////////////////////////////////////////////////////////////////////////


        myDb = new DatabaseHelper(this);

        Cursor data = myDb.getAllData();
        if(data.getCount() > 0){
*//*            placeorder.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.INVISIBLE);*//*
        } else {
*//*            placeorder.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            noitems.setVisibility(View.VISIBLE);*//*
        }
        populateListView();
        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[] {"Place Order", "Gift"};

                AlertDialog.Builder builder = new AlertDialog.Builder(bluefirelabs.mojo.menu.Checkout.this);
                builder.setTitle("Checkout");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position_option) {
                        // the user clicked on options[position_option]

                        if(position_option == 0) {

                            //for(int i = 0; i<=99; i++){       //this is a test loop to place 100 orders
                            Log.d("Button clicked", "Placing order");

                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            pushId = reference.push().getKey();     //String


                            Map notification = new HashMap<>();
                            Map itemListing = new HashMap<>();
                            Map costListing = new HashMap<>();

                            Double total_cost = 0.00;


                            //get the data and append to a list
                            //Cursor data = myDb.getAllData();

                            //order data alphabetically by restaurant name and append to a list
                            Cursor data = myDb.orderAlpha();

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
                                    next_restaurant = data.getString(1);
                                } else {
                                    next_restaurant = restaurant;
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

                                    reference.child(pushId).setValue(notification);
                                    reference.child(pushId).child("items").setValue(itemListing);
                                    reference.child(pushId).child("cost").setValue(costListing);

                                    //data.moveToPosition(position + 1);
                                    if (data.moveToNext() != false) {
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
                                        reference.child(pushId).setValue(notification);
                                        reference.child(pushId).child("items").setValue(itemListing);
                                        reference.child(pushId).child("cost").setValue(costListing);
                                        Log.d("The items pushed for " + restaurant + " are", itemListing.toString());
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


                                        reference.child(pushId).setValue(notification);
                                        reference.child(pushId).child("items").setValue(itemListing);
                                        reference.child(pushId).child("cost").setValue(costListing);
                                        //Log.d("Push ID", pushId.toString());
                                        pushId = reference.push().getKey();     //sets a new push id for the different restaurant
                                        Log.d("Refreshing Push", pushId);
                                        Log.d("The items pushed for " + restaurant + " are", itemListing.toString());
                                        itemListing.clear();
                                        costListing.clear();
                                        notification.clear();
                                    } else {
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


                                        reference.child(pushId).setValue(notification);
                                        reference.child(pushId).child("items").setValue(itemListing);
                                        reference.child(pushId).child("cost").setValue(costListing);
                                        Log.d("The items pushed for " + next_restaurant + " are", itemListing.toString());
                                        Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show();
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
                            //}


                        } else {
                            Intent intent = new Intent(bluefirelabs.mojo.menu.Checkout.this, gift_order.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();



            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteAll();
                populateListView();
                Toast.makeText(getApplicationContext(), "Cart Cleared", Toast.LENGTH_SHORT).show();
                placeorder.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
            }
        });
    }*/


}
