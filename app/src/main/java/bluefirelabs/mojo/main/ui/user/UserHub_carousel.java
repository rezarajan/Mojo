package bluefirelabs.mojo.main.ui.user;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.background_tasks.MyFirebaseInstanceIDService;
import bluefirelabs.mojo.fragments.detailActivity;
import bluefirelabs.mojo.fragments.restaurantCards;
import bluefirelabs.mojo.handlers.adapters.FirebaseViewPagerAdapter;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import bluefirelabs.mojo.handlers.online.HttpDataHandler;
import bluefirelabs.mojo.main.transition.DetailActivity;
import bluefirelabs.mojo.handlers.online.SharedPrefManager;
import cdflynn.android.library.turn.TurnLayoutManager;

public class UserHub_carousel extends AppCompatActivity
        implements android.location.LocationListener {


    LocationManager locationManager;
    String provider;
    final int MY_PERMISSION_REQUEST_CODE = 7171;
    double lat, lng;

    FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver;

    public static final String RESTAURANT = "listing";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Food_List, FirebaseViewPagerAdapter.RecyclerViewHolder> mFirebaseAdapter;

    int cardPosition = 0;

    private RecyclerView mRestaurantRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    CarouselLayoutManager layoutManager;

    TurnLayoutManager turnLayoutManager;

    FragmentManager fm;

    private ImageView checkout_icon, order_history;

    private ScrollView scrollView;
    private View checkout_icon_dummy, order_history_dummy;


    public static final String EXTRA_RESTAURANT_LOGO = "restaurantLogo";
    public static final String EXTRA_RESTAURANT_NAME = "restaurantName";
    public static final String EXTRA_RESTAURANT_COLOR = "restaurantColor";
    public static final String EXTRA_TRANSITION_NAME = "transitionName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        }


        scrollView = (ScrollView) findViewById(R.id.scrollView);

        checkout_icon = (ImageView) findViewById(R.id.checkout_icon);
        order_history = (ImageView) findViewById(R.id.order_history);

        checkout_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(DetailActivity.this, Checkout.class);            //Goes to checkout
                Intent intent = new Intent(getApplicationContext(), bluefirelabs.mojo.main.ui.checkout.Checkout.class);            //Goes to checkout
                startActivity(intent);
            }
        });

        order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(DetailActivity.this, Checkout.class);            //Goes to checkout
                Intent intent = new Intent(getApplicationContext(), order_tracking.class);            //Goes to checkout
                startActivity(intent);
            }
        });


