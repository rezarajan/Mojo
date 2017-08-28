package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class shoppingCartAdapter extends RecyclerView.Adapter<SetViewHolder>{


    private Context context;
    private ArrayList<String> restaurantName, restaurantQuantity;

    private Map<String, String> itemName = new HashMap<String, String>();
    private Map<String, String> itemCost = new HashMap<String, String>();
    private Map<String, String> itemCount = new HashMap<String, String>();

    private View view;

    private int mExpandedPosition = -1;

    private boolean firstRun = true;

    private Double restaurantCost = 0.00;
    private DecimalFormat df = new DecimalFormat("#.##");

    private String newTotal;

    private int positionToSet;




    public shoppingCartAdapter(Context context, ArrayList<String> restaurantName, ArrayList<String> restaurantQuantity, Map<String, String> itemName, Map<String, String> itemCost, Map<String, String> itemCount){
        this.context = context;
        this.restaurantName = restaurantName;
        this.restaurantQuantity = restaurantQuantity;
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.itemCount = itemCount;
    }



/*    public shoppingCartAdapter (Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;

        CursorAdapter mCursorAdapter = new CursorAdapter(context, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                //Cursor mCursor = getCursor();

                final LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.cart_item, parent, false);

                int restaurantCol = cursor.getColumnIndex("RESTAURANT");
                String restName = cursor.getString(restaurantCol);

                restaurantName = (TextView) view.findViewById(R.id.restaurantName);
                restaurantCost = (TextView) view.findViewById(R.id.restaurantCost);
                totalItemQuantity = (TextView) view.findViewById(R.id.totalItemQuantity);
                if (restaurantName != null) {
                    restaurantName.setText(restName);
                }

                return view;
            }


            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                int nameCol = cursor.getColumnIndex("Code");
                String name = cursor.getString(nameCol);

                restaurantName = (TextView) view.findViewById(R.id.restaurantName);
                restaurantCost = (TextView) view.findViewById(R.id.restaurantCost);
                totalItemQuantity = (TextView) view.findViewById(R.id.totalItemQuantity);
            }
        };

    }*/

    @Override
    public SetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SetViewHolder holder, final int position) {
        //holder.subRecycler.setVisibility(View.GONE);
        holder.restaurantName.setText(restaurantName.get(position));
        String item;

        final LinearLayoutManager itemlayoutManager = new LinearLayoutManager(context);
        itemlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        final subItemAdapter itemAdapter = new subItemAdapter(context, itemName, itemCost, itemCount, restaurantName.get(position), Integer.parseInt(restaurantQuantity.get(position)), holder);

        //this appends the correct spelling based on item quantity
        if(restaurantQuantity.get(position).equals("1")){
            item = " item";
        }
        else {
            item = " items";
        }
        holder.totalItemQuantity.setText(restaurantQuantity.get(position) + item);

        restaurantCost = 0.00;

        for(int i=0; i < Integer.parseInt(restaurantQuantity.get(position)); i++){
            restaurantCost += (Double.parseDouble(itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)))) * (Double.parseDouble(itemCount.get(restaurantName.get(position) + "_" + String.valueOf(i))));

            //Log.d("Cost", itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)));
        }

        holder.restaurantCost.setText("$" + String.valueOf(df.format(restaurantCost)));

        //if the item quantities are changed this sets the text to the appropriate view
        if(newTotal != null && position == positionToSet){
            holder.restaurantCost.setText(newTotal);
        }



        if(firstRun){
            if(position == getItemCount()-1){
                holder.subRecycler.setVisibility(View.VISIBLE);
                holder.view.setVisibility(View.VISIBLE);

                //Sets the expanded position to the last item so that on the first run the last
                //card is expanded
                mExpandedPosition = getItemCount() - 1;

                /*Log.d("Item Count", String.valueOf(getItemCount()));
                Log.d("Items", itemName.toString());*/
                firstRun = false;
            }
        }


        final boolean isExpanded = position==mExpandedPosition;
        holder.subRecycler.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.view.setVisibility(isExpanded?View.VISIBLE:View.GONE);

        if(isExpanded){
            //if the view is expanded then the items for the restaurant are displayed
            holder.subRecycler.setLayoutManager(itemlayoutManager);
            holder.subRecycler.setAdapter(itemAdapter);

            RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            };

            holder.subRecycler.addOnItemTouchListener(mScrollTouchListener);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //For the case when the user changes the item total
                newTotal = holder.restaurantCost.getText().toString();

                positionToSet = position;

                Log.d("New Cost", newTotal);

                mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(holder.subRecycler);
                notifyDataSetChanged();

            }
        });




        //holder.view.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return restaurantName.size();
    }
}
