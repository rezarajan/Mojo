package bluefirelabs.mojo.main.ui.user;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.background_tasks.MyFirebaseInstanceIDService;
import bluefirelabs.mojo.handlers.adapters.FirebaseRecyclerAdapterRestaurants;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import bluefirelabs.mojo.handlers.online.HttpDataHandler;
import bluefirelabs.mojo.handlers.online.SharedPrefManager;
import bluefirelabs.mojo.main.transition.CommonFragment;
import bluefirelabs.mojo.main.transition.CustPagerTransformer;


/**
 * Created by xmuSistone on 2016/9/18.
 */
public class UserHub_test extends FragmentActivity implements android.location.LocationListener{


    ArgbEvaluator argbEvaluator;
    int defaultColor = 0x000000;
    int vibrantColor = -1, mutedColor = -1, accentColor = -1, accentColorMuted = -1;

    boolean firstTime = true, secondTime = true;


    private BroadcastReceiver broadcastReceiver;
    LocationManager locationManager;
    final int MY_PERMISSION_REQUEST_CODE = 7171;
    double lat, lng;
    String provider;



    FirebaseAuth firebaseAuth;
    public static final String RESTAURANT = "listing";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Food_List, FirebaseRecyclerAdapterRestaurants.RecyclerViewHolder> mFirebaseAdapter;

    private TextView indicatorTv, restaurantName_indicator, location_indicator;
    private ImageView checkout_icon, order_history;
    private View positionView;
    private ViewPager viewPager;
    private List<CommonFragment> fragments = new ArrayList<>();
    private TabLayout tablayout;

    DecimalFormat df = new DecimalFormat("#.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        restaurantName_indicator = (TextView) findViewById(R.id.restaurantName_indicator);
        location_indicator = (TextView) findViewById(R.id.location_indicator);

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   */

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        argbEvaluator = new ArgbEvaluator();
        positionView = findViewById(R.id.position_view);
        dealStatusBar();

        fillViewPager();
    }


    public void firebaseTask(final MyCallback myCallback) {

        myCallback.callbackCall("listing");
    }

    public void setRestaurantName(final MyCallback_2 myCallback_2, TextView textView, String restaurantName) {

        myCallback_2.callbackCall(textView, restaurantName);
    }

