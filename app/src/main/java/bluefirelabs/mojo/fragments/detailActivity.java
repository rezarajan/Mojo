package bluefirelabs.mojo.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.FirebaseAdapterExtras;
import bluefirelabs.mojo.handlers.adapters.Food_List;
import bluefirelabs.mojo.main.transition.MyCallback;
import database.DatabaseHelper;

/**
 * Created by reza on 8/5/17.
 */

public class detailActivity extends Fragment{

    public static final String EXTRA_RESTAURANT_DETAILS = "detailRestaurant";
    public static final String EXTRA_RESTAURANT_LOGO = "restaurantLogo";
    public static final String EXTRA_RESTAURANT_NAME = "restaurantName";
    public static final String EXTRA_RESTAURANT_COLOR = "restaurantColor";

    String imageUrl, restaurant_description, restaurantName, restaurantColor;

    private ImageView imageView;
    private RelativeLayout background_image_view;
    private View accent_layout;
    ;
    private LinearLayout listContainer;

    LinearLayout linearLayout;
    RelativeLayout detail_item;

    NestedScrollView nestedScrollView, nestedScrollViewExtras;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    DatabaseHelper myDb;

    private FirebaseRecyclerAdapter<Food_List, FirebaseAdapterExtras.RecyclerViewHolder> mFirebaseAdapter;
    private RecyclerView mRestaurantRecyclerView;

    int cardPosition = 0;

    private String parent = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.item_menu, container, false);

        Bundle bundle = this.getArguments();
        if(bundle != null) {

            imageUrl = bundle.getString(EXTRA_RESTAURANT_LOGO);
            restaurant_description = bundle.getString(EXTRA_RESTAURANT_DETAILS);
            restaurantName = bundle.getString(EXTRA_RESTAURANT_NAME);
            restaurantColor = bundle.getString(EXTRA_RESTAURANT_COLOR);

            Log.d("Restaurant", restaurantName);

            myDb = new DatabaseHelper(getContext()); //calls constructor from the database helper class

            imageView = (ImageView) view.findViewById(R.id.image);

            listContainer = (LinearLayout) view.findViewById(R.id.detail_list_container);

            background_image_view = (RelativeLayout) view.findViewById(R.id.detail_background);

            detail_item = (RelativeLayout) view.findViewById(R.id.detail_card_layout);
            accent_layout = (View) view.findViewById(R.id.accent_layout);        //separator colour
            linearLayout = (LinearLayout) view.findViewById(R.id.detail_list_layout);

            nestedScrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
            nestedScrollViewExtras = (NestedScrollView) view.findViewById(R.id.scrollViewExtras);

            //setting up the sliding panel from the parent activity
            slidingUpPanelLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout_frag2);

            //resetting the panel to a collapsed state
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            //enables the scrollview in the sliding panel
            slidingUpPanelLayout.setScrollableView(nestedScrollViewExtras);

            slidingUpPanelLayout.setTouchEnabled(false);


            Log.d("Colour", String.valueOf(restaurantColor));

            Picasso.with(getContext()).load(imageUrl).into(imageView);

            //referencing the recyclerview in the sliding panel from the parent activity
            mRestaurantRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_extras);



            if(restaurantColor != null){
                //background_image_view.setBackgroundColor(Color.parseColor(restaurantColor));
                //background_image_view.getBackground().setAlpha(153);        //0 <= alpha <= 255, setting for 60% = 153 to maintain the theme

                //nestedScrollView.setBackgroundColor(Color.parseColor(restaurantColor));
                //nestedScrollView.getBackground().setAlpha(153);        //0 <= alpha <= 255, setting for 60% = 153 to maintain the theme

                linearLayout.setBackgroundColor(Color.parseColor(restaurantColor));
                linearLayout.getBackground().setAlpha(153);        //0 <= alpha <= 255, setting for 60% = 153 to maintain the theme
            }

        }

        dealListView();
        return view;
    }



    public void firebaseTask(final MyCallback myCallback) {

        myCallback.callbackCall("menu");
    }


    public void dealListView() {       //TODO: Firebase Menu Items will go here
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());

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

                                                                        //adding the item to the database for receipt
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

                                                                            //populating the extras view
                                                                            populateView(reference);

                                                                            //brings up the sliding panel if the item has extras
                                                                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);


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


    public void populateView(final DatabaseReference reference){

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Food_List, FirebaseAdapterExtras.RecyclerViewHolder>(
                Food_List.class,
                R.layout.extra_item,
                FirebaseAdapterExtras.RecyclerViewHolder.class,
                reference.child("extras").orderByChild("parent")
        ) {
            @Override
            public void onBindViewHolder(FirebaseAdapterExtras.RecyclerViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

            }


            @Override
            protected void populateViewHolder(final FirebaseAdapterExtras.RecyclerViewHolder viewHolder, final Food_List model, final int position) {

                if(parent.equals(model.getParent())){
                    parent = checkString(model.getParent());
                    Log.d("Parent", parent);

                    viewHolder.extraTypeHolder.setVisibility(View.GONE);
                }
                else{
                    parent = checkString(model.getParent());

                    if(model.getName() != null){
                        viewHolder.extraTypeHolder.setVisibility(View.VISIBLE);

                        viewHolder.extraParent.setText(model.getParent());
                        viewHolder.extraName.setText(model.getName());
                    }
                }

                //viewHolder.openIndicatorText.setText(model.getOpen());
                //viewHolder.extraCost.setText("$" + String.valueOf(model.getCost()));
                //viewHolder.averageTime.setText(model.getAverageTime());

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int restaurantCount = mFirebaseAdapter.getItemCount();
                //mRestaurantRecyclerView.scrollToPosition(cardPosition);

            }
        });

        mRestaurantRecyclerView.setAdapter(mFirebaseAdapter);
        //mRestaurantRecyclerView.setLayoutManager(turnLayoutManager);
        //mRestaurantRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRestaurantRecyclerView.setLayoutManager(mLinearLayoutManager);

        //SnapHelper helper = new LinearSnapHelper();
/*        LinearSnapHelper helper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        helper.attachToRecyclerView(mRestaurantRecyclerView);*/

    }

    private String checkString(String extraType) {
        //handle value
        return extraType;
    }

}
