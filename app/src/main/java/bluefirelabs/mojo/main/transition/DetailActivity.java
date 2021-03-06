package bluefirelabs.mojo.main.transition;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.FirebaseRecyclerAdapterItems_new;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import database.DatabaseHelper;


/**
 * Created by xmuSistone on 2016/9/19.
 */
public class DetailActivity extends FragmentActivity {

    public static final String EXTRA_IMAGE_URL = "detailImageUrl";
    public static final String EXTRA_RESTAURANT_DETAILS = "detailRestaurant";
    public static final String EXTRA_RESTAURANT_NAME = "restaurantName";
    public static final String EXTRA_RESTAURANT_COLOR = "restaurantColor";

    String imageUrl, restaurant_description, restaurantName;
    Integer mutedColor;
    int defaultColor = 0x000000;

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
    private ImageView imageView, checkout_icon;
    //private CircleImageView imageView;
    private RatingBar ratingBar;
    //private LinearLayout detail_layout;
    private RelativeLayout detail_layout;
    private View accent_layout;
    LinearLayout linearLayout;
    RelativeLayout detail_item;

    DatabaseHelper myDb;

    private FirebaseRecyclerAdapter<Food_List, FirebaseRecyclerAdapterItems_new.RecyclerViewHolder> mFirebaseAdapter;

    private RecyclerView mRestaurantRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


    private LinearLayout listContainer;
    private static final String[] headStrs = {HEAD1_TRANSITION_NAME, HEAD2_TRANSITION_NAME, HEAD3_TRANSITION_NAME, HEAD4_TRANSITION_NAME};
    //private static final int[] imageIds = {R.drawable.image1, R.drawable.image1, R.drawable.image1, R.drawable.image1};



     //dealListView() is only done in the onResume state since when the app is started the activity is preloaded as well
    //This ensures that the items are not loaded twice
    @Override
    protected void onResume() {
        super.onResume();          //Ensures that the animations remain constant
        listContainer.removeAllViews();     //clears the previous set of items to refresh if the user makes a change at checkout
        dealListView();     //reloads all the views
    }

/*    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity", "paused");
        listContainer.removeAllViews();     //clears the previous set of items to refresh if the user makes a change at checkout
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_detail);
        myDb = new DatabaseHelper(this); //calls constructor from the database helper class

        imageView = (ImageView) findViewById(R.id.image);
        //imageView = (CircleImageView) findViewById(R.id.image);
        //address1 = findViewById(R.id.address1);
        //address2 = findViewById(R.id.address2);
        //address3 = findViewById(R.id.address3);
        //address4 = (TextView) findViewById(R.id.address4);
        //address5 = findViewById(R.id.address5);
        //ratingBar = (RatingBar) findViewById(R.id.rating);
        listContainer = (LinearLayout) findViewById(R.id.detail_list_container);
        checkout_icon = (ImageView) findViewById(R.id.checkout_icon);
        //accent_layout = (LinearLayout) findViewById(R.id.accent_layout);
        //detail_layout = (LinearLayout) findViewById(R.id.detail_background);
        detail_layout = (RelativeLayout) findViewById(R.id.detail_background);
        detail_item = (RelativeLayout) findViewById(R.id.detail_card_layout);
        accent_layout = (View) findViewById(R.id.accent_layout);        //separator colour
        linearLayout = (LinearLayout) findViewById(R.id.detail_list_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        restaurant_description = getIntent().getStringExtra(EXTRA_RESTAURANT_DETAILS);
        restaurantName = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);
        //mutedColor = getIntent().getIntExtra(EXTRA_RESTAURANT_COLOR, 0);
        Log.d("Colour", String.valueOf(mutedColor));
        //ImageLoader.getInstance().displayImage(imageUrl, imageView);
        //address4.setText(restaurant_description);
        Picasso.with(getApplicationContext()).load(imageUrl).into(imageView);


        Picasso.with(getApplicationContext())
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        //Log.d("Setting Colour for", fragments.get(position%10).dataReturn());
                        Log.d("Changing", "activated 1");

                        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                                                        /*Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                                            @Override
                                                            public void onGenerated(Palette palette) {
                                                                mutedColor = palette.getVibrantColor(defaultColor);
                                                                //mutedColor = palette.getMutedColor(defaultColor);
                                                                Log.d("Changing", "activated 2");
                                                                //obj.colorFetched(position, vibrantColor, mutedColor);
                                                                //viewPager.setBackgroundColor(vibrantColor);
                                                            }
                                                        }); */
                        Palette palette;
                        palette = Palette.from(bitmap).generate();
                        //mutedColor = palette.getDarkVibrantColor(defaultColor);
                        mutedColor = palette.getVibrantColor(defaultColor);
                        //mutedColor = palette.getMutedColor(defaultColor);

