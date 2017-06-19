package bluefirelabs.mojo.barcode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.Manifest;
import bluefirelabs.mojo.R;

public class barcodeReader extends AppCompatActivity {
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    Button scanbtn;
    TextView barcodeDisplay;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        scanbtn = (Button) findViewById(R.id.scanbutton);
        barcodeDisplay = (TextView) findViewById(R.id.barcodeDisplay);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(barcodeReader.this, barcodeScanner.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("requests");


        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");

                reference1.orderByChild("orderid").equalTo(barcode.displayValue).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        DatabaseReference hopperRef = reference1.child(barcode.displayValue);     //uses the itemTitle, which is set to be the orderid, in order to get the order id on click of a specific card
                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                        hopperUpdates.put("result", "collected");                                           //appends the key "result" a value of "accepted". This can be changed to suit
                        hopperRef.updateChildren(hopperUpdates);                                           //updates the child, without destroying, or overwriting all data
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                barcodeDisplay.post(new Runnable() {
                    @Override
                    public void run() {
                        barcodeDisplay.setText(barcode.displayValue);
                    }
                });
            }
        }
    }
}
