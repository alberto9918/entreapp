package com.mario.myapplication.ui.badges;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mario.myapplication.R;
import com.mario.myapplication.responses.BadgeResponse;
import com.mario.myapplication.responses.ResponseContainer;
import com.mario.myapplication.retrofit.generator.AuthType;
import com.mario.myapplication.retrofit.generator.ServiceGenerator;
import com.mario.myapplication.retrofit.services.BadgeService;
import com.mario.myapplication.util.UtilToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgesFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    String jwt;
    BadgeService service;
    List<BadgeResponse> items;
    BadgesAdapter adapter;
    SwipeRefreshLayout swipeLayout;
    RecyclerView recycler;
    private BadgeListener mListener;
    private Context ctx;
    private int mColumnCount = 1;

    public BadgesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BadgesFragment newInstance(int columnCount) {
        BadgesFragment fragment = new BadgesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        jwt = UtilToken.getToken(getContext());
        if (jwt == null) {

        }
        BadgeService service = ServiceGenerator.createService(BadgeService.class, jwt, AuthType.JWT);
        Call<ResponseContainer<BadgeResponse>> callList = service.listBadges();
        callList.enqueue(new Callback<ResponseContainer<BadgeResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<BadgeResponse>> call, Response<ResponseContainer<BadgeResponse>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "You have to log in!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<BadgeResponse>> call, Throwable t) {

            }
        });
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_badges, container, false);

        if (layout instanceof SwipeRefreshLayout) {
            ctx = layout.getContext();
            recycler = layout.findViewById(R.id.badges_list);
            if (mColumnCount <= 1) {
                recycler.setLayoutManager(new LinearLayoutManager(ctx));
            } else {
                recycler.setLayoutManager(new GridLayoutManager(ctx, mColumnCount));
            }
            items = new ArrayList<>();
            getData();
            adapter = new BadgesAdapter(ctx, items, mListener);
            recycler.setAdapter(adapter);

            swipeLayout = layout.findViewById(R.id.swipeContainer);
            swipeLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary), ContextCompat.getColor(getContext(), R.color.colorAccent));
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                    if (swipeLayout.isRefreshing()) {
                        swipeLayout.setRefreshing(false);
                    }
                }
            });
        }
        return layout;
    }

    public void getData() {
        BadgeService service = ServiceGenerator.createService(BadgeService.class, jwt, AuthType.JWT);
        Call<ResponseContainer<BadgeResponse>> call = service.listBadges();
        call.enqueue(new Callback<ResponseContainer<BadgeResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<BadgeResponse>> call, Response<ResponseContainer<BadgeResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    items = response.body().getRows();
                    adapter = new BadgesAdapter(ctx, items, mListener);
                    recycler.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<BadgeResponse>> call, Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BadgeListener) {
            mListener = (BadgeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BadgeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
