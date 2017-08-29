package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.database.Cursor;
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
import database.DatabaseHelper;
import database.DatabaseHelperExtras;

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

    private int previouslyExpanded = -1;


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

        //holder.setIsRecyclable(false);

        DatabaseHelper myDb = new DatabaseHelper(context);
        DatabaseHelperExtras myDbExtras = new DatabaseHelperExtras(context);

        Cursor data = myDb.orderAlpha();
        Cursor dataItems = myDb.orderAlpha();
        String previousRestaurant = "";
        int index = -1; //set to -1 since on the first iteration for item quantity there is a ++ to make it 0
        int indexItems = 0;
        int specificItemQuantity = 1;

        //String restaurantNameholder = holder.restaurantName.getText().toString();

        itemName.clear();
        itemCost.clear();
        itemCount.clear();

        if(data != null){
            if(data.moveToFirst()){
                do{

                    if(!data.getString(1).equals(previousRestaurant)){
                        specificItemQuantity = 1;
                        indexItems = 0;

                        myDbExtras.orderExtras(data.getString(2) + "_0", data.getString(1));

                        itemName.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(2));   //restaurant_0, itemName
                        itemCost.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(3));   //restaurant_0, itemCost
                        itemCount.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(4));   //restaurant_0, itemQuantity
                        indexItems++;

                        previousRestaurant = data.getString(1);

                        //index is set to -1 so for the first iteration this operation sets the index to 0
                        index++;
                        TODO: restaurantQuantity.add(index, String.valueOf(specificItemQuantity));    //using the add operation since this is a new index

                        //Log.d("Index : Quantity", String.valueOf(index) + ":" + String.valueOf(specificItemQuantity));

                    }
                    else {
                        specificItemQuantity++;
                        restaurantQuantity.set(index, String.valueOf(specificItemQuantity));    //using the set operation to overwrite existing data at the index
                        Log.d("Index : Quantity", String.valueOf(index) + ":" + String.valueOf(specificItemQuantity));

                        itemName.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(2));   //restaurant_1, itemName
                        itemCost.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(3));   //restaurant_1, itemCost
                        itemCount.put(data.getString(1) + "_" + String.valueOf(indexItems), data.getString(4));   //restaurant_1, itemQuantity
                        indexItems++;


                    }
                } while (data.moveToNext());
            }
        }


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



/*        if(position != previouslyExpanded && previouslyExpanded != -1){
            Log.d("Previously Expanded_0", String.valueOf(position));
            Log.d("Previously Expanded", String.valueOf(previouslyExpanded));

            restaurantCost = 0.00;

            for(int i=0; i < Integer.parseInt(restaurantQuantity.get(position)); i++){
                restaurantCost += (Double.parseDouble(itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)))) * (Double.parseDouble(itemCount.get(restaurantName.get(position) + "_" + String.valueOf(i))));

                //Log.d("Cost", itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)));
            }

            Log.d("Cost", String.valueOf(restaurantCost));

            if(position != getItemCount()-1 && holder.restaurantCost.getText().toString().equals("$0.00")){

            }
        }*/




        //Log.d("Cost_1", String.valueOf(restaurantCost));


        restaurantCost = 0.00;

        for(int i=0; i < Integer.parseInt(restaurantQuantity.get(position)); i++){
            restaurantCost += (Double.parseDouble(itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)))) * (Double.parseDouble(itemCount.get(restaurantName.get(position) + "_" + String.valueOf(i))));

            //Log.d("Cost", itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)));
        }

        holder.restaurantCost.setText("$" + String.valueOf(df.format(restaurantCost)));



        if(firstRun){
            Log.d("Operation", "First Run");

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
/*
                restaurantCost = 0.00;

                for(int i=0; i < Integer.parseInt(restaurantQuantity.get(position)); i++){
                    restaurantCost += (Double.parseDouble(itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)))) * (Double.parseDouble(itemCount.get(restaurantName.get(position) + "_" + String.valueOf(i))));

                    //Log.d("Cost", itemCost.get(restaurantName.get(position) + "_" + String.valueOf(i)));
                }

                Log.d("Cost_2", String.valueOf(restaurantCost));*/
                //Log.d("Previously Expanded", String.valueOf(mExpandedPosition));

                previouslyExpanded = mExpandedPosition;

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
