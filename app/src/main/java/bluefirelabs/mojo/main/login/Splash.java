package bluefirelabs.mojo.main.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Reza Rajan on 2017-05-13.
 */

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //Thread to load services while app is starting
        Thread loadServices = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    //sleep(500);  //Delay of 5 seconds

                } catch (Exception e) {

                } finally {

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    if (firebaseAuth.getCurrentUser() != null) {
                        //Start a new intent
                        Intent intent = new Intent(getApplicationContext(), bluefirelabs.mojo.permissions.permission_location.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Intent intent = new Intent(Splash.this, Sign_In.class);
                        Intent intent = new Intent(Splash.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        loadServices.start();
    }
}
