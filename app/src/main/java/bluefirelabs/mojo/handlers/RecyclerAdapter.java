package bluefirelabs.mojo.handlers;

/**
 * Created by Reza Rajan on 2017-05-17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.distance_duration;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;

    private String[] titles =
            {"Restaurant One",
            "Restaurant Two",
            "Restaurant Three",
            "Resturant Four",
            "Restaurant Five",
            "Restaurant Six",
            "Restaurant Seven",
            "Restaurant Eight"};

    class ViewHolder extends RecyclerView.ViewHolder{


        public TextView itemTitle;


        public ViewHolder(final View itemView) {
            super(itemView);
            itemTitle = (TextView)itemView.findViewById(R.id.item_title);

            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    final Intent intent;

                    intent = new Intent(context, distance_duration.class);

                    /*This is for card-specific intent actions
                    switch(getAdapterPosition()){
                        case 0:
                            intent = new Intent(context, MapsActivity.class);
                            break;
                        case 1:
                            intent = new Intent(context, MapsActivity.class);
                            break;
                        default:
                            intent = new Intent(context, MapsActivity.class);
                            break;
                    }
                    */
                    context.startActivity(intent);

                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}