/*        // in Activity Context
        ImageView icon = new ImageView(this); // Create an icon
        Drawable iconDrawable = getResources().getDrawable(R.drawable.mojo_monkey);
        icon.setImageDrawable(iconDrawable);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setPosition(8)     //top left
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView orderHistory_button = new ImageView(this);
        Drawable orderHistoryDrawable = getResources().getDrawable(R.drawable.ic_history_48px);
        orderHistory_button.setImageDrawable(orderHistoryDrawable);
        SubActionButton button1 = itemBuilder.setContentView(orderHistory_button).build();

        // repeat many times:
        ImageView qrButton = new ImageView(this);
        Drawable qrDrawable = getResources().getDrawable(R.drawable.ic_menu_send);
        qrButton.setImageDrawable(qrDrawable);
        SubActionButton button2 = itemBuilder.setContentView(qrButton).build();


        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .setStartAngle(0)
                .setEndAngle(90)
                .setRadius(216)
                // ...
                .attachTo(actionButton)
                .build();*/


        fm = getFragmentManager();

        restaurantCards restaurantCards = new restaurantCards();
        detailActivity detailActivity = new detailActivity();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment1, restaurantCards, "restaurantCards");
        ft.commit();

        locationTasks();






    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);


        detailActivity detailActivity = (bluefirelabs.mojo.fragments.detailActivity) fm.findFragmentByTag("detailActivity");
        //restaurantCards restaurantCards = (bluefirelabs.mojo.fragments.restaurantCards) fm.findFragmentByTag("restaurantCards");
        FragmentTransaction ft = fm.beginTransaction();
        //Log.d("Position back", String.valueOf(cardPosition));
        if(detailActivity!=null){
            //ft.remove(detailActivity);
            //ft.add(R.id.fragment2, restaurantCards, "restaurantCards");
            ft.detach(detailActivity);      //removes the detailActivity from before the checkout
            ft.attach(detailActivity);      //adds a new detailActivity with the refreshed checkboxes
            ft.commit();

/*            if(restaurantCards != null){
                ft.setCustomAnimations(R.animator.slide_out_down, R.animator.slide_in_up);
                ft.detach(detailActivity);
                ft.attach(restaurantCards);
                ft.commit();
                locationTasks();
            }*/

        }

        /*scrollView.setVisibility(View.GONE);*/


    }

    @Override
    public void onBackPressed() {

        //restaurantCards restaurantCards = new restaurantCards();
        detailActivity detailActivity = (bluefirelabs.mojo.fragments.detailActivity) fm.findFragmentByTag("detailActivity");
        restaurantCards restaurantCards = (bluefirelabs.mojo.fragments.restaurantCards) fm.findFragmentByTag("restaurantCards");
        FragmentTransaction ft = fm.beginTransaction();
        Log.d("Position back", String.valueOf(cardPosition));
        if(detailActivity!=null && restaurantCards != null){
            //ft.remove(detailActivity);
            //ft.add(R.id.fragment2, restaurantCards, "restaurantCards");
                ft.setCustomAnimations(R.animator.slide_out_down, R.animator.slide_in_up);
                ft.remove(detailActivity);      //Using remove here so that in the next case, when the user wants to exit the app detail activity will show null
                                                //If detach was used, then detailActivity will still remain in the Fragment Manager, which results in an error
                ft.attach(restaurantCards);
                ft.commit();
                locationTasks();

        }
        else if(detailActivity==null && restaurantCards != null){
                finish();

        }

        scrollView.setVisibility(View.GONE);
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

        ProgressDialog dialog = new ProgressDialog(UserHub_carousel.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();*/
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
                //small_description.setText(address);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
    }

    public void locationTasks(){
        ImageView checkout_icon = (ImageView) findViewById(R.id.checkout_icon);
        ImageView order_history = (ImageView) findViewById(R.id.order_history);

        //Setting the icons to the secondary color (accent) using the Material Design Palette
        checkout_icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));
        order_history.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        //registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIDService.TOKEN_BROADCAST));

        if(SharedPrefManager.getInstance(this).getToken() != null){
            Log.d("FCM Token: ", SharedPrefManager.getInstance(this).getToken());
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


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

        DatabaseReference ref_users = FirebaseDatabase.getInstance().getReference("geofire").child("users");
        final GeoFire geoFire_users = new GeoFire(ref_users);

        DatabaseReference ref_venues = FirebaseDatabase.getInstance().getReference("geofire").child("venues");
        final GeoFire geoFire_venues = new GeoFire(ref_venues);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String uid = "";
        if(firebaseAuth.getCurrentUser() != null){
            uid = firebaseAuth.getCurrentUser().getUid();

            geoFire_users.setLocation(uid, new GeoLocation(lat, lng), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: " + error);
                    } else {
                        System.out.println("Location saved on server successfully!");
                    }
                }
            });

            //Querying for nearby venues
            GeoQuery geoQuery = geoFire_venues.queryAtLocation(new GeoLocation(lat, lng), 5);

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                    populateView(key);
                }

                @Override
                public void onKeyExited(String key) {
                    System.out.println(String.format("Key %s is no longer in the search area", key));
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                }

                @Override
                public void onGeoQueryReady() {
                    System.out.println("All initial data has been loaded and events have been fired!");
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    System.err.println("There was an error with this query: " + error);
                }
            });
        }

    }

    public void populateView(String venue){
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        turnLayoutManager = new TurnLayoutManager(UserHub_carousel.this,              // provide a context
                TurnLayoutManager.Gravity.END,        // from which direction should the list items orbit?
                TurnLayoutManager.Orientation.HORIZONTAL, // Is this a vertical or horizontal scroll?
                4600,               // The radius of the item rotation
                72,                 // Extra offset distance
                true);        // should list items angle towards the center? true/false.



        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("UID", user.getUid());


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Food_List, FirebaseViewPagerAdapter.RecyclerViewHolder>(
                Food_List.class,
                R.layout.infocard_front,
                FirebaseViewPagerAdapter.RecyclerViewHolder.class,
                mFirebaseDatabaseReference.child(RESTAURANT).child(venue)
        ) {
            @Override
            public void onBindViewHolder(FirebaseViewPagerAdapter.RecyclerViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

            }


            @Override
            protected void populateViewHolder(final FirebaseViewPagerAdapter.RecyclerViewHolder viewHolder, final Food_List model, final int position) {

                viewHolder.restaurantName.setText(model.getRestaurant());
                //viewHolder.openIndicatorText.setText(model.getOpen());
                viewHolder.restaurantDescription.setText(model.getDescription());
                //viewHolder.averageTime.setText(model.getAverageTime());


                Picasso.with(getApplicationContext()).load(model.getIcon()).into(viewHolder.restaurantLogo);


                if(model.getColor() != null){
                    viewHolder.background_image_view.setBackgroundColor(Color.parseColor(model.getColor()));
                }

                if(model.getOpen() != null){
                    String openStatus = model.getOpen();
                    viewHolder.openIndicatorText.setText(openStatus);

                    if(openStatus.equals("Open")){
                        viewHolder.openindicatorIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorOpen));
                    } else if(openStatus.equals("Closed")){
                        viewHolder.openindicatorIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorClosed));
                    } else if(openStatus.equals("Closing")){
                        viewHolder.openindicatorIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorClosing));
                    }
                }


                viewHolder.descriptor1.setImageResource(R.drawable.ic_local_cafe_48px);
                viewHolder.descriptor2.setImageResource(R.drawable.ic_local_pizza_48px);
                viewHolder.descriptor3.setImageResource(R.drawable.ic_local_drink_48px);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Setting up the transitions for the logo, using the restaurant's name as the unique identifier
                        //to avoid image clashing with other items
