package bluefirelabs.mojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import bluefirelabs.mojo.handlers.RecyclerAdapter_Menu;

public class Restaurant_Menu extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private String restaurant, iconRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Menu");

        Intent receivedIntent = getIntent();
        restaurant = receivedIntent.getStringExtra("Restaurant");
        iconRef = receivedIntent.getStringExtra("Icon");

        ImageView restaurantIcon = (ImageView) findViewById(R.id.restaurant_icon);
        Picasso.with(Restaurant_Menu.this).load(iconRef).into(restaurantIcon);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(restaurant);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setNestedScrollingEnabled(true);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter_Menu();
        recyclerView.setAdapter(adapter);

    }

}