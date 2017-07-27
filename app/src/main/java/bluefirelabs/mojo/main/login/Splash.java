package bluefirelabs.mojo.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Reza Rajan on 2017-05-13.
 */

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Thread to load services while app is starting
        Thread loadServices = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    //sleep(500);  //Delay of 5 seconds
                } catch (Exception e) {

                } finally {

                    //Start a new intent
                    //Intent intent = new Intent(Splash.this, Sign_In.class);
                    //Intent intent = new Intent(Splash.this, MainActivity.class);
                    Intent intent = new Intent(Splash.this, Sign_In.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        loadServices.start();
    }
}
