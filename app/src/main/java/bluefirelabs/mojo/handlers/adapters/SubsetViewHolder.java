package bluefirelabs.mojo.handlers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-08-25.
 */

public class SubsetViewHolder extends RecyclerView.ViewHolder {

    public TextView itemName, specificItemPrice,
            removeItem;

    public RecyclerView supersubRecycler;

    public View subSeparator;

    public SubsetViewHolder(View itemView) {
        super(itemView);

        itemName = (TextView) itemView.findViewById(R.id.itemName);
        specificItemPrice = (TextView) itemView.findViewById(R.id.specificItemPrice);
        removeItem = (TextView) itemView.findViewById(R.id.removeItem);
        supersubRecycler = itemView.findViewById(R.id.supersubRecycler);
        subSeparator = itemView.findViewById(R.id.subSeparator);
    }
}
