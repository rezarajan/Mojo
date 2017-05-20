package bluefirelabs.mojo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bluefirelabs.mojo.R;

/**
 * Created by rezarajan on 19/05/2017.
 */

public class currentinfo_fragment extends Fragment{

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
        return inflater.inflate(R.layout.currentinfo_layout, container, false);
    }


}
