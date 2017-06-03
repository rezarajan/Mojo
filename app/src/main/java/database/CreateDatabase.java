package database;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class CreateDatabase extends AppCompatActivity{
    DatabaseHelper myDb;
    EditText restaurant, item, cost;
    Button addData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        myDb = new DatabaseHelper(this); //calls constructor from the database helper class

        restaurant = (EditText)findViewById(R.id.editText_restaurant);
        item = (EditText)findViewById(R.id.editText_item);
        cost = (EditText)findViewById(R.id.editText_cost);
        addData = (Button)findViewById(R.id.buttonAdd);

        AddData();
    }

    public void AddData(){
        addData.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                boolean isInserted = myDb.insertData(restaurant.getText().toString(),
                        item.getText().toString(),
                        cost.getText().toString(),
                        "0");

                if(isInserted == true){
                    Toast.makeText(database.CreateDatabase.this, "Data Inserted", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(database.CreateDatabase.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
