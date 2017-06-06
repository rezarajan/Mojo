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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import bluefirelabs.mojo.menu.Drinks_Menu;
import bluefirelabs.mojo.R;

public class RecyclerAdapter_Menu extends RecyclerView.Adapter<RecyclerAdapter_Menu.ViewHolder> {

    private Context context;
    private DatabaseReference mDatabase;

    private String[] titles =
            {"Food",
            "Drinks",
            "Desserts",};
    private int[] icon =
            {R.drawable.food,
                    R.drawable.drinks,
                    R.drawable.dessert};

    class ViewHolder extends RecyclerView.ViewHolder{


        public TextView itemTitle;
        public TextView itemDescription;
        public ImageView itemIcon;


        public ViewHolder(final View itemView) {
            super(itemView);
            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemTitle = (TextView)itemView.findViewById(R.id.item_title);
            itemDescription = (TextView)itemView.findViewById(R.id.item_description);
            mDatabase = FirebaseDatabase.getInstance().getReference();

            context = itemView.getContext();

            /*itemDescription.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    final Intent intent;

                    intent = new Intent(context, Runner_Mapper.class);
                    */

                    /*This is for card-specific intent actions
                    switch(getAdapterPosition()){
                        case 0:
                            intent = new Intent(context, Runner_Mapper.class);
                            break;
                        case 1:
                            intent = new Intent(context, Runner_Mapper.class);
                            break;
                        default:
                            intent = new Intent(context, Runner_Mapper.class);
                            break;
                    }
                    */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    final Intent intent;

                    intent = new Intent(context, Drinks_Menu.class);
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
        viewHolder.itemIcon.setImageResource(icon[i]);
        viewHolder.itemTitle.setText(titles[i]);
        viewHolder.itemDescription.setText(titles[i]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}