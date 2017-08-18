package bluefirelabs.mojo.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.barcode.barcodeScanner;

/**
 * Created by rezarajan on 10/08/2017.
 */

public class barcodeReader extends Fragment {

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    Button scanbtn;
    TextView barcodeDisplay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.activity_barcode_reader, container, false);
        scanbtn = (Button) view.findViewById(R.id.scanbutton);
        barcodeDisplay = (TextView) view.findViewById(R.id.barcodeDisplay);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), barcodeScanner.class);       //geos to the barcode scanner to read the QR
                startActivityForResult(intent, REQUEST_CODE);       //waits for the QR result
            }
        });

        return view;
    }




}
