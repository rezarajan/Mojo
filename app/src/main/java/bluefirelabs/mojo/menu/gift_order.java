package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

public class gift_order extends AppCompatActivity {


    private static final String TAG = "editCheckoutActivity";

    private Button btn_del, btn_save;
    private EditText editable_item;

    private String pushId;

    DatabaseHelper myDb;

    private String selectedName;
    private int selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcheckout_layout);


        btn_save = (Button)findViewById(R.id.button_save);
        btn_del = (Button)findViewById(R.id.button_delete);
        editable_item = (EditText) findViewById(R.id.editText);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pushId = reference.push().getKey();     //String

        myDb = new DatabaseHelper(this);

        editable_item.setText("Please Enter Recipient UID");
        btn_save.setText("Gift");

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = editable_item.getText().toString();
                if(!item.equals("")){
                    Log.d("Button clicked", "Placing order");

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requests");
                    FirebaseUser user = firebaseAuth.getCurrentUser();


                    Map notification = new HashMap<>();
                    Map itemListing = new HashMap<>();
                    Map costListing = new HashMap<>();

                    int total_cost = 0;


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
                        Log.d("Cost", data.getString(1));
                        total_cost += Integer.parseInt(data.getString(3)) * Integer.parseInt(data.getString(4)); //adding the cost of each item multiplied by the quantity

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
                            notification.put("customeruid_to", editable_item.getText().toString());
                            notification.put("customeruid_from", user.getUid());
                            notification.put("vendoruid", data.getString(1));
                            notification.put("result", "transient");
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
                                notification.put("customeruid_to", editable_item.getText().toString());
                                notification.put("customeruid_from", user.getUid());
                                notification.put("vendoruid", data.getString(1));
                                notification.put("result", "transient");
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
                                notification.put("customeruid_to", editable_item.getText().toString());
                                notification.put("customeruid_from", user.getUid());
                                notification.put("vendoruid", data.getString(1));
                                notification.put("result", "transient");
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
                                notification.put("customeruid_to", editable_item.getText().toString());
                                notification.put("customeruid_from", user.getUid());
                                notification.put("vendoruid", data.getString(1));
                                notification.put("result", "transient");
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
                } else{
                    Snackbar.make(v, "Please enter a recipient",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_del.setText("Back");
                Intent intent = new Intent(gift_order.this, Checkout.class);
                startActivity(intent);
            }
        });
    }
}
