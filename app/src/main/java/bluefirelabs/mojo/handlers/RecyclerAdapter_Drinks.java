package bluefirelabs.mojo.handlers;

/**
 * Created by Reza Rajan on 2017-05-17.
 */

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bluefirelabs.mojo.R;
import database.DatabaseHelper;

public class RecyclerAdapter_Drinks extends RecyclerView.Adapter<RecyclerAdapter_Drinks.ViewHolder> {

    private Context context;
    DatabaseHelper myDb;
    private String restaurant = "Restaurant";

    Button imagebutton_delete;

    private String[] titles =
            {"Coke",
                    "Sprite",
                    "Canada Dry",};

    private String[] cost =
            {"6",
                    "4",
                    "5",};
    private int[] icon =
            {R.drawable.food,
                    R.drawable.drinks,
                    R.drawable.dessert};

    class ViewHolder extends RecyclerView.ViewHolder {


        public TextView itemTitle;


        public ViewHolder(final View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            Button imagebutton = (Button) itemView.findViewById(R.id.imageButton_add);
            //imagebutton_delete = (Button) itemView.findViewById(R.id.imageButton_delete);


            context = itemView.getContext();

            imagebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                    boolean isInserted = myDb.insertData(restaurant,
                            titles[position],
                            cost[position]);                                //Adds the item at at the specific position to the database

                    if (isInserted == true) {
                        Snackbar.make(v, "Data Inserted",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(v, "Data not Inserted",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

            /*imagebutton_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Integer deletedRows = 0;

                    switch(position){
                        case 0:
                            deletedRows = myDb.deleteData("Coke");
                            break;
                        case 1:
                            deletedRows = myDb.deleteData("Sprite");
                            break;
                        case 2:
                            deletedRows = myDb.deleteData("Canada Dry");
                            break;
                        default:
                            break;

                    }
                    if(deletedRows > 0){
                            Snackbar.make(v, "Data Deleted",
                                    Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                    } else {
                        Snackbar.make(v, "Data not Deleted",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }); */
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        myDb = new DatabaseHelper(context); //calls constructor from the database helper class

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.food_card, viewGroup, false);
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