package eu.visiton.app.ui.common;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import eu.visiton.app.R;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.responses.BadgeResponse;
import eu.visiton.app.responses.PeopleResponse;
import eu.visiton.app.responses.RouteResponse;
import eu.visiton.app.ui.badges.detail.BadgeDetailActivity;
import eu.visiton.app.ui.badges.list.BadgeListener;
import eu.visiton.app.ui.badges.list.BadgesFragment;
import eu.visiton.app.ui.badges.detail.BadgeDetailListener;
import eu.visiton.app.ui.people.IPeopleListener;
import eu.visiton.app.ui.people.PeopleFragment;
import eu.visiton.app.ui.pois.map.PoiMapFragment;
import eu.visiton.app.ui.pois.details.DetallePoiActivity;
import eu.visiton.app.ui.pois.list.PoiListFragment;
import eu.visiton.app.ui.pois.list.PoiListListener;
import eu.visiton.app.ui.pois.qrScanner.QrCodeActivity;
import eu.visiton.app.ui.profile.ProfileDarkActivity;
import eu.visiton.app.ui.routes.RouteListener;
import eu.visiton.app.util.Constantes;
import eu.visiton.app.util.UtilToken;

//import com.mario.myapplication.PoiFragment;

public class DashboardActivity extends AppCompatActivity implements BadgeListener, BadgeDetailListener, RouteListener, PoiListListener, IPeopleListener {
    FragmentTransaction fragmentChanger;
    private boolean showMap = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // QR Button
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 2;
    FloatingActionButton fab;
    Toolbar toolbar;


    // Bottom menu
    @SuppressLint("RestrictedApi")
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment f = null;
        switch (item.getItemId()) {

            case R.id.navigation_pois:
                if(showMap) {
                    fab.setVisibility(View.GONE);
                    f = new PoiMapFragment();
                    toolbar.setTitle(getString(R.string.title_pois_map));
                } else {
                    fab.setVisibility(View.VISIBLE);
                    f = new PoiListFragment();
                    toolbar.setTitle(getString(R.string.title_pois_list));
                }
                break;
            case R.id.navigation_routes:
                fab.setVisibility(View.GONE);
                f = new UnderConstructionFragment();
                toolbar.setTitle(getString(R.string.title_routes));
                break;
            case R.id.navigation_people:
                fab.setVisibility(View.GONE);
                f = new PeopleFragment();
                toolbar.setTitle(getString(R.string.title_users));
                break;
            case R.id.navigation_badges:
                fab.setVisibility(View.GONE);
                f = new BadgesFragment();
                toolbar.setTitle(getString(R.string.title_badges));
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

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initToolbar();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        fab = findViewById(R.id.fab);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Para que por defecto cargue el fragmento de POIs (general)
        Fragment f = null;
        if(showMap) {
            fab.setVisibility(View.GONE);
            f = new PoiMapFragment();
            toolbar.setTitle(getString(R.string.title_pois_map));
        } else {
            fab.setVisibility(View.VISIBLE);
            f = new PoiListFragment();
            toolbar.setTitle(getString(R.string.title_pois_list));
        }

        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, f);
        fragmentChanger.commit();

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
        UtilToken.removePreferences(this);
        finish();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }


    @Override
    public void onBadgeClick(View v, BadgeResponse b) {
        Intent i = new Intent(this, BadgeDetailActivity.class);
        i.putExtra(Constantes.EXTRA_BADGE_ID, b.getId());
        i.putExtra(Constantes.EXTRA_BADGE_EARNED, b.isEarned());
        startActivity(i);
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
    public void goPoiDetails(String id) {
        showMap = false;
        //fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new PoiDetailsFragment(id));
        //fragmentChanger.commit();
        Intent i = new Intent(this, DetallePoiActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showPoiMap() {
        showMap = true;
        fab.setVisibility(View.GONE);
        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new PoiMapFragment());
        fragmentChanger.commit();
        toolbar.setTitle(getString(R.string.title_pois_map));

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showPoiList() {
        showMap = false;
        fab.setVisibility(View.VISIBLE);
        fragmentChanger = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new PoiListFragment());
        fragmentChanger.commit();
        toolbar.setTitle(getString(R.string.title_pois_list));

    }

    @Override
    public void showQrScanner() {
        checkCameraPermissions();
    }

    @Override
    public void showGoogleMaps(String coords) {
        Uri gmmIntentUri = Uri.parse("geo:"+coords);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
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

    @Override
    public void onPeopleClick(PeopleResponse p) {
        Intent i = new Intent(DashboardActivity.this, ProfileDarkActivity.class);
        i.putExtra(Constantes.EXTRAS_USER_ID, p.get_id());
        startActivity(i);
    }
}
