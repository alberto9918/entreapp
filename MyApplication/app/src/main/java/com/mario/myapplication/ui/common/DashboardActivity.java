package com.mario.myapplication.ui.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.Dash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mario.myapplication.R;
import com.mario.myapplication.materialx.utils.Tools;
import com.mario.myapplication.responses.BadgeResponse;
import com.mario.myapplication.responses.CategoryResponse;
import com.mario.myapplication.responses.PeopleResponse;
import com.mario.myapplication.responses.RouteResponse;
import com.mario.myapplication.ui.badges.BadgeListener;
import com.mario.myapplication.ui.badges.BadgesFragment;
import com.mario.myapplication.ui.badges.detail.BadgeDetailFragment;
import com.mario.myapplication.ui.badges.detail.BadgeDetailListener;
import com.mario.myapplication.ui.categories.CategoryFragment;
import com.mario.myapplication.ui.people.PeopleFragment;
import com.mario.myapplication.ui.people.details.PeopleDetailsFragment;
import com.mario.myapplication.ui.pois.PoiMapFragment;
import com.mario.myapplication.ui.pois.details.PoiDetailsFragment;
import com.mario.myapplication.ui.pois.list.PoiListFragment;
import com.mario.myapplication.ui.pois.list.PoiListListener;
import com.mario.myapplication.ui.pois.qrScanner.QrCodeActivity;
import com.mario.myapplication.ui.profile.ProfileDarkActivity;
import com.mario.myapplication.ui.routes.RouteListener;
import com.mario.myapplication.ui.routes.RoutesFragment;
import com.mario.myapplication.util.UtilToken;

import java.util.Objects;

//import com.mario.myapplication.PoiFragment;

public class DashboardActivity extends AppCompatActivity implements CategoryFragment.OnListFragmentCategoryInteractionListener, BadgeListener, BadgeDetailListener, RouteListener, PeopleFragment.OnListFragmentUserInteractionListener, PoiListListener, PeopleDetailsFragment.OnFragmentInteractionListener {
    FragmentTransaction fragmentChanger;
    private boolean showMap = true;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // QR Button
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 2;


    // Bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment f = null;
        switch (item.getItemId()) {

            case R.id.navigation_pois:
                if(showMap) {
                    f = new PoiMapFragment();
                } else {
                    f = new PoiListFragment();
                }
                break;
            case R.id.navigation_routes:
                f = new RoutesFragment();
                break;
            case R.id.navigation_people:
                f = new PeopleFragment();
                break;
            case R.id.navigation_badges:
                f = new BadgesFragment();
                break;

        }//default fragment

        if (f != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor, f)
                    .commit();
            return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Para que por defecto cargue el fragmento de POIs (general)
        Fragment f = null;
        if(showMap) {
            f = new PoiMapFragment();
        } else {
            f = new PoiListFragment();
        }

        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, f);
        fragmentChanger.commit();

        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent i = new Intent(DashboardActivity.this, ProfileDarkActivity.class);
                startActivity(i);
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        UtilToken.removeToken(this);
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    @Override
    public void onListFragmentCategoryInteraction(CategoryResponse item) {

    }

    @Override
    public void onBadgeClick(View v, BadgeResponse b) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor, new BadgeDetailFragment(b.getId(), b.isEarned()))
                .commit();
    }

    @Override
    public void onRouteClick(View v, RouteResponse r) {

    }

    /*public void clickOnCamera() {
        Toast.makeText(this, "CLICK ON CAMERA", Toast.LENGTH_LONG).show();


    }

    @Override
    public void editUser(MyProfileResponse u) {
        Toast.makeText(this, "CLICK ON EDIT USER", Toast.LENGTH_LONG).show();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor, new MyProfileEdit())
                .commit();


    }*/

    @Override
    public void onListFragmentUserInteraction(PeopleResponse item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void goPoiDetails(String id) {
        showMap = false;
        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new PoiDetailsFragment(id));
        fragmentChanger.commit();
    }

    @Override
    public void showPoiMap() {
        showMap = true;
        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new PoiMapFragment());
        fragmentChanger.commit();
    }

    @Override
    public void showPoiList() {
        showMap = false;
        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new PoiListFragment());
        fragmentChanger.commit();
    }

    @Override
    public void showQrScanner() {
        checkCameraPermissions();
    }

    /** Check if camera permissions are granted. If not, ask for it. **/
    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(this, QrCodeActivity.class);
            startActivity(i);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ACCESS_CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // El usuario concede el permiso
                    Intent i = new Intent(this, QrCodeActivity.class);
                    startActivity(i);
                } else {
                    // El usuario ha denegado el permiso de Cámara
                    Toast.makeText(DashboardActivity.this, "Sin el permiso de cámara no podrá escanear códigos Qr", Toast.LENGTH_SHORT).show();

                }
                return;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Si deseas salir de la aplicación, deberás cerrar sesión", Toast.LENGTH_SHORT).show();
    }
}
