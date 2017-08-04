package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stripe.android.model.Card;

import bluefirelabs.mojo.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class FirebaseViewPagerAdapter {

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private Context context;
/*        public TextView itemTitle;
        public TextView itemDescription;
        public ImageView itemIcon;*/


        public CircleImageView restaurantLogo, openindicatorIcon,
                descriptor1, descriptor2, descriptor3, descriptor4;

        public TextView restaurantName, openIndicatorText,
                restaurantDescription, averageTime;

        public ImageView background_image_view;


        public RecyclerViewHolder(View itemView) {
            super(itemView);

            //itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            //itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            //itemDescription = (TextView) itemView.findViewById(R.id.address1);

            background_image_view = (ImageView) itemView.findViewById(R.id.background_image_view);


            restaurantLogo = (CircleImageView) itemView.findViewById(R.id.restaurantLogo);
            openindicatorIcon = (CircleImageView) itemView.findViewById(R.id.openIndicatorIcon);
            descriptor1 = (CircleImageView) itemView.findViewById(R.id.descriptor1);
            descriptor2 = (CircleImageView) itemView.findViewById(R.id.descriptor2);
            descriptor3 = (CircleImageView) itemView.findViewById(R.id.descriptor3);
            descriptor4 = (CircleImageView) itemView.findViewById(R.id.descriptor4);




            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            openIndicatorText = (TextView) itemView.findViewById(R.id.openIndicatorText);
            restaurantDescription = (TextView) itemView.findViewById(R.id.restaurantDescription);
            averageTime = (TextView) itemView.findViewById(R.id.averageTime);


            context = itemView.getContext();

            //itemIcon.setBackgroundColor(Color.parseColor("#d86a0a"));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                   // getter_restaurants();
                }
            });






        }

    }
}
