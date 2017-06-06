package bluefirelabs.mojo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.FirebaseRecyclerAdapterMenu;
import bluefirelabs.mojo.handlers.Food_List;

public class Restaurant_Menu extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public static final String MENU = "menu/Starbucks";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Food_List, FirebaseRecyclerAdapterMenu.RecyclerViewHolder> mFirebaseAdapter;

    private RecyclerView mRestaurantRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private String restaurant, iconRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Menu");

        Intent receivedIntent = getIntent();
        restaurant = receivedIntent.getStringExtra("Restaurant");
        iconRef = receivedIntent.getStringExtra("Icon");

        ImageView restaurantIcon = (ImageView) findViewById(R.id.restaurant_icon);
        Picasso.with(Restaurant_Menu.this).load(iconRef).into(restaurantIcon);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(restaurant);

        /*
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setNestedScrollingEnabled(true);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter_Menu();
        recyclerView.setAdapter(adapter);
        */
       // Context context = this.getApplicationContext();
        populateView();

    }

    public void populateView(){
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Food_List, FirebaseRecyclerAdapterMenu.RecyclerViewHolder>(
                Food_List.class,
                R.layout.card_layout,
                FirebaseRecyclerAdapterMenu.RecyclerViewHolder.class,
                mFirebaseDatabaseReference.child(MENU)
        ) {

            @Override
            protected void populateViewHolder(FirebaseRecyclerAdapterMenu.RecyclerViewHolder viewHolder, Food_List model, int position) {
                //Log.d("Description: ", model.getDescription());
                //viewHolder.itemDescription.setText(model.getDescription());
                //viewHolder.itemTitle.setText(model.getRestaurant());
                viewHolder.itemDescription.setText(model.getType());
                viewHolder.itemTitle.setText(model.getType());
                //viewHolder.itemIcon.setImageResource(R.drawable.restaurant_icon);
               // Picasso.with(getApplicationContext()).load(model.getIcon()).into(viewHolder.itemIcon);
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