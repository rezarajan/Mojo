package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.RecyclerAdapter_Drinks;
import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class Drinks_Menu extends AppCompatActivity{
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatabaseHelper myDb;
    FloatingActionButton checkout_btn;
    Button imagebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_menu);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Menu");

        checkout_btn = (FloatingActionButton)findViewById(R.id.fabCheckout);
        myDb = new DatabaseHelper(this); //calls constructor from the database helper class

        imagebutton = (Button) findViewById(R.id.imageButton_add);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle("Drinks");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter_Drinks();
        recyclerView.setAdapter(adapter);

        ViewAll();
    }

    public void ViewAll(){
        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), Checkout.class);
                startActivity(intent);
              /* Cursor res = myDb.getAllData();
                if(res.getCount() == 0){
                    //no data in database, show message
                    showMessage("Error", "No data found");
                    return;
                } */

                /* StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("ID: " + res.getString(0) + "\n");
                    buffer.append("MENU: " + res.getString(1) + "\n");
                    buffer.append("ITEM: " + res.getString(2) + "\n");
                    buffer.append("COST: " + res.getString(3) + "\n\n");
                }

                //Show all data
                showMessage("Data", buffer.toString()); */

            }
        });
    }



    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
