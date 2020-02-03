package com.mario.myapplication.ui.common;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mario.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnderConstructionFragment extends Fragment {


    public UnderConstructionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_under_construction, container, false);
    }

}
