package bluefirelabs.mojo.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.barcode.barcodeScanner;
import bluefirelabs.mojo.main.transition.MyCallback;
import bluefirelabs.mojo.main.ui.user.UserHub_carousel;
import bluefirelabs.mojo.main.ui.user.receipt;
import database.DatabaseHelper;

/**
 * Created by reza on 8/5/17.
 */

public class barcodeConfirmer extends Fragment{

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    Button scanbtn;
    TextView barcodeDisplay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.activity_barcode_reader, container, false);
        scanbtn = (Button) view.findViewById(R.id.scanbutton);
        barcodeDisplay = (TextView) view.findViewById(R.id.barcodeDisplay);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        //directing to the scan activity
        Intent intent = new Intent(getContext(), barcodeScanner.class);       //goes to the barcode scanner to read the QR
        startActivityForResult(intent, REQUEST_CODE);       //waits for the QR result

        return view;
    }


    //confirms the delivery/pickup on Firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fm;
        fm = getFragmentManager();

        final barcodeConfirmer barcodeConfirmer = (bluefirelabs.mojo.fragments.barcodeConfirmer) fm.findFragmentByTag("barcodeConfirmer");
        final restaurantCards restaurantCards = (bluefirelabs.mojo.fragments.restaurantCards) fm.findFragmentByTag("restaurantCards");
        final FragmentTransaction ft = fm.beginTransaction();


        //final RelativeLayout receiptOverview = (RelativeLayout) getView().findViewById(R.id.receiptOverview);


        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("requests");


        if (requestCode == REQUEST_CODE && resultCode == -1) {      //-1 is RESULT_OK
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");

                reference1.orderByChild("orderid").equalTo(barcode.displayValue).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        DatabaseReference hopperRef = reference1.child(barcode.displayValue);     //uses the itemTitle, which is set to be the orderid, in order to get the order id on click of a specific card
                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                        Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();



                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String UID = user.getUid();



                        if(hopperValues != null){
                            //The case for the runner picking up the order for delivery to the kiosk (no runner and uid not the customeruid)
                            if(hopperValues.get("runneruid") == null && !hopperValues.get("customeruid_to").toString().equals(UID)){       //TODO: && runnermode == "enabled"
                                //appends the key "result" a value of "accepted". This can be changed to suit
                                hopperValues.put("runneruid", UID);     //at this stage we need the runner's UID to be written to Firebase
                                hopperUpdates.put("result", "collected");
                                //updates the child, without destroying, or overwriting all data
                                hopperRef.updateChildren(hopperUpdates);

                                //If it is a user, then show a receipt overview of the transaction on scanning
                                //receiptOverview.setVisibility(View.VISIBLE);
                            }

                            //The case of the user picking up the order from the kiosk or directly
                            else if (hopperValues.get("customeruid_to") != null && hopperValues.get("customeruid_to").toString().equals(UID)){
                                //appends the key "result" a value of "accepted". This can be changed to suit
                                hopperUpdates.put("result", "delivered");
                                //updates the child, without destroying, or overwriting all data
                                hopperRef.updateChildren(hopperUpdates);

                                //If it is a runner, then show a confirmation of the delivery
                                //receiptOverview.setVisibility(View.VISIBLE);

                                //Showing the customer the receipt
                                Intent intent = new Intent(getContext(), receipt.class);
                                startActivity(intent);

                            }

                            //The case of the wrong person picking up the order
                            else{

                                if(getView() != null){
                                    Snackbar.make(getView(), "This is not your order",
                                            Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        }



                        //TODO: Change this to an OK button, or a confirm button
                        scanbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Intent intent = new Intent(getContext(), barcodeScanner.class);       //geos to the barcode scanner to read the QR
                                //startActivityForResult(intent, REQUEST_CODE);       //waits for the QR result
                                //getFragmentManager().popBackStack();
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();

                                restaurantCards restaurantCards = (bluefirelabs.mojo.fragments.restaurantCards) fm.findFragmentByTag("restaurantCards");
                                detailActivity detailActivity = (bluefirelabs.mojo.fragments.detailActivity) fm.findFragmentByTag("detailActivity");
                                barcodeConfirmer barcodeConfirmer = (bluefirelabs.mojo.fragments.barcodeConfirmer) fm.findFragmentByTag("barcodeConfirmer");

                                restaurantCards restaurantCards_fragment = new restaurantCards();


                                ft.detach(barcodeConfirmer);

                                //finding the parent's ScrollView
                                final ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);

                                scrollView.setVisibility(View.GONE);



                                //ft.remove(restaurantCards);
/*                if(detailActivity != null){
                    ft.attach(detailActivity);
                }*/


                                ft.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down);

                                if(restaurantCards != null) {
                                    ft.attach(restaurantCards);
                                    Log.d("Action", "attaching");
                                }

                                else {
                                    ft.add(R.id.fragment1, restaurantCards_fragment, "restaurantCards");
                                    Log.d("Action", "adding");
                                    ((UserHub_carousel)getActivity()).locationTasks();

                                }

                                ft.commit();

                            }
                        });


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
