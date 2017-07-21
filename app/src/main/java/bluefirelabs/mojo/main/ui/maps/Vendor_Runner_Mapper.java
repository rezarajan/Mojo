package bluefirelabs.mojo.main.ui.maps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.background_tasks.MapHttpConnection;
import bluefirelabs.mojo.background_tasks.PathJSONParser_duration;

import static bluefirelabs.mojo.R.id.map;

/**
 * Created by Reza Rajan on 2017-05-16.
 */

public class Vendor_Runner_Mapper extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private String url;
    TextView txtView;
    LocationManager locationManager;
    String provider;
    final int MY_PERMISSION_REQUEST_CODE = 7171;
    double myLat, myLng, runnerLat, runnerLng;
    LatLng myLocation, runnerLocation;
    private ReadTask readTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        /*
        Updating last known location
         */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            getLocation();
            Log.d("Getting Location", "Now");
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        Location myLocation = locationManager.getLastKnownLocation(provider);
        if(myLocation == null){
            getLocation();
        } else {
            myLat = myLocation.getLatitude();
            myLng = myLocation.getLongitude();
            Log.d("Location", String.valueOf(myLat) + "," + String.valueOf(myLng));

            DatabaseReference ref_users = FirebaseDatabase.getInstance().getReference("geofire").child("users");
            final GeoFire geoFire_users = new GeoFire(ref_users);

            DatabaseReference ref_runners = FirebaseDatabase.getInstance().getReference("geofire").child("runners");
            final GeoFire geoFire_runners = new GeoFire(ref_runners);

            geoFire_runners.setLocation("Runner", new GeoLocation(myLat,myLng), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: " + error);
                    } else {
                        System.out.println("Location saved on server successfully!");
                    }
                }
            });

            //TODO: Replace user with the user's UID
            geoFire_users.getLocation("User", new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    if (location != null) {
                        runnerLat = location.latitude;
                        runnerLng = location.longitude;
                        System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                    } else {
                        System.out.println(String.format("There is no location for key %s in GeoFire", key));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("There was an error getting the GeoFire location: " + databaseError);
                }
            });



        }
        txtView = (TextView) findViewById(R.id.locationAddress);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference ref_users = FirebaseDatabase.getInstance().getReference("geofire").child("users");
        final GeoFire geoFire_users = new GeoFire(ref_users);

        DatabaseReference ref_runners = FirebaseDatabase.getInstance().getReference("geofire").child("runners");
        final GeoFire geoFire_runners = new GeoFire(ref_runners);

        geoFire_runners.setLocation("Runner", new GeoLocation(myLat,myLng), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });

        //Note that the drawing has to be done within the OnLocationResult itself, or else for some reason it resets the value of runnerLat and runnerLng

        //TODO: Replace user with the user's UID
        geoFire_users.getLocation("User", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    runnerLat = location.latitude;
                    runnerLng = location.longitude;
                    // Add a marker in Sydney, Australia, and move the camera.

                    myLocation = new LatLng(myLat, myLng);
                    //runnerLocation = new LatLng(43.473463, -80.537056);
                    runnerLocation = new LatLng(runnerLat, runnerLng);
                    mMap.addMarker(new MarkerOptions().position(runnerLocation).title("Customer"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));      //Zooms the map to your location, with zoom parameter = 17

                    url = getMapsApiDirectionsUrl(myLocation, runnerLocation);
                    readTask = new ReadTask();
                    readTask.execute(url);
                    System.out.println(String.format("The location for maps for key %s is [%f,%f]", key, location.latitude, location.longitude));
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });


    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;

    }

    private class ReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);
                Log.d("Data", data.toString());


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser_duration parser = new PathJSONParser_duration();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                Log.d("Line Options", lineOptions.toString());
                lineOptions.width(2);
                lineOptions.color(Color.BLUE);
            }

            txtView.setText("Distance:" + distance + ", Duration:" + duration);

            // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation();
                break;

        }
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
    public void onLocationChanged(Location location) {
        getLocation();
        myLat = location.getLatitude();
        myLng = location.getLongitude();

        DatabaseReference ref_users = FirebaseDatabase.getInstance().getReference("geofire").child("users");
        final GeoFire geoFire_users = new GeoFire(ref_users);

        DatabaseReference ref_runners = FirebaseDatabase.getInstance().getReference("geofire").child("runners");
        final GeoFire geoFire_runners = new GeoFire(ref_runners);

        geoFire_runners.setLocation("Runner", new GeoLocation(myLat,myLng), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                }
            }
        });

        geoFire_users.getLocation("User", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    runnerLat = location.latitude;
                    runnerLng = location.longitude;
                    mMap.addMarker(new MarkerOptions().position(runnerLocation).title("Customer"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    url = getMapsApiDirectionsUrl(myLocation, runnerLocation);
                    //readTask.cancel(true);
                    //mMap.clear();
                    //new ReadTask().execute(url);
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
    }
}