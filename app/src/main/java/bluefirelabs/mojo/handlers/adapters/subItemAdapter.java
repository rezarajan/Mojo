package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class subItemAdapter extends RecyclerView.Adapter<SubsetViewHolder>{


    private Context context;
    private Map<String, String> itemName, restaurantQuantity, itemCost, itemCount;

    private String restaurantName;

    private SetViewHolder setHolder;

    private View view;

    private int mExpandedPosition = -1;
    private int totalItemCount = 1;

    private boolean firstRun = true;

    private DecimalFormat df = new DecimalFormat("#.##");

    private DatabaseHelper myDb;



    public subItemAdapter(Context context, Map<String, String> itemName,
                          Map<String, String> itemCost, Map<String, String> itemCount,
                          String restaurantName, Integer totalItemCount,
                          SetViewHolder holder){
        this.context = context;
        this.itemName = itemName;
        this.restaurantName = restaurantName;
        this.totalItemCount = totalItemCount;
        this.itemCost = itemCost;
        this.itemCount = itemCount;
        this.setHolder = holder;
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

        myDb = new DatabaseHelper(context);
        return new SubsetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubsetViewHolder holder, final int position) {
        //holder.subRecycler.setVisibility(View.GONE);
        //holder.itemName.setText(itemName.get(position));
        /*Log.d("Hello from ViewHolder", itemName.toString());
        Log.d("Hello from ViewHolder", restaurantName + "_" + String.valueOf(position));
        Log.d("Hello from ViewHolder", String.valueOf(itemCount));*/


        //holder.itemName.setText("Item Name");
        holder.itemName.setText(itemName.get(restaurantName + "_" + String.valueOf(position)));
        holder.specificItemPrice.setText("$" + itemCost.get(restaurantName + "_" + String.valueOf(position)));
        holder.itemCount.setText(itemCount.get(restaurantName + "_" + String.valueOf(position)));


        holder.itemCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(holder.itemCount.getText().toString());
                count++;

                holder.itemCount.setText(String.valueOf(count));

                Double currentCost = Double.parseDouble(setHolder.restaurantCost.getText().toString().replace("$", ""));

                Double addedCost = Double.parseDouble(itemCost.get(restaurantName + "_" + String.valueOf(position)));

                Double newCost = currentCost + addedCost;
                setHolder.restaurantCost.setText("$" + String.valueOf(df.format(newCost)));


                String sanitizedItem = holder.itemName.getText().toString().replace("'", "''");        //looks for any "'" in the item name (like S'Mores) so that the DatabaseHelper can properly query it
                Cursor data = myDb.getItemID(sanitizedItem);        //gets the primary key associated with the item name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > 0){

                    myDb.deleteName(itemID, sanitizedItem);     //The item name and ID are used to delete the item on checkbox unchecked

                    myDb.insertData(restaurantName, holder.itemName.getText().toString(), itemCost.get(restaurantName + "_" + String.valueOf(position)), String.valueOf(count));
                }


            }
        });

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
        return totalItemCount;
    }
}
