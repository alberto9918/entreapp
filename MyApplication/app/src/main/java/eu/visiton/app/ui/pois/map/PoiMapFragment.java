package eu.visiton.app.ui.pois.map;


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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import eu.visiton.app.R;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.ui.pois.list.PoiListListener;
import eu.visiton.app.util.UtilToken;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;


public class PoiMapFragment extends Fragment implements OnMapReadyCallback {

    // MAP
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(37.3866245, -5.9942548);
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private PoiListListener mListener;
    private ClusterManager<PoiClusterItem> mClusterManager;

    // Retrofit
    private String jwt;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_poi_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_poi_map_goList: mListener.showPoiList(); break;
            case R.id.action_filter_favs: getFavPois(); break;
            case R.id.action_filter_visited: getVisitedPois(); break;
            case R.id.action_filter_nofilter: getDeviceLocation(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jwt = UtilToken.getToken(Objects.requireNonNull(getContext()));
        getLocationPermissions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        checkDeviceLocation();

        View v = inflater.inflate(R.layout.fragment_poi_map, container, false);
        v.findViewById(R.id.btn_show_myloc).setOnClickListener(view -> {
            if (checkDeviceLocation()) getDeviceLocation();
            else enableDeviceLocation();
        });

        v.findViewById(R.id.btn_scan_qr).setOnClickListener(view -> {
            mListener.showQrScanner();
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        ((SupportMapFragment) Objects.requireNonNull(getChildFragmentManager().findFragmentById(R.id.map))).getMapAsync(this);
//        btnGetLocation();
//        btnQRClick();

        return v;
    }

    /** Actions done when the map is loaded **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapUIConfig();
        setUpClusterer();
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

    /** Actions when click location Button. If GPS isn't activated, it won't give it. **/
    private void btnGetLocation() {
        Objects.requireNonNull(getView()).findViewById(R.id.btn_show_myloc).setOnClickListener(view -> {
            if (checkDeviceLocation()) getDeviceLocation();
            else enableDeviceLocation();
        });
    }

    /** Try to get your mobile location.
     * If is enabled, show yours.
     * If not:
     *      * Show your last location
     *      * Show the default one.**/
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(this.getActivity()), task -> {
                    if (task.getResult() != null) {
                        mMap.setMyLocationEnabled(true);
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        showNearbyLocations(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    } else if (mLastKnownLocation != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        showNearbyLocations(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    } else {
                        mMap.setMyLocationEnabled(false);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        showNearbyLocations(mDefaultLocation.latitude, mDefaultLocation.longitude);
                    }
                });
            } else if (mLastKnownLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                showNearbyLocations(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                showNearbyLocations(mDefaultLocation.latitude, mDefaultLocation.longitude);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /** Show nearby locations to yours **/
    private void showNearbyLocations(double latitude, double longitude) {
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);

        String coords = longitude + "," + latitude;
        Call<ResponseContainer<PoiResponse>> call = service.listPois(coords, 5000);

        call.enqueue(new Callback<ResponseContainer<PoiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Response<ResponseContainer<PoiResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    drawPoisMap(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawPoisMap(Response<ResponseContainer<PoiResponse>> response) {
        mMap.clear();
        mClusterManager.clearItems();

        for (PoiResponse i : Objects.requireNonNull(response.body()).getRows()) {

            String snippet = "";
            if(i.getPrice() == 0.0f) {
                snippet = getString(R.string.free);
            } else {
                snippet = i.getPrice()+ " €";
            }

            PoiClusterItem clusterItem = new PoiClusterItem(
                    i.getId(),
                    i.getLoc().getCoordinates().get(0),
                    i.getLoc().getCoordinates().get(1),
                    i.getName(),
                    snippet);
            mClusterManager.addItem(clusterItem);

                        /*mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(i.getLoc().getCoordinates()[0], i.getLoc().getCoordinates()[1]))
                                .title(i.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                        ).setTag(i.getId());*/
        }
    }

    /** Set the config of map Interface **/
    private void mapUIConfig() {
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(getContext()), R.raw.poi_map_style));

        mMap.setOnMarkerClickListener(mClusterManager);
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

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<PoiClusterItem>(getActivity(), mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<PoiClusterItem>() {
            @Override
            public void onClusterItemInfoWindowClick(PoiClusterItem poiClusterItem) {
                mListener.goPoiDetails(poiClusterItem.getId());
            }
        });

        getDeviceLocation();
    }

    /** API Call to get FAV Pois **/
    private void getFavPois() {

        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        Call<ResponseContainer<PoiResponse>> call = service.listFavPois();
        call.enqueue(new Callback<ResponseContainer<PoiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Response<ResponseContainer<PoiResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    drawPoisMap(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** API Call to get VISITED Pois **/
    private void getVisitedPois() {

        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        Call<ResponseContainer<PoiResponse>> call = service.listVisitedPois();
        call.enqueue(new Callback<ResponseContainer<PoiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Response<ResponseContainer<PoiResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    drawPoisMap(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
