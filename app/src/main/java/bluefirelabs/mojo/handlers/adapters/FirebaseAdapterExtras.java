package bluefirelabs.mojo.handlers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class FirebaseAdapterExtras {

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        public TextView extraName, extraCost,
                restaurantDescription, averageTime;


        public RecyclerViewHolder(View itemView) {
            super(itemView);




            extraName = (TextView) itemView.findViewById(R.id.item_dets);
            extraCost = (TextView) itemView.findViewById(R.id.itemCost);
/*            restaurantDescription = (TextView) itemView.findViewById(R.id.restaurantDescription);
            averageTime = (TextView) itemView.findViewById(R.id.averageTime);*/

        }

    }
}