    private void fillViewPager() {
        //indicatorTv = (TextView) findViewById(R.id.indicator_tv);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tablayout = (TabLayout) findViewById(R.id.tabDots);


        viewPager.setPageTransformer(false, new CustPagerTransformer(this));
        tablayout.setupWithViewPager(viewPager, true);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        final MyCallback_2 myCallback_2 = new MyCallback_2() {
            @Override
            public void callbackCall(TextView textView, String restaurantName) {
                textView.setText(restaurantName);

            }
        };



        //---------------------------------------------------------------------------------//


        final MyCallback myCallback = new MyCallback() {
            @Override
            public void callbackCall(final String restaurant) {







                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                    }
                };

                registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIDService.TOKEN_BROADCAST));

                if(SharedPrefManager.getInstance(getApplicationContext()).getToken() != null){
                    Log.d("FCM Token: ", SharedPrefManager.getInstance(getApplicationContext()).getToken());
                }

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
       /* user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.d("Token: ", idToken);
                            // Send token to your backend via HTTPS
                            // ...
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                }); */

                //userEmail = (TextView) header.findViewById(R.id.emailTxt);
                //userEmail.setText(user.getEmail());


                //small_description = (TextView) findViewById(R.id.small_description_location);

                //FirebaseApp.initializeApp(this);
                //FirebaseMessaging.getInstance().subscribeToTopic("Notifications");

        /*Location Functions */
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(UserHub_test.this, new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    }, MY_PERMISSION_REQUEST_CODE);

                } else {
                    getLocation();
                }
                locationManager.requestLocationUpdates(provider, 10000, 100, UserHub_test.this);
                Location myLocation = locationManager.getLastKnownLocation(provider);
                if(myLocation == null){
                    getLocation();
                } else {
                    lat = myLocation.getLatitude();
                    lng = myLocation.getLongitude();
                }

                new UserHub_test.GetAddress().execute(String.format("%.4f,%.4f",lat,lng));

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

                            //setRestaurantName(myCallback_2, location_indicator, "Welcome to \n"+ key.toUpperCase());
                            //setRestaurantName(myCallback_2, location_indicator, key.toUpperCase());

                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant).child(key);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    final Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();

                                    //Log.d("Size", String.valueOf(hopperValues.size()));

                                    if (hopperValues != null) {

                                        Log.d("Size", String.valueOf(hopperValues.size()));
                                        Log.d("Keys", String.valueOf(hopperValues.keySet()));

                                        // 2. viewPager添加adapter
                                        for (int i = 0; i < hopperValues.size(); i++) {       //This is the list of menu items
                                            // 预先准备10个fragment
                                            fragments.add(new CommonFragment());
                                        }


                                        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                                            @Override
                                            public Fragment getItem(int position) {
                                                CommonFragment fragment = fragments.get(position % 10);
                                                Log.d("Position", String.valueOf(position));


                                                fragment.bindAllData(dataSnapshot.child("id" + String.valueOf(position)).child("icon").getValue().toString(),
                                                        dataSnapshot.child("id" + String.valueOf(position)).child("description").getValue().toString(),
                                                        dataSnapshot.child("id" + String.valueOf(position)).child("restaurant").getValue().toString(),
                                                        Float.parseFloat(dataSnapshot.child("id" + String.valueOf(position)).child("rating").getValue().toString())
                                                );

                                                //backgroundChanger(myCallback_2, dataSnapshot.child("id" + String.valueOf(position)).child("icon").getValue().toString());


                                    /* First Time colour palette set */
                                                Picasso.with(getApplicationContext())
                                                        .load(fragments.get((position)%10).dataReturn())
                                                        .into(new Target() {
                                                            @Override
                                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                //Log.d("Setting Colour for", fragments.get(position%10).dataReturn());
                                                                Log.d("Changing", "activated 1");

                                                                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                                                        /*Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                                            @Override
                                                            public void onGenerated(Palette palette) {
                                                                mutedColor = palette.getVibrantColor(defaultColor);
                                                                //mutedColor = palette.getMutedColor(defaultColor);
                                                                Log.d("Changing", "activated 2");
                                                                //obj.colorFetched(position, vibrantColor, mutedColor);
                                                                //viewPager.setBackgroundColor(vibrantColor);
                                                            }
                                                        }); */
                                                                Palette palette;
                                                                palette = Palette.from(bitmap).generate();
                                                                //mutedColor = palette.getDarkVibrantColor(defaultColor);
                                                                mutedColor = palette.getVibrantColor(defaultColor);
                                                                //mutedColor = palette.getMutedColor(defaultColor);
                                                                accentColorMuted = palette.getLightVibrantColor(defaultColor);

                                                                //First time and second time for the case when the ViewPager cycles to the
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
                                                                //viewPager.setBackgroundColor(vibrantColor);


                                                            }

                                                            @Override
                                                            public void onBitmapFailed(Drawable errorDrawable) {

                                                            }


                                                            @Override
                                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                            }
                                                        });

                                                return fragment;
                                            }

                                            @Override
                                            public int getCount() {
                                                return hopperValues.size();
                                            }       //This is the number of restaurants
                                        });


                                        // 3. viewPager滑动时，调整指示器
                                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                            @Override
                                            public void onPageScrolled(final int position, final float positionOffset, int positionOffsetPixels) {

                                                //backgroundChanger(myCallback_2, fragments.get(position%10).dataReturn());
                                                //Log.d("URL", fragments.get(position%10).dataReturn());

                                                //sets the restaurant name
                                                setRestaurantName(myCallback_2, restaurantName_indicator, dataSnapshot.child("id" + String.valueOf(position)).child("restaurant").getValue().toString().toUpperCase());

                                                if (position < hopperValues.size() - 1) {
                                                    Picasso.with(getApplicationContext())
                                                            .load(fragments.get((position)%10).dataReturn())
                                                            .into(new Target() {
                                                                @Override
                                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                    //Log.d("Setting Colour for", fragments.get(position%10).dataReturn());
                                                                    Log.d("Changing", "activated 1");

                                                                    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                                                        /*Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                                            @Override
                                                            public void onGenerated(Palette palette) {
                                                                mutedColor = palette.getVibrantColor(defaultColor);
                                                                //mutedColor = palette.getMutedColor(defaultColor);
                                                                Log.d("Changing", "activated 2");
                                                                //obj.colorFetched(position, vibrantColor, mutedColor);
                                                                //viewPager.setBackgroundColor(vibrantColor);
                                                            }
                                                        }); */
                                                                    Palette palette;
                                                                    palette = Palette.from(bitmap).generate();
                                                                   // mutedColor = palette.getDarkVibrantColor(defaultColor);
                                                                    mutedColor = palette.getVibrantColor(defaultColor);
                                                                    accentColorMuted = palette.getLightVibrantColor(defaultColor);

                                                                    //mutedColor = palette.getMutedColor(defaultColor);
                                                                    Log.d("Changing", "activated 2");
                                                                    //obj.colorFetched(position, vibrantColor, mutedColor);
                                                                    //viewPager.setBackgroundColor(vibrantColor);


                                                                }

                                                                @Override
                                                                public void onBitmapFailed(Drawable errorDrawable) {

                                                                }


                                                                @Override
                                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                }
                                                            });

                                                    int position_increment = position + 1;

                                                    Picasso.with(getApplicationContext())
                                                            .load(fragments.get(position_increment%10).dataReturn())
                                                            .into(new Target() {
                                                                @Override
                                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                    //Log.d("Setting Colour for", fragments.get(position%10).dataReturn());
                                                                    Log.d("Changing", "activated 3");

                                                                    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                                                    /*Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                                        @Override
                                                        public void onGenerated(Palette palette) {
                                                            vibrantColor = palette.getVibrantColor(defaultColor);
                                                            Log.d("Changing", "activated 4");
                                                            //obj.colorFetched(position, vibrantColor, mutedColor);
                                                            //viewPager.setBackgroundColor(vibrantColor);
                                                        }
                                                    }); */
                                                                    Palette palette;
                                                                    palette = Palette.from(bitmap).generate();
                                                                    //vibrantColor = palette.getDarkVibrantColor(defaultColor);
                                                                    vibrantColor = palette.getVibrantColor(defaultColor);
                                                                    //mutedColor = palette.getMutedColor(defaultColor);
                                                                    accentColor = palette.getLightVibrantColor(defaultColor);

                                                                    Log.d("Changing", "activated 4");
                                                                    //obj.colorFetched(position, vibrantColor, mutedColor);
                                                                    //viewPager.setBackgroundColor(vibrantColor);


                                                                }

                                                                @Override
                                                                public void onBitmapFailed(Drawable errorDrawable) {

                                                                }


                                                                @Override
                                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                }
                                                            });



                                                    //animates the colour
                                                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset,
                                                            mutedColor, vibrantColor));

                                                    checkout_icon.setColorFilter((Integer) argbEvaluator.evaluate(positionOffset,
                                                            accentColorMuted, accentColor));

                                                    order_history.setColorFilter((Integer) argbEvaluator.evaluate(positionOffset,
                                                            accentColorMuted, accentColor));

                                                    Log.d("Icon Links", fragments.get(position%10).dataReturn());
                                                }




                                                //TODO: Add the bitmap colour url to firebase

                                     /*if (position < hopperValues.size() - 1) {                                               //animates the colour
                                        viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset,
                                                Color.BLACK, Color.BLUE));
                                    } */

                                    /*
                                    if(position == 1){
                                        viewPager.setBackgroundColor(Color.CYAN);
                                    } else {
                                        Picasso.with(getApplicationContext())
                                                .load(fragments.get(position%10).dataReturn())
                                                .into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                        setColors(bitmap);
                                                    }
                                                    @Override
                                                    public void onBitmapFailed(Drawable errorDrawable) {
                                                    }
                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                    }
                                                });
                                    } */


                                            }

                                            @Override
                                            public void onPageSelected(int position) {
                                                //updateIndicatorTv();
                                            }

                                            @Override
                                            public void onPageScrollStateChanged(int state) {

                                            }
                                        });

                                        //updateIndicatorTv();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
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
        };

        firebaseTask(myCallback);
    }


    private void updateIndicatorTv() {
        int totalNum = viewPager.getAdapter().getCount();
        int currentItem = viewPager.getCurrentItem() + 1;
        indicatorTv.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + totalNum));
    }


    private void dealStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = positionView.getLayoutParams();
            lp.height = statusBarHeight;
            positionView.setLayoutParams(lp);
        }
    }

    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(provider, 10000, 100, this);


        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        final Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("ERROR", "Location is null");
    }

    @Override
    public void onLocationChanged(Location location) {
        //viewPager.removeAllViews();     //removes the current venue restaurants when location updates
        //fillViewPager();                //refills the viewPager when you are in a venue
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

        ProgressDialog dialog = new ProgressDialog(UserHub_test.this);

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

                String address = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .get("formatted_address").toString();
                Log.d("Address", address);
                //small_description.setText(address);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
    }

}