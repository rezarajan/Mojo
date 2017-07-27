package bluefirelabs.mojo.handlers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.menu.Restaurant_Menu;

/**
 * Created by Reza Rajan on 2017-06-06.
 */

public class FirebaseRecyclerAdapterRestaurants {

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        public TextView itemTitle;
        public TextView itemDescription;
        public ImageView itemIcon;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDescription = (TextView) itemView.findViewById(R.id.item_description);
            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    getter_restaurants();
                }
            });

        }
        public void getter_restaurants(){

            Intent intent = new Intent(context, Restaurant_Menu.class);
            intent.putExtra("Restaurant", itemTitle.getText().toString());

            itemIcon.buildDrawingCache();
            Bitmap image= itemIcon.getDrawingCache();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            Log.d("DrawingCache", "built");

            intent.putExtra("imagebitmap", image);

            Bundle extras = new Bundle();
            extras.putByteArray("BMP", bytes);
            extras.putString("Restaurant", itemTitle.getText().toString());
            intent.putExtras(extras);
            context.startActivity(intent);


        }

    }
}