                        /* Setting the colours of the detail view based on the icon clicked */

                        listContainer.setBackgroundColor(mutedColor);       //Sets the colour for one item of the detail list
                        linearLayout.setBackgroundColor(mutedColor);        //Sets the colour for the entire detail list
                        //accent_layout.setBackgroundColor(mutedColor);        //Sets the colour for the separator
                        detail_layout.setBackgroundColor(mutedColor);        //Sets the colour for the frame (accents)
                        Log.d("Muted Color Status", "Activated");


                        //obj.colorFetched(position, vibrantColor, mutedColor);
                        //viewPager.setBackgroundColor(vibrantColor);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }


                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        checkout_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(DetailActivity.this, Checkout.class);            //Goes to checkout
                Intent intent = new Intent(DetailActivity.this, bluefirelabs.mojo.main.ui.checkout.Checkout.class);            //Goes to checkout
                startActivity(intent);
            }
        });


        ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);
        //ViewCompat.setTransitionName(address1, ADDRESS1_TRANSITION_NAME);
        //ViewCompat.setTransitionName(address2, ADDRESS2_TRANSITION_NAME);
        //ViewCompat.setTransitionName(address3, ADDRESS3_TRANSITION_NAME);
        //ViewCompat.setTransitionName(address4, ADDRESS4_TRANSITION_NAME);
        //ViewCompat.setTransitionName(address5, ADDRESS5_TRANSITION_NAME);
        //ViewCompat.setTransitionName(ratingBar, RATINGBAR_TRANSITION_NAME);

        //dealListView();
    }



    public static int getComplementaryColor(int colorToInvert) {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorToInvert), Color.green(colorToInvert),
                Color.blue(colorToInvert), hsv);
        hsv[0] = (hsv[0] + 180) % 360;
        return Color.HSVToColor(hsv);
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

                                            Map.Entry<String, Object> entry = items.entrySet().iterator().next();

                                            final String itemKeyInitial = entry.getKey();       //Setting up the identifier for the first item on the list to compare and set header
                                            Log.d("Initial key", itemKeyInitial);

                                            for (final String itemKey : items.keySet()) {



                                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(restaurant).child(restaurantName).child(s).child("Items").child(itemKey);
                                                reference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Map<String, Object> item_information = (Map<String, Object>) dataSnapshot.getValue();

                                                        if (item_information != null) {


                                                            View childView = layoutInflater.inflate(R.layout.detail_list_item, null);

                                                            listContainer.addView(childView);

                                                            //ImageView headView = (ImageView) childView.findViewById(R.id.head);
                                                            final TextView item_name = (TextView) childView.findViewById(R.id.item_type);
                                                            final TextView item_details = (TextView) childView.findViewById(R.id.item_dets);
                                                            final TextView item_cost = (TextView) childView.findViewById(R.id.item_cost);
                                                            final TextView item_quantity = (TextView) childView.findViewById(R.id.item_quantity);


                                                            item_details.setText(item_information.get("name").toString());
                                                            String cost = "$" + item_information.get("cost").toString();
                                                            item_cost.setText(cost);


                                                            if(item_details.getText().equals(itemKeyInitial)){      //Comparing the first value for each item type to set the header for the different category
                                                                //item_name.setVisibility(childView.GONE);        //Sets the view for item type to gone for the actual food items
                                                                item_name.setText(s);        //Sets the view for item type to gone for the actual food items

                                                            } else {
                                                                item_name.setVisibility(childView.GONE);        //Sets the view for item type to gone for the actual food items

                                                            }


                                                            //item_quantity.setText(item_information.get("Quantity").toString());
                                                            item_quantity.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua");

                                                            //Picasso.with(getApplicationContext()).load(restaurantInfo.child("icon").getValue().toString()).into(headView);        //TODO: Use the vector logos here


                                                            //TODO: Get the checkboxes to add/delete the data from the database
                                                            CheckBox checkbox = (CheckBox) childView.findViewById(R.id.checkb);


                                                            /*Finding the complementary color for muted color and setting it as the color for the checkbox */

                                                            ColorStateList colorStateList = new ColorStateList(
                                                                    new int[][]{
                                                                            new int[]{-android.R.attr.state_checked}, // unchecked
                                                                            new int[]{android.R.attr.state_checked} , // checked
                                                                    },
                                                                    new int[] {


                                                                            //getResources().getColor(R.color.colorPrimary),     //unchecked
                                                                            //getResources().getColor(R.color.colorPrimary),     //checked
                                                                            getComplementaryColor(mutedColor), //unchecked
                                                                            getComplementaryColor(mutedColor),      //checked


                                                                    }
                                                            );



                                                            CompoundButtonCompat.setButtonTintList(checkbox, colorStateList);



                                                         String sanitizedItem = item_details.getText().toString().replace("'", "''");        //looks for any "'" in the item name (like S'Mores) so that the DatabaseHelper can properly query it
                                                         Cursor data = myDb.getItemID(sanitizedItem);        //gets the primary key associated with the item name
                                                            int itemID = -1;
                                                            while(data.moveToNext()){
                                                                itemID = data.getInt(0);
                                                            }
                                                            if(itemID > 0){

      /*                                                         myDb.deleteName(itemID, sanitizedItem);     //The item name and ID are used to delete the item on checkbox unchecked

                                                                Snackbar.make(childView, item_details.getText() + " removed from Cart",
                                                                        Snackbar.LENGTH_LONG)
                                                                        .setAction("Action", null).show();*/

                                                                checkbox.setChecked(true);
                                                                Log.d(item_details.getText().toString(), "checked");

                                                            } else {
                                                                checkbox.setChecked(false);
                                                            }





                                                            /*AppCompatCheckBox checkbox = (AppCompatCheckBox) childView.findViewById(R.id.checkb);
                                                            checkbox.setSupportButtonTintList(colorStateList);*/


                                                            checkbox.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    CheckBox checkbox = (CheckBox) v;
                                                                    if(checkbox.isChecked()){
                                                                        Snackbar.make(v, item_details.getText() + " added to Cart",
                                                                                Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();

                                                                        //adding the item to the database for checkout
                                                                        boolean isInserted = myDb.insertData(restaurantName,       //The restaurant name
                                                                                item_details.getText().toString(),     //The item name
                                                                                item_cost.getText().toString().replace("$",""),       //The item cost
                                                                                "1");                                //Adds the item at at the specific position to the database
                                                                        //Default Quantity is 1

                                                                        Log.d("Adapted Restaurant", restaurantName);
                                                                        if (isInserted == true) {
                                                                            Snackbar.make(v, item_details.getText() + " added to Cart",
                                                                                    Snackbar.LENGTH_LONG)
                                                                                    .setAction("Action", null).show();
                                                                        } else {
                                                                            Snackbar.make(v, "Error adding item to cart",
                                                                                    Snackbar.LENGTH_LONG)
                                                                                    .setAction("Action", null).show();
                                                                        }


                                                                    } else {

                                                                        String sanitizedItem = item_details.getText().toString().replace("'", "''");        //looks for any "'" in the item name (like S'Mores) so that the DatabaseHelper can properly query it
                                                                        Cursor data = myDb.getItemID(sanitizedItem);        //gets the primary key associated with the item name
                                                                        int itemID = -1;
                                                                        while(data.moveToNext()){
                                                                            itemID = data.getInt(0);
                                                                        }
                                                                        if(itemID > 0){

                                                                            myDb.deleteName(itemID, sanitizedItem);     //The item name and ID are used to delete the item on checkbox unchecked

                                                                            Snackbar.make(v, item_details.getText() + " removed from Cart",
                                                                                    Snackbar.LENGTH_LONG)
                                                                                    .setAction("Action", null).show();

                                                                        }


                                                                    }
                                                                }
                                                            });




/*                                                            childView.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Snackbar.make(v, item_details.getText() + " added to Cart",
                                                                            Snackbar.LENGTH_LONG)
                                                                            .setAction("Action", null).show();




                                                                    //adding the item to the database for checkout
                                                                    boolean isInserted = myDb.insertData(restaurant,       //The restaurant name
                                                                            item_details.getText().toString(),     //The item name
                                                                            item_cost.getText().toString().replace("$",""),       //The item cost
                                                                            "1");                                //Adds the item at at the specific position to the database
                                                                    //Default Quantity is 1

                                                                    Log.d("Adapted Restaurant", restaurant);
                                                                    if (isInserted == true) {
                                                                        Snackbar.make(v, item_details.getText() + " added to Cart",
                                                                                Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();
                                                                    } else {
                                                                        Snackbar.make(v, "Data not Inserted",
                                                                                Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();
                                                                    }





                                                                }
                                                            });*/

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