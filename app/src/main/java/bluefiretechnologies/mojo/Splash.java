package bluefiretechnologies.mojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bluefiretechnologies.mojo.permissions.permission_location;

/**
 * Created by Reza Rajan on 2017-05-13.
 */

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Thread to services while app is starting
        Thread loadServices = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(500);  //Delay of 5 seconds
                } catch (Exception e) {

                } finally {

                    //Intent intent = new Intent(Splash.this, displayLocation.class);
                    Intent intent = new Intent(Splash.this, permission_location.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        loadServices.start();
    }
}
