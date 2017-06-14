package bluefirelabs.mojo.main.ui.user;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.background_tasks.MyFirebaseInstanceIDService;
import bluefirelabs.mojo.fragments.restaurantlist_fragment;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import bluefirelabs.mojo.handlers.online.HttpDataHandler;
import bluefirelabs.mojo.handlers.online.SharedPrefManager;
import bluefirelabs.mojo.handlers.online.uploadImage;
import bluefirelabs.mojo.main.login.Sign_In;
import bluefirelabs.mojo.menu.Vendor_Checkout;


public class VendorHub extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , bluefirelabs.mojo.fragments.vendor_currentinfo_fragment.currentinfoListener
        , restaurantlist_fragment.restaurantlistListener
        , android.location.LocationListener
        , MyCallback{

    LocationManager locationManager;
    String provider;
    final int MY_PERMISSION_REQUEST_CODE = 7171;
    double lat, lng;
    TextView small_description;
    TextView userEmail;
    FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver;
    private String UID = "";
    public String restaurantName = "";



    //private String pushId;
    //public static List<Object> idList= new ArrayList<Object>();

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        public TextView itemTitle, itemDescription, itemName, itemTotal;
        public ImageView itemIcon;
        public Button btn_accept, btn_decline, btn_sending, btn_complete;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDescription = (TextView) itemView.findViewById(R.id.item_description);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemTotal = (TextView) itemView.findViewById(R.id.item_total_cost);
            btn_accept = (Button) itemView.findViewById(R.id.button_accept);
            btn_decline = (Button) itemView.findViewById(R.id.button_decline);
            btn_sending = (Button) itemView.findViewById(R.id.button_sending);
            btn_complete = (Button) itemView.findViewById(R.id.button_complete);
            context = itemView.getContext();

            //final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("requests");

            btn_decline.setVisibility(View.VISIBLE);
            btn_accept.setVisibility(View.VISIBLE);
            btn_sending.setVisibility(View.INVISIBLE);
            btn_complete.setVisibility(View.INVISIBLE);
        }
    }

    public void firebaseTask(final MyCallback myCallback) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("uid/"+UID+"/info");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                //hopperValues.put("key", dataSnapshot.getKey().toString());
                //Log.d("Values", dataSnapshot.getKey().toString());
                Log.d("Values", dataSnapshot.getValue().toString());
                restaurantName = (String) hopperValues.get("name"); //this directory only contains one item so it should not be a problem
                RESTAURANT  = "uid/"+restaurantName+"/requests/";
                Log.d("Restaurant", RESTAURANT);
                myCallback.callbackCall(RESTAURANT);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void callbackCall(String restaurant) {
        RESTAURANT = restaurant;
    }

    public static String RESTAURANT;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Food_List, RecyclerViewHolder> mFirebaseAdapter;

    private RecyclerView mRestaurantRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic("Starbucks");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIDService.TOKEN_BROADCAST));

        if(SharedPrefManager.getInstance(this).getToken() != null){
            Log.d("FCM Token: ", SharedPrefManager.getInstance(this).getToken());
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        UID = user.getUid();

///////////////////////////

        MyCallback myCallback = new MyCallback() {
            @Override
            public void callbackCall(String restaurant) {
                RESTAURANT = restaurant;
                Log.d("Restaurant Returned", RESTAURANT);

                mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                mLinearLayoutManager = new LinearLayoutManager(VendorHub.this);
                mLinearLayoutManager.setStackFromEnd(true);

                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mFirebaseAdapter = new FirebaseRecyclerAdapter<Food_List, VendorHub.RecyclerViewHolder>(
                        Food_List.class,
                        R.layout.vendor_card_layout,
                        VendorHub.RecyclerViewHolder.class,
                        mFirebaseDatabaseReference.child(RESTAURANT)
                ) {

                    @Override
                    protected void populateViewHolder(final VendorHub.RecyclerViewHolder viewHolder, Food_List model, int position) {
                        viewHolder.itemTitle.setText("Order ID: " + model.getOrderid());
                        viewHolder.itemName.setText(model.getName());

                        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("requests");

                        viewHolder.btn_decline.setVisibility(View.VISIBLE);
                        viewHolder.btn_accept.setVisibility(View.VISIBLE);
                        viewHolder.btn_sending.setVisibility(View.INVISIBLE);
                        viewHolder.btn_complete.setVisibility(View.INVISIBLE);

                        viewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference1.orderByChild("orderid").equalTo(viewHolder.itemTitle.getText().toString().replace("Order ID: ", "")).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        DatabaseReference hopperRef = reference1.child(viewHolder.itemTitle.getText().toString().replace("Order ID: ", ""));     //uses the itemTitle, which is set to be the orderid, in order to get the order id on click of a specific card
                                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                                        hopperUpdates.put("result", "accepted");
                                        hopperRef.updateChildren(hopperUpdates);                                           //updates the child, without destroying, or overwriting all data
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                viewHolder.btn_decline.setVisibility(View.INVISIBLE);
                                viewHolder.btn_accept.setVisibility(View.INVISIBLE);
                                viewHolder.btn_sending.setVisibility(View.VISIBLE);
                            }
                        });

                        viewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ///////////////////////////////////////////////////////////////////////////////////////////////////
                                //This part of the code retrieved a specific part of the data from the firebase database
                                //It bypasses the wildcard requirement by filtering for a specific child value in the
                                //reference provided, which is requests in this case

                                //reference1.orderByChild("orderid").equalTo(pushId).addChildEventListener(new ChildEventListener() {     //searches specifically for the orderid "-KlSF5GydNxjegYK--R2"
                                reference1.orderByChild("orderid").equalTo(viewHolder.itemTitle.getText().toString().replace("Order ID: ", "")).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        //Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();        //stores all the child data in a map
                                        //Log.e("onChildAdded", dataSnapshot.toString());
                                        //Log.e("orderid retrieved", newPost.get("orderid").toString());                      //searches the map newPost for the child "orderid" and then returns the value
                                        //Log.e("idList Order", idList.get(position+1).toString());

                                        //Log.e("idListTextView", itemTitle.getText().toString());
                                        //DatabaseReference hopperRef = reference1.child(newPost.get("orderid").toString()); //this part adds a child reference to the orderid in requests. Remember, the parent is set up to be the orderid.
                                        //DatabaseReference hopperRef = reference1.child(idList.get(position+1).toString());      //+1 becuase the position gives a value of 1 less than what is needed
                                        DatabaseReference hopperRef = reference1.child(viewHolder.itemTitle.getText().toString().replace("Order ID: ", ""));     //uses the itemTitle, which is set to be the orderid, in order to get the order id on click of a specific card
                                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                                        hopperUpdates.put("result", "declined");                                           //appends the key "result" a value of "accepted". This can be changed to suit
                                        hopperRef.updateChildren(hopperUpdates);                                           //updates the child, without destroying, or overwriting all data
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        viewHolder.btn_sending.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ///////////////////////////////////////////////////////////////////////////////////////////////////
                                //This part of the code retrieved a specific part of the data from the firebase database
                                //It bypasses the wildcard requirement by filtering for a specific child value in the
                                //reference provided, which is requests in this case

                                //reference1.orderByChild("orderid").equalTo(pushId).addChildEventListener(new ChildEventListener() {     //searches specifically for the orderid "-KlSF5GydNxjegYK--R2"
                                reference1.orderByChild("orderid").equalTo(viewHolder.itemTitle.getText().toString().replace("Order ID: ", "")).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        DatabaseReference hopperRef = reference1.child(viewHolder.itemTitle.getText().toString().replace("Order ID: ", ""));     //uses the itemTitle, which is set to be the orderid, in order to get the order id on click of a specific card
                                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                                        hopperUpdates.put("result", "sending");                                           //appends the key "result" a value of "accepted". This can be changed to suit
                                        hopperRef.updateChildren(hopperUpdates);                                           //updates the child, without destroying, or overwriting all data
                                        viewHolder.btn_sending.setVisibility(View.INVISIBLE);
                                        //viewHolder.btn_complete.setVisibility(View.VISIBLE);
                                        viewHolder.btn_decline.setVisibility(View.VISIBLE);
                                        viewHolder.btn_accept.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        /*viewHolder.btn_complete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ///////////////////////////////////////////////////////////////////////////////////////////////////
                                //This part of the code retrieved a specific part of the data from the firebase database
                                //It bypasses the wildcard requirement by filtering for a specific child value in the
                                //reference provided, which is requests in this case

                                //reference1.orderByChild("orderid").equalTo(pushId).addChildEventListener(new ChildEventListener() {     //searches specifically for the orderid "-KlSF5GydNxjegYK--R2"
                                reference1.orderByChild("orderid").equalTo(viewHolder.itemTitle.getText().toString().replace("Order ID: ", "")).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        DatabaseReference hopperRef = reference1.child(viewHolder.itemTitle.getText().toString().replace("Order ID: ", ""));     //uses the itemTitle, which is set to be the orderid, in order to get the order id on click of a specific card
                                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                                        hopperUpdates.put("result", "delivered");                                           //appends the key "result" a value of "accepted". This can be changed to suit
                                        hopperRef.updateChildren(hopperUpdates);                                           //updates the child, without destroying, or overwriting all data

                                        //btn_complete.setVisibility(View.INVISIBLE);
                                        //btn_accept.setVisibility(View.VISIBLE);
                                        //btn_decline.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                viewHolder.btn_decline.setVisibility(View.VISIBLE);
                                viewHolder.btn_accept.setVisibility(View.VISIBLE);
                                viewHolder.btn_complete.setVisibility(View.INVISIBLE);
                            }
                        }); */



                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent editCheckoutintent = new Intent(VendorHub.this, Vendor_Checkout.class);
                                editCheckoutintent.putExtra("ID", viewHolder.itemTitle.getText().toString().replace("Order ID: ", ""));
                                if(viewHolder.itemTitle.getText().toString() == null){
                                    Log.d("Order ID", "Hello");
                                } else {
                                    Log.d("Order ID", viewHolder.itemTitle.getText().toString());
                                }

                                startActivity(editCheckoutintent);
                            }
                        });
                    }
                };

                mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int restaurantCount = mFirebaseAdapter.getItemCount();
                        int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                        if(lastVisiblePosition == -1 || (positionStart >= (restaurantCount -1) && lastVisiblePosition == (positionStart -1))){
                            mRestaurantRecyclerView.scrollToPosition(positionStart);
                        }
                    }
                });

                mRestaurantRecyclerView.setLayoutManager(mLinearLayoutManager);
                mRestaurantRecyclerView.setAdapter(mFirebaseAdapter);
            }
        };

        firebaseTask(myCallback);

        userEmail = (TextView) header.findViewById(R.id.emailTxt);
        userEmail.setText(user.getEmail());

        small_description = (TextView) findViewById(R.id.small_description_location);

        //FirebaseApp.initializeApp(this);
        //FirebaseMessaging.getInstance().subscribeToTopic("Notifications");

        /*Location Functions */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            getLocation();
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        Location myLocation = locationManager.getLastKnownLocation(provider);
        if(myLocation == null){
            getLocation();
        } else {
            lat = myLocation.getLatitude();
            lng = myLocation.getLongitude();
        }

        new GetAddress().execute(String.format("%.4f,%.4f",lat,lng));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_hub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            //Handle the camera action
            firebaseAuth.signOut();
            Snackbar.make(findViewById(android.R.id.content), "User Signed Out",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Intent intent = new Intent(this, Sign_In.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, uploadImage.class);
            startActivity(intent);

        } /* else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        } */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(provider, 400, 1, this);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        final Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("ERROR", "Location is null");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation();
                break;

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class GetAddress extends AsyncTask<String,Void,String> {

        ProgressDialog dialog = new ProgressDialog(VendorHub.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                double lat = Double.parseDouble(strings[0].split(",")[0]);
                double lng = Double.parseDouble(strings[0].split(",")[1]);
                String response;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&sensor=false",lat,lng);
                response = http.GetHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                String address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                small_description.setText(address);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
    }
}