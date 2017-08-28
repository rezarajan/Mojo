package bluefirelabs.mojo.handlers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-08-25.
 */

public class SetViewHolder extends RecyclerView.ViewHolder {

    public TextView restaurantName, restaurantCost,
            totalItemQuantity;

    public RecyclerView subRecycler;

    public View view;

    public SetViewHolder(View itemView) {
        super(itemView);

        restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
        restaurantCost = (TextView) itemView.findViewById(R.id.restaurantCost);
        totalItemQuantity = (TextView) itemView.findViewById(R.id.totalItemQuantity);
        subRecycler = itemView.findViewById(R.id.subRecycler);
        view = itemView.findViewById(R.id.view);
    }

}
