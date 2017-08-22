package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bluefirelabs.mojo.R;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class FirebaseRecyclerAdapterHistory {

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public Context context;
        public ImageView restaurant_icon, status_initial_icon, status_ready_icon, status_collected_icon, status_delivered_icon;

        public TextView orderid, restaurant_name, status_initial, status_ready, status_collected, status_delivered;
        public TextView status_initial_time, status_ready_time, status_collected_time, status_delivered_time;



        public RecyclerViewHolder(View itemView) {
            super(itemView);


            /* Declaring the ImageViews for progress */

            restaurant_icon = (ImageView) itemView.findViewById(R.id.image);
            status_initial_icon = (ImageView) itemView.findViewById(R.id.status_initial_icon);
            status_ready_icon = (ImageView) itemView.findViewById(R.id.status_ready_icon);
            status_collected_icon = (ImageView) itemView.findViewById(R.id.status_collected_icon);
            status_delivered_icon = (ImageView) itemView.findViewById(R.id.status_delivered_icon);

            /* Declaring the TextViews for progress descriptions*/

            orderid = (TextView) itemView.findViewById(R.id.orderid);
            restaurant_name = (TextView) itemView.findViewById(R.id.extra_parent);
            status_initial = (TextView) itemView.findViewById(R.id.status_initial);

            status_ready = (TextView) itemView.findViewById(R.id.status_ready);
            status_collected = (TextView) itemView.findViewById(R.id.status_collected);
            status_delivered = (TextView) itemView.findViewById(R.id.status_delivered);

            /* Declaring the TextViews for progress timestamp*/

            status_initial_time = (TextView) itemView.findViewById(R.id.status_initial_time);
            status_ready_time = (TextView) itemView.findViewById(R.id.status_ready_time);
            status_collected_time = (TextView) itemView.findViewById(R.id.status_collected_time);
            status_delivered_time = (TextView) itemView.findViewById(R.id.status_delivered_time);

            context = itemView.getContext();

        }

    }
}
