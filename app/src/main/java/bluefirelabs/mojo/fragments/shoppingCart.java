package bluefirelabs.mojo.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.handlers.adapters.shoppingCartAdapter;
import database.DatabaseHelper;

public class shoppingCart extends Fragment {

    private ArrayList<String> restaurantNames = new ArrayList<>();
    private  RecyclerView recyclerView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper myDb = new DatabaseHelper(getContext());

        Cursor data = myDb.orderAlpha();
        
        if(data != null){
            if(data.moveToFirst()){
                do{
                    restaurantNames.add(data.getString(0));
                } while (data.moveToNext());
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        shoppingCartAdapter adapter = new shoppingCartAdapter(getContext(), restaurantNames);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        recyclerView = view.findViewById(R.id.subRecycler);
        return view;
    }
}
