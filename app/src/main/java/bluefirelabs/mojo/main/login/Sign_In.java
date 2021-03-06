package bluefirelabs.mojo.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import bluefirelabs.mojo.R;

/**
 * Created by rezarajan on 22/05/2017.
 */

public class Sign_In extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(Sign_In.this, bluefirelabs.mojo.permissions.permission_location.class);
            startActivity(intent);
            finish();
        } else {

           /* TextView sign_in = (TextView) findViewById(R.id.sign_in);

            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Sign_In.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }); */

            TextView sign_up = (TextView) findViewById(R.id.sign_up);

            sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Sign_In.this, Login.class);
                    startActivity(intent);
                }
            });

            TextView sign_in = (TextView) findViewById(R.id.sign_in);

            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Sign_In.this, Login.class);
                    startActivity(intent);
                }
            });

/*            Button social_media = (Button) findViewById(R.id.social_media);

            social_media.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Sign_In.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            });*/
        }
    }
}


