package bluefirelabs.mojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-05-27.
 */

public class editcheckout extends AppCompatActivity{

    private static final String TAG = "editCheckoutActivity";

    private Button btn_del, btn_save;
    private EditText editable_item;

    DatabaseHelper myDb;

    private String selectedName;
    private int selectedID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcheckout_layout);

        btn_save = (Button)findViewById(R.id.button_save);
        btn_del = (Button)findViewById(R.id.button_delete);
        editable_item = (EditText) findViewById(R.id.editText);
        myDb = new DatabaseHelper(this);

        //get the intent extra from the Checkout Activity
        Intent receivedIntent = getIntent();

        //now get the itemID passed as an extra
        selectedID = receivedIntent.getIntExtra("ID", -1); //NOTE: -1 is just the default value

        //now get the name passed as an extra
        selectedName = receivedIntent.getStringExtra("name");

        editable_item.setText(selectedName);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = editable_item.getText().toString();
                if(!item.equals("")){
                    myDb.updateQuantity(item, selectedID);
                } else{
                    Snackbar.make(v, "Please enter a quantity",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = editable_item.getText().toString();
                if(!item.equals("")){
                    myDb.deleteName(selectedID, selectedName);
                    editable_item.setText("");
                    Snackbar.make(v, "Deleted",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else{
                    Snackbar.make(v, "Please enter a name",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
}
