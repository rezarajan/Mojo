package bluefirelabs.mojo.main.ui.user;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.FirebaseRecyclerAdapterHistory;
import bluefirelabs.mojo.handlers.adapters.orderHistory_List;

public class order_tracking extends AppCompatActivity
{

    FirebaseAuth firebaseAuth;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<orderHistory_List, FirebaseRecyclerAdapterHistory.RecyclerViewHolder> mFirebaseAdapter;

    private RecyclerView mRestaurantRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    public static final String RESTAURANT = "uid";       //TODO: Change this to the correct node

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        }


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("User's ID", user.getUid());

        populateView(user.getUid());      //TODO: find the user's current orders
    }


    private void populateView(String user){
    mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);





    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    mFirebaseAdapter = new FirebaseRecyclerAdapter<orderHistory_List, FirebaseRecyclerAdapterHistory.RecyclerViewHolder>(
    orderHistory_List.class,
    R.layout.order_tracking_card,
    FirebaseRecyclerAdapterHistory.RecyclerViewHolder.class,
            mFirebaseDatabaseReference.child(RESTAURANT).child(user).child("orders")    //Firebase updated to contain the orders node
                                                                                        //under the uid node to avoid the info node
                                                                                        //conflict for null result
        ) {

        @Override
        protected void populateViewHolder(final FirebaseRecyclerAdapterHistory.RecyclerViewHolder viewHolder, orderHistory_List model, int position) {

            //String[] restaurantName = {""};

            final String result = model.getResult();
            Log.d("Result", result);

            viewHolder.orderid.setText(model.getOrderid());

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(RESTAURANT).child(model.getVendoruid()).child("info");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                    String restaurantName = hopperValues.get("name").toString();        //finds the vendor's name
                    String imageUrl = hopperValues.get("icon").toString();        //finds the vendor's icon

                    //Sets the vendor's name
                    viewHolder.restaurant_name.setText(restaurantName);
                    if(result.equals("declined")){
                        //case for declined orders
                        viewHolder.status_initial.setText(restaurantName + " has declined your order :(");
                    } else {
                        //case for accepted orders
                        //easier to do the else case for this to account for all the other results
                        //since this is an asynchronous task
                        viewHolder.status_initial.setText(restaurantName + " has accepted your order!");

                    }

                    //sets the vendor's icon
                    Picasso.with(getApplicationContext())
                            .load(imageUrl)
                            .into(viewHolder.restaurant_icon);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //the runner's uid is now available at this stage

            if(model.getRunneruid() != null) {
                final DatabaseReference reference_1 = FirebaseDatabase.getInstance().getReference(RESTAURANT)
                        .child(model.getRunneruid()).child("info");
                reference_1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                        String runnerName = hopperValues.get("name").toString();        //finds the runner's name


                        //Sets the vendor's name
                        viewHolder.status_collected.setText(runnerName + " has collected your order");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final DatabaseReference reference_2 = FirebaseDatabase.getInstance().getReference(RESTAURANT)
                        .child(model.getRunneruid()).child("info");
                reference_2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> hopperValues = (Map<String, Object>) dataSnapshot.getValue();
                        String runnerName = hopperValues.get("name").toString();        //finds the runner's name


                        //Sets the vendor's name
                        viewHolder.status_delivered.setText(runnerName + " has delivered your order");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            //The case for the user collecting the order
            else {
                //Sets the message for the case of the user collecting the order
                viewHolder.status_delivered.setText("You have collected the order");
            }

            switch (result) {
                case "accepted":
                case "declined":
                    viewHolder.status_initial_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_initial.setVisibility(View.VISIBLE);
                    viewHolder.status_initial_time.setVisibility(View.VISIBLE);

                    viewHolder.status_ready_icon.setVisibility(View.GONE);
                    viewHolder.status_ready.setVisibility(View.GONE);
                    viewHolder.status_ready_time.setVisibility(View.GONE);

                    viewHolder.status_collected_icon.setVisibility(View.GONE);
                    viewHolder.status_collected.setVisibility(View.GONE);
                    viewHolder.status_collected_time.setVisibility(View.GONE);

                    viewHolder.status_delivered_icon.setVisibility(View.GONE);
                    viewHolder.status_delivered.setVisibility(View.GONE);
                    viewHolder.status_delivered_time.setVisibility(View.GONE);

                    break;
                case "sending":
                    viewHolder.status_initial_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_initial.setVisibility(View.VISIBLE);
                    viewHolder.status_initial_time.setVisibility(View.VISIBLE);

                    viewHolder.status_ready_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_ready.setVisibility(View.VISIBLE);
                    viewHolder.status_ready_time.setVisibility(View.VISIBLE);

                    viewHolder.status_collected_icon.setVisibility(View.GONE);
                    viewHolder.status_collected.setVisibility(View.GONE);
                    viewHolder.status_collected_time.setVisibility(View.GONE);

                    viewHolder.status_delivered_icon.setVisibility(View.GONE);
                    viewHolder.status_delivered.setVisibility(View.GONE);
                    viewHolder.status_delivered_time.setVisibility(View.GONE);

                    break;
                case "collected":
                    viewHolder.status_initial_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_initial.setVisibility(View.VISIBLE);
                    viewHolder.status_initial_time.setVisibility(View.VISIBLE);

                    viewHolder.status_ready_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_ready.setVisibility(View.VISIBLE);
                    viewHolder.status_ready_time.setVisibility(View.VISIBLE);

                    viewHolder.status_collected_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_collected.setVisibility(View.VISIBLE);
                    viewHolder.status_collected_time.setVisibility(View.VISIBLE);

                    viewHolder.status_delivered_icon.setVisibility(View.GONE);
                    viewHolder.status_delivered.setVisibility(View.GONE);
                    viewHolder.status_delivered_time.setVisibility(View.GONE);

                    break;
                case "delivered":
                    viewHolder.status_initial_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_initial.setVisibility(View.VISIBLE);
                    viewHolder.status_initial_time.setVisibility(View.VISIBLE);

                    viewHolder.status_ready_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_ready.setVisibility(View.VISIBLE);
                    viewHolder.status_ready_time.setVisibility(View.VISIBLE);

                    viewHolder.status_collected_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_collected.setVisibility(View.VISIBLE);
                    viewHolder.status_collected_time.setVisibility(View.VISIBLE);

                    viewHolder.status_delivered_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_delivered.setVisibility(View.VISIBLE);
                    viewHolder.status_delivered_time.setVisibility(View.VISIBLE);

                    break;
                case "user_collected":
                    viewHolder.status_initial_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_initial.setVisibility(View.VISIBLE);
                    viewHolder.status_initial_time.setVisibility(View.VISIBLE);

                    viewHolder.status_ready_icon.setVisibility(View.GONE);
                    viewHolder.status_ready.setVisibility(View.GONE);
                    viewHolder.status_ready_time.setVisibility(View.GONE);

                    viewHolder.status_collected_icon.setVisibility(View.GONE);
                    viewHolder.status_collected.setVisibility(View.GONE);
                    viewHolder.status_collected_time.setVisibility(View.GONE);

                    viewHolder.status_delivered_icon.setVisibility(View.VISIBLE);
                    viewHolder.status_delivered.setVisibility(View.VISIBLE);
                    viewHolder.status_delivered_time.setVisibility(View.VISIBLE);

                    break;
                default:
                    viewHolder.status_initial_icon.setVisibility(View.GONE);
                    viewHolder.status_initial.setVisibility(View.GONE);
                    viewHolder.status_initial_time.setVisibility(View.GONE);

                    viewHolder.status_ready_icon.setVisibility(View.GONE);
                    viewHolder.status_ready.setVisibility(View.GONE);
                    viewHolder.status_ready_time.setVisibility(View.GONE);

                    viewHolder.status_collected_icon.setVisibility(View.GONE);
                    viewHolder.status_collected.setVisibility(View.GONE);
                    viewHolder.status_collected_time.setVisibility(View.GONE);

                    viewHolder.status_delivered_icon.setVisibility(View.GONE);
                    viewHolder.status_delivered.setVisibility(View.GONE);
                    viewHolder.status_delivered_time.setVisibility(View.GONE);
                    break;
            }



            /*if (model.getResult().equals("delivered")) {
                viewHolder.itemView.setVisibility(View.GONE);
            } */

            //Log.d("Description: ", model.getDescription());
            //viewHolder.itemDescription.setText(model.getDescription());
            //viewHolder.itemTitle.setText(model.getRestaurant());
            //viewHolder.itemIcon.setImageResource(R.drawable.restaurant_icon);
            //Picasso.with(getApplicationContext()).load(model.getIcon()).into(viewHolder.itemIcon);

                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.getter_restaurants();
                    }
                }); */
        }
    };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            int restaurantCount = mFirebaseAdapter.getItemCount();
            int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
            if(lastVisiblePosition == -1 || (positionStart >= (restaurantCount -1) && lastVisiblePosition == (positionStart -1))){
                mRestaurantRecyclerView.scrollToPosition(positionStart);
            }
        }
    });

        mRestaurantRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRestaurantRecyclerView.setAdapter(mFirebaseAdapter);
        mRestaurantRecyclerView.setNestedScrollingEnabled(false);
}
}
