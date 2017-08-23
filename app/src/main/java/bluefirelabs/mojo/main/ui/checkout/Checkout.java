package bluefirelabs.mojo.main.ui.checkout;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;
import database.DatabaseHelperExtras;

public class Checkout extends FragmentActivity {

    DatabaseHelper myDb;
    DatabaseHelperExtras myDbExtras;

    private String itemRef;
    private Cursor dataExtras;
    private LinearLayout listContainer;
    private RelativeLayout detail_card_layout;

    private ImageView image;

    private TextView restaurant_name, item_quantity, item_dets, item_cost, item_description, remove_item; //receipt item view
    private TextView item_subtotal, item_tax, item_total;           //base wrapper for the main view
    private TextView item_subtotal_value, item_tax_value, item_total_value;     //extended base wrapper for the main view

    View accentLayout, restaurant_separator;
    private String pushId;

    private RadioButton defaultPayment, addPayment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        defaultPayment = (RadioButton) findViewById(R.id.default_payment);
        addPayment = (RadioButton) findViewById(R.id.add_payment);

/*        listContainer = (LinearLayout) findViewById(R.id.checkout_list_container);



        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        View childView = layoutInflater.inflate(R.layout.checkout_item, null);

        listContainer.addView(childView);



        //The receipt item contents
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
        myDbExtras = new DatabaseHelperExtras(this); //calls constructor from the database helper class

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
        processPayments();
    }


    private void getPaymentsReady(){


        //for(int i = 0; i<=99; i++){       //this is a test loop to place 100 orders
        Log.d("Button clicked", "Placing order");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pushId = reference.push().getKey();     //String


        Map notification = new HashMap<>();
        Map itemListing = new HashMap<>();
        Map costListing = new HashMap<>();
        Map extrasListing = new HashMap<>();

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

                itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)

                dataExtras = myDbExtras.orderExtras(itemRef, data.getString(1));    //uniquetag = itemId + (_0), restaurant

                //Iterates through the database for all extras
                while(dataExtras.moveToNext()){
                    //checking for any extra reading 0 quantity and excluding it
                    if(Integer.parseInt(dataExtras.getString(2)) > 0){
                        Log.d("Extra for Upload", dataExtras.getString(0));
                        extrasListing.put("name",  dataExtras.getString(0));    //name, extraName
                        extrasListing.put("cost",  dataExtras.getString(1));    //cost, extraCost
                        extrasListing.put("quantity",  dataExtras.getString(2));    //cost, extraQuantity
                        extrasListing.put("parent",  dataExtras.getString(3));    //parent(type) of extra, type
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

                    extrasListing.clear();
                }

                /*reference.child(pushId).child("items").child(itemRef).setValue(itemListing);
                reference.child(pushId).child("items").child(itemRef).setValue(costListing);*/
                /*reference.child(pushId).child("items").setValue(itemListing);
                reference.child(pushId).child("cost").setValue(costListing);*/


                /*itemListing.put("name", data.getString(2));    //name, itemId
                itemListing.put("cost", data.getString(3));    //cost, cost

                itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)

                dataExtras = myDbExtras.orderExtras(itemRef, data.getString(1));    //uniquetag = itemId + (_0), restaurant

                //Iterates through the database for all extras
                while(dataExtras.moveToNext()){
                    Log.d("Extra for Upload", dataExtras.getString(0));
                    extrasListing.put("name",  dataExtras.getString(0));    //name, extraName
                    extrasListing.put("cost",  dataExtras.getString(1));    //cost, extraCost
                    extrasListing.put("quantity",  dataExtras.getString(2));    //cost, extraQuantity

                    reference.child(pushId).child("items").child(itemRef).child("extras").setValue(extrasListing);

                    Log.d("ExtraListing", extrasListing.toString());

                    extrasListing.clear();


                }

                reference.child(pushId).setValue(notification);
                *//*reference.child(pushId).child("items").child(itemRef).setValue(itemListing);
                reference.child(pushId).child("items").child(itemRef).setValue(costListing);*//*
                reference.child(pushId).child("items").child(itemRef).setValue(itemListing);
                //reference.child(pushId).child("cost").setValue(costListing);*/



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

