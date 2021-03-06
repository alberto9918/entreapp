package eu.visiton.app.ui.pois.list;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import eu.visiton.app.R;
import eu.visiton.app.data.PoiViewModel;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.util.UtilToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class PoiListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private String jwt;
    private List<PoiResponse> items;
    private PoiListAdapter adapter;
    private PoiListListener mListener;
    private Context ctx;
    private int mColumnCount = 1;
    private RecyclerView recycler;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(37.3866245, -5.9942548);

    private PoiViewModel poiViewModel;


    public PoiListFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_poi_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_map: mListener.showPoiMap(); break;
            case R.id.action_filter_favs: getFavPois(); break;
            case R.id.action_filter_visited: getVisitedPois(); break;
            case R.id.action_filter_nofilter: getDeviceLocation(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        poiViewModel = ViewModelProviders.of(getActivity())
                .get(PoiViewModel.class);

        jwt = UtilToken.getToken(Objects.requireNonNull(getContext()));

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_poi_list, container, false);
        if (layout instanceof RecyclerView) {
            ctx = layout.getContext();

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

            recycler = layout.findViewById(R.id.pois_list);
            if (mColumnCount <= 1) {
                recycler.setLayoutManager(new LinearLayoutManager(ctx));
            } else {
                recycler.setLayoutManager(new GridLayoutManager(ctx, mColumnCount));
            }

            getDeviceLocation();
        }
        return layout;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PoiListListener) {
            mListener = (PoiListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PoiListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /** API Call to get All Pois **/
    private void getAllPois(double latitude, double longitude) {

        poiViewModel.getPois(latitude, longitude).observe(getActivity(), poiResponseResponseContainer -> {
            items = poiResponseResponseContainer;

            adapter = new PoiListAdapter(
                    ctx,
                    items,
                    mListener);
            recycler.setAdapter(adapter);

            adapter.setData(items);
        });

    }

    /** API Call to get FAV Pois **/
    private void getFavPois() {
        //items = new ArrayList<>();
        //PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);

        poiViewModel.getFavPois().observe(getActivity(), poiResponses -> {
            items = poiResponses;

            adapter = new PoiListAdapter(
                    ctx,
                    items,
                    mListener);

            recycler.setAdapter(adapter);

            adapter.setData(items);
        });
    }

    /** API Call to get VISITED Pois **/
    private void getVisitedPois() {
        //items = new ArrayList<>();
        //PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);

        poiViewModel.getVisitedPois().observe(getActivity(), poiResponses -> {
            items = poiResponses;

            adapter = new PoiListAdapter(
                    ctx,
                    items,
                    mListener);

            recycler.setAdapter(adapter);

            adapter.setData(items);
        });
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(this.getActivity()), task -> {
                    if (task.getResult() != null) {
                        mLastKnownLocation = task.getResult();

                        getAllPois(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    } else if (mLastKnownLocation != null) {

                        getAllPois(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    } else {
                        getAllPois(mDefaultLocation.latitude, mDefaultLocation.longitude);
                    }
                });
            } else if (mLastKnownLocation != null) {
                getAllPois(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            } else {
                getAllPois(mDefaultLocation.latitude, mDefaultLocation.longitude);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /** Check if location permissions are granted. If not, ask for it. **/
    private void getLocationPermissions() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this.getActivity()),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    /** Check if GPS is enabled **/
    private boolean checkDeviceLocation() {
        LocationManager service;
        service = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /** Ask the user to activate GPS **/
    private void enableDeviceLocation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilder.setTitle(R.string.need_gps_title)
                .setMessage(R.string.need_gps_message)
                .setPositiveButton(R.string.accept, (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
