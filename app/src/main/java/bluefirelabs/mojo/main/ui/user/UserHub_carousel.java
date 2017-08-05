package bluefirelabs.mojo.main.ui.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.background_tasks.MyFirebaseInstanceIDService;
import bluefirelabs.mojo.fragments.restaurantlist_fragment;
import bluefirelabs.mojo.handlers.adapters.FirebaseViewPagerAdapter;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import bluefirelabs.mojo.handlers.online.HttpDataHandler;
import bluefirelabs.mojo.main.transition.CommonFragment;
import bluefirelabs.mojo.main.transition.DetailActivity;
import bluefirelabs.mojo.main.ui.payments.Payments;
import bluefirelabs.mojo.handlers.online.SharedPrefManager;
import bluefirelabs.mojo.handlers.online.uploadImage;
import bluefirelabs.mojo.main.login.Sign_In;
import bluefirelabs.mojo.menu.OrderHistory;

public class UserHub_carousel extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , bluefirelabs.mojo.fragments.currentinfo_fragment.currentinfoListener
        , restaurantlist_fragment.restaurantlistListener
        , android.location.LocationListener {


    LocationManager locationManager;
    String provider;
    final int MY_PERMISSION_REQUEST_CODE = 7171;
    double lat, lng;
    TextView small_description;
    TextView userEmail;
    FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver;

    public static final String RESTAURANT = "listing";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Food_List, FirebaseViewPagerAdapter.RecyclerViewHolder> mFirebaseAdapter;

    int defaultColor = 0x000000;
    int mutedColor = -1;

    private RecyclerView mRestaurantRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


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

/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   */

            //Sets the status bar and navBar to no background, just icons
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        }

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

        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIDService.TOKEN_BROADCAST));

        if(SharedPrefManager.getInstance(this).getToken() != null){
            Log.d("FCM Token: ", SharedPrefManager.getInstance(this).getToken());
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        //small_description = (TextView) findViewById(R.id.small_description_location);

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

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(this, OrderHistory.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, Payments.class);
            startActivity(intent);
        } else if (id == R.id.nav_gifts) {
            Intent intent = new Intent(this, Gifts.class);
            startActivity(intent);
        }

        /* else if (id == R.id.nav_slideshow) {

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

        ProgressDialog dialog = new ProgressDialog(UserHub_carousel.this);

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
                //small_description.setText(address);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
    }

    public void populateView(String venue){
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //mLinearLayoutManager = new CenterZoomLayoutManager(this);
         //mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setStackFromEnd(true);
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());


        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("UID", user.getUid());

        final CommonFragment fragment = new CommonFragment();

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
                Log.d("Description: ", model.getDescription());
                //viewHolder.itemDescription.setText(model.getDescription());
                //viewHolder.itemTitle.setText(model.getRestaurant());
                //viewHolder.itemIcon.setImageResource(R.drawable.restaurant_icon);

                viewHolder.restaurantName.setText(model.getRestaurant());
                //viewHolder.openIndicatorText.setText(model.getOpen());
                viewHolder.restaurantDescription.setText(model.getDescription());
                //viewHolder.averageTime.setText(model.getAverageTime());


                Picasso.with(getApplicationContext()).load(model.getIcon()).into(viewHolder.restaurantLogo);
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.drinks);
/*
                if(viewHolder.restaurantLogo.getDrawable() != null) {
                    bitmapDrawable = (BitmapDrawable) viewHolder.restaurantLogo.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Palette palette;
                    palette = Palette.from(bitmap).generate();
                    mutedColor = palette.getVibrantColor(defaultColor);
                    viewHolder.itemView.setBackgroundColor(mutedColor);
                }
*/



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




                               /* First Time colour palette set */
              /*  Picasso.with(getApplicationContext())
                        .load(model.getIcon())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                //Log.d("Setting Colour for", fragments.get(position%10).dataReturn());
                                Log.d("Changing", "activated 1");
                                Palette palette;
                                palette = Palette.from(bitmap).generate();
                                //mutedColor = palette.getDarkVibrantColor(defaultColor);
                                mutedColor = palette.getVibrantColor(defaultColor);
                                Log.d("mutedColor", String.valueOf(mutedColor));
                                //mutedColor = palette.getMutedColor(defaultColor);
                                //accentColorMuted = palette.getLightVibrantColor(defaultColor);

                                viewHolder.background_image_view.setBackgroundColor(mutedColor);

*//*                                //First time and second time for the case when the ViewPager cycles to the
                                //third colour and sets the wrong initial colour
                                if (firstTime && secondTime) {
                                    viewPager.setBackgroundColor(mutedColor);
                                    checkout_icon.setColorFilter(accentColorMuted);
                                    order_history.setColorFilter(accentColorMuted);
                                    firstTime = false;
                                }

                                //after the first time this now sets the second colour
                                //which is the same as the first fragment (since we exclude the 0th)
                                if(!firstTime && secondTime){
                                    viewPager.setBackgroundColor(mutedColor);
                                    checkout_icon.setColorFilter(accentColorMuted);
                                    order_history.setColorFilter(accentColorMuted);
                                    secondTime = false;
                                }
                                Log.d("Changing", "activated 2");
                                //obj.colorFetched(position, vibrantColor, mutedColor);
                                //viewPager.setBackgroundColor(vibrantColor);*//*


                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }


                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                viewHolder.background_image_view.setBackgroundColor(mutedColor);

                            }
                        });*/
                 //This works, so the problem only exists with Picasso since it is asynchronous
                 //viewHolder.itemView.setBackgroundColor(Color.parseColor("#d86a0a"));


