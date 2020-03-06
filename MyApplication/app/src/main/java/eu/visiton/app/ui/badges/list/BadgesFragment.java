package eu.visiton.app.ui.badges.list;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import eu.visiton.app.R;
import eu.visiton.app.data.BadgeViewModel;
import eu.visiton.app.responses.BadgeResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.BadgeService;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.util.UtilToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgesFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    String jwt;
    BadgeService service;
    UserService userService;
    List<BadgeResponse> items;
    BadgesAdapter adapter;
    SwipeRefreshLayout swipeLayout;
    RecyclerView recycler;
    private BadgeListener mListener;
    private Context ctx;
    private int mColumnCount = 1;
    private boolean asc;
    MenuItem menuItemSort;
    private boolean earnedFilter = false;

    private BadgeViewModel badgeViewModel;

//    @Override
//    protected FragmentToolbar builder() {
//        return new Builder()
//                .withMenu(R.menu.badges_menu)
//                .withTitle(R.string.badges_toolbar_title)
//                .build();
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.badges_menu, menu);
        menuItemSort = menu.findItem(R.id.ascending);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ascending:
                if (asc) {
                    listBadgesAndEarnedSort(UtilToken.getId(ctx));
                    this.asc = !this.asc;
                    menuItemSort.setIcon(R.drawable.ic_baseline_sort_down_24px);
                } else {
                    listBadgesAndEarnedSort(UtilToken.getId(ctx));
                    menuItemSort.setIcon(R.drawable.ic_baseline_sort_up_24px);
                    this.asc = !this.asc;
                }
                return true;
            case R.id.badges_earned_filter:
                if(item.isChecked()){
                    earnedFilter = !earnedFilter;
                    item.setChecked(earnedFilter);
                    listBadgesAndEarned();
                } else {
                    earnedFilter = !earnedFilter;
                    item.setChecked(earnedFilter);
                    listBadgesAndEarnedFiltered();
                }
//                updateTextView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listBadgesAndEarnedFiltered() {
        badgeViewModel.getBadgesAndEarnedFiltered().observe(getActivity(), badgeResponses -> {
            items = badgeResponses;

            adapter = new BadgesAdapter(
                    ctx,
                    items,
                    mListener);

            recycler.setAdapter(adapter);

            adapter.setData(items);
        });
    }

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

    public void listBadgesAndEarned() {
        badgeViewModel.getBadgesAndEarned().observe(getActivity(), badgeResponses -> {
            items = badgeResponses;

            adapter = new BadgesAdapter(
                    ctx,
                    items,
                    mListener);

            recycler.setAdapter(adapter);

            adapter.setData(items);
        });
    }

    public void listBadgesAndEarnedSort(String userId) {

        badgeViewModel.getBadgesAndEarnedSort(asc).observe(getActivity(), badgeResponses -> {
            items = badgeResponses;

            adapter = new BadgesAdapter(
                    ctx,
                    items,
                    mListener);

            recycler.setAdapter(adapter);

            adapter.setData(items);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        badgeViewModel = ViewModelProviders.of(getActivity())
                .get(BadgeViewModel.class);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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
            listBadgesAndEarned();
            adapter = new BadgesAdapter(ctx, items, mListener);
            recycler.setAdapter(adapter);

            swipeLayout = layout.findViewById(R.id.swipeContainer);
            swipeLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary), ContextCompat.getColor(getContext(), R.color.colorAccent));
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    listBadgesAndEarned();
                    if (swipeLayout.isRefreshing()) {
                        swipeLayout.setRefreshing(false);
                    }
                }
            });
        }
        return layout;
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
