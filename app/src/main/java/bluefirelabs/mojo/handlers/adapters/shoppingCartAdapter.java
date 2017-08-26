package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class shoppingCartAdapter extends RecyclerView.Adapter<SetViewHolder>{


    private Context context;
    private ArrayList<String> arrayList;
    private View view;

    public shoppingCartAdapter(Context context, ArrayList<String> arrayList){
        this.context = context;
        this.arrayList = arrayList;
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
    public void onBindViewHolder(SetViewHolder holder, int position) {
        holder.restaurantName.setText(arrayList.get(position));
        holder.subRecycler.setVisibility(View.GONE);
        holder.view.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