/*                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //notifyItemChanged(position);
                        notifyDataSetChanged();
                    }
                });*/




            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Setting up the transitions for the logo, using the restaurant's name as the unique identifier
                    //to avoid image clashing with other items
/*                    ViewCompat.setTransitionName(viewHolder.restaurantLogo, viewHolder.restaurantName.toString());


                    intent.putExtra(EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(viewHolder.restaurantLogo));*/



                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(UserHub_carousel.this,
                            new Pair(viewHolder.restaurantLogo, DetailActivity.EXTRA_RESTAURANT_LOGO)
                    );


                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_RESTAURANT_LOGO, model.getIcon());
                    intent.putExtra(DetailActivity.EXTRA_RESTAURANT_COLOR, model.getColor());
                    intent.putExtra(DetailActivity.EXTRA_RESTAURANT_NAME, model.getRestaurant());


                    startActivity(intent, options.toBundle());
                }
            });

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int restaurantCount = mFirebaseAdapter.getItemCount();
                //mRestaurantRecyclerView.scrollToPosition(restaurantCount);
/*                int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if(lastVisiblePosition == -1 || (positionStart >= (restaurantCount -1) && lastVisiblePosition == (positionStart -1))){
                    mRestaurantRecyclerView.scrollToPosition(positionStart);
                }*/

                Log.d("Restaurant Count", String.valueOf(restaurantCount));
                mRestaurantRecyclerView.scrollToPosition(restaurantCount/2);        //when the app launches the card is the middle card
                //mRestaurantRecyclerView.smoothScrollToPosition(restaurantCount/2);        //when the app launches the cards scroll to the middle card
            //mFirebaseAdapter.cleanup();

            }
        });


        //mRestaurantRecyclerView.addItemDecoration(new OverlapDecoration());

/*        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRestaurantRecyclerView);*/


        Log.d("Max Visible Items", String.valueOf(layoutManager.getMaxVisibleItems()));
        //mRestaurantRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRestaurantRecyclerView.setLayoutManager(layoutManager);
        mRestaurantRecyclerView.setAdapter(mFirebaseAdapter);
        mRestaurantRecyclerView.addOnScrollListener(new CenterScrollListener());
        mRestaurantRecyclerView.setNestedScrollingEnabled(false);

        //SnapHelper helper = new StartSnapHelper();







    }
}