/*                    ViewCompat.setTransitionName(viewHolder.restaurantLogo, viewHolder.restaurantName.toString());
                    intent.putExtra(EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(viewHolder.restaurantLogo));*/



/*
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(UserHub_carousel.this,
                            new Pair(viewHolder.restaurantLogo, DetailActivity.EXTRA_RESTAURANT_LOGO)
                    );
*/


                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                        intent.putExtra(DetailActivity.EXTRA_RESTAURANT_LOGO, model.getIcon());
                        intent.putExtra(DetailActivity.EXTRA_RESTAURANT_COLOR, model.getColor());
                        intent.putExtra(DetailActivity.EXTRA_RESTAURANT_NAME, model.getRestaurant());


                    /*startActivity(intent, options.toBundle());*/
                        //startActivity(intent);



                        Bundle args = new Bundle();
                        args.putString(detailActivity.EXTRA_RESTAURANT_LOGO, model.getIcon());
                        args.putString(detailActivity.EXTRA_RESTAURANT_NAME, model.getRestaurant());
                        args.putString(detailActivity.EXTRA_RESTAURANT_COLOR, model.getColor());
                        args.putString(detailActivity.EXTRA_RESTAURANT_NAME, model.getRestaurant());

                        cardPosition = position;
                        Log.d("Position", String.valueOf(cardPosition));


                        scrollView.setVisibility(View.VISIBLE);


                        FragmentTransaction ft = fm.beginTransaction();
                        //ft.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);


                        detailActivity detailActivity = new detailActivity();
                        //ft.replace(R.id.fragment2, detailActivity);
                        //ft.addToBackStack(null);

                        detailActivity.setArguments(args);
                        restaurantCards restaurantCards = (bluefirelabs.mojo.fragments.restaurantCards) fm.findFragmentByTag("restaurantCards");
                        //ft.remove(restaurantCards);
                        ft.detach(restaurantCards);

                        ft.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down);


                        ft.add(R.id.fragment2, detailActivity, "detailActivity");
                        ft.commit();

                        checkout_icon_dummy = (View) findViewById(R.id.checkout_icon_dummy);
                        order_history_dummy = (View) findViewById(R.id.order_history_dummy);


                        checkout_icon_dummy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Intent intent = new Intent(DetailActivity.this, Checkout.class);            //Goes to checkout
                                Intent intent = new Intent(getApplicationContext(), bluefirelabs.mojo.main.ui.checkout.Checkout.class);            //Goes to checkout
                                startActivity(intent);
                            }
                        });

                        order_history_dummy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Intent intent = new Intent(DetailActivity.this, Checkout.class);            //Goes to checkout
                                Intent intent = new Intent(getApplicationContext(), order_tracking.class);            //Goes to checkout
                                startActivity(intent);
                            }
                        });


                    }
                });

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int restaurantCount = mFirebaseAdapter.getItemCount();
                mRestaurantRecyclerView.scrollToPosition(cardPosition);

            }
        });

        mRestaurantRecyclerView.setAdapter(mFirebaseAdapter);
        mRestaurantRecyclerView.setLayoutManager(turnLayoutManager);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRestaurantRecyclerView);







    }
}