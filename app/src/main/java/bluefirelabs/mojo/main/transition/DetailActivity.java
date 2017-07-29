package bluefirelabs.mojo.main.transition;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import bluefirelabs.mojo.R;


/**
 * Created by xmuSistone on 2016/9/19.
 */
public class DetailActivity extends FragmentActivity {

    public static final String EXTRA_IMAGE_URL = "detailImageUrl";
    public static final String EXTRA_RESTAURANT_DETAILS = "detailRestaurant";
    public static final String EXTRA_RESTAURANT_NAME = "restaurantName";

    String imageUrl, restaurant_description, restaurantName;

    public static final String IMAGE_TRANSITION_NAME = "transitionImage";
    public static final String ADDRESS1_TRANSITION_NAME = "address1";
    public static final String ADDRESS2_TRANSITION_NAME = "address2";
    public static final String ADDRESS3_TRANSITION_NAME = "address3";
    public static final String ADDRESS4_TRANSITION_NAME = "address4";
    public static final String ADDRESS5_TRANSITION_NAME = "address5";
    public static final String RATINGBAR_TRANSITION_NAME = "ratingBar";

    public static final String HEAD1_TRANSITION_NAME = "head1";
    public static final String HEAD2_TRANSITION_NAME = "head2";
    public static final String HEAD3_TRANSITION_NAME = "head3";
    public static final String HEAD4_TRANSITION_NAME = "head4";

    private View address1, address2, address3, address5;
    private TextView address4;
    private ImageView imageView;
    private RatingBar ratingBar;

    private LinearLayout listContainer;
    private static final String[] headStrs = {HEAD1_TRANSITION_NAME, HEAD2_TRANSITION_NAME, HEAD3_TRANSITION_NAME, HEAD4_TRANSITION_NAME};
    //private static final int[] imageIds = {R.drawable.image1, R.drawable.image1, R.drawable.image1, R.drawable.image1};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = (ImageView) findViewById(R.id.image);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        address3 = findViewById(R.id.address3);
        address4 = (TextView) findViewById(R.id.address4);
        address5 = findViewById(R.id.address5);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        listContainer = (LinearLayout) findViewById(R.id.detail_list_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        restaurant_description = getIntent().getStringExtra(EXTRA_RESTAURANT_DETAILS);
        restaurantName = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);
        //ImageLoader.getInstance().displayImage(imageUrl, imageView);
        address4.setText(restaurant_description);
        Picasso.with(getApplicationContext()).load(imageUrl).into(imageView);

        ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);
        ViewCompat.setTransitionName(address1, ADDRESS1_TRANSITION_NAME);
        ViewCompat.setTransitionName(address2, ADDRESS2_TRANSITION_NAME);
        ViewCompat.setTransitionName(address3, ADDRESS3_TRANSITION_NAME);
        ViewCompat.setTransitionName(address4, ADDRESS4_TRANSITION_NAME);
        ViewCompat.setTransitionName(address5, ADDRESS5_TRANSITION_NAME);
        ViewCompat.setTransitionName(ratingBar, RATINGBAR_TRANSITION_NAME);

        dealListView();
    }

    public void firebaseTask(final MyCallback myCallback) {

        myCallback.callbackCall("menu");
    }

    private void dealListView() {       //TODO: Firebase Menu Items will go here
        final LayoutInflater layoutInflater = LayoutInflater.from(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final MyCallback myCallback = new MyCallback() {
            @Override
            public void callbackCall(final String restaurant) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant).child(restaurantName);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();


                        if (hopperValues != null) {
                            Log.d("HopperValues", hopperValues.toString());
                            Log.d("Size", String.valueOf(hopperValues.size()));


                            for (final String s : hopperValues.keySet()) {
                                Log.d("List Item", "Key: " + s);


                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant).child(restaurantName).child(s).child("Items");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Map<String, Object> items = (Map<String, Object>) dataSnapshot.getValue();


                                        if (items != null) {

                                            for (String itemKey : items.keySet()) {

                                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant).child(restaurantName).child(s).child("Items").child(itemKey);
                                                reference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Map<String, Object> item_information = (Map<String, Object>) dataSnapshot.getValue();


                                                        if (item_information != null) {

                                                            View childView = layoutInflater.inflate(R.layout.detail_list_item, null);
                                                            listContainer.addView(childView);

                                                            ImageView headView = (ImageView) childView.findViewById(R.id.head);
                                                            TextView item_details = (TextView) childView.findViewById(R.id.item_dets);
                                                            final TextView item_cost = (TextView) childView.findViewById(R.id.item_cost);
                                                            final TextView item_quantity = (TextView) childView.findViewById(R.id.item_quantity);

                                                            item_details.setText(item_information.get("name").toString());
                                                            String cost = "$" + item_information.get("cost").toString();
                                                            item_cost.setText(cost);
                                                            item_quantity.setText(item_information.get("Quantity").toString());

                                                            //Picasso.with(getApplicationContext()).load(restaurantInfo.child("icon").getValue().toString()).into(headView);        //TODO: Use the vector logos here

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }




                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        firebaseTask(myCallback);

    }


}
