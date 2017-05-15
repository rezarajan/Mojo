package bluefirelabs.mojo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import database.Restaurants_Helper;

public class Restaurants_Cards extends AppCompatActivity {

    Restaurants_Helper restaurantHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants__cards);

        restaurantHelper = new Restaurants_Helper(this);

        SQLiteDatabase sqLiteDatabase = restaurantHelper.getWritableDatabase();
    }

}
