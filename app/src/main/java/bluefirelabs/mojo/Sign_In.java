package bluefirelabs.mojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by rezarajan on 22/05/2017.
 */

public class Sign_In extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        TextView sign_in = (TextView) findViewById(R.id.sign_in);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_In.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}


