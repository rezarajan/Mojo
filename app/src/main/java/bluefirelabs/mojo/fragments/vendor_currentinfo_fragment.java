package bluefirelabs.mojo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bluefirelabs.mojo.R;
import bluefirelabs.mojo.Vendor_Runner_Mapper;

/**
 * Created by rezarajan on 19/05/2017.
 */

public class vendor_currentinfo_fragment extends Fragment{

    currentinfoListener currentinfolistener;

    public interface currentinfoListener{

    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            currentinfolistener = (currentinfoListener) activity;
        } catch(ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.currentinfo_layout, container, false);

        final TextView currentlocation = (TextView) view.findViewById(R.id.current_location_text);

        currentlocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Vendor_Runner_Mapper.class);
                startActivity(intent);
            }
        });

        return view;
    }


}