                    itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)

                    dataExtras = myDbExtras.orderExtras(itemRef, data.getString(1));    //uniquetag = itemId + (_0), restaurant

                    //Iterates through the database for all extras
                    while(dataExtras.moveToNext()){
                        //checking for any extra reading <= 0 quantity and excluding it
                        if(Integer.parseInt(dataExtras.getString(2)) > 0){
                            Log.d("Extra for Upload", dataExtras.getString(0));
                            extrasListing.put("name",  dataExtras.getString(0));    //name, extraName
                            extrasListing.put("cost",  dataExtras.getString(1));    //cost, extraCost
                            extrasListing.put("quantity",  dataExtras.getString(2));    //cost, extraQuantity
                            extrasListing.put("parent",  dataExtras.getString(3));    //parent(type) of extra, type
                        }


                        reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                        reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                        reference.child(pushId).child("customeruid_from").setValue("none");
                        reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                        reference.child(pushId).child("postid").setValue(pushId);
                        //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                        reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                        reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);

                        extrasListing.clear();

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

                    itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)

                    dataExtras = myDbExtras.orderExtras(itemRef, data.getString(1));    //uniquetag = itemId + (_0), restaurant

                    //Iterates through the database for all extras
                    while(dataExtras.moveToNext()){

                        //checking for any extra reading <=0 quantity and excluding it
                        if(Integer.parseInt(dataExtras.getString(2)) > 0){
                            Log.d("Extra for Upload", dataExtras.getString(0));
                            extrasListing.put("name",  dataExtras.getString(0));    //name, extraName
                            extrasListing.put("cost",  dataExtras.getString(1));    //cost, extraCost
                            extrasListing.put("quantity",  dataExtras.getString(2));    //cost, extraQuantity
                            extrasListing.put("parent",  dataExtras.getString(3));    //parent(type) of extra, type
                        }

                        reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                        reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                        reference.child(pushId).child("customeruid_from").setValue("none");
                        reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                        reference.child(pushId).child("postid").setValue(pushId);
                        //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                        reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                        reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);

                        extrasListing.clear();

                    }
                    //Log.d("Push ID", pushId.toString());
                    pushId = reference.push().getKey();     //sets a new push id for the different restaurant
                    Log.d("Refreshing Push", pushId);
                    Log.d("The items pushed for " + restaurant, itemListing.toString());
                    itemListing.clear();
                    costListing.clear();
                    notification.clear();
                    extrasListing.clear();
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

                    itemRef = data.getString(2) + "_0";     //uniquetag = itemId + (_0)

                    dataExtras = myDbExtras.orderExtras(itemRef, data.getString(1));    //uniquetag = itemId + (_0), restaurant

                    //Iterates through the database for all extras
                    while(dataExtras.moveToNext()){
                        //checking for any extra reading <=0 quantity and excluding it
                        if(Integer.parseInt(dataExtras.getString(2)) > 0){
                            Log.d("Extra for Upload", dataExtras.getString(0));
                            extrasListing.put("name",  dataExtras.getString(0));    //name, extraName
                            extrasListing.put("cost",  dataExtras.getString(1));    //cost, extraCost
                            extrasListing.put("quantity",  dataExtras.getString(2));    //cost, extraQuantity
                            extrasListing.put("parent",  dataExtras.getString(3));    //parent(type) of extra, type
                        }


                        reference.child(pushId).child("user_token").setValue(FirebaseInstanceId.getInstance().getToken());
                        reference.child(pushId).child("customeruid_to").setValue(user.getUid());
                        reference.child(pushId).child("customeruid_from").setValue("none");
                        reference.child(pushId).child("vendoruid").setValue(data.getString(1));
                        reference.child(pushId).child("postid").setValue(pushId);
                        //reference.child(pushId).child("items").child(data.getString(2)).setValue(data.getString(4));
                        reference.child(pushId).child("cost").child(data.getString(2)).setValue(data.getString(3));
                        reference.child(pushId).child("items").child(itemRef).child("extras").child(dataExtras.getString(0)).setValue(extrasListing);

                        extrasListing.clear();

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


    private void processPayments(){
        final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        final Map card = new HashMap<>();


        defaultPayment.setOnClickListener(new View.OnClickListener() {
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
        });

        Button pay = (Button) findViewById(R.id.pay);
        Button cancel = (Button) findViewById(R.id.cancel);

        pay.setOnClickListener(new View.OnClickListener() {
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
        });


        cancel.setOnClickListener(new View.OnClickListener() {
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
    }



    private void populateListView() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pushId = reference.push().getKey();     //String

        DecimalFormat df = new DecimalFormat("#.00");

        Double subtotalCost = 0.00;

        final Cursor data = myDb.orderAlpha();


        int count = data.getCount();
        for (int position = 0; position < count; position++) {
            data.moveToPosition(position);
            Log.d("Database", data.getString(1));
            Log.d("Cost", data.getString(3));
            subtotalCost+= Double.parseDouble(data.getString(3)) * Double.parseDouble(data.getString(4)) * 1.00; //adding the cost of each item multiplied by the quantity and *1.00 to keep it a double
        }


        String subtotal = "$" + String.valueOf(df.format(subtotalCost));
        item_subtotal_value.setText(subtotal);      //setting the subtotal value on the main layout


        Double finalCost = subtotalCost + Double.parseDouble(item_tax_value.getText().toString().replace("$", ""));
        String total = "$" + String.valueOf(df.format(finalCost));
        item_total_value.setText(total);      //setting the subtotal value on the main layout



        listContainer = (LinearLayout) findViewById(R.id.checkout_list_container);

        Log.d("Checkout", "populating items");

            Double total_cost = 0.00;

        final LayoutInflater layoutInflater = LayoutInflater.from(this);

        for(int position = 0; position < count; position ++) {
            data.moveToPosition(position);
                View childView = layoutInflater.inflate(R.layout.checkout_item, null);
                //The receipt item contents
                restaurant_name = (TextView) childView.findViewById(R.id.extra_parent);
                item_quantity = (TextView) childView.findViewById(R.id.item_quantity);
                item_dets = (TextView) childView.findViewById(R.id.item_dets);
                item_cost = (TextView) childView.findViewById(R.id.item_cost);
                item_description = (TextView) childView.findViewById(R.id.item_description);
                restaurant_separator = (View) childView.findViewById(R.id.restaurant_separator);
                restaurant_name = (TextView) childView.findViewById(R.id.extra_parent);
                remove_item = (TextView) childView.findViewById(R.id.remove_item);
                image = (ImageView) childView.findViewById(R.id.image);
                //////////////////////////////////////////////////////////////////////////////////
            String placeHolder = data.getString(1);     //restaurant name
            String placeHolder_future = "";     //restaurant name
            String placeHolder_previous = "";     //restaurant name

            //Checking the next parameter
            if(position > 0 && data.moveToNext()) {
                placeHolder_future = data.getString(1);     //restaurant name
                Log.d("Future", placeHolder_future);
            }
            data.moveToPosition(position);      //resetting to the current position

            //Checking the previous parameter
            if(position > 0 && data.moveToPrevious()) {
                placeHolder_previous = data.getString(1);     //restaurant name
                Log.d("Previous", placeHolder_previous);

            }

            data.moveToPosition(position);      //resetting to the current position


            if(position == 0 && data.moveToNext()){
                placeHolder_future = data.getString(1);     //restaurant name
                Log.d("Initial Future", placeHolder_future);

            }

            data.moveToPosition(position);      //resetting to the current position


            if(placeHolder.equals(placeHolder_previous)) {

                //1. If previous restaurant equals future restaurant (middle value) then remove the
                //separators
                //middle
                if(placeHolder.equals(placeHolder_future)){
                    restaurant_separator.setVisibility(View.GONE);
                    restaurant_name.setVisibility(View.GONE);
                    image.setVisibility(View.GONE);

                    listContainer.addView(childView);
                }

                //2. If previous restaurant does not equal future restaurant (middle value) then remove the
                //separators
                //last
                else if (!placeHolder.equals(placeHolder_future)){

                    //Checking the case for the last item
                    //if the future item belongs to the same restaurant
                    //last item, set visibility to gone
                    if(position == count-1){
                        restaurant_separator.setVisibility(View.GONE);
                        restaurant_name.setVisibility(View.GONE);
                        image.setVisibility(View.GONE);
                    }

                    else if(position == count-2){
                        restaurant_separator.setVisibility(View.GONE);
                        restaurant_name.setVisibility(View.GONE);
                        image.setVisibility(View.GONE);
                    }

                    //Checking the case for all other items
                    //if the future item does not belong to the same restaurant
                    //generic item, set visibility to visible
                    else {
                        restaurant_separator.setVisibility(View.VISIBLE);
                        restaurant_name.setVisibility(View.VISIBLE);
                        image.setVisibility(View.VISIBLE);
                    }

                    listContainer.addView(childView);
                }
            } else {

                if (placeHolder.equals(placeHolder_previous)){
                    //First case at position = 1 if the restaurant equals the first restaurant
                    restaurant_separator.setVisibility(View.GONE);
                    restaurant_name.setVisibility(View.GONE);
                    image.setVisibility(View.GONE);

                }

                else if (!placeHolder.equals(placeHolder_previous)){
                    //First case at position = 0
                    restaurant_separator.setVisibility(View.VISIBLE);
                    restaurant_name.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);

                }

                listContainer.addView(childView);

            }

            restaurant_name.setText(data.getString(1));

            int positionClicked = listContainer.indexOfChild(childView);
            /*childView.setTag(positionClicked);*/
            remove_item.setTag(positionClicked);


/*            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionClicked = (int) v.getTag();
                    Log.d("Position", String.valueOf(positionClicked));

                    Log.d("Item", item_dets.getText().toString());
                }
            });*/


            item_dets.setText(data.getString(2));
            item_quantity.setText(data.getString(4));
            String cost = "$" + String.valueOf(df.format(Double.parseDouble(data.getString(3))));       //Displays with double decimals
            item_cost.setText(cost);

            remove_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int positionClicked = (int) v.getTag();


                    //String sanitizedItem = item_dets.getText().toString().replace("'", "''");        //looks for any "'" in the item name (like S'Mores) so that the DatabaseHelper can properly query it

                    data.moveToPosition(positionClicked);
                    String sanitizedItem = data.getString(2).replace("'", "''");        //looks for any "'" in the item name (like S'Mores) so that the DatabaseHelper can properly query it
                    Log.d("Item", sanitizedItem);


                    Cursor dataItem = myDb.getItemID(sanitizedItem);        //gets the primary key associated with the item name
                    int itemID = -1;
                    while(dataItem.moveToNext()){
                        itemID = dataItem.getInt(0);
                    }
                    if(itemID > 0){

                        myDb.deleteName(itemID, sanitizedItem);     //The item name and ID are used to delete the item on checkbox unchecked

                        Snackbar.make(v, item_dets.getText() + " removed from Cart",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        finish();
                        startActivity(getIntent());

                    }
                }
            });

        }



    }





}
