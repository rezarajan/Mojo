package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class subItemAdapter extends RecyclerView.Adapter<SubsetViewHolder>{


    private Context context;
    private Map<String, String> itemName, restaurantQuantity;

    private String restaurantName;

    private View view;

    private int mExpandedPosition = -1;
    private int itemCount = 1;

    private boolean firstRun = true;


    public subItemAdapter(Context context, Map<String, String> itemName, String restaurantName, Integer itemCount){
        this.context = context;
        this.itemName = itemName;
        this.restaurantName = restaurantName;
        this.itemCount = itemCount;
        //this.restaurantQuantity = restaurantQuantity;
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
    public SubsetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_sub_item, parent, false);
        Log.d("Hello from ViewHolder", String.valueOf(itemCount));
        return new SubsetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubsetViewHolder holder, final int position) {
        //holder.subRecycler.setVisibility(View.GONE);
        //holder.itemName.setText(itemName.get(position));
        Log.d("Hello from ViewHolder", itemName.toString());
        Log.d("Hello from ViewHolder", restaurantName + "_" + String.valueOf(position));
        Log.d("Hello from ViewHolder", String.valueOf(itemCount));


        //holder.itemName.setText("Item Name");
        holder.itemName.setText(itemName.get(restaurantName + "_" + String.valueOf(position)));
        String item;

        //this appends the correct spelling based on item quantity
/*
        if(restaurantQuantity.get(position).equals("1")){
            item = " item";
        }
        else {
            item = " items";
        }
        holder.totalItemQuantity.setText(restaurantQuantity.get(position) + item);



        if(firstRun){
            if(position == getItemCount()-1){
                holder.subRecycler.setVisibility(View.VISIBLE);
                holder.view.setVisibility(View.VISIBLE);

                //Sets the expanded position to the last item so that on the first run the last
                //card is expanded
                mExpandedPosition = getItemCount() - 1;

                Log.d("Item Count", String.valueOf(getItemCount()));
                firstRun = false;
            }
        }

        final boolean isExpanded = position==mExpandedPosition;
        holder.subRecycler.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.view.setVisibility(isExpanded?View.VISIBLE:View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(holder.subRecycler);
                notifyDataSetChanged();
            }
        });
*/


        //holder.view.setVisibility(View.GONE);
    }



    @Override
    public int getItemCount() {
        return itemCount;
    }
}
