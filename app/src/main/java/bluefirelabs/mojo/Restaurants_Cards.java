package bluefirelabs.mojo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import database.Restaurants_DatabaseAdapter;

public class Restaurants_Cards extends AppCompatActivity {

    Restaurants_DatabaseAdapter restaurantHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants__cards);

        restaurantHelper = new Restaurants_DatabaseAdapter(this);
    }
        //TODO: Create a for loop or something to add some dummy values
        public void addUser(View view){
        long id = restaurantHelper.insertData("Name", "Password");
            if(id<0){

            } else{

            }
    }
}
