package bluefirelabs.mojo.main.ui.user;

import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   */

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

            String result = model.getResult();
            Log.d("Result", result);

            viewHolder.orderid.setText(model.getOrderid());


            if(result.equals("accepted") || result.equals("declined")){

                if(result.equals("accepted")){
                    //case for accpted orders
                    viewHolder.status_initial.setText("{Restaurant} has accepted your order!");
                } else {
                    //case for declined orders
                    viewHolder.status_initial.setText("{Restaurant} has declined your order :(");

                }
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

            } else if (result.equals("sending")){
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

            } else if (result.equals("collected")){
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

            } else if (result.equals("delivered")){
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

            } else {
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